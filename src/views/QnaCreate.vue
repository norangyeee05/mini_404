<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import PageBanner from '../components/PageBanner.vue'
import { questions } from '../services/mainData'
import { currentMember } from '../services/mockAuth'
const router = useRouter()
const form = reactive({ title: '', content: '' })
function submit() {
  if (!form.title.trim() || !form.content.trim()) return
  const id = Math.max(100, ...questions.map(item => item.id)) + 1
  const date = new Intl.DateTimeFormat('sv-SE', { timeZone: 'Asia/Seoul', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).format(new Date()).replaceAll('-', '.')
  questions.unshift({ id, title: form.title.trim(), content: form.content.trim(), author: currentMember.value.name, views: 0, date })
  router.push(`/qna/${id}`)
}
</script>
<template><div class="qna-create-shell"><PageHeader /><main><PageBanner title="Q&A" description="궁금하신 내용을 문의하실 수 있습니다." /><form class="qna-create-form" @submit.prevent="submit"><h2>문의/요청 등록</h2><label for="question-title">제목</label><input id="question-title" v-model="form.title" required maxlength="100" pattern=".*\S.*" placeholder="제목을 입력하세요" /><label for="question-content">내용</label><textarea id="question-content" v-model="form.content" required rows="10" placeholder="문의 내용을 입력하세요" /><div class="qna-create-actions"><RouterLink to="/qna">취소</RouterLink><button type="submit">등록</button></div></form></main><PageFooter /></div></template>
<style scoped>
.qna-create-shell { display: flex; flex-direction: column; min-height: 100svh; width: min(1440px, 100%); margin: 0 auto; padding: 0 34px; background: #e2ecee; } main { flex: 1; }.qna-create-form { display: grid; gap: 14px; margin-top: 24px; padding: 28px; border-radius: 22px; background: #f0f5f7; } h2 { margin: 0; color: #155369; } label { font-weight: 600; } input, textarea { width: 100%; padding: 12px; border: 2px solid #c6dadd; border-radius: 8px; background: #fff; color: #333; font: inherit; } textarea { resize: vertical; }.qna-create-actions { display: flex; justify-content: flex-end; gap: 16px; align-items: center; } button { padding: 10px 24px; border: 0; border-radius: 8px; background: #457384; color: #fff; } @media (max-width: 760px) { .qna-create-shell { padding: 0 16px; }.qna-create-form { padding: 20px; } }
</style>
