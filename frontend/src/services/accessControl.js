import { ref } from 'vue'
import api from './api.js'
import { currentMember, hydrateMember } from './auth.js'
export const accessDenied = ref(false)
export async function requireMember(to) {
  if (!currentMember.value) await hydrateMember()
  if (to.meta.requiresAdmin && currentMember.value && !currentMember.value.role) {
    try { await api.get('/admin/dashboard'); currentMember.value.role = 'ADMIN' }
    catch (error) { if (error.response?.status === 403) currentMember.value.role = 'USER'; else if (error.response?.status === 401) currentMember.value = null }
  }
  accessDenied.value = false
  if (to.meta.requiresAdmin && currentMember.value?.role !== 'ADMIN') return false
  if (to.meta.requiresMember && !currentMember.value) return false
  return true
}
