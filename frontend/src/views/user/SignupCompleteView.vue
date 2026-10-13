<script setup>
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import './signup-complete.css'

const router = useRouter()
</script>

<template>
  <div class="signup-complete-page">
    <PageHeader role="guest" />
    <main class="signup-complete-main" aria-label="회원가입 완료"></main>
    <PageFooter />

    <div class="signup-complete-backdrop">
      <section class="signup-complete-dialog" role="dialog" aria-modal="true" aria-labelledby="signup-complete-title" aria-describedby="signup-complete-description">
        <button class="signup-complete-close" type="button" aria-label="닫기" @click="router.push('/')">×</button>
        <div class="signup-complete-icon" aria-hidden="true">✓</div>
        <h1 id="signup-complete-title">회원가입이 완료되었습니다.</h1>
        <p id="signup-complete-description">로그인을 진행해주세요.</p>
        <button class="signup-complete-login" type="button" @click="router.push('/login')">로그인</button>
      </section>
    </div>
  </div>
</template>
