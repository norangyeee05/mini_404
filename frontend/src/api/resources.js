import api, { listParams, listResult } from './client'

export async function getResources(params) { return listResult((await api.get('/resources', { params: listParams(params) })).data) }
export const getResource = (id) => api.get(`/resources/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const createResourceRequest = (body) => api.post('/resources/requests', body)
export const createResource = (body) => api.post('/admin/resources', body)
export const updateResource = (id, body) => api.put(`/admin/resources/${encodeURIComponent(id)}`, body)
export const deleteResource = (id) => api.delete(`/admin/resources/${encodeURIComponent(id)}`)
export const downloadResourceFile = (resourceId, fileId) => api.get(`/resources/${encodeURIComponent(resourceId)}/files/${encodeURIComponent(fileId)}`, { responseType: 'blob' })
