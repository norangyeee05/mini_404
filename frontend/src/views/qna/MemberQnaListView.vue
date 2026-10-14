<script setup>
import { computed,ref,watch } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue';import PageBanner from '../../components/PageBanner.vue';import PageFooter from '../../components/PageFooter.vue'
import './member-qna-list.css';import {qnaApi} from '../../services/endpoints';import {apiError} from '../../services/api'
const props=defineProps({mine:Boolean}),route=useRoute(),router=useRouter(),qnas=ref([]),totalCount=ref(0),searchField=ref('title'),searchText=ref(''),currentPage=ref(1),pageSize=10,inquiryType=ref(''),error=ref('')
const pageCount=computed(()=>Math.max(1,Math.ceil(totalCount.value/pageSize))),visibleQnas=computed(()=>qnas.value);const types={FACILITY:'시설',OPERATION:'운행',ETC:'기타'},statusText={WAITING:'답변대기',ANSWERED:'답변완료'}
async function load(){try{const r=await qnaApi.list({page:currentPage.value,size:pageSize,searchType:searchField.value,keyword:searchText.value,inquiryType:inquiryType.value||undefined},props.mine||route.path==='/qna/my');qnas.value=r.items.map(x=>({...x,id:x.qnaId,category:types[x.inquiryType]||x.inquiryType,status:statusText[x.status]||x.status,date:x.createdAt}));totalCount.value=r.totalCount}catch(e){error.value=apiError(e)}}
function goToPage(v){currentPage.value=Math.min(Math.max(v,1),pageCount.value);load()}
watch(()=>route.fullPath,()=>{currentPage.value=1;load()},{immediate:true})
</script>

<template>
  <div class="member-qna-page">
    <PageHeader />

    <main class="member-qna-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다."  />

      <div class="member-qna-layout">
        <aside class="member-qna-sidebar" aria-label="Q&A 메뉴">
          <RouterLink :class="['member-qna-sidebar__link', { 'is-active': route.path !== '/qna/my' }]" to="/qna">목록</RouterLink>
          <RouterLink class="member-qna-sidebar__link" to="/qna/new">문의하기</RouterLink>
          <RouterLink :class="['member-qna-sidebar__link', { 'is-active': route.path === '/qna/my' }]" to="/qna/my">내 문의 내역</RouterLink>
        </aside>

        <section class="member-qna-board" aria-label="Q&A 목록">
          <form class="member-qna-search" @submit.prevent="currentPage = 1; load()">
            <label class="visually-hidden" for="member-qna-search-field">검색 범위</label>
            <select id="member-qna-search-field" v-model="searchField" @change="load">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="content">내용</option>
            </select>
            <label class="visually-hidden" for="member-qna-inquiry-type">문의 유형</label>
            <select id="member-qna-inquiry-type" v-model="inquiryType" @change="currentPage = 1; load()"><option value="">전체 유형</option><option value="FACILITY">편의시설</option><option value="OPERATION">운행</option><option value="ETC">기타·요금</option></select>
            <label class="visually-hidden" for="member-qna-search-text">검색어</label>
            <div class="member-qna-search__input-wrap">
              <input id="member-qna-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <span class="member-qna-search__icon" aria-hidden="true"></span>
            </div>
            <button class="member-qna-search__button" type="submit">검색</button>
            <button class="member-qna-register-button" type="button" @click="router.push('/qna/new')">⊕ 등록하기</button>
          </form>

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
                <tr v-for="qna in visibleQnas" :key="qna.qnaId">
                  <td>{{ qna.qnaId }}</td>
                  <td class="member-qna-table__title">
                    <button class="member-qna-title-link" type="button" @click="router.push(`/qna/${qna.qnaId}`)">{{ qna.title }}</button>
                  </td>
                  <td>—</td>
                  <td><span :class="['member-qna-category', `member-qna-category--${qna.category}`]">{{ qna.category }}</span></td>
                  <td>{{ qna.writer }}</td>
                  <td><span :class="['member-qna-status', `member-qna-status--${qna.status}`]">{{ qna.status }}</span></td>
                  <td>{{ qna.date }}</td>
                </tr>
                <tr v-if="error"><td colspan="7">{{ error }}</td></tr><tr v-else-if="!visibleQnas.length">
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

