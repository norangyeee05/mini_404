<script setup>
import {computed,reactive,ref,watch} from 'vue';import {useRoute,useRouter} from 'vue-router';import Resource from './Resource.vue';import PageHeader from '../../components/PageHeader.vue';import PageFooter from '../../components/PageFooter.vue';import PageBanner from '../../components/PageBanner.vue';import {resourcesApi} from '../../services/endpoints';import {apiError} from '../../services/api'
const route=useRoute(),router=useRouter(),editing=computed(()=>route.name==='admin-resource-edit'),formMode=computed(()=>editing.value||route.name==='admin-resource-new'),form=reactive({title:'',content:'',attachment:null}),error=ref(''),saving=ref(false),fileInput=ref(null),existing=ref(null),resourceId=computed(()=>route.params.resourceId)
watch(()=>route.fullPath,async()=>{error.value='';form.title='';form.content='';form.attachment=null;existing.value=null;if(editing.value)try{const r=await resourcesApi.adminGet(resourceId.value),d=r?.data??r;existing.value=d;form.title=d.title;form.content=d.content;form.attachment=d.files?.[0]||null}catch(e){error.value=apiError(e)}},{immediate:true})
function chooseFile(e){const f=e.target.files?.[0];if(f?.size>10*1024*1024){error.value='첨부파일은 10MB 이하로 선택해 주세요.';e.target.value='';return}form.attachment=f||null}
async function submit(){error.value='';saving.value=true;try{const body=new FormData();body.append('title',form.title.trim());body.append('content',form.content.trim());if(form.attachment instanceof File)body.append('files[]',form.attachment);const r=editing.value?await resourcesApi.update(resourceId.value,body):await resourcesApi.create(body),d=r?.data?.data??r?.data??r,id=d.resourceId;router.push(`/admin/resources/${id}`)}catch(e){error.value=apiError(e)}finally{saving.value=false}}
const cancelPath=computed(()=>editing.value?`/admin/resources/${resourceId.value}`:'/admin/resources')
</script>
<template>
  <Resource v-if="!formMode" admin />
  <div v-else class="admin-resource-shell"><PageHeader /><main>
    <PageBanner title="자료실" description="교통서비스 관련 자료를 확인하실 수 있습니다." :stage="editing ? '수정' : '등록'" />
    <form id="resource-editor" class="resource-editor" @submit.prevent="submit">
      <template v-if="!editing || existing">
        <label class="editor-label" for="resource-title">제목 <span>*</span></label><input id="resource-title" v-model="form.title" maxlength="200" required placeholder="제목을 입력하세요." /><p class="character-count">{{ form.title.length }}/200자</p>
        <label class="editor-label" for="resource-content">내용 <span>*</span></label><textarea id="resource-content" v-model="form.content" maxlength="200" required placeholder="질문 내용을 입력해주세요." /><p class="character-count">{{ form.content.length }}/200자</p>
        <label class="file-label" for="resource-file">첨부파일(선택사항)</label><label class="file-picker" for="resource-file">{{ form.attachment?.fileName || form.attachment?.name || '파일을 첨부해주세요. (10MB 이하)' }}</label><input id="resource-file" ref="fileInput" class="file-input" type="file" @change="chooseFile" :disabled="saving" /><button v-if="form.attachment" class="remove-file" type="button" @click="form.attachment = null; fileInput.value = ''">첨부파일 제거</button>
      </template><p v-else>해당 자료를 찾을 수 없습니다.</p>
      <p v-if="error" class="editor-error" role="alert">{{ error }}</p>
    </form>
    <div class="editor-actions"><RouterLink class="editor-button" to="/admin/resources">목록</RouterLink><div><RouterLink class="editor-button" :to="cancelPath">취소</RouterLink><button v-if="!editing || existing" class="editor-button" type="submit" form="resource-editor">{{ saving ? '저장 중...' : editing ? '저장' : '등록' }}</button></div></div>
  </main><PageFooter /></div>
</template>
<style scoped src="./styles/admin-resource.css"></style>

