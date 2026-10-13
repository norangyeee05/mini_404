<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getNotices, getNoticeById, saveNotices } from './noticeMockData'
import './admin-notice-create.css'

const props = defineProps({
  mode: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value),
  },
})

const router = useRouter()
const route = useRoute()
const noticeId = route.query.id
const listNotice = props.mode === 'edit' && noticeId ? getNoticeById(noticeId) : null
const savedNotice = props.mode === 'edit' && !noticeId ? readSavedNotice() : null
const title = ref(listNotice?.title || savedNotice?.title || (props.mode === 'edit' ? '7호선 일부 구간 점검 안내' : ''))
const category = ref(listNotice?.category || savedNotice?.category || '시설')
const content = ref(
  listNotice?.content || savedNotice?.content || (props.mode === 'edit'
    ? '안녕하세요. 인천교통공사입니다.\n\n7호선 일부 구간의 시설 점검으로 인해\n아래와 같이 운행이 조정될 예정입니다.\n\n점검 일시: 2026.10.03 01:00 ~ 04:00\n대상 구간: 7호선 00역 ~ 00역\n\n이용에 참고하시기 바랍니다.'
    : ''),
)

function readSavedNotice() {
  try {
    return JSON.parse(localStorage.getItem('adminNoticeDetail') || 'null')
  } catch {
    return null
  }
}

const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)

function submitNotice() {
  if (props.mode !== 'edit') return

  const updatedNotice = {
    title: title.value,
    category: category.value,
    content: content.value,
  }

  if (noticeId) {
    const notices = getNotices().map((notice) => (
      String(notice.id) === String(noticeId) ? { ...notice, ...updatedNotice } : notice
    ))
    saveNotices(notices)
    router.push('/admin/notices')
    return
  }

  localStorage.setItem('adminNoticeDetail', JSON.stringify(updatedNotice))
  router.push('/admin/notices/detail')
}
</script>

<template>
  <div class="admin-notice-create-page">
    <PageHeader role="admin" />

    <main class="admin-notice-create-main">
      <section class="admin-notice-create-hero" aria-labelledby="admin-notice-create-heading">
        <div class="admin-notice-create-breadcrumb">HOME <span>›</span> 공지사항 <span>›</span> {{ props.mode === 'edit' ? '수정' : '등록' }}</div>
        <h1 id="admin-notice-create-heading">공지사항</h1>
        <p>교통서비스 관련 주요 내용을 확인하실 수 있습니다.</p>
      </section>

      <form id="admin-notice-form" class="admin-notice-form" @submit.prevent="submitNotice">
        <div class="admin-notice-form__title-row">
          <label class="admin-notice-form__heading" for="notice-title">제목 <span>*</span></label>
          <label class="visually-hidden" for="notice-category">공지 분류</label>
          <select id="notice-category" v-model="category" class="admin-notice-form__category">
            <option>시설</option>
            <option>운행</option>
            <option>일반</option>
          </select>
        </div>

        <input
          id="notice-title"
          v-model="title"
          class="admin-notice-form__title-input"
          type="text"
          maxlength="200"
          placeholder="제목을 입력하세요."
          required
        />
        <p class="admin-notice-form__count">{{ titleLength }}/200자</p>

        <label class="admin-notice-form__heading admin-notice-form__content-heading" for="notice-content">
          내용 <span>*</span>
        </label>
        <textarea
          id="notice-content"
          v-model="content"
          class="admin-notice-form__content-input"
          maxlength="200"
          placeholder="내용을 입력하세요."
          required
        ></textarea>
        <p class="admin-notice-form__count">{{ contentLength }}/200자</p>
      </form>

      <div class="admin-notice-create-actions">
        <a class="admin-notice-create-button" href="/admin/notices">목록</a>
        <div class="admin-notice-create-actions__right">
          <a class="admin-notice-create-button" :href="props.mode === 'edit' && !noticeId ? '/admin/notices/detail' : '/admin/notices'">취소</a>
          <button class="admin-notice-create-button" type="submit" form="admin-notice-form">{{ props.mode === 'edit' ? '저장' : '등록' }}</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
