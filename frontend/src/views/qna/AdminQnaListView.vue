<script setup>
import {computed,onMounted,ref,watch} from 'vue';import {useRoute,useRouter} from 'vue-router';import PageHeader from '../../components/PageHeader.vue';import PageBanner from '../../components/PageBanner.vue';import PageFooter from '../../components/PageFooter.vue';import DeleteModal from '../../components/DeleteModal.vue';import {qnaApi} from '../../services/endpoints';import {apiError} from '../../services/api';import './admin-qna-list.css'
const props=defineProps({mode:{type:String,default:'all'}}),route=useRoute(),router=useRouter(),qnas=ref([]),totalCount=ref(0),searchField=ref('title'),searchText=ref(''),inquiryType=ref(''),currentPage=ref(1),pageSize=10,editingQna=ref(null),deleteStage=ref(''),deletingId=ref(null),error=ref(''),pageCount=computed(()=>Math.max(1,Math.ceil(totalCount.value/pageSize))),paginatedQnas=computed(()=>qnas.value),types={FACILITY:'시설',OPERATION:'운행',ETC:'기타'},statusText={WAITING:'답변대기',ANSWERED:'답변완료'}
async function load(){try{const r=await qnaApi.adminList({page:currentPage.value,size:pageSize,searchType:searchField.value,keyword:searchText.value,inquiryType:inquiryType.value||undefined,status:props.mode==='pending'?'WAITING':undefined});qnas.value=r.items.map(x=>({...x,id:x.qnaId,category:types[x.inquiryType],status:statusText[x.status]||x.status,date:x.createdAt}));totalCount.value=r.totalCount}catch(e){error.value=apiError(e)}}
function editQna(q){editingQna.value={...q}}
async function saveEditedQna(){try{await qnaApi.answer(editingQna.value.id,editingQna.value.answer);editingQna.value=null;load()}catch(e){error.value=apiError(e)}}
function deleteQna(id){deletingId.value=id;deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){closeDelete();return}try{await qnaApi.adminRemove(deletingId.value);deleteStage.value='complete';load()}catch(e){error.value=apiError(e);deleteStage.value=''}}
function closeDelete(){deleteStage.value='';deletingId.value=null}
function openQna(id){router.push(`/admin/qna/${id}`)}
watch(currentPage,load);onMounted(()=>{load();if(route.query.edit)qnaApi.adminGet(route.query.edit).then(r=>{const d=r?.data??r;editQna({...d,id:d.qnaId,answer:d.answer||''})}).catch(e=>error.value=apiError(e))})
</script>

