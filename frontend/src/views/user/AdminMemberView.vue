<script setup>
import { computed, onMounted, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { getMember, getMembers, updateMemberStatus } from '../../api/admin'
import { errorMessage } from '../../api/client'
import './admin-member.css'

const members = ref([])
const totalCount = ref(0)
const currentPage = ref(1)
const pageSize = 10
const message = ref('')
const loading = ref(false)
const searchField = ref('all')
const searchText = ref('')
const selectedMember = ref(null)
const isEditingStatus = ref(false)
const statusDraft = ref({ status: '', grade: '' })
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / pageSize)))
const statusLabel = (status) => ({ ACTIVE: '활성', BLOCKED: '임시차단', BANNED: '강퇴' }[status] || status || '-')
const normalizeMember = (item = {}) => ({ ...item, number: item.memberId ?? item.memberNumber ?? item.number ?? '', memberId: item.memberId ?? item.memberNumber ?? item.number ?? '', id: item.loginId ?? item.id ?? '', name: item.name ?? '', email: item.email ?? '', phone: item.phone ?? item.phoneNumber ?? '', status: item.status ?? '', grade: item.grade ?? '', joinedAt: item.createdAt ?? item.joinedAt ?? '', updatedAt: item.updatedAt ?? '' })
const filteredMembers = computed(() => members.value.filter((member) => searchField.value === 'all' || member.status === searchField.value))

async function loadMembers() {
  loading.value = true
  message.value = ''
  try {
    const result = await getMembers({ page: currentPage.value, size: pageSize, searchType: 'all', keyword: searchText.value.trim() })
    members.value = result.items.map(normalizeMember)
    totalCount.value = result.totalCount
    currentPage.value = result.page
  } catch (error) { message.value = errorMessage(error, '회원 목록을 불러오지 못했습니다.') }
  finally { loading.value = false }
}
async function selectMember(member) {
  try { selectedMember.value = normalizeMember(await getMember(member.memberId)); message.value = '' }
  catch (error) { message.value = errorMessage(error, '회원 정보를 불러오지 못했습니다.') }
  isEditingStatus.value = false
}
function startStatusEdit() {
  statusDraft.value = { status: selectedMember.value?.status || '', grade: selectedMember.value?.grade || '' }
  isEditingStatus.value = true
}
function cancelStatusEdit() { isEditingStatus.value = false }
async function saveStatusEdit() {
  if (!selectedMember.value) return
  try {
    await updateMemberStatus(selectedMember.value.memberId, { status: statusDraft.value.status })
    selectedMember.value = normalizeMember(await getMember(selectedMember.value.memberId))
    isEditingStatus.value = false
    await loadMembers()
  } catch (error) { message.value = errorMessage(error, '회원 상태를 변경하지 못했습니다.') }
}
function searchMembers() { currentPage.value = 1; loadMembers() }
function goToPage(page) { currentPage.value = Math.min(Math.max(page, 1), pageCount.value); loadMembers() }
onMounted(loadMembers)
</script>

