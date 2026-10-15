import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'

export const stationDetails = ref([])
export async function fetchStations() {
  const response = await api.line.stations()
  stationDetails.value = responseItems(response).map(item => ({ id: String(item.stationId), name: item.stationName, stationOrder: item.stationOrder }))
  return stationDetails.value
}
export async function fetchStation(id) {
  const item = await api.line.station(id)
  return { ...item, id: String(id), name: item.stationName, english: '', platform: item.usageInfo || '', restroom: '', crossing: '', date: item.updatedAt || '' }
}
