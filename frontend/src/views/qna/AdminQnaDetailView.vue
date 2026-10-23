<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { getQna, deleteQna as removeQna } from './qnaMockData'
import { apiError } from '../../services/backendApi'
import './admin-qna-detail.css'

const route = useRoute()
const router = useRouter()

const qna = ref({ id: null, title: '', writer: '', views: 0, date: '', status: '답변대기', content: '', answer: '' })
const isAnswered = computed(() => qna.value.status === '답변완료')

onMounted(async () => {
  if (!route.query.id) return
  try { qna.value = await getQna(route.query.id, true) } catch (error) { console.error(apiError(error)) }
})

function editQna() {
  router.push({ path: '/admin/qna', query: { edit: qna.value.id } })
}

const deleteStage = ref('')
function deleteQna() {
  deleteStage.value = 'confirm'
}
async function confirmDelete() {
  if (deleteStage.value === 'complete') { closeDelete(); return }
  try { await removeQna(qna.value.id, true); deleteStage.value = 'complete' }
  catch (error) { window.alert(apiError(error)); deleteStage.value = '' }
}
function closeDelete() {
  const completed = deleteStage.value === 'complete'
  deleteStage.value = ''
  if (completed) router.push('/admin/qna')
}
</script>

<template>
  <div class="admin-qna-detail-page">
    <PageHeader />

    <main class="admin-qna-detail-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." stage="상세" />

      <article class="admin-qna-detail-card" aria-labelledby="admin-qna-detail-title">
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
        <RouterLink class="admin-qna-detail-button" to="/admin/qna">목록</RouterLink>
        <div>
          <button class="admin-qna-detail-button" type="button" @click="editQna">수정</button>
          <button class="admin-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>
