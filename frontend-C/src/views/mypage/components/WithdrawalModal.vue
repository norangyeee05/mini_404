<script setup>
import { onMounted, ref } from 'vue'
defineProps({ completed: Boolean })
const emit = defineEmits(['confirm', 'close'])
const dialog = ref(null)
const confirmButton = ref(null)
onMounted(() => { dialog.value.showModal(); confirmButton.value.focus() })
</script>
<template>
  <dialog ref="dialog" class="permission-dialog" aria-labelledby="permission-title" aria-describedby="permission-description" @cancel.prevent="emit('close')">
    <button class="permission-close" type="button" aria-label="탈퇴 안내 닫기" @click="emit('close')">X</button>
    <svg v-if="!completed" class="permission-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#ff4b59" stroke-width="5" /><path d="M50 32 V57" stroke="#ff6671" stroke-width="6" /><circle cx="50" cy="65" r="3.5" fill="#ff6671" /></svg><svg v-else class="permission-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#333" stroke-width="5" /><path d="M32 51 L45 68 L67 33" fill="none" stroke="#444" stroke-width="9" stroke-linecap="round" stroke-linejoin="round" /></svg>
    <h2 id="permission-title">{{ completed ? '탈퇴되었습니다.' : '탈퇴하시겠습니까?' }}</h2>
    <p id="permission-description">해당 페이지에 접근 {{ completed ? '탈퇴되었습니다.' : '탈퇴하시겠습니까?' }}<br />로그인하여 주시길 바랍니다.</p>
    <button ref="confirmButton" class="permission-confirm" type="button" @click="emit('confirm')">확인</button>
  </dialog>
</template>
<style scoped src="../styles/withdrawal-modal.css"></style>

