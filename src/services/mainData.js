import { reactive } from 'vue'
export const notices = [
 { id: 100, title: '교통카드 이용 안내 변경사항', author: '관리자', views: 110, date: '2026.10.01 19:36' },
 { id: 99, title: '7호선 운행 재개 안내', author: '관리자', views: 109, date: '2026.10.01 16:22' },
 { id: 98, title: '시설 점검 일정 안내', author: '관리자', views: 108, date: '2026.10.01 13:45' },
 { id: 97, title: '운행 지연 안내', author: '관리자', views: 107, date: '2026.10.01 11:06' },
 { id: 96, title: '인천 패스 안내', author: '관리자', views: 106, date: '2026.10.01 08:26' },
]
export const questions = reactive([
 { id: 95, title: '보관함 있나요??', author: '홍길동', views: 105, date: '2026.10.01 05:18' },
 { id: 94, title: '막차 시간 언제인가요?', author: '김아무', views: 104, date: '2026.09.30 22:25' },
 { id: 93, title: '에어컨 좀 틀어주세요 ㅠㅠ', author: '이무개', views: 103, date: '2026.09.30 16:45' },
])
export const stations = ['석남', '산곡', '부평구청', '굴포천', '삼산체육관', '상동', '부천시청', '신중동', '춘의', '부천종합운동장', '까치울']

export function deleteQuestion(id) {
  const index = questions.findIndex(question => String(question.id) === String(id))
  if (index < 0) return false
  questions.splice(index, 1)
  return true
}
