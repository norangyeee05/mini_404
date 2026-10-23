<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import SearchIcon from '../../components/SearchIcon.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import TrainSidebar from './components/TrainSidebar.vue'
import TrainForm from './components/TrainForm.vue'
import RailItemDetail from './components/RailItemDetail.vue'
import { trains, deleteTrain, fetchTrains } from './services/trainData'
import { apiError } from '../../services/backendApi'
import { currentMember } from '../../services/auth'
const route = useRoute()
const router = useRouter()
const isList = computed(() => route.name === 'admin-trains')
const isDetail = computed(() => route.name === 'admin-train-detail')
const fullAdmin = computed(() => currentMember.value?.role === 'ADMIN')
const field = ref('all')
const keyword = ref('')
const applied = ref({ field: 'all', keyword: '' })
const page = ref(1)
const feedback = ref('')
const deleting = ref(null)
const deleteComplete = ref(false)
const totalCount = ref(0)
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / 10)))
const rows = computed(() => trains.value)
const filtered = computed(() => ({ length: totalCount.value }))
async function loadTrains() {
  const searchType = { number: 'trainNo', operator: 'driver', section: 'section', status: 'status', direction: 'direction' }[applied.value.field]
  try { const result = await fetchTrains({ page: page.value, size: 10, ...(searchType ? { searchType } : {}), ...(applied.value.keyword ? { keyword: applied.value.keyword } : {}) }); totalCount.value = result.totalCount ?? result.items.length }
  catch (error) { trains.value = []; totalCount.value = 0; feedback.value = apiError(error) }
}
onMounted(loadTrains)
watch(isList, (active, previous) => { if (active && !previous) loadTrains() })
const pages = computed(() => Array.from({ length: Math.min(5, pageCount.value) }, (_, index) => Math.min(Math.max(1, page.value - 2), Math.max(1, pageCount.value - 4)) + index))
watch(pageCount, count => { page.value = Math.min(page.value, count) })
watch(page, loadTrains)
function search() { applied.value = { field: field.value, keyword: keyword.value }; page.value = 1; loadTrains() }
function reset() { field.value = 'all'; keyword.value = ''; search() }
async function confirmDelete() {
  if (deleteComplete.value) { deleting.value = null; deleteComplete.value = false; return }
  try { await deleteTrain(deleting.value); await loadTrains(); deleteComplete.value = true }
  catch (error) { feedback.value = apiError(error); deleting.value = null }
}
</script>
<template>
  <div class="train-shell">
    <PageHeader />
    <main class="train-layout">
      <TrainSidebar />
      <section v-if="isList" class="train-list" aria-label="열차 관리">
        <div class="train-toolbar">
          <form class="train-search" @submit.prevent="search">
            <select v-model="field" aria-label="열차 검색 범위"><option value="all">전체</option><option value="number">열차 번호</option><option value="operator">기관사</option><option value="status">운행 상태</option><option value="section">운행 구간</option></select>
            <div class="train-search-input"><input v-model="keyword" aria-label="열차 검색어" placeholder="제목 또는 내용을 검색하세요" /><SearchIcon /></div>
            <button class="train-button" type="submit">검색</button>
            <button class="train-button train-button--reset" type="button" @click="reset">초기화</button>
          </form>
          <RouterLink v-if="fullAdmin" class="train-button train-button--new" to="/admin/line7/trains/new">⊕ 등록하기</RouterLink>
          <p class="train-count">전체 {{ filtered.length }}건</p>
          <p v-if="feedback" role="alert">{{ feedback }}</p>
        </div>
        <div class="train-table-scroll">
          <table class="train-table">
            <thead><tr><th>운행 상태</th><th>운행 구간</th><th>열차 번호</th><th>기관사</th><th>최근 업데이트 일시</th><th>상행/하행</th><th>활성화 여부</th><th>관리</th></tr></thead>
            <tbody>
              <tr v-for="row in rows" :key="row.id" class="train-row-link" tabindex="0" :aria-label="`${row.number} 열차 상세보기`" @click="router.push(`/admin/line7/trains/${row.id}/detail`)" @keydown.enter.self.prevent="router.push(`/admin/line7/trains/${row.id}/detail`)" @keydown.space.self.prevent="router.push(`/admin/line7/trains/${row.id}/detail`)">
                <td><span class="train-pill" :class="{ running: row.status === '운행', inspection: row.status === '점검', broken: row.status === '고장' }">{{ row.status }}</span></td>
                <td>{{ row.section }}</td><td>{{ row.number }}</td><td>{{ row.operator }}</td><td>{{ row.updated }}</td>
                <td><span class="train-pill" :class="row.direction === '상행' ? 'running' : 'down'">{{ row.direction }}</span></td><td>{{ row.active ? '활성화' : '비활성화' }}</td>
                <td><div v-if="fullAdmin" class="train-row-actions"><RouterLink class="train-button train-button--edit" :to="'/admin/line7/trains/' + row.id" @click.stop>수정</RouterLink><button class="train-button train-button--delete" type="button" @click.stop="deleting = row.id; deleteComplete = false">삭제</button></div><span v-else>조회 전용</span></td>
              </tr>
              <tr v-if="!rows.length"><td colspan="8">검색 결과가 없습니다.</td></tr>
            </tbody>
          </table>
        </div>
        <div class="train-list-footer">
          <nav class="train-pagination" aria-label="열차 목록 페이지">
            <button :disabled="page === 1" @click="page = 1" aria-label="첫 페이지">«</button><button :disabled="page === 1" @click="page--" aria-label="이전 페이지">‹</button>
            <button v-for="number in pages" :key="number" :class="{ current: page === number }" :aria-current="page === number ? 'page' : undefined" @click="page = number">{{ number }}</button>
            <button :disabled="page === pageCount" @click="page++" aria-label="다음 페이지">›</button><button :disabled="page === pageCount" @click="page = pageCount" aria-label="마지막 페이지">»</button>
          </nav>
          <span>총 페이지 수 : {{ pageCount }}</span>
        </div>
      </section>
      <RailItemDetail v-else-if="isDetail" kind="train" :id="route.params.trainId" :key="route.fullPath" />
      <TrainForm v-else :key="route.fullPath" />
    </main>
    <PageFooter />
    <DeleteModal v-if="deleting" :completed="deleteComplete" @confirm="confirmDelete" @close="deleting = null; deleteComplete = false" />
  </div>
</template>
<style scoped src="./styles/admin-line.css"></style>

