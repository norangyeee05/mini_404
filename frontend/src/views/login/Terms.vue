<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import AgreementSection from './components/AgreementSection.vue'
import { registrationConsent, allAgreed } from './services/registrationConsent'
const router = useRouter()
const showError = ref(false)
const partiallyAgreed = computed(() => registrationConsent.terms !== registrationConsent.privacy)
watch(allAgreed, () => { showError.value = false })
function next() {
  if (!allAgreed.value) { showError.value = true; return }
  router.push('/signup')
}
</script>
<template>
  <main class="terms-page">
    <section class="terms-panel" aria-labelledby="terms-title">
      <h1 id="terms-title">회원가입</h1>
      <p class="terms-description">인천교통사공사 이용을 위해 이용약관의 동의가 필요합니다.</p>
      <form @submit.prevent="next">
        <label class="agree-all" for="agree-all">
          <input id="agree-all" v-model="allAgreed" :indeterminate="partiallyAgreed" type="checkbox" />
          <span>이용약관 및 개인정보처리방침에 <u>모두</u> 동의합니다.</span>
        </label>
        <div class="agreement-list">
          <AgreementSection id="agree-terms" v-model="registrationConsent.terms" title="이용약관">
            <p>이용약관 전문은 API 명세와 프로젝트 자료에 포함되어 있지 않습니다. 실제 약관 문안을 서비스 운영 주체에서 제공해야 합니다.</p>
            
            
          </AgreementSection>
          <AgreementSection id="agree-privacy" v-model="registrationConsent.privacy" title="개인정보 처리방침">
            <p>개인정보 처리방침 전문은 API 명세와 프로젝트 자료에 포함되어 있지 않습니다. 실제 처리 목적, 항목, 보유기간과 이용자 권리를 서비스 운영 주체에서 제공해야 합니다.</p>
            
            
          </AgreementSection>
        </div>
        <div class="terms-actions">
          <p v-if="showError" class="terms-error" role="alert">이용약관과 개인정보 처리방침에 모두 동의해 주세요.</p>
          <button class="terms-next" type="submit">다음</button>
        </div>
      </form>
    </section>
  </main>
</template>
<style scoped src="./styles/terms.css"></style>

