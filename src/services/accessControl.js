import { ref } from 'vue'
import { currentMember } from './mockAuth.js'
export const accessDenied = ref(false)
export function requireMember(to) {
  if (to.meta.requiresAdmin && currentMember.value?.role !== 'ADMIN') {
    accessDenied.value = true
    return false
  }
  if (to.meta.requiresMember && !currentMember.value) {
    accessDenied.value = true
    return false
  }
  return true
}

