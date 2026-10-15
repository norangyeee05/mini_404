export const inquiryToApi = { '시설': 'FACILITY', '운행': 'OPERATION', '기타': 'ETC', '자료실': 'ETC' }
export const inquiryFromApi = { FACILITY: '시설', OPERATION: '운행', ETC: '기타' }
export const qnaStatusFromApi = { WAITING: '답변대기', ANSWERED: '답변완료' }
export const memberStatusToApi = { '활성': 'ACTIVE', '임시차단': 'BLOCKED', '비활성': 'BANNED', '강퇴': 'BANNED' }
export const memberStatusFromApi = { ACTIVE: '활성', BLOCKED: '임시차단', BANNED: '강퇴' }
export const trainStatusToApi = { '운행': 'RUNNING', '점검': 'MAINTENANCE', '고장': 'BROKEN' }
export const trainStatusFromApi = { RUNNING: '운행', MAINTENANCE: '점검', BROKEN: '고장' }
export const directionToApi = { '상행': 'UP', '하행': 'DOWN' }
export const directionFromApi = { UP: '상행', DOWN: '하행' }
export const facilityStatusToApi = { '양호': 'INSPECTED', '점검 필요': 'NEEDS_INSPECTION', '필요': 'NEEDS_INSPECTION', '점검 중': 'IN_INSPECTION' }
export const facilityStatusFromApi = { INSPECTED: '양호', NEEDS_INSPECTION: '필요', IN_INSPECTION: '점검 중' }

export function formatDate(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return new Intl.DateTimeFormat('sv-SE', { timeZone: 'Asia/Seoul', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).format(date).replaceAll('-', '.')
}

export function noticeFromApi(item) {
  return { ...item, id: item.noticeId, views: item.viewCount ?? 0, date: formatDate(item.createdAt), content: item.content || '' }
}
export function resourceFromApi(item) {
  const files = item.files || []
  const firstFile = files[0]
  return { ...item, id: item.resourceId, views: item.viewCount ?? 0, author: item.writer || '', date: formatDate(item.createdAt), files, attachment: firstFile ? { ...firstFile, name: firstFile.fileName, size: firstFile.size || 0 } : null }
}
export function qnaFromApi(item) {
  return { ...item, id: item.qnaId, views: item.viewCount ?? 0, writer: item.writer || '', date: formatDate(item.createdAt), category: inquiryFromApi[item.inquiryType] || item.inquiryType || '', status: qnaStatusFromApi[item.status] || item.status, files: item.files || [] }
}
export function memberFromApi(item) {
  return { ...item, name: item.name || '', loginId: item.loginId || '', email: item.email || '', status: memberStatusFromApi[item.status] || item.status, updatedAt: formatDate(item.updatedAt) }
}
export function trainFromApi(item) {
  return { ...item, id: String(item.trainId), number: String(item.trainNo || ''), operator: item.driver || '', status: trainStatusFromApi[item.status] || item.status, direction: directionFromApi[item.direction] || item.direction, updated: formatDate(item.updatedAt), active: item.active !== false }
}
export function stationFromApi(item) {
  return { ...item, id: String(item.stationId), number: String(item.stationId), name: item.stationName || '', staff: item.staffName || '', status: facilityStatusFromApi[item.facilityStatus] || item.facilityStatus, active: item.active !== false, updated: formatDate(item.updatedAt), inspection: item.inspectionDate || item.lastInspectionDate || '' }
}
