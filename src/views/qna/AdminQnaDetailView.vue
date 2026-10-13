<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQnas, saveQnas } from './qnaMockData'
import './admin-qna-detail.css'

const route = useRoute()
const router = useRouter()

const sampleQna = {
  id: 0,
  title: '7호선 운행시간 문의',
  writer: '관리자',
  views: 31,
  date: '2026.09.30',
  status: '답변완료',
  content: '안녕하세요.\n7호선의 평일 첫차와 막차 시간이 어떻게 되나요?\n자세한 운행시간을 알고 싶습니다.',
  answer: '안녕하세요. 인천교통공사입니다.\n7호선의 평일 첫차는 05:30, 막차는 23:40입니다.\n자세한 운행 시간은 교통 서비스 관리 메뉴에서 확인하실 수 있습니다.',
}

const qna = ref({ ...sampleQna })
const isAnswered = computed(() => qna.value.status === '답변완료')

onMounted(() => {
  if (!route.query.id) return

  const selectedQna = getQnas().find((item) => String(item.id) === String(route.query.id))
  if (selectedQna) qna.value = { ...selectedQna }
})

function editQna() {
  router.push({ path: '/admin/qna', query: { edit: qna.value.id } })
}

function deleteQna() {
  if (!window.confirm('이 Q&A를 삭제하시겠습니까?')) return

  saveQnas(getQnas().filter((item) => item.id !== qna.value.id))
  router.push('/admin/qna')
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
