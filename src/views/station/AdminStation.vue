<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import SearchIcon from '../../components/SearchIcon.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import TrainSidebar from '../line/components/TrainSidebar.vue'
import StationAdminForm from './components/StationAdminForm.vue'
import { adminStations as trains, deleteStation as deleteTrain, fetchAdminStations } from './services/adminStationData'
import { apiError } from '../../services/backendApi'
const route = useRoute()
const isList = computed(() => route.name === 'admin-stations')
const field = ref('all')
const keyword = ref('')
const applied = ref({ field: 'all', keyword: '' })
const page = ref(1)
const feedback = ref('')
const deleting = ref(null)
const deleteComplete = ref(false)
const totalCount = ref(0)
const filtered = computed(() => ({ length: totalCount.value }))
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / 10)))
const rows = computed(() => trains.value)
async function loadStations() {
  try { const result = await fetchAdminStations({ page: page.value, size: 10, ...(applied.value.keyword ? { keyword: applied.value.keyword } : {}), ...(applied.value.keyword && applied.value.field === 'name' ? { searchType: 'stationName' } : {}) }); totalCount.value = result.totalCount ?? result.items.length }
  catch (error) { trains.value = []; totalCount.value = 0; feedback.value = apiError(error) }
}
onMounted(loadStations)
const pages = computed(() => Array.from({ length: Math.min(5, pageCount.value) }, (_, index) => Math.min(Math.max(1, page.value - 2), Math.max(1, pageCount.value - 4)) + index))
watch(pageCount, count => { page.value = Math.min(page.value, count) })
watch(page, loadStations)
function search() { applied.value = { field: field.value, keyword: keyword.value }; page.value = 1; loadStations() }
function reset() { field.value = 'all'; keyword.value = ''; search() }
async function confirmDelete() {
  if (deleteComplete.value) { deleting.value = null; deleteComplete.value = false; return }
  try { await deleteTrain(deleting.value); await loadStations(); deleteComplete.value = true }
  catch (error) { feedback.value = apiError(error); deleting.value = null }
}
</script>
<template>
  <div class="train-shell">
    <PageHeader />
    <main class="train-layout">
      <TrainSidebar />
      <section v-if="isList" class="train-list" aria-label="역 관리">
        <div class="train-toolbar">
          <form class="train-search" @submit.prevent="search">
            <select v-model="field" aria-label="역 검색 범위"><option value="all">전체</option><option value="number">역 번호</option><option value="staff">역무원</option><option value="status">시설 상태</option><option value="name">역 이름</option></select>
            <div class="train-search-input"><input v-model="keyword" aria-label="역 검색어" placeholder="제목 또는 내용을 검색하세요" /><SearchIcon /></div>
            <button class="train-button" type="submit">검색</button>
            <button class="train-button train-button--reset" type="button" @click="reset">초기화</button>
          </form>
          <RouterLink class="train-button train-button--new" to="/admin/line7/stations/new">⊕ 등록하기</RouterLink>
          <p class="train-count">전체 역 {{ filtered.length }}개</p>
          <p v-if="feedback" role="alert">{{ feedback }}</p>
        </div>
        <div class="train-table-scroll">
          <table class="train-table">
            <thead><tr><th>시설 상태</th><th>역 이름</th><th>역 번호</th><th>역무원</th><th>최근 점검 일시</th><th>활성화 여부</th><th>관리</th></tr></thead>
            <tbody>
              <tr v-for="row in rows" :key="row.id">
                <td><span class="train-pill" :class="{ running: row.status === '양호', inspection: row.status === '점검 중', broken: row.status === '필요' }">{{ row.status }}</span></td>
                <td>{{ row.name }}</td><td><RouterLink :to="'/admin/line7/stations/' + row.id">{{ row.number }}</RouterLink></td><td>{{ row.staff }}</td><td>{{ row.updated }}</td>
                <td>{{ row.active ? '활성화' : '비활성화' }}</td>
                <td><div class="train-row-actions"><RouterLink class="train-button train-button--edit" :to="'/admin/line7/stations/' + row.id">수정</RouterLink><button class="train-button train-button--delete" type="button" @click="deleting = row.id; deleteComplete = false">삭제</button></div></td>
              </tr>
              <tr v-if="!rows.length"><td colspan="7">검색 결과가 없습니다.</td></tr>
            </tbody>
          </table>
        </div>
        <div class="train-list-footer">
          <nav class="train-pagination" aria-label="역 목록 페이지">
            <button :disabled="page === 1" @click="page = 1" aria-label="첫 페이지">«</button><button :disabled="page === 1" @click="page--" aria-label="이전 페이지">‹</button>
            <button v-for="number in pages" :key="number" :class="{ current: page === number }" :aria-current="page === number ? 'page' : undefined" @click="page = number">{{ number }}</button>
            <button :disabled="page === pageCount" @click="page++" aria-label="다음 페이지">›</button><button :disabled="page === pageCount" @click="page = pageCount" aria-label="마지막 페이지">»</button>
          </nav>
          <span>총 페이지 수 : {{ pageCount }}</span>
        </div>
      </section>
      <StationAdminForm v-else :key="route.fullPath" />
    </main>
    <PageFooter />
    <DeleteModal v-if="deleting" action-label="비활성화" :completed="deleteComplete" @confirm="confirmDelete" @close="deleting = null; deleteComplete = false" />
  </div>
</template>
<style scoped src="../line/styles/admin-line.css"></style>


