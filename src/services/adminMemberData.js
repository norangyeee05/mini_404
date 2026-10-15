import { ref } from 'vue'
import { api, responseItems } from './backendApi'
import { memberFromApi, memberStatusToApi } from './apiMappers'
export const adminMembers = ref([])
export const memberStatuses = ['활성', '임시차단', '강퇴']
export async function fetchAdminMembers(params = {}) {
  const response = await api.members.list(params)
  adminMembers.value = responseItems(response).map(memberFromApi)
  return { ...response, items: adminMembers.value }
}
export async function fetchAdminMember(id) { return memberFromApi(await api.members.detail(id)) }
export async function updateMemberStatus(memberId, status) {
  const apiStatus = memberStatusToApi[status]
  if (!apiStatus) throw new Error('지원하지 않는 회원 상태입니다.')
  const response = await api.members.status(memberId, apiStatus)
  const updated = memberFromApi(response)
  adminMembers.value = adminMembers.value.map(member => String(member.memberId) === String(memberId) ? { ...member, status: updated.status } : member)
  return updated
}
