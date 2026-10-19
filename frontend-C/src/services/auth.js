import { ref } from 'vue'
import { api } from './backendApi'

const storageKey = 'frontend-auth-session'
function readSession() {
  try { return JSON.parse(localStorage.getItem(storageKey)) || null } catch { return null }
}
export const currentMember = ref(readSession())
let restorePromise

function persist(member) {
  currentMember.value = member
  if (member) localStorage.setItem(storageKey, JSON.stringify(member))
  else localStorage.removeItem(storageKey)
}

export async function restoreSession() {
  if (!restorePromise) restorePromise = api.auth.me().then(async session => {
    if (!session?.authenticated || session.role === 'GUEST') {
      persist(null)
      return null
    }
    const cached = currentMember.value || {}
    if (session.adminAccount) {
      const member = { ...cached, memberId: session.memberId, name: session.name, role: session.role, adminAccount: true }
      persist(member)
      return member
    }
    const profile = await api.members.me()
    const member = { ...cached, memberId: session.memberId, loginId: profile.loginId, name: profile.name, email: profile.email, phone: profile.phone || '', role: session.role, adminAccount: false }
    persist(member)
    return member
  }).catch(error => {
    if (error.response?.status === 401) persist(null)
    throw error
  }).finally(() => { restorePromise = null })
  return restorePromise
}

export async function login(credentials) {
  const result = await api.auth.login(credentials)
  const member = { memberId: result.memberId, name: result.name, role: result.role, adminAccount: result.adminAccount, loginId: credentials.loginId }
  persist(member)
  return member
}

export async function signup({ loginId, password, name, email, phone, agreeTerms, agreePrivacy = agreeTerms }) {
  if (!agreeTerms || !agreePrivacy) throw new Error('이용약관과 개인정보 처리방침에 동의해 주세요.')
  return api.auth.signup({ loginId, password, name, email, ...(phone ? { phone } : {}), agreeTerms, agreePrivacy })
}

export async function updateProfile({ name, email, phone, password }) {
  if (!name.trim()) throw new Error('이름을 입력해 주세요.')
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) throw new Error('올바른 이메일을 입력해 주세요.')
  const update = currentMember.value?.adminAccount ? api.admin.updateMe : api.members.updateMe
  if (phone != null && phone.trim().length > 20) throw new Error('전화번호는 20자 이내로 입력해 주세요.')
  const result = await update({ name: name.trim(), email: email.trim(), ...(phone != null ? { phone: phone.trim() } : {}), ...(password ? { password } : {}) })
  persist({ ...currentMember.value, name: name.trim(), email: email.trim(), ...(phone != null ? { phone: phone.trim() } : {}), memberId: result?.memberId ?? currentMember.value?.memberId })
  return result
}

export async function withdraw({ loginId, password, confirmation }) {
  await api.members.withdraw({ loginId: loginId.trim(), password, passwordConfirm: confirmation })
  persist(null)
}

export async function logout() {
  try { await api.auth.logout() } finally { persist(null) }
}
