import { ref } from 'vue'
import { currentMember, restoreSession } from './auth.js'
export const accessDenied = ref(false)
export async function requireMember(to) {
  if (!to.meta.requiresAdmin && !to.meta.requiresMember) return true
  try { await restoreSession() } catch {}
  if (to.meta.requiresAdmin && !['ADMIN', 'WEB_ADMIN'].includes(currentMember.value?.role)) { accessDenied.value = true; return false }
  if (to.meta.requiresFullAdmin && currentMember.value?.role !== 'ADMIN') { accessDenied.value = true; return false }
  if (to.meta.requiresRegularMember && currentMember.value?.role !== 'USER') { accessDenied.value = true; return false }
  if (to.meta.requiresMember && !currentMember.value) { accessDenied.value = true; return false }
  return true
}
