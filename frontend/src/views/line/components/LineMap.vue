<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({ stations: Array, selected: String })
defineEmits(['select'])

const isMobile = ref(typeof window !== 'undefined' && window.innerWidth <= 760)
const stationCount = computed(() => props.stations?.length ?? 0)
const desktopRows = computed(() => Math.max(1, Math.ceil(stationCount.value / 6)))
const mobileRows = computed(() => Math.max(1, Math.ceil(stationCount.value / 4)))
const wrapsDesktop = computed(() => !isMobile.value && stationCount.value > 10)

const mapHeight = computed(() => {
  if (isMobile.value) return Math.max(500, mobileRows.value * 130 + 100)
  if (wrapsDesktop.value) return Math.max(580, desktopRows.value * 180 + 40)
  return 580
})

const dynamicViewBox = computed(() => `0 0 1000 ${mapHeight.value}`)

function updateViewport() {
  isMobile.value = window.innerWidth <= 760
}

onMounted(() => window.addEventListener('resize', updateViewport))
onBeforeUnmount(() => window.removeEventListener('resize', updateViewport))

function wrappedPosition(index, columns, mobile) {
  const row = Math.floor(index / columns)
  const slot = index % columns
  const column = row % 2 === 0 ? slot : columns - 1 - slot
  const x = mobile ? 12.5 + column * 25 : 8.5 + column * 16
  const y = mobile ? 80 + row * 130 : 180 + row * 180
  return { x, y }
}

function position(index) {
  if (isMobile.value) {
    const { x, y } = wrappedPosition(index, 4, true)
    return { '--station-x': `${x}%`, '--station-y': `${y}px` }
  }

  if (wrapsDesktop.value) {
    const { x, y } = wrappedPosition(index, 6, false)
    return { '--station-x': `${x}%`, '--station-y': `${y}px` }
  }

  // Keep the established desktop arrangement while it fits on one line.
  return {
    '--station-x': (index === 0 ? 4.2 : index === 1 ? 8.8 : 22.5 + (index - 2) * 9.1) + '%',
    '--station-y': index === 0 ? '65.69%' : '43.1%',
  }
}

const dynamicRoute = computed(() => {
  const mobile = isMobile.value
  const columns = mobile ? 4 : 6
  const stations = props.stations ?? []

  if (!stations.length) return ''

  const points = stations.map((_, index) => {
    const { x, y } = wrappedPosition(index, columns, mobile)

    return {
      x: x * 10,
      y,
      row: Math.floor(index / columns),
    }
  })

  return points.reduce((path, point, index) => {
    if (index === 0) {
      return `M ${point.x} ${point.y}`
    }

    const previous = points[index - 1]

    // 같은 줄의 역은 직선으로 연결
    if (point.row === previous.row) {
      return `${path} L ${point.x} ${point.y}`
    }

    // 오른쪽 또는 왼쪽에서 다음 줄로 둥글게 연결
    const isRightTurn = previous.row % 2 === 0

    const edgeX = isRightTurn
      ? (mobile ? 990 : 980)
      : (mobile ? 10 : 20)

    const radius = 22

    const approachX = isRightTurn
      ? edgeX - radius
      : edgeX + radius

    return `${path}
      L ${approachX} ${previous.y}
      Q ${edgeX} ${previous.y} ${edgeX} ${previous.y + radius}
      L ${edgeX} ${point.y - radius}
      Q ${edgeX} ${point.y} ${approachX} ${point.y}
      L ${point.x} ${point.y}`
  }, '')
})
</script>

<template>
  <div class="line-map" :class="{ 'line-map--wrapped': wrapsDesktop }" :style="{ height: `${mapHeight}px` }" role="group" aria-label="7호선 노선도">
    <svg v-if="!isMobile && !wrapsDesktop" class="desktop-track" viewBox="0 0 1000 580" preserveAspectRatio="none" aria-hidden="true">
      <path d="M42 381 L88 250 L953 250" fill="none" stroke="#697516" stroke-width="11" stroke-linecap="round" />
    </svg>
    <svg v-else :class="isMobile ? 'mobile-track' : 'desktop-track'" :viewBox="dynamicViewBox" preserveAspectRatio="none" aria-hidden="true">
      <path :d="dynamicRoute" fill="none" stroke="#697516" :stroke-width="isMobile ? 12 : 11" stroke-linecap="round" stroke-linejoin="round" />
    </svg>
    <button v-for="(station, index) in stations" :key="station.name" type="button"
      :class="{ stadium: station.name === '부천종합운동장' }" :style="position(index)"
      :aria-pressed="selected === station.name" :aria-label="station.name + '역 정보 보기'" @click="$emit('select', station)">
      <span v-if="station.name === '부천종합운동장'">부천종합<br />운동장</span><span v-else>{{ station.name }}</span>
    </button>
  </div>
</template>

<style scoped>
.line-map { position: relative; width: 100%; height: 580px; }
svg { position: absolute; width: 100%; height: 100%; inset: 0; pointer-events: none; overflow: visible; }
.mobile-track { display: none; }
button { position: absolute; left: var(--station-x); top: var(--station-y); transform: translate(-50%, -50%); min-height: 38px; width: 7.4%; padding: 7px 3px; border: 0; border-radius: 24px; background: #858e40; color: white; font-size: clamp(11px, 1.25vw, 18px); line-height: 1.35; font-weight: 500; }
button.stadium { width: 6.2%; border-radius: 10px; min-height: 65px; }
.line-map--wrapped button { width: 14%; }
.line-map--wrapped button.stadium { width: 14%; }
button:hover, button[aria-pressed="true"] { background: #626c22; }
button:focus-visible { outline: 3px solid #155369; outline-offset: 3px; }
@media (max-width: 760px) {
  .desktop-track { display: none; }
  .mobile-track { display: block; }
  button, button.stadium { left: var(--station-x); top: var(--station-y); width: 22%; min-height: 44px; padding: 7px 2px; font-size: clamp(11px, 2.4vw, 16px); }
  button.stadium { min-height: 56px; }
}
</style>

