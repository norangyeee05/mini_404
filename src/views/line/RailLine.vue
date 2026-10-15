<script setup>
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import LineMap from './components/LineMap.vue'
import { stations } from './services/lineData'
const router = useRouter()
function select(station) { router.push({ name: 'station', params: { stationId: station.id } }) }
</script>
<template>
  <div class="rail-shell">
    <PageHeader />
    <main class="rail-main">
      <PageBanner title="철도 정보" breadcrumb-title="철도정보" description="7호선 노선도에 대해 확인할 수 있습니다." />
      <section class="rail-panel" aria-labelledby="line-title">
        <h2 id="line-title">노선도</h2>
        <LineMap :stations="stations" @select="select" />
      </section>
    </main>
    <PageFooter />
  </div>
</template>
<style scoped src="./styles/line.css"></style>
