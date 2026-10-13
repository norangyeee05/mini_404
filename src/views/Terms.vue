<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import AgreementSection from '../components/AgreementSection.vue'
import { registrationConsent, allAgreed } from '../services/registrationConsent'
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
<style scoped>
.terms-page { min-height: 100vh; min-height: 100svh; padding: 54px 20px 52px; background: #e2ecee; color: #155369; }
.terms-panel { width: min(720px, 100%); min-height: 918px; margin: 0 auto; padding: 39px 60px 64px; border-radius: 12px; background: #f0f5f7; }
h1 { margin: 0 0 23px; font-size: 36px; font-weight: 800; line-height: 48px; }
.terms-description { margin: 0 0 43px 4px; color: #858585; font-size: 16px; line-height: 23px; }
.agree-all { display: flex; align-items: center; gap: 9px; min-height: 60px; padding: 13px 20px; background: #d2e6e9; font-size: 24px; font-weight: 700; line-height: 34px; cursor: pointer; }
.agree-all input { appearance: none; flex: 0 0 18px; width: 18px; height: 18px; margin: 0; border: 2px solid #155369; border-radius: 0; cursor: pointer; }
.agree-all input:checked { background: #155369; box-shadow: inset 0 0 0 3px #d2e6e9; }
.agree-all input:indeterminate { background: linear-gradient(#155369, #155369) center / 10px 3px no-repeat; }
input:focus-visible, button:focus-visible { outline: 2px solid #155369; outline-offset: 4px; }
.agreement-list { display: grid; gap: 32px; margin: 31px 10px 0 3px; }
.terms-actions { position: relative; margin-top: 109px; }
.terms-next { display: block; width: min(400px, 100%); height: 40px; margin: 0 auto; padding: 0 16px; border: 0; border-radius: 8px; background: #eee; color: #000; font-size: 16px; cursor: pointer; }
.terms-next:hover { background: #e0e0e0; }
.terms-error { position: absolute; right: 0; bottom: 48px; left: 0; margin: 0; color: #f00; font-size: 14px; text-align: center; }
@media (max-width: 760px) { .terms-page { padding: 24px 16px; } .terms-panel { min-height: auto; padding: 32px 24px 40px; } .agree-all { font-size: 20px; } .agreement-list { margin-right: 0; margin-left: 0; } .terms-actions { margin-top: 80px; } }
@media (max-width: 480px) { .terms-panel { padding: 26px 18px 32px; } h1 { font-size: 30px; margin-bottom: 16px; } .terms-description { margin-left: 0; margin-bottom: 28px; font-size: 14px; } .agree-all { padding: 12px; font-size: 18px; line-height: 28px; } .terms-actions { margin-top: 72px; } }
</style>
