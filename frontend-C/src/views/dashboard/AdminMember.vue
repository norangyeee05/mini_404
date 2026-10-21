<script setup>
import SearchIcon from '../../components/SearchIcon.vue'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import { adminMembers, memberStatuses, fetchAdminMembers, fetchAdminMember, updateMemberStatus, updateMemberRole, resetMemberPassword } from '../../services/adminMemberData'
import { currentMember } from '../../services/auth'
import { api, apiError } from '../../services/backendApi'
const route = useRoute()
const router = useRouter()
function toggleDetail(memberId) {
  router.push(String(memberId) === route.params.memberId ? '/admin/members' : '/admin/members/' + memberId)
}
function resetList() {
  page.value = 1
  field.value = 'all'
  keyword.value = ''
  applied.value = { field: 'all', keyword: '' }
  editing.value = false
  feedback.value = ''
  router.push('/admin/members')
}
const field = ref('all')
const keyword = ref('')
const applied = ref({ field: 'all', keyword: '' })
const page = ref(1)
const selected = ref(null)
const totalCount = ref(0)
const editing = ref(false)
const draftStatus = ref('활성')
const draftRole = ref('USER')
const roleOptions = [{ value: 'USER', label: '유저' }, { value: 'WEB_ADMIN', label: '웹관리자' }, { value: 'ADMIN', label: '관리자' }]
const feedback = ref('')
const results = computed(() => ({ length: totalCount.value }))
const pageCount = computed(() => Math.max(1, Math.ceil(totalCount.value / 10)))
const rows = computed(() => adminMembers.value)
const visiblePages = computed(() => { const start = Math.floor((page.value - 1) / 5) * 5 + 1; return Array.from({ length: Math.min(5, pageCount.value - start + 1) }, (_, index) => start + index) })
const statusCounts = ref({ ACTIVE: 0, BLOCKED: 0, INACTIVE: 0 })
const summaries = computed(() => [{ title: '전체회원', value: totalCount.value }, ...[['활성회원', 'ACTIVE'], ['임시차단', 'BLOCKED'], ['비활성 회원', 'INACTIVE']].map(([title, status]) => ({ title, value: statusCounts.value[status] }))])
async function loadMembers() {
  try { const result = await fetchAdminMembers({ page: page.value, size: 10, ...(applied.value.keyword ? { keyword: applied.value.keyword } : {}), ...(applied.value.field !== 'all' ? { searchType: applied.value.field } : {}) }); totalCount.value = result.totalCount ?? result.items.length; await loadStatusCounts() }
  catch (error) { adminMembers.value = []; totalCount.value = 0; feedback.value = apiError(error) }
}
async function loadStatusCounts() {
  const statuses = Object.keys(statusCounts.value)
  const results = await Promise.all(statuses.map(status => api.members.list({ page: 1, size: 1, status })))
  statusCounts.value = Object.fromEntries(statuses.map((status, index) => [status, results[index].totalCount ?? 0]))
}
async function loadSelected() {
  selected.value = null
  if (!route.params.memberId) return
  try { selected.value = await fetchAdminMember(route.params.memberId) }
  catch (error) { feedback.value = apiError(error) }
}
onMounted(() => { loadMembers(); loadSelected() })
watch(page, loadMembers)
watch(() => route.params.memberId, () => { editing.value = false; feedback.value = ''; draftStatus.value = selected.value?.status ?? '활성'; loadSelected() })
function search() { applied.value = { field: field.value, keyword: keyword.value }; page.value = 1; loadMembers() }
function goPage(value) { page.value = Math.max(1, Math.min(pageCount.value, value)) }
async function editStatus() {
  if (!editing.value) { draftStatus.value = selected.value.status; draftRole.value = selected.value.role || 'USER'; editing.value = true; feedback.value = ''; return }
  try {
    await updateMemberStatus(selected.value.memberId, draftStatus.value)
    if (currentMember.value?.role === 'ADMIN' && draftRole.value !== selected.value.role) {
      await updateMemberRole(selected.value.memberId, draftRole.value)
      if (String(currentMember.value.memberId) === String(selected.value.memberId)) currentMember.value.role = draftRole.value
    }
    selected.value.status = draftStatus.value
    selected.value.role = draftRole.value
    selected.value.roleLabel = roleOptions.find(option => option.value === draftRole.value)?.label
    editing.value = false
    await loadStatusCounts()
    feedback.value = '회원 정보가 수정되었습니다.'
  }
  catch (error) { feedback.value = apiError(error) }
}
async function requestPasswordReset() {
  try {
    const result = await resetMemberPassword(selected.value.memberId)
    feedback.value = `임시 비밀번호: ${result.temporaryPassword}`
  } catch (error) { feedback.value = apiError(error) }
}
function badgeClass(status) { return { 활성: 'active', 임시차단: 'blocked', 비활성: 'inactive', 강퇴: 'removed', 탈퇴: 'withdrawn' }[status] || 'unknown' }
</script>
<template>
  <div class="admin-member-shell"><PageHeader /><main class="member-layout" :class="{ 'member-layout--detail': Boolean(route.params.memberId) }">
    <aside class="member-sidebar"><RouterLink to="/admin/members" @click.prevent="resetList">회원 목록</RouterLink></aside>
    <section class="member-list"><h1>회원관리</h1><p class="member-description">회원 정보를 검색하고 관리 할 수 있습니다.</p>
      <div class="member-summary"><article v-for="summary in summaries" :key="summary.title"><h2>{{ summary.title }}</h2><strong>{{ summary.value }}명</strong></article></div>
      <form class="member-search" @submit.prevent="search"><label for="member-search-field">검색조건</label><div><select id="member-search-field" v-model="field"><option value="all">전체</option><option value="name">이름</option><option value="loginId">아이디</option><option value="email">이메일</option></select><div class="member-search-input"><input v-model="keyword" aria-label="회원 검색어" placeholder="검색어를 입력하세요. (예: 이름 아이디 이메일)" /><SearchIcon /></div><button type="submit">검색</button></div></form>
      <div class="member-table-scroll"><table><colgroup><col style="width:10%" /><col style="width:10%" /><col style="width:13%" /><col style="width:31%" /><col style="width:14%" /><col style="width:12%" /><col style="width:10%" /></colgroup><thead><tr><th>회원번호</th><th>이름</th><th>아이디</th><th>이메일</th><th>등급</th><th>회원상태</th><th>관리</th></tr></thead><tbody><tr v-for="member in rows" :key="member.memberId" :class="{ selected: selected?.memberId === member.memberId }"><td>{{ member.memberId }}</td><td>{{ member.name }}</td><td>{{ member.loginId }}</td><td :title="member.email">{{ member.email }}</td><td>{{ member.roleLabel }}</td><td><span class="member-badge" :class="badgeClass(member.status)">{{ member.status }}</span></td><td><RouterLink class="member-detail-button" :to="String(member.memberId) === route.params.memberId ? '/admin/members' : `/admin/members/${member.memberId}`" :aria-expanded="selected?.memberId === member.memberId" @click.prevent="toggleDetail(member.memberId)">상세</RouterLink></td></tr><tr v-if="!rows.length"><td colspan="7">검색 결과가 없습니다.</td></tr></tbody></table></div>
      <nav class="member-pagination" aria-label="회원 목록 페이지"><button :disabled="page === 1" @click="goPage(1)" aria-label="첫 페이지">«</button><button :disabled="page === 1" @click="goPage(page - 1)" aria-label="이전 페이지">‹</button><button v-for="number in visiblePages" :key="number" :class="{ current: page === number }" :aria-current="page === number ? 'page' : undefined" @click="goPage(number)">{{ number }}</button><button :disabled="page === pageCount" @click="goPage(page + 1)" aria-label="다음 페이지">›</button><button :disabled="page === pageCount" @click="goPage(pageCount)" aria-label="마지막 페이지">»</button></nav>
    </section>
    <section v-if="route.params.memberId" class="member-detail" aria-labelledby="member-detail-title"><h2 id="member-detail-title">회원 상세보기</h2><template v-if="selected">
      <article class="member-detail-card"><h3>기본정보</h3><dl><dt>회원번호</dt><dd>{{ String(selected.memberId).padStart(6, '0') }}</dd><dt>이름</dt><dd>{{ selected.name }}</dd><dt>아이디</dt><dd>{{ selected.loginId }}</dd><dt>이메일</dt><dd>{{ selected.email }}</dd><dt>연락처</dt><dd>{{ selected.phone }}</dd></dl></article>
      <article class="member-detail-card member-status-card"><div class="status-heading"><h3>회원상태</h3><button @click="editStatus">{{ editing ? '완료' : '수정' }}</button></div><dl><dt>상태</dt><dd><select v-if="editing" v-model="draftStatus" aria-label="회원상태 변경"><option v-for="status in memberStatuses" :key="status">{{ status }}</option></select><span v-else class="member-badge" :class="badgeClass(selected.status)">{{ selected.status }}</span></dd><dt>등급</dt><dd><select v-if="editing && currentMember?.role === 'ADMIN'" v-model="draftRole" aria-label="회원 등급 변경"><option v-for="option in roleOptions" :key="option.value" :value="option.value">{{ option.label }}</option></select><span v-else>{{ selected.roleLabel }}</span></dd><dt>가입일</dt><dd>{{ selected.createdAt }}</dd><dt>최근 수정일</dt><dd>{{ selected.updatedAt }}</dd></dl><button class="member-password-reset" type="button" @click="requestPasswordReset">비밀번호 초기화</button><p v-if="feedback" class="member-feedback" role="status">{{ feedback }}</p></article>
    </template><p v-else class="detail-placeholder">{{ route.params.memberId ? '회원을 찾을 수 없습니다.' : '목록에서 상세 버튼을 눌러 주세요.' }}</p></section>
  </main><PageFooter /></div>
