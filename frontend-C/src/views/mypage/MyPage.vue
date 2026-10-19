<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import WithdrawalModal from './components/WithdrawalModal.vue'
import { currentMember, updateProfile, withdraw } from '../../services/auth'
import { api, apiError } from '../../services/backendApi'
import PageBanner from '../../components/PageBanner.vue'

const route = useRoute()
const router = useRouter()
const deleting = computed(() => route.path === '/mypage/delete')
const profile = reactive({ name: currentMember.value?.name ?? '', email: currentMember.value?.email ?? '', phone: currentMember.value?.phone ?? '' })
const editing = reactive({ name: false, email: false, phone: false })
const credentials = reactive({ loginId: '', password: '', confirmation: '' })
const message = ref('')
const error = ref('')
const modal = ref('')
watch(() => route.path, () => { error.value = ''; message.value = ''; credentials.password = ''; credentials.confirmation = '' })
onMounted(async () => {
  try {
    const { name, email, phone } = currentMember.value?.adminAccount ? await api.admin.me() : await api.members.me()
    profile.name = name || ''; profile.email = email || ''; profile.phone = phone || ''
    if (!currentMember.value?.adminAccount) currentMember.value.phone = profile.phone
  }
  catch (exception) { error.value = apiError(exception) }
})
async function save() {
  error.value = ''; message.value = ''
  try { await updateProfile(profile); editing.name = false; editing.email = false; editing.phone = false; message.value = '회원정보가 저장되었습니다.' }
  catch (exception) { error.value = apiError(exception) }
}
function requestWithdrawal() {
  error.value = ''
  if (credentials.password !== credentials.confirmation) { error.value = '비밀번호 확인이 일치하지 않습니다.'; return }
  modal.value = 'confirm'
}
async function confirm() {
  if (modal.value === 'complete') { router.push('/'); return }
  try { await withdraw(credentials); modal.value = 'complete' }
  catch (exception) { modal.value = ''; error.value = apiError(exception) }
}
function close() { if (modal.value === 'complete') router.push('/'); else modal.value = '' }
</script>
<template>
  <div class="mypage-shell">
    <PageHeader />
    <main v-if="modal !== 'complete'" class="mypage-main">
      <PageBanner :title="deleting ? '회원탈퇴' : '마이페이지'" breadcrumb-title="마이페이지" :stage="deleting ? '회원탈퇴' : undefined" :description="'언제나 안전하고 편리한 교통서비스를 위해\n최선을 다하겠습니다.'" />
      <div class="mypage-layout">
        <aside class="mypage-sidebar"><nav aria-label="마이페이지 메뉴"><RouterLink :to="currentMember?.adminAccount ? '/admin/mypage' : '/mypage'" :aria-current="!deleting ? 'page' : undefined">회원정보</RouterLink><RouterLink v-if="currentMember?.role === 'USER'" to="/mypage/delete" :aria-current="deleting ? 'page' : undefined">회원 탈퇴</RouterLink></nav></aside>
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
            <div class="profile-row"><label class="profile-label" for="profile-phone">전화번호 :</label><div class="profile-value"><input id="profile-phone" v-model="profile.phone" type="tel" autocomplete="tel" maxlength="20" :readonly="!editing.phone" placeholder="전화번호를 입력하세요" /><button class="profile-button" type="button" @click="editing.phone = !editing.phone">{{ editing.phone ? '완료' : '변경' }}</button></div></div>
            <div v-for="field in ['name', 'email']" :key="field" class="profile-row"><label class="profile-label" :for="`profile-${field}`">{{ field === 'name' ? '이름 :' : '이메일 :' }}</label><div class="profile-value"><input :id="`profile-${field}`" v-model="profile[field]" :type="field === 'email' ? 'email' : 'text'" :readonly="!editing[field]" :autocomplete="field" required /><button class="profile-button" type="button" @click="editing[field] = !editing[field]">{{ editing[field] ? '완료' : '변경' }}</button></div></div>
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
<style scoped src="./styles/mypage.css"></style>


