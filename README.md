# 통합 프론트엔드

mini01 기반의 인증·마이페이지·자료실·회원관리와 teammini의 공지사항·Q&A·관리자 대시보드를 통합했습니다.

## 실행

```powershell
npm install
npm run dev
```

배포 빌드: npm run build

일반 회원: demo / demo1234
관리자: admin / admin1234

홈: /, 관리자 대시보드: /admin/dashboard
공지사항: /notices, 자료실: /resources, Q&A: /qna
관리자: /admin/notices, /admin/resources, /admin/qna, /admin/members

공통 헤더와 라우터는 하나만 사용하며 기존 /notice 주소도 지원합니다.
데이터는 기존 모의 데이터 방식입니다. 인증은 메모리에서 유지되고 새로고침하면 로그아웃됩니다. 게시판은 localStorage에 저장됩니다. 실제 백엔드 연동 및 파일 업로드는 별도 작업이 필요합니다.
중복 회원관리 화면은 mini01의 구현을 사용합니다. teammini 대시보드의 교통 서비스 데이터와 일부 버튼은 원래의 시연용 상태입니다.

## 역 지도
역 상세 화면은 카카오맵 JavaScript SDK로 지도와 역 마커를 표시합니다.
.env.local의 VITE_KAKAO_MAP_KEY에 JavaScript 키를 설정하고 개발 서버를 재시작하세요.
카카오 Developers에서 카카오맵 사용을 활성화하고 JavaScript SDK 도메인에 접속 주소를 등록해야 합니다.
http://localhost:5173에서 실제 지도 표시를 확인했습니다.
배포 주소나 127.0.0.1, 다른 포트로 접속하면 해당 주소도 앱 설정에 등록해야 합니다.
지도 키는 브라우저용 JavaScript 키이며, REST API 키나 Admin 키를 사용하지 않습니다.

