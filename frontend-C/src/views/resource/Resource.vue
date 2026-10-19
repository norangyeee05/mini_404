<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import PageBanner from '../../components/PageBanner.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { resources, fetchResources, fetchResource, deleteResource } from './services/resourceData'
import { api, apiError } from '../../services/backendApi'
const props = defineProps({ admin: Boolean })
const route = useRoute()
const router = useRouter()
const basePath = computed(() => props.admin ? '/admin/resources' : '/resources')
const deleteStage = ref('')
const deletingId = ref(null)
const totalCount = ref(0)
const pageSize = 10
const page = ref(1)
const resource = ref(null)
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const rows = computed(() => resources.value)
const visiblePages = computed(() => { const start = Math.floor((page.value - 1) / 5) * 5 + 1; return Array.from({ length: Math.min(5, pageCount.value - start + 1) }, (_, index) => start + index) })
const detail = computed(() => Boolean(route.params.resourceId))
async function load() {
  try {
    if (detail.value) resource.value = await fetchResource(route.params.resourceId, props.admin)
    else { resource.value = null; const result = await fetchResources({ page: page.value, size: pageSize, searchType: applied.value.field === 'title' ? 'title' : undefined, keyword: applied.value.keyword }, props.admin); totalCount.value = result.totalCount ?? result.items.length }
  } catch (error) { resources.value = []; resource.value = null; totalCount.value = 0; console.error(apiError(error)) }
}
onMounted(load)
watch(() => route.fullPath, () => { window.scrollTo({ top: 0 }); load() })
watch(page, load)
function requestDelete(id) { deletingId.value = id; deleteStage.value = 'confirm' }
async function confirmDelete() {
  if (deleteStage.value === 'complete') { deleteStage.value = ''; router.push(basePath.value); return }
  try { await deleteResource(deletingId.value); totalCount.value = Math.max(0, totalCount.value - 1); deleteStage.value = 'complete' }
  catch (error) { deleteStage.value = ''; window.alert(apiError(error)) }
}
const field = ref('all')
const keyword = ref('')
const applied = ref({ field: 'all', keyword: '' })
function search() { applied.value = { field: field.value, keyword: keyword.value }; page.value = 1; load() }
function goPage(value) { page.value = Math.min(pageCount.value, Math.max(1, value)) }
async function download() {
  const file = resource.value?.attachment
  if (!file?.fileId) { window.alert('다운로드할 첨부파일이 없습니다.'); return }
  try { const blob = await api.resources.download(resource.value.id, file.fileId); const url = URL.createObjectURL(blob); const anchor = document.createElement('a'); anchor.href = url; anchor.download = file.fileName || file.name; anchor.click(); setTimeout(() => URL.revokeObjectURL(url), 1000) }
  catch (error) { window.alert(apiError(error)) }
}
</script>
<template>
  <div class="resource-shell" :class="{ 'resource-shell--admin': admin }"><PageHeader /><main class="resource-main">
    <PageBanner title="자료실" description="교통서비스 관련 자료를 확인하실 수 있습니다." :detail="detail" />
    <div v-if="!detail" class="resource-layout">
      <section class="resource-list" aria-label="자료 목록">
        <div class="list-toolbar"><form class="resource-search" @submit.prevent="search"><select v-model="field" aria-label="검색 범위"><option value="all">전체</option><option value="title">제목</option><option value="content">내용</option><option value="title-content">제목+내용</option></select><div class="search-input"><input v-model="keyword" type="search" placeholder="제목 또는 내용을 검색하세요" aria-label="검색어" /><SearchIcon /></div><button class="resource-button" type="submit">검색</button><RouterLink class="request-button" :to="admin ? '/resources/new' : '/qna/create'">{{ admin ? '⊕ 등록하기' : '⊕ 문의/요청' }}</RouterLink></form></div>
        <div class="resource-table-scroll"><table><colgroup><col class="resource-col-number" /><col class="resource-col-title" /><col class="resource-col-author" /><col class="resource-col-views" /><col class="resource-col-date" /><col v-if="admin" class="resource-col-manage" /></colgroup><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th><th v-if="admin">관리</th></tr></thead><tbody><tr v-for="row in rows" :key="row.id"><td>{{ row.id }}</td><td><RouterLink :to="`${basePath}/${row.id}`">{{ row.title }}</RouterLink></td><td>{{ row.author }}</td><td>{{ row.views }}</td><td>{{ row.date }}</td><td v-if="admin" class="manage-cell"><div class="resource-row-actions"><RouterLink class="manage-edit" :to="`${basePath}/${row.id}/edit`">수정</RouterLink><button class="manage-delete" type="button" @click="requestDelete(row.id)">삭제</button></div></td></tr><tr v-if="!rows.length"><td :colspan="admin ? 6 : 5">검색 결과가 없습니다.</td></tr></tbody></table></div>
        <nav class="resource-pagination" aria-label="자료실 페이지"><button :disabled="page === 1" aria-label="첫 페이지" @click="goPage(1)">«</button><button :disabled="page === 1" aria-label="이전 페이지" @click="goPage(page - 1)">‹</button><button v-for="number in visiblePages" :key="number" :class="{ active: page === number }" :aria-current="page === number ? 'page' : undefined" @click="goPage(number)">{{ number }}</button><button :disabled="page === pageCount" aria-label="다음 페이지" @click="goPage(page + 1)">›</button><button :disabled="page === pageCount" aria-label="마지막 페이지" @click="goPage(pageCount)">»</button></nav>
      </section>
    </div>
    <template v-else><article class="resource-detail"><template v-if="resource"><h2>{{ resource.title }}</h2><div class="resource-meta"><span>작성자　 {{ resource.author }}</span><span>조회수　 {{ resource.views }}　　 등록일　 {{ resource.date.split(' ')[0] }}</span></div><p class="resource-body">{{ resource.content }}</p><section class="resource-attachment"><h3>첨부파일</h3><div class="attachment-row"><span>{{ resource.attachment?.name || '첨부파일이 없습니다.' }} <small v-if="resource.attachment && resource.attachment.size">({{ (resource.attachment.size / 1024).toFixed(1) }}KB)</small></span><button class="resource-button" @click="download">↓ 다운로드</button></div></section></template><p v-else>해당 자료를 찾을 수 없습니다.</p></article><div class="detail-actions"><RouterLink class="resource-button back-button" :to="basePath">목록</RouterLink><div v-if="admin && resource" class="detail-manage"><RouterLink class="resource-button" :to="`${basePath}/${resource.id}/edit`">수정</RouterLink><button class="resource-button" @click="requestDelete(resource.id)">삭제</button></div></div></template>
  </main><PageFooter /><DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="deleteStage = ''" /></div>
</template>
<style scoped src="./styles/resource.css"></style>





