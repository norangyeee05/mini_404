<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import StationMap from './components/StationMap.vue'
import StationFacilities from './components/StationFacilities.vue'
import { fetchStation } from './services/stationData'
import { apiError } from '../../services/backendApi'
const route = useRoute()
const station = ref(null)
async function loadStation() { try { station.value = await fetchStation(route.params.stationId) } catch (error) { station.value = null; console.error(apiError(error)) } }
onMounted(loadStation)
watch(() => route.params.stationId, loadStation)
</script>
<template>
  <div class="station-shell">
    <PageHeader />
    <main class="station-main">
      <PageBanner title="철도 정보" breadcrumb-title="철도정보" description="7호선 노선도에 대해 확인할 수 있습니다." />
      <div v-if="station" class="station-grid">
        <StationMap :station="station" />
        <article class="station-info">
          <RouterLink class="line-back" to="/line7">노선도</RouterLink>
          <h1>{{ station.name }} ({{ station.id }})</h1>
          <p class="station-english">{{ station.english }}</p>
          <h2>역 기본 정보</h2>
          <dl>
            <div><dt>주소</dt><dd>{{ station.address }}</dd></div>
            <div><dt>연락처</dt><dd><a :href="'tel:' + station.phone">TEL {{ station.phone }}</a></dd></div>
            <div class="usage"><dt>이용정보</dt><dd><span>{{ station.platform }}</span><span>{{ station.restroom }}</span><span>{{ station.crossing }}</span></dd></div>
          </dl>
        </article>
        <StationFacilities :station="station" />
      </div>
      <section v-else class="station-missing"><h1>역 정보를 찾을 수 없습니다.</h1><RouterLink class="line-back" to="/line7">노선도</RouterLink></section>
    </main>
    <PageFooter />
  </div>
</template>
<style scoped src="./styles/station.css"></style>
