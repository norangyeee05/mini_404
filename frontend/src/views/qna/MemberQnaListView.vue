<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getMyQnas, getQnas } from '../../api/qna'
import { errorMessage } from '../../api/client'
import { normalizeQna } from '../../api/normalize'
import './member-qna-list.css'

const route = useRoute()
const router = useRouter()
const qnas = ref([])
const searchField = ref('all')
const searchText = ref('')
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)
const message = ref('')
const loading = ref(false)

const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const visibleQnas = computed(() => qnas.value)

async function loadQnas() {
  loading.value = true
  message.value = ''
  try {
    const params = { page: currentPage.value, size: pageSize, searchType: searchField.value, keyword: searchText.value.trim() }
    const result = route.name === 'member-qna-my' || route.query.mine === 'true' ? await getMyQnas(params) : await getQnas(params)
    qnas.value = result.items.map(normalizeQna)
    totalCount.value = result.totalCount
    currentPage.value = result.page
  } catch (error) { message.value = errorMessage(error, 'Q&A 목록을 불러오지 못했습니다.') }
  finally { loading.value = false }
}

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), pageCount.value)
  loadQnas()
}

function search() { currentPage.value = 1; loadQnas() }
watch(() => [route.query.mine, route.name], () => { currentPage.value = 1; loadQnas() })
onMounted(loadQnas)
</script>

<template>
  <div class="member-qna-page">
    <PageHeader role="member" />

    <main class="member-qna-main">
      <section class="member-qna-hero" aria-labelledby="member-qna-title">
        <div class="member-qna-breadcrumb">HOME <span>›</span> Q&amp;A</div>
        <h1 id="member-qna-title">Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

      <div class="member-qna-layout">
        <aside class="member-qna-sidebar" aria-label="Q&A 메뉴">
          <a :class="['member-qna-sidebar__link', { 'is-active': route.query.mine !== 'true' }]" href="/qna">목록</a>
          <a class="member-qna-sidebar__link" href="/qna/create">문의하기</a>
          <a :class="['member-qna-sidebar__link', { 'is-active': route.name === 'member-qna-my' || route.query.mine === 'true' }]" href="/qna/my">내 문의 내역</a>
        </aside>

        <section class="member-qna-board" aria-label="Q&A 목록">
          <form class="member-qna-search" @submit.prevent="search">
            <label class="visually-hidden" for="member-qna-search-field">검색 범위</label>
            <select id="member-qna-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="writer">작성자</option>
            </select>
            <label class="visually-hidden" for="member-qna-search-text">검색어</label>
            <div class="member-qna-search__input-wrap">
              <input id="member-qna-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <span class="member-qna-search__icon" aria-hidden="true"></span>
            </div>
            <button class="member-qna-search__button" type="submit">검색</button>
            <button class="member-qna-register-button" type="button" @click="router.push('/qna/new')">⊕ 등록하기</button>
          </form>
          <p v-if="message" role="alert">{{ message }}</p>

          <p v-if="loading" role="status">불러오는 중...</p>
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
                    <button class="member-qna-title-link" type="button" @click="router.push(`/qna/${qna.id}`)">{{ qna.title }}</button>
                  </td>
                  <td>{{ qna.visibility ? (qna.visibility === '비공개' ? '🔒' : qna.visibility) : '-' }}</td>
                  <td><span :class="['member-qna-category', `member-qna-category--${qna.category}`]">{{ qna.category }}</span></td>
                  <td>{{ qna.writer }}</td>
                  <td><span :class="['member-qna-status', `member-qna-status--${qna.status}`]">{{ qna.status }}</span></td>
                  <td>{{ qna.date }}</td>
                </tr>
                <tr v-if="visibleQnas.length === 0">
                  <td colspan="7" class="member-qna-table__empty">문의 내역이 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="member-qna-pagination" aria-label="Q&A 페이지">
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
