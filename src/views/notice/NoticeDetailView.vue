<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-detail.css'

const props = defineProps({
  role: {
    type: String,
    default: 'guest',
    validator: (value) => ['guest', 'admin'].includes(value),
  },
})

const router = useRouter()

const notice = ref({
  title: '7호선 일부 구간 점검 안내',
  writer: '관리자',
  viewCount: 31,
  createdAt: '2026.09.30',
  introduction: '안녕하세요. 인천교통공사입니다.',
  description: [
    '7호선 일부 구간의 시설 점검으로 인해',
    '아래와 같이 운행이 조정될 예정입니다.',
  ],
  details: [
    '점검 일시: 2026.10.03 01:00 ~ 04:00',
    '대상 구간: 7호선 00역 ~ 00역',
  ],
  closing: '이용에 참고하시기 바랍니다.',
})

onMounted(() => {
  if (props.role !== 'admin') return

  const savedNotice = localStorage.getItem('adminNoticeDetail')
  if (!savedNotice) return

  try {
    const savedData = JSON.parse(savedNotice)
    notice.value.title = savedData.title || notice.value.title
    notice.value.content = savedData.content
  } catch {
    localStorage.removeItem('adminNoticeDetail')
  }
})
</script>

<template>
  <div class="notice-detail-page">
    <PageHeader :role="props.role" />

    <main class="notice-detail-main">
      <section class="notice-detail-hero" aria-labelledby="notice-detail-page-title">
        <div class="notice-detail-breadcrumb">HOME <span>›</span> 공지사항 <span>›</span> 상세</div>
        <h1 id="notice-detail-page-title">공지사항</h1>
        <p>교통서비스 관련 주요 내용을 확인하실 수 있습니다.</p>
      </section>

      <article class="notice-detail-card" aria-labelledby="notice-detail-title">
        <h2 id="notice-detail-title">{{ notice.title }}</h2>

        <dl class="notice-detail-meta">
          <div class="notice-detail-meta__author">
            <dt>작성자</dt>
            <dd>{{ notice.writer }}</dd>
          </div>
          <div class="notice-detail-meta__views">
            <dt>조회수</dt>
            <dd>{{ notice.viewCount }}</dd>
          </div>
          <div class="notice-detail-meta__date">
            <dt>등록일</dt>
            <dd>{{ notice.createdAt }}</dd>
          </div>
        </dl>

        <div
          :class="[
            'notice-detail-content',
            { 'notice-detail-content--admin': props.role === 'admin' },
          ]"
        >
          <p v-if="notice.content" class="notice-detail-content__saved">{{ notice.content }}</p>
          <template v-else>
            <p>{{ notice.introduction }}</p>

            <p>
              {{ notice.description[0] }}<br />
              {{ notice.description[1] }}
            </p>

            <ul>
              <li v-for="detail in notice.details" :key="detail">{{ detail }}</li>
            </ul>

            <p>{{ notice.closing }}</p>
          </template>

          <a
            v-if="props.role === 'admin'"
            class="notice-detail-back-link"
            href="/admin/notices"
          >
            ‹ &nbsp; 목록으로 돌아가기
          </a>
        </div>
      </article>

      <div class="notice-detail-actions">
        <a
          class="notice-detail-list-button"
          :href="props.role === 'admin' ? '/admin/notices' : '/notice'"
        >
          목록
        </a>
        <div v-if="props.role === 'admin'" class="notice-detail-admin-actions">
          <button class="notice-detail-admin-button" type="button" @click="router.push('/admin/notices/edit')">수정</button>
          <button class="notice-detail-admin-button" type="button">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
