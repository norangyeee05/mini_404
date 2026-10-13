const storageKey = 'adminQnaList'

const initialQnas = [
  { id: 100, title: '엘리베이터 고장', visibility: '전체', category: '시설', writer: '관리자', status: '답변완료', date: '2026.10.01 19:36', views: 42, content: '역사 내 엘리베이터 고장 관련 문의입니다.', answer: '불편을 드려 죄송합니다. 담당 부서에 전달하여 점검하겠습니다.' },
  { id: 99, title: '분실물 관련해서 문의 드립니다', visibility: '전체', category: '기타', writer: '관리자', status: '답변대기', date: '2026.10.01 16:22', views: 18, content: '분실물 접수 방법을 문의합니다.', answer: '' },
  { id: 98, title: '교통서비스 이용 문의', visibility: '전체', category: '운행', writer: '관리자', status: '답변대기', date: '2026.10.01 13:45', views: 25, content: '교통서비스 이용 방법을 문의합니다.', answer: '' },
  { id: 97, title: '점검시간에 대한 문의', visibility: '전체', category: '시설', writer: '이무개', status: '답변완료', date: '2026.10.01 11:06', views: 31, content: '시설 점검 시간을 문의합니다.', answer: '점검 일정은 공지사항에서 확인하실 수 있습니다.' },
  { id: 96, title: '교통카드 충전 방법 문의', visibility: '전체', category: '기타', writer: '관리자', status: '답변완료', date: '2026.10.01 08:26', views: 28, content: '교통카드 충전 방법을 문의합니다.', answer: '역 내 교통카드 충전기 또는 편의점에서 충전하실 수 있습니다.' },
  { id: 95, title: '7호선 배차 간격 문의', visibility: '전체', category: '운행', writer: '홍길동', status: '답변대기', date: '2026.10.01 05:18', views: 31, content: '7호선 배차 간격을 문의합니다.', answer: '' },
  { id: 94, title: '막차 시간 언제인가요?', visibility: '비공개', category: '시설', writer: '김아무', status: '답변대기', date: '2026.09.30 22:25', views: 12, content: '역별 막차 시간을 문의합니다.', answer: '' },
  { id: 93, title: '에어컨 좀 틀어주세요 ㅠㅠ', visibility: '비공개', category: '기타', writer: '이무개', status: '답변완료', date: '2026.09.30 16:45', views: 37, content: '객실 냉방을 요청합니다.', answer: '열차 내 냉방 상태를 확인하고 개선하겠습니다.' },
  { id: 92, title: '자료실 추가 요청', visibility: '전체', category: '자료실', writer: '이무개', status: '답변대기', date: '2026.09.30 15:53', views: 9, content: '자료실에 자료 추가를 요청합니다.', answer: '' },
  { id: 91, title: '석남역 막차 시간 문의', visibility: '전체', category: '운행', writer: '이무개', status: '답변완료', date: '2026.09.30 15:53', views: 21, content: '석남역 막차 시간을 문의합니다.', answer: '석남역 막차 시간은 교통서비스 메뉴에서 확인하실 수 있습니다.' },
  { id: 90, title: '환승 통로 이용 문의', visibility: '전체', category: '시설', writer: '김민지', status: '답변대기', date: '2026.09.30 14:20', views: 8, content: '역 안에서 환승 통로를 이용하는 방법을 문의합니다.', answer: '' },
  { id: 89, title: '교통카드 환불 문의', visibility: '전체', category: '기타', writer: '박서준', status: '답변대기', date: '2026.09.30 13:05', views: 15, content: '교통카드 잔액 환불 방법을 문의합니다.', answer: '' },
  { id: 88, title: '역 내 편의시설 문의', visibility: '전체', category: '시설', writer: '최은지', status: '답변대기', date: '2026.09.30 11:42', views: 6, content: '역 내 편의시설 위치를 문의합니다.', answer: '' },
  { id: 87, title: '유실물 확인 요청', visibility: '비공개', category: '기타', writer: '정현우', status: '답변대기', date: '2026.09.30 10:16', views: 4, content: '열차에 두고 내린 물건을 확인하고 싶습니다.', answer: '' },
  { id: 86, title: '환승 시간 확인 부탁드립니다', visibility: '전체', category: '운행', writer: '윤아름', status: '답변대기', date: '2026.09.30 09:03', views: 11, content: '노선 간 환승 가능 시간을 문의합니다.', answer: '' },
]

