<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { deleteResource as requestDeleteResource, getResources } from '../../api/resources'
import { errorMessage } from '../../api/client'
import { normalizeResource } from '../../api/normalize'
import './admin-resource-list.css'

const props = defineProps({
  role: { type: String, default: 'admin' },
})

const router = useRouter()
const resources = ref([])
const searchType = ref('all')
const searchText = ref('')
const currentPage = ref(1)
const pageSize = 10
const message = ref('')
const totalCount = ref(0)
const loading = ref(false)

const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const visibleResources = computed(() => resources.value)

async function loadResources() {
  loading.value = true
  message.value = ''
  try {
    const result = await getResources({ page: currentPage.value, size: pageSize, searchType: searchType.value, keyword: searchText.value.trim() })
    resources.value = result.items.map(normalizeResource)
    totalCount.value = result.totalCount
    currentPage.value = result.page
  } catch (error) { message.value = errorMessage(error, '자료실 목록을 불러오지 못했습니다.') }
  finally { loading.value = false }
}
function search() { currentPage.value = 1; loadResources() }
function goToPage(page) { currentPage.value = Math.min(Math.max(page, 1), pageCount.value); loadResources() }
function openDetail(resource) {
  const path = props.role === 'admin' ? `/admin/resources/${resource.id}` : `/resources/${resource.id}`
  router.push(path)
}
function editResource(resource) { router.push({ path: '/admin/resources/new', query: { id: resource.id } }) }
function requestResource() { router.push('/resources/request') }
async function deleteResource(resource) {
  if (!window.confirm('이 자료를 삭제하시겠습니까?')) return
  try { await requestDeleteResource(resource.id); await loadResources() }
  catch (error) { message.value = errorMessage(error, '자료를 삭제하지 못했습니다.') }
}
onMounted(loadResources)
</script>

<template>
  <div class="admin-resource-list-page">
    <PageHeader :role="props.role" />
    <main class="admin-resource-list-main">
      <section class="admin-resource-list-hero">
        <div class="admin-resource-list-breadcrumb">HOME <span>›</span> 자료실</div>
        <h1>자료실</h1>
        <p>교통서비스 관련 자료를 확인하고 관리할 수 있습니다.</p>
      </section>

      <div class="admin-resource-list-layout">
        <aside class="admin-resource-list-sidebar" aria-label="자료실 메뉴">
          <a class="admin-resource-list-sidebar__link is-active" href="/admin/resources">목록</a>
        </aside>

        <section class="admin-resource-list-board" aria-label="자료실 목록">
          <form class="admin-resource-list-search" @submit.prevent="search">
            <label class="visually-hidden" for="resource-search-type">검색 범위</label>
            <select id="resource-search-type" v-model="searchType">
              <option value="all">전체</option><option value="title">제목</option><option value="writer">작성자</option>
            </select>
            <label class="visually-hidden" for="resource-search-text">검색어</label>
            <div class="admin-resource-list-search__input">
              <input id="resource-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <span aria-hidden="true">⌕</span>
            </div>
            <button class="admin-resource-list-search__button" type="submit">검색</button>
            <a v-if="props.role === 'admin'" class="admin-resource-list-register" href="/admin/resources/create">⊕ 등록하기</a>
            <button v-else class="admin-resource-list-register" type="button" @click="requestResource">⊕ 문의/요청</button>
          </form>

          <p v-if="message" class="admin-resource-list-message" role="status">{{ message }}</p>

          <p v-if="loading" role="status">불러오는 중...</p>
          <div class="admin-resource-list-table-wrap">
            <table class="admin-resource-list-table">
              <thead><tr><th>번호</th><th class="resource-title-col">제목</th><th>작성자</th><th>조회수</th><th>등록일</th><th v-if="props.role === 'admin'">관리</th></tr></thead>
              <tbody>
                <tr v-for="resource in visibleResources" :key="resource.id">
                  <td>{{ resource.id }}</td>
                  <td class="admin-resource-list-title"><button type="button" @click="openDetail(resource)">{{ resource.title }}</button></td>
                  <td>{{ resource.writer }}</td><td>{{ resource.views }}</td><td>{{ resource.date }}</td>
                  <td v-if="props.role === 'admin'" class="admin-resource-list-actions">
                    <button class="resource-edit-button" type="button" @click="editResource(resource)">수정</button>
                    <button class="resource-delete-button" type="button" @click="deleteResource(resource)">삭제</button>
                  </td>
                </tr>
                <tr v-if="visibleResources.length === 0"><td :colspan="props.role === 'admin' ? 6 : 5" class="admin-resource-list-empty">검색 결과가 없습니다.</td></tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-resource-list-pagination" aria-label="자료실 페이지">
            <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="goToPage(1)">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">‹</button>
            <button v-for="page in pageCount" :key="page" type="button" :class="{ 'is-current': page === currentPage }" @click="goToPage(page)">{{ page }}</button>
            <button type="button" aria-label="다음 페이지" :disabled="currentPage === pageCount" @click="goToPage(currentPage + 1)">›</button>
            <button type="button" aria-label="마지막 페이지" :disabled="currentPage === pageCount" @click="goToPage(pageCount)">»</button>
          </nav>
        </section>
      </div>
    </main>
    <PageFooter />
  </div>
</template>
