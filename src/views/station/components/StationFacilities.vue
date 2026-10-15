<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { currentMember } from '../../../services/auth'
import { api } from '../../../services/backendApi'
const props = defineProps({ station: { type: Object, required: true } })
const transfers = ref([])
const info = computed(() => ({ facilities: [], transfer: transfers.value.length ? { label: transfers.value[0].transferLineName, lines: transfers.value.map(item => item.transferInfo) } : null }))
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
    <p v-if="!currentMember" class="guest-message">로그인 시 빠른 환승 정보<br />및 편의시설 이용 가능합니다.</p>
    <template v-else-if="info">
      <section class="facilities-section">
        <h2>편의시설</h2>
        <p class="facility-copy"><span v-for="(line, index) in info.facilities" :key="index">{{ line }}</span></p>
      </section>
      <section v-if="info.transfer" class="transfer-section">
        <h2>빠른 환승 정보</h2>
        <span class="transfer-chip">{{ info.transfer.label }}</span>
        <p class="transfer-copy"><span v-for="(line, index) in info.transfer.lines" :key="index">{{ line }}</span></p>
      </section>
    </template>
  </aside>
</template>
<style scoped>
.station-facilities { min-width: 0; padding: 30px 22px; border-radius: 20px; background: #b5cce0; color: #155369; display: flex; flex-direction: column; justify-content: center; }
.guest-message { margin: 0; text-align: center; font-size: 24px; font-weight: 700; line-height: 1.6; }
.station-facilities--member { justify-content: flex-start; padding: 54px 28px 35px; }
h2 { margin: 0; padding-left: 10px; border-left: 3px solid #155369; font-size: 24px; font-weight: 700; line-height: 29px; }
.facility-copy, .transfer-copy { margin: 20px 0 0; font-size: 24px; font-weight: 700; line-height: 36px; overflow-wrap: anywhere; }
.facility-copy { padding-left: 6px; }
.facility-copy span, .transfer-copy span { display: block; }
.transfer-section { margin-top: 140px; }
.transfer-chip { display: inline-block; margin-top: 39px; padding: 7px 11px; border-radius: 24px; background: #858e40; color: white; font-size: 18px; line-height: 24px; font-weight: 500; }
.transfer-copy { margin-top: 19px; }
@media (min-width: 1101px) and (max-width: 1350px) {
  .station-facilities--member { padding-right: 20px; padding-left: 20px; }
  h2, .facility-copy, .transfer-copy { font-size: 20px; }
}
@media (max-width: 1100px) {
  .guest-message { font-size: 18px; }
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
