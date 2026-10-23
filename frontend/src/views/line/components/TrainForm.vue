<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchTrain, saveTrain } from '../services/trainData'
import { apiError } from '../../../services/backendApi'
const route = useRoute()
const router = useRouter()
const editing = computed(() => route.name === 'admin-train-edit')
const existing = ref(null)
const form = reactive({ number: '', operator: '', section: '석남 ~ 까치울', direction: '상행', registered: '', inspection: '', active: true, status: '운행' })
const error = ref('')
onMounted(async () => {
  if (!editing.value) return
  try {
    existing.value = await fetchTrain(route.params.trainId)
    Object.assign(form, existing.value, { number: String(existing.value.number || '').replace(/^7L-/, '') })
  }
  catch (failure) { error.value = apiError(failure) }
})
function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false
  const date = new Date(value + 'T00:00:00Z')
  return !Number.isNaN(date.getTime()) && date.toISOString().slice(0, 10) === value
}
async function submit() {
  error.value = ''
  const number = form.number.trim()
  if (!/^\d{4}$/.test(number)) { error.value = '열차 번호는 7L- 뒤에 숫자 4자리로 입력해 주세요.'; return }
  if (!form.operator.trim()) { error.value = '기관사를 입력해 주세요.'; return }
  if (form.registered && !validDate(form.registered)) { error.value = '등록일을 올바른 날짜로 입력해 주세요.'; return }
  if (form.inspection && !validDate(form.inspection)) { error.value = '점검일을 올바른 날짜로 입력해 주세요.'; return }
  try {
    await saveTrain({ ...form, number: `7L-${number}`, operator: form.operator.trim() }, editing.value ? existing.value.id : undefined)
    router.push('/admin/line7/trains')
  } catch (failure) { error.value = apiError(failure) }
}
</script>
<template>
  <section class="train-editor">
    <template v-if="!editing || existing">
      <h1>{{ editing ? '열차 정보 수정하기' : '열차 등록하기' }}</h1>
      <form id="train-form" class="train-form" novalidate @submit.prevent="submit">
        <div class="train-form-top">
          <label>열차 번호<div class="train-number-control"><span>7L-</span><input v-model="form.number" inputmode="numeric" maxlength="4" placeholder="0001" aria-label="열차 번호 숫자 4자리" @input="form.number = form.number.replace(/\D/g, '').slice(0, 4)" /></div></label>
          <label>기관사<input v-model="form.operator" maxlength="30" placeholder="ex) 김아무개" /></label>
          <label>운행 구간<select v-model="form.section"><option>석남 ~ 까치울</option><option>까치울 ~ 석남</option></select></label>
          <label>상행/하행<select v-model="form.direction"><option>상행</option><option>하행</option></select></label>
        </div>
        <div class="train-form-dates">
          <label>등록일<input v-model="form.registered" type="date" /></label>
          <div><label>점검일<input v-model="form.inspection" type="date" /></label><p class="inspection-note"><span>*</span> 점검은 2년 주기로 필요합니다.</p></div>
        </div>
        <div class="train-form-bottom">
          <label>활성화 여부<select v-model="form.active"><option :value="true">활성화</option><option :value="false">비활성화</option></select></label>
          <label v-if="editing">운행 상태<select v-model="form.status"><option>운행</option><option>점검</option><option>고장</option></select></label>
        </div>
        <p v-if="error" class="train-form-error" role="alert">{{ error }}</p>
      </form>
      <div class="train-editor-actions"><RouterLink class="train-button" to="/admin/line7/trains">목록</RouterLink><div><RouterLink class="train-button" to="/admin/line7/trains">취소</RouterLink><button class="train-button" form="train-form" type="submit">{{ editing ? '수정' : '등록' }}</button></div></div>
    </template>
    <template v-else><h1>열차를 찾을 수 없습니다.</h1><RouterLink class="train-button" to="/admin/line7/trains">목록</RouterLink></template>
  </section>
</template>
<style scoped src="../styles/admin-line.css"></style>
