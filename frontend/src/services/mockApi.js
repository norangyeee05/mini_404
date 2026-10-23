import axios from 'axios'

const SESSION_KEY = 'frontend-mock-session'
const stamp = '2026-10-15T09:00:00+09:00'
let id = { member: 104, notice: 6, resource: 4, qna: 6, train: 4, station: 8, request: 3 }
const members = [
  { memberId: 1, loginId: 'admin', password: 'admin1234', name: '관리자', email: 'admin@example.test', role: 'ADMIN', status: 'ACTIVE', updatedAt: stamp },
  { memberId: 2, loginId: 'member', password: 'member1234', name: '테스트회원', email: 'member@example.test', role: 'USER', status: 'ACTIVE', updatedAt: stamp },
  { memberId: 3, loginId: 'member2', password: 'member1234', name: '홍길동', email: 'member2@example.test', role: 'USER', status: 'BLOCKED', updatedAt: stamp },
  { memberId: 4, loginId: 'webadmin', password: 'webadmin1234', name: '웹관리자', email: 'webadmin@example.test', role: 'WEB_ADMIN', status: 'ACTIVE', updatedAt: stamp },
]
const notices = [
  { noticeId: 5, title: '7호선 역 시설 점검 안내', writer: '관리자', content: '안전한 이용을 위해 역 시설을 점검합니다.', viewCount: 21, createdAt: stamp },
  { noticeId: 4, title: '열차 운행 안내', writer: '관리자', content: '열차 운행 정보를 확인해 주세요.', viewCount: 18, createdAt: '2026-10-12T09:00:00+09:00' },
  { noticeId: 3, title: '시민 참여 안내', writer: '홍보팀', content: '서비스 개선을 위한 시민 의견을 기다립니다.', viewCount: 12, createdAt: '2026-10-10T09:00:00+09:00' },
  { noticeId: 2, title: '자료실 이용 안내', writer: '관리자', content: '자료실에서 관련 자료를 확인하실 수 있습니다.', viewCount: 9, createdAt: '2026-10-08T09:00:00+09:00' },
  { noticeId: 1, title: '인천교통공사 공지사항', writer: '관리자', content: '공지사항 상세 화면 확인용 샘플입니다.', viewCount: 7, createdAt: '2026-10-06T09:00:00+09:00' },
]
const resources = [
  { resourceId: 3, title: '7호선 이용 안내', writer: '관리자', content: '7호선 이용에 필요한 안내 자료입니다.', viewCount: 32, createdAt: stamp, files: [{ fileId: 31, fileName: '7호선_이용안내.pdf', size: 128000 }] },
  { resourceId: 2, title: '역 시설 안내 자료', writer: '관리자', content: '역별 시설 안내 자료입니다.', viewCount: 18, createdAt: '2026-10-11T09:00:00+09:00', files: [] },
  { resourceId: 1, title: '교통 서비스 안내', writer: '관리자', content: '교통 서비스 안내 자료입니다.', viewCount: 11, createdAt: '2026-10-09T09:00:00+09:00', files: [] },
]
const qnas = [
  { qnaId: 5, memberId: 2, title: '부평구청역 환승 문의', content: '환승 통로 이용 방법이 궁금합니다.', inquiryType: 'FACILITY', status: 'WAITING', answer: '', writer: '테스트회원', viewCount: 4, createdAt: stamp, files: [] },
  { qnaId: 4, memberId: 2, title: '열차 운행 시간 문의', content: '주말 운행 시간은 어떻게 되나요?', inquiryType: 'OPERATION', status: 'ANSWERED', answer: '운행 시간표에서 확인하실 수 있습니다.', writer: '테스트회원', viewCount: 8, createdAt: '2026-10-12T09:00:00+09:00', files: [] },
  { qnaId: 3, memberId: 3, title: '역 편의시설 문의', content: '역 내 편의시설 위치를 알고 싶습니다.', inquiryType: 'FACILITY', status: 'WAITING', answer: '', writer: '홍길동', viewCount: 2, createdAt: '2026-10-10T09:00:00+09:00', files: [] },
  { qnaId: 2, memberId: 2, title: '분실물 접수 방법', content: '분실물은 어디로 문의하면 되나요?', inquiryType: 'ETC', status: 'ANSWERED', answer: '고객센터로 문의해 주세요.', writer: '테스트회원', viewCount: 6, createdAt: '2026-10-08T09:00:00+09:00', files: [] },
  { qnaId: 1, memberId: 3, title: '역 이용 관련 문의', content: '역 이용 안내를 확인하고 싶습니다.', inquiryType: 'OPERATION', status: 'WAITING', answer: '', writer: '홍길동', viewCount: 1, createdAt: '2026-10-06T09:00:00+09:00', files: [] },
]
const stations = [
  ['석남', '김민수'], ['산곡', '이수진'], ['부평구청', '박지훈'], ['굴포천', '최유진'], ['삼산체육관', '정하늘'], ['상동', '윤서연'], ['부천종합운동장', '한도윤'],
].map(([stationName, staffName], i) => ({ stationId: i + 1, stationName, stationNo: String(761 - i), stationOrder: i + 1, staffName, phone: `032-000-000${i + 1}`, address: `인천 7호선 ${stationName}역`, usageInfo: i % 2 ? '상대식 승강장' : '섬식 승강장', active: true, facilityStatus: i === 4 ? 'IN_INSPECTION' : 'INSPECTED', lastInspectionDate: '2026-09-10', nextInspectionDueDate: '2027-09-10', updatedAt: stamp }))
const trains = [
  { trainId: 1, lineId: 7, trainNo: '701', section: '석남 - 부천종합운동장', driver: '김기관', direction: 'UP', status: 'RUNNING', active: true, updatedAt: stamp },
  { trainId: 2, lineId: 7, trainNo: '702', section: '산곡 - 상동', driver: '이기관', direction: 'DOWN', status: 'MAINTENANCE', active: true, updatedAt: stamp },
  { trainId: 3, lineId: 7, trainNo: '703', section: '부평구청 - 굴포천', driver: '박기관', direction: 'UP', status: 'BROKEN', active: false, updatedAt: stamp },
]
const requests = [{ requestId: 2, title: '역별 시설 안내 요청', content: '역별 엘리베이터 위치 자료를 요청합니다.', status: 'RECEIVED', createdAt: stamp, updatedAt: stamp, writer: '테스트회원' }, { requestId: 1, title: '노선도 자료 요청', content: '고해상도 노선도를 요청합니다.', status: 'IN_PROGRESS', createdAt: stamp, updatedAt: stamp, writer: '홍길동' }]