<template>
  <div class="admin-qna-page">
    <PageHeader />

    <main class="admin-qna-main">
      <PageBanner title="Q&amp;A" :description="props.mode === 'pending' ? '답변을 기다리는 문의를 확인하고 답변할 수 있습니다.' : '궁금하신 내용을 확인하실 수 있습니다.'" :stage="props.mode === 'pending' ? '답변 대기' : undefined" />

      <div class="admin-qna-layout">
        <aside class="admin-qna-sidebar" aria-label="Q&A 메뉴">
          <RouterLink :class="['admin-qna-sidebar__link', { 'admin-qna-sidebar__link--active': props.mode === 'all' }]" to="/admin/qna">목록</RouterLink>
          <RouterLink :class="['admin-qna-sidebar__link', { 'admin-qna-sidebar__link--active': props.mode === 'pending' }]" to="/admin/qna/pending">답변 대기</RouterLink>
          
          
        </aside>

        <section class="admin-qna-board" aria-label="Q&A 관리 목록">
          <form class="admin-qna-search" @submit.prevent="currentPage=1;load()">
            <label class="visually-hidden" for="admin-qna-search-field">검색 범위</label>
            <select id="admin-qna-search-field" v-model="searchField">
              
              <option value="title">제목</option>
              
              <option value="writer">작성자</option>
            </select>
            <label class="visually-hidden" for="admin-qna-inquiry-type">문의 유형</label>
            <select id="admin-qna-inquiry-type" v-model="inquiryType" @change="currentPage=1;load()"><option value="">전체 유형</option><option value="FACILITY">편의시설</option><option value="OPERATION">운행</option><option value="ETC">기타·요금</option></select>
            <label class="visually-hidden" for="admin-qna-search-text">검색어</label>
            <div class="admin-qna-search__input-wrap">
              <input id="admin-qna-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <span class="admin-qna-search__icon" aria-hidden="true"></span>
            </div>
            <button class="admin-qna-search__button" type="submit">검색</button>
          </form>

          <div class="admin-qna-table-scroll">
            <table class="admin-qna-table">
              <thead>
                <tr>
                  <th class="admin-qna-col-number">번호</th>
                  <th class="admin-qna-col-title">제목</th>
                  <th class="admin-qna-col-visibility">공개</th>
                  <th class="admin-qna-col-category">문의 유형</th>
                  <th class="admin-qna-col-writer">작성자</th>
                  <th class="admin-qna-col-status">상태</th>
                  <th class="admin-qna-col-date">등록일</th>
                  <th class="admin-qna-col-actions">{{ props.mode === 'pending' ? '답변' : '관리' }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="qna in paginatedQnas" :key="qna.qnaId">
                  <td>{{ qna.qnaId }}</td>
                  <td class="admin-qna-table__title">
                    <button class="admin-qna-title-link" type="button" @click="openQna(qna.qnaId)">{{ qna.title }}</button>
                  </td>
                  <td>—</td>
                  <td><span :class="['admin-qna-category', `admin-qna-category--${qna.category}`]">{{ qna.category }}</span></td>
                  <td>{{ qna.writer }}</td>
                  <td><span :class="['admin-qna-status', `admin-qna-status--${qna.status}`]">{{ qna.status }}</span></td>
                  <td>{{ qna.date }}</td>
                  <td class="admin-qna-table__actions">
                    <button
                      v-if="props.mode === 'pending'"
                      class="admin-qna-row-button admin-qna-row-button--answer"
                      type="button"
                      @click="editQna(qna)"
                    >답변하기</button>
                    <template v-else><button class="admin-qna-row-button admin-qna-row-button--answer" type="button" @click="openQna(qna.qnaId)">상세</button><button class="admin-qna-row-button admin-qna-row-button--delete" type="button" @click="deleteQna(qna.qnaId)">삭제</button></template>
                  </td>
                </tr>
                <tr v-if="error"><td colspan="8">{{ error }}</td></tr><tr v-else-if="!paginatedQnas.length">
                  <td colspan="8" class="admin-qna-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-qna-pagination" aria-label="Q&A 페이지">
            <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="currentPage = 1">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="currentPage -= 1">‹</button>
            <button v-for="page in pageCount" :key="page" type="button" :class="{ 'is-current': page === currentPage }" @click="currentPage = page">{{ page }}</button>
            <button type="button" aria-label="다음 페이지" :disabled="currentPage === pageCount" @click="currentPage += 1">›</button>
            <button type="button" aria-label="마지막 페이지" :disabled="currentPage === pageCount" @click="currentPage = pageCount">»</button>
          </nav>
        </section>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />

    <div v-if="editingQna" class="admin-qna-modal-backdrop" @click.self="editingQna = null">
      <form class="admin-qna-edit-modal" @submit.prevent="saveEditedQna">
        <div class="admin-qna-edit-modal__heading">
          <h2>Q&amp;A 답변 작성</h2>
          <button type="button" aria-label="닫기" @click="editingQna = null">×</button>
        </div>
        <template v-if="props.mode === 'pending'">
          <label for="qna-question">질문</label>
          <textarea id="qna-question" :value="editingQna.content" rows="4" readonly></textarea>
          <label for="qna-answer">관리자 답변</label>
          <textarea id="qna-answer" v-model="editingQna.answer" rows="5" placeholder="답변 내용을 입력하세요." required></textarea>
        </template>

        <div class="admin-qna-edit-modal__actions">
          <button type="button" @click="editingQna = null">취소</button>
          <button type="submit">답변 등록</button>
        </div>
      </form>
    </div>
  </div>
</template>

