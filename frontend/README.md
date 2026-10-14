# 통합 프런트엔드

Vue 3 + Vite 앱입니다. 기존 화면과 스타일을 유지하고 API 명세에 정의된 서버 기능을 사용합니다.

## 실행

```powershell
npm install
npm run dev
```

기본 API 주소는 현재 origin의 `/api`입니다. 별도 API 호스트를 사용하는 경우 프로젝트 루트에 `.env.local`을 만들고 아래 값을 지정합니다.

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

세션 쿠키 인증을 위해 API 서버는 프런트엔드 origin의 credential 요청을 허용해야 합니다. API 명세에 맞춰 `page`, `size`, `searchType`, `keyword` 쿼리와 multipart 파일 업로드를 사용합니다.

## 화면 경로

- 사용자: `/`, `/login`, `/signup`, `/notices`, `/resources`, `/resources/request`, `/qna`, `/qna/my`, `/line7`, `/mypage`
- 관리자: `/admin`, `/admin/members`, `/admin/notices`, `/admin/resources`, `/admin/qna`

기존 URL 일부는 명세 경로로 리다이렉트됩니다.