const session = () => {
  try {
    const mockSession = JSON.parse(localStorage.getItem(SESSION_KEY) || 'null')
    if (mockSession) return mockSession
    const cachedSession = JSON.parse(localStorage.getItem('frontend-auth-session') || 'null')
    return cachedSession ? { memberId: cachedSession.memberId } : null
  } catch { return null }
}
const setSession = member => member ? localStorage.setItem(SESSION_KEY, JSON.stringify({ memberId: member.memberId })) : localStorage.removeItem(SESSION_KEY)
const err = (status, message) => { const e = new Error(message); e.status = status; throw e }
const failIf = (condition, status, message) => { if (condition) err(status, message) }
const current = () => { const s = session(); const m = members.find(x => String(x.memberId) === String(s?.memberId)); failIf(!m, 401, '로그인이 필요합니다.'); return m }
const admin = () => failIf(!['ADMIN', 'WEB_ADMIN'].includes(current().role), 403, '관리자 권한이 필요합니다.')
const find = (list, key, value) => { const item = list.find(x => String(x[key]) === String(value)); failIf(!item, 404, '요청한 정보를 찾을 수 없습니다.'); return item }
const bodyOf = c => {
  if (typeof c.data === 'string') { try { return JSON.parse(c.data) } catch { return {} } }
  if (c.data instanceof FormData) return Object.fromEntries(c.data.entries())
  return c.data || {}
}
const pathOf = c => `${String(c.baseURL || '').replace(/\/$/, '')}${new URL(c.url || '/', 'http://mock.local').pathname}`.replace(/\/+/g, '/').replace(/^\/api(?=\/|$)/, '') || '/'
const page = (rows, p = {}) => { const number = Math.max(1, Number(p.page) || 1), size = Math.max(1, Number(p.size) || 10); return { totalCount: rows.length, page: number, size, items: rows.slice((number - 1) * size, number * size) } }
const filter = (rows, p = {}, fields = ['title', 'content', 'writer']) => { const q = String(p.keyword || '').trim().toLowerCase(); let found = q ? rows.filter(x => (fields.includes(p.searchType) ? [p.searchType] : fields).some(k => String(x[k] ?? '').toLowerCase().includes(q))) : [...rows]; if (p.status) found = found.filter(x => x.status === p.status); if (p.inquiryType) found = found.filter(x => x.inquiryType === p.inquiryType); return found.sort((a, b) => Date.parse(b.updatedAt || b.createdAt || 0) - Date.parse(a.updatedAt || a.createdAt || 0) || (b.id ?? b.noticeId ?? b.resourceId ?? b.qnaId ?? b.memberId ?? b.trainId ?? b.stationId ?? b.requestId ?? 0) - (a.id ?? a.noticeId ?? a.resourceId ?? a.qnaId ?? a.memberId ?? a.trainId ?? a.stationId ?? a.requestId ?? 0)) }
const fileList = body => body.files ? [{ fileId: Date.now(), fileName: body.files.name || 'sample-file.txt', size: body.files.size || 0 }] : []

