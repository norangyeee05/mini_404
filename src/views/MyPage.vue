<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import WithdrawalModal from '../components/WithdrawalModal.vue'
import { currentMember, updateProfile, verifyWithdrawal, withdraw } from '../services/mockAuth'
import heroImage from '../assets/notice-hero.png'

const route = useRoute()
const router = useRouter()
const deleting = computed(() => route.path === '/mypage/delete')
const profile = reactive({ name: currentMember.value?.name ?? '', email: currentMember.value?.email ?? '' })
const editing = reactive({ name: false, email: false })
const credentials = reactive({ loginId: '', password: '', confirmation: '' })
const message = ref('')
const error = ref('')
const modal = ref('')
watch(() => route.path, () => { error.value = ''; message.value = ''; credentials.password = ''; credentials.confirmation = '' })
function save() {
  error.value = ''; message.value = ''
  try { updateProfile(profile); editing.name = false; editing.email = false; message.value = '회원정보가 저장되었습니다.' }
  catch (exception) { error.value = exception.message }
}
function requestWithdrawal() {
  error.value = ''
  if (credentials.password !== credentials.confirmation) { error.value = '비밀번호 확인이 일치하지 않습니다.'; return }
  if (!verifyWithdrawal(credentials)) { error.value = '아이디 및 비밀번호가 올바르지 않습니다.'; return }
  modal.value = 'confirm'
}
function confirm() {
  if (modal.value === 'complete') { router.push('/'); return }
  try { withdraw(credentials); modal.value = 'complete' }
  catch (exception) { modal.value = ''; error.value = exception.message }
}
function close() { if (modal.value === 'complete') router.push('/'); else modal.value = '' }
</script>
<template>
  <div class="mypage-shell">
    <PageHeader />
    <main v-if="modal !== 'complete'" class="mypage-main">
      <section class="mypage-banner" aria-label="마이페이지 안내">
        <img class="mypage-banner-image" :src="heroImage" alt="인천 도시철도 열차" />
        <div class="mypage-banner-copy">
          <p class="banner-breadcrumb">HOME &gt; 마이페이지<span v-if="deleting"> &gt; 회원탈퇴</span></p>
          <h2>{{ deleting ? '회원탈퇴' : '마이페이지' }}</h2>
          <p class="banner-description">언제나 안전하고 편리한 교통서비스를 위해<br />최선을 다하겠습니다.</p>
        </div>
      </section>
      <div class="mypage-layout">
        <aside class="mypage-sidebar"><nav aria-label="마이페이지 메뉴"><RouterLink to="/mypage" :aria-current="!deleting ? 'page' : undefined">회원정보</RouterLink><RouterLink to="/mypage/delete" :aria-current="deleting ? 'page' : undefined">회원 탈퇴</RouterLink></nav></aside>
        <section class="mypage-panel">
          <h1>{{ deleting ? '회원탈퇴' : '회원정보' }}</h1>
          <template v-if="deleting">
            <p class="withdraw-description">회원 탈퇴 시 모든 정보가 삭제되며, 복구할 수 없습니다.<br />신중하게 결정해주세요.</p>
            <form class="withdraw-form" @submit.prevent="requestWithdrawal">
              <div class="withdraw-fields"><label for="withdraw-id">아이디</label><input id="withdraw-id" v-model="credentials.loginId" autocomplete="username" required /><label for="withdraw-password">비밀번호</label><input id="withdraw-password" v-model="credentials.password" type="password" autocomplete="current-password" required /><label for="withdraw-confirmation">비밀번호 확인</label><input id="withdraw-confirmation" v-model="credentials.confirmation" type="password" autocomplete="current-password" required /></div>
              <p v-if="error" class="feedback error" role="alert">{{ error }}</p>
              <button class="withdraw-button" type="submit">회원 탈퇴</button>
            </form>
          </template>
          <form v-else class="profile-form" @submit.prevent="save">
            <div class="profile-row"><span class="profile-label">아이디 :</span><span class="profile-value">{{ currentMember?.loginId }}</span></div>
            <div class="profile-row"><span class="profile-label">전화번호 :</span><span class="profile-value">{{ currentMember?.phone || '등록된 전화번호가 없습니다.' }}</span></div>
            <div v-for="field in ['name', 'email']" :key="field" class="profile-row"><label class="profile-label" :for="`profile-${field}`">{{ field === 'name' ? '이름:' : '이메일:' }}</label><div class="profile-value"><input :id="`profile-${field}`" v-model="profile[field]" :type="field === 'email' ? 'email' : 'text'" :readonly="!editing[field]" :autocomplete="field" required /><button class="profile-button" type="button" @click="editing[field] = !editing[field]">{{ editing[field] ? '완료' : '변경' }}</button></div></div>
            <p v-if="error || message" class="feedback" :class="{ error }" role="status">{{ error || message }}</p>
            <button class="profile-button save-button" type="submit">저 장</button>
          </form>
        </section>
      </div>
    </main>
    <div v-else class="completion-space" />
    <PageFooter />
    <WithdrawalModal v-if="modal" :completed="modal === 'complete'" @confirm="confirm" @close="close" />
  </div>
