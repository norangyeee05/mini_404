<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getNotice, deleteNotice } from '../../api/notices'
import { errorMessage } from '../../api/client'
import { normalizeNotice } from '../../api/normalize'
import './notice-detail.css'

const props = defineProps({
  role: {
    type: String,
    default: 'guest',
    validator: (value) => ['guest', 'admin'].includes(value),
  },
})

const router = useRouter()
const route = useRoute()
const message = ref('')
const loading = ref(true)

const notice = ref({ title: '', writer: '', viewCount: 0, createdAt: '', content: '' })

onMounted(async () => {
  const id = route.params.noticeId || route.query.id
  if (!id) { message.value = '공지사항 번호가 없습니다.'; loading.value = false; return }
  try { notice.value = normalizeNotice(await getNotice(id)) }
  catch (error) { message.value = errorMessage(error, '공지사항을 불러오지 못했습니다.') }
  finally { loading.value = false }
})

async function removeNotice() {
  if (!window.confirm('이 공지사항을 삭제하시겠습니까?')) return
  try { await deleteNotice(notice.value.id); router.push('/admin/notices') }
  catch (error) { message.value = errorMessage(error, '공지사항을 삭제하지 못했습니다.') }
}
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
        <p v-if="loading" role="status">불러오는 중...</p>
        <p v-if="message" role="alert">{{ message }}</p>
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
          <p v-else>{{ notice.content }}</p>

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
          :href="props.role === 'admin' ? '/admin/notices' : '/notices'"
        >
          목록
        </a>
        <div v-if="props.role === 'admin'" class="notice-detail-admin-actions">
          <button class="notice-detail-admin-button" type="button" @click="router.push({ path: '/admin/notices/edit', query: { id: notice.id } })">수정</button>
          <button class="notice-detail-admin-button" type="button" @click="removeNotice">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
  </div>
</template>
