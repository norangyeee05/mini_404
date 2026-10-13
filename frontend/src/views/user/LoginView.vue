<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as requestLogin } from '../../api/auth'
import { errorMessage } from '../../api/client'
import { useAuthStore } from '../../stores/auth'
import transportLogo from '../../assets/transport-404-logo.png'
import './login.css'

const router = useRouter()
const auth = useAuthStore()
const loginId = ref('')
const password = ref('')
const message = ref('')
const submitting = ref(false)

async function submitLogin() {
  message.value = ''
  submitting.value = true
  try {
    const response = await requestLogin({ loginId: loginId.value, password: password.value })
    const result = response.data?.data ?? response.data
    const user = result?.member ?? result?.user ?? result
    if (!['USER', 'ADMIN'].includes(user?.role)) throw new Error('로그인 응답에 유효한 role(USER 또는 ADMIN)이 없습니다.')
    auth.setUser(user)
    const redirect = router.currentRoute.value.query.redirect
    await router.push(redirect || (user.role === 'ADMIN' ? '/admin/dashboard' : '/member/home'))
  } catch (error) { message.value = errorMessage(error, '아이디 및 비밀번호가 올바르지 않습니다.') }
  finally { submitting.value = false }
}
</script>

<template>
  <main class="login-page">
    <section class="login-form-wrap" aria-labelledby="login-title">
      <a class="login-brand" href="/" aria-label="인천교통공사 홈">
        <img :src="transportLogo" alt="" />
        <h1 id="login-title">인천교통공사</h1>
      </a>

      <form class="login-form" @submit.prevent="submitLogin">
        <label class="login-visually-hidden" for="login-username">아이디</label>
        <input id="login-username" v-model.trim="loginId" type="text" autocomplete="username" placeholder="아이디" required />
        <label class="login-visually-hidden" for="login-password">비밀번호</label>
        <input id="login-password" v-model="password" type="password" autocomplete="current-password" placeholder="비밀번호" required />
        <p v-if="message" class="login-error" role="alert">{{ message }}</p>
        <button class="login-submit" type="submit">로그인</button>
        <button class="login-signup" type="button" @click="router.push('/terms')">회원가입</button>
      </form>
    </section>
  </main>
</template>
