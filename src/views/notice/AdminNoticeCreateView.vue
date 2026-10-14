<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import PageBanner from '../../components/PageBanner.vue'
import { noticesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
const props=defineProps({mode:{type:String,default:'create'}}),route=useRoute(),router=useRouter(),editing=computed(()=>props.mode==='edit'||Boolean(route.params.noticeId)),title=ref(''),content=ref(''),attachment=ref(null),saving=ref(false),error=ref(''),existing=ref(false)
const noticeId=computed(()=>route.params.noticeId||route.query.id)
watch(()=>route.fullPath,async()=>{title.value='';content.value='';error.value='';existing.value=false;if(editing.value&&noticeId.value)try{const r=await noticesApi.get(noticeId.value);const n=r?.data??r;title.value=n.title||'';content.value=n.content||'';existing.value=true}catch(e){error.value=apiError(e)}},{immediate:true})
function chooseFile(e){attachment.value=e.target.files?.[0]||null}
function removeFile(){attachment.value=null}
async function submitNotice(){if(saving.value)return;saving.value=true;error.value='';try{if(editing.value)await noticesApi.update(noticeId.value,{title:title.value.trim(),content:content.value.trim()});else await noticesApi.create({title:title.value.trim(),content:content.value.trim()});router.push(editing.value?`/admin/notices/${noticeId.value}`:'/admin/notices')}catch(e){error.value=apiError(e)}finally{saving.value=false}}
const cancelPath=computed(()=>editing.value?`/admin/notices/${noticeId.value}`:'/admin/notices')
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
        <label class="file-picker" for="notice-file">API 명세에서 공지 첨부파일을 지원하지 않습니다.</label>
        <input id="notice-file" class="file-input" type="file" disabled />
        
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

