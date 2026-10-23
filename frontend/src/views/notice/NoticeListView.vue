<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref, watch } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-list.css'
import { getNotices } from './noticeMockData'
import { apiError } from '../../services/backendApi'

const notices = ref([])

const searchField = ref('all')
const searchInput = ref('')
const appliedQuery = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)
const totalPages = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const pageRows = computed(() => notices.value)
const visiblePages = computed(() => { const start = Math.min(Math.max(1, currentPage.value - 2), Math.max(1, totalPages.value - 4)); return Array.from({ length: Math.min(5, totalPages.value) }, (_, index) => start + index) })
async function loadNotices() {
  try {
    const result = await getNotices({ page: currentPage.value, size: pageSize, searchType: ['title', 'writer'].includes(searchField.value) ? searchField.value : undefined, keyword: appliedQuery.value })
    notices.value = result.items
    totalCount.value = result.totalCount ?? result.items.length
  } catch (error) { notices.value = []; totalCount.value = 0; console.error(apiError(error)) }
}
onMounted(loadNotices)
watch(currentPage, loadNotices)

function searchNotices() {
  appliedQuery.value = searchInput.value.trim()
  currentPage.value = 1
  loadNotices()
}

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), totalPages.value)
}
</script>

<template>
  <div class="notice-page">
    <PageHeader />

    <main class="notice-main">
      <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하실 수 있습니다."  />

      <div class="notice-content-grid">

        <section class="notice-board" aria-label="공지사항 목록">
          <form class="notice-search" @submit.prevent="searchNotices">
            <label class="visually-hidden" for="notice-search-field">검색 범위</label>
            <select id="notice-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="content">내용</option>
              <option value="writer">작성자</option>
            </select>
            <label class="visually-hidden" for="notice-search-input">검색어</label>
            <div class="notice-search__input-wrap">
              <input
                id="notice-search-input"
                v-model="searchInput"
                type="search"
                placeholder="제목 또는 내용을 검색하세요"
              />
              <SearchIcon />
            </div>
            <button class="notice-search__button" type="submit">검색</button>
          </form>

          <p class="notice-total-count">전체 {{ totalCount }}건</p>

          <div class="notice-table-scroll">
            <table class="notice-table">
              <thead>
                <tr>
                  <th class="notice-col-number">번호</th>
                  <th class="notice-col-title">제목</th>
                  <th class="notice-col-writer">작성자</th>
                  <th class="notice-col-views">조회수</th>
                  <th class="notice-col-date">등록일</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="notice in pageRows" :key="notice.id">
                  <td>{{ notice.id }}</td>
                  <td class="notice-table__title">
                    <RouterLink class="notice-table__title-link" :to="{ path: '/notice/detail', query: { id: notice.id } }">{{ notice.title }}</RouterLink>
                  </td>
                  <td>{{ notice.writer }}</td>
                  <td>{{ notice.views }}</td>
                  <td>{{ notice.date }}</td>
                </tr>
                <tr v-if="pageRows.length === 0">
                  <td colspan="5" class="notice-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="notice-board__bottom">
            <nav class="notice-pagination" aria-label="공지사항 페이지">
              <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="goToPage(1)">«</button>
              <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">‹</button>
              <button
                v-for="page in visiblePages"
                :key="page"
                type="button"
                :class="{ 'is-current': currentPage === page }"
                :aria-current="currentPage === page ? 'page' : undefined"
                @click="goToPage(page)"
              >{{ page }}</button>
              <button type="button" aria-label="다음 페이지" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">›</button>
              <button type="button" aria-label="마지막 페이지" :disabled="currentPage === totalPages" @click="goToPage(totalPages)">»</button>
            </nav>
          </div>
        </section>
      </div>
    </main>

    <PageFooter />
  </div>
</template>

