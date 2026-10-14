import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  withCredentials: true,
  headers: { Accept: 'application/json' },
})

api.interceptors.response.use(response => response, error => {
  if (error.response?.status === 401 && error.config?.url !== '/members/me' && !['/login', '/signup'].includes(window.location.pathname)) {
    window.dispatchEvent(new CustomEvent('api:unauthorized'))
  }
  return Promise.reject(error)
})

export function pageItems(data) {
  const payload = data?.data ?? data
  if (Array.isArray(payload)) return { items: payload, totalCount: payload.length }
  return { ...payload, items: payload?.items ?? [], totalCount: payload?.totalCount ?? 0 }
}

export function apiError(error, fallback = '요청을 처리하지 못했습니다. 다시 시도해 주세요.') {
  return error.response?.data?.message || error.response?.data?.error || fallback
}

export default api

