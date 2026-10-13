import { defineStore } from 'pinia'
import { getMe, logout as requestLogout } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({ user: null, role: sessionStorage.getItem('authRole'), loaded: false }),
  getters: {
    isAuthenticated: (state) => Boolean(state.user || state.role),
    isAdmin: (state) => state.role === 'ADMIN',
  },
  actions: {
    setUser(user) {
      this.user = user || null
      this.role = user?.role || null
      if (this.role) sessionStorage.setItem('authRole', this.role)
      else sessionStorage.removeItem('authRole')
      this.loaded = true
    },
    async loadMe() {
      if (this.loaded && this.user) return this.user
      try {
        const response = await getMe()
        const data = response.data?.data ?? response.data
        const member = data?.member ?? data?.user ?? data
        const user = { ...member, role: member?.role || this.role }
        this.setUser(user)
        return user
      } catch (error) {
        this.setUser(null)
        throw error
      }
    },
    async signOut() {
      try { await requestLogout() } finally { this.setUser(null) }
    },
  },
})
