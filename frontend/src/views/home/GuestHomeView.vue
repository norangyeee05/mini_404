<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import LoginRequiredModal from '../../components/LoginRequiredModal.vue'
import { getNotices } from '../../api/notices'
import { getQnas } from '../../api/qna'
import { getStations } from '../../api/line7'
import { errorMessage } from '../../api/client'
import { normalizeNotice, normalizeQna } from '../../api/normalize'
import './guest-home.css'

const props = defineProps({
  role: { type: String, default: 'guest' },
})
const router = useRouter()
const showLoginRequired = ref(false)

const stations = ref([])
const notices = ref([])
const qnas = ref([])
const message = ref('')

onMounted(async () => {
  const tasks = [
    getNotices({ page: 1, size: 5, searchType: 'all', keyword: '' }).then((result) => { notices.value = result.items.map(normalizeNotice) }),
    getStations().then((result) => { stations.value = Array.isArray(result) ? result : result?.items || [] }),
  ]
  if (props.role === 'member') tasks.push(getQnas({ page: 1, size: 3, searchType: 'all', keyword: '' }).then((result) => { qnas.value = result.items.map(normalizeQna) }))
  const results = await Promise.allSettled(tasks)
  const failure = results.find((result) => result.status === 'rejected')
  if (failure) message.value = errorMessage(failure.reason, '일부 홈 화면 정보를 불러오지 못했습니다.')
})

function openQna() {
  if (props.role === 'guest') {
    showLoginRequired.value = true
    return
  }
  router.push('/qna')
}
</script>

<template>
  <div class="guest-home-page">
    <PageHeader :role="props.role" />

    <main class="guest-home-main">
      <section class="guest-home-hero" aria-label="인천교통공사 소개">
        <p class="guest-home-eyebrow">INCHEON TRANSIT CORPORATION</p>
        <h1>안전하고 편리한 교통서비스로<br />시민의 행복한 일상을 만들어갑니다.</h1>
      </section>

      <section class="guest-home-stations" aria-labelledby="guest-home-stations-title">
        <h2 id="guest-home-stations-title">역정보</h2>
        <div class="guest-home-station-line">
          <a v-for="station in stations" :key="station.stationId ?? station.id ?? station.name" href="#" class="guest-home-station">{{ station.stationName ?? station.name ?? station }}</a>
        </div>
      </section>

      <div class="guest-home-content-grid">
        <section class="guest-home-panel guest-home-notices" aria-labelledby="guest-home-notices-title">
          <div class="guest-home-panel-heading">
            <h2 id="guest-home-notices-title">공지사항</h2>
            <a href="/notice">전체보기 <span>›</span></a>
          </div>
          <div class="guest-home-table-wrap">
            <table class="guest-home-table guest-home-notice-table">
              <thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th></tr></thead>
              <tbody>
                <tr v-for="notice in notices" :key="notice.id">
                  <td>{{ notice.id }}</td>
                  <td><a :href="`/notice/detail?id=${notice.id}`">{{ notice.title }}</a></td>
                  <td>{{ notice.writer }}</td><td>{{ notice.views }}</td><td>{{ notice.date }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <div class="guest-home-side-column">
          <a class="guest-home-resource-banner" href="#resources">
            <span>더 편리한 교통, 더 좋은 일상</span>
            <strong>인천교통공사<br />자료실</strong>
            <small>바로가기 ›</small>
          </a>

          <section class="guest-home-panel guest-home-qna" aria-labelledby="guest-home-qna-title">
            <div class="guest-home-panel-heading">
              <h2 id="guest-home-qna-title">Q&amp;A</h2>
            <a href="/qna" @click.prevent="openQna">더보기 <span>›</span></a>
            </div>
            <div class="guest-home-table-wrap">
              <table class="guest-home-table">
                <thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th></tr></thead>
                <tbody>
                  <tr v-for="qna in qnas" :key="qna.id">
                    <td>{{ qna.id }}</td><td>{{ qna.title }}</td><td>{{ qna.writer }}</td><td>{{ qna.views }}</td><td>{{ qna.date }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>
    </main>

    <PageFooter />
    <LoginRequiredModal :open="showLoginRequired" @close="showLoginRequired = false" />
  </div>
</template>
