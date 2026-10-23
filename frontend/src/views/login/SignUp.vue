<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import SignUpSuccessModal from './components/SignUpSuccessModal.vue'
import { signup } from '../../services/auth'
import { apiError } from '../../services/backendApi'
import { allAgreed } from './services/registrationConsent'
const router = useRouter()
const form = reactive({ loginId: '', password: '', passwordConfirm: '', name: '', email: '', phone: '' })
const error = ref('')
const completed = ref(false)
const fields = [
  { key: 'loginId', label: '아이디', placeholder: '아이디(20글자 이내)', autocomplete: 'username', maxlength: 20 },
  { key: 'password', label: '비밀번호', placeholder: '비밀번호(8~64자)', type: 'password', autocomplete: 'new-password', minlength: 8, maxlength: 64 },
  { key: 'passwordConfirm', label: '비밀번호 확인', placeholder: '비밀번호를 다시 입력하세요', type: 'password', autocomplete: 'new-password', minlength: 8, maxlength: 64 },
  { key: 'name', label: '이름', placeholder: '이름을 입력하세요', autocomplete: 'name' },
  { key: 'email', label: '이메일', placeholder: '이메일을 입력하세요', type: 'email', autocomplete: 'email' },
  { key: 'phone', label: '전화번호(선택)', placeholder: '전화번호를 입력하세요', type: 'tel', autocomplete: 'tel', optional: true },
]
async function submit() {
  error.value = ''
  if (form.password !== form.passwordConfirm) { error.value = '비밀번호가 일치하지 않습니다.'; return }
  if (!allAgreed.value) { error.value = '먼저 이용약관과 개인정보 처리방침에 동의해 주세요.'; return }
  try {
    await signup({ loginId: form.loginId.trim(), password: form.password, name: form.name.trim(), email: form.email.trim(), phone: form.phone.trim(), agreeTerms: allAgreed.value, agreePrivacy: allAgreed.value })
    completed.value = true
  } catch (exception) { error.value = apiError(exception) }
}
</script>
<template>
  <main v-if="!completed" class="signup-page">
    <section class="signup-panel" aria-labelledby="signup-title">
      <h1 id="signup-title">회원가입</h1>
      <p class="signup-description">회원정보를 입력하여 계정을 생성하세요</p>
      <form @submit.prevent="submit">
        <div class="signup-fields">
          <div v-for="field in fields" :key="field.key" class="signup-field">
            <label :for="`signup-${field.key}`">{{ field.label }} <span v-if="!field.optional" class="required-mark" aria-hidden="true">*</span></label>
            <input :id="`signup-${field.key}`" v-model="form[field.key]" :type="field.type || 'text'" :placeholder="field.placeholder" :autocomplete="field.autocomplete" :required="!field.optional" :minlength="field.minlength" :maxlength="field.maxlength" :pattern="['loginId', 'name'].includes(field.key) ? '.*\\S.*' : undefined" />
          </div>
        </div>
        <div class="signup-actions">
          <p v-if="error" class="signup-error" role="alert">{{ error }} <RouterLink v-if="!allAgreed" to="/terms">약관 동의하기</RouterLink></p>
          <button class="signup-submit" type="submit">회원가입</button>
          <p class="login-prompt">이미 계정이 있으신가요? <RouterLink to="/login">로그인</RouterLink></p>
        </div>
      </form>
    </section>
  </main>
  <div v-else class="signup-complete-shell">
    <PageHeader />
    <div class="completion-space" />
    <PageFooter />
    <SignUpSuccessModal @login="router.push('/login')" @close="router.push('/')" />
  </div>
</template>
<style scoped src="./styles/signup.css"></style>

