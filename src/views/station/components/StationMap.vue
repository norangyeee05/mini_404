<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { loadKakaoMaps, stationCoordinates } from '../services/kakaoMap'
const props = defineProps({ station: { type: Object, required: true } })
const canvas = ref(null)
const loading = ref(true)
const error = ref('')
let map, marker, observer, active = false, revision = 0
async function initialize() {
  const current = ++revision
  loading.value = true; error.value = ''
  try {
    const maps = await loadKakaoMaps()
    if (!active || current !== revision) return
    const coordinates = stationCoordinates[props.station.id]
    if (!coordinates) throw new Error('이 역의 위치 정보가 없습니다.')
    const center = new maps.LatLng(...coordinates)
    if (!map) {
      map = new maps.Map(canvas.value, { center, level: 3 })
      map.addControl(new maps.ZoomControl(), maps.ControlPosition.RIGHT)
      observer = new ResizeObserver(() => { map.relayout(); if (marker) map.setCenter(marker.getPosition()) })
      observer.observe(canvas.value)
    }
    marker?.setMap(null)
    marker = new maps.Marker({ map, position: center, title: props.station.name + '역' })
    map.relayout(); map.setCenter(center)
  } catch (failure) { if (active && current === revision) error.value = failure.message }
  finally { if (active && current === revision) loading.value = false }
}
onMounted(() => { active = true; initialize() })
watch(() => props.station.id, () => { if (active) initialize() })
onBeforeUnmount(() => { active = false; revision++; observer?.disconnect(); marker?.setMap(null); map = null })
</script>
<template>
  <aside class="station-map-card" aria-label="역 위치">
    <h2>{{ station.name }}</h2>
    <div class="map-frame">
      <div ref="canvas" class="map-canvas" :aria-label="station.name + '역 카카오 지도'"></div>
      <div v-if="loading || error" class="map-message" role="status">
        <p>{{ loading ? '지도를 불러오는 중입니다.' : error }}</p>
        <button v-if="error" type="button" @click="initialize">다시 시도</button>
      </div>
      <a class="map-open" :href="'https://map.kakao.com/link/search/' + encodeURIComponent(station.name + '역')" target="_blank" rel="noopener noreferrer">카카오맵에서 보기 ↗</a>
    </div>
  </aside>
</template>
<style scoped>
.station-map-card { padding: 39px 8px 34px; border-radius: 20px; background: #b5cce0; display: flex; flex-direction: column; align-items: center; gap: 30px; min-width: 0; }
h2 { display: grid; place-items: center; width: 172px; max-width: 100%; min-height: 56px; margin: 0; padding: 8px 4px; border-radius: 30px; background: #858e40; color: #fff; font-size: 24px; white-space: nowrap; }
.map-frame { position: relative; width: 100%; flex: 1; min-height: 420px; border-radius: 15px; overflow: hidden; background: #eef3f4; }
.map-canvas { position: absolute; inset: 0; }
.map-message { position: absolute; inset: 0; z-index: 2; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 18px 18px 55px; background: #eef3f4; text-align: center; color: #155369; font-size: 14px; line-height: 1.6; }
.map-message button { border: 0; border-radius: 8px; padding: 8px 14px; background: #467486; color: white; }
.map-open { position: absolute; bottom: 8px; left: 50%; transform: translateX(-50%); white-space: nowrap; padding: 6px 10px; border-radius: 8px; background: #fff; color: #155369; font-size: 12px; z-index: 3; box-shadow: 0 1px 5px #0002; }
a:focus-visible, button:focus-visible { outline: 3px solid #155369; outline-offset: 3px; }
@media (max-width: 760px) { .station-map-card { padding: 20px 12px; gap: 18px; } .map-frame { flex: none; height: 420px; } }
</style>
