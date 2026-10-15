# 역할별 API 명세 정리

> 이 문서는 백엔드 API 명세를 비회원·회원·관리자 관점으로 재배열한 참고 자료입니다. API 데이터나 애플리케이션 코드는 변경하지 않습니다.

## 공통 규칙

- 기본 경로: `/api`
- 일반 요청 및 응답: JSON
- 파일 업로드: `multipart/form-data`의 `files` 필드
- 목록 공통 쿼리: `page`, `size`, `searchType`, `keyword`
- 목록 공통 응답: `{ totalCount, page, size, items: [...] }`
- 명세에는 인증 토큰/쿠키 형식이 구체적으로 적혀 있지 않습니다. 실제 인증 전달 방식은 백엔드 설정과 확인이 필요합니다.

## 비회원

비회원에게 필요한 가입·로그인 API와 공개 열람 화면에서 사용하는 조회 API입니다. 권한이 명세에 별도로 고정되어 있지 않은 조회 API는 서버의 실제 접근 정책을 확인해야 합니다.

| 기능 | Method | 경로 | 요청/응답 요약 |
|---|---|---|---|
| 아이디 중복 확인 | GET | `/auth/check-id?loginId=...` | 아이디 사용 가능 여부 |
| 회원가입 | POST | `/auth/signup` | `{ loginId, password, name, email, agreeTerms }` |
| 로그인 | POST | `/auth/login` | 요청 `{ loginId, password }`; 응답 `{ memberId, name, role }` |
| 공지 목록 | GET | `/notices` | 공통 목록 쿼리; 공지 목록 응답 |
| 공지 상세 | GET | `/notices/{noticeId}` | 공지 상세 |
| 역 목록 | GET | `/line7/stations` | 역 목록 |
| 역 상세 | GET | `/line7/stations/{stationId}` | 역 상세 |

## 회원

회원 화면의 프로필, 자료실, 문의, 역 환승정보와 관련된 API입니다.

| 기능 | Method | 경로 | 요청/응답 요약 |
|---|---|---|---|
| 로그아웃 | POST | `/auth/logout` | 현재 로그인 세션 종료 |
| 내 정보 조회 | GET | `/members/me` | `{ loginId, name, email }` |
| 내 정보 수정 | PUT | `/members/me` | `{ name, email, password? }` |
| 회원 탈퇴 | DELETE | `/members/me` | `{ loginId, password, passwordConfirm }` |
| 자료 목록 | GET | `/resources` | 공통 목록 쿼리; 자료 목록 응답 |
| 자료 상세 | GET | `/resources/{resourceId}` | 자료 상세 및 첨부파일 메타데이터 |
| 첨부파일 다운로드 | GET | `/resources/{resourceId}/files/{fileId}` | 파일 응답 |
| 자료 요청 등록 | POST | `/resources/requests` | `{ title, content, files[] }`; 파일이 있으면 multipart |
| Q&A 전체 목록 | GET | `/qna` | 공통 목록 쿼리 |
| 내 Q&A 목록 | GET | `/qna/my` | 공통 목록 쿼리 |
| Q&A 상세 | GET | `/qna/{qnaId}` | Q&A 상세 |
| Q&A 등록 | POST | `/qna` | 질문 DTO; 첨부가 있으면 multipart |
| Q&A 수정 | PUT | `/qna/{qnaId}` | 질문 DTO; 첨부가 있으면 multipart |
| Q&A 삭제 | DELETE | `/qna/{qnaId}` | 질문 삭제 |
| 역 환승정보 | GET | `/line7/stations/{stationId}/transfer` | 환승정보; 명세상 인증 필요 |

Q&A 문의 유형은 `FACILITY`, `OPERATION`, `ETC`, 처리 상태는 `WAITING`, `ANSWERED`입니다. Q&A의 공개 여부 필드는 명세에 정의되어 있지 않습니다.

## 관리자

