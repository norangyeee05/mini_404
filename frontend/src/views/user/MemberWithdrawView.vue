<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { withdrawMe } from '../../api/auth'
import { useAuthStore } from '../../stores/auth'
import PageHeader from '../../components/PageHeader.vue'
import PageFooter from '../../components/PageFooter.vue'
import './member-withdraw.css'

const memberId = ref('')
const router = useRouter()
const auth = useAuthStore()
const password = ref('')
const passwordConfirm = ref('')
const formError = ref('')
const showConfirmation = ref(false)
const showSuccess = ref(false)
const headerRole = computed(() => showSuccess.value ? 'guest' : 'member')

function submitWithdrawal() {
  formError.value = ''

  if (!memberId.value.trim() || !password.value || !passwordConfirm.value) {
    formError.value = '아이디와 비밀번호를 모두 입력해주세요.'
    return
  }

  if (password.value !== passwordConfirm.value) {
    formError.value = '비밀번호 확인이 일치하지 않습니다.'
    return
  }

  showConfirmation.value = true
}

function closeConfirmation() {
  showConfirmation.value = false
}

async function confirmWithdrawal() {
  formError.value = ''
  try {
    await withdrawMe({ loginId: memberId.value.trim(), password: password.value, passwordConfirm: passwordConfirm.value })
    auth.setUser(null)
  } catch (error) { formError.value = errorMessageFrom(error); showConfirmation.value = false; return }
  showConfirmation.value = false
  showSuccess.value = true
}

function errorMessageFrom(error) { return error?.response?.data?.message || '회원 탈퇴 요청을 처리하지 못했습니다.' }

function finishWithdrawal() {
  showSuccess.value = false
  router.push('/')
}
</script>

<template>
  <div class="member-withdraw-page">
    <PageHeader :role="headerRole" />

    <main v-if="!showSuccess" class="member-withdraw-main">
      <section class="member-withdraw-hero" aria-labelledby="member-withdraw-page-title">
        <div class="member-withdraw-breadcrumb">HOME <span>›</span> 마이페이지 <span>›</span> 회원탈퇴</div>
        <h1 id="member-withdraw-page-title">회원탈퇴</h1>
        <p>언제나 안전하고 편리한 교통서비스를 위해<br />최선을 다하겠습니다.</p>
      </section>

      <div class="member-withdraw-layout">
        <aside class="member-withdraw-sidebar" aria-label="마이페이지 메뉴">
          <a class="member-withdraw-sidebar__link" href="/mypage">회원정보</a>
          <a class="member-withdraw-sidebar__link is-active" href="/mypage/withdraw" aria-current="page">회원 탈퇴</a>
        </aside>

        <section class="member-withdraw-panel" aria-labelledby="member-withdraw-heading">
          <h2 id="member-withdraw-heading">회원탈퇴</h2>
          <p class="member-withdraw-description">회원 탈퇴 시 모든 정보가 삭제되며, 복구할 수 없습니다.<br />신중하게 결정해주세요.</p>

          <form class="member-withdraw-form" @submit.prevent="submitWithdrawal">
            <div class="member-withdraw-fields">
              <label for="withdraw-id">아이디</label>
              <input id="withdraw-id" v-model="memberId" autocomplete="username" required />
              <label for="withdraw-password">비밀번호</label>
              <input id="withdraw-password" v-model="password" type="password" autocomplete="current-password" required />
              <label for="withdraw-password-confirm">비밀번호 확인</label>
              <input id="withdraw-password-confirm" v-model="passwordConfirm" type="password" autocomplete="new-password" required />
            </div>
            <p v-if="formError" class="member-withdraw-message" role="status">{{ formError }}</p>
            <button class="member-withdraw-submit" type="submit">회원 탈퇴</button>
          </form>
        </section>
      </div>
    </main>

    <PageFooter />

    <div v-if="showConfirmation || showSuccess" class="withdraw-confirm-backdrop" @click.self="showSuccess ? finishWithdrawal() : closeConfirmation()" @keydown.esc="showSuccess ? finishWithdrawal() : closeConfirmation()" tabindex="-1">
      <section v-if="showConfirmation" class="withdraw-confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="withdraw-confirm-title" aria-describedby="withdraw-confirm-description">
        <button class="withdraw-confirm-close" type="button" aria-label="팝업 닫기" @click="closeConfirmation">×</button>
        <div class="withdraw-confirm-icon" aria-hidden="true">!</div>
        <h2 id="withdraw-confirm-title">탈퇴하시겠습니까?</h2>
        <p id="withdraw-confirm-description">탈퇴 후엔 계정 복구가 어렵습니다.<br />진짜로 탈퇴하겠습니까?</p>
        <button class="withdraw-confirm-button" type="button" @click="confirmWithdrawal">확인</button>
      </section>

      <section v-else class="withdraw-confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="withdraw-success-title" aria-describedby="withdraw-success-description">
        <button class="withdraw-confirm-close" type="button" aria-label="팝업 닫기" @click="finishWithdrawal">×</button>
        <div class="withdraw-confirm-icon withdraw-confirm-icon--success" aria-hidden="true">✓</div>
        <h2 id="withdraw-success-title">탈퇴되었습니다.</h2>
        <p id="withdraw-success-description">그동안 감사했습니다.</p>
        <button class="withdraw-confirm-button" type="button" @click="finishWithdrawal">확인</button>
      </section>
    </div>
  </div>
</template>
