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
            <p>제1조 (목적)<br />본 약관은 인천교통사공사(이하 "사공사"라 합니다.)가 제공하는 모든 서비스(이하 “서비스”라 합니다.)의 이용 조건 및 절차, 이용자와 사공사 간의 관리, 이무 및 책임사항을 규정함을 목적으로 합니다.</p>
            <p>제2조 (정의)<br />1. "이용자"만 본 약관에 따라 공사가 제공하는 서비스를 받는 회원 및 비회원을 말합니다.
              <br /> 2. "회원"이란 사공사에 개인정보를 제공하여 회원등록을 한 자로서, 사공사의 서비스를 지속적으로 이용할 수 있는 자를 말합니다.
              <br /> 3. "비회원"이란 회원가입을 하지 않고 사공사가 제공하는 서비스를 이용하는 자를 말합니다.</p>
            <p>제3조<br />이 내용은 화면 구현을 위한 더미 이용약관입니다. 실제 서비스 이용계약이나 가입은 이루어지지 않습니다.</p>
          </AgreementSection>
          <AgreementSection id="agree-privacy" v-model="registrationConsent.privacy" title="개인정보 처리방침">
            <p>제1조 (개인정보의 수집 및 이용목적)<br />인천교통사공사는 다음의 목적을 위하여 개인정보를 수집 및 이용합니다.
              <br />● 회원관리 및 본인확인
              <br />● 서비스 제공을 위한 본인인증
              <br />● 공지사항, 자료실, Q&A 등 공지 및 정보 제공
              <br />● 고객문의 처리 및 민원 처리
              <br />● 기타 원활한 서비스 제공을 위한 이용자 식별</p>
            <p>제2조 (수집하는 개인정보 항목)<br />인천교통사공사는 회원가입, 서비스 이용 등을 위해 아래와 같은 개인정보를 수집합니다.
              <br />● 필수항목 : 아이디, 비밀번호, 이름, 이메일
              <br />● 선택항목 : 연락처, 주소</p>
            <p>제3조<br />개인정보를 서버에 전송하거나 제3자에게 제공하지 않습니다. 이 내용은 화면 확인용 더미 개인정보 처리방침입니다.</p>
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
