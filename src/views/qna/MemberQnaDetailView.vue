<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQna, deleteQna as removeQna } from './qnaMockData'
import { currentMember } from '../../services/auth'
import { apiError } from '../../services/backendApi'
import './member-qna-detail.css'

const route = useRoute()
const router = useRouter()

const qna = ref({ id: null, title: '', writer: '', views: 0, date: '', status: '답변대기', visibility: '', category: '', content: '', answer: '' })
const isOwner = computed(() => Boolean(qna.value.id) && qna.value.writer === currentMember.value?.name)
const isAnswered = computed(() => qna.value.status === '답변완료')
const displayDate = computed(() => qna.value.date.split(' ')[0])

onMounted(async () => {
  if (!route.query.id) return
  try { qna.value = await getQna(route.query.id) }
  catch (error) { console.error(apiError(error)) }
})

async function deleteQna() {
  if (!isOwner.value) return
  if (!window.confirm('내 문의를 삭제하시겠습니까?')) return

  try { await removeQna(qna.value.id); router.push('/qna?mine=true') }
  catch (error) { window.alert(apiError(error)) }
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
        <RouterLink class="member-qna-detail-button" to="/qna">목록</RouterLink>
        <div v-if="isOwner" class="member-qna-detail-actions__right">
          <button class="member-qna-detail-button" type="button" @click="router.push({ path: '/qna/edit', query: { id: qna.id } })">수정</button>
          <button class="member-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />

  </div>
</template>
