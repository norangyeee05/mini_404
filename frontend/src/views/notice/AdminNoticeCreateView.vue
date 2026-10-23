<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import PageBanner from '../../components/PageBanner.vue'
import { getNoticeById, createNotice, updateNotice } from './noticeMockData'
import { apiError } from '../../services/backendApi'
const props = defineProps({ mode: { type: String, default: 'create' } })
const route = useRoute()
const router = useRouter()
const editing = computed(() => props.mode === 'edit')
const existing = ref(null)
const title = ref('')
const content = ref('')
const category = ref('일반')
const selectedFiles = ref([])
const fileInput = ref(null)
const error = ref('')
const saving = ref(false)
watch(() => route.fullPath, async () => {
  existing.value = null
  title.value = existing.value?.title || ''
  content.value = existing.value?.content || ''
  category.value = existing.value?.category || '일반'
  selectedFiles.value = []
  error.value = ''
  if (fileInput.value) fileInput.value.value = ''
  if (editing.value && route.query.id) {
    try { existing.value = await getNoticeById(route.query.id, true); title.value = existing.value.title || ''; content.value = existing.value.content || '' }
    catch (exception) { error.value = apiError(exception) }
  }
}, { immediate: true })
function chooseFile(event) {
  error.value = ''
  const files = [...(event.target.files || [])]
  const oversized = files.find(file => file.size > 20 * 1024 * 1024)
  if (oversized) { error.value = '첨부파일은 파일당 20MB 이하로 선택해 주세요.'; event.target.value = ''; return }
  selectedFiles.value.push(...files)
  event.target.value = ''
}
function removeFile(index) { selectedFiles.value.splice(index, 1) }
async function submitNotice() {
  if (saving.value) return
  error.value = ''
  if (!title.value.trim() || !content.value.trim()) { error.value = '제목과 내용을 입력해 주세요.'; return }
  if (editing.value && !existing.value) { error.value = '해당 공지사항을 찾을 수 없습니다.'; return }
  saving.value = true
  try {
    const body = { title: title.value.trim(), content: content.value.trim() }
    const result = editing.value ? await updateNotice(route.query.id, body, selectedFiles.value) : await createNotice(body, selectedFiles.value)
    const id = result?.noticeId ?? route.query.id
    router.push({ path: '/admin/notices/detail', query: { id } })
  } catch (exception) { error.value = apiError(exception) }
  finally { saving.value = false }
}
const cancelPath = computed(() => editing.value ? { path: '/admin/notices/detail', query: { id: route.query.id } } : '/admin/notices')
</script>
<template>
  <div class="admin-notice-editor-shell"><PageHeader /><main>
    <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하실 수 있습니다." :stage="editing ? '수정' : '등록'" />
    <form id="notice-editor" class="notice-editor" @submit.prevent="submitNotice">
      <template v-if="!editing || existing">
        <label class="editor-label" for="notice-title">제목 <span>*</span></label>
        <input id="notice-title" v-model="title" maxlength="200" required placeholder="제목을 입력하세요." /><p class="character-count">{{ title.length }}/200자</p>
        <label class="editor-label" for="notice-content">내용 <span>*</span></label>
        <textarea id="notice-content" v-model="content" maxlength="200" required placeholder="내용을 입력하세요." /><p class="character-count">{{ content.length }}/200자</p>
        <label class="file-label" for="notice-file">첨부파일(선택사항)</label>
        <label class="file-picker" for="notice-file">파일을 첨부해주세요. (파일당 20MB 이하, 여러 파일 선택 가능)</label>
        <input id="notice-file" ref="fileInput" class="file-input" type="file" multiple :disabled="saving" @change="chooseFile" />
        <ul class="notice-selected-files"><li v-for="(file, index) in existing?.files || []" :key="'old-' + file.fileId">{{ file.fileName }}</li><li v-for="(file, index) in selectedFiles" :key="file.name + file.lastModified">{{ file.name }} <button type="button" :disabled="saving" @click="removeFile(index)">제거</button></li></ul>
      </template><p v-else>해당 공지사항을 찾을 수 없습니다.</p>
      <p v-if="error" class="editor-error" role="alert">{{ error }}</p>
    </form>
    <div class="editor-actions"><RouterLink class="editor-button" to="/admin/notices">목록</RouterLink><div><RouterLink class="editor-button" :to="cancelPath">취소</RouterLink><button v-if="!editing || existing" class="editor-button" type="submit" form="notice-editor" :disabled="saving">{{ saving ? '저장 중...' : editing ? '저장' : '등록' }}</button></div></div>
  </main><PageFooter /></div>
</template>
<style scoped>
.admin-notice-editor-shell { display: flex; flex-direction: column; min-height: 100svh; width: min(1440px, 100%); margin: 0 auto; padding: 0 34px; background: var(--page-background); } main { flex: 1; }.notice-editor { margin-top: 24px; padding: 26px 24px 15px; border-radius: 22px; background: #f0f5f7; }.editor-label { display: block; margin-bottom: 15px; color: #155369; font-size: 32px; font-weight: 700; line-height: 40px; }.editor-label span { color: #f00; } input:not([type=file]), textarea { display: block; width: 100%; padding: 18px 20px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; color: #333; font: inherit; } input:not([type=file]) { height: 65px; } textarea { min-height: 166px; resize: vertical; }.character-count { margin: 7px 8px 8px; text-align: right; color: #858585; font-weight: 600; }.file-label { display: block; margin-top: -22px; color: #155369; font-weight: 600; }.file-picker { display: block; margin-top: 3px; padding: 18px 22px; border-radius: 10px; background: #dbe4e7; color: #427486; font-weight: 600; cursor: pointer; overflow-wrap: anywhere; }.file-input { position: absolute; width: 1px; height: 1px; opacity: 0; }.file-picker:has(+ .file-input:focus-visible) { outline: 2px solid #155369; }.remove-file { margin-top: 8px; border: 0; background: none; color: #427486; text-decoration: underline; }.editor-error { color: #f00; }.editor-actions { display: flex; justify-content: space-between; margin-top: 16px; }.editor-actions > div { display: flex; gap: 16px; }.editor-button { display: inline-flex; align-items: center; justify-content: center; min-width: 84px; min-height: 40px; padding: 8px 20px; border: 0; border-radius: 9px; background: #457384; color: #fff; font-size: 16px; font-weight: 600; } input:focus-visible, textarea:focus-visible, a:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
@media (max-width: 760px) { .admin-notice-editor-shell { padding: 0 16px; }.notice-editor { padding: 20px 16px; }.editor-label { font-size: 26px; }.file-label { margin-top: 0; }.editor-actions > div { gap: 8px; }.editor-button { min-width: 70px; padding: 8px 14px; } }

.editor-button:disabled { opacity: .6; cursor: wait; }
</style>
