<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { getQnas, getQna, deleteQna as removeQna, updateAdminQna, createAdminQna, updateAdminQuestion } from './qnaMockData'
import { apiError } from '../../services/backendApi'
import { inquiryToApi } from '../../services/apiMappers'
import './admin-qna-list.css'

const props = defineProps({
  mode: {
    type: String,
    default: 'all',
    validator: (value) => ['all', 'pending'].includes(value),
  },
})

const qnas = ref([])
const route = useRoute()
const router = useRouter()
const searchField = ref('all')
const searchText = ref('')
const editingQna = ref(null)
const questionEditor = ref(null)
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)

onMounted(loadQnas)

const filteredQnas = computed(() => qnas.value)
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const visiblePages = computed(() => { const start = Math.min(Math.max(1, currentPage.value - 2), Math.max(1, pageCount.value - 4)); return Array.from({ length: Math.min(5, pageCount.value) }, (_, index) => start + index) })
const paginatedQnas = computed(() => qnas.value)
async function loadQnas() {
  try {
    const result = await getQnas({ page: currentPage.value, size: pageSize, searchType: ['title', 'content', 'writer'].includes(searchField.value) ? searchField.value : undefined, keyword: searchText.value.trim(), status: props.mode === 'pending' ? 'WAITING' : undefined, admin: true })
    qnas.value = result.items
    totalCount.value = result.totalCount ?? result.items.length
    if (route.query.edit) {
      const detail = await getQna(route.query.edit, true)
      openQuestionEditor(detail)
    }
  } catch (error) { qnas.value = []; totalCount.value = 0; console.error(apiError(error)) }
}
watch(currentPage, loadQnas)
watch(() => props.mode, () => { currentPage.value = 1; loadQnas() })

watch([searchText, searchField], () => {
  currentPage.value = 1
})
function search() { currentPage.value = 1; loadQnas() }

async function editQna(qna) {
  try {
    const detail = await getQna(qna.id, true)
    editingQna.value = { ...qna, ...detail }
  } catch (error) { window.alert(apiError(error)) }
}

async function editQuestion(qna) {
  try { openQuestionEditor({ ...qna, ...await getQna(qna.id, true) }) }
  catch (error) { window.alert(apiError(error)) }
}

async function saveEditedQna() {
  try {
    if (!editingQna.value.answer?.trim()) { window.alert('답변 내용을 입력해 주세요.'); return }
    await updateAdminQna(editingQna.value.id, editingQna.value.answer.trim(), editingQna.value.status === '답변완료')
    editingQna.value = null
    await router.push('/admin/qna')
    await loadQnas()
  }
  catch (error) { window.alert(apiError(error)) }
}

function openQuestionEditor(qna = null) {
  questionEditor.value = qna
    ? { id: qna.id, title: qna.title || '', category: qna.category || '운행', isPrivate: qna.isPrivate === true, content: qna.content || '' }
    : { id: null, title: '', category: '운행', isPrivate: false, content: '' }
}

async function saveQuestion() {
  const form = questionEditor.value
  if (!form?.title.trim() || !form?.content.trim()) { window.alert('제목과 내용을 입력해 주세요.'); return }
  const fields = { title: form.title.trim(), inquiryType: inquiryToApi[form.category], isPrivate: form.isPrivate, content: form.content.trim() }
  try {
    if (form.id) await updateAdminQuestion(form.id, fields)
    else await createAdminQna(fields)
    questionEditor.value = null
    if (route.query.edit) await router.replace('/admin/qna')
    await loadQnas()
  } catch (error) { window.alert(apiError(error)) }
}

const deleteStage = ref('')
const deletingId = ref(null)
function deleteQna(qnaId) {
  deletingId.value = qnaId
  deleteStage.value = 'confirm'
}
async function confirmDelete() {
  if (deleteStage.value === 'complete') { closeDelete(); return }
  try { await removeQna(deletingId.value, true); totalCount.value = Math.max(0, totalCount.value - 1); await loadQnas(); deleteStage.value = 'complete' }
  catch (error) { deleteStage.value = ''; window.alert(apiError(error)) }
}
function closeDelete() {
  deleteStage.value = ''
  deletingId.value = null
}

