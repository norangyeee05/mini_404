<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import transportLogo from '../assets/transport-404-logo.png'
import { currentMember, logout } from '../services/mockAuth'
const props = defineProps({ role: { type: String, default: undefined }, userName: { type: String, default: undefined } })
const router = useRouter()
const role = computed(() => props.role ?? (currentMember.value?.role === 'ADMIN' ? 'admin' : currentMember.value ? 'member' : 'guest'))
const userName = computed(() => props.userName ?? currentMember.value?.name ?? '홍길동')
const navigation = computed(() => [{ label: '공지사항', to: '/notices' }, { label: '자료실', to: role.value === 'admin' ? '/admin/resources' : '/resources' }, { label: 'Q&A', to: '/qna' }, { label: '철도정보', to: '/rail-info' }])
function signout() { logout(); router.push('/') }
</script>
<template>
  <header class="site-header">
    <RouterLink class="brand" to="/" aria-label="인천교통사공사 홈"><img class="brand-logo" :src="transportLogo" alt="404" /><span class="brand-name">인천교통사공사</span></RouterLink>
    <nav class="primary-nav" aria-label="주요 메뉴"><RouterLink v-for="item in navigation" :key="item.to" :to="item.to">{{ item.label }}</RouterLink></nav>
    <nav class="account-nav" aria-label="계정 메뉴">
      <RouterLink v-if="role === 'guest'" class="account-link account-link--guest" to="/login">로그인</RouterLink>
      <template v-else><RouterLink v-if="role === 'admin'" to="/admin/members">회원관리</RouterLink><RouterLink to="/mypage">마이페이지</RouterLink><button class="header-logout" @click="signout">로그아웃</button><span class="role-chip">{{ role === 'admin' ? '관리자님' : `${userName}님` }}</span></template>
    </nav>
  </header>
</template>
<style scoped>
.site-header { min-height: 59px; gap: 36px; padding: 0 4px; }
.brand { gap: 5px; }.brand-logo { width: 58px; height: 36px; }.brand-name { color: #155369; font-size: 24px; }
.primary-nav .router-link-active::after { transform: scaleX(1); }.primary-nav { gap: 50px; color: #155369; font-size: 20px; }.account-nav { gap: 22px; color: #155369; font-size: 20px; }
.account-link--guest, .role-chip { font-size: 16px; font-weight: 400; padding: 5px 15px; }
.header-logout { padding: 0; border: 0; background: none; color: inherit; font: inherit; }
a:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 4px; }
@media (max-width: 1150px) { .site-header { flex-wrap: wrap; gap: 12px 24px; padding: 12px 4px; } .primary-nav { order: 3; width: 100%; justify-content: center; gap: 40px; margin: 0; } .account-nav { margin-left: auto; font-size: 16px; } }
@media (max-width: 600px) { .brand-name { font-size: 18px; }.brand-logo { width: 48px; }.primary-nav { font-size: 16px; gap: 20px; }.account-nav { font-size: 14px; gap: 14px; }.role-chip { display: none; } }
</style>


