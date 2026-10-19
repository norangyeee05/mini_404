import { ref } from 'vue'
import { fetchStations } from '../../station/services/stationData'
export const stations = ref([])
export async function loadLineStations() {
  const result = await fetchStations()
  stations.value = [...result].sort((a, b) => a.stationOrder - b.stationOrder).map(item => ({ id: item.id, name: item.name }))
  return stations.value
}
