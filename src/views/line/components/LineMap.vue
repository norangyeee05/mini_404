<script setup>
defineProps({ stations: Array, selected: String })
defineEmits(['select'])
function position(index) {
  const row = Math.floor(index / 4)
  const column = row % 2 === 0 ? index % 4 : 3 - index % 4
  return {
    '--desktop-x': (index === 0 ? 4.2 : index === 1 ? 8.8 : 22.5 + (index - 2) * 9.1) + '%',
    '--desktop-y': index === 0 ? '65.69%' : '43.1%',
    '--mobile-x': (12.5 + column * 25) + '%',
    '--mobile-y': (27 + row * 28) + '%',
  }
}
</script>
<template>
  <div class="line-map" role="group" aria-label="7호선 노선도">
    <svg class="desktop-track" viewBox="0 0 1000 580" preserveAspectRatio="none" aria-hidden="true">
      <path d="M42 381 L88 250 L953 250" fill="none" stroke="#697516" stroke-width="11" stroke-linecap="round" />
    </svg>
    <svg class="mobile-track" viewBox="0 0 1000 500" preserveAspectRatio="none" aria-hidden="true">
      <path d="M125 135 H875 V275 H125 V415 H625" fill="none" stroke="#697516" stroke-width="12" stroke-linecap="round" stroke-linejoin="round" />
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
button { position: absolute; left: var(--desktop-x); top: var(--desktop-y); transform: translate(-50%, -50%); min-height: 38px; width: 7.4%; padding: 7px 3px; border: 0; border-radius: 24px; background: #858e40; color: white; font-size: clamp(11px, 1.25vw, 18px); line-height: 1.35; font-weight: 500; }
button.stadium { width: 6.2%; border-radius: 10px; min-height: 65px; }
button:hover, button[aria-pressed="true"] { background: #626c22; }
button:focus-visible { outline: 3px solid #155369; outline-offset: 3px; }
@media (max-width: 760px) {
  .line-map { height: 500px; }
  .desktop-track { display: none; }
  .mobile-track { display: block; }
  button, button.stadium { left: var(--mobile-x); top: var(--mobile-y); width: 22%; min-height: 44px; padding: 7px 2px; font-size: clamp(11px, 2.4vw, 16px); }
  button.stadium { min-height: 56px; }
}
</style>
