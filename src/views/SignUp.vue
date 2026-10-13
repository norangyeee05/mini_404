<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import SignUpSuccessModal from '../components/SignUpSuccessModal.vue'
import { signup } from '../services/mockAuth'
import { allAgreed } from '../services/registrationConsent'
const router = useRouter()
const form = reactive({ loginId: '', password: '', passwordConfirm: '', name: '', email: '', phone: '' })
const error = ref('')
const completed = ref(false)
const fields = [
  { key: 'loginId', label: '아이디', placeholder: '아이디(20글자 이내)', autocomplete: 'username', maxlength: 20 },
  { key: 'password', label: '비밀번호', placeholder: '비번(4~20글자 이내)', type: 'password', autocomplete: 'new-password', minlength: 4, maxlength: 20 },
  { key: 'passwordConfirm', label: '비밀번호 확인', placeholder: '비밀번호를 다시 입력하세요', type: 'password', autocomplete: 'new-password', minlength: 4, maxlength: 20 },
  { key: 'name', label: '이름', placeholder: '이름을 입력하세요', autocomplete: 'name' },
  { key: 'email', label: '이메일', placeholder: '이메일을 입력하세요', type: 'email', autocomplete: 'email' },
  { key: 'phone', label: '전화번호(선택)', placeholder: '전화번호를 입력하세요', type: 'tel', autocomplete: 'tel', optional: true },
]
function submit() {
  error.value = ''
  if (form.password !== form.passwordConfirm) { error.value = '비밀번호가 일치하지 않습니다.'; return }
  if (!allAgreed.value) { error.value = '먼저 이용약관과 개인정보 처리방침에 동의해 주세요.'; return }
  try {
    signup({ ...form, loginId: form.loginId.trim(), name: form.name.trim(), email: form.email.trim(), agreeTerms: allAgreed.value })
    completed.value = true
  } catch (exception) { error.value = exception.message }
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
<style scoped>
.signup-page { min-height: 100vh; min-height: 100svh; padding: 54px 20px 52px; background: #e2ecee; }
.signup-panel { width: min(720px, 100%); min-height: 918px; margin: 0 auto; padding: 38px 60px 56px; border-radius: 12px; background: #f0f5f7; }
h1 { margin: 0; color: #155369; font-size: 36px; line-height: 48px; font-weight: 800; }
.signup-description { margin: 0; color: #858585; font-size: 16px; line-height: 24px; }
.signup-fields { display: grid; gap: 14px; margin: 15px 15px 0; }
.signup-field label { display: block; margin-bottom: 14px; color: #858585; font-size: 20px; line-height: 28px; font-weight: 600; }
.required-mark { margin-left: 3px; color: #f00; }
.signup-field input { display: block; width: 100%; height: 50px; padding: 0 15px; border: 1px solid #ddd; border-radius: 8px; background: #fff; color: #222; font: 20px/1.4 Arial, 'Noto Sans KR', sans-serif; }
.signup-field input::placeholder { color: #858585; opacity: 1; }
.signup-field input:focus-visible, button:focus-visible, a:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
.signup-actions { position: relative; margin-top: 42px; }
.signup-submit { display: block; width: min(400px, 100%); height: 40px; margin: 0 auto; border: 0; border-radius: 8px; background: #eee; color: #000; font-size: 16px; }
.signup-submit:hover { background: #ddd; }
.login-prompt { margin: 12px 0 0; color: #858585; text-align: center; font-size: 16px; line-height: 24px; }
.login-prompt a { margin-left: 5px; color: #385bf5; font-weight: 700; text-decoration: underline; }
.signup-error { margin: 0 0 12px; color: #f00; font-size: 14px; text-align: center; }
.signup-error a { text-decoration: underline; }
.signup-complete-shell { display: flex; flex-direction: column; min-height: 100svh; padding: 0 34px; background: #e2ecee; }
.completion-space { flex: 1; }
.signup-complete-shell :deep(.brand-logo) { width: 58px; height: 36px; }
.signup-complete-shell :deep(.brand-name) { font-size: 24px; }
.signup-complete-shell :deep(.primary-nav) { gap: 50px; font-size: 20px; }
.signup-complete-shell :deep(.dashboard-footer) { padding: 20px 0; color: #626262; letter-spacing: 2px; font-size: 16px; }
@media (max-width: 760px) { .signup-page { padding: 24px 16px; } .signup-panel { min-height: auto; padding: 30px 24px 40px; } .signup-fields { margin-right: 0; margin-left: 0; } .signup-complete-shell { padding: 0 16px; } }
@media (max-width: 480px) { .signup-panel { padding: 24px 18px 32px; } h1 { font-size: 30px; } .signup-description, .login-prompt { font-size: 14px; } .signup-field label, .signup-field input { font-size: 17px; } .signup-field label { margin-bottom: 9px; } .signup-actions { margin-top: 32px; } }
</style>

