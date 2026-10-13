<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Resource from './Resource.vue'
import PageHeader from '../components/PageHeader.vue'
import PageFooter from '../components/PageFooter.vue'
import PageBanner from '../components/PageBanner.vue'
import { resources, saveResource } from '../services/resourceData'
const route = useRoute()
const router = useRouter()
const editing = computed(() => route.name === 'admin-resource-edit')
const formMode = computed(() => editing.value || route.name === 'admin-resource-new')
const form = reactive({ title: '', content: '', attachment: null })
const error = ref('')
const fileInput = ref(null)
const existing = computed(() => resources.find(item => String(item.id) === route.params.resourceId))
watch(() => route.fullPath, () => {
  error.value = ''
  form.title = editing.value ? existing.value?.title ?? '' : ''
  form.content = editing.value ? existing.value?.content ?? '' : ''
  form.attachment = editing.value ? existing.value?.attachment ?? null : null
  if (fileInput.value) fileInput.value.value = ''
}, { immediate: true })
function chooseFile(event) {
  error.value = ''
  const file = event.target.files[0]
  if (file?.size > 10 * 1024 * 1024) { error.value = '첨부파일은 10MB 이하로 선택해 주세요.'; event.target.value = ''; return }
  if (file) form.attachment = file
}
function submit() {
  error.value = ''
  try { const id = saveResource({ ...form }, editing.value ? route.params.resourceId : undefined); router.push(`/admin/resources/${id}`) }
  catch (exception) { error.value = exception.message }
}
const cancelPath = computed(() => editing.value ? `/admin/resources/${route.params.resourceId}` : '/admin/resources')
</script>
<template>
  <Resource v-if="!formMode" admin />
  <div v-else class="admin-resource-shell"><PageHeader /><main>
    <PageBanner title="자료실" description="교통서비스 관련 자료를 확인하실 수 있습니다." :stage="editing ? '수정' : '등록'" />
    <form id="resource-editor" class="resource-editor" @submit.prevent="submit">
      <template v-if="!editing || existing">
        <label class="editor-label" for="resource-title">제목 <span>*</span></label><input id="resource-title" v-model="form.title" maxlength="200" required placeholder="제목을 입력하세요." /><p class="character-count">{{ form.title.length }}/200자</p>
        <label class="editor-label" for="resource-content">내용 <span>*</span></label><textarea id="resource-content" v-model="form.content" maxlength="200" required placeholder="질문 내용을 입력해주세요." /><p class="character-count">{{ form.content.length }}/200자</p>
        <label class="file-label" for="resource-file">첨부파일(선택사항)</label><label class="file-picker" for="resource-file">{{ form.attachment?.name || '파일을 첨부해주세요. (10MB 이하)' }}</label><input id="resource-file" ref="fileInput" class="file-input" type="file" @change="chooseFile" /><button v-if="form.attachment" class="remove-file" type="button" @click="form.attachment = null; fileInput.value = ''">첨부파일 제거</button>
      </template><p v-else>해당 자료를 찾을 수 없습니다.</p>
      <p v-if="error" class="editor-error" role="alert">{{ error }}</p>
    </form>
    <div class="editor-actions"><RouterLink class="editor-button" to="/admin/resources">목록</RouterLink><div><RouterLink class="editor-button" :to="cancelPath">취소</RouterLink><button v-if="!editing || existing" class="editor-button" type="submit" form="resource-editor">{{ editing ? '저장' : '등록' }}</button></div></div>
  </main><PageFooter /></div>
</template>
<style scoped>
.admin-resource-shell { display: flex; flex-direction: column; min-height: 100svh; width: min(1440px, 100%); margin: 0 auto; padding: 0 34px; background: #e2ecee; } main { flex: 1; }.resource-editor { margin-top: 24px; padding: 26px 24px 15px; border-radius: 22px; background: #f0f5f7; }.editor-label { display: block; margin-bottom: 15px; color: #155369; font-size: 32px; font-weight: 700; line-height: 40px; }.editor-label span { color: #f00; } input:not([type=file]), textarea { display: block; width: 100%; padding: 18px 20px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; color: #333; font: inherit; } input:not([type=file]) { height: 65px; } textarea { min-height: 166px; resize: vertical; }.character-count { margin: 7px 8px 8px; text-align: right; color: #858585; font-weight: 600; }.file-label { display: block; margin-top: -22px; color: #155369; font-weight: 600; }.file-picker { display: block; margin-top: 3px; padding: 18px 22px; border-radius: 10px; background: #dbe4e7; color: #427486; font-weight: 600; cursor: pointer; overflow-wrap: anywhere; }.file-input { position: absolute; width: 1px; height: 1px; opacity: 0; }.file-picker:has(+ .file-input:focus-visible) { outline: 2px solid #155369; }.remove-file { margin-top: 8px; border: 0; background: none; color: #427486; text-decoration: underline; }.editor-error { color: #f00; }.editor-actions { display: flex; justify-content: space-between; margin-top: 16px; }.editor-actions > div { display: flex; gap: 16px; }.editor-button { display: inline-flex; align-items: center; justify-content: center; min-width: 84px; min-height: 40px; padding: 8px 20px; border: 0; border-radius: 9px; background: #457384; color: #fff; font-size: 16px; font-weight: 600; } input:focus-visible, textarea:focus-visible, a:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
@media (max-width: 760px) { .admin-resource-shell { padding: 0 16px; }.resource-editor { padding: 20px 16px; }.editor-label { font-size: 26px; }.file-label { margin-top: 0; }.editor-actions > div { gap: 8px; }.editor-button { min-width: 70px; padding: 8px 14px; } }
</style>
