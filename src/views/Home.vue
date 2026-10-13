<script setup>
import { useRouter } from 'vue-router'
import AuthBrand from '../components/AuthBrand.vue'
import DashboardCard from '../components/DashboardCard.vue'
import { currentMember, logout } from '../services/mockAuth'
const router = useRouter()
const cards = [ { title: '전체 서비스', value: 22 }, { title: '정상 운행', value: 16 }, { title: '점검 중', value: 5 }, { title: '장애 발생', value: 1 } ]
function signout() { logout(); router.push('/login') }
</script>
<template>
  <main class="dashboard-shell">
    <header class="welcome-header"><AuthBrand /><button v-if="currentMember" class="auth-button auth-button--primary logout-button" @click="signout">로그아웃</button><RouterLink v-else to="/login">로그인</RouterLink></header>
    <section class="dashboard-main">
      <div class="page-heading"><h1>대시보드</h1></div>
      <p class="welcome-message">{{ currentMember ? `${currentMember.name}님, 로그인되었습니다.` : '인천교통공사에 오신 것을 환영합니다.' }}</p>
      <div class="summary-grid"><DashboardCard v-for="card in cards" :key="card.title" v-bind="card" note="화면 확인용 더미 데이터" /></div>
    </section>
  </main>
</template>
<style scoped>
.welcome-header { display: flex; flex-wrap: wrap; align-items: center; justify-content: space-between; gap: 20px; padding: 30px 0; }
.welcome-header :deep(.auth-brand) { margin: 0; }
.logout-button { width: auto; }
.welcome-message { margin-bottom: 24px; }
</style>
