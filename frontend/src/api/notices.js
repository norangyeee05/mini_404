import api, { listParams, listResult } from './client'

export async function getNotices(params) { return listResult((await api.get('/notices', { params: listParams(params) })).data) }
export const getNotice = (id) => api.get(`/notices/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const createNotice = (body) => api.post('/admin/notices', body)
export const updateNotice = (id, body) => api.put(`/admin/notices/${encodeURIComponent(id)}`, body)
export const deleteNotice = (id) => api.delete(`/admin/notices/${encodeURIComponent(id)}`)