</template>
<style scoped>
.mypage-shell { display: flex; flex-direction: column; width: min(1440px, 100%); min-height: 100svh; margin: 0 auto; padding: 0 30px; background: #e2ecee; }.mypage-main, .completion-space { flex: 1; }.mypage-banner { position: relative; width: 100%; height: 316px; overflow: hidden; border-radius: 20px; }.mypage-banner-image { display: block; width: 100%; height: 100%; object-fit: cover; object-position: center; }.mypage-banner-copy { position: absolute; top: 46px; left: 34px; right: 24px; color: #eef3f5; text-shadow: 0 1px 3px rgba(0,0,0,.25); }.banner-breadcrumb { margin: 0 0 18px; font-size: 20px; line-height: 28px; }.mypage-banner-copy h2 { margin: 0 0 2px; font-size: 32px; line-height: 40px; }.banner-description { margin: 0; font-size: 20px; line-height: 25px; }
.mypage-layout { display: grid; grid-template-columns: 234px minmax(0, 1fr); gap: 15px; margin-top: 12px; }.mypage-sidebar { padding: 17px 8px; border-radius: 20px; background: #bcd1d5; }.mypage-sidebar nav { display: grid; gap: 18px; }.mypage-sidebar a { display: block; min-height: 40px; padding: 9px 16px; border-radius: 8px; background: #fff; color: #858585; font-size: 16px; font-weight: 600; }.mypage-sidebar a[aria-current] { background: #f7f7f7; }
.mypage-panel { min-height: 570px; padding: 18px 20px 26px; border-radius: 22px; background: #f0f5f7; } h1 { margin: 0 0 16px; color: #155369; font-size: 24px; line-height: 30px; }.profile-form { min-height: 480px; padding: 12px 20px 40px; border-radius: 10px; background: #e6eef0; }.profile-row { display: grid; grid-template-columns: 160px minmax(0, 1fr); margin-bottom: 16px; min-height: 55px; font-size: 20px; }.profile-label { display: flex; align-items: center; padding: 12px 14px; border-radius: 10px; background: #dfe7e9; color: #427486; font-weight: 700; }.profile-value { display: flex; align-items: center; gap: 12px; min-width: 0; padding: 8px 18px; border-radius: 10px; background: #f0f5f7; color: #000; }.profile-value input { width: 100%; min-width: 0; padding: 5px 0; border: 0; background: transparent; color: #000; font: inherit; }.profile-value input:not([readonly]) { border-bottom: 1px solid #427486; }.profile-button { flex: 0 0 120px; min-height: 38px; padding: 2px 16px; border: 2px solid #d6e0e3; border-radius: 24px; background: #457384; color: #fff; font-size: 20px; font-weight: 700; }.save-button { display: block; width: 120px; margin: 119px auto 0; }
.withdraw-description { margin: -12px 0 18px; color: #858585; font-size: 16px; line-height: 26px; }.withdraw-form { min-height: 400px; padding: 36px 35px 30px; border-radius: 10px; background: #fff; }.withdraw-fields { display: grid; grid-template-columns: 170px minmax(0, 1fr); align-items: center; gap: 15px 18px; }.withdraw-fields label { color: #155369; font-size: 24px; font-weight: 700; }.withdraw-fields input { width: 100%; height: 51px; padding: 8px 14px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; color: #000; font: inherit; }.withdraw-button { display: block; width: min(445px, 100%); min-height: 56px; margin: 94px auto 0; border: 0; border-radius: 21px; background: #ff292d; color: #fff; font-size: 24px; font-weight: 700; }.feedback { margin: 12px 0 0; color: #155369; text-align: center; }.error { color: #f00; } input:focus-visible, button:focus-visible, a:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
@media (max-width: 900px) { .mypage-shell { padding: 0 16px; }.mypage-layout { grid-template-columns: 1fr; }.mypage-sidebar nav { grid-template-columns: 1fr 1fr; }.mypage-banner { height: 240px; }.mypage-panel { min-height: auto; }.profile-row { grid-template-columns: 120px minmax(0, 1fr); font-size: 17px; }.profile-button { flex-basis: 80px; font-size: 17px; }.withdraw-fields { grid-template-columns: 130px minmax(0, 1fr); }.withdraw-fields label { font-size: 20px; } }
@media (max-width: 540px) { .mypage-banner-copy { top: 34px; left: 20px; right: 16px; }.banner-breadcrumb { font-size: 16px; }.mypage-banner-copy h2 { font-size: 28px; }.banner-description { font-size: 16px; line-height: 24px; } .mypage-panel { padding: 18px 12px; }.profile-form { padding: 12px; min-height: auto; }.profile-row { grid-template-columns: 1fr; }.profile-value { padding: 10px; }.profile-label { padding: 8px 10px; }.save-button { margin-top: 40px; }.withdraw-form { padding: 24px 16px; }.withdraw-fields { grid-template-columns: 1fr; gap: 8px; }.withdraw-fields label { margin-top: 10px; font-size: 18px; }.withdraw-button { margin-top: 40px; font-size: 20px; } }
</style>

