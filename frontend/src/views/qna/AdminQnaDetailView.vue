<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { deleteAdminQna, getAdminQna } from '../../api/qna'
import { errorMessage } from '../../api/client'
import { normalizeQna } from '../../api/normalize'
import './admin-qna-detail.css'

const route = useRoute()
const router = useRouter()

const qna = ref({ id: null, title: '', writer: '', views: 0, date: '', status: '', content: '', answer: '' })
const message = ref('')
const loading = ref(true)
const isAnswered = computed(() => ['답변완료', 'ANSWERED', 'COMPLETED'].includes(qna.value.status))

onMounted(async () => {
  const id = route.params.qnaId || route.query.id
  if (!id) { message.value = 'Q&A 번호가 없습니다.'; loading.value = false; return }
  try { qna.value = normalizeQna(await getAdminQna(id)) }
  catch (error) { message.value = errorMessage(error, 'Q&A를 불러오지 못했습니다.') }
  finally { loading.value = false }
})

function editQna() {
  router.push({ path: '/admin/qna', query: { edit: qna.value.id } })
}

async function deleteQna() {
  if (!window.confirm('이 Q&A를 삭제하시겠습니까?')) return
  try { await deleteAdminQna(qna.value.id); router.push('/admin/qna') }
  catch (error) { message.value = errorMessage(error, 'Q&A를 삭제하지 못했습니다.') }
}
</script>

<template>
  <div class="admin-qna-detail-page">
    <PageHeader role="admin" />

    <main class="admin-qna-detail-main">
      <section class="admin-qna-detail-hero" aria-labelledby="admin-qna-detail-page-title">
        <div class="admin-qna-detail-breadcrumb">HOME <span>›</span> Q&amp;A <span>›</span> 상세</div>
        <h1 id="admin-qna-detail-page-title">Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

      <article class="admin-qna-detail-card" aria-labelledby="admin-qna-detail-title">
        <p v-if="loading" role="status">불러오는 중...</p>
        <p v-if="message" role="alert">{{ message }}</p>
        <div class="admin-qna-detail-heading">
          <h2 id="admin-qna-detail-title">{{ qna.title }}</h2>
          <span :class="['admin-qna-detail-status', { 'is-pending': !isAnswered }]">{{ qna.status }}</span>
        </div>

        <dl class="admin-qna-detail-meta">
          <div><dt>작성자</dt><dd>{{ qna.writer }}</dd></div>
          <div><dt>조회수</dt><dd>{{ qna.views }}</dd></div>
          <div><dt>등록일</dt><dd>{{ qna.date }}</dd></div>
        </dl>

        <section class="admin-qna-message admin-qna-message--question" aria-labelledby="qna-question-heading">
          <h3 id="qna-question-heading">질문 내용</h3>
          <p>{{ qna.content }}</p>
        </section>

        <section v-if="isAnswered" class="admin-qna-message admin-qna-message--answer" aria-labelledby="qna-answer-heading">
          <h3 id="qna-answer-heading">관리자 답변</h3>
          <p>{{ qna.answer }}</p>
        </section>
        <section v-else class="admin-qna-message admin-qna-message--pending" aria-label="답변 대기">
          <h3>관리자 답변</h3>
          <p>아직 등록된 답변이 없습니다.</p>
        </section>
      </article>

      <div class="admin-qna-detail-actions">
        <a class="admin-qna-detail-button" href="/admin/qna">목록</a>
        <div>
          <button class="admin-qna-detail-button" type="button" @click="editQna">수정</button>
          <button class="admin-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
