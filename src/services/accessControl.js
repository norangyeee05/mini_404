import { ref } from 'vue'
import { currentMember, restoreSession } from './auth.js'
export const accessDenied = ref(false)
export async function requireMember(to) {
  if (!to.meta.requiresAdmin && !to.meta.requiresMember) return true
  if (currentMember.value) {
    try { await restoreSession() } catch {}
  }
  if (to.meta.requiresAdmin && currentMember.value?.role !== 'ADMIN') { accessDenied.value = true; return false }
  if (to.meta.requiresMember && !currentMember.value) { accessDenied.value = true; return false }
  return true
}
