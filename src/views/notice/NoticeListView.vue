<script setup>
import { computed, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-list.css'

const noticeSeeds = [
  { title: '교통카드 이용 안내 변경사항', writer: '관리자', viewCount: 110, createdAt: '2026.10.01 19:36', content: '교통카드 이용 안내 변경사항을 알려드립니다.' },
  { title: '7호선 일부 구간 점검 안내', writer: '관리자', viewCount: 109, createdAt: '2026.10.01 16:22', content: '시설 점검에 따른 일부 구간 운행 안내입니다.' },
  { title: '시설 점검 일정 안내', writer: '관리자', viewCount: 108, createdAt: '2026.10.01 13:45', content: '역사 및 교통서비스 시설 점검 일정을 안내합니다.' },
  { title: '운행 지연 안내', writer: '이무개', viewCount: 107, createdAt: '2026.10.01 11:06', content: '열차 운행이 지연되고 있어 안내드립니다.' },
  { title: '인천 패스 안내', writer: '관리자', viewCount: 106, createdAt: '2026.10.01 08:26', content: '인천 패스 이용 방법과 적용 범위를 확인해 주세요.' },
  { title: '보관함 있나요??', writer: '홍길동', viewCount: 105, createdAt: '2026.10.01 05:18', content: '역사 내 물품 보관함 위치를 안내합니다.' },
  { title: '막차 시간 언제인가요?', writer: '김아무', viewCount: 104, createdAt: '2026.09.30 22:25', content: '역별 막차 시간을 확인하실 수 있습니다.' },
  { title: '에어컨 좀 틀어주세요 ㅠㅠ', writer: '이무개', viewCount: 103, createdAt: '2026.09.30 16:45', content: '객실 냉방 관련 이용 안내입니다.' },
  { title: '직원이 불친절해요', writer: '이무개', viewCount: 102, createdAt: '2026.09.30 15:53', content: '고객 응대 관련 안내와 의견 접수 방법입니다.' },
  { title: '직원이 불친절해요', writer: '이무개', viewCount: 101, createdAt: '2026.09.30 15:53', content: '고객 응대 관련 안내와 의견 접수 방법입니다.' },
]

const notices = Array.from({ length: 50 }, (_, index) => {
  const seed = noticeSeeds[index % noticeSeeds.length]
  const cycle = Math.floor(index / noticeSeeds.length)
  return {
    id: 100 - index,
    ...seed,
    title: cycle === 0 ? seed.title : `${seed.title} (${cycle + 1})`,
    viewCount: Math.max(seed.viewCount - index, 0),
  }
})

const searchField = ref('all')
const searchInput = ref('')
const appliedQuery = ref('')
const currentPage = ref(1)
const pageSize = 10

const filteredNotices = computed(() => {
  const query = appliedQuery.value.toLocaleLowerCase()
  if (!query) return notices

  return notices.filter((notice) => {
    const fields = searchField.value === 'all'
      ? [notice.title, notice.content, notice.writer, String(notice.id)]
      : [notice[searchField.value] ?? '']
    return fields.some((value) => value.toLocaleLowerCase().includes(query))
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredNotices.value.length / pageSize)))
const pageRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredNotices.value.slice(start, start + pageSize)
})
const visiblePages = computed(() => Array.from({ length: Math.min(5, totalPages.value) }, (_, index) => index + 1))

function searchNotices() {
  appliedQuery.value = searchInput.value.trim()
  currentPage.value = 1
}

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), totalPages.value)
}
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
              <span class="notice-search__icon" aria-hidden="true"></span>
            </div>
            <button class="notice-search__button" type="submit">검색</button>
          </form>

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