function handle(c) {
  const path = pathOf(c), method = (c.method || 'get').toLowerCase(), p = c.params || {}, b = bodyOf(c); let m
  if (path.startsWith('/admin/')) {
    admin()
    if (current().role === 'WEB_ADMIN' && path === '/admin/dashboard') err(403, '접근 권한이 없습니다.')
    if (current().role === 'WEB_ADMIN' && path.startsWith('/admin/line7/') && method !== 'get') err(403, '접근 권한이 없습니다.')
    if (current().role === 'WEB_ADMIN' && /^\/admin\/members\/\d+\/role$/.test(path)) err(403, '접근 권한이 없습니다.')
  }
  if (method === 'post' && path === '/auth/login') { const user = members.find(x => x.loginId === b.loginId && x.password === b.password); failIf(!user, 401, '아이디 또는 비밀번호가 올바르지 않습니다.'); setSession(user); return { memberId: user.memberId, name: user.name, role: user.role } }
  if (method === 'get' && path === '/auth/me') { const active = members.find(x => String(x.memberId) === String(session()?.memberId)); return active ? { authenticated: true, role: active.role, memberId: active.memberId, name: active.name, adminAccount: false } : { authenticated: false, role: 'GUEST', memberId: null, name: null, adminAccount: false } }
  if (method === 'post' && path === '/auth/signup') { failIf(members.some(x => x.loginId === b.loginId), 409, '이미 사용 중인 아이디입니다.'); const user = { memberId: id.member++, ...b, role: 'USER', status: 'ACTIVE', updatedAt: stamp }; members.push(user); return { memberId: user.memberId } }
  if (method === 'get' && path === '/auth/check-id') return { available: !members.some(x => x.loginId === p.loginId) }
  if (method === 'post' && path === '/auth/logout') { setSession(null); return { message: '로그아웃되었습니다.' } }
  if (method === 'get' && path === '/members/me') { const u = current(); return { loginId: u.loginId, name: u.name, email: u.email, phone: u.phone || '' } }
  if (method === 'put' && path === '/members/me') { const u = current(); Object.assign(u, b, { updatedAt: stamp }); return { memberId: u.memberId, name: u.name, email: u.email } }
  if (method === 'delete' && path === '/members/me') { const u = current(); failIf(u.loginId !== b.loginId || u.password !== b.password || b.password !== b.passwordConfirm, 400, '입력한 회원 정보가 일치하지 않습니다.'); members.splice(members.indexOf(u), 1); setSession(null); return { message: '탈퇴되었습니다.' } }
  if (method === 'get' && path === '/notices') return page(filter(notices, p, ['title', 'writer']), p)
  if (method === 'get' && (m = path.match(/^\/notices\/([^/]+)$/))) return find(notices, 'noticeId', m[1])
  if (path.startsWith('/admin/')) admin()
  if (method === 'post' && path === '/admin/notices') { const x = { noticeId: id.notice++, ...b, writer: '관리자', viewCount: 0, createdAt: stamp }; notices.unshift(x); return { noticeId: x.noticeId } }
  if (method === 'put' && (m = path.match(/^\/admin\/notices\/([^/]+)$/))) { const x = find(notices, 'noticeId', m[1]); Object.assign(x, b); return x }
  if (method === 'delete' && (m = path.match(/^\/admin\/notices\/([^/]+)$/))) { const x = find(notices, 'noticeId', m[1]); notices.splice(notices.indexOf(x), 1); return {} }
  if (method === 'get' && path === '/resources') return page(filter(resources, p), p)
  if (method === 'get' && (m = path.match(/^\/resources\/([^/]+)$/))) return find(resources, 'resourceId', m[1])
  if (method === 'get' && path.match(/^\/resources\/[^/]+\/files\/[^/]+$/)) return new Blob(['개발용 샘플 첨부파일입니다.'], { type: 'text/plain' })
  if (method === 'post' && path === '/resources/requests') { const x = { requestId: id.request++, ...b, files: fileList(b), status: 'RECEIVED', writer: current().name, createdAt: stamp }; requests.unshift(x); return { requestId: x.requestId } }
  if (method === 'post' && path === '/admin/resources') { const x = { resourceId: id.resource++, ...b, files: fileList(b), writer: '관리자', viewCount: 0, createdAt: stamp }; resources.unshift(x); return { resourceId: x.resourceId } }
  if (method === 'put' && (m = path.match(/^\/admin\/resources\/([^/]+)$/))) { const x = find(resources, 'resourceId', m[1]); Object.assign(x, b, { updatedAt: stamp }); return x }
  if (method === 'delete' && (m = path.match(/^\/admin\/resources\/([^/]+)$/))) { const x = find(resources, 'resourceId', m[1]); resources.splice(resources.indexOf(x), 1); return {} }
  if (method === 'get' && path === '/qna') return page(filter(qnas, p), p)
  if (method === 'get' && path === '/qna/my') return page(filter(qnas.filter(x => String(x.memberId) === String(current().memberId)), p, ['title', 'content']), p)
  if (method === 'post' && path === '/qna') { const u = current(), x = { qnaId: id.qna++, memberId: u.memberId, ...b, files: fileList(b), status: 'WAITING', answer: '', writer: u.name, viewCount: 0, createdAt: stamp }; qnas.unshift(x); return { qnaId: x.qnaId } }
  if (method === 'get' && (m = path.match(/^\/qna\/([^/]+)$/))) return find(qnas, 'qnaId', m[1])
  if (method === 'put' && (m = path.match(/^\/qna\/([^/]+)$/))) { const u = current(), x = find(qnas, 'qnaId', m[1]); failIf(String(x.memberId) !== String(u.memberId), 403, '본인의 문의만 수정할 수 있습니다.'); Object.assign(x, b); return x }
  if (method === 'delete' && (m = path.match(/^\/qna\/([^/]+)$/))) { const u = current(), x = find(qnas, 'qnaId', m[1]); failIf(String(x.memberId) !== String(u.memberId), 403, '본인의 문의만 삭제할 수 있습니다.'); qnas.splice(qnas.indexOf(x), 1); return {} }
  if (method === 'get' && path === '/admin/qna') return page(filter(qnas, p), p)
  if (method === 'get' && (m = path.match(/^\/admin\/qna\/([^/]+)$/))) return find(qnas, 'qnaId', m[1])
  if (method === 'post' && (m = path.match(/^\/admin\/qna\/([^/]+)\/answer$/))) { const x = find(qnas, 'qnaId', m[1]); Object.assign(x, { answer: b.answer, status: 'ANSWERED', answeredAt: stamp }); return x }
  if (method === 'delete' && (m = path.match(/^\/admin\/qna\/([^/]+)$/))) { const x = find(qnas, 'qnaId', m[1]); qnas.splice(qnas.indexOf(x), 1); return {} }
  if (method === 'get' && path === '/admin/members') return page(filter(members, p, ['name', 'loginId', 'email']), p)
  if (method === 'get' && (m = path.match(/^\/admin\/members\/([^/]+)$/))) return find(members, 'memberId', m[1])
  if (method === 'patch' && (m = path.match(/^\/admin\/members\/([^/]+)\/status$/))) { const x = find(members, 'memberId', m[1]); Object.assign(x, { status: b.status, updatedAt: stamp }); return x }
  if (method === 'patch' && (m = path.match(/^\/admin\/members\/([^/]+)\/role$/))) { const x = find(members, 'memberId', m[1]); failIf(!['USER', 'WEB_ADMIN', 'ADMIN'].includes(b.role), 400, '지원하지 않는 회원 등급입니다.'); Object.assign(x, { role: b.role, updatedAt: stamp }); return x }
  if (method === 'post' && (m = path.match(/^\/admin\/members\/([^/]+)\/password-reset$/))) { const x = find(members, 'memberId', m[1]); Object.assign(x, { password: `Temp-${Math.random().toString(36).slice(2, 12)}!`, updatedAt: stamp }); return { memberId: x.memberId, temporaryPassword: x.password } }
  if (method === 'get' && path === '/admin/dashboard') return { memberCount: members.length, noticeCount: notices.length, waitingQnaCount: qnas.filter(x => x.status === 'WAITING').length, recentNotices: notices.slice(0, 5), recentQnas: qnas.slice(0, 5) }
  if (method === 'get' && path === '/line7/stations') return stations.filter(x => x.active).sort((a, b) => Number(b.stationNo) - Number(a.stationNo) || a.stationOrder - b.stationOrder)
  if (method === 'get' && (m = path.match(/^\/line7\/stations\/([^/]+)$/))) return find(stations, 'stationId', m[1])
  if (method === 'get' && path.match(/^\/line7\/stations\/[^/]+\/transfer$/)) return { transfers: [{ transferLineName: '인천 1호선', transferInfo: '환승 통로 안내' }] }
  if (method === 'get' && path === '/admin/line7/trains') return page(filter(trains, p, ['trainNo', 'driver', 'section', 'status', 'direction']), p)
  if (method === 'get' && (m = path.match(/^\/admin\/line7\/trains\/([^/]+)$/))) return find(trains, 'trainId', m[1])
  if (method === 'post' && path === '/admin/line7/trains') { const x = { trainId: id.train++, ...b, updatedAt: stamp }; trains.unshift(x); return { trainId: x.trainId } }
  if (method === 'put' && (m = path.match(/^\/admin\/line7\/trains\/([^/]+)$/))) { const x = find(trains, 'trainId', m[1]); Object.assign(x, b, { updatedAt: stamp }); return x }
  if (method === 'patch' && (m = path.match(/^\/admin\/line7\/trains\/([^/]+)\/active$/))) { const x = find(trains, 'trainId', m[1]); Object.assign(x, { active: b.active, updatedAt: stamp }); return x }
  if (method === 'get' && path === '/admin/line7/stations') return page(filter(stations, p, ['stationName', 'staffName']), p)
  if (method === 'get' && (m = path.match(/^\/admin\/line7\/stations\/([^/]+)$/))) return find(stations, 'stationId', m[1])
  if (method === 'post' && path === '/admin/line7/stations') { const x = { stationId: id.station++, ...b, updatedAt: stamp }; stations.push(x); return { stationId: x.stationId } }
  if (method === 'put' && (m = path.match(/^\/admin\/line7\/stations\/([^/]+)$/))) { const x = find(stations, 'stationId', m[1]); Object.assign(x, b, { updatedAt: stamp }); return x }
  if (method === 'patch' && (m = path.match(/^\/admin\/line7\/stations\/([^/]+)\/active$/))) { const x = find(stations, 'stationId', m[1]); Object.assign(x, { active: b.active, updatedAt: stamp }); return x }
  if (method === 'get' && path === '/admin/resource-requests') return page(filter(requests, p), p)
  if (method === 'get' && (m = path.match(/^\/admin\/resource-requests\/([^/]+)$/))) return find(requests, 'requestId', m[1])
  if (method === 'patch' && (m = path.match(/^\/admin\/resource-requests\/([^/]+)\/status$/))) { const x = find(requests, 'requestId', m[1]); Object.assign(x, { status: b.status, updatedAt: stamp }); return x }
  if (method === 'delete' && (m = path.match(/^\/admin\/resource-requests\/([^/]+)$/))) { const x = find(requests, 'requestId', m[1]); requests.splice(requests.indexOf(x), 1); return {} }
  err(404, `모의 API에 등록되지 않은 요청: ${method.toUpperCase()} ${path}`)
}

export function createMockAdapter() {
  return async config => {
    try { return { data: handle(config), status: 200, statusText: 'OK', headers: {}, config, request: null } }
    catch (error) {
      const status = error.status || 500
      const response = { data: { message: error.message }, status, statusText: error.message, headers: {}, config, request: null }
      throw new axios.AxiosError(error.message, status >= 500 ? 'ERR_BAD_RESPONSE' : 'ERR_BAD_REQUEST', config, null, response)
    }
  }
}
