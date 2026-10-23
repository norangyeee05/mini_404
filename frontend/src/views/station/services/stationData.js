import { ref } from 'vue'
import { api, responseItems } from '../../../services/backendApi'

export const stationDetails = ref([])

const stationUsageInfo = {
  '761': ['상대식 승강장', '화장실 개찰구 밖, 개찰구 안', '반대편 횡단 가능'],
  '760': ['상대식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '759': ['상대식 승강장', '화장실 개찰구 밖, 개찰구 안', '반대편 횡단 가능'],
  '758': ['상대식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '757': ['상대식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '756': ['상대식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '755': ['섬식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '754': ['섬식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '753': ['섬식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
  '752': ['섬식 승강장', '화장실 개찰구 밖, 개찰구 안', '반대편 횡단 가능'],
  '751': ['섬식 승강장', '화장실 개찰구 밖', '반대편 횡단 가능'],
}

const stationDetailOverrides = {
  '761': { address: '인천광역시 서구 길주로 120', phone: '032-451-1402' },
  '760': { address: '인천광역시 부평구 길주로 379', phone: '032-451-1392' },
  '759': { address: '인천광역시 부평구 길주로 527', phone: '032-451-1383' },
  '758': { address: '인천광역시 부평구 길주로 623', phone: '032-451-1373' },
  '757': { address: '인천광역시 부평구 길주로 713', phone: '032-451-1363' },
  '756': { address: '경기도 부천시 길주로 104', phone: '032-451-1353' },
  '755': { address: '인천광역시 부평구 길주로 202', phone: '032-451-1343' },
  '754': { address: '경기도 부천시 길주로 314', phone: '032-451-1333' },
  '753': { address: '경기도 부천시 길주로 406', phone: '032-451-1323' },
  '752': { address: '경기도 부천시 길주로 502', phone: '032-451-1313' },
  '751': { address: '경기도 부천시 길주로 626', phone: '032-451-1303' },
}

const stationFacilityOverrides = {
  '761': ['고객센터', '장애인 화장실', '물품 보관함', '자전거보관실', '스마트도서관', '꽃자판기'],
}

export async function fetchStations() {
  const response = await api.line.stations()
  stationDetails.value = responseItems(response).map(item => ({ id: String(item.stationId), name: item.stationName, stationNo: item.stationNo, stationOrder: item.stationOrder }))
  return stationDetails.value
}
export async function fetchStation(id) {
  const item = await api.line.station(id)
  const stationNo = String(item.stationNo || '')
  const usage = stationUsageInfo[stationNo] || String(item.usageInfo || '').split(/[|,]/).map(value => value.trim()).filter(Boolean)
  const details = stationDetailOverrides[stationNo] || {}
  return { ...item, ...details, id: String(id), name: item.stationName || item.name || '', english: item.nameEn || '', phone: details.phone || item.phone || '', usage, facilities: stationFacilityOverrides[stationNo] || item.facilities || [], date: item.updatedAt || '' }
}
