import api, { listParams, listResult } from './client'

export const getDashboard = () => api.get('/admin/dashboard').then((response) => response.data?.data ?? response.data)
export async function getMembers(params) { return listResult((await api.get('/admin/members', { params: listParams(params) })).data) }
export const getMember = (id) => api.get(`/admin/members/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const updateMemberStatus = (id, body) => api.patch(`/admin/members/${encodeURIComponent(id)}/status`, body)
export async function getResourceRequests(params) { return listResult((await api.get('/admin/resource-requests', { params: listParams(params) })).data) }
export const getResourceRequest = (id) => api.get(`/admin/resource-requests/${encodeURIComponent(id)}`).then((response) => response.data?.data ?? response.data)
export const updateResourceRequestStatus = (id, body) => api.patch(`/admin/resource-requests/${encodeURIComponent(id)}/status`, body)
export const deleteResourceRequest = (id) => api.delete(`/admin/resource-requests/${encodeURIComponent(id)}`)

export const getAdminTrains = (params) => api.get('/admin/line7/trains', { params: listParams(params) }).then((r) => r.data?.data ?? r.data)
export const getAdminTrain = (id) => api.get(`/admin/line7/trains/${encodeURIComponent(id)}`).then((r) => r.data?.data ?? r.data)
export const createAdminTrain = (body) => api.post('/admin/line7/trains', body)
export const updateAdminTrain = (id, body) => api.put(`/admin/line7/trains/${encodeURIComponent(id)}`, body)
export const updateAdminTrainActive = (id, body) => api.patch(`/admin/line7/trains/${encodeURIComponent(id)}/active`, body)
export const getAdminStations = (params) => api.get('/admin/line7/stations', { params: listParams(params) }).then((r) => r.data?.data ?? r.data)
export const getAdminStation = (id) => api.get(`/admin/line7/stations/${encodeURIComponent(id)}`).then((r) => r.data?.data ?? r.data)
export const createAdminStation = (body) => api.post('/admin/line7/stations', body)
export const updateAdminStation = (id, body) => api.put(`/admin/line7/stations/${encodeURIComponent(id)}`, body)
export const updateAdminStationActive = (id, body) => api.patch(`/admin/line7/stations/${encodeURIComponent(id)}/active`, body)
