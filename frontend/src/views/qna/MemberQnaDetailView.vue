<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { getQna, deleteQna as removeQna } from './qnaMockData'
import { apiError } from '../../services/backendApi'
import './member-qna-detail.css'

const route = useRoute()
const router = useRouter()

const qna = ref({ id: null, title: '', writer: '', views: 0, date: '', status: '답변대기', visibility: '', category: '', content: '', answer: '' })
const isOwner = computed(() => qna.value.isOwner === true)
const isAnswered = computed(() => qna.value.status === '답변완료')
const displayDate = computed(() => qna.value.date.split(' ')[0])
const deleteStage = ref('')
const accessDenied = ref(false)
const returnQuery = computed(() => ({ ...(route.query.page ? { page: route.query.page } : {}), ...(route.query.mine === 'true' ? { mine: 'true' } : {}) }))
const returnLocation = computed(() => ({ path: '/qna', query: returnQuery.value }))

onMounted(async () => {
  if (!route.query.id) return
  try { qna.value = await getQna(route.query.id) }
  catch (error) {
    if (error.response?.status === 403 || error.status === 403) accessDenied.value = true
    else { window.alert(apiError(error)); router.replace(returnLocation.value) }
  }
})

function closeAccessDenied() {
  accessDenied.value = false
  router.replace(returnLocation.value)
}

function deleteQna() {
  if (!isOwner.value) return
  deleteStage.value = 'confirm'
}

async function confirmDelete() {
  if (deleteStage.value === 'complete') { closeDelete(); return }
  try {
    await removeQna(qna.value.id)
    deleteStage.value = 'complete'
  } catch (error) {
    deleteStage.value = ''
    window.alert(apiError(error))
  }
}

function closeDelete() {
  const completed = deleteStage.value === 'complete'
  deleteStage.value = ''
  if (completed) router.push('/qna?mine=true')
}
</script>

<template>
  <div class="member-qna-detail-page">
    <PageHeader />

    <main class="member-qna-detail-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." stage="상세" />

      <article class="member-qna-detail-card" aria-labelledby="member-qna-detail-title">
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
        <RouterLink class="member-qna-detail-button" :to="returnLocation">목록</RouterLink>
        <div v-if="isOwner" class="member-qna-detail-actions__right">
          <button class="member-qna-detail-button" type="button" @click="router.push({ path: '/qna/edit', query: { id: qna.id } })">수정</button>
          <button class="member-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
    <div v-if="accessDenied" class="qna-access-backdrop" role="presentation"><section class="qna-access-dialog" role="alertdialog" aria-labelledby="qna-access-title" aria-describedby="qna-access-description"><svg class="qna-access-icon" viewBox="0 0 100 100" aria-hidden="true"><circle cx="50" cy="50" r="47" fill="none" stroke="#ff4b59" stroke-width="5" /><path d="M50 32 V57" stroke="#ff6671" stroke-width="6" /><circle cx="50" cy="65" r="3.5" fill="#ff6671" /></svg><h2 id="qna-access-title">접근 권한이 없습니다.</h2><p id="qna-access-description">본인이 작성한 글이 아니므로 공개가 불가합니다.</p><button type="button" class="qna-access-confirm" @click="closeAccessDenied">확인</button></section></div>
  </div>
</template>

<style scoped>
.qna-access-backdrop { position: fixed; inset: 0; z-index: 1000; display: grid; place-items: center; background: rgba(67,84,105,.4); }
.qna-access-dialog { width: 650px; max-width: calc(100vw - 32px); padding: 42px 40px 50px; border-radius: 28px; background: #fff; color: #000; text-align: center; box-shadow: 0 12px 50px #0003; }
.qna-access-icon { display: block; width: 100px; height: 100px; margin: 0 auto; }
.qna-access-dialog h2 { margin: 40px 0 20px; font-size: 30px; }
.qna-access-dialog p { margin: 0; color: #858585; font-size: 19px; font-weight: 600; }
.qna-access-confirm { display: block; width: min(400px, 100%); height: 42px; margin: 40px auto 0; border: 0; border-radius: 8px; background: #eee; color: #000; font-size: 16px; cursor: pointer; }
.qna-access-confirm:hover { background: #ddd; }
@media (max-width: 600px) { .qna-access-dialog { padding: 36px 20px; } .qna-access-dialog h2 { font-size: 24px; } .qna-access-dialog p { font-size: 16px; } }
</style>
