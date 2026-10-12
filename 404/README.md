# 404 (인천교통사공사) 팀 프로젝트

- Spring Boot 4.1.1 / Java 21 / Gradle-Groovy / PostgreSQL / Thymeleaf / JPA / Lombok
- 패키지: `com._404._404` (숫자로 시작하는 패키지명은 Java에서 쓸 수 없어 `_404`로 표기)

## 실행
1. PostgreSQL에 DB 생성 (예: `teamdb`)
2. `src/main/resources/application.yml`의 DB URL / 계정 수정
3. 실행 후 http://localhost:8080 접속
4. 테스트 계정 (app.seed-sample-data=true): `hong` / `hong1234!`

## 이미지 (선택)
- `src/main/resources/static/images/hero.jpg` : 메인 상단 열차 배너
- `src/main/resources/static/images/archive.jpg` : 자료실 배너
  (없으면 기본 그라데이션으로 표시됨)
