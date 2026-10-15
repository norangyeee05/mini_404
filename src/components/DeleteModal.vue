<script setup>
import { onMounted, ref } from 'vue'
defineProps({ completed: Boolean })
const emit = defineEmits(['confirm', 'close'])
const dialog = ref(null)
const confirmButton = ref(null)
onMounted(() => { dialog.value.showModal(); confirmButton.value.focus() })
</script>
<template>
  <dialog ref="dialog" class="permission-dialog" :class="{ 'permission-dialog--complete': completed }" aria-labelledby="permission-title" :aria-describedby="completed ? undefined : 'permission-description'" @cancel.prevent="emit('close')">
    <button class="permission-close" type="button" aria-label="삭제 팝업 닫기" @click="emit('close')">X</button>
    <svg v-if="!completed" class="permission-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#ff4b59" stroke-width="5" /><path d="M50 32 V57" stroke="#ff6671" stroke-width="6" /><circle cx="50" cy="65" r="3.5" fill="#ff6671" /></svg><svg v-else class="permission-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#333" stroke-width="5" /><path d="M32 51 L45 68 L67 33" fill="none" stroke="#444" stroke-width="9" stroke-linecap="round" stroke-linejoin="round" /></svg>
    <h2 id="permission-title">{{ completed ? '삭제되었습니다.' : '삭제하시겠습니까?' }}</h2>
    <p v-if="!completed" id="permission-description">삭제 후 복구가 불가합니다.<br />진짜로 삭제하시겠습니까?</p>
    <button ref="confirmButton" class="permission-confirm" type="button" @click="emit('confirm')">확인</button>
  </dialog>
</template>
<style scoped>
.permission-dialog { width: 650px; max-width: calc(100vw - 32px); height: 500px; max-height: calc(100svh - 32px); overflow-y: auto; padding: 42px 40px 50px; border: 0; border-radius: 28px; background: #fff; color: #000; text-align: center; }
.permission-dialog::backdrop { background: rgba(67, 84, 105, .4); }
.permission-close { position: absolute; top: 22px; right: 46px; padding: 4px 6px; border: 0; background: transparent; color: #858585; font-size: 20px; }
.permission-icon { display: block; width: 100px; height: 100px; margin: 0 auto; }
h2 { margin: 42px 0 42px; font-size: 36px; font-weight: 700; line-height: 48px; }
p { margin: 0; color: #858585; font-size: 20px; font-weight: 600; line-height: 30px; }
.permission-confirm { display: block; width: min(400px, 100%); height: 40px; margin: 54px auto 0; border: 0; border-radius: 8px; background: #eee; color: #000; font-size: 16px; }
.permission-confirm:hover { background: #ddd; } button:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
.permission-dialog--complete { padding-top: 118px; }.permission-dialog--complete h2 { margin-bottom: 0; }.permission-dialog--complete .permission-confirm { margin-top: 74px; }
@media (max-width: 600px) { .permission-dialog { height: auto; padding: 44px 20px 36px; }.permission-close { top: 14px; right: 18px; }.permission-icon { width: 80px; height: 80px; } h2 { margin: 32px 0; font-size: 28px; line-height: 38px; } p { font-size: 17px; }.permission-confirm { margin-top: 38px; } }
</style>


