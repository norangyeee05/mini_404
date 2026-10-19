<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { currentMember } from '../../../services/auth'
import { api } from '../../../services/backendApi'
const props = defineProps({ station: { type: Object, required: true } })
const transfers = ref([])
const info = computed(() => {
  const facilities = (Array.isArray(props.station.facilities) ? props.station.facilities : [props.station.facilities])
    .flatMap(value => String(value || '').split(/[|,]/).map(item => item.trim()).filter(Boolean))
  const facilityLines = String(props.station.stationNo) === '761'
    ? Array.from({ length: Math.ceil(facilities.length / 2) }, (_, index) => facilities.slice(index * 2, index * 2 + 2).join(', '))
    : facilities
  return { facilities: facilityLines, transfer: transfers.value.length ? { label: transfers.value[0].transferLineName, lines: transfers.value.map(item => item.transferInfo) } : null }
})
async function loadTransfer() {
  transfers.value = []
  if (!currentMember.value) return
  try { const result = await api.line.transfer(props.station.id); transfers.value = result.transfers || [] } catch { transfers.value = [] }
}
onMounted(loadTransfer)
watch(() => [props.station.id, currentMember.value], loadTransfer)
</script>
<template>
  <aside class="station-facilities" :class="{ 'station-facilities--member': currentMember }" aria-label="환승 및 편의시설">
    <template v-if="currentMember">
      <section class="facilities-section">
        <h2>편의시설</h2>
        <p v-if="info.facilities.length" class="facility-copy"><span v-for="(line, index) in info.facilities" :key="index">{{ line }}</span></p>
        <p v-else class="facility-copy">등록된 편의시설 정보가 없습니다.</p>
      </section>
      <section v-if="info.transfer" class="transfer-section">
        <h2>빠른 환승 정보</h2>
        <span class="transfer-chip">{{ info.transfer.label }}</span>
        <p class="transfer-copy"><span v-for="(line, index) in info.transfer.lines" :key="index">{{ line }}</span></p>
      </section>
    </template>
    <p v-else class="guest-message">로그인 시 편의시설 및 빠른 환승 정보를 이용할 수 있습니다.</p>
  </aside>
</template>
<style scoped>
.station-facilities { min-width: 0; padding: 30px 22px; border-radius: 20px; background: #b5cce0; color: #155369; display: flex; flex-direction: column; justify-content: center; }
.guest-message { margin: 0; text-align: center; font-size: clamp(16px, 1.8vw, 24px); font-weight: 700; line-height: 1.6; }
.station-facilities--member { justify-content: flex-start; padding: clamp(30px, 3.7vw, 54px) clamp(16px, 1.9vw, 28px) 30px; }
h2 { margin: 0; padding-left: 10px; border-left: 3px solid #155369; font-size: clamp(17px, 1.65vw, 24px); font-weight: 700; line-height: 1.2; }
.facility-copy, .transfer-copy { margin: 20px 0 0; font-size: clamp(16px, 1.65vw, 24px); font-weight: 700; line-height: 1.5; overflow-wrap: anywhere; }
.facility-copy { padding-left: 6px; }
.facility-copy span, .transfer-copy span { display: block; }
.transfer-section { margin-top: clamp(40px, 5vw, 72px); }
.transfer-chip { display: inline-block; margin-top: clamp(20px, 2.8vw, 39px); padding: 7px 11px; border-radius: 24px; background: #858e40; color: white; font-size: clamp(14px, 1.25vw, 18px); line-height: 24px; font-weight: 500; }
.transfer-copy { margin-top: 19px; }
@media (min-width: 1101px) and (max-width: 1350px) {
  .station-facilities--member { padding-right: 20px; padding-left: 20px; }
  h2, .facility-copy, .transfer-copy { font-size: 20px; }
}
@media (max-width: 900px) {
  .station-facilities--member { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 30px; padding: 30px 28px; }
  .transfer-section { margin-top: 0; }
  .transfer-chip { margin-top: 20px; }
  .facility-copy, .transfer-copy { font-size: 20px; line-height: 32px; }
}
@media (max-width: 760px) {
  .station-facilities { min-height: 180px; }
  .station-facilities--member { display: flex; gap: 0; padding: 28px 22px; }
  .transfer-section { margin-top: 40px; }
  h2 { font-size: 22px; }
  .facility-copy, .transfer-copy { font-size: 18px; line-height: 30px; }
}
</style>
