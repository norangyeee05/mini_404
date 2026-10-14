import api, { pageItems } from './api.js'

const query = params => Object.fromEntries(Object.entries(params).filter(([, value]) => value !== '' && value != null))
export const noticesApi = {
  list: async params => pageItems((await api.get('/notices', { params: query(params) })).data),
  get: async id => (await api.get(`/notices/${id}`)).data,
  create: async body => (await api.post('/admin/notices', body)).data,
  update: async (id, body) => (await api.put(`/admin/notices/${id}`, body)).data,
  remove: id => api.delete(`/admin/notices/${id}`),
}
export const resourcesApi = {
  list: async params => pageItems((await api.get('/resources', { params: query(params) })).data),
  get: async id => (await api.get(`/resources/${id}`)).data,
  downloadFile: (resourceId,fileId) => api.get(`/resources/${resourceId}/files/${fileId}`, { responseType: 'blob' }),
  request: body => api.post('/resources/requests', body),
  adminList: async params => pageItems((await api.get('/resources', { params: query(params) })).data),
  adminGet: async id => (await api.get(`/resources/${id}`)).data,
  create: body => api.post('/admin/resources', body),
  update: (id, body) => api.put(`/admin/resources/${id}`, body),
  remove: id => api.delete(`/admin/resources/${id}`),
}
export const resourceRequestsApi = {
  list: async params => pageItems((await api.get('/admin/resource-requests', { params: query(params) })).data),
  get: async id => (await api.get(`/admin/resource-requests/${id}`)).data,
  setStatus: (id, status) => api.patch(`/admin/resource-requests/${id}/status`, { status }),
  remove: id => api.delete(`/admin/resource-requests/${id}`),
}
export const qnaApi = {
  list: async (params, mine = false) => pageItems((await api.get(mine ? '/qna/my' : '/qna', { params: query(params) })).data),
  get: async id => (await api.get(`/qna/${id}`)).data,
  create: body => api.post('/qna', body),
  update: (id, body) => api.put(`/qna/${id}`, body),
  remove: id => api.delete(`/qna/${id}`),
  adminList: async params => pageItems((await api.get('/admin/qna', { params: query(params) })).data),
  adminGet: async id => (await api.get(`/admin/qna/${id}`)).data,
  answer: (id, answer) => api.post(`/admin/qna/${id}/answer`, { answer }),
  adminRemove: id => api.delete(`/admin/qna/${id}`),
}
export const line7Api={stations:async()=>pageItems((await api.get('/line7/stations')).data).items,station:async id=>(await api.get(`/line7/stations/${id}`)).data,transfer:async id=>(await api.get(`/line7/stations/${id}/transfer`)).data}
export const adminLine7Api = {
  list: async (type, params) => pageItems((await api.get(`/admin/line7/${type}`, { params: query(params) })).data),
  get: async (type, id) => (await api.get(`/admin/line7/${type}/${id}`)).data,
  create: (type, body) => api.post(`/admin/line7/${type}`, body),
  update: (type, id, body) => api.put(`/admin/line7/${type}/${id}`, body),
  setActive: (type, id, active) => api.patch(`/admin/line7/${type}/${id}/active`, { active }),
}