</template>
<style scoped>
.admin-member-shell { display: flex; flex-direction: column; width: min(1440px, 100%); min-height: 100svh; margin: 0 auto; padding: 0 33px; background: var(--page-background); }.member-layout { display: grid; grid-template-columns: 201px minmax(0, 1fr); gap: 8px; flex: 1; margin: 13px 0 30px; }.member-layout--detail { grid-template-columns: 201px minmax(0, 1fr) 370px; }.member-sidebar { display: flex; flex-direction: column; gap: 4px; padding: 16px 8px; border-radius: 20px; background: #b5cce0; }.member-sidebar a { display: flex; align-items: center; min-height: 40px; padding: 0 16px; border-radius: 10px; background: #f8f9f9; color: #858b8d; font-size: 15px; text-decoration: none; }.member-sidebar a:hover { background: #fff; color: #4e7e8d; }.member-list { min-width: 0; padding: 20px; border-radius: 12px; background: #f0f5f7; }.member-list h1, .member-detail > h2 { margin: 0 0 4px; color: #155369; font-size: 24px; line-height: 32px; }.member-description { margin: 0; color: #000; font-size: 16px; }.member-summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; margin: 14px 0 17px; }.member-summary article { padding: 12px 16px; border-radius: 12px; background: #fff; }.member-summary h2 { margin: 0 0 12px; font-size: 16px; color: #000; font-weight: 500; }.member-summary strong { color: #155369; font-size: 26px; }.member-search { padding: 10px; border-radius: 12px; background: #d9d9d9; margin: 0 4px 14px; color: #000; }.member-search label { font-size: 14px; }.member-search > div { display: flex; align-items: center; gap: 10px; }.member-search select { width: 28%; height: 39px; padding: 0 14px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; color: #427486; font: inherit; font-weight: 600; }.member-search-input { position: relative; display: flex; align-items: center; flex: 1; min-width: 0; height: 39px; padding: 0 42px 0 18px; border: 3px solid #c6dadd; border-radius: 10px; background: #fff; }.member-search-input input { width: 100%; min-width: 0; border: 0; font: inherit; }.member-search button { min-width: 95px; height: 39px; border: 0; border-radius: 9px; background: #457384; color: #fff; font: inherit; font-weight: 600; }.member-table-scroll { overflow-x: auto; margin: 0 8px; } table { table-layout: fixed; font-family: inherit; font-size: 15px; font-weight: 400; color: #858b8d; text-align: center; } thead { background: #e7edef; } th { height: 42px; font-weight: 700; text-align: center; } td { height: 40px; padding: 3px 5px; border: 0; border-bottom: 1px solid #e0e8ea; overflow: hidden; text-overflow: ellipsis; text-align: center; }.member-badge { display: inline-flex; min-width: 61px; padding: 5px 8px; justify-content: center; border-radius: 10px; color: #fff; font-weight: 600; white-space: nowrap; line-height: 20px; }.active { background: #77b184; }.blocked { background: #f0df7b; }.inactive { background: #c4c4c4; }.removed { background: #cf7779; }.member-detail-button { display: inline-flex; padding: 5px 15px; border-radius: 9px; background: #cfdfe2; color: #fff; font-weight: 600; }.selected { background: #e6eef0; }.member-pagination { display: flex; justify-content: center; gap: 5px; margin-top: 56px; }.member-pagination button { min-width: 28px; height: 28px; border: 0; border-radius: 5px; background: #e7eef0; color: #858585; font-size: 24px; }.member-pagination button:first-child, .member-pagination button:nth-child(2), .member-pagination button:nth-last-child(-n+2) { font-size: 13px; }.member-pagination .current { background: #c6dadd; }.member-pagination button:disabled { opacity: .5; }.member-detail { min-width: 0; margin-left: 16px; padding: 16px; border-radius: 12px; background: #f0f5f7; }.member-detail-card { min-height: 218px; padding: 12px 17px; margin-top: 10px; border-radius: 12px; background: #fff; }.member-detail-card h3 { margin: 0; padding: 0 3px 12px; border-bottom: 2px solid #a0bac5; color: #155369; font-size: 14px; }.member-detail-card dl { display: grid; grid-template-columns: 90px minmax(0, 1fr); column-gap: 8px; margin: 18px 0; font-size: 15px; line-height: 35px; } dt { color: #999; font-weight: 600; } dd { margin: 0; color: #327086; overflow-wrap: anywhere; }.member-status-card { min-height: 219px; margin-top: 30px; }.status-heading { position: relative; }.status-heading button { position: absolute; right: 0; top: -1px; min-width: 61px; padding: 6px 12px; border: 0; border-radius: 10px; background: #d9d9d9; color: #858585; font-size: 13px; font-weight: 600; }.member-status-card dd:first-of-type { margin-bottom: 12px; }.member-status-card select { max-width: 100%; padding: 4px; border: 1px solid #a0bac5; border-radius: 6px; font: inherit; }.member-feedback { color: #327086; font-size: 13px; }.detail-placeholder { color: #858585; font-size: 14px; } a:focus-visible, button:focus-visible, input:focus-visible, select:focus-visible { outline: 2px solid #155369; outline-offset: 3px; }
@media (max-width: 1200px) { .member-layout, .member-layout--detail { grid-template-columns: 150px minmax(0, 1fr); gap: 16px; }.member-detail { grid-column: 2; margin-left: 0; }.member-summary { gap: 10px; }.member-summary article { padding: 10px; } table { min-width: 680px; } }
@media (max-width: 760px) { .admin-member-shell { padding: 0 16px; }.member-layout, .member-layout--detail { grid-template-columns: 1fr; }.member-detail { grid-column: 1; }.member-sidebar { padding: 16px 8px; }.member-summary { grid-template-columns: repeat(2, 1fr); }.member-search > div { flex-wrap: wrap; }.member-search select { width: 100%; }.member-search-input { min-width: 160px; }.member-search button { min-width: 70px; }.member-list { padding: 16px 12px; }.member-pagination { margin-top: 24px; } table { font-size: 12px; } }
.member-password-reset { display: block; width: 100%; min-height: 28px; padding: 4px 12px; border: 0; border-radius: 10px; background: #d9d9d9; color: #858585; font: inherit; font-size: 13px; line-height: 20px; font-weight: 700; cursor: pointer; }
.member-password-reset:hover { background: #ccc; }
.withdrawn { background: #75828a; }.unknown { background: #75828a; }
</style>










