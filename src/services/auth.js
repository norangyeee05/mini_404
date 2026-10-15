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
  if (!currentMember.value) return null
  if (!restorePromise) restorePromise = api.members.me().then(profile => {
    const cached = currentMember.value
    if (!cached) return null
    const member = { ...cached, loginId: profile.loginId, name: profile.name, email: profile.email }
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
  const member = { memberId: result.memberId, name: result.name, role: result.role, loginId: credentials.loginId }
  persist(member)
  return member
}

export async function signup({ loginId, password, name, email, agreeTerms }) {
  if (!agreeTerms) throw new Error('이용약관에 동의해 주세요.')
  return api.auth.signup({ loginId, password, name, email, agreeTerms })
}

export async function updateProfile({ name, email, password }) {
  if (!name.trim()) throw new Error('이름을 입력해 주세요.')
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) throw new Error('올바른 이메일을 입력해 주세요.')
  const result = await api.members.updateMe({ name: name.trim(), email: email.trim(), ...(password ? { password } : {}) })
  persist({ ...currentMember.value, name: name.trim(), email: email.trim(), memberId: result?.memberId ?? currentMember.value?.memberId })
  return result
}

export async function withdraw({ loginId, password, confirmation }) {
  await api.members.withdraw({ loginId: loginId.trim(), password, passwordConfirm: confirmation })
  persist(null)
}

export async function logout() {
  try { await api.auth.logout() } finally { persist(null) }
}
