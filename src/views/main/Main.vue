<script setup>
import {ref,onMounted,watch} from 'vue';import PageHeader from '../../components/PageHeader.vue';import PageFooter from '../../components/PageFooter.vue';import BoardCard from './components/BoardCard.vue';import mainBanner from '../../assets/notice-hero.png';import resourcesBanner from '../../assets/local7-1.jpg';import {noticesApi,qnaApi,line7Api} from '../../services/endpoints';import {apiError} from '../../services/api';import {currentMember} from '../../services/auth'
const notices=ref([]),questions=ref([]),stations=ref([]),error=ref('')
async function loadQuestions(){if(!currentMember.value||questions.value.length)return;try{const q=await qnaApi.list({page:1,size:3});questions.value=q.items.map(x=>({id:x.qnaId,title:x.title,author:x.writer,views:x.viewCount??'—',date:x.createdAt}))}catch(e){error.value=apiError(e)}}
watch(currentMember,loadQuestions)
onMounted(async()=>{try{const [n,s]=await Promise.all([noticesApi.list({page:1,size:5}),line7Api.stations()]);notices.value=n.items.map(x=>({id:x.noticeId,title:x.title,author:x.writer,views:x.viewCount,date:x.createdAt}));stations.value=s;await loadQuestions()}catch(e){error.value=apiError(e)}})
</script>
<template>
  <div class="main-shell">
    <PageHeader />
    <main class="main-content">
      <section class="main-hero" aria-label="인천교통공사 소개">
        <img class="main-banner" :src="mainBanner" alt="인천 도시철도 열차" width="1381" height="316" />
        <div class="main-hero-copy">
          <p class="main-hero-brand">INCHEON TRANSIT CORPORATION</p>
          <p class="main-hero-message">안전하고 편리한 교통서비스로<br />시민의 행복한 일상을 만들어갑니다.</p>
        </div>
      </section>
      <section class="station-panel" aria-labelledby="station-title"><h2 id="station-title">역정보</h2><p v-if="error" role="alert">{{ error }}</p><div class="station-line"><RouterLink v-for="station in stations" :key="station.stationId" :to="`/line7/stations/${station.stationId}`"><span>{{ station.stationName }}</span></RouterLink></div></section>
      <div class="main-boards">
        <BoardCard title="공지사항" :rows="notices" to="/notices" />
        <div class="main-right"><RouterLink class="resources-banner" to="/resources" aria-label="인천교통사공사 자료실 바로가기"><img :src="resourcesBanner" alt="인천 도시철도 열차" width="593" height="177" /><span class="resources-banner-copy"><span class="resources-banner-tagline">더 편리한 교통, 더 좋은 일상</span><span class="resources-banner-title">인천교통사공사<br /><strong>자료실</strong></span><span class="resources-banner-link">바로가기 ›</span></span></RouterLink><BoardCard title="Q&A" :rows="questions" to="/qna" more-label="더보기" compact /></div>
      </div>
    </main>
    <PageFooter />
  </div>
</template>
<style scoped src="./styles/main.css"></style>


