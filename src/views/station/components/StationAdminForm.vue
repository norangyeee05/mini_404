<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StationTagPicker from './StationTagPicker.vue'
import { adminStations, fetchAdminStation, saveStation, usageOptions, facilityOptions } from '../services/adminStationData'
import { apiError } from '../../../services/backendApi'
const route = useRoute()
const router = useRouter()
const editing = computed(() => route.name === 'admin-station-edit')
const existing = ref(null)
const form = reactive({ name: '', english: '', number: '', staff: '', address: '', phone: '', active: true, status: '양호', registered: '', inspection: '', usage: [], facilities: [], transferLine: '', transferText: '', stationOrder: null, nextInspectionDueDate: null })
const error = ref('')
watch(() => form.status, status => {
  if (!editing.value || !existing.value) return
  adminStations.value = adminStations.value.map(station => station.id === String(existing.value.id) ? { ...station, status } : station)
})
onMounted(async () => {
  if (!editing.value) return
  try {
    existing.value = await fetchAdminStation(route.params.stationId)
    Object.assign(form, { name: existing.value.name, staff: existing.value.staff, phone: existing.value.phone || '', address: existing.value.address || '', active: existing.value.active, status: existing.value.status, inspection: existing.value.inspectionDate || '', stationOrder: existing.value.stationOrder, nextInspectionDueDate: existing.value.nextInspectionDueDate, usage: existing.value.usageInfo ? [existing.value.usageInfo] : [] })
  } catch (failure) { error.value = apiError(failure) }
})
function date(value) {
  const normalized = value.trim().replaceAll('.', '-')
  if (!/^\d{4}-\d{2}-\d{2}$/.test(normalized)) return null
  const parsed = new Date(normalized + 'T00:00:00Z')
  return !Number.isNaN(parsed.getTime()) && parsed.toISOString().slice(0,10) === normalized ? normalized : null
}
async function submit() {
  error.value = ''
  if (!form.name.trim() || !form.staff.trim() || !form.address.trim()) { error.value = '역 이름, 역무원, 주소를 입력해 주세요.'; return }
  if (form.phone && !/^0\d{1,2}-\d{3,4}-\d{4}$/.test(form.phone.trim())) { error.value = '연락처를 032-000-0000 형식으로 입력해 주세요.'; return }
  try {
    const values = { ...form, inspectionDate: form.status === '점검 중' ? date(form.inspection) : null, facilityStatus: form.status }
    for (const field of ['name','english','number','staff','address','phone','transferText']) values[field] = values[field].trim()
    await saveStation(values, editing.value ? existing.value.id : undefined)
    router.push('/admin/line7/stations')
  } catch (failure) { error.value = apiError(failure) }
}
</script>
<template>
  <section class="station-admin-editor">
    <template v-if="!editing || existing">
      <h1>{{ editing ? '역 수정하기' : '역 등록하기' }}</h1>
      <form id="station-admin-form" novalidate @submit.prevent="submit">
        <div class="station-editor-grid">
          <div class="station-base-panel">
            <div class="station-name-row"><label for="station-name">역 이름</label><input id="station-name" v-model="form.name" placeholder="ex) 석남" maxlength="40" /><input v-model="form.english" aria-label="역 이름 영문" placeholder="ex) Seongnam" maxlength="80" /><label for="station-number">역 번호</label><input id="station-number" v-model="form.number" placeholder="ex) 1761" maxlength="4" inputmode="numeric" /></div>
            <div class="station-staff-row"><label for="station-staff">역무원</label><input id="station-staff" v-model="form.staff" placeholder="ex) 김아무개" maxlength="30" /></div>
            <h2>역 기본 정보</h2>
            <div class="station-address-row"><label for="station-address">주소</label><input id="station-address" v-model="form.address" placeholder="ex) 인천광역시 부평구 길주로 527" /><label for="station-phone">연락처</label><input id="station-phone" v-model="form.phone" placeholder="ex) 032-XXX-XXXX" /></div>
            <div class="station-usage-row"><div><h3>이용정보</h3><StationTagPicker v-model="form.usage" :options="usageOptions" label="이용정보" /></div><label class="station-active">활성화 여부<select v-model="form.active"><option :value="true">활성화</option><option :value="false">비활성화</option></select><template v-if="editing">시설 상태<select v-model="form.status"><option>양호</option><option>점검 중</option><option>필요</option></select></template></label></div>
            <div class="station-date-row"><label>등록일<input v-model="form.registered" placeholder="ex) 2026.10.06" /></label><label>점검일<input v-model="form.inspection" placeholder="ex) 2028.10.05" /><span class="station-inspection-note"><b>*</b> 점검은 2년 주기로 필요합니다.</span></label></div>
          </div>
          <aside class="station-extra-panel">
            <label>빠른 환승 정보<select v-model="form.transferLine"><option value="">호선 선택</option><option>인천 1호선</option><option>인천 2호선</option><option>서해선</option><option>7호선</option></select></label>
            <textarea v-model="form.transferText" aria-label="빠른 환승 안내" placeholder="ex) 석남 방면&#10;→ 인천 1호선 5-2" maxlength="500"></textarea>
            <h3>편의 시설</h3><StationTagPicker v-model="form.facilities" :options="facilityOptions" label="편의시설" />
          </aside>
        </div>
        <p v-if="error" class="station-form-error" role="alert">{{ error }}</p>
      </form>
      <div class="station-editor-actions"><RouterLink class="station-admin-button" to="/admin/line7/stations">목록</RouterLink><div><RouterLink class="station-admin-button" to="/admin/line7/stations">취소</RouterLink><button class="station-admin-button" type="submit" form="station-admin-form">{{ editing ? '수정' : '등록' }}</button></div></div>
    </template>
    <template v-else><h1>역을 찾을 수 없습니다.</h1><RouterLink class="station-admin-button" to="/admin/line7/stations">목록</RouterLink></template>
  </section>
</template>
<style scoped src="../styles/admin-station.css"></style>

