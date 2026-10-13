<script setup>
import { useRouter } from 'vue-router'

defineProps({ open: { type: Boolean, default: false } })
const emit = defineEmits(['close'])
const router = useRouter()

function goToLogin() {
  emit('close')
  router.push('/login')
}
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="login-required-backdrop" @click.self="emit('close')">
      <section class="login-required-dialog" role="dialog" aria-modal="true" aria-labelledby="login-required-title">
        <button class="login-required-close" type="button" aria-label="닫기" @click="emit('close')">×</button>
        <div class="login-required-icon" aria-hidden="true">
          <svg viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="29" fill="none" stroke="currentColor" stroke-width="4" />
            <path d="M32 18v18m0 9v1" fill="none" stroke="currentColor" stroke-width="5" stroke-linecap="round" />
          </svg>
        </div>
        <h2 id="login-required-title">권한이 없습니다.</h2>
        <p>해당 페이지에 접근 권한이 없습니다.<br />로그인하여 주시길 바랍니다.</p>
        <button class="login-required-confirm" type="button" @click="goToLogin">확인</button>
      </section>
    </div>
  </Teleport>
</template>

<style>
.login-required-backdrop { position: fixed; z-index: 1000; inset: 0; display: grid; place-items: center; padding: 20px; background: rgba(91, 110, 128, .52); }
.login-required-dialog { position: relative; width: min(650px, 100%); min-height: 500px; display: flex; flex-direction: column; align-items: center; padding: 42px 48px 78px; border-radius: 28px; background: #fff; text-align: center; }
.login-required-close { position: absolute; top: 20px; right: 48px; padding: 0; border: 0; background: transparent; color: #858585; font: inherit; font-size: 24px; line-height: 1; cursor: pointer; }
.login-required-icon { width: 100px; height: 100px; color: #ff5b66; }
.login-required-icon svg { display: block; width: 100%; height: 100%; }
.login-required-dialog h2 { margin: 40px 0 0; color: #050505; font-size: 34px; font-weight: 700; line-height: 1.35; }
.login-required-dialog p { margin: 38px 0 0; color: #858585; font-size: 20px; line-height: 1.55; }
.login-required-confirm { width: min(400px, 100%); min-height: 41px; margin-top: auto; border: 0; border-radius: 9px; background: #ededed; color: #111; font: inherit; font-weight: 700; cursor: pointer; }
@media (max-width: 640px) {
  .login-required-dialog { min-height: 400px; padding: 38px 24px 42px; border-radius: 22px; }
  .login-required-close { top: 18px; right: 24px; }
  .login-required-icon { width: 82px; height: 82px; }
  .login-required-dialog h2 { margin-top: 34px; font-size: 27px; }
  .login-required-dialog p { margin-top: 26px; font-size: 16px; }
}
</style>
