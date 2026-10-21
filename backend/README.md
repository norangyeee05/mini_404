# 인천 7호선 미니 프로젝트 - Backend

Spring Boot 4.x + Java 21 + Gradle + Spring Data JPA + PostgreSQL (세션 기반 로그인, REST API)

> 이 백엔드는 **인증·회원, 자료실(+추가 요청), Q&A, 관리자 회원관리/자료실/자료실 요청/Q&A 답변, 대시보드, 철도정보(7호선 노선·열차·역)**를 제공합니다.

## 실행 방법

1. PostgreSQL에서 DB 생성: `CREATE DATABASE mini404;`
2. DB 비밀번호 설정 (택 1)
   - 환경변수 `DB_PASSWORD` 지정 (기본값 `1234`)
   - 또는 `src/main/resources/application-local.yml.example` 을 `application-local.yml` 로 복사해 수정 후 `--spring.profiles.active=local` 로 실행
3. 실행: IntelliJ에서 `BackendApplication` 실행, 또는 `gradlew bootRun` (wrapper가 없다면 `gradle wrapper` 로 생성)
4. Flyway가 버전별 DB 변경을 먼저 적용하고, Hibernate `ddl-auto: update`가 기존 엔티티 테이블을 생성·동기화합니다. 현재 첫 마이그레이션은 기존 `member` 테이블에 `role` 컬럼을 안전하게 추가합니다. 전체 기존 테이블의 Flyway 기준 스키마가 아직 없으므로, 현 단계에서는 `ddl-auto: update`를 유지하고 새 DB 변경은 `db/migration/V*.sql`로 추가합니다.

서버 시작 시 관리자 계정이 없으면 자동 생성됩니다. (`app.admin.*`, 기본 `admin` / `admin1234!`, **운영에서는 반드시 변경**)

## 공통 규칙

- 응답: `{ "success": true, "data": {...}, "message": "..." }` / 실패 시 `success:false` + `message`
- 목록 응답 `data`: `{ totalCount, page, size, items[] }`, 공통 쿼리 `page`(기본 1), `size`(기본 10), `searchType`, `keyword`
- 인증: 로그인하면 세션 쿠키(`JSESSIONID`)가 발급됩니다. 프론트 Axios는 `withCredentials: true` 필수
- 상태 변경 API는 `XSRF-TOKEN` 쿠키와 `X-XSRF-TOKEN` 헤더를 비교해 CSRF 요청을 확인합니다. Axios 인스턴스가 이 값을 자동 전송합니다.
- 배포 시 HTTPS를 사용하면 `SESSION_COOKIE_SECURE=true`를 설정하세요. HTTPS 프론트와 API가 서로 다른 사이트에 배치된 경우에는 `SESSION_COOKIE_SAME_SITE=None`과 `SESSION_COOKIE_SECURE=true`가 필요합니다.
- CORS: `http://localhost:5173` 허용 (`app.cors-allowed-origins`)
- 권한 오류: 미로그인 `401`, 권한 없음 `403`, 대상 없음 `404`, 중복 `409`, 유효성 실패 `400`
- 파일 업로드: `multipart/form-data` (`title`, `content`, 파일은 `files` 또는 `files[]`), 최대 20MB/파일

