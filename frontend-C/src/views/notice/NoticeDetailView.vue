<script setup>
import { onMounted, ref } from 'vue'
import DeleteModal from '../../components/DeleteModal.vue'

import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-detail.css'
import { getNoticeById, deleteNotice as removeNotice } from './noticeMockData'
import { api, apiError } from '../../services/backendApi'

const props = defineProps({
  role: {
    type: String,
    default: 'guest',
    validator: (value) => ['guest', 'admin'].includes(value),
  },
})

const router = useRouter()
const route = useRoute()

const attachmentError = ref('')
async function downloadAttachment(file) {
  try { const blob = await api.notices.download(route.query.id, file.fileId); const url = URL.createObjectURL(blob); const link = document.createElement('a'); link.href = url; link.download = file.fileName; link.click(); URL.revokeObjectURL(url) }
  catch (error) { attachmentError.value = apiError(error) }
}
const notice = ref({
  title: '', writer: '', viewCount: 0, createdAt: '', content: '', introduction: '', description: [], details: [], closing: '',
})

const deleteStage = ref('')
function deleteNotice() {
  if (props.role !== 'admin') return
  deleteStage.value = 'confirm'
}
async function confirmDelete() {
  if (deleteStage.value === 'complete') { closeDelete(); return }
  if (props.role !== 'admin') return
  try { await removeNotice(route.query.id); deleteStage.value = 'complete' }
  catch (error) { attachmentError.value = apiError(error); deleteStage.value = '' }
}
function closeDelete() {
  const completed = deleteStage.value === 'complete'
  deleteStage.value = ''
  if (completed) router.push('/admin/notices')
}
onMounted(async () => {
  if (!route.query.id) return
  try { notice.value = await getNoticeById(route.query.id) }
  catch (error) { attachmentError.value = apiError(error) }
})
</script>

<template>
  <div class="notice-detail-page">
    <PageHeader />

    <main class="notice-detail-main">
      <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하실 수 있습니다." stage="상세" />

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

          <RouterLink
            v-if="props.role === 'admin'"
            class="notice-detail-back-link"
            to="/admin/notices"
          >
            ‹ &nbsp; 목록으로 돌아가기
          </RouterLink>
        </div>
      <section v-if="notice.files?.length" class="notice-attachment"><h3>첨부파일</h3><div v-for="file in notice.files" :key="file.fileId"><span>{{ file.fileName }}</span><button class="notice-detail-admin-button" type="button" @click="downloadAttachment(file)">↓ 다운로드</button></div><p v-if="attachmentError" role="alert">{{ attachmentError }}</p></section>
      </article>

      <div class="notice-detail-actions">
        <RouterLink class="notice-detail-list-button" :to="props.role === 'admin' ? '/admin/notices' : '/notice'"
        >
          목록
        </RouterLink>
        <div v-if="props.role === 'admin'" class="notice-detail-admin-actions">
          <button class="notice-detail-admin-button" type="button" @click="router.push({ path: '/admin/notices/edit', query: { id: route.query.id } })">수정</button>
          <button class="notice-detail-admin-button" type="button" @click="deleteNotice">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>

<style scoped>
.notice-attachment { margin-top: 24px; padding: 18px 22px; border-radius: 10px; background: #e6eef0; color: #427486; }.notice-attachment h3 { margin: 0 0 10px; font-size: 16px; }.notice-attachment > div { display: flex; align-items: center; gap: 16px; justify-content: space-between; }.notice-attachment span { overflow-wrap: anywhere; min-width: 0; }.notice-attachment button { flex-shrink: 0; }@media(max-width:640px){.notice-attachment > div { align-items: flex-start; flex-direction: column; }}
</style>
