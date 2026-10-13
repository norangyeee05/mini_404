<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { deleteQna as requestDeleteQna, getQna } from '../../api/qna'
import { errorMessage } from '../../api/client'
import { normalizeQna } from '../../api/normalize'
import './member-qna-detail.css'

const route = useRoute()
const router = useRouter()

const qna = ref({ id: null, title: '', writer: '', views: 0, date: '', status: '', content: '', answer: '', isOwner: false })
const showDeleteConfirm = ref(false)
const showDeleteComplete = ref(false)
const isOwner = computed(() => qna.value.isOwner === true || qna.value.ownedByCurrentUser === true)
const isAnswered = computed(() => ['답변완료', 'ANSWERED', 'COMPLETED'].includes(qna.value.status))
const displayDate = computed(() => String(qna.value.date || '').split(' ')[0])
const message = ref('')
const loading = ref(true)

onMounted(async () => {
  const id = route.params.qnaId || route.query.id
  if (!id) { message.value = 'Q&A 번호가 없습니다.'; loading.value = false; return }
  try { qna.value = normalizeQna(await getQna(id)) }
  catch (error) { message.value = errorMessage(error, 'Q&A를 불러오지 못했습니다.') }
  finally { loading.value = false }
})

function deleteQna() {
  showDeleteConfirm.value = true
}

async function confirmDeleteQna() {
  try { await requestDeleteQna(qna.value.id) }
  catch (error) { message.value = errorMessage(error, 'Q&A를 삭제하지 못했습니다.'); showDeleteConfirm.value = false; return }
  showDeleteConfirm.value = false
  showDeleteComplete.value = true
}

function closeDeleteConfirm() {
  showDeleteConfirm.value = false
}

function closeDeleteComplete() {
  showDeleteComplete.value = false
  router.push('/qna/my')
}
</script>

<template>
  <div class="member-qna-detail-page">
    <PageHeader role="member" />

    <main class="member-qna-detail-main">
      <section class="member-qna-detail-hero" aria-labelledby="member-qna-detail-page-title">
        <div class="member-qna-detail-breadcrumb">HOME <span>›</span> Q&amp;A <span>›</span> 상세</div>
        <h1 id="member-qna-detail-page-title">Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

      <article class="member-qna-detail-card" aria-labelledby="member-qna-detail-title">
        <p v-if="loading" role="status">불러오는 중...</p>
        <p v-if="message" role="alert">{{ message }}</p>
        <div class="member-qna-detail-heading">
          <h2 id="member-qna-detail-title">{{ qna.title }}</h2>
          <span :class="['member-qna-detail-status', { 'is-pending': !isAnswered }]">{{ qna.status }}</span>
        </div>

        <dl class="member-qna-detail-meta">
          <div><dt>작성자</dt><dd>{{ qna.writer }}</dd></div>
          <div><dt>조회수</dt><dd>{{ qna.views }}</dd></div>
          <div><dt>등록일</dt><dd>{{ displayDate }}</dd></div>
        </dl>

        <section class="member-qna-message member-qna-message--question" aria-labelledby="member-qna-question-heading">
          <h3 id="member-qna-question-heading">질문 내용</h3>
          <p>{{ qna.content }}</p>
        </section>

        <section v-if="isAnswered" class="member-qna-message member-qna-message--answer" aria-labelledby="member-qna-answer-heading">
          <h3 id="member-qna-answer-heading">관리자 답변</h3>
          <p>{{ qna.answer }}</p>
        </section>
        <section v-else class="member-qna-message member-qna-message--pending" aria-label="답변 대기">
          <h3>관리자 답변</h3>
          <p>답변을 준비하고 있습니다.</p>
        </section>
      </article>

      <div class="member-qna-detail-actions">
        <a class="member-qna-detail-button" href="/qna">목록</a>
        <div v-if="isOwner" class="member-qna-detail-actions__right">
          <button class="member-qna-detail-button" type="button" @click="router.push(`/qna/${qna.id}/edit`)">수정</button>
          <button class="member-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />

    <div v-if="showDeleteConfirm" class="qna-delete-modal-backdrop" @click.self="closeDeleteConfirm">
      <section class="qna-delete-modal qna-delete-confirm-modal" role="dialog" aria-modal="true" aria-labelledby="qna-delete-confirm-title">
        <button class="qna-delete-modal__close" type="button" aria-label="닫기" @click="closeDeleteConfirm">×</button>
        <div class="qna-delete-modal__icon qna-delete-modal__icon--warning" aria-hidden="true">
          <svg viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="29" fill="none" stroke="currentColor" stroke-width="4" />
            <path d="M32 18v18m0 9v1" fill="none" stroke="currentColor" stroke-width="5" stroke-linecap="round" />
          </svg>
        </div>
        <h2 id="qna-delete-confirm-title">삭제하시겠습니까?</h2>
        <p>삭제 후 복구가 불가합니다.<br />진짜로 삭제하시겠습니까?</p>
        <button class="qna-delete-modal__confirm" type="button" @click="confirmDeleteQna">확인</button>
      </section>
    </div>

    <div v-if="showDeleteComplete" class="qna-delete-modal-backdrop" @click.self="closeDeleteComplete">
      <section class="qna-delete-modal" role="dialog" aria-modal="true" aria-labelledby="qna-delete-title">
        <button class="qna-delete-modal__close" type="button" aria-label="닫기" @click="closeDeleteComplete">×</button>
        <div class="qna-delete-modal__icon" aria-hidden="true">
          <svg viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="29" fill="none" stroke="currentColor" stroke-width="4" />
            <path d="m19 33 9 10 18-23" fill="none" stroke="currentColor" stroke-width="5" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </div>
        <h2 id="qna-delete-title">삭제되었습니다.</h2>
        <button class="qna-delete-modal__confirm" type="button" @click="closeDeleteComplete">확인</button>
      </section>
    </div>

  </div>
</template>
