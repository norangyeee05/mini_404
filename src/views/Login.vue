<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import AuthBrand from '../components/AuthBrand.vue'
import { login } from '../services/mockAuth'
const router = useRouter()
const loginId = ref('')
const password = ref('')
const invalid = ref(false)
watch([loginId, password], () => { invalid.value = false })
function submit() {
  invalid.value = false
  if (!login({ loginId: loginId.value.trim(), password: password.value })) {
    invalid.value = true
    return
  }
  router.push('/')
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel" aria-label="로그인">
      <AuthBrand />
      <form novalidate @submit.prevent="submit">
        <label class="sr-only" for="login-id">아이디</label>
        <input id="login-id" v-model="loginId" placeholder="아이디" autocomplete="username" required :aria-invalid="invalid" :aria-describedby="invalid ? 'login-error' : undefined" />
        <label class="sr-only" for="login-password">비밀번호</label>
        <input id="login-password" v-model="password" type="password" placeholder="비밀번호" autocomplete="current-password" required :aria-invalid="invalid" :aria-describedby="invalid ? 'login-error' : undefined" />
        <div class="auth-feedback" aria-live="polite">
          <p v-if="invalid" id="login-error" class="auth-error" role="alert">아이디 및 비밀번호가 올바르지 않습니다.</p>
        </div>
        <button class="auth-button auth-button--primary" type="submit">로그인</button>
      </form>
      <RouterLink class="auth-button auth-button--secondary signup-link" to="/terms">회원가입</RouterLink>

      <p class="demo-hint">테스트 계정: demo / demo1234 </p>
      <p class="demo-hint">테스트 계정: admin / admin1234</p>
    </section>
  </main>
</template>


