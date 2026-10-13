<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import './signup-terms.css'

const agreements = reactive({ terms: false, privacy: false })
const router = useRouter()
const message = ref('')
const isAllAgreed = computed(() => agreements.terms && agreements.privacy)
const canContinue = computed(() => isAllAgreed.value)

function toggleAll(event) {
  agreements.terms = event.target.checked
  agreements.privacy = event.target.checked
  message.value = ''
}

function continueSignup() {
  if (!canContinue.value) return
  router.push({ path: '/signup', query: { agreeTerms: 'true' } })
}
</script>

<template>
  <main class="signup-terms-page">
    <section class="signup-terms-card" aria-labelledby="signup-terms-title">
      <h1 id="signup-terms-title">회원가입</h1>
      <p class="signup-terms-intro">인천교통공사 이용을 위해 이용약관의 동의가 필요합니다.</p>

      <label class="signup-terms-all">
        <input type="checkbox" :checked="isAllAgreed" @change="toggleAll" />
        <span>이용약관 및 개인정보처리방침에 <u>모두</u> 동의합니다.</span>
      </label>

      <div class="signup-terms-section">
        <label class="signup-terms-label">
          <input v-model="agreements.terms" type="checkbox" @change="message = ''" />
          <span>이용약관(필수)</span>
        </label>
        <div class="signup-terms-content" tabindex="0" aria-label="이용약관 내용">
          <strong>제1조</strong>
          <p>본 약관은 인천교통공사(이하 “공사”라 합니다.)가 제공하는 모든 서비스(이하 “서비스”라 합니다.)의 이용 조건 및 절차, 이용자와 공사 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.</p>
          <strong>제2조 서비스의 이용</strong>
          <p>이용자는 본 약관에 동의한 후 서비스를 이용할 수 있으며, 서비스 이용과 관련한 안내를 확인해야 합니다.</p>
          <strong>제3조 이용자의 의무</strong>
          <p>이용자는 관계 법령과 본 약관을 준수하고 다른 이용자의 서비스 이용을 방해해서는 안 됩니다.</p>
        </div>
      </div>

      <div class="signup-terms-section">
        <label class="signup-terms-label">
          <input v-model="agreements.privacy" type="checkbox" @change="message = ''" />
          <span>개인정보 처리방침(필수)</span>
        </label>
        <div class="signup-terms-content" tabindex="0" aria-label="개인정보 처리방침 내용">
          <strong>제1조 개인정보의 처리 목적</strong>
          <p>인천교통공사는 회원가입 및 서비스 제공을 위해 필요한 최소한의 개인정보를 처리합니다.</p>
          <strong>제2조 처리하는 개인정보 항목</strong>
          <p>회원 식별과 서비스 이용을 위해 아이디, 이름, 이메일 등 가입 과정에서 이용자가 입력한 정보를 처리할 수 있습니다.</p>
          <strong>제3조 개인정보의 보유 및 이용 기간</strong>
          <p>개인정보는 서비스 이용 기간 동안 보유하며, 처리 목적이 달성되면 관련 기준에 따라 파기합니다.</p>
        </div>
      </div>

      <p v-if="message" class="signup-terms-message" role="status">{{ message }}</p>
      <button class="signup-terms-next" type="button" :disabled="!canContinue" @click="continueSignup">다음</button>
    </section>
  </main>
</template>
