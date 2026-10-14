<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeleteModal from '../../components/DeleteModal.vue'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import './notice-detail.css'
import { noticesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
const props=defineProps({role:{type:String,default:'guest'}}),router=useRouter(),route=useRoute(),notice=ref({}),error=ref(''),deleteStage=ref(''),noticeId=computed(()=>route.params.noticeId||route.query.id)
async function load(){try{const r=await noticesApi.get(noticeId.value);notice.value=r?.data??r}catch(e){error.value=apiError(e)}}
function deleteNotice(){deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){closeDelete();return}try{await noticesApi.remove(noticeId.value);deleteStage.value='complete'}catch(e){error.value=apiError(e);deleteStage.value=''}}
function closeDelete(){const done=deleteStage.value==='complete';deleteStage.value='';if(done)router.push('/admin/notices')}
watch(()=>route.fullPath,load,{immediate:true})
</script>

<template>
  <div class="notice-detail-page">
    <PageHeader />

    <main class="notice-detail-main">
      <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하실 수 있습니다." stage="상세" />

      <article class="notice-detail-card" aria-labelledby="notice-detail-title">
        <h2 id="notice-detail-title">{{ notice.title }}</h2><p v-if="error" role="alert">{{ error }}</p>

        <dl class="notice-detail-meta">
          <div class="notice-detail-meta__author">
            <dt>작성자</dt>
            <dd>{{ notice.writer }}</dd>
          </div>
          <div class="notice-detail-meta__views">
            <dt>조회수</dt>
            <dd>{{ notice.viewCount }}</dd>
          </div>
          <div class="notice-detail-meta__date">
            <dt>등록일</dt>
            <dd>{{ notice.createdAt }}</dd>
          </div>
        </dl>

        <div
          :class="[
            'notice-detail-content',
            { 'notice-detail-content--admin': props.role === 'admin' },
          ]"
        >
          <p v-if="notice.content" class="notice-detail-content__saved">{{ notice.content }}</p>
          <p>{{ notice.content }}</p>

          <RouterLink
            v-if="props.role === 'admin'"
            class="notice-detail-back-link"
            to="/admin/notices"
          >
            ‹ &nbsp; 목록으로 돌아가기
          </RouterLink>
        </div>
      </article>

      <div class="notice-detail-actions">
        <RouterLink class="notice-detail-list-button" :to="props.role === 'admin' ? '/admin/notices' : '/notices'"
        >
          목록
        </RouterLink>
        <div v-if="props.role === 'admin'" class="notice-detail-admin-actions">
          <button class="notice-detail-admin-button" type="button" @click="router.push(`/admin/notices/${noticeId}/edit`)">수정</button>
          <button class="notice-detail-admin-button" type="button" @click="deleteNotice">삭제</button>
        </div>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>

<style scoped>.notice-attachment {
  margin-top: 24px;
  padding: 18px 22px;
  border-radius: 10px;
  background: #e6eef0;
  color: #427486;
}
.notice-attachment h3 {
  margin: 0 0 10px;
  font-size: 16px;
}
.notice-attachment > div {
  display: flex;
  align-items: center;
  gap: 16px;
  justify-content: space-between;
}
.notice-attachment span {
  overflow-wrap: anywhere;
  min-width: 0;
}
.notice-attachment button {
  flex-shrink: 0;
}
@media (max-width:640px) {
  .notice-attachment > div {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>

