import api from './client'

export const getStations = () => api.get('/line7/stations').then((r) => r.data?.data ?? r.data)
export const getStation = (id) => api.get(`/line7/stations/${encodeURIComponent(id)}`).then((r) => r.data?.data ?? r.data)
export const getStationTransfers = (id) => api.get(`/line7/stations/${encodeURIComponent(id)}/transfer`).then((r) => r.data?.data ?? r.data)
