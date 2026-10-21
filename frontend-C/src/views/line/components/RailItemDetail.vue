<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { fetchTrain } from '../services/trainData'
import { fetchAdminStation } from '../../station/services/adminStationData'
import { apiError } from '../../../services/backendApi'
import { currentMember } from '../../../services/auth'
import { formatDate } from '../../../services/apiMappers'

const props = defineProps({
  kind: { type: String, required: true, validator: value => ['train', 'station'].includes(value) },
  id: { type: [String, Number], required: true },
})
const record = ref(null)
const error = ref('')
const isTrain = computed(() => props.kind === 'train')
const canEdit = computed(() => currentMember.value?.role === 'ADMIN')
const title = computed(() => isTrain.value ? '열차 정보 상세보기' : '역 정보 상세보기')
const listPath = computed(() => `/admin/line7/${isTrain.value ? 'trains' : 'stations'}`)
const editPath = computed(() => `${listPath.value}/${props.id}`)
const sections = computed(() => {
  const item = record.value
  if (!item) return []
  if (isTrain.value) return [
    { title: '열차 정보', fields: [['열차 번호', item.number], ['운행 상태', item.status], ['기관사', item.operator], ['활성화 여부', item.active ? '활성화' : '비활성화']] },
    { title: '운행 정보', fields: [['운행 구간', item.section], ['상행/하행', item.direction]] },
    { title: '등록 및 점검', fields: [['등록일', formatDate(item.registered) || '—'], ['점검일', formatDate(item.inspection) || '—'], ['최근 수정일', item.updated || '—']] },
  ]
  return [
    { title: '역 정보', fields: [['역 이름', item.name], ['영문 역 이름', item.nameEn || '—'], ['역 번호', item.number], ['역무원', item.staff], ['시설 상태', item.status || '—'], ['활성화 여부', item.active ? '활성화' : '비활성화']] },
    { title: '위치 및 연락처', fields: [['주소', item.address || '—'], ['연락처', item.phone || '—']], coordinates: true },
    { title: '운영 및 점검', fields: [['등록일', formatDate(item.registeredAt) || '—'], ['최근 점검일', formatDate(item.inspection) || '—'], ['다음 점검일', formatDate(item.nextInspectionDueDate) || '—'], ['이용 정보', item.usageInfo || '—'], ['편의 시설', item.facilities || '—'], ['환승 노선', item.transferLine || '—'], ['환승 안내', item.transferInfo || '—']] },
  ]
})
onMounted(async () => {
  try { record.value = isTrain.value ? await fetchTrain(props.id) : await fetchAdminStation(props.id) }
  catch (failure) { error.value = apiError(failure) }
})
</script>

<template>
  <section class="rail-item-detail train-list" :aria-label="title">
    <div class="rail-item-detail__heading">
      <h1>{{ title }}</h1>
    </div>
    <p v-if="error" class="rail-item-detail__message" role="alert">{{ error }}</p>
    <p v-else-if="!record" class="rail-item-detail__message" aria-live="polite">정보를 불러오는 중입니다.</p>
    <div v-else class="rail-item-detail__sections">
      <section v-for="section in sections" :key="section.title" class="rail-item-detail__card">
        <h2>{{ section.title }}</h2>
        <dl class="rail-item-detail__grid">
          <div v-for="([label, value]) in section.fields" :key="label" class="rail-item-detail__field">
            <dt>{{ label }}</dt><dd>{{ value || '—' }}</dd>
          </div>
          <div v-if="section.coordinates" class="rail-item-detail__coordinates">
            <div class="rail-item-detail__field"><dt>위도</dt><dd>{{ record.latitude ?? '—' }}</dd></div>
            <div class="rail-item-detail__field"><dt>경도</dt><dd>{{ record.longitude ?? '—' }}</dd></div>
          </div>
        </dl>
      </section>
    </div>
    <div class="rail-item-detail__actions">
      <RouterLink class="train-button rail-item-detail__button" :to="listPath">목록</RouterLink>
      <RouterLink v-if="canEdit" class="train-button rail-item-detail__button" :to="editPath">정보 수정</RouterLink>
    </div>
  </section>
</template>

<style scoped>
.rail-item-detail { padding: 34px; gap: 20px; }
.rail-item-detail__heading h1 { margin: 0; padding-left: 14px; border-left: 4px solid #155369; color: #155369; font-size: 30px; line-height: 1.35; }
.rail-item-detail__heading p { margin: 12px 0 0 18px; color: #647b86; }
.rail-item-detail__message { margin: 0; padding: 24px; color: #467486; }
.rail-item-detail__sections { display: grid; gap: 16px; }
.rail-item-detail__card { margin: 0; padding: 20px 24px; border-radius: 12px; background: #fff; box-shadow: 0 5px 18px rgb(27 67 83 / 6%); }
.rail-item-detail__card h2 { margin: 0 0 8px; color: #155369; font-size: 18px; }
.rail-item-detail__grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0 28px; margin: 0; }
.rail-item-detail__field { display: grid; grid-template-columns: minmax(110px, .8fr) minmax(0, 1.5fr); align-items: start; gap: 12px; padding: 15px 4px; border-bottom: 1px solid #e5edef; }
.rail-item-detail__field dt { color: #467486; font-weight: 700; }
.rail-item-detail__field dd { margin: 0; color: #667781; overflow-wrap: anywhere; }
.rail-item-detail__coordinates { grid-column: 1 / -1; display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 0 28px; }
.rail-item-detail__actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: auto; }
.rail-item-detail__button { display: inline-flex; align-items: center; justify-content: center; min-width: 88px; min-height: 44px; padding: 10px 20px; border: 0; border-radius: 9px; color: #fff; font: inherit; font-weight: 700; text-decoration: none; cursor: pointer; transition: filter .15s ease, transform .15s ease; }
.rail-item-detail__button { background: #467486; }
.rail-item-detail__button:hover { filter: brightness(.94); transform: translateY(-1px); }
.rail-item-detail__button:focus-visible { outline: 3px solid #8abac7; outline-offset: 2px; }
@media (max-width: 760px) { .rail-item-detail { padding: 24px 16px; } .rail-item-detail__card { padding: 14px 16px; } .rail-item-detail__grid, .rail-item-detail__coordinates { grid-template-columns: 1fr; } .rail-item-detail__coordinates { grid-column: auto; } .rail-item-detail__field { grid-template-columns: minmax(100px, .7fr) minmax(0, 1.3fr); } }
</style>
