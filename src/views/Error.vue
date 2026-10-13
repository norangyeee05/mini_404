<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import image401 from '../assets/errors/401.png'
import image403 from '../assets/errors/403.png'
import image404 from '../assets/errors/404.png'
import image500 from '../assets/errors/500.png'
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
<style scoped>
.error-page { display: flex; align-items: flex-start; min-height: 100vh; min-height: 100svh; padding: clamp(80px, 33.3vh, 341px) 44px 64px; background: #e2ecee; }
.error-layout { display: grid; grid-template-columns: 477px minmax(0, 715px); align-items: center; gap: 94px; width: 100%; max-width: 1352px; margin: 0 auto; }
.error-illustration { display: block; width: 100%; height: auto; border-radius: 50px; }
.error-content { min-width: 0; }
h1 { margin: 0 0 31px; color: #155369; font-size: 48px; font-weight: 800; line-height: 1.35; letter-spacing: -1px; word-break: keep-all; }
.error-description { margin: 0; color: #858585; font-size: 24px; line-height: 36px; word-break: keep-all; }
.error-description span { display: block; }
.error-actions { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 11px; margin-top: 54px; }
.error-button { display: flex; justify-content: center; align-items: center; min-height: 40px; padding: 8px 12px; border: 0; border-radius: 8px; font-size: 16px; font-weight: 500; line-height: 24px; text-align: center; text-decoration: none; cursor: pointer; }
.error-button--primary { background: #647bdf; color: #fff; }
.error-button--secondary { background: #fff; color: #3f7184; }
.error-button:hover { filter: brightness(.95); }
.error-button:focus-visible { outline: 3px solid #155369; outline-offset: 4px; }
.error-page--404 .error-layout { grid-template-columns: 477px minmax(0, 715px); gap: 62px; max-width: 1288px; }
@media (max-width: 1300px) { .error-layout, .error-page--404 .error-layout { grid-template-columns: minmax(280px, .8fr) minmax(0, 1.2fr); gap: 48px; } h1 { font-size: 38px; } .error-description { font-size: 20px; line-height: 32px; } }
@media (max-width: 900px) { .error-page { padding: 64px 24px; } .error-layout, .error-page--404 .error-layout { grid-template-columns: 1fr; gap: 36px; max-width: 600px; } .error-illustration { max-width: 477px; margin: 0 auto; } .error-content { text-align: center; } h1 { font-size: 34px; margin-bottom: 22px; } .error-actions { margin-top: 32px; } }
@media (max-width: 480px) { .error-page { padding: 48px 20px; } .error-illustration { border-radius: 32px; } h1 { font-size: 28px; } .error-description { font-size: 17px; line-height: 28px; } .error-actions { grid-template-columns: 1fr; gap: 12px; } .error-button { min-height: 44px; } }
@media (min-width: 901px) and (max-height: 650px) { .error-page { padding-top: 80px; } }
</style>
