import { ref } from 'vue'
import api from './api.js'

export const currentMember = ref(null)
const ROLE_KEY = 'frontend.memberRole'
export async function hydrateMember() {
  try { const { data } = await api.get('/members/me'); currentMember.value = { ...(data?.data ?? data), role: sessionStorage.getItem(ROLE_KEY) || undefined } }
  catch { currentMember.value = null }
  return currentMember.value
}
export async function login(credentials) {
  const { data } = await api.post('/auth/login', credentials)
  currentMember.value = data?.data ?? data
  if (currentMember.value.role) sessionStorage.setItem(ROLE_KEY, currentMember.value.role)
  return currentMember.value
}
export async function checkLoginId(loginId) {
  const { data } = await api.get('/auth/check-id', { params: { loginId } })
  return (data?.data ?? data).available
}
export async function signup(details) {
  const { passwordConfirm, phone, ...payload } = details
  return (await api.post('/auth/signup', payload)).data
}
export async function updateProfile(profile) {
  const { data } = await api.put('/members/me', profile)
  await hydrateMember()
  return data?.data ?? data
}
export function verifyWithdrawal(credentials) {
  return credentials.loginId.trim() === currentMember.value?.loginId && credentials.password === credentials.confirmation
}
export async function withdraw(credentials) {
  await api.delete('/members/me', { data: { loginId: credentials.loginId, password: credentials.password, passwordConfirm: credentials.confirmation } })
  currentMember.value = null
  sessionStorage.removeItem(ROLE_KEY)
}
export async function logout() {
  if (currentMember.value) await api.post('/auth/logout')
  currentMember.value = null
  sessionStorage.removeItem(ROLE_KEY)
}

