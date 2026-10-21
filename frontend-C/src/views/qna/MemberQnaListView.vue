<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQnas } from './qnaMockData'
import { apiError } from '../../services/backendApi'
import './member-qna-list.css'

const route = useRoute()
const router = useRouter()
const qnas = ref([])
const searchField = ref('all')
const searchText = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const visiblePages = computed(() => { const start = Math.min(Math.max(1, currentPage.value - 2), Math.max(1, pageCount.value - 4)); return Array.from({ length: Math.min(5, pageCount.value) }, (_, index) => start + index) })
const filteredQnas = computed(() => qnas.value)
const visibleQnas = computed(() => qnas.value)
async function loadQnas() {
  try { const result = await getQnas({ page: currentPage.value, size: pageSize, searchType: ['title', 'content'].includes(searchField.value) ? searchField.value : undefined, keyword: searchText.value.trim(), mine: route.query.mine === 'true' }); qnas.value = result.items; totalCount.value = result.totalCount ?? result.items.length }
  catch (error) { qnas.value = []; totalCount.value = 0; console.error(apiError(error)) }
}
onMounted(loadQnas)
watch(currentPage, loadQnas)
watch(() => route.query.mine, () => { currentPage.value = 1; loadQnas() })
function search() { currentPage.value = 1; loadQnas() }

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), pageCount.value)
}
</script>

<template>
  <div class="member-qna-page">
    <PageHeader />

    <main class="member-qna-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다."  />

      <div class="member-qna-layout">
        <aside class="member-qna-sidebar" aria-label="Q&A 메뉴">
          <RouterLink :class="['member-qna-sidebar__link', { 'is-active': route.query.mine !== 'true' }]" to="/qna">목록</RouterLink>
          <RouterLink class="member-qna-sidebar__link" to="/qna/create">문의하기</RouterLink>
          <RouterLink :class="['member-qna-sidebar__link', { 'is-active': route.query.mine === 'true' }]" to="/qna?mine=true">내 문의 내역</RouterLink>
        </aside>

        <section class="member-qna-board" aria-label="Q&A 목록">
          <form class="member-qna-search" @submit.prevent="search">
            <label class="visually-hidden" for="member-qna-search-field">검색 범위</label>
            <select id="member-qna-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="content">내용</option>
            </select>
            <label class="visually-hidden" for="member-qna-search-text">검색어</label>
            <div class="member-qna-search__input-wrap">
              <input id="member-qna-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <SearchIcon />
            </div>
            <button class="member-qna-search__button" type="submit">검색</button>
            <button class="member-qna-register-button" type="button" @click="router.push('/qna/create')">⊕ 등록하기</button>
          </form>

          <p class="member-qna-total-count">전체 {{ totalCount }}건</p>
          <div class="member-qna-table-scroll">
            <table class="member-qna-table">
              <thead>
                <tr>
                  <th>번호</th>
                  <th class="member-qna-col-title">제목</th>
                  <th>공개</th>
                  <th>문의 유형</th>
                  <th>작성자</th>
                  <th>상태</th>
                  <th>등록일</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="qna in visibleQnas" :key="qna.id">
                  <td>{{ qna.id }}</td>
                  <td class="member-qna-table__title">
                    <button class="member-qna-title-link" type="button" @click="router.push({ path: '/qna/detail', query: { id: qna.id } })">{{ qna.title }}</button>
                  </td>
                  <td>{{ qna.visibility === '비공개' ? '🔒' : '전체' }}</td>
                  <td><span :class="['member-qna-category', `member-qna-category--${qna.category}`]">{{ qna.category }}</span></td>
                  <td>{{ qna.writer }}</td>
                  <td><span :class="['member-qna-status', `member-qna-status--${qna.status}`]">{{ qna.status }}</span></td>
                  <td>{{ qna.date }}</td>
                </tr>
                <tr v-if="filteredQnas.length === 0">
                  <td colspan="7" class="member-qna-table__empty">문의 내역이 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="member-qna-pagination" aria-label="Q&A 페이지">
            <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="goToPage(1)">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">‹</button>
            <button v-for="page in visiblePages" :key="page" type="button" :class="{ 'is-current': page === currentPage }" :aria-current="currentPage === page ? 'page' : undefined" @click="goToPage(page)">{{ page }}</button>
            <button type="button" aria-label="다음 페이지" :disabled="currentPage === pageCount" @click="goToPage(currentPage + 1)">›</button>
            <button type="button" aria-label="마지막 페이지" :disabled="currentPage === pageCount" @click="goToPage(pageCount)">»</button>
          </nav>
        </section>
      </div>
    </main>

    <PageFooter />

  </div>
</template>

