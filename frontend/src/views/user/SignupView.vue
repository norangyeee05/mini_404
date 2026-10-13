<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { checkId as requestCheckId, signup } from '../../api/auth'
import { errorMessage } from '../../api/client'
import './signup.css'

const router = useRouter()
const form = ref({
  loginId: '',
  password: '',
  passwordConfirm: '',
  name: '',
  email: '',
})
const message = ref('')
const idCheckMessage = ref('')
const submitting = ref(false)

async function checkUsername() {
  idCheckMessage.value = ''
  if (!form.value.loginId.trim()) return
  try {
    const response = await requestCheckId({ loginId: form.value.loginId.trim() })
    const data = response.data?.data ?? response.data
    idCheckMessage.value = (data?.available ?? data?.isAvailable) ? '사용할 수 있는 아이디입니다.' : '이미 사용 중인 아이디입니다.'
  } catch (error) { idCheckMessage.value = errorMessage(error, '아이디 중복을 확인하지 못했습니다.') }
}

async function submitSignup() {
  message.value = ''

  if (form.value.password !== form.value.passwordConfirm) {
    message.value = '비밀번호 확인이 일치하지 않습니다.'
    return
  }

  submitting.value = true
  try {
    if (router.currentRoute.value.query.agreeTerms !== 'true') {
      message.value = '회원가입을 위해 이용약관과 개인정보 처리방침에 동의해주세요.'
      return
    }
    await signup({ loginId: form.value.loginId.trim(), password: form.value.password, name: form.value.name.trim(), email: form.value.email.trim(), agreeTerms: true })
    router.push('/signup/complete')
  } catch (error) { message.value = errorMessage(error, '회원가입에 실패했습니다.') }
  finally { submitting.value = false }
}
</script>

<template>
  <main class="signup-page">
    <section class="signup-card" aria-labelledby="signup-title">
      <h1 id="signup-title">회원가입</h1>
      <p class="signup-intro">회원정보를 입력하여 계정을 생성하세요</p>

      <form class="signup-form" @submit.prevent="submitSignup">
        <label for="signup-username">아이디 <span>*</span></label>
        <input id="signup-username" v-model.trim="form.loginId" @blur="checkUsername" type="text" maxlength="20" autocomplete="username" placeholder="아이디(20글자 이내)" required />
        <p v-if="idCheckMessage" class="signup-message" role="status">{{ idCheckMessage }}</p>

        <label for="signup-password">비밀번호 <span>*</span></label>
        <input id="signup-password" v-model="form.password" type="password" minlength="4" maxlength="20" autocomplete="new-password" placeholder="비번(4~20글자 이내)" required />

        <label for="signup-password-confirm">비밀번호 확인 <span>*</span></label>
        <input id="signup-password-confirm" v-model="form.passwordConfirm" type="password" minlength="4" maxlength="20" autocomplete="new-password" placeholder="비밀번호를 다시 입력하세요" required />

        <label for="signup-name">이름 <span>*</span></label>
        <input id="signup-name" v-model.trim="form.name" type="text" autocomplete="name" placeholder="이름을 입력하세요" required />

        <label for="signup-email">이메일 <span>*</span></label>
        <input id="signup-email" v-model.trim="form.email" type="email" autocomplete="email" placeholder="이메일을 입력하세요" required />


        <p v-if="message" class="signup-message" role="alert">{{ message }}</p>
        <button type="submit" class="signup-submit" :disabled="submitting">회원가입</button>
      </form>

      <p class="signup-login-link">이미 계정이 있으신가요? <a href="/login">로그인</a></p>
    </section>
  </main>
</template>
