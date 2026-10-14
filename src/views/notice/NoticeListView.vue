<script setup>
import { computed, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-list.css'
import { currentMember } from '../../services/auth'
import { noticesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
const notices = ref([]), totalCount = ref(0), error = ref('')
const searchField = ref('title'), searchInput = ref(''), appliedQuery = ref(''), currentPage = ref(1), pageSize = 10
const totalPages = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const pageRows = computed(() => notices.value)
const visiblePages = computed(() => { const start=Math.floor((currentPage.value-1)/5)*5+1; return Array.from({length:Math.min(5,totalPages.value-start+1)},(_,index)=>start+index) })
async function load() { error.value=''; try { const res=await noticesApi.list({page:currentPage.value,size:pageSize,searchType:searchField.value,keyword:appliedQuery.value}); notices.value=res.items; totalCount.value=res.totalCount } catch(e) { error.value=apiError(e) } }
function searchNotices() { appliedQuery.value=searchInput.value.trim();currentPage.value=1;load() }
function goToPage(page) { currentPage.value=Math.min(Math.max(page,1),totalPages.value);load() }
load()
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
                <tr v-for="notice in pageRows" :key="notice.noticeId">
                  <td>{{ notice.noticeId }}</td>
                  <td class="notice-table__title">
                    <RouterLink class="notice-table__title-link" :to="`/notices/${notice.noticeId}`">{{ notice.title }}</RouterLink>
                  </td>
                  <td>{{ notice.writer }}</td>
                  <td>{{ notice.viewCount }}</td>
                  <td>{{ notice.createdAt }}</td>
                </tr>
                <tr v-if="error"><td colspan="5" class="notice-table__empty">{{ error }}</td></tr>
                <tr v-else-if="pageRows.length === 0">
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
<div v-if="currentMember" class="notice-actions" aria-label="문의 작성"><RouterLink to="/qna/new" class="notice-actions__button notice-actions__button--primary">질문하기</RouterLink></div>
          </div>
        </section>
      </div>
    </main>

    <PageFooter />
  </div>
</template>

