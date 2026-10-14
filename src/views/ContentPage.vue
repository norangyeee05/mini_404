<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import { line7Api } from '../services/endpoints'
import { currentMember } from '../services/auth'
import { apiError } from '../services/api'
const route=useRoute(),stations=ref([]),station=ref(null),transfers=ref([]),error=ref(''),stationId=computed(()=>route.params.stationId)
async function load(){error.value='';try{stations.value=await line7Api.stations();if(stationId.value){station.value=await line7Api.station(stationId.value);if(currentMember.value){const response=await line7Api.transfer(stationId.value);transfers.value=(response?.data??response)?.transfers||[]}else transfers.value=[]}else station.value=null}catch(e){error.value=apiError(e)}}
watch(()=>route.fullPath,load,{immediate:true})
watch(()=>currentMember.value,()=>{if(stationId.value)load()})
</script>
<template>
  <div class="content-shell"><PageHeader/><main class="content-main"><h1>7호선 노선도</h1><section class="content-panel">
    <p v-if="error" role="alert">{{ error }}</p>
    <nav class="station-links" aria-label="역 선택"><RouterLink v-for="item in stations" :key="item.stationId" :to="`/line7/stations/${item.stationId}`">{{ item.stationName }}</RouterLink></nav>
    <template v-if="station"><h2>{{ station.stationName }}역</h2><p>전화번호: {{ station.phone || '정보 없음' }}</p><p>주소: {{ station.address || '정보 없음' }}</p><p>{{ station.usageInfo }}</p><p>이전역: {{ station.prevStation || '없음' }} · 다음역: {{ station.nextStation || '없음' }}</p><section v-if="currentMember"><h3>빠른 환승 정보</h3><ul><li v-for="(transfer,index) in transfers" :key="index">{{ transfer.transferLineName }}: {{ transfer.transferInfo }}</li></ul></section><p v-else>빠른 환승 정보는 로그인 후 이용할 수 있습니다.</p></template>
    <p v-else-if="!error">역을 선택해 상세 정보를 확인하세요.</p>
  </section><RouterLink class="home-link" to="/">메인으로 돌아가기</RouterLink></main><PageFooter/></div>
</template>
<style scoped>.qna-delete-button {
  display: block;
  min-width: 84px;
  min-height: 40px;
  margin: 20px 0 16px auto;
  padding: 8px 20px;
  border: 0;
  border-radius: 9px;
  background: #457384;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}
.qna-delete-button:focus-visible {
  outline: 2px solid #155369;
  outline-offset: 3px;
}
.content-shell {
  display: flex;
  flex-direction: column;
  min-height: 100svh;
  width: min(1440px, 100%);
  margin: 0 auto;
  padding: 0 34px;
  background: var(--page-background);
}
.content-main {
  flex: 1;
  padding: 32px 0;
}
h1 {
  color: #155369;
  font-size: 32px;
}
.content-panel {
  padding: 28px;
  border-radius: 20px;
  background: #f0f5f7;
  line-height: 1.8;
}
h2 {
  color: #155369;
}
.dummy-body {
  margin: 32px 0;
  white-space: pre-line;
}
.home-link {
  display: inline-block;
  margin-top: 24px;
}
.content-panel a, .home-link {
  text-decoration: underline;
}
.station-links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}
@media (max-width: 600px) {
  .content-shell {
    padding: 0 16px;
  }
  .content-panel {
    padding: 20px;
  }
}
</style>




