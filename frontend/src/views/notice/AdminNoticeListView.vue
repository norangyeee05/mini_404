<script setup>
import { ref, onMounted } from 'vue'
import DeleteModal from '../../components/DeleteModal.vue'
import { useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageBanner from '../../components/PageBanner.vue'
import PageFooter from '../../components/PageFooter.vue'
import { noticesApi } from '../../services/endpoints'
import { apiError } from '../../services/api'
import './admin-notice-list.css'
const router=useRouter(), notices=ref([]),totalCount=ref(0),page=ref(1),searchField=ref('title'),searchText=ref(''),error=ref(''),deleteStage=ref(''),deletingId=ref(null),pageSize=10
async function load(){try{const r=await noticesApi.list({page:page.value,size:pageSize,searchType:searchField.value,keyword:searchText.value});notices.value=r.items;totalCount.value=r.totalCount}catch(e){error.value=apiError(e)}}
function editNotice(notice){router.push(`/admin/notices/${notice.noticeId}/edit`)}
function deleteNotice(id){deletingId.value=id;deleteStage.value='confirm'}
async function confirmDelete(){if(deleteStage.value==='complete'){closeDelete();return}try{await noticesApi.remove(deletingId.value);deleteStage.value='complete';await load()}catch(e){error.value=apiError(e);deleteStage.value=''}}
function closeDelete(){deleteStage.value='';deletingId.value=null}
onMounted(load)
</script>

<template>
  <div class="admin-notice-list-page">
    <PageHeader />

    <main class="admin-notice-list-main">
      <PageBanner title="공지사항" description="교통서비스 관련 주요 내용을 확인하고 관리할 수 있습니다."  />

      <div class="admin-notice-list-layout">

        <section class="admin-notice-board" aria-label="공지사항 관리 목록">
          <form class="admin-notice-search" @submit.prevent="page=1;load()">
            <label class="visually-hidden" for="admin-notice-search-field">검색 범위</label>
            <select id="admin-notice-search-field" v-model="searchField">
              
              <option value="title">제목</option>
              
              <option value="writer">작성자</option>
            </select>

            <label class="visually-hidden" for="admin-notice-search-text">검색어</label>
            <div class="admin-notice-search__input-wrap">
              <input id="admin-notice-search-text" v-model="searchText" type="search" placeholder="제목 또는 내용을 검색하세요" />
              <span class="admin-notice-search__icon" aria-hidden="true"></span>
            </div>

            <button class="admin-notice-search__button" type="submit">검색</button>
            <RouterLink class="admin-notice-register-button" to="/admin/notices/new">⊕ 등록하기</RouterLink>
          </form>

          <div class="admin-notice-table-scroll">
            <table class="admin-notice-table">
              <thead>
                <tr>
                  <th class="admin-notice-col-number">번호</th>
                  <th class="admin-notice-col-title">제목</th>
                  <th class="admin-notice-col-writer">작성자</th>
                  <th class="admin-notice-col-views">조회수</th>
                  <th class="admin-notice-col-date">등록일</th>
                  <th class="admin-notice-col-actions">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="notice in notices" :key="notice.noticeId">
                  <td>{{ notice.noticeId }}</td>
                  <td class="admin-notice-table__title">
                    <RouterLink class="admin-notice-title-link" :to="`/admin/notices/${notice.noticeId}`">{{ notice.title }}</RouterLink>
                  </td>
                  <td>{{ notice.writer }}</td>
                  <td>{{ notice.viewCount }}</td>
                  <td>{{ notice.createdAt }}</td>
                  <td class="admin-notice-table__actions">
                    <button class="admin-notice-row-button admin-notice-row-button--edit" type="button" @click="editNotice(notice)">수정</button>
                    <button class="admin-notice-row-button admin-notice-row-button--delete" type="button" @click="deleteNotice(notice.noticeId)">삭제</button>
                  </td>
                </tr>
                <tr v-if="error"><td colspan="6" class="admin-notice-table__empty">{{ error }}</td></tr><tr v-else-if="notices.length === 0">
                  <td colspan="6" class="admin-notice-table__empty">검색 결과가 없습니다.</td>
                </tr>
              </tbody>
            </table>
          </div>

          <nav class="admin-notice-pagination" aria-label="공지사항 페이지">
            <button type="button" aria-label="첫 페이지" @click="page=1;load()">«</button>
            <button type="button" aria-label="이전 페이지" :disabled="page<=1" @click="page--;load()">‹</button>
            <button v-for="number in Math.max(1, Math.ceil(totalCount/pageSize))" :key="number" type="button" :class="{ 'is-current': number === page }" @click="page=number;load()">
              {{ number }}
            </button>
            <button type="button" aria-label="다음 페이지" :disabled="page>=Math.ceil(totalCount/pageSize)" @click="page++;load()">›</button>
            <button type="button" aria-label="마지막 페이지" @click="page=Math.max(1,Math.ceil(totalCount/pageSize));load()">»</button>
          </nav>
        </section>
      </div>
    </main>

    <PageFooter />
    <DeleteModal v-if="deleteStage" :completed="deleteStage === 'complete'" @confirm="confirmDelete" @close="closeDelete" />
  </div>
</template>

