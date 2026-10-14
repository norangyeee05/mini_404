<script setup>
import {computed,ref,watch} from 'vue';import {useRoute,useRouter} from 'vue-router';import PageHeader from '../../components/PageHeader.vue';import PageBanner from '../../components/PageBanner.vue';import PageFooter from '../../components/PageFooter.vue';import './member-qna-form.css';import {qnaApi} from '../../services/endpoints';import {apiError} from '../../services/api'
const props=defineProps({mode:{type:String,default:'create'}}),route=useRoute(),router=useRouter(),title=ref(''),content=ref(''),category=ref('OPERATION'),visibility=ref('전체'),selectedFile=ref(null),error=ref(''),saving=ref(false),titleLength=computed(()=>title.value.length),contentLength=computed(()=>content.value.length),isEdit=computed(()=>props.mode==='edit'||Boolean(route.params.qnaId)),qnaId=computed(()=>route.params.qnaId||route.query.id)
const types={'운행':'OPERATION','시설':'FACILITY','기타':'ETC','OPERATION':'OPERATION','FACILITY':'FACILITY','ETC':'ETC'}
watch(()=>route.fullPath,async()=>{error.value='';if(isEdit.value)try{const r=await qnaApi.get(qnaId.value),q=r?.data??r;title.value=q.title;content.value=q.content;category.value=q.inquiryType||'OPERATION'}catch(e){error.value=apiError(e)}},{immediate:true})
function handleFileChange(e){const f=e.target.files?.[0];if(f&&f.size>10*1024*1024){error.value='첨부파일은 10MB 이하만 선택할 수 있습니다.';e.target.value='';return}selectedFile.value=f||null}
function goBack(){router.push(isEdit.value?`/qna/${qnaId.value}`:'/qna/my')}
async function saveQuestion(){saving.value=true;error.value='';try{const body=new FormData();body.append('title',title.value.trim());body.append('inquiryType',types[category.value]||'ETC');body.append('content',content.value.trim());if(selectedFile.value)body.append('files[]',selectedFile.value);if(isEdit.value){await qnaApi.update(qnaId.value,body);router.push(`/qna/${qnaId.value}`)}else{const r=await qnaApi.create(body),data=r?.data??r;router.push(`/qna/${data.qnaId}`)}}catch(e){error.value=apiError(e)}finally{saving.value=false}}
</script>

<template>
  <div class="member-qna-form-page">
    <PageHeader />

    <main class="member-qna-form-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." :stage="isEdit ? '수정' : '등록'" />

      <form class="member-qna-form-card" @submit.prevent="saveQuestion">
        <div class="member-qna-form-title-row">
          <label for="qna-title">제목 <span>*</span></label>
          <div class="member-qna-form-selects">
            <label class="visually-hidden" for="qna-visibility">공개 여부</label>
            <select id="qna-visibility" disabled><option>공개 여부는 명세에 없음</option></select>
            <label class="visually-hidden" for="qna-category">문의 유형</label>
            <select id="qna-category" v-model="category"><option>운행</option><option>시설</option><option>기타</option></select>
          </div>
        </div>
        <input id="qna-title" v-model="title" class="member-qna-form-title-input" maxlength="200" placeholder="제목을 입력하세요." required />
        <div class="member-qna-form-count">{{ titleLength }}/200자</div>

        <label class="member-qna-form-content-label" for="qna-content">내용 <span>*</span></label>
        <textarea id="qna-content" v-model="content" maxlength="200" placeholder="질문 내용을 입력해주세요." required></textarea>
        <div class="member-qna-form-count">{{ contentLength }}/200자</div>

        <label class="member-qna-form-file-label" for="qna-file">첨부파일(선택사항)</label>
        <label class="member-qna-form-file-control" for="qna-file">
          <span>{{ selectedFile?.name || '파일을 첨부해주세요. (10MB 이하)' }}</span>
          <input id="qna-file" type="file" @change="handleFileChange" />
        </label>

        <p v-if="error" role="alert">{{ error }}</p><div class="member-qna-form-actions">
          <button type="button" class="member-qna-form-button" @click="goBack">목록</button>
          <div>
            <button type="button" class="member-qna-form-button" @click="goBack">취소</button>
            <button type="submit" class="member-qna-form-button">{{ saving ? '저장 중...' : isEdit ? '수정' : '등록' }}</button>
          </div>
        </div>
      </form>
    </main>

    <PageFooter />
  </div>
</template>

