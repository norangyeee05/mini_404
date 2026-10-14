<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import image401 from './assets/401.png'
import image403 from './assets/403.png'
import image404 from './assets/404.png'
import image500 from './assets/500.png'
const route = useRoute()
const router = useRouter()
const pages = {
  '401': { image: image401, title: '로그인이 필요합니다', lines: ['해당 페이지에 접근하려면 로그인이 필요합니다.', '계속 이용하시려면 로그인 후 다시 시도해 주세요.'], button: '로그인 하기', to: '/login' },
  '403': { image: image403, title: '접근 권한이 없습니다.', lines: ['해당 페이지에 접속할 수 있는 권한이 없습니다.', '관리자에게 문의하시거나, 다른 페이지로 이동해 주세요.'], button: '홈으로 가기', to: '/' },
  '404': { image: image404, title: '요청하신 페이지를 찾을 수 없습니다.', lines: ['입력하신 주소가 잘못 되었거나,', '페이지가 삭제되었을 수 있습니다.'], button: '메인으로 가기', to: '/' },
  '500': { image: image500, title: '서버에 문제가 발생하였습니다.', lines: ['요청을 처리하는 중 서버에 일시적인 오류가 발생했습니다.', '잠시 후 다시 시도해 주시기 바랍니다.'], button: '홈으로 가기', to: '/' },
}
const code = computed(() => Object.hasOwn(pages, route.params.code) ? route.params.code : '404')
const page = computed(() => pages[code.value])
function previousPage() {
  // Direct entry has no previous in-app page, so return home.
  if (router.options.history.state.back) router.back()
  else router.push('/')
}
</script>
<template>
  <main class="error-page" :class="`error-page--${code}`">
    <section class="error-layout" aria-labelledby="error-title">
      <img class="error-illustration" :src="page.image" :alt="`${code} 오류 안내 삽화`" width="477" height="290" />
      <div class="error-content">
        <h1 id="error-title">{{ page.title }}</h1>
        <p class="error-description"><span v-for="line in page.lines" :key="line">{{ line }}</span></p>
        <div class="error-actions">
          <RouterLink class="error-button error-button--primary" :to="page.to">{{ page.button }}</RouterLink>
          <button class="error-button error-button--secondary" type="button" @click="previousPage">이전 페이지로</button>
        </div>
      </div>
    </section>
  </main>
</template>
<style scoped src="./styles/error.css"></style>

