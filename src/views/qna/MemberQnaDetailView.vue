<script setup>
import {computed,ref,watch} from 'vue';import {useRoute,useRouter} from 'vue-router';import PageHeader from '../../components/PageHeader.vue';import PageBanner from '../../components/PageBanner.vue';import PageFooter from '../../components/PageFooter.vue';import DeleteModal from '../../components/DeleteModal.vue';import './member-qna-detail.css';import {qnaApi} from '../../services/endpoints';import {apiError} from '../../services/api';import {currentMember} from '../../services/auth'
const route=useRoute(),router=useRouter(),qna=ref({}),error=ref(''),deleteStage=ref(''),qnaId=computed(()=>route.params.qnaId||route.query.id),types={FACILITY:'시설',OPERATION:'운행',ETC:'기타'},statusText={WAITING:'답변대기',ANSWERED:'답변완료'}
const isOwner=computed(()=>qna.value.writer&&qna.value.writer===currentMember.value?.name),isAnswered=computed(()=>qna.value.status==='ANSWERED'),displayDate=computed(()=>qna.value.createdAt?.split('T')[0]||'')
async function load(){try{const r=await qnaApi.get(qnaId.value),d=r?.data??r;qna.value={...d,category:types[d.inquiryType],status:d.status,views:d.viewCount??'—',writer:d.writer}}catch(e){error.value=apiError(e)}}
async function deleteQna(){deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){deleteStage.value='';router.push('/qna/my');return}try{await qnaApi.remove(qnaId.value);deleteStage.value='complete'}catch(e){error.value=apiError(e);deleteStage.value=''}}
watch(()=>route.fullPath,load,{immediate:true})
</script>

<template>
  <div class="member-qna-detail-page">
    <PageHeader />

    <main class="member-qna-detail-main">
      <PageBanner title="Q&amp;A" description="궁금하신 내용을 확인하실 수 있습니다." stage="상세" />

      <article class="member-qna-detail-card" aria-labelledby="member-qna-detail-title"><p v-if="error" role="alert">{{ error }}</p>
        <div class="member-qna-detail-heading">
          <h2 id="member-qna-detail-title">{{ qna.title }}</h2>
          <span :class="['member-qna-detail-status', { 'is-pending': !isAnswered }]">{{ qna.status === 'ANSWERED' ? '답변완료' : '답변대기' }}</span>
        </div>

        <dl class="member-qna-detail-meta">
          <div><dt>작성자</dt><dd>{{ qna.writer }}</dd></div>
          <div><dt>조회수</dt><dd>{{ qna.views }}</dd></div>
          <div><dt>등록일</dt><dd>{{ displayDate }}</dd></div>
        </dl>

        <section class="member-qna-message member-qna-message--question" aria-labelledby="member-qna-question-heading">
          <h3 id="member-qna-question-heading">질문 내용</h3>
          <p>{{ qna.content }}</p><p v-if="qna.files?.length">첨부파일: {{ qna.files.map(file => file.fileName).join(', ') }}</p>
        </section>

        <section v-if="isAnswered" class="member-qna-message member-qna-message--answer" aria-labelledby="member-qna-answer-heading">
          <h3 id="member-qna-answer-heading">관리자 답변</h3>
          <p>{{ qna.answer }}</p>
        </section>
        <section v-else class="member-qna-message member-qna-message--pending" aria-label="답변 대기">
          <h3>관리자 답변</h3>
          <p>답변을 준비하고 있습니다.</p>
        </section>
      </article>

      <div class="member-qna-detail-actions">
        <RouterLink class="member-qna-detail-button" to="/qna">목록</RouterLink>
        <div v-if="isOwner" class="member-qna-detail-actions__right">
          <button class="member-qna-detail-button" type="button" @click="router.push(`/qna/${qna.qnaId}/edit`)">수정</button>
          <button class="member-qna-detail-button" type="button" @click="deleteQna">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="deleteStage = ''" />
  </div>
</template>

