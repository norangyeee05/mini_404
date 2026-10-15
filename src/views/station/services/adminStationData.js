import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'
import { facilityStatusToApi, stationFromApi } from '../../../services/apiMappers'

export const adminStations = ref([])
export const usageOptions = ['상대식 승강장', '섬식 승강장', '반대편 횡단 가능', '화장실 개찰구 밖', '화장실 개찰구 안']
export const facilityOptions = ['고객센터', '장애인 화장실', '물품 보관함', '자전거보관실', '수유실', 'ATM기', '스마트도서관', '꽃자판기', '보조배터리 대여소', '휴대폰충전설비', '전동휠체어 충전설비', '편의점', '승차권자동발매기', '반대편 횡단 가능']
export async function fetchAdminStations(params = {}) {
  const response = await api.line.stationsAdmin(params)
  adminStations.value = responseItems(response).map(stationFromApi)
  return { ...response, items: adminStations.value }
}
export async function fetchAdminStation(id) { return stationFromApi(await api.line.stationAdmin(id)) }
export async function saveStation(values, id) {
  if (!Number.isInteger(Number(values.stationOrder))) throw new Error('역 등록 API의 필수 stationOrder가 기존 입력 화면에 없어 저장 요청을 구성할 수 없습니다.')
  const body = { stationName: values.name, staffName: values.staff, phone: values.phone, address: values.address, usageInfo: values.usage.join(', '), active: values.active, nextInspectionDueDate: values.nextInspectionDueDate || null, stationOrder: Number(values.stationOrder), facilityStatus: facilityStatusToApi[values.facilityStatus] || values.facilityStatus, lastInspectionDate: values.lastInspectionDate || null, inspectionDate: values.inspectionDate || null }
  const response = id ? await api.line.updateStation(id, body) : await api.line.createStation(body)
  return response?.stationId ?? id
}
export async function deleteStation(id) { await api.line.setStationActive(id, false); adminStations.value = adminStations.value.map(row => row.id === String(id) ? { ...row, active: false } : row); return true }