아래 경로들은 관리자 화면용 API입니다. 관리자 권한 검증은 서버에서도 수행해야 합니다.

| 기능 | Method | 경로 | 요청/응답 요약 |
|---|---|---|---|
| 대시보드 | GET | `/admin/dashboard` | 통계 및 최근 항목; 응답 필드명이 구체적으로 정의되지 않음 |
| 회원 목록 | GET | `/admin/members` | 공통 목록 쿼리 |
| 회원 상세 | GET | `/admin/members/{memberId}` | 회원 상세 |
| 회원 상태 변경 | PATCH | `/admin/members/{memberId}/status` | `{ status }` |
| 공지 등록 | POST | `/admin/notices` | 공지 DTO |
| 공지 수정 | PUT | `/admin/notices/{noticeId}` | 공지 DTO |
| 공지 삭제 | DELETE | `/admin/notices/{noticeId}` | 공지 삭제 |
| 자료 등록 | POST | `/admin/resources` | 자료 DTO; 첨부파일은 multipart `files` |
| 자료 수정 | PUT | `/admin/resources/{resourceId}` | 자료 DTO; 첨부파일은 multipart `files` |
| 자료 삭제 | DELETE | `/admin/resources/{resourceId}` | 자료 삭제 |
| 자료 요청 목록 | GET | `/admin/resource-requests` | 공통 목록 쿼리 |
| 자료 요청 상세 | GET | `/admin/resource-requests/{requestId}` | 요청 상세 |
| 자료 요청 상태 변경 | PATCH | `/admin/resource-requests/{requestId}/status` | `{ status }` |
| 자료 요청 삭제 | DELETE | `/admin/resource-requests/{requestId}` | 요청 삭제 |
| Q&A 목록 | GET | `/admin/qna` | 공통 목록 쿼리; 상태 검색 가능 |
| Q&A 상세 | GET | `/admin/qna/{qnaId}` | 질문 및 답변 상세 |
| Q&A 답변 등록 | POST | `/admin/qna/{qnaId}/answer` | `{ answer }` |
| Q&A 삭제 | DELETE | `/admin/qna/{qnaId}` | Q&A 삭제 |
| 열차 목록 | GET | `/admin/line7/trains` | 공통 목록 쿼리 |
| 열차 상세 | GET | `/admin/line7/trains/{trainId}` | 열차 상세 |
| 열차 등록 | POST | `/admin/line7/trains` | 열차 DTO; `lineId` 포함 |
| 열차 수정 | PUT | `/admin/line7/trains/{trainId}` | 열차 DTO; `lineId` 포함 |
| 열차 활성 상태 변경 | PATCH | `/admin/line7/trains/{trainId}/active` | `{ active }` |
| 역 목록 | GET | `/admin/line7/stations` | 공통 목록 쿼리 |
| 역 상세 | GET | `/admin/line7/stations/{stationId}` | 역 상세 |
| 역 등록 | POST | `/admin/line7/stations` | 역 DTO; `stationOrder`, `nextInspectionDueDate` 등 포함 |
| 역 수정 | PUT | `/admin/line7/stations/{stationId}` | 역 DTO |
| 역 활성 상태 변경 | PATCH | `/admin/line7/stations/{stationId}/active` | `{ active }` |

## 명세 확인이 필요한 항목

- 로그인 이후 인증 정보의 전달 방식과 세션 만료 처리
- 관리자 대시보드 응답 DTO의 구체적인 필드
- Q&A 검색 `searchType`의 허용값과 공개 여부 처리
- 공지사항 첨부파일 API
- 회원관리 비밀번호 초기화 API
- 열차·역 삭제 API: 명세에는 삭제 대신 활성 상태 변경 API만 정의됨
- 현재 화면 입력 중 일부는 명세 DTO에 대응 항목이 없음 (예: 회원가입 전화번호, 열차 `lineId` 입력, 역 `stationOrder` 입력)

