import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'
import { facilityStatusFromApi, facilityStatusToApi, formatDate, stationFromApi } from '../../../services/apiMappers'

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
  const hasOrder = values.stationOrder !== null && values.stationOrder !== '' && Number.isInteger(Number(values.stationOrder))
  const body = { stationNo: values.number, stationName: values.name, nameEn: values.english || null, staffName: values.staff, phone: values.phone, address: values.address, usageInfo: values.usage.join(', '), facilities: values.facilities.join(', '), transferLine: values.transferLine || null, transferInfo: values.transferText || null, active: values.active, registeredAt: values.registered ? values.registered.replaceAll('.', '-') : null, nextInspectionDueDate: values.nextInspectionDueDate || null, stationOrder: hasOrder ? Number(values.stationOrder) : 0, latitude: Number(values.latitude) || 0, longitude: Number(values.longitude) || 0, facilityStatus: facilityStatusToApi[values.facilityStatus] || values.facilityStatus, lastInspectionDate: values.lastInspectionDate || null, inspectionDate: values.inspectionDate || null }
  const response = id ? await api.line.updateStation(id, body) : await api.line.createStation(body)
  if (id) {
    adminStations.value = adminStations.value.map(station => station.id === String(id) ? {
      ...station,
      name: body.stationName,
      staff: body.staffName,
      status: facilityStatusFromApi[body.facilityStatus] || body.facilityStatus,
      active: body.active,
      inspection: body.inspectionDate || body.lastInspectionDate || '',
      updated: formatDate(response?.updatedAt || new Date().toISOString()),
    } : station)
  }
  return response?.stationId ?? id
}
export async function deleteStation(id) { await api.line.setStationActive(id, false); adminStations.value = adminStations.value.map(row => row.id === String(id) ? { ...row, active: false } : row); return true }
