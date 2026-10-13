const noticeStorageKey = 'adminNoticeList'

const initialNotices = [
  { id: 100, title: '교통카드 이용 안내 변경사항', writer: '관리자', views: 110, date: '2026.10.01 19:36', content: '교통카드 이용 안내 변경사항을 알려드립니다.', category: '일반' },
  { id: 99, title: '7호선 운행 재개 안내', writer: '관리자', views: 109, date: '2026.10.01 16:22', content: '7호선 운행 재개 일정을 안내합니다.', category: '운행' },
  { id: 98, title: '시설 점검 일정 안내', writer: '관리자', views: 108, date: '2026.10.01 13:45', content: '시설 점검 일정을 안내합니다.', category: '시설' },
  { id: 97, title: '운행 지연 안내', writer: '이무개', views: 107, date: '2026.10.01 11:06', content: '열차 운행 지연 정보를 안내합니다.', category: '운행' },
  { id: 96, title: '인천 패스 안내', writer: '관리자', views: 106, date: '2026.10.01 08:26', content: '인천 패스 이용 방법을 안내합니다.', category: '일반' },
  { id: 95, title: '보관함 있나요??', writer: '홍길동', views: 105, date: '2026.10.01 05:18', content: '역사 내 물품 보관함을 문의합니다.', category: '일반' },
  { id: 94, title: '막차 시간 언제인가요?', writer: '김아무', views: 104, date: '2026.09.30 22:25', content: '역별 막차 시간을 문의합니다.', category: '운행' },
  { id: 93, title: '에어컨 좀 틀어주세요 ㅠㅠ', writer: '이무개', views: 103, date: '2026.09.30 16:45', content: '객실 냉방을 요청합니다.', category: '일반' },
  { id: 92, title: '직원이 불친절해요', writer: '이무개', views: 102, date: '2026.09.30 15:53', content: '고객 응대 관련 의견입니다.', category: '일반' },
  { id: 91, title: '직원이 불친절해요', writer: '이무개', views: 101, date: '2026.09.30 15:53', content: '고객 응대 관련 의견입니다.', category: '일반' },
]

export function getNotices() {
  try {
    const savedNotices = localStorage.getItem(noticeStorageKey)
    return savedNotices ? JSON.parse(savedNotices) : initialNotices.map((notice) => ({ ...notice }))
  } catch {
    return initialNotices.map((notice) => ({ ...notice }))
  }
}

export function saveNotices(notices) {
  localStorage.setItem(noticeStorageKey, JSON.stringify(notices))
}

export function getNoticeById(id) {
  return getNotices().find((notice) => String(notice.id) === String(id))
}