## 구현된 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 (`loginId,password,name,email,agreeTerms`, `phone` 선택) | 비회원 |
| GET | `/api/auth/check-id?loginId=` | 아이디 중복 확인 (`available`) | 비회원 |
| POST | `/api/auth/login` | 로그인 (회원/관리자 공통, `memberId,name,role`) | 비회원 |
| POST | `/api/auth/logout` | 로그아웃 | 회원 |
| GET | `/api/members/me` | 내 정보 조회 | 회원 |
| PUT | `/api/members/me` | 내 정보 수정 (`name,email,password`선택) | 회원 |
| DELETE | `/api/members/me` | 회원 탈퇴 (`loginId,password,passwordConfirm`) | 회원 |
| GET | `/api/notices` | 공지사항 목록 (searchType: title/writer) | 전체 |
| GET | `/api/notices/{noticeId}` | 공지사항 상세 (조회수 +1) | 전체 |
| POST | `/api/admin/notices` | 공지사항 등록 (`title,content`) | 관리자 |
| PUT | `/api/admin/notices/{noticeId}` | 공지사항 수정 | 관리자 |
| DELETE | `/api/admin/notices/{noticeId}` | 공지사항 삭제 | 관리자 |
| GET | `/api/resources` | 자료실 목록 (`searchType`: title/writer) | 로그인 회원/관리자 |
| GET | `/api/resources/{resourceId}` | 상세 (조회수 +1, `files[]`) | 로그인 회원/관리자 |
| GET | `/api/resources/{resourceId}/files/{fileId}` | 파일 다운로드 | 로그인 회원/관리자 |
| POST | `/api/resources/requests` | 자료실 추가 요청 (`title,content,files[]`) | 회원 |
| GET | `/api/qna` | Q&A 목록 (`searchType`, `inquiryType`, `keyword`) | 회원 |
| GET | `/api/qna/my` | 내 질문 목록 | 회원 |
| GET | `/api/qna/{qnaId}` | Q&A 상세 (`content`, `files[]`, `answer`) | 회원 |
| POST | `/api/qna` | 질문 등록 (JSON 또는 multipart) | 회원 |
| PUT | `/api/qna/{qnaId}` | 질문 수정 (작성자) | 작성자 |
| DELETE | `/api/qna/{qnaId}` | 질문 삭제 (작성자) | 작성자 |
| GET | `/api/qna/{qnaId}/files/{fileId}` | 첨부파일 다운로드 | 회원 |
| GET | `/api/admin/qna` | Q&A 답변 목록 (`inquiryType`, `status`) | 관리자 |
| GET | `/api/admin/qna/{qnaId}` | 답변 상세 | 관리자 |
| PUT | `/api/admin/qna/{qnaId}` | 관리자 질문·공개여부·문의유형·답변상태 수정 (`title,content,inquiryType,isPrivate,answer,status`) | 관리자 |
| POST | `/api/admin/qna/{qnaId}/answer` | 답변 등록 | 관리자 |
| PUT | `/api/admin/qna/{qnaId}/answer` | 답변 수정 | 관리자 |
| DELETE | `/api/admin/qna/{qnaId}` | Q&A 삭제 | 관리자 |
| POST | `/api/admin/resources` | 자료 등록 | 관리자 |
| PUT | `/api/admin/resources/{resourceId}` | 자료 수정 (`deleteFileIds`로 기존 파일 삭제) | 관리자 |
| DELETE | `/api/admin/resources/{resourceId}` | 자료 삭제 | 관리자 |
| GET | `/api/admin/members` | 회원 목록 (`searchType`: name/loginId/email, `status`) | 관리자 |
| GET | `/api/admin/members/{memberId}` | 회원 상세 | 관리자 |
| PATCH | `/api/admin/members/{memberId}/status` | 상태 변경 (`ACTIVE/BLOCKED/BANNED`) | 관리자 |
| GET | `/api/admin/resource-requests` | 요청 목록 (`status`: PENDING/COMPLETED) | 관리자 |
| GET | `/api/admin/resource-requests/{requestId}` | 요청 상세 | 관리자 |
| GET | `/api/admin/resource-requests/{requestId}/files/{fileId}` | 요청 첨부파일 다운로드 (명세서 외 추가) | 관리자 |
| PATCH | `/api/admin/resource-requests/{requestId}/status` | 처리 상태 변경 | 관리자 |
| DELETE | `/api/admin/resource-requests/{requestId}` | 요청 삭제 | 관리자 |

## 명세서와 달라진/추가된 부분

- 엔티티에 `resource.view_count`, `resource_request`, `attachment` 를 추가했습니다. 실제 DB 반영은 Flyway 마이그레이션과 Hibernate 스키마 동기화가 담당합니다.
- 회원 상태에 `WITHDRAWN`(탈퇴)을 추가했습니다. 탈퇴 시 데이터는 지우지 않고 상태만 바꾸며, 이후 로그인은 불가합니다. 관리자 상태 변경 API에서는 지정할 수 없습니다.
- `BLOCKED`/`BANNED` 회원은 로그인이 거부됩니다(403). 이미 로그인된 세션은 다음 로그인 전까지 유지됩니다.
- 로그인은 `member` → `admin` 순으로 조회하며, 관리자는 `role=ADMIN` 으로 응답합니다.
- 아이디 규칙(영문·숫자·밑줄 4~20자), 비밀번호 규칙(8~64자)은 임의로 정한 값이므로 피그마 화면 규칙과 다르면 `SignupRequest` 를 수정하세요.
- Spring Security 필터 대신 `AuthInterceptor` 로 권한을 검사하고, 비밀번호 암호화에만 `spring-security-crypto`(BCrypt)를 사용합니다.
- `backend/src/test/resources/application-test.yml`은 H2를 사용하므로 `gradlew test`가 개발 PostgreSQL에 의존하지 않습니다. 운영 DB 스키마는 Flyway 마이그레이션으로 계속 추가하세요.





