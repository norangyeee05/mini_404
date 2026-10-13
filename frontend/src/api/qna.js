import api, { listParams, listResult } from './client'

const query = (params) => {
  const { inquiryType, status, ...list } = params || {}
  return {
    ...listParams(list),
    ...(inquiryType ? { inquiryType } : {}),
    ...(status ? { status } : {}),
  }
}
export async function getQnas(params) { return listResult((await api.get('/qna', { params: query(params) })).data) }
export async function getMyQnas(params) { return listResult((await api.get('/qna/my', { params: query(params) })).data) }
export const getQna = (id) => api.get(`/qna/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const createQna = (body) => api.post('/qna', body)
export const updateQna = (id, body) => api.put(`/qna/${encodeURIComponent(id)}`, body)
export const deleteQna = (id) => api.delete(`/qna/${encodeURIComponent(id)}`)
export async function getAdminQnas(params) { return listResult((await api.get('/admin/qna', { params: query(params) })).data) }
export const getAdminQna = (id) => api.get(`/admin/qna/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const answerQna = (id, body) => api.post(`/admin/qna/${encodeURIComponent(id)}/answer`, body)
export const deleteAdminQna = (id) => api.delete(`/admin/qna/${encodeURIComponent(id)}`)