function openQna(qnaId) {
  router.push({ path: '/admin/qna/detail', query: { id: qnaId } })
}
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
          <form class="admin-qna-search" @submit.prevent="search">
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
              <SearchIcon />
            </div>
            <button class="admin-qna-search__button" type="submit">검색</button>
            <button v-if="props.mode === 'all'" class="admin-qna-register-button" type="button" @click="openQuestionEditor()">⊕ 등록하기</button>
          </form>

          <p class="admin-qna-total-count">전체 {{ totalCount }}건</p>
          <div class="admin-qna-table-scroll">
            <table class="admin-qna-table">
              <thead>
                <tr>
                  <th class="admin-qna-col-number">번호</th>
                  <th class="admin-qna-col-title">제목</th>
                  <th class="admin-qna-col-writer">작성자</th>
                  <th class="admin-qna-col-views">조회수</th>
                  <th class="admin-qna-col-visibility">공개</th>
                  <th class="admin-qna-col-category">문의 유형</th>
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
                  <td>{{ qna.writer }}</td>
                  <td>{{ qna.views ?? qna.viewCount ?? 0 }}</td>
                  <td>{{ qna.visibility === '비공개' ? '🔒 비공개' : '전체' }}</td>
                  <td><span :class="['admin-qna-category', `admin-qna-category--${qna.category}`]">{{ qna.category }}</span></td>
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
                      <button class="admin-qna-row-button admin-qna-row-button--delete" type="button" @click="deleteQna(qna.id)">삭제</button>
                    </template>
                  </td>
                </tr>
                <tr v-if="filteredQnas.length === 0">
                  <td colspan="9" class="admin-qna-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-qna-pagination" aria-label="Q&A 페이지">
            <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="currentPage = 1">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="currentPage -= 1">‹</button>
            <button v-for="page in visiblePages" :key="page" type="button" :class="{ 'is-current': page === currentPage }" :aria-current="currentPage === page ? 'page' : undefined" @click="currentPage = page">{{ page }}</button>
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
        <label for="qna-title">제목</label>
        <input id="qna-title" :value="editingQna.title" readonly aria-readonly="true" />
        <label for="qna-question">문의 내용</label>
        <textarea id="qna-question" :value="editingQna.content" rows="5" readonly aria-readonly="true"></textarea>
        <label for="qna-answer">관리자 답변</label>
        <textarea id="qna-answer" v-model="editingQna.answer" rows="5" placeholder="답변 내용을 입력하세요." required></textarea>
        <div class="admin-qna-edit-modal__actions">
          <button type="button" @click="editingQna = null">취소</button>
          <button type="submit">답변 등록</button>
        </div>
      </form>
    </div>

    <div v-if="questionEditor" class="admin-qna-modal-backdrop" @click.self="questionEditor = null">
      <form class="admin-qna-edit-modal" @submit.prevent="saveQuestion">
        <div class="admin-qna-edit-modal__heading">
          <h2>{{ questionEditor.id ? 'Q&A 질문 수정' : 'Q&A 등록' }}</h2>
          <button type="button" aria-label="닫기" @click="questionEditor = null">×</button>
        </div>
        <label for="admin-qna-question-title">제목</label>
        <input id="admin-qna-question-title" v-model="questionEditor.title" maxlength="200" required />
        <label for="admin-qna-question-category">문의 유형</label>
        <select id="admin-qna-question-category" v-model="questionEditor.category">
          <option>운행</option><option>시설</option><option>기타</option>
        </select>
        <label for="admin-qna-question-visibility">공개 설정</label>
        <select id="admin-qna-question-visibility" v-model="questionEditor.isPrivate">
          <option :value="false">전체 공개</option><option :value="true">비공개</option>
        </select>
        <label for="admin-qna-question-content">문의 내용</label>
        <textarea id="admin-qna-question-content" v-model="questionEditor.content" rows="6" required></textarea>
        <div class="admin-qna-edit-modal__actions">
          <button type="button" @click="questionEditor = null">취소</button>
          <button type="submit">저장</button>
        </div>
      </form>
    </div>
  </div>
</template>

