import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'
import { directionToApi, trainFromApi, trainStatusToApi } from '../../../services/apiMappers'
export const trains = ref([])
export async function fetchTrains(params = {}) {
  const result = await api.line.trains(params)
  trains.value = responseItems(result).map(trainFromApi)
  return { ...result, items: trains.value, totalCount: result.totalCount ?? result.pageInfo?.totalElements ?? trains.value.length }
}
export async function fetchTrain(id) { return trainFromApi(await api.line.train(id)) }
export async function saveTrain(values, id) {
  const lines = await api.line.lines()
  const lineId = lines.find(line => line.name === '7호선')?.id
  if (!lineId) throw new Error('DB에서 7호선 노선 정보를 찾지 못했습니다.')
  const dateToApi = value => value ? String(value).trim().replaceAll('.', '-') : null
  const number = String(values.number || '').replace(/^7L-/, '')
  if (!/^\d{4}$/.test(number)) throw new Error('열차 번호는 7L- 뒤에 숫자 4자리로 입력해 주세요.')
  const body = { lineId, trainNo: `7L-${number}`, section: values.section, driver: values.operator, direction: directionToApi[values.direction] || values.direction, status: trainStatusToApi[values.status] || values.status, active: values.active, registeredAt: dateToApi(values.registered), inspectionDate: dateToApi(values.inspection) }
  const result = id ? await api.line.updateTrain(id, body) : await api.line.createTrain(body)
  return fetchTrain(id ?? result.trainId ?? result.id)
}
export async function deleteTrain(id) { await api.line.setTrainActive(id, false); trains.value = trains.value.map(row => row.id === String(id) ? { ...row, active: false } : row); return true }
