import { reactive } from 'vue'
const titles = ['7호선 교통서비스 이용 안내', '7호선 운행 재개 안내', '시설 점검 일정 안내', '운행 지연 안내', '인천 패스 안내', '보관함 있나요??', '막차 시간 언제인가요?', '에어컨 좀 틀어주세요 ㅠㅠ', '직원이 불친절해요', '직원이 불친절해요']
export const resources = reactive(Array.from({ length: 100 }, (_, index) => ({
  id: 100 - index,
  title: titles[index % titles.length],
  author: ['관리자', '관리자', '관리자', '이무개', '관리자', '홍길동', '김아무', '이무개', '이무개', '이무개'][index % 10],
  views: 110 - index,
  date: ['2026.10.01 19:36', '2026.10.01 16:22', '2026.10.01 13:45', '2026.10.01 11:06', '2026.10.01 08:26', '2026.10.01 05:18', '2026.09.30 22:25', '2026.09.30 16:45', '2026.09.30 15:53', '2026.09.30 15:53'][index % 10],
  content: index % 10 === 0 ? '7호선 교통서비스 이용과 관련된 안내 자료입니다.\n운행 시간 및 서비스 이용 방법을 확인하시기 바랍니다.\n\n안전하고 편리한 대중교통 이용을 위해 항상 노력하겠습니다.' : `${titles[index % titles.length]}에 대한 안내 자료입니다.\n\n화면 확인을 위한 더미 자료이며 실제 운행 정보가 아닙니다.`,
})))
export function searchResources(field, keyword) {
  const query = keyword.trim().toLowerCase()
  return resources.filter(item => (field === 'title' ? item.title : field === 'content' ? item.content : `${item.title} ${item.content}`).toLowerCase().includes(query))
}


let nextResourceId = 101
export function saveResource(data, id) {
  if (!data.title.trim() || !data.content.trim()) throw new Error('제목과 내용을 입력해 주세요.')
  if (data.title.length > 200 || data.content.length > 200) throw new Error('제목과 내용은 각각 200자 이내로 입력해 주세요.')
  if (data.attachment && data.attachment.size > 10 * 1024 * 1024) throw new Error('첨부파일은 10MB 이하로 선택해 주세요.')
  const existing = id ? resources.find(item => String(item.id) === String(id)) : null
  if (id && !existing) throw new Error('해당 자료를 찾을 수 없습니다.')
  if (existing) { Object.assign(existing, data, { title: data.title.trim(), content: data.content.trim() }); return existing.id }
  const resourceId = nextResourceId++
  const date = new Intl.DateTimeFormat('sv-SE', { timeZone: 'Asia/Seoul', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).format(new Date()).replaceAll('-', '.')
  resources.unshift({ ...data, id: resourceId, title: data.title.trim(), content: data.content.trim(), author: '관리자', views: 0, date })
  return resourceId
}
export function deleteResource(id) {
  const index = resources.findIndex(item => String(item.id) === String(id))
  if (index < 0) return false
  resources.splice(index, 1)
  return true
}
