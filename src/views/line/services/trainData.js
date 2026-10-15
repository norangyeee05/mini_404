import { ref } from 'vue'
import { api, isMockApiEnabled, responseItems } from '../../../services/backendApi'
import { directionToApi, trainFromApi, trainStatusToApi } from '../../../services/apiMappers'
export const trains = ref([])
export async function fetchTrains(params = {}) {
  const result = await api.line.trains(params)
  trains.value = responseItems(result).map(trainFromApi)
  return { ...result, items: trains.value }
}
export async function fetchTrain(id) { return trainFromApi(await api.line.train(id)) }
export async function saveTrain(values, id) {
  const existing = id ? await fetchTrain(id) : null
  if (!id && !isMockApiEnabled) throw new Error('열차 등록 API의 필수 lineId가 기존 등록 화면에 없어 등록 요청을 구성할 수 없습니다.')
  if (!existing?.lineId && !isMockApiEnabled) throw new Error('열차 상세 응답에 lineId가 없어 수정 요청을 구성할 수 없습니다.')
  const body = { lineId: existing?.lineId ?? 7, trainNo: values.number, section: values.section, driver: values.operator, direction: directionToApi[values.direction] || values.direction, status: trainStatusToApi[values.status] || values.status, active: values.active }
  const result = id ? await api.line.updateTrain(id, body) : await api.line.createTrain(body)
  return fetchTrain(id ?? result.trainId)
}
export async function deleteTrain(id) { await api.line.setTrainActive(id, false); trains.value = trains.value.map(row => row.id === String(id) ? { ...row, active: false } : row); return true }
