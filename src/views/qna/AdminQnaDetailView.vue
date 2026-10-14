<script setup>
import {computed,ref,watch} from 'vue';import {useRoute,useRouter} from 'vue-router';import PageHeader from '../../components/PageHeader.vue';import PageBanner from '../../components/PageBanner.vue';import PageFooter from '../../components/PageFooter.vue';import DeleteModal from '../../components/DeleteModal.vue';import './admin-qna-detail.css';import {qnaApi} from '../../services/endpoints';import {apiError} from '../../services/api'
const route=useRoute(),router=useRouter(),qna=ref({}),error=ref(''),deleteStage=ref(''),qnaId=computed(()=>route.params.qnaId||route.query.id),isAnswered=computed(()=>qna.value.status==='ANSWERED'),types={FACILITY:'시설',OPERATION:'운행',ETC:'기타'}
async function load(){try{const r=await qnaApi.adminGet(qnaId.value),d=r?.data??r;qna.value={...d,category:types[d.inquiryType],views:d.viewCount??'—',date:d.createdAt}}catch(e){error.value=apiError(e)}}
function editQna(){router.push({path:'/admin/qna',query:{edit:qnaId.value}})}
function deleteQna(){deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){closeDelete();return}try{await qnaApi.adminRemove(qnaId.value);deleteStage.value='complete'}catch(e){error.value=apiError(e);deleteStage.value=''}}
function closeDelete(){const done=deleteStage.value==='complete';deleteStage.value='';if(done)router.push('/admin/qna')}
watch(()=>route.fullPath,load,{immediate:true})
</script>

<template>
  <div class="admin-qna-detail-page">
    <PageHeader />

    <main class="admin-qna-detail-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." stage="상세" />

      <article class="admin-qna-detail-card" aria-labelledby="admin-qna-detail-title"><p v-if="error" role="alert">{{ error }}</p>
        <div class="admin-qna-detail-heading">
          <h2 id="admin-qna-detail-title">{{ qna.title }}</h2>
          <span :class="['admin-qna-detail-status', { 'is-pending': !isAnswered }]">{{ qna.status === 'ANSWERED' ? '답변완료' : '답변대기' }}</span>
        </div>

        <dl class="admin-qna-detail-meta">
          <div><dt>작성자</dt><dd>{{ qna.writer }}</dd></div>
          <div><dt>조회수</dt><dd>{{ qna.views }}</dd></div>
          <div><dt>등록일</dt><dd>{{ qna.date }}</dd></div>
        </dl>

        <section class="admin-qna-message admin-qna-message--question" aria-labelledby="qna-question-heading">
          <h3 id="qna-question-heading">질문 내용</h3>
          <p>{{ qna.content }}</p>
        </section>

        <section v-if="isAnswered" class="admin-qna-message admin-qna-message--answer" aria-labelledby="qna-answer-heading">
          <h3 id="qna-answer-heading">관리자 답변</h3>
          <p>{{ qna.answer }}</p>
        </section>
        <section v-else class="admin-qna-message admin-qna-message--pending" aria-label="답변 대기">
          <h3>관리자 답변</h3>
          <p>아직 등록된 답변이 없습니다.</p>
        </section>
      </article>

      <div class="admin-qna-detail-actions">
        <RouterLink class="admin-qna-detail-button" to="/admin/qna">목록</RouterLink>
        <div>
          <button class="admin-qna-detail-button" type="button" @click="editQna">수정</button>
          <button class="admin-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>

