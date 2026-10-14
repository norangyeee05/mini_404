<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import PageBanner from '../../components/PageBanner.vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { resourcesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
const props=defineProps({admin:Boolean}),route=useRoute(),router=useRouter(),basePath=computed(()=>props.admin?'/admin/resources':'/resources'),detail=computed(()=>Boolean(route.params.resourceId)),resources=ref([]),totalCount=ref(0),resource=ref(null),field=ref('title'),keyword=ref(''),applied=ref(''),page=ref(1),pageSize=10,error=ref(''),deleteStage=ref(''),deletingId=ref(null)
const pageCount=computed(()=>Math.max(1,Math.ceil(totalCount.value/pageSize))),visiblePages=computed(()=>{const start=Math.floor((page.value-1)/5)*5+1;return Array.from({length:Math.min(5,pageCount.value-start+1)},(_,i)=>start+i)}),rows=computed(()=>resources.value)
async function load(){error.value='';try{if(detail.value){const r=props.admin?await resourcesApi.adminGet(route.params.resourceId):await resourcesApi.get(route.params.resourceId);resource.value=r?.data??r;return}const r=props.admin?await resourcesApi.adminList({page:page.value,size:pageSize,searchType:field.value,keyword:applied.value}):await resourcesApi.list({page:page.value,size:pageSize,searchType:field.value,keyword:applied.value});resources.value=r.items;totalCount.value=r.totalCount}catch(e){error.value=apiError(e)}}
function search(){applied.value=keyword.value.trim();page.value=1;load()}
function goPage(v){page.value=Math.max(1,Math.min(pageCount.value,v));load()}
function requestDelete(id){deletingId.value=id;deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){deleteStage.value='';router.push(basePath.value);return}try{await resourcesApi.remove(deletingId.value);deleteStage.value='complete'}catch(e){error.value=apiError(e);deleteStage.value=''}}
async function download(){const f=resource.value?.files?.[0];if(!f)return;try{const response=await resourcesApi.downloadFile(route.params.resourceId,f.fileId);const url=URL.createObjectURL(response.data);const a=document.createElement('a');a.href=url;a.download=f.fileName;a.click();URL.revokeObjectURL(url)}catch(e){error.value=apiError(e)}}
watch(()=>route.fullPath,()=>{resource.value=null;page.value=1;load();window.scrollTo({top:0})},{immediate:true})
</script>
<template>
  <div class="resource-shell" :class="{ 'resource-shell--admin': admin }"><PageHeader /><main class="resource-main">
    <PageBanner title="자료실" description="교통서비스 관련 자료를 확인하실 수 있습니다." :detail="detail" />
    <div v-if="!detail" class="resource-layout">
      <section class="resource-list" aria-label="자료 목록">
        <div class="list-toolbar"><form class="resource-search" @submit.prevent="search"><select v-model="field" aria-label="검색 범위"><option value="title">제목</option><option value="writer">작성자</option></select><div class="search-input"><input v-model="keyword" type="search" placeholder="제목 또는 내용을 검색하세요" aria-label="검색어" /><span class="resource-search-icon" aria-hidden="true"></span></div><button class="resource-button" type="submit">검색</button><RouterLink class="request-button" :to="admin ? '/admin/resources/new' : '/resources/request'">{{ admin ? '⊕ 등록하기' : '⊕ 문의/요청' }}</RouterLink><RouterLink v-if="admin" class="request-button" to="/admin/resource-requests">요청 관리</RouterLink></form></div>
        <div class="resource-table-scroll"><table><colgroup><col class="resource-col-number" /><col class="resource-col-title" /><col class="resource-col-author" /><col class="resource-col-views" /><col class="resource-col-date" /><col v-if="admin" class="resource-col-manage" /></colgroup><thead><tr><th>번호</th><th>제목</th><th>작성자</th><th>조회수</th><th>등록일</th><th v-if="admin">관리</th></tr></thead><tbody><tr v-for="row in rows" :key="row.resourceId"><td>{{ row.resourceId }}</td><td><RouterLink :to="`${basePath}/${row.resourceId}`">{{ row.title }}</RouterLink></td><td>{{ row.writer }}</td><td>{{ row.viewCount }}</td><td>{{ row.createdAt }}</td><td v-if="admin" class="manage-cell"><div class="resource-row-actions"><RouterLink class="manage-edit" :to="`${basePath}/${row.resourceId}/edit`">수정</RouterLink><button class="manage-delete" type="button" @click="requestDelete(row.resourceId)">삭제</button></div></td></tr><tr v-if="error"><td :colspan="admin ? 6 : 5">{{ error }}</td></tr><tr v-else-if="!rows.length"><td :colspan="admin ? 6 : 5">검색 결과가 없습니다.</td></tr></tbody></table></div>
        <nav class="resource-pagination" aria-label="자료실 페이지"><button :disabled="page === 1" aria-label="첫 페이지" @click="goPage(1)">«</button><button :disabled="page === 1" aria-label="이전 페이지" @click="goPage(page - 1)">‹</button><button v-for="number in visiblePages" :key="number" :class="{ active: page === number }" :aria-current="page === number ? 'page' : undefined" @click="goPage(number)">{{ number }}</button><button :disabled="page === pageCount" aria-label="다음 페이지" @click="goPage(page + 1)">›</button><button :disabled="page === pageCount" aria-label="마지막 페이지" @click="goPage(pageCount)">»</button></nav>
      </section>
    </div>
    <template v-else><article class="resource-detail"><template v-if="resource"><h2>{{ resource.title }}</h2><div class="resource-meta"><span>작성자　 {{ resource.writer }}</span><span>조회수　 {{ resource.viewCount }}　　 등록일　 {{ resource.createdAt?.split('T')[0] || resource.createdAt }}</span></div><p class="resource-body">{{ resource.content }}</p><section class="resource-attachment"><h3>첨부파일</h3><div class="attachment-row"><span>{{ resource.files?.[0]?.fileName || '첨부파일이 없습니다.' }} <small>{{ '' }}</small></span><button v-if="resource.files?.length" class="resource-button" @click="download">↓ 다운로드</button></div></section></template><p v-else>{{ error || '자료를 불러오는 중입니다.' }}</p></article><div class="detail-actions"><RouterLink class="resource-button back-button" :to="basePath">목록</RouterLink><div v-if="admin && resource" class="detail-manage"><RouterLink class="resource-button" :to="`${basePath}/${resource.resourceId}/edit`">수정</RouterLink><button class="resource-button" @click="requestDelete(resource.resourceId)">삭제</button></div></div></template>
  </main><PageFooter /><DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="deleteStage = ''" /></div>
</template>
<style scoped src="./styles/resource.css"></style>





