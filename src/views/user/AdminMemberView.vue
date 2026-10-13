<script setup>
import { computed, ref } from 'vue'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import './admin-member.css'

const members = ref([
  { number: 101, name: '이서연', id: 'seoyeon', email: 'seoyeon@naver.com', status: '활성', phone: '010-1234-1001', joinedAt: '2025.09.15 10:24', updatedAt: '2026.10.03 10:24', grade: '일반회원' },
  { number: 102, name: '박민준', id: 'minjun', email: 'minjun@naver.com', status: '임시차단', phone: '010-1234-1002', joinedAt: '2025.08.21 14:10', updatedAt: '2026.10.02 16:30', grade: '일반회원' },
  { number: 103, name: '정우성', id: 'woosung', email: 'woosung@naver.com', status: '비활성', phone: '010-1234-1003', joinedAt: '2025.07.03 09:15', updatedAt: '2026.09.28 11:40', grade: '일반회원' },
  { number: 104, name: '한지민', id: 'jimin', email: 'jimin@naver.com', status: '활성', phone: '010-1234-1004', joinedAt: '2025.06.18 13:52', updatedAt: '2026.10.01 09:20', grade: '일반회원' },
  { number: 105, name: '오세훈', id: 'sehun', email: 'sehun@naver.com', status: '비활성', phone: '010-1234-1005', joinedAt: '2025.05.11 08:44', updatedAt: '2026.09.25 15:11', grade: '일반회원' },
  { number: 106, name: '조예린', id: 'yerin', email: 'yerin@gmail.com', status: '활성', phone: '010-1234-1006', joinedAt: '2025.04.06 11:12', updatedAt: '2026.10.03 08:14', grade: '일반회원' },
  { number: 107, name: '윤지호', id: 'jiho', email: 'jiho@daum.net', status: '활성', phone: '010-1234-1007', joinedAt: '2025.03.22 17:31', updatedAt: '2026.09.30 17:48', grade: '일반회원' },
  { number: 108, name: '김지현', id: 'hong123', email: 'hong123@naver.com', status: '비활성', phone: '010-1234-5678', joinedAt: '2025.09.15 10:24', updatedAt: '2026.10.03 10:24', grade: '일반회원' },
  { number: 109, name: '이준원', id: 'jun103', email: 'jun103@gmail.com', status: '강퇴', phone: '010-1234-1009', joinedAt: '2025.02.14 12:08', updatedAt: '2026.09.29 13:03', grade: '일반회원' },
  { number: 110, name: '최지은', id: 'jieun', email: 'jileun@naver.com', status: '임시차단', phone: '010-1234-1010', joinedAt: '2025.01.09 10:00', updatedAt: '2026.10.02 12:26', grade: '일반회원' },
])

const searchField = ref('all')
const searchText = ref('')
const selectedMember = ref({
  number: '000128',
  name: '홍길동',
  id: 'hong123',
  email: 'hong123@gmail.com',
  phone: '010-1234-5678',
  status: '활성',
  grade: '일반회원',
  joinedAt: '2025.09.15 10:24',
  updatedAt: '2026.10.03 10:24',
})

const filteredMembers = computed(() => {
  const query = searchText.value.trim().toLocaleLowerCase()
  return members.value.filter((member) => {
    const matchesStatus = searchField.value === 'all' || member.status === searchField.value
    const matchesText = !query || `${member.name} ${member.id} ${member.email}`.toLocaleLowerCase().includes(query)
    return matchesStatus && matchesText
  })
})

function selectMember(member) {
  selectedMember.value = member
}
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
          <article class="admin-member-stat"><h2>전체회원</h2><strong>101명</strong></article>
          <article class="admin-member-stat"><h2>활성회원</h2><strong>101명</strong></article>
          <article class="admin-member-stat"><h2>임시차단</h2><strong>101명</strong></article>
          <article class="admin-member-stat"><h2>비활성 회원</h2><strong>101명</strong></article>
        </div>

        <form class="admin-member-search" @submit.prevent>
          <label class="admin-member-search__label" for="member-status-filter">검색조건</label>
          <div class="admin-member-search__controls">
            <select id="member-status-filter" v-model="searchField" aria-label="회원 상태 선택">
              <option value="all">전체</option>
              <option value="활성">활성</option>
              <option value="임시차단">임시차단</option>
              <option value="비활성">비활성</option>
              <option value="강퇴">강퇴</option>
            </select>
            <label class="visually-hidden" for="member-search-text">회원 검색어</label>
            <div class="admin-member-search__input-wrap">
              <input id="member-search-text" v-model="searchText" type="search" placeholder="검색어를 입력하세요. (예: 이름, 아이디, 이메일)" />
              <span class="admin-member-search__icon" aria-hidden="true"></span>
            </div>
            <button type="submit">검색</button>
          </div>
        </form>

        <div class="admin-member-table-wrap">
          <table class="admin-member-table">
            <thead>
              <tr>
                <th>회원번호</th><th>이름</th><th>아이디</th><th>이메일</th><th>회원상태</th><th>관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in filteredMembers" :key="member.number" :class="{ 'is-selected': selectedMember.id === member.id }">
                <td>{{ member.number }}</td>
                <td>{{ member.name }}</td>
                <td>{{ member.id }}</td>
                <td class="admin-member-table__email">{{ member.email }}</td>
                <td><span :class="['admin-member-status', `admin-member-status--${member.status}`]">{{ member.status }}</span></td>
                <td><button class="admin-member-detail-button" type="button" @click="selectMember(member)">상세</button></td>
              </tr>
              <tr v-if="filteredMembers.length === 0">
                <td colspan="6" class="admin-member-table__empty">검색 결과가 없습니다.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <nav class="admin-member-pagination" aria-label="회원 목록 페이지">
          <button type="button" aria-label="첫 페이지">&lt;&lt;</button>
          <button type="button" aria-label="이전 페이지">&lt;</button>
          <button v-for="page in 5" :key="page" type="button" :class="{ 'is-current': page === 1 }">{{ page }}</button>
          <button type="button" aria-label="다음 페이지">&gt;</button>
          <button type="button" aria-label="마지막 페이지">&gt;&gt;</button>
        </nav>
      </section>

      <aside class="admin-member-detail" aria-labelledby="member-detail-heading">
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
            <button class="admin-member-muted-button" type="button">수정</button>
          </div>
          <dl class="admin-member-info admin-member-info--status">
            <dt>상태</dt><dd><span :class="['admin-member-status', `admin-member-status--${selectedMember.status}`]">{{ selectedMember.status }}</span></dd>
            <dt>등급</dt><dd>{{ selectedMember.grade }}</dd>
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
