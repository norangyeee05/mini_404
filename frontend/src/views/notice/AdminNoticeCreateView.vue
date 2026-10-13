<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { createNotice, getNotice, updateNotice } from '../../api/notices'
import { errorMessage } from '../../api/client'
import { normalizeNotice } from '../../api/normalize'
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
const title = ref('')
const category = ref('시설')
const content = ref('')
const message = ref('')
const saving = ref(false)

const titleLength = computed(() => title.value.length)
const contentLength = computed(() => content.value.length)

onMounted(async () => {
  if (props.mode !== 'edit' || !noticeId) return
  try {
    const item = normalizeNotice(await getNotice(noticeId))
    title.value = item.title
    content.value = item.content
  } catch (error) { message.value = errorMessage(error, '공지사항을 불러오지 못했습니다.') }
})

async function submitNotice() {
  message.value = ''
  saving.value = true
  try {
    const body = { title: title.value, content: content.value }
    if (props.mode === 'edit') {
      if (!noticeId) throw new Error('수정할 공지사항 번호가 없습니다.')
      await updateNotice(noticeId, body)
    } else await createNotice(body)
    router.push('/admin/notices')
  } catch (error) { message.value = errorMessage(error, '공지사항을 저장하지 못했습니다.') }
  finally { saving.value = false }
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
        <p v-if="message" role="alert">{{ message }}</p>
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
          <button class="admin-notice-create-button" type="submit" form="admin-notice-form" :disabled="saving">{{ props.mode === 'edit' ? '저장' : '등록' }}</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
