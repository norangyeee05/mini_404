import axios from 'axios'
import router from '../router'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: { Accept: 'application/json' },
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const status = error.response?.status
    const requestUrl = error.config?.url || ''
    if (status === 401 && !requestUrl.includes('/auth/login') && !requestUrl.includes('/members/me')) {
      await router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    } else if (status === 403 && router.currentRoute.value.name !== 'forbidden') {
      await router.push('/error/403')
    } else if (status === 404 && router.currentRoute.value.name !== 'not-found') {
      await router.push('/error/404')
    } else if (status >= 500 && router.currentRoute.value.name !== 'server-error') {
      await router.push('/error/500')
    }
    return Promise.reject(error)
  },
)

export function errorMessage(error, fallback = '요청을 처리하지 못했습니다.') {
  const status = error.response?.status
  if (status === 400) return error.response?.data?.message || '입력값을 확인해주세요.'
  if (status === 404) return error.response?.data?.message || '요청한 정보를 찾을 수 없습니다.'
  if (status === 409) return error.response?.data?.message || '이미 사용 중인 정보입니다.'
  return error.response?.data?.message || fallback
}

export function listParams({ page = 1, size = 10, searchType, keyword, ...filters } = {}) {
  return {
    page,
    size,
    ...(searchType && searchType !== 'all' ? { searchType } : {}),
    ...(keyword !== undefined ? { keyword } : {}),
    ...filters,
  }
}

export function listResult(data) {
  const result = data?.data ?? data ?? {}
  return {
    items: Array.isArray(result.items) ? result.items : [],
    totalCount: Number(result.totalCount) || 0,
    page: Number(result.page) || 1,
    size: Number(result.size) || 10,
  }
}

export default api