<template>
  <div class="admin-member-page">
    <PageHeader role="admin" />

    <main class="admin-member-main">
      <aside class="admin-member-sidebar" aria-label="회원관리 메뉴">
        <a class="admin-member-sidebar__link admin-member-sidebar__link--active" href="/admin/members">회원 목록</a>
      </aside>

      <section class="admin-member-list-panel" aria-labelledby="admin-member-heading">
        <div class="admin-member-heading">
          <h1 id="admin-member-heading">회원관리</h1>
          <p>회원 정보를 검색하고 관리 할 수 있습니다.</p>
        </div>

        <div class="admin-member-stats" aria-label="회원 현황">
          <article class="admin-member-stat"><h2>전체회원</h2><strong>{{ totalCount }}명</strong></article>
          <article class="admin-member-stat"><h2>활성회원</h2><strong>-</strong></article>
          <article class="admin-member-stat"><h2>임시차단</h2><strong>-</strong></article>
          <article class="admin-member-stat"><h2>비활성 회원</h2><strong>-</strong></article>
        </div>
        <p v-if="message" role="alert">{{ message }}</p>

        <form class="admin-member-search" @submit.prevent="searchMembers">
          <label class="admin-member-search__label" for="member-status-filter">검색조건</label>
          <div class="admin-member-search__controls">
            <select id="member-status-filter" v-model="searchField" aria-label="회원 상태 선택">
              <option value="all">전체</option>
              <option value="ACTIVE">활성</option>
              <option value="BLOCKED">임시차단</option>
              <option value="BANNED">강퇴</option>
            </select>
            <label class="visually-hidden" for="member-search-text">회원 검색어</label>
            <div class="admin-member-search__input-wrap">
              <input id="member-search-text" v-model="searchText" type="search" placeholder="검색어를 입력하세요. (예: 이름, 아이디, 이메일)" />
              <span class="admin-member-search__icon" aria-hidden="true"></span>
            </div>
            <button type="submit">검색</button>
          </div>
        </form>

        <p v-if="loading" role="status">불러오는 중...</p>
        <div class="admin-member-table-wrap">
          <table class="admin-member-table">
            <thead>
              <tr>
                <th>회원번호</th><th>이름</th><th>아이디</th><th>이메일</th><th>회원상태</th><th>관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in filteredMembers" :key="member.number" :class="{ 'is-selected': selectedMember?.id === member.id }">
                <td>{{ member.number }}</td>
                <td>{{ member.name }}</td>
                <td>{{ member.id }}</td>
                <td class="admin-member-table__email">{{ member.email }}</td>
                <td><span :class="['admin-member-status', `admin-member-status--${statusLabel(member.status)}`]">{{ statusLabel(member.status) }}</span></td>
                <td><button class="admin-member-detail-button" type="button" @click="selectMember(member)">상세</button></td>
              </tr>
              <tr v-if="filteredMembers.length === 0">
                <td colspan="6" class="admin-member-table__empty">검색 결과가 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <nav class="admin-member-pagination" aria-label="회원 목록 페이지">
          <button type="button" aria-label="첫 페이지" :disabled="currentPage === 1" @click="goToPage(1)">&lt;&lt;</button>
          <button type="button" aria-label="이전 페이지" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">&lt;</button>
          <button v-for="page in pageCount" :key="page" type="button" :class="{ 'is-current': page === currentPage }" @click="goToPage(page)">{{ page }}</button>
          <button type="button" aria-label="다음 페이지" :disabled="currentPage === pageCount" @click="goToPage(currentPage + 1)">&gt;</button>
          <button type="button" aria-label="마지막 페이지" :disabled="currentPage === pageCount" @click="goToPage(pageCount)">&gt;&gt;</button>
        </nav>
      </section>

      <aside v-if="selectedMember" class="admin-member-detail" aria-labelledby="member-detail-heading">
        <h2 id="member-detail-heading">회원 상세보기</h2>
        <section class="admin-member-detail-card">
          <div class="admin-member-detail-card__heading"><h3>기본정보</h3></div>
          <dl class="admin-member-info">
            <dt>회원번호</dt><dd>{{ selectedMember.number }}</dd>
            <dt>이름</dt><dd>{{ selectedMember.name }}</dd>
            <dt>아이디</dt><dd>{{ selectedMember.id }}</dd>
            <dt>이메일</dt><dd>{{ selectedMember.email }}</dd>
            <dt>연락처</dt><dd>{{ selectedMember.phone }}</dd>
          </dl>
        </section>

        <section class="admin-member-detail-card admin-member-status-card">
          <div class="admin-member-detail-card__heading">
            <h3>회원상태</h3>
            <div class="admin-member-edit-actions">
              <template v-if="isEditingStatus">
                <button class="admin-member-muted-button" type="button" @click="cancelStatusEdit">취소</button>
                <button class="admin-member-save-button" type="button" @click="saveStatusEdit">저장</button>
              </template>
              <button v-else class="admin-member-muted-button" type="button" @click="startStatusEdit">수정</button>
            </div>
          </div>
          <dl class="admin-member-info admin-member-info--status">
            <dt>상태</dt>
            <dd v-if="isEditingStatus">
              <select v-model="statusDraft.status" class="admin-member-edit-select" aria-label="회원 상태 수정">
                <option value="ACTIVE">활성</option><option value="BLOCKED">임시차단</option><option value="BANNED">강퇴</option>
              </select>
            </dd>
            <dd v-else><span :class="['admin-member-status', `admin-member-status--${statusLabel(selectedMember.status)}`]">{{ statusLabel(selectedMember.status) }}</span></dd>
            <dt>등급</dt>
            <dd v-if="isEditingStatus">
              <select v-model="statusDraft.grade" class="admin-member-edit-select" aria-label="회원 등급 수정">
                <option disabled>일반회원</option><option disabled>우수회원</option>
              </select>
            </dd>
            <dd v-else>{{ selectedMember.grade }}</dd>
            <dt>가입일</dt><dd>{{ selectedMember.joinedAt }}</dd>
            <dt>최근 수정일</dt><dd>{{ selectedMember.updatedAt }}</dd>
          </dl>
          <button class="admin-member-reset-button" type="button">비밀번호 초기화</button>
        </section>
      </aside>
    </main>

    <PageFooter />
  </div>
</template>
