<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import BoardCard from './main/components/BoardCard.vue'
import DeleteModal from '../components/DeleteModal.vue'
import { notices, questions, stations, deleteQuestion } from './main/services/mainData'
import { currentMember } from '../services/mockAuth'
const route = useRoute()
const router = useRouter()
const deleteStage = ref('')
const deletedItem = ref(null)
const isQna = computed(() => route.path.startsWith('/qna/'))
watch(() => route.fullPath, () => { deleteStage.value = ''; deletedItem.value = null })
function confirmDelete() {
  if (deleteStage.value === 'complete') { deleteStage.value = ''; router.push('/qna'); return }
  if (!item.value) { deleteStage.value = ''; return }
  const snapshot = { ...item.value }
  if (deleteQuestion(route.params.id)) { deletedItem.value = snapshot; deleteStage.value = 'complete' }
}

const title = computed(() => route.meta.title)
const rows = computed(() => route.path.startsWith('/notices') ? notices : questions)
const item = computed(() => rows.value.find(row => String(row.id) === route.params.id) ?? deletedItem.value)
</script>
<template>
  <div class="content-shell"><PageHeader /><main class="content-main"><h1>{{ title }}</h1>
    <template v-if="route.meta.kind === 'board'">
      <BoardCard v-if="!route.params.id" :title="title" :rows="rows" :to="route.path" />
      <article v-else class="content-panel"><template v-if="item"><h2>{{ item.title }}</h2><p>{{ item.author }} · {{ item.date }}</p><p class="dummy-body">{{ item.content || `${item.title}에 대한 화면 확인용 더미 내용입니다.` }}</p><button v-if="isQna && !deletedItem" class="qna-delete-button" type="button" @click="deleteStage = 'confirm'">삭제</button></template><p v-else>해당 게시글을 찾을 수 없습니다.</p><RouterLink :to="route.path.split('/').slice(0, 2).join('/')">목록으로 돌아가기</RouterLink></article>
    </template>
    <section v-else-if="route.meta.kind === 'rail'" class="content-panel"><nav class="station-links" aria-label="역 선택"><RouterLink v-for="station in stations" :key="station" :to="{ path: '/rail-info', query: { station } }">{{ station }}</RouterLink></nav><h2>{{ stations.includes(route.query.station) ? route.query.station : '석남' }}역</h2><p>7호선 역정보 · 첫차 05:30 · 막차 23:50</p><p>운행 시간은 화면 확인용 더미 데이터입니다.</p></section>
    <section v-else-if="route.meta.kind === 'mypage'" class="content-panel"><template v-if="currentMember"><h2>{{ currentMember.name }}님</h2><p>회원번호: {{ currentMember.memberId }}</p></template><template v-else><p>로그인이 필요합니다.</p><RouterLink to="/login">로그인 하기</RouterLink></template></section>
    <section v-else class="content-panel"><h2>이용 안내 자료</h2><p>교통카드 이용 안내, 노선 안내 및 시설 이용 안내를 제공하는 더미 자료실입니다.</p></section>
    <RouterLink class="home-link" to="/">메인으로 돌아가기</RouterLink>
  </main><PageFooter /><DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="deleteStage = ''" /></div>
</template>
<style scoped>
.qna-delete-button { display: block; min-width: 84px; min-height: 40px; margin: 20px 0 16px auto; padding: 8px 20px; border: 0; border-radius: 9px; background: #457384; color: #fff; font-size: 16px; font-weight: 700; }.qna-delete-button:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
.content-shell { display: flex; flex-direction: column; min-height: 100svh; width: min(1440px, 100%); margin: 0 auto; padding: 0 34px; background: var(--page-background); }.content-main { flex: 1; padding: 32px 0; } h1 { color: #155369; font-size: 32px; }.content-panel { padding: 28px; border-radius: 20px; background: #f0f5f7; line-height: 1.8; } h2 { color: #155369; }.dummy-body { margin: 32px 0; white-space: pre-line; }.home-link { display: inline-block; margin-top: 24px; }.content-panel a, .home-link { text-decoration: underline; }.station-links { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 24px; }
@media (max-width: 600px) { .content-shell { padding: 0 16px; }.content-panel { padding: 20px; } }
</style>



