<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import PageBanner from '../components/PageBanner.vue'
import DeleteModal from '../components/DeleteModal.vue'
import { resources, searchResources, deleteResource } from '../services/resourceData'
const props = defineProps({ admin: Boolean })
const route = useRoute()
const router = useRouter()
const basePath = computed(() => props.admin ? '/admin/resources' : '/resources')
const deleteStage = ref('')
const deletingId = ref(null)
function requestDelete(id) { deletingId.value = id; deleteStage.value = 'confirm' }
function confirmDelete() {
  if (deleteStage.value === 'complete') { deleteStage.value = ''; router.push(basePath.value); return }
  if (deleteResource(deletingId.value)) deleteStage.value = 'complete'
  else deleteStage.value = ''
}
const detail = computed(() => Boolean(route.params.resourceId))
const resource = computed(() => resources.find(item => String(item.id) === route.params.resourceId))
const field = ref('all')
const keyword = ref('')
const applied = ref({ field: 'all', keyword: '' })
const page = ref(1)
const results = computed(() => searchResources(applied.value.field, applied.value.keyword))
const pageCount = computed(() => Math.max(1, Math.ceil(results.value.length / 10)))
watch(pageCount, () => { page.value = Math.min(page.value, pageCount.value) })
const visiblePages = computed(() => {
  const start = Math.floor((page.value - 1) / 5) * 5 + 1
  return Array.from({ length: Math.min(5, pageCount.value - start + 1) }, (_, index) => start + index)
})
const rows = computed(() => results.value.slice((page.value - 1) * 10, page.value * 10))
function search() { applied.value = { field: field.value, keyword: keyword.value }; page.value = 1 }
function goPage(value) { page.value = Math.min(pageCount.value, Math.max(1, value)) }
watch(() => route.fullPath, () => window.scrollTo({ top: 0 }))
function download() {
  const blob = resource.value.attachment || new Blob([resource.value.content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url; anchor.download = resource.value.attachment?.name || `${resource.value.title}_데모.txt`; anchor.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
</script>
<template>
  <div class="resource-shell" :class="{ 'resource-shell--admin': admin }"><PageHeader /><main class="resource-main">
    <PageBanner title="자료실" description="교통서비스 관련 자료를 확인하실 수 있습니다." :detail="detail" />
    <div v-if="!detail" class="resource-layout">
      <aside class="resource-sidebar"><RouterLink :to="basePath" aria-current="page">목록</RouterLink></aside>
      <section class="resource-list" aria-label="자료 목록">
        <div class="list-toolbar"><form class="resource-search" @submit.prevent="search"><select v-model="field" aria-label="검색 범위"><option value="all">전체</option><option value="title">제목</option><option value="content">내용</option><option value="title-content">제목+내용</option></select><div class="search-input"><input v-model="keyword" placeholder="제목 또는 내용을 검색하세요" aria-label="검색어" /><span aria-hidden="true">🔍</span></div><button class="resource-button" type="submit">검색</button></form><RouterLink class="request-button" :to="admin ? `${basePath}/new` : '/qna/create'">{{ admin ? '⊕ 등록하기' : '⊕ 문의/요청' }}</RouterLink></div>
        <div class="resource-table-scroll"><table><colgroup><col class="resource-col-number" /><col class="resource-col-title" /><col class="resource-col-author" /><col class="resource-col-views" /><col class="resource-col-date" /><col v-if="admin" class="resource-col-manage" /></colgroup><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th><th v-if="admin">관리</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.id }}</td><td><RouterLink :to="`${basePath}/${row.id}`">{{ row.title }}</RouterLink></td><td>{{ row.author }}</td><td>{{ row.views }}</td><td>{{ row.date }}</td><td v-if="admin" class="manage-cell"><RouterLink class="manage-edit" :to="`${basePath}/${row.id}/edit`">수정</RouterLink><button class="manage-delete" @click="requestDelete(row.id)">삭제</button></td></tr><tr v-if="!rows.length"><td :colspan="admin ? 6 : 5">검색 결과가 없습니다.</td></tr></tbody></table></div>
        <nav class="resource-pagination" aria-label="자료실 페이지"><button :disabled="page === 1" aria-label="첫 페이지" @click="goPage(1)">«</button><button :disabled="page === 1" aria-label="이전 페이지" @click="goPage(page - 1)">‹</button><button v-for="number in visiblePages" :key="number" :class="{ active: page === number }" :aria-current="page === number ? 'page' : undefined" @click="goPage(number)">{{ number }}</button><button :disabled="page === pageCount" aria-label="다음 페이지" @click="goPage(page + 1)">›</button><button :disabled="page === pageCount" aria-label="마지막 페이지" @click="goPage(pageCount)">»</button></nav>
      </section>
    </div>
    <template v-else><article class="resource-detail"><template v-if="resource"><h2>{{ resource.title }}</h2><div class="resource-meta"><span>작성자　 {{ resource.author }}</span><span>조회수　 {{ resource.views }}　　 등록일　 {{ resource.date.split(' ')[0] }}</span></div><p class="resource-body">{{ resource.content }}</p><section class="resource-attachment"><h3>첨부파일</h3><div class="attachment-row"><span>{{ resource.attachment?.name || `${resource.title}_데모.txt` }} <small>{{ resource.attachment ? `(${(resource.attachment.size / 1024).toFixed(1)}KB)` : '(더미 자료)' }}</small></span><button class="resource-button" @click="download">↓ 다운로드</button></div></section></template><p v-else>해당 자료를 찾을 수 없습니다.</p></article><div class="detail-actions"><RouterLink class="resource-button back-button" :to="basePath">목록</RouterLink><div v-if="admin && resource" class="detail-manage"><RouterLink class="resource-button" :to="`${basePath}/${resource.id}/edit`">수정</RouterLink><button class="resource-button" @click="requestDelete(resource.id)">삭제</button></div></div></template>
  </main><PageFooter /><DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="deleteStage = ''" /></div>
</template>
<style scoped>
.detail-actions { display: flex; align-items: center; justify-content: space-between; }.detail-manage { display: flex; gap: 16px; margin-top: 16px; }.manage-cell { overflow: visible; }.manage-cell a, .manage-cell button { display: inline-flex; justify-content: center; margin: 0 2px; padding: 4px 12px; border: 0; border-radius: 8px; color: #fff; font-size: 16px; line-height: 20px; }.manage-edit { background: #77b184; }.manage-delete { background: #cf7779; }.resource-shell--admin .resource-col-number { width: 9%; }.resource-shell--admin .resource-col-title { width: 39%; }.resource-shell--admin .resource-col-author { width: 9%; }.resource-shell--admin .resource-col-views { width: 9%; }.resource-shell--admin .resource-col-date { width: 19%; }.resource-shell--admin .resource-col-manage { width: 15%; }.resource-shell--admin table { min-width: 1000px; }

.resource-shell { display: flex; flex-direction: column; width: min(1440px, 100%); min-height: 100svh; margin: 0 auto; padding: 0 34px; background: #e2ecee; }.resource-main { flex: 1; }.resource-layout { display: grid; grid-template-columns: 234px minmax(0, 1fr); gap: 44px; margin-top: 12px; }.resource-sidebar { padding: 17px 8px; border-radius: 20px; background: #bcd1d5; }.resource-sidebar a { display: block; padding: 10px 16px; border-radius: 8px; background: #f7f7f7; color: #858585; font-size: 16px; }.resource-list { min-height: 580px; min-width: 0; padding: 17px 24px 28px; border-radius: 22px; background: #f0f5f7; }.list-toolbar { display: flex; align-items: center; justify-content: space-between; gap: 16px; margin-bottom: 12px; }.resource-search { display: flex; align-items: center; gap: 16px; }.resource-search select { width: 180px; height: 40px; padding: 0 13px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; color: #427486; font: inherit; font-weight: 600; }.search-input { display: flex; align-items: center; width: 307px; height: 40px; padding: 0 10px 0 15px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; }.search-input input { width: 100%; min-width: 0; border: 0; background: transparent; font: inherit; color: #333; }.search-input span { font-size: 22px; }.resource-button, .request-button { display: inline-flex; align-items: center; justify-content: center; min-height: 40px; padding: 8px 24px; border: 0; border-radius: 9px; background: #457384; color: #fff; font-size: 16px; font-weight: 600; white-space: nowrap; }.request-button { background: #5686ce; font-size: 13px; padding: 8px 20px; }.resource-table-scroll { overflow-x: auto; } table { table-layout: fixed; width: 100%; font-size: 16px; color: #858585; }.resource-col-number { width: 9%; }.resource-col-title { width: 43%; }.resource-col-author { width: 13%; }.resource-col-views { width: 12%; }.resource-col-date { width: 23%; } thead { background: #e7eef0; } th { height: 42px; } td { height: 40px; padding: 4px 10px; border: 1px solid #e5edef; } th:nth-child(2) { width: 43%; } td:nth-child(2) { text-align: left; } td { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; } td:nth-child(2) a { display: block; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; } td a:hover { text-decoration: underline; }.resource-pagination { display: flex; justify-content: center; gap: 5px; margin-top: 15px; }.resource-pagination button { min-width: 28px; height: 28px; padding: 0 5px; border: 0; border-radius: 5px; background: #e7eef0; color: #858585; font-size: 24px; }.resource-pagination button:first-child, .resource-pagination button:nth-child(2), .resource-pagination button:nth-last-child(-n+2) { font-size: 13px; }.resource-pagination .active { background: #c6dadd; }.resource-pagination button:disabled { opacity: .5; cursor: default; }
.resource-detail { display: flex; flex-direction: column; min-height: 503px; margin-top: 24px; padding: 20px 18px 40px; border-radius: 22px; background: #f0f5f7; } h2 { margin: 0 10px 3px; font-size: 24px; line-height: 30px; color: #155369; }.resource-meta { display: flex; justify-content: space-between; gap: 20px; padding: 13px 20px; border-radius: 10px; background: #e6eef0; color: #858585; font-weight: 600; }.resource-body { margin: 32px 10px 48px; color: #155369; font-size: 20px; font-weight: 500; line-height: 30px; white-space: pre-line; }.resource-attachment { margin-top: auto; padding: 18px 22px; border-radius: 10px; background: #e6eef0; } h3 { margin: 0 8px 6px; font-size: 16px; color: #155369; }.attachment-row { display: flex; align-items: center; gap: 15px; }.attachment-row > span { flex: 1; min-width: 0; padding: 18px 40px; border-radius: 10px; background: #dbe4e7; color: #427486; font-weight: 600; overflow-wrap: anywhere; }.attachment-row small { color: #858585; font-size: 16px; }.back-button { margin-top: 16px; min-width: 84px; } a:focus-visible, button:focus-visible, input:focus-visible, select:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
@media (max-width: 1150px) { .resource-layout { gap: 20px; grid-template-columns: 180px minmax(0, 1fr); }.list-toolbar { flex-wrap: wrap; }.resource-search { flex: 1; flex-wrap: wrap; }.resource-search select { width: 140px; }.search-input { flex: 1; min-width: 200px; } }
@media (max-width: 760px) { .resource-shell { padding: 0 16px; }.resource-layout { grid-template-columns: 1fr; }.resource-list { padding: 18px 12px; min-height: auto; } table { min-width: 700px; }.resource-meta { flex-direction: column; font-size: 14px; }.resource-body { font-size: 17px; }.attachment-row { align-items: stretch; flex-direction: column; }.attachment-row > span { padding: 14px; }.resource-detail { min-height: auto; }.resource-search { gap: 10px; } }
</style>




