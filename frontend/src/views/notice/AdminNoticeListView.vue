<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref } from 'vue'
import DeleteModal from '../../components/DeleteModal.vue'

import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getNotices, deleteNotice as removeNotice } from './noticeMockData'
import { api, apiError } from '../../services/backendApi'
import './admin-notice-list.css'

const router = useRouter()
const notices = ref([])
const searchField = ref('all')
const searchText = ref('')
const currentPage = ref(1)
const totalCount = ref(0)
const selectedIds = ref([])
const bulkDeleting = ref(false)
const pageSize = 10
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const visiblePages = computed(() => { const start = Math.min(Math.max(1, currentPage.value - 2), Math.max(1, pageCount.value - 4)); return Array.from({ length: Math.min(5, pageCount.value) }, (_, index) => start + index) })
const filteredNotices = computed(() => notices.value)
async function loadNotices() {
  try { const result = await getNotices({ page: currentPage.value, size: pageSize, admin: true, searchType: ['title', 'writer'].includes(searchField.value) ? searchField.value : undefined, keyword: searchText.value.trim() }); notices.value = result.items; totalCount.value = result.totalCount ?? result.items.length }
  catch (error) { notices.value = []; totalCount.value = 0; console.error(apiError(error)) }
}
onMounted(loadNotices)
function search() { selectedIds.value = []; currentPage.value = 1; loadNotices() }
function goToPage(page) { selectedIds.value = []; currentPage.value = Math.min(Math.max(1, page), pageCount.value); loadNotices() }
const allPageSelected = computed(() => filteredNotices.value.length > 0 && filteredNotices.value.every(notice => selectedIds.value.includes(notice.id)))
function toggleNotice(id) { selectedIds.value = selectedIds.value.includes(id) ? selectedIds.value.filter(value => value !== id) : [...selectedIds.value, id] }
function toggleCurrentPage() {
  const pageIds = filteredNotices.value.map(notice => notice.id)
  selectedIds.value = allPageSelected.value
    ? selectedIds.value.filter(id => !pageIds.includes(id))
    : [...new Set([...selectedIds.value, ...pageIds])]
}
async function deleteSelected() {
  if (!selectedIds.value.length || bulkDeleting.value) return
  const ids = [...selectedIds.value]
  if (!window.confirm(`선택한 공지사항 ${ids.length}건을 삭제하시겠습니까? 첨부파일도 함께 삭제됩니다.`)) return
  bulkDeleting.value = true
  try {
    await api.notices.removeMany(ids)
    totalCount.value = Math.max(0, totalCount.value - ids.length)
    selectedIds.value = []
    if (filteredNotices.value.length === ids.length && currentPage.value > 1) currentPage.value -= 1
    await loadNotices()
  } catch (error) { window.alert(apiError(error)) }
  finally { bulkDeleting.value = false }
}

function editNotice(notice) {
  router.push({ path: '/admin/notices/edit', query: { id: notice.id } })
}

const deleteStage = ref('')
const deletingId = ref(null)
function deleteNotice(noticeId) {
  deletingId.value = noticeId
  deleteStage.value = 'confirm'
}
async function confirmDelete() {
  if (deleteStage.value === 'complete') { closeDelete(); return }
  try { await removeNotice(deletingId.value); notices.value = notices.value.filter(notice => String(notice.id) !== String(deletingId.value)); selectedIds.value = selectedIds.value.filter(id => String(id) !== String(deletingId.value)); totalCount.value = Math.max(0, totalCount.value - 1); deleteStage.value = 'complete' }
  catch (error) { deleteStage.value = ''; console.error(apiError(error)) }
}
function closeDelete() {
  deleteStage.value = ''
  deletingId.value = null
}
</script>

<template>
  <div class="admin-notice-list-page">
    <PageHeader />

    <main class="admin-notice-list-main">
      <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하고 관리할 수 있습니다."  />

      <div class="admin-notice-list-layout">

        <section class="admin-notice-board" aria-label="공지사항 관리 목록">
          <form class="admin-notice-search" @submit.prevent="search">
            <label class="visually-hidden" for="admin-notice-search-field">검색 범위</label>
            <select id="admin-notice-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="content">내용</option>
              <option value="writer">작성자</option>
            </select>

            <label class="visually-hidden" for="admin-notice-search-text">검색어</label>
            <div class="admin-notice-search__input-wrap">
              <input id="admin-notice-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <SearchIcon />
            </div>

            <button class="admin-notice-search__button" type="submit">검색</button>
            <RouterLink class="admin-notice-register-button" to="/notices/create">⊕ 등록하기</RouterLink>
          </form>

          <div class="admin-notice-list-controls">
            <p class="admin-notice-total-count">전체 {{ totalCount }}건</p>
            <div v-if="selectedIds.length" class="admin-notice-bulk-actions">
              <span>{{ selectedIds.length }}건 선택</span>
              <button type="button" :disabled="bulkDeleting" @click="deleteSelected">선택 삭제</button>
            </div>
          </div>

          <div class="admin-notice-table-scroll">
            <table class="admin-notice-table">
              <thead>
                <tr>
                  <th class="admin-notice-col-select"><input type="checkbox" :checked="allPageSelected" aria-label="현재 페이지 공지사항 전체 선택" @change="toggleCurrentPage" /></th>
                  <th class="admin-notice-col-number">번호</th>
                  <th class="admin-notice-col-title">제목</th>
                  <th class="admin-notice-col-writer">작성자</th>
                  <th class="admin-notice-col-views">조회수</th>
                  <th class="admin-notice-col-date">등록일</th>
                  <th class="admin-notice-col-actions">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="notice in filteredNotices" :key="notice.id">
                  <td class="admin-notice-select-cell"><input type="checkbox" :checked="selectedIds.includes(notice.id)" :aria-label="`${notice.title} 선택`" @change="toggleNotice(notice.id)" /></td>
                  <td>{{ notice.id }}</td>
                  <td class="admin-notice-table__title">
                    <RouterLink class="admin-notice-title-link" :to="{ path: '/admin/notices/detail', query: { id: notice.id } }">{{ notice.title }}</RouterLink>
                  </td>
                  <td>{{ notice.writer }}</td>
                  <td>{{ notice.views }}</td>
                  <td>{{ notice.date }}</td>
                  <td class="admin-notice-table__actions">
                    <button class="admin-notice-row-button admin-notice-row-button--edit" type="button" @click="editNotice(notice)">수정</button>
                    <button class="admin-notice-row-button admin-notice-row-button--delete" type="button" @click="deleteNotice(notice.id)">삭제</button>
                  </td>
                </tr>
                <tr v-if="filteredNotices.length === 0">
                  <td colspan="7" class="admin-notice-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-notice-pagination" aria-label="공지사항 페이지">
            <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="goToPage(1)">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">‹</button>
            <button v-for="page in visiblePages" :key="page" type="button" :class="{ 'is-current': page === currentPage }" @click="goToPage(page)">
              {{ page }}
            </button>
            <button type="button" aria-label="다음 페이지" :disabled="currentPage === pageCount" @click="goToPage(currentPage + 1)">›</button>
            <button type="button" aria-label="마지막 페이지" :disabled="currentPage === pageCount" @click="goToPage(pageCount)">»</button>
          </nav>
        </section>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>

