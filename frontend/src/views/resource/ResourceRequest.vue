<script setup>
import { reactive, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import PageBanner from '../../components/PageBanner.vue'
import { resourcesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
import './styles/admin-resource.css'
const form=reactive({title:'',content:'',file:null}),error=ref(''),done=ref(false),saving=ref(false)
function choose(e){const file=e.target.files?.[0];if(file?.size>10*1024*1024){error.value='첨부파일은 10MB 이하로 선택해 주세요.';e.target.value='';return}form.file=file||null}
async function submit(){saving.value=true;error.value='';try{const body=new FormData();body.append('title',form.title.trim());body.append('content',form.content.trim());if(form.file)body.append('files[]',form.file);await resourcesApi.request(body);done.value=true}catch(e){error.value=apiError(e)}finally{saving.value=false}}
</script>
<template><div class="admin-resource-shell"><PageHeader/><main><PageBanner title="자료실" description="자료실에 등록할 자료를 요청하실 수 있습니다." stage="요청"/><form class="resource-editor" @submit.prevent="submit"><template v-if="!done"><label class="editor-label" for="resource-request-title">제목 <span>*</span></label><input id="resource-request-title" v-model="form.title" maxlength="200" required/><p class="character-count">{{ form.title.length }}/200자</p><label class="editor-label" for="resource-request-content">내용 <span>*</span></label><textarea id="resource-request-content" v-model="form.content" maxlength="200" required></textarea><label class="file-label" for="resource-request-file">첨부파일(선택사항)</label><label class="file-picker" for="resource-request-file">{{ form.file?.name||'파일을 첨부해주세요. (10MB 이하)' }}</label><input id="resource-request-file" class="file-input" type="file" @change="choose"/><p v-if="error" class="editor-error" role="alert">{{ error }}</p><button class="editor-button" type="submit" :disabled="saving">{{ saving?'전송 중...':'요청하기' }}</button></template><p v-else role="status">자료실 추가 요청을 접수했습니다.</p></form><RouterLink class="editor-button" to="/resources">목록으로</RouterLink></main><PageFooter/></div></template>

