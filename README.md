# 인천교통공사 웹사이트 프로젝트 매뉴얼

# 1. 프로젝트 소개

## 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [배포 관리](#2-배포-관리)
   - [2-1. 중앙 DB 공유형 구조](#2-1-중앙-db-공유형-구조)
   - [2-2. 배포 파일을 받은 사용자: CMD에서 localhost 실행](#2-2-배포-파일을-받은-사용자-cmd에서-localhost-실행)
   - [2-3. Docker Hub 이미지 빌드와 업데이트](#2-3-docker-hub-이미지-빌드와-업데이트)
3. [GitHub 협업 방식](#3-github-협업-방식)
4. [로그인, 세션, 쿠키와 CSRF 토큰](#4-로그인-세션-쿠키와-csrf-토큰)
5. [설정과 데이터 보호](#5-설정과-데이터-보호)
6. [문제 해결](#6-문제-해결)
7. [주요 파일](#7-주요-파일)

인천교통공사 웹사이트는 공지사항, 자료실, Q&A, 7호선 역·열차 정보와 회원·관리자 기능을 제공합니다.

| 구성 | 세부 기술 | 역할 |
|---|---|---|
| 프론트엔드 프레임워크 | Vue 3, Vue Router | 컴포넌트 기반 화면 구성과 페이지 이동·라우팅 |
| 프론트엔드 상태·데이터 통신 | Pinia, Axios | 화면 간 상태 관리, 쿠키 기반 세션을 포함한 API 요청 |
| 프론트엔드 시각화 | Chart.js, vue-chartjs | 통계·대시보드 차트 표현 |
| 프론트엔드 빌드 | Vite, Node.js 22 | 개발 서버 구동 및 정적 배포 파일 생성 |
| 웹 서버·프록시 | Nginx | 정적 화면 제공, `/api` 요청을 백엔드로 전달 |
| 백엔드 언어·프레임워크 | Java 21, Spring Boot 4 | REST API와 서버 애플리케이션 실행 |
| 백엔드 웹·검증 | Spring MVC, Jakarta Bean Validation | HTTP API 처리와 입력값 검증 |
| ORM·DB 변경 관리 | Spring Data JPA, Hibernate, Flyway | 객체와 PostgreSQL 데이터 매핑, 스키마 마이그레이션 |
| 인증·보안 | 서버 세션, 요청 인터셉터, BCrypt, CSRF 토큰 | 로그인 상태·역할 확인, 비밀번호 해시, 변경 요청 보호 |
| 데이터베이스 | PostgreSQL | 회원, 게시물, 교통정보 저장 |

소스는 하나의 저장소에서 관리하고, 배포할 때 프론트엔드와 백엔드 이미지를 각각 실행합니다.

# 2. 배포 관리

## 2-1. 중앙 DB 공유형 구조

기본 배포는 **중앙 DB 공유형**입니다. 사용자 PC에서는 화면 컨테이너만 실행하고, 중앙 서버의 백엔드가 중앙 PostgreSQL `mini404` DB를 사용합니다.

```text
브라우저 http://localhost
  → 사용자 PC 프론트엔드/Nginx
  → 중앙 백엔드 API
  → 중앙 PostgreSQL mini404
```

- 사용자 PC에는 PostgreSQL이나 백엔드 컨테이너가 필요하지 않습니다.
- 모두 같은 중앙 백엔드를 이용하면 같은 DB 내용을 보고 변경 사항도 공유합니다.
- 첨부파일도 중앙 백엔드의 Docker 볼륨 `uploaded_files`에 저장됩니다.
- `localhost`는 접속한 각 PC 자신입니다. 사용자 PC에서 화면 컨테이너를 실행해야 하며, 중앙 백엔드 주소가 해당 PC에서 네트워크로 접근 가능해야 합니다.
- 내부망에서는 서버 내부 IP를, 인터넷 배포에서는 HTTPS 공개 주소를 사용합니다. `MINI_API_UPSTREAM`에 `localhost`/`127.0.0.1`을 넣으면 각 사용자 PC 자신을 가리켜 중앙 서버에 연결되지 않습니다.

중앙 서버 관리자는 DB·백엔드·업로드 볼륨을 운영하고 백업합니다. DB 포트는 일반 사용자에게 공개하지 말고, 사용자 PC에서 중앙 API에 접근하도록 네트워크/방화벽을 설정합니다. Compose 기본 API 포트는 `8080`입니다. 외부 공개 서비스는 HTTPS 도메인과 인증서를 설정해야 합니다.

## 2-2. 배포 파일을 받은 사용자: CMD에서 localhost 실행

1. Docker Desktop을 설치하고 실행합니다.
2. 팀장님이 전달한 중앙 DB 공유형 배포 파일을 `C:\mini-team`에 풉니다. 아래 명령을 실행할 때 `C:\mini-team` 바로 아래에 `compose.yaml`, `nginx.shared.conf.template`, `.env.example`이 있어야 합니다.
3. CMD에서 `.env.example`을 복사해 `.env`를 만들고 편집합니다. 중앙 관리자가 알려준 API 주소를 `MINI_API_UPSTREAM`에 넣습니다. 주소 끝에는 `/`를 붙이지 않습니다. 사설망 예: `MINI_API_UPSTREAM=http://192.168.0.51:8080`. 인터넷에서는 HTTPS 주소를 사용합니다.

CMD를 열고 아래 명령을 한 줄씩 실행합니다.

```bat
:: ① 배포 파일을 푼 프로젝트 폴더로 이동
cd /d C:\mini-team

:: ② 예시 환경 파일을 복사한 뒤 중앙 API 주소를 설정
copy .env.example .env
notepad .env

:: ③ compose.yaml에 지정된 이미지 다운로드
docker compose pull

:: ④ 다운로드된 이미지 목록 확인
docker images

:: ⑤ 팀장님이 제공한 프런트엔드 이미지를 명시적으로 다운로드
docker pull sumi0705/mini-1-frontend:1.0

:: ⑥ 컨테이너 실행 및 상태 확인
docker compose up -d
docker compose ps
```

`docker compose pull`이 Compose 파일에 지정된 프런트엔드 이미지를 이미 받으므로 ⑤ 명령은 따로 이미지 이름을 확인하거나 다시 받기 위한 명시적 명령입니다. 같은 이미지가 이미 최신이면 Docker가 기존 레이어를 재사용합니다. CMD에서 `#`은 주석으로 처리되지 않으므로 위 예시의 `::` 주석 표기를 사용합니다.

브라우저에서 [http://localhost](http://localhost)를 엽니다. 종료할 때 CMD에서 같은 폴더로 이동한 뒤 `docker compose down`을 실행합니다. 80번 포트가 이미 사용 중이면 배포 Compose의 `80:80` 중 왼쪽 포트를 변경하고 그 포트로 접속합니다. 이 배포 파일은 프런트엔드 컨테이너만 실행합니다.

## 2-3. Docker Hub 이미지 빌드와 업데이트

- 백엔드: `sumi0705/mini-1-backend:1.0`
- 프론트엔드: `sumi0705/mini-1-frontend:1.0`

루트의 `compose.yaml`은 중앙 서버용입니다. 기본 설정은 별도의 DB 컨테이너를 실행하지 않고 `.env.2tier`에 지정한 중앙 DB에 연결합니다. `local-db` 프로필은 로컬 개발 DB가 필요할 때만 사용합니다.

소스 변경 후 이미지 빌드 및 Docker Hub Push (프로젝트 루트에서):

```powershell
docker login
docker compose --env-file .env.2tier build backend frontend
docker compose --env-file .env.2tier push backend frontend
```

중앙 서버에서 이미지와 서비스를 갱신합니다.

```powershell
docker compose --env-file .env.2tier pull backend frontend
docker compose --env-file .env.2tier up -d backend frontend
docker compose --env-file .env.2tier ps
docker compose --env-file .env.2tier logs --tail 100 backend frontend
```

릴리스마다 이미지 태그를 올리고 Compose 및 배포 파일의 태그를 함께 맞춥니다. Docker Hub는 실행 이미지, GitHub는 소스 코드와 변경 이력을 관리하므로 각각 별도 Push가 필요합니다.

# 3. GitHub 협업 방식

기본 브랜치의 변경사항을 받아 작업 브랜치에서 수정하고, 커밋·Push 후 Pull Request 검토와 병합을 거치는 흐름을 권장합니다.

```powershell
git switch main
git pull
git switch -c feature/작업이름
git add .
git commit -m "작업 내용 요약"
git push -u origin feature/작업이름
```

기본 브랜치명과 팀의 리뷰 규칙이 다르면 저장소의 규칙을 따릅니다. `.env.2tier`, `.env`, DB 덤프, 비밀번호, 토큰, 개인 키는 GitHub에 올리지 않습니다. 비밀값이 실수로 올라갔다면 파일만 지우지 말고 해당 비밀번호/키도 교체해야 합니다.

# 4. 로그인, 세션, 쿠키와 CSRF 토큰

이 프로젝트는 **JWT Bearer 토큰이 아니라 서버 세션 방식**으로 로그인 상태를 관리합니다.

- 회원 비밀번호는 BCrypt 해시로 저장하며 원문 비밀번호를 저장하지 않습니다.
- 로그인 시 서버는 새 세션에 사용자 ID와 역할을 저장하고 기존 세션을 폐기해 세션 고정 공격 위험을 줄입니다.
- 브라우저는 세션 식별 쿠키를 보냅니다. 세션 쿠키는 `HttpOnly`라 JavaScript에서 읽을 수 없고 기본 `SameSite=Lax`입니다.
- 프론트엔드 Axios는 쿠키를 포함해 API를 요청합니다. 로그아웃은 서버 세션을 무효화하고 CSRF 쿠키를 지웁니다.
- 서버는 안전한 난수 CSRF 토큰을 세션에 보관합니다. 변경 요청에서 `XSRF-TOKEN` 쿠키, `X-XSRF-TOKEN` 헤더, 서버 세션의 값이 모두 일치해야 합니다. 프론트엔드가 헤더를 만들 수 있도록 CSRF 쿠키만 `HttpOnly=false`입니다.
- HTTPS 운영은 `SESSION_COOKIE_SECURE=true`로 설정하고 CORS 허용 출처를 실제 사이트 주소에 맞춥니다. 현재 구성은 단일 백엔드 인스턴스 기준이며, 여러 백엔드로 확장하려면 공유 세션 저장소가 필요합니다.
- CSRF 토큰은 요청 위조 방지용이며 DB 암호나 API 비밀키가 아닙니다.

# 5. 설정과 데이터 보호

중앙 서버의 `.env.2tier`에는 DB 접속값, 관리자 암호, CORS 주소, 지도 키 등 비밀 설정이 들어갈 수 있습니다. 실제 값을 README나 공개 저장소, 메신저에 복사하지 마세요. `.env.example`에는 형식과 예시만 둡니다.

`VITE_KAKAO_MAP_KEY`는 프론트엔드 빌드 인자이며 브라우저 번들에서 확인될 수 있습니다. 공개 가능한 키인지 확인하고 카카오 개발자 콘솔에서 허용 도메인을 제한하세요. DB와 업로드 볼륨을 정기적으로 백업하고 복구도 확인합니다. 운영 데이터가 있는 볼륨을 삭제하지 마세요.

# 6. 문제 해결

| 증상 | 확인 사항 |
|---|---|
| localhost 연결 거부 | Docker Desktop, `docker compose ps`, PC의 80번 포트 사용 여부 |
| 화면은 뜨나 데이터가 없음 | `MINI_API_UPSTREAM`의 중앙 서버 주소, 사용자 PC에서 API 주소/포트 접근 가능 여부 |
| 로그인 후 403 | 페이지 새로고침, 쿠키 허용, HTTP/HTTPS와 Secure·SameSite 설정 |
| 중앙 DB 연결 오류 | 중앙 백엔드의 DB 주소·포트·DB명·계정 및 PostgreSQL 접근 여부 |
| 파일 업로드 실패 | 파일 20MB/요청 전체 60MB 제한, 중앙 업로드 볼륨 상태 |

중앙 서버 루트에서:

```powershell
docker compose --env-file .env.2tier ps
docker compose --env-file .env.2tier logs --tail 100 backend
```

사용자 PC 배포 폴더에서:

```powershell
docker compose ps
docker compose logs --tail 100 frontend
```

# 7. 주요 파일

| 경로 | 역할 |
|---|---|
| `backend/` | Spring Boot 소스 및 DB 마이그레이션 |
| `frontend/` | Vue 프론트엔드 소스 |
| `compose.yaml` | 중앙 서버 서비스와 선택적 로컬 DB 프로필 |
| `Dockerfile.backend`, `Dockerfile.frontend` | 서비스별 이미지 빌드 |
| `nginx.conf` | 화면 제공 및 API 프록시 |
| `중앙DB_공유형_Localhost_배포파일/` | 사용자 PC용 프론트엔드 배포 파일 |
| `.env.2tier` | 중앙 서버 비공개 설정. 외부 공유 금지 |
