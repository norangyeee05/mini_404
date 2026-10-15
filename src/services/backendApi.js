import axios from 'axios'
import { createMockAdapter } from './mockApi'

export const isMockApiEnabled = import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_API === 'true'

const client = axios.create({ baseURL: '/api', withCredentials: true })
if (isMockApiEnabled) client.defaults.adapter = createMockAdapter()

client.interceptors.response.use(response => response, error => {
  const status = error.response?.status
  const message = error.response?.data?.message || error.response?.data?.detail
  error.userMessage = message || (status === 401 ? '로그인이 필요합니다.' : status === 403 ? '접근 권한이 없습니다.' : status === 404 ? '요청한 정보를 찾을 수 없습니다.' : status === 409 ? '이미 등록된 정보입니다.' : '서버와 통신하지 못했습니다. 잠시 후 다시 시도해 주세요.')
  return Promise.reject(error)
})

const data = response => response.data
const list = (url, params) => client.get(url, { params }).then(data)
const json = (method, url, body) => client.request({ method, url, data: body }).then(data)
const multipart = (method, url, fields, files = []) => {
  const body = new FormData()
  for (const [key, value] of Object.entries(fields)) if (value !== undefined && value !== null) body.append(key, value)
  for (const file of files.filter(Boolean)) body.append('files', file)
  return client.request({ method, url, data: body }).then(data)
}

export const api = {
  client,
  auth: {
    signup: body => json('post', '/auth/signup', body),
    checkId: loginId => client.get('/auth/check-id', { params: { loginId } }).then(data),
    login: body => json('post', '/auth/login', body),
    logout: () => client.post('/auth/logout').then(data),
  },
  members: {
    me: () => client.get('/members/me').then(data),
    updateMe: body => json('put', '/members/me', body),
    withdraw: body => client.delete('/members/me', { data: body }).then(data),
    list: params => list('/admin/members', params),
    detail: id => client.get(`/admin/members/${id}`).then(data),
    status: (id, status) => json('patch', `/admin/members/${id}/status`, { status }),
  },
  notices: {
    list: params => list('/notices', params),
    detail: id => client.get(`/notices/${id}`).then(data),
    create: body => json('post', '/admin/notices', body),
    update: (id, body) => json('put', `/admin/notices/${id}`, body),
    remove: id => client.delete(`/admin/notices/${id}`).then(data),
  },
  resources: {
    list: params => list('/resources', params),
    detail: id => client.get(`/resources/${id}`).then(data),
    download: (id, fileId) => client.get(`/resources/${id}/files/${fileId}`, { responseType: 'blob' }).then(data),
    request: (fields, files = []) => files.some(Boolean) ? multipart('post', '/resources/requests', fields, files) : json('post', '/resources/requests', fields),
    create: (fields, files = []) => files.some(Boolean) ? multipart('post', '/admin/resources', fields, files) : json('post', '/admin/resources', fields),
    update: (id, fields, files = []) => files.some(Boolean) ? multipart('put', `/admin/resources/${id}`, fields, files) : json('put', `/admin/resources/${id}`, fields),
    remove: id => client.delete(`/admin/resources/${id}`).then(data),
  },
  qna: {
    list: params => list('/qna', params),
    my: params => list('/qna/my', params),
    detail: id => client.get(`/qna/${id}`).then(data),
    create: (fields, files = []) => files.some(Boolean) ? multipart('post', '/qna', fields, files) : json('post', '/qna', fields),
    update: (id, fields, files = []) => files.some(Boolean) ? multipart('put', `/qna/${id}`, fields, files) : json('put', `/qna/${id}`, fields),
    remove: id => client.delete(`/qna/${id}`).then(data),
    adminList: params => list('/admin/qna', params),
    adminDetail: id => client.get(`/admin/qna/${id}`).then(data),
    answer: (id, answer) => json('post', `/admin/qna/${id}/answer`, { answer }),
    adminRemove: id => client.delete(`/admin/qna/${id}`).then(data),
  },
  dashboard: () => client.get('/admin/dashboard').then(data),
  line: {
    stations: () => client.get('/line7/stations').then(data),
    station: id => client.get(`/line7/stations/${id}`).then(data),
    transfer: id => client.get(`/line7/stations/${id}/transfer`).then(data),
    trains: params => list('/admin/line7/trains', params),
    train: id => client.get(`/admin/line7/trains/${id}`).then(data),
    createTrain: body => json('post', '/admin/line7/trains', body),
    updateTrain: (id, body) => json('put', `/admin/line7/trains/${id}`, body),
    setTrainActive: (id, active) => json('patch', `/admin/line7/trains/${id}/active`, { active }),
    stationsAdmin: params => list('/admin/line7/stations', params),
    stationAdmin: id => client.get(`/admin/line7/stations/${id}`).then(data),
    createStation: body => json('post', '/admin/line7/stations', body),
    updateStation: (id, body) => json('put', `/admin/line7/stations/${id}`, body),
    setStationActive: (id, active) => json('patch', `/admin/line7/stations/${id}/active`, { active }),
  },
  resourceRequests: {
    list: params => list('/admin/resource-requests', params),
    detail: id => client.get(`/admin/resource-requests/${id}`).then(data),
    setStatus: (id, status) => json('patch', `/admin/resource-requests/${id}/status`, { status }),
    remove: id => client.delete(`/admin/resource-requests/${id}`).then(data),
  },
}

export function responseItems(response) { return Array.isArray(response) ? response : response?.items || [] }
export function pageCountFrom(response, size = 10) { return Math.max(1, Math.ceil((response?.totalCount || 0) / size)) }
export function apiError(error) { return error?.userMessage || error?.message || '요청을 처리하지 못했습니다.' }
