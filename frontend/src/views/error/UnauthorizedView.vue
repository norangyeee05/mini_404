<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import './unauthorized.css'

const props = defineProps({
  code: { type: String, default: '401' },
})
const router = useRouter()

const page = computed(() => ({
  '401': {
    title: '로그인이 필요합니다',
    description: ['해당 페이지에 접근하려면 로그인이 필요합니다.', '계속 이용하시려면 로그인 후 다시 시도해 주세요.'],
    action: '로그인 하기',
  },
  '403': {
    title: '접근 권한이 없습니다.',
    description: ['해당 페이지에 접속할 수 있는 권한이 없습니다.', '관리자에게 문의하시거나, 다른 페이지로 이동해 주세요.'],
    action: '홈으로 가기',
  },
  '404': {
    title: '요청하신 페이지를 찾을 수 없습니다.',
    description: ['입력하신 주소가 잘못 되었거나,', '페이지가 삭제되었을 수 있습니다.'],
    action: '메인으로 가기',
  },
  '500': {
    title: '서버에 문제가 발생하였습니다.',
    description: ['요청을 처리하는 중 서버에 일시적인 오류가 발생했습니다.', '잠시 후 다시 시도해 주시기 바랍니다.'],
    action: '홈으로 가기',
  },
})[props.code] || {})

function goBack() {
  if (window.history.state?.back) {
    router.back()
    return
  }
  router.push('/')
}

function goPrimary() {
  if (props.code === '401') {
    router.push('/login')
    return
  }
  router.push('/')
}
</script>

<template>
  <main class="unauthorized-page">
    <section class="unauthorized-content" aria-labelledby="unauthorized-title">
      <div class="unauthorized-illustration" role="img" :aria-label="`도시와 열차 그림, ${code}`">
        <svg viewBox="0 0 480 290" aria-hidden="true">
          <defs>
            <linearGradient id="sky" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stop-color="#f8fdff" />
              <stop offset="1" stop-color="#e8f5ff" />
            </linearGradient>
            <linearGradient id="train" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0" stop-color="#ffffff" />
              <stop offset="1" stop-color="#dceeff" />
            </linearGradient>
          </defs>
          <rect width="480" height="290" rx="42" fill="url(#sky)" />
          <g fill="#c8e4ff" opacity=".75">
            <path d="M71 93c0-8 7-14 15-14 4-12 21-13 27-2 9 0 15 7 15 16H71z" />
            <path d="M342 54c0-8 7-14 15-14 4-12 21-13 27-2 9 0 15 7 15 16h-57z" />
            <path d="M28 166c0-7 6-12 13-12 4-10 18-11 23-2 8 0 13 6 13 14H28z" />
          </g>
          <g fill="#d5eaff">
            <path d="M28 231v-58l18-13 18 13v58z" /><path d="M54 231v-39h24v39z" />
            <path d="M388 231v-73l22-17 22 17v73z" /><path d="M417 231v-46h34v46z" />
            <path d="M93 231v-48l17-12 17 12v48z" /><path d="M350 231v-44l16-12 16 12v44z" />
          </g>
          <g fill="#c1def8">
            <path d="M38 187h7v8h-7zm14 0h7v8h-7zm-14 14h7v8h-7zm14 0h7v8h-7zM398 166h8v9h-8zm14 0h8v9h-8zm-14 16h8v9h-8zm14 0h8v9h-8z" />
          </g>
          <path d="M235 48v84m-22-62h44m-35 0-9 14m35-14 9 14m-29-1-13 38m21-38 13 38" fill="none" stroke="#a9d2fa" stroke-width="4" stroke-linecap="round" />
          <text x="240" y="166" text-anchor="middle" fill="#78a9e8" font-size="98" font-weight="800" font-family="Arial, sans-serif">{{ code }}</text>
          <ellipse cx="241" cy="246" rx="183" ry="19" fill="#c8e4ff" opacity=".7" />
          <path d="M86 231h305l-22 17H105z" fill="#9bc4ed" opacity=".55" />
          <g transform="translate(145 180)">
            <path d="M7 48c0-20 13-34 34-34h143c21 0 35 14 35 34v34H7z" fill="url(#train)" stroke="#a9c9e7" stroke-width="3" />
            <path d="M27 29h42v25H27zm54 0h43v25H81zm55 0h38v25h-38z" fill="#9dc9ef" />
            <path d="M191 29c12 3 19 12 20 25h-20z" fill="#83b8e8" />
            <path d="M12 60h205v10H12z" fill="#6e9fce" />
            <circle cx="52" cy="83" r="8" fill="#557fa7" /><circle cx="183" cy="83" r="8" fill="#557fa7" />
            <path d="M0 91h237" stroke="#8bb8e2" stroke-width="4" stroke-linecap="round" />
          </g>
        </svg>
      </div>

      <div class="unauthorized-copy">
        <h1 id="unauthorized-title">{{ page.title }}</h1>
        <p>{{ page.description?.[0] }}<br />{{ page.description?.[1] }}</p>
        <div class="unauthorized-actions">
          <button class="unauthorized-login" type="button" @click="goPrimary">{{ page.action }}</button>
          <button class="unauthorized-back" type="button" @click="goBack">이전 페이지로</button>
        </div>
      </div>
    </section>
  </main>
</template>