const memberInquirySamples = [
  { id: 85, title: '석남역 출구 엘리베이터 이용 문의', visibility: '전체', category: '시설', writer: '홍길동', status: '답변완료', date: '2026.09.29 18:42', views: 12, content: '석남역 2번 출구 엘리베이터 운영 시간을 알고 싶습니다.', answer: '엘리베이터는 역사 운영 시간 동안 이용하실 수 있습니다.' },
  { id: 84, title: '평일 아침 열차 혼잡도 문의', visibility: '전체', category: '운행', writer: '홍길동', status: '답변대기', date: '2026.09.29 16:18', views: 4, content: '평일 오전 8시경 7호선 혼잡도가 어느 정도인지 궁금합니다.', answer: '' },
  { id: 83, title: '역사 내 자전거 보관 장소가 있나요?', visibility: '전체', category: '기타', writer: '홍길동', status: '답변완료', date: '2026.09.29 13:05', views: 9, content: '역사 안이나 출입구 근처에 자전거를 보관할 수 있는 곳이 있나요?', answer: '역별 자전거 보관 시설 위치는 역사 안내도에서 확인하실 수 있습니다.' },
  { id: 82, title: '운행 지연 안내를 받을 수 있나요?', visibility: '전체', category: '운행', writer: '홍길동', status: '답변대기', date: '2026.09.28 20:31', views: 2, content: '열차 운행이 지연될 때 문자나 앱으로 안내를 받을 수 있는지 문의드립니다.', answer: '' },
  { id: 81, title: '역무실 분실물 접수 방법', visibility: '전체', category: '기타', writer: '홍길동', status: '답변완료', date: '2026.09.28 15:24', views: 17, content: '열차에서 물건을 잃어버렸습니다. 분실물 접수 방법을 알려주세요.', answer: '분실 장소와 물품 정보를 알려주시면 유실물 센터를 통해 확인해 드립니다.' },
  { id: 80, title: '유모차로 환승할 때 이용할 수 있는 시설', visibility: '전체', category: '시설', writer: '홍길동', status: '답변대기', date: '2026.09.27 11:52', views: 6, content: '유모차를 가지고 환승할 때 엘리베이터 위치를 확인할 방법이 있을까요?', answer: '' },
  { id: 79, title: '정기권 이용 가능 구간 문의', visibility: '전체', category: '운행', writer: '홍길동', status: '답변완료', date: '2026.09.26 17:40', views: 21, content: '정기권으로 이용할 수 있는 구간과 이용 횟수 기준을 알고 싶습니다.', answer: '정기권 종류별 이용 가능 구간과 조건은 교통카드 안내에서 확인하실 수 있습니다.' },
  { id: 78, title: '역사 화장실 위치 안내 요청', visibility: '전체', category: '시설', writer: '홍길동', status: '답변대기', date: '2026.09.26 09:16', views: 3, content: '환승역에서 화장실 위치를 쉽게 확인할 수 있는 안내 표시가 있나요?', answer: '' },
  { id: 77, title: '막차 환승 가능 여부 문의', visibility: '전체', category: '운행', writer: '홍길동', status: '답변완료', date: '2026.09.25 19:03', views: 14, content: '막차 시간대에 노선 간 환승이 가능한지 확인하고 싶습니다.', answer: '역과 노선에 따라 막차 시각이 다를 수 있어 이용 전 운행정보를 확인해 주세요.' },
  { id: 76, title: '교통약자 이동 지원 서비스 문의', visibility: '전체', category: '기타', writer: '홍길동', status: '답변대기', date: '2026.09.24 14:27', views: 5, content: '역에서 교통약자 이동을 지원하는 서비스를 신청하려면 어떻게 해야 하나요?', answer: '' },
]

const allInitialQnas = [...initialQnas, ...memberInquirySamples]
const memberSamplesMigrationKey = 'memberQnaSamplesAdded'

export function getQnas() {
  try {
    const savedQnas = localStorage.getItem(storageKey)
    if (!savedQnas) {
      localStorage.setItem(memberSamplesMigrationKey, 'true')
      return allInitialQnas.map((qna) => ({ ...qna }))
    }

    const parsedQnas = JSON.parse(savedQnas)
    if (!Array.isArray(parsedQnas)) {
      localStorage.setItem(memberSamplesMigrationKey, 'true')
      return allInitialQnas.map((qna) => ({ ...qna }))
    }

    if (localStorage.getItem(memberSamplesMigrationKey) !== 'true') {
      const savedIds = new Set(parsedQnas.map((qna) => String(qna.id)))
      const missingSamples = memberInquirySamples.filter((qna) => !savedIds.has(String(qna.id)))
      parsedQnas.push(...missingSamples)
      localStorage.setItem(storageKey, JSON.stringify(parsedQnas))
      localStorage.setItem(memberSamplesMigrationKey, 'true')
    }

    return parsedQnas.map((qna) => ({ views: 0, answer: '', ...qna }))
  } catch {
    return allInitialQnas.map((qna) => ({ ...qna }))
  }
}

export function saveQnas(qnas) {
  localStorage.setItem(storageKey, JSON.stringify(qnas))
}
