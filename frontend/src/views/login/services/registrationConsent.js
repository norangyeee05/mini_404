import { computed, reactive } from 'vue'

// 화면 확인용 동의 상태: 서버나 브라우저 저장소에 전송하지 않습니다.
export const registrationConsent = reactive({ terms: false, privacy: false })
export const allAgreed = computed({
  get: () => registrationConsent.terms && registrationConsent.privacy,
  set: (checked) => {
    registrationConsent.terms = checked
    registrationConsent.privacy = checked
  },
})

