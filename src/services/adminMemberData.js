import { reactive } from 'vue'
const names = ['이서연', '박민준', '정우성', '한지민', '오세훈', '조예린', '윤지호', '김지현', '이준원', '최지은']
const ids = ['seoyeon', 'minjun', 'woosung', 'jimin', 'sehun', 'yerin', 'jiho', 'hong123', 'jun103', 'jieun']
const states = ['활성', '임시차단', '비활성', '활성', '비활성', '활성', '활성', '비활성', '강퇴', '임시차단']
export const memberStatuses = ['활성', '임시차단', '비활성', '강퇴']
export const adminMembers = reactive(Array.from({ length: 100 }, (_, index) => ({
  memberId: 101 + index, name: names[index % 10], loginId: index < 10 ? ids[index % 10] : `${ids[index % 10]}${index}`, email: `${index < 10 ? ids[index % 10] : ids[index % 10] + index}@${index % 10 === 5 || index % 10 === 8 ? 'gmail.com' : 'naver.com'}`, phone: '010-1234-5678', status: states[index % 10], role: '일반회원', joinedAt: '2025.09.15 10:24', updatedAt: '2026.10.03 10:24',
})))
export function updateMemberStatus(memberId, status) {
  if (!memberStatuses.includes(status)) throw new Error('올바른 회원상태를 선택해 주세요.')
  const member = adminMembers.find(item => item.memberId === Number(memberId))
  if (!member) throw new Error('회원을 찾을 수 없습니다.')
  member.status = status
  member.updatedAt = new Intl.DateTimeFormat('sv-SE', { timeZone: 'Asia/Seoul', year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }).format(new Date()).replaceAll('-', '.')
}
export function searchMembers(field, keyword) {
  const query = keyword.trim().toLowerCase()
  return adminMembers.filter(member => (field === 'all' ? `${member.name} ${member.loginId} ${member.email}` : member[field]).toLowerCase().includes(query))
}
