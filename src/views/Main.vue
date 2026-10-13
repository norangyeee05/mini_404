<script setup>
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import BoardCard from '../components/BoardCard.vue'
import { notices, questions, stations } from '../services/mainData'
import mainBanner from '../assets/main-banner.png'
import resourcesBanner from '../assets/resources-banner.png'
</script>
<template>
  <div class="main-shell">
    <PageHeader />
    <main class="main-content">
      <img class="main-banner" :src="mainBanner" alt="INCHEON TRANSIT CORPORATION. 안전하고 편리한 교통서비스로 시민의 행복한 일상을 만들어갑니다." width="1381" height="316" />
      <section class="station-panel" aria-labelledby="station-title"><h2 id="station-title">역정보</h2><div class="station-line"><RouterLink v-for="station in stations" :key="station" :class="{ 'station-stadium': station === '부천종합운동장' }" :to="{ path: '/rail-info', query: { station } }"><span v-if="station === '부천종합운동장'">부천종합<br />운동장</span><span v-else>{{ station }}</span></RouterLink></div></section>
      <div class="main-boards">
        <BoardCard title="공지사항" :rows="notices" to="/notices" />
        <div class="main-right"><RouterLink class="resources-banner" to="/resources" aria-label="인천교통사공사 자료실 바로가기"><img :src="resourcesBanner" alt="더 편리한 교통, 더 좋은 일상. 인천교통사공사 자료실 바로가기" width="593" height="177" /></RouterLink><BoardCard title="Q&A" :rows="questions" to="/qna" more-label="더보기" compact /></div>
      </div>
    </main>
    <PageFooter />
  </div>
</template>
<style scoped>
.main-shell { display: flex; flex-direction: column; width: min(1440px, 100%); min-height: 100svh; margin: 0 auto; padding: 0 34px; background: #e2ecee; }
.main-content { flex: 1; }.main-banner { display: block; width: 100%; height: 316px; object-fit: cover; object-position: left center; border-radius: 20px; }
.station-panel { margin-top: 24px; padding: 5px 0 13px; border-radius: 10px; background: #d2e6e9; } h2 { margin: 0 10px; padding-left: 10px; border-left: 3px solid #155369; font-size: 24px; line-height: 30px; color: #155369; }
.station-line { position: relative; display: flex; align-items: center; justify-content: space-between; gap: 20px; min-height: 72px; margin-top: 14px; }.station-line::before { position: absolute; top: 50%; right: 20px; left: 20px; height: 14px; background: #666d2d; content: ''; transform: translateY(-50%); }.station-line a { position: relative; z-index: 1; display: grid; place-items: center; flex: 1; min-width: 85px; max-width: 103px; min-height: 39px; padding: 6px 12px; border-radius: 24px; background: #878e3f; color: #fff; font-size: 18px; line-height: 26px; text-align: center; word-break: keep-all; }.station-line a.station-stadium { flex: 0 0 85px; min-width: 85px; max-width: 85px; min-height: 65px; padding: 6px 4px; border-radius: 11px; }.station-stadium span { white-space: nowrap; }
.main-boards { display: grid; grid-template-columns: 1.28fr 1fr; gap: 24px; margin-top: 36px; margin-bottom: 30px; }.main-right { display: flex; flex-direction: column; gap: 20px; padding-top: 8px; min-width: 0; }.resources-banner { display: block; border-radius: 20px; overflow: hidden; }.resources-banner img { display: block; width: 100%; height: 177px; object-fit: cover; object-position: left; }
a:focus-visible { outline: 3px solid #155369; outline-offset: 3px; }
@media (max-width: 1250px) { .station-line { overflow-x: auto; justify-content: flex-start; padding: 0 10px; }.station-line a { flex: 0 0 100px; }.station-line::before { width: 1280px; right: auto; }.main-boards { grid-template-columns: 1fr; }.main-right { display: grid; grid-template-columns: 1fr 1.2fr; } }
@media (max-width: 760px) { .main-shell { padding: 0 16px; }.main-banner { height: 240px; }.main-boards { gap: 20px; margin-top: 24px; }.main-right { grid-template-columns: 1fr; }.station-line a { font-size: 16px; }.resources-banner img { height: auto; min-height: 150px; }.main-banner { object-position: 25% center; } }
</style>

