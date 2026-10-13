<script setup>
import { computed } from 'vue'
import transportLogo from '../assets/transport-404-logo.png'

const props = defineProps({
  role: {
    type: String,
    default: 'guest',
    validator: (value) => ['guest', 'member', 'admin'].includes(value),
  },
  userName: {
    type: String,
    default: '홍길동',
  },
})

const navigation = computed(() => [
  { label: '공지사항', href: props.role === 'admin' ? '/admin/notices' : '/notice' },
  { label: '자료실', href: '#' },
  { label: 'Q&A', href: props.role === 'admin' ? '/admin/qna' : props.role === 'member' ? '/qna' : '#' },
  { label: '철도정보', href: '#' },
])

const accountLinks = computed(() => {
  const linksByRole = {
    guest: [{ label: '로그인', href: '#' }],
    member: [
      { label: '마이페이지', href: '#' },
      { label: '로그아웃', href: '#' },
    ],
    admin: [
      { label: '회원관리', href: '/admin/members' },
      { label: '마이페이지', href: '#' },
      { label: '로그아웃', href: '#' },
    ],
  }

  return linksByRole[props.role] ?? linksByRole.guest
})

const roleLabel = computed(() => {
  if (props.role === 'admin') return '관리자님'
  if (props.role === 'member') return `${props.userName}님`
  return ''
})
</script>

<template>
  <header class="site-header">
    <a class="brand" href="/" aria-label="인천교통공사 홈">
      <img class="brand-logo" :src="transportLogo" alt="404" />
      <span class="brand-name">인천교통공사</span>
    </a>

    <nav class="primary-nav" aria-label="주요 메뉴">
      <a v-for="item in navigation" :key="item.label" :href="item.href">
        {{ item.label }}
      </a>
    </nav>

    <nav class="account-nav" aria-label="계정 메뉴">
      <a
        v-for="item in accountLinks"
        :key="item.label"
        :class="['account-link', { 'account-link--guest': role === 'guest' }]"
        :href="item.href"
      >
        {{ item.label }}
      </a>
      <span v-if="roleLabel" class="role-chip">{{ roleLabel }}</span>
    </nav>
  </header>
</template>



