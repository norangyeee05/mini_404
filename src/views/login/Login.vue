<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthBrand from '../../components/AuthBrand.vue'
import { login } from '../../services/auth'
import { apiError } from '../../services/api'
const router = useRouter()
const route = useRoute()
const loginId = ref('')
const password = ref('')
const invalid = ref(false)
watch([loginId, password], () => { invalid.value = false })
const errorMessage = ref('')
async function submit() {
  invalid.value = false
  errorMessage.value = ''
  try {
    const member = await login({ loginId: loginId.value.trim(), password: password.value })
    router.push(route.query.redirect || (member.role === 'ADMIN' ? '/admin' : '/'))
  } catch (error) { invalid.value = true; errorMessage.value = apiError(error, '아이디 및 비밀번호가 올바르지 않습니다.') }
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
          <p v-if="invalid" id="login-error" class="auth-error" role="alert">{{ errorMessage }}</p>
        </div>
        <button class="auth-button auth-button--primary" type="submit">로그인</button>
      </form>
      <RouterLink class="auth-button auth-button--secondary signup-link" to="/terms">회원가입</RouterLink>

    </section>
  </main>
</template>



