<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getNotices, saveNotices } from './noticeMockData'
import './admin-notice-list.css'

const router = useRouter()
const notices = ref(getNotices())
const searchField = ref('all')
const searchText = ref('')

const filteredNotices = computed(() => {
  const query = searchText.value.trim().toLocaleLowerCase()
  if (!query) return notices.value

  return notices.value.filter((notice) => {
    if (searchField.value === 'all') {
      return `${notice.title} ${notice.content} ${notice.writer}`.toLocaleLowerCase().includes(query)
    }

    return notice[searchField.value].toLocaleLowerCase().includes(query)
  })
})

function editNotice(notice) {
  router.push({ path: '/admin/notices/edit', query: { id: notice.id } })
}

function deleteNotice(noticeId) {
  const confirmed = window.confirm('이 공지사항을 삭제하시겠습니까?')
  if (!confirmed) return

  notices.value = notices.value.filter((notice) => notice.id !== noticeId)
  saveNotices(notices.value)
}
</script>

<template>
  <div class="admin-notice-list-page">
    <PageHeader role="admin" />

    <main class="admin-notice-list-main">
      <section class="admin-notice-list-hero" aria-labelledby="admin-notice-list-title">
        <div class="admin-notice-list-breadcrumb">HOME <span>›</span> 공지사항</div>
        <h1 id="admin-notice-list-title">공지사항</h1>
        <p>교통서비스 관련 주요 내용을 확인하고 관리할 수 있습니다.</p>
      </section>

      <div class="admin-notice-list-layout">
        <aside class="admin-notice-list-sidebar" aria-label="공지사항 메뉴">
          <a class="admin-notice-list-sidebar__link admin-notice-list-sidebar__link--active" href="/admin/notices">목록</a>
          <a class="admin-notice-list-sidebar__link" href="#">문의하기</a>
          <a class="admin-notice-list-sidebar__link" href="#">내 문의 내역</a>
          <a class="admin-notice-list-sidebar__link" href="#">자주 묻는 질문</a>
        </aside>

        <section class="admin-notice-board" aria-label="공지사항 관리 목록">
          <form class="admin-notice-search" @submit.prevent>
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
              <span class="admin-notice-search__icon" aria-hidden="true"></span>
            </div>

            <button class="admin-notice-search__button" type="submit">검색</button>
            <a class="admin-notice-register-button" href="/admin/notices/create">⊕ 등록하기</a>
          </form>

          <div class="admin-notice-table-scroll">
            <table class="admin-notice-table">
              <thead>
                <tr>
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
                  <td>{{ notice.id }}</td>
                  <td class="admin-notice-table__title">
                    <a class="admin-notice-title-link" href="/admin/notices/detail">{{ notice.title }}</a>
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
                  <td colspan="6" class="admin-notice-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-notice-pagination" aria-label="공지사항 페이지">
            <button type="button" aria-label="첫 페이지">«</button>
            <button type="button" aria-label="이전 페이지">‹</button>
            <button v-for="page in 5" :key="page" type="button" :class="{ 'is-current': page === 1 }">
              {{ page }}
            </button>
            <button type="button" aria-label="다음 페이지">›</button>
            <button type="button" aria-label="마지막 페이지">»</button>
          </nav>
        </section>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
