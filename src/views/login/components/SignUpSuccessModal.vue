<script setup>
import { onMounted, ref } from 'vue'
const emit = defineEmits(['login', 'close'])
const dialog = ref(null)
const loginButton = ref(null)
onMounted(() => { dialog.value.showModal(); loginButton.value.focus() })
</script>
<template>
  <dialog ref="dialog" class="success-dialog" aria-labelledby="signup-success-title" aria-describedby="signup-success-description" @cancel.prevent="emit('close')">
    <button class="dialog-close" type="button" aria-label="회원가입 완료 팝업 닫기" @click="emit('close')">X</button>
    <svg class="success-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#333" stroke-width="5" /><path d="M32 51 L45 68 L67 33" fill="none" stroke="#444" stroke-width="9" stroke-linecap="round" stroke-linejoin="round" /></svg>
    <h2 id="signup-success-title">회원가입이 완료되었습니다.</h2>
    <p id="signup-success-description">로그인을 진행해주세요.</p>
    <button ref="loginButton" class="dialog-login" type="button" @click="emit('login')">로그인</button>
  </dialog>
</template>
<style scoped>
.success-dialog { width: 650px; max-width: calc(100vw - 32px); max-height: calc(100svh - 32px); overflow-y: auto; height: 500px; padding: 42px 40px 50px; border: 0; border-radius: 28px; background: #fff; color: #000; text-align: center; }
.success-dialog::backdrop { background: rgba(67, 84, 105, .4); }
.dialog-close { position: absolute; top: 22px; right: 46px; padding: 4px 6px; border: 0; background: transparent; color: #858585; font-size: 20px; }
.success-icon { display: block; width: 100px; height: 100px; margin: 0 auto; }
h2 { margin: 42px 0 58px; font-size: 36px; line-height: 48px; font-weight: 700; }
p { margin: 0; color: #858585; font-size: 20px; font-weight: 600; line-height: 30px; }
.dialog-login { display: block; width: min(400px, 100%); height: 40px; margin: 66px auto 0; border: 0; border-radius: 8px; background: #eee; color: #000; font-size: 16px; }
button:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
.dialog-login:hover { background: #ddd; }
@media (max-width: 600px) { .success-dialog { height: auto; padding: 44px 20px 36px; } .dialog-close { right: 18px; top: 14px; } .success-icon { width: 80px; height: 80px; } h2 { font-size: 25px; line-height: 36px; margin: 32px 0; } p { font-size: 17px; } .dialog-login { margin-top: 44px; } }
</style>

