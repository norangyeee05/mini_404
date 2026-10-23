import { ref } from 'vue'
import { fetchStations } from '../../station/services/stationData'
export const stations = ref([])
const stationNumber = value => {
  const digits = String(value ?? '').match(/(\d+)\s*$/)?.[1]
  return digits ? Number(digits) : Number.NEGATIVE_INFINITY
}
export async function loadLineStations() {
  const result = await fetchStations()
  stations.value = [...result]
    .sort((a, b) => stationNumber(b.stationNo) - stationNumber(a.stationNo) || (a.stationOrder ?? 0) - (b.stationOrder ?? 0))
    .map(item => ({ id: item.id, name: item.name }))
  return stations.value
}
