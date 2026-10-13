<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQnas, saveQnas } from './qnaMockData'
import './admin-qna-list.css'

const props = defineProps({
  mode: {
    type: String,
    default: 'all',
    validator: (value) => ['all', 'pending'].includes(value),
  },
})

const qnas = ref(getQnas())
const route = useRoute()
const router = useRouter()
const searchField = ref('all')
const searchText = ref('')
const editingQna = ref(null)
const currentPage = ref(1)
const pageSize = 10

onMounted(() => {
  if (props.mode === 'pending') return
  if (!route.query.edit) return

  const selectedQna = qnas.value.find((qna) => String(qna.id) === String(route.query.edit))
  if (selectedQna) editQna(selectedQna)
})

const filteredQnas = computed(() => {
  const query = searchText.value.trim().toLocaleLowerCase()
  const sourceQnas = props.mode === 'pending'
    ? qnas.value.filter((qna) => qna.status === '답변대기')
    : qnas.value
  if (!query) return sourceQnas

  return sourceQnas.filter((qna) => {
    const text = searchField.value === 'all'
      ? `${qna.title} ${qna.content} ${qna.writer}`
      : qna[searchField.value]
    return text.toLocaleLowerCase().includes(query)
  })
})

const pageCount = computed(() => Math.max(1, Math.ceil(filteredQnas.value.length / pageSize)))
const paginatedQnas = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize
  return filteredQnas.value.slice(startIndex, startIndex + pageSize)
})

watch([searchText, searchField], () => {
  currentPage.value = 1
})

function editQna(qna) {
  editingQna.value = { ...qna }
}

function saveEditedQna() {
  if (props.mode === 'pending') {
    editingQna.value.status = '답변완료'
  }

  qnas.value = qnas.value.map((qna) => (
    qna.id === editingQna.value.id ? { ...editingQna.value } : qna
  ))
  saveQnas(qnas.value)
  editingQna.value = null
  currentPage.value = Math.min(currentPage.value, pageCount.value)
}

function deleteQna(qnaId) {
  if (!window.confirm('이 Q&A를 삭제하시겠습니까?')) return

  qnas.value = qnas.value.filter((qna) => qna.id !== qnaId)
  saveQnas(qnas.value)
  currentPage.value = Math.min(currentPage.value, pageCount.value)
}

function openQna(qnaId) {
  router.push({ path: '/admin/qna/detail', query: { id: qnaId } })
}
</script>

<template>
  <div class="admin-qna-page">
    <PageHeader role="admin" />

    <main class="admin-qna-main">
      <section class="admin-qna-hero" aria-labelledby="admin-qna-title">
        <div class="admin-qna-breadcrumb">HOME <span>›</span> Q&amp;A <span v-if="props.mode === 'pending'">› 답변 대기</span></div>
        <h1 id="admin-qna-title">Q&amp;A</h1>
        <p>{{ props.mode === 'pending' ? '답변을 기다리는 문의를 확인하고 답변할 수 있습니다.' : '궁금하신 내용을 확인하실 수 있습니다.' }}</p>
      </section>

      <div class="admin-qna-layout">
        <aside class="admin-qna-sidebar" aria-label="Q&A 메뉴">
          <a :class="['admin-qna-sidebar__link', { 'admin-qna-sidebar__link--active': props.mode === 'all' }]" href="/admin/qna">목록</a>
          <a :class="['admin-qna-sidebar__link', { 'admin-qna-sidebar__link--active': props.mode === 'pending' }]" href="/admin/qna/pending">답변 대기</a>
          <a class="admin-qna-sidebar__link" href="#">문의하기</a>
          <a class="admin-qna-sidebar__link" href="#">내 문의 내역</a>
        </aside>

        <section class="admin-qna-board" aria-label="Q&A 관리 목록">
          <form class="admin-qna-search" @submit.prevent>
            <label class="visually-hidden" for="admin-qna-search-field">검색 범위</label>
            <select id="admin-qna-search-field" v-model="searchField">
              <option value="all">전체</option>
              <option value="title">제목</option>
              <option value="content">내용</option>
              <option value="writer">작성자</option>
            </select>
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
                <tr v-for="qna in paginatedQnas" :key="qna.id">
                  <td>{{ qna.id }}</td>
                  <td class="admin-qna-table__title">
                    <button class="admin-qna-title-link" type="button" @click="openQna(qna.id)">{{ qna.title }}</button>
                  </td>
                  <td>{{ qna.visibility === '비공개' ? '🔒' : '전체' }}</td>
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
                    <template v-else>
                      <button class="admin-qna-row-button admin-qna-row-button--edit" type="button" @click="editQna(qna)">수정</button>
                      <button class="admin-qna-row-button admin-qna-row-button--delete" type="button" @click="deleteQna(qna.id)">삭제</button>
                    </template>
                  </td>
                </tr>
                <tr v-if="filteredQnas.length === 0">
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

    <div v-if="editingQna" class="admin-qna-modal-backdrop" @click.self="editingQna = null">
      <form class="admin-qna-edit-modal" @submit.prevent="saveEditedQna">
        <div class="admin-qna-edit-modal__heading">
          <h2>{{ props.mode === 'pending' ? 'Q&A 답변 작성' : 'Q&A 수정' }}</h2>
          <button type="button" aria-label="닫기" @click="editingQna = null">×</button>
        </div>
        <template v-if="props.mode === 'pending'">
          <label for="qna-question">질문</label>
          <textarea id="qna-question" :value="editingQna.content" rows="4" readonly></textarea>
          <label for="qna-answer">관리자 답변</label>
          <textarea id="qna-answer" v-model="editingQna.answer" rows="5" placeholder="답변 내용을 입력하세요." required></textarea>
        </template>
        <template v-else>
          <label for="qna-title">제목</label>
          <input id="qna-title" v-model="editingQna.title" maxlength="200" required />
          <label for="qna-content">내용</label>
          <textarea id="qna-content" v-model="editingQna.content" rows="5" required></textarea>
          <label for="qna-answer">관리자 답변</label>
          <textarea id="qna-answer" v-model="editingQna.answer" rows="4" placeholder="답변 내용을 입력하세요."></textarea>
          <div class="admin-qna-edit-modal__row">
            <label for="qna-visibility">공개 여부</label>
            <select id="qna-visibility" v-model="editingQna.visibility">
              <option>전체</option>
              <option>비공개</option>
            </select>
          </div>
          <div class="admin-qna-edit-modal__row">
            <label for="qna-category">문의 유형</label>
            <select id="qna-category" v-model="editingQna.category">
              <option>시설</option>
              <option>기타</option>
              <option>운행</option>
              <option>자료실</option>
            </select>
          </div>
          <div class="admin-qna-edit-modal__row">
            <label for="qna-status">답변 상태</label>
            <select id="qna-status" v-model="editingQna.status">
              <option>답변대기</option>
              <option>답변완료</option>
            </select>
          </div>
        </template>
        <div class="admin-qna-edit-modal__actions">
          <button type="button" @click="editingQna = null">취소</button>
          <button type="submit">{{ props.mode === 'pending' ? '답변 등록' : '저장' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>
