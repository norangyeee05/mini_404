<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getQnas, saveQnas } from './qnaMockData'
import './member-qna-detail.css'

const route = useRoute()
const router = useRouter()

const sampleQna = {
  id: null,
  title: '7호선 운행시간 문의',
  writer: '관리자',
  views: 31,
  date: '2026.09.30',
  status: '답변완료',
  visibility: '전체',
  category: '운행',
  content: '안녕하세요.\n7호선의 평일 첫차와 막차 시간이 어떻게 되나요?\n자세한 운행시간을 알고 싶습니다.',
  answer: '안녕하세요. 인천교통공사입니다.\n7호선의 평일 첫차는 05:30, 막차는 23:40입니다.\n자세한 운행 시간은 교통 서비스 관리 메뉴에서 확인하실 수 있습니다.',
}

const qna = ref({ ...sampleQna })
const isOwner = computed(() => qna.value.id === null || qna.value.writer === '홍길동')
const isAnswered = computed(() => qna.value.status === '답변완료')
const displayDate = computed(() => qna.value.date.split(' ')[0])

onMounted(() => {
  if (route.query.sample === 'true') {
    const savedSample = localStorage.getItem('memberQnaDetail')
    if (savedSample) qna.value = { ...sampleQna, ...JSON.parse(savedSample) }
    return
  }
  if (!route.query.id) return

  const selectedQna = getQnas().find((item) => String(item.id) === String(route.query.id))
  if (selectedQna) qna.value = { ...selectedQna }
})

function deleteQna() {
  if (!window.confirm('내 문의를 삭제하시겠습니까?')) return

  if (qna.value.id !== null) {
    saveQnas(getQnas().filter((item) => item.id !== qna.value.id))
  }
  router.push('/qna?mine=true')
}
</script>

<template>
  <div class="member-qna-detail-page">
    <PageHeader role="member" user-name="홍길동" />

    <main class="member-qna-detail-main">
      <section class="member-qna-detail-hero" aria-labelledby="member-qna-detail-page-title">
        <div class="member-qna-detail-breadcrumb">HOME <span>›</span> Q&amp;A <span>›</span> 상세</div>
        <h1 id="member-qna-detail-page-title">Q&amp;A</h1>
        <p>궁금하신 내용을 확인하실 수 있습니다.</p>
      </section>

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
        <a class="member-qna-detail-button" href="/qna">목록</a>
        <div v-if="isOwner" class="member-qna-detail-actions__right">
          <button class="member-qna-detail-button" type="button" @click="router.push({ path: '/qna/edit', query: qna.id === null ? { sample: 'true' } : { id: qna.id } })">수정</button>
          <button class="member-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />

  </div>
</template>
