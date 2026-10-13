<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import transportLogo from '../assets/transport-404-logo.png'
import LoginRequiredModal from './LoginRequiredModal.vue'
import { errorMessage } from '../api/client'

const props = defineProps({
  role: {
    type: String,
    default: 'guest',
    validator: (value) => ['guest', 'member', 'admin'].includes(value),
  },
  userName: {
    type: String,
    default: '',
  },
})
const showLoginRequired = ref(false)
const router = useRouter()
const auth = useAuthStore()
const logoutMessage = ref('')

function handleNavigation(event, item) {
  if (props.role === 'guest' && item.label === 'Q&A') {
    event.preventDefault()
    showLoginRequired.value = true
  }
}

async function handleAccountNavigation(event, item) {
  if (item.label !== '로그아웃') return
  event.preventDefault()
  try {
    await auth.signOut()
    await router.push('/')
  } catch (error) { logoutMessage.value = errorMessage(error, '로그아웃하지 못했습니다.') }
}

const navigation = computed(() => [
  { label: '공지사항', href: props.role === 'admin' ? '/admin/notices' : '/notices' },
  { label: '자료실', href: props.role === 'admin' ? '/admin/resources' : props.role === 'member' ? '/resources' : '#' },
  { label: 'Q&A', href: props.role === 'admin' ? '/admin/qna' : props.role === 'member' ? '/qna' : '#' },
  { label: '철도정보', href: '#' },
])

const accountLinks = computed(() => {
  const linksByRole = {
    guest: [{ label: '로그인', href: '/login' }],
    member: [
      { label: '마이페이지', href: '/mypage' },
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
  if (props.role === 'member') return `${auth.user?.name || props.userName}님`
  return ''
})
</script>

<template>
  <header class="site-header">
    <a class="brand" :href="role === 'admin' ? '/admin' : role === 'member' ? '/member/home' : '/'" aria-label="인천교통공사 홈">
      <img class="brand-logo" :src="transportLogo" alt="404" />
      <span class="brand-name">인천교통공사</span>
    </a>

    <nav class="primary-nav" aria-label="주요 메뉴">
      <a v-for="item in navigation" :key="item.label" :href="item.href" @click="handleNavigation($event, item)">
        {{ item.label }}
      </a>
    </nav>

    <nav class="account-nav" aria-label="계정 메뉴">
      <a
        v-for="item in accountLinks"
        :key="item.label"
        :class="['account-link', { 'account-link--guest': role === 'guest' }]"
        :href="item.href"
        @click="handleAccountNavigation($event, item)"
      >
        {{ item.label }}
      </a>
      <span v-if="roleLabel" class="role-chip">{{ roleLabel }}</span>
      <span v-if="logoutMessage" role="alert">{{ logoutMessage }}</span>
    </nav>

    <LoginRequiredModal :open="showLoginRequired" @close="showLoginRequired = false" />
  </header>
</template>



