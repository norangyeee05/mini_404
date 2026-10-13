import { ref } from 'vue'
// Demo accounts live only in memory; refreshing resets all changes.
const demoAccount = { memberId: 1, loginId: 'demo', password: 'demo1234', name: '홍길동', email: 'demo@example.com', phone: '010-1234-5678', role: 'USER' }
const adminAccount = { memberId: 2, loginId: 'admin', password: 'admin1234', name: '관리자', email: 'admin@example.com', phone: '010-1234-5678', role: 'ADMIN' }
const members = [{ ...demoAccount }, { ...adminAccount }]
let nextMemberId = 3
export const currentMember = ref(null)
function publicMember(member) {
  const { password, ...profile } = member
  return profile
}
export function login({ loginId, password }) {
  const member = members.find(item => item.loginId === loginId && item.password === password)
  if (!member) return null
  currentMember.value = publicMember(member)
  return currentMember.value
}
export function signup({ loginId, password, name, email, phone = '', agreeTerms }) {
  if (!agreeTerms) throw new Error('이용약관에 동의해 주세요.')
  if (members.some(member => member.loginId === loginId)) throw new Error('이미 사용 중인 아이디입니다.')
  const memberId = nextMemberId++
  members.push({ memberId, loginId, password, name, email, phone, role: 'USER' })
  return { memberId }
}
export function updateProfile({ name, email }) {
  const member = members.find(item => item.memberId === currentMember.value?.memberId)
  if (!member) throw new Error('로그인이 필요합니다.')
  if (!name.trim()) throw new Error('이름을 입력해 주세요.')
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) throw new Error('올바른 이메일을 입력해 주세요.')
  member.name = name.trim()
  member.email = email.trim()
  currentMember.value = publicMember(member)
}
export function verifyWithdrawal({ loginId, password }) {
  return members.some(item => item.memberId === currentMember.value?.memberId && item.loginId === loginId.trim() && item.password === password)
}
export function withdraw(credentials) {
  if (!verifyWithdrawal(credentials)) throw new Error('아이디 및 비밀번호가 올바르지 않습니다.')
  const index = members.findIndex(item => item.memberId === currentMember.value.memberId)
  // Keep the built-in demo account available for repeated UI testing.
  if (members[index].memberId === demoAccount.memberId) members[index] = { ...demoAccount }
  else members.splice(index, 1)
  logout()
}
export function logout() { currentMember.value = null }


