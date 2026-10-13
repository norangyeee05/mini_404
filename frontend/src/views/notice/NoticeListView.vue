<script setup>
import { computed, onMounted, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getNotices } from '../../api/notices'
import { errorMessage } from '../../api/client'
import { normalizeNotice } from '../../api/normalize'
import './notice-list.css'

const notices = ref([])
const searchField = ref('all')
const searchInput = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)
const loading = ref(false)
const message = ref('')
const totalPages = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const pageRows = computed(() => notices.value)
const visiblePages = computed(() => Array.from({ length: Math.min(5, totalPages.value) }, (_, index) => index + 1))

async function loadNotices() {
  loading.value = true
  message.value = ''
  try {
    const result = await getNotices({ page: currentPage.value, size: pageSize, searchType: searchField.value, keyword: searchInput.value.trim() })
    notices.value = result.items.map(normalizeNotice)
    totalCount.value = result.totalCount
  } catch (error) { message.value = errorMessage(error, '공지사항을 불러오지 못했습니다.') }
  finally { loading.value = false }
}

function searchNotices() {
  currentPage.value = 1
  loadNotices()
}

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), totalPages.value)
  loadNotices()
}

onMounted(loadNotices)
</script>

<template>
  <div class="notice-page">
    <PageHeader role="guest" />

    <main class="notice-main">
      <section class="notice-hero" aria-labelledby="notice-page-title">
        <div class="notice-breadcrumb">HOME <span>›</span> 공지사항</div>
        <h1 id="notice-page-title">공지사항</h1>
        <p>교통서비스 관련 주요 내용을 확인하실 수 있습니다.</p>
      </section>

      <div class="notice-content-grid">
        <aside class="notice-sidebar" aria-label="고객센터 메뉴">
          <a class="notice-sidebar__link notice-sidebar__link--active" href="#">목록</a>
          <a class="notice-sidebar__link" href="#">문의하기</a>
          <a class="notice-sidebar__link" href="#">내 문의 내역</a>
          <a class="notice-sidebar__link" href="#">자주 묻는 질문</a>
        </aside>

        <section class="notice-board" aria-label="공지사항 목록">
          <form class="notice-search" @submit.prevent="searchNotices">
            <label class="visually-hidden" for="notice-search-field">검색 범위</label>
            <select id="notice-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
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
              <span class="notice-search__icon" aria-hidden="true"></span>
            </div>
            <button class="notice-search__button" type="submit">검색</button>
          </form>

          <p v-if="loading" role="status">불러오는 중...</p>
          <p v-if="message" role="alert">{{ message }}</p>
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
                    <a class="notice-table__title-link" href="/notice/detail">{{ notice.title }}</a>
                  </td>
                  <td>{{ notice.writer }}</td>
                  <td>{{ notice.viewCount }}</td>
                  <td>{{ notice.createdAt }}</td>
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

            <div class="notice-actions" aria-label="문의 작성">
              <button type="button" class="notice-actions__button notice-actions__button--primary">질문하기</button>
            </div>
          </div>
        </section>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
