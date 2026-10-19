-- ============================================================
-- 404 교통서비스 관리 시스템 더미 데이터
-- 기준: backend.zip의 JPA Entity / Enum
-- 실행 위치: pgAdmin > mini 데이터베이스 > Query Tool
--
-- 주의
-- 1) 이 스크립트는 기존 데이터를 삭제하지 않습니다.
-- 2) 같은 스크립트를 다시 실행해도 동일한 더미 키는 중복 생성하지 않습니다.
-- 3) 관리자 계정은 생성하지 않습니다. 백엔드를 한 번 실행해
--    app.admin.login-id 설정값(기본값 admin)의 관리자 계정이 있어야 합니다.
-- 4) 로그인 테스트 더미 회원 비밀번호: password
-- ============================================================

BEGIN;

-- 자료실 게시글은 admin_id를 참조하므로 관리자 계정이 먼저 있어야 합니다.
DO $$
BEGIN
    IF to_regclass('public.admin') IS NULL THEN
        RAISE EXCEPTION 'admin 테이블이 없습니다. 먼저 백엔드를 실행해 JPA 테이블을 생성하세요.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.admin) THEN
        RAISE EXCEPTION 'admin 테이블에 관리자 계정이 없습니다. 백엔드를 한 번 실행해 기본 관리자 계정을 생성하세요.';
    END IF;
END $$;

-- 1. 일반 회원: 120명
-- BCrypt 해시 값은 테스트 비밀번호 "password"에 사용되는 해시입니다.
INSERT INTO public.member
    (login_id, password, name, email, phone, role, status, created_at, updated_at,
     terms_accepted_at, privacy_accepted_at)
SELECT
    'dummy_member_' || lpad(g.n::text, 3, '0'),
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CASE (g.n - 1) % 10
        WHEN 0 THEN '김민수' WHEN 1 THEN '이지은' WHEN 2 THEN '박서준'
        WHEN 3 THEN '최유진' WHEN 4 THEN '정하늘' WHEN 5 THEN '오지훈'
        WHEN 6 THEN '한서연' WHEN 7 THEN '윤도현' WHEN 8 THEN '강민지'
        ELSE '임수현' END
        || ' ' || lpad(g.n::text, 3, '0'),
    'dummy_member_' || lpad(g.n::text, 3, '0') || '@example.com',
    '010-' || lpad((2000 + g.n)::text, 4, '0') || '-' || lpad((3000 + g.n)::text, 4, '0'),
    'USER',
    CASE WHEN g.n % 25 = 0 THEN 'BANNED'
         WHEN g.n % 17 = 0 THEN 'BLOCKED'
         WHEN g.n % 31 = 0 THEN 'WITHDRAWN'
         WHEN g.n % 13 = 0 THEN 'INACTIVE'
         ELSE 'ACTIVE' END,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.member m
    WHERE m.login_id = 'dummy_member_' || lpad(g.n::text, 3, '0')
);

-- 2. 공지사항: 120건
INSERT INTO public.notice (title, content, writer, view_count, created_at)
SELECT
    '더미 공지사항 ' || lpad(g.n::text, 3, '0'),
    CASE (g.n - 1) % 5
        WHEN 0 THEN '인천 7호선 교통서비스 관리 시스템 이용 안내입니다. 서비스 상태와 공지사항을 확인해 주세요.'
        WHEN 1 THEN '역사 내 편의시설 이용 시 안전 수칙을 준수해 주시기 바랍니다. 불편 사항은 Q&A로 접수할 수 있습니다.'
        WHEN 2 THEN '시스템 점검 및 데이터 갱신 안내입니다. 화면에 표시되는 상태는 시연용 데이터일 수 있습니다.'
        WHEN 3 THEN '교통서비스 이용 관련 자주 묻는 질문과 답변을 정리했습니다. 자세한 사항은 관리자에게 문의해 주세요.'
        ELSE '관리자 페이지의 서비스 현황은 등록된 데이터 기준으로 집계됩니다. 이상 정보는 담당자에게 알려 주세요.' END
        || E'\n\n공지 번호: ' || g.n,
    '관리자',
    (g.n * 7) % 350,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.notice n
    WHERE n.title = '더미 공지사항 ' || lpad(g.n::text, 3, '0')
);

-- 3. 자료실: 120건 (기존 관리자 계정과 연결)
INSERT INTO public.resource (title, content, admin_id, view_count, created_at, updated_at)
SELECT
    '7호선 자료실 더미 ' || lpad(g.n::text, 3, '0'),
    CASE (g.n - 1) % 5
        WHEN 0 THEN '인천·부천 구간 교통서비스 운영 참고 자료입니다. 실제 운영 문서가 아닌 프로젝트 시연용 자료입니다.'
        WHEN 1 THEN '역사 편의시설 관리 시 확인할 항목과 이용자 안내 내용을 정리한 자료입니다.'
        WHEN 2 THEN '교통서비스 상태 분류 예시: NORMAL은 정상, MAINTENANCE는 점검, FAULT는 고장을 의미합니다.'
        WHEN 3 THEN '관리자 업무 참고용 자료입니다. 실제 시설 정보 및 운영 절차는 담당 기관의 공식 자료를 확인해야 합니다.'
        ELSE '시스템 테스트를 위해 생성한 자료입니다. 실제 공사 공지나 공식 운영 지침으로 사용하지 마세요.' END
        || E'\n\n자료 번호: ' || g.n,
    (SELECT a.id FROM public.admin a ORDER BY a.id LIMIT 1),
    (g.n * 11) % 500,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.resource r
    WHERE r.title = '7호선 자료실 더미 ' || lpad(g.n::text, 3, '0')
);

-- 4. Q&A: 120건. 작성 회원과 author_id를 연결하고 답변 상태를 섞습니다.
INSERT INTO public.qna
    (title, writer, author_id, inquiry_type, content, answer, view_count, created_at, answered, is_public)
SELECT
    '교통서비스 문의 더미 ' || lpad(g.n::text, 3, '0'),
    m.name,
    m.id,
    CASE WHEN g.n % 3 = 0 THEN 'FACILITY'
         WHEN g.n % 3 = 1 THEN 'OPERATION'
         ELSE 'ETC' END,
    CASE (g.n - 1) % 5
        WHEN 0 THEN '역사 내 엘리베이터 위치와 이용 가능 여부를 어디에서 확인할 수 있나요?'
        WHEN 1 THEN '교통서비스 상태가 점검으로 표시되는 경우 이용자는 어떻게 확인하면 되나요?'
        WHEN 2 THEN '역사 편의시설 정보가 실제와 다를 때 수정 요청을 접수할 수 있나요?'
        WHEN 3 THEN '공지사항에 안내된 서비스 변경 내용을 다시 확인하고 싶습니다.'
        ELSE '회원 정보 변경 및 계정 이용과 관련해 문의드립니다.' END,
    CASE WHEN g.n % 4 = 0 THEN NULL
         ELSE '문의해 주셔서 감사합니다. 등록된 서비스 정보를 확인하고 안내 사항을 업데이트하겠습니다.' END,
    (g.n * 3) % 120,
    now() - (g.n || ' days')::interval,
    (g.n % 4 <> 0),
    (g.n % 7 <> 0)
FROM generate_series(1, 120) AS g(n)
JOIN public.member m
  ON m.login_id = 'dummy_member_' || lpad(g.n::text, 3, '0')
WHERE NOT EXISTS (
    SELECT 1 FROM public.qna q
    WHERE q.title = '교통서비스 문의 더미 ' || lpad(g.n::text, 3, '0')
);

-- 5. 자료실 추가 요청: 120건
INSERT INTO public.resource_request
    (member_id, title, content, status, created_at, updated_at)
SELECT
    m.id,
    '자료 요청 더미 ' || lpad(g.n::text, 3, '0'),
    CASE (g.n - 1) % 5
        WHEN 0 THEN '7호선 역사별 편의시설 현황 자료를 자료실에 추가해 주세요.'
        WHEN 1 THEN '교통서비스 상태 분류에 대한 안내 자료를 요청합니다.'
        WHEN 2 THEN '역사 이용 안내 및 교통약자 편의시설 관련 자료를 확인하고 싶습니다.'
        WHEN 3 THEN '서비스 관리 화면 사용 방법을 설명하는 자료를 추가해 주세요.'
        ELSE '교통서비스 시스템 관련 사용자 안내 문서를 요청합니다.' END,
    CASE WHEN g.n % 3 = 0 THEN 'COMPLETED' ELSE 'PENDING' END,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 120) AS g(n)
JOIN public.member m
  ON m.login_id = 'dummy_member_' || lpad(g.n::text, 3, '0')
WHERE NOT EXISTS (
    SELECT 1 FROM public.resource_request rr
    WHERE rr.title = '자료 요청 더미 ' || lpad(g.n::text, 3, '0')
);

-- 6. 교통서비스: 120건
INSERT INTO public.transit_service (name, status)
SELECT
    CASE (g.n - 1) % 6
        WHEN 0 THEN '엘리베이터' WHEN 1 THEN '에스컬레이터'
        WHEN 2 THEN '고객센터' WHEN 3 THEN '장애인 화장실'
        WHEN 4 THEN '물품 보관함' ELSE '자전거 보관소' END
        || ' 관리 더미 ' || lpad(g.n::text, 3, '0'),
    CASE WHEN g.n % 20 = 0 THEN 'FAULT'
         WHEN g.n % 5 = 0 THEN 'MAINTENANCE'
         ELSE 'NORMAL' END
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.transit_service ts
    WHERE ts.name = CASE (g.n - 1) % 6
        WHEN 0 THEN '엘리베이터' WHEN 1 THEN '에스컬레이터'
        WHEN 2 THEN '고객센터' WHEN 3 THEN '장애인 화장실'
        WHEN 4 THEN '물품 보관함' ELSE '자전거 보관소' END
        || ' 관리 더미 ' || lpad(g.n::text, 3, '0')
);

-- 7. 열차: 120건. 실제 열차 편성/운행 데이터가 아닌 시연용 데이터입니다.
INSERT INTO public.train (train_no, route, driver, direction, status, active)
SELECT
    '7L-DUMMY-' || lpad(g.n::text, 4, '0'),
    CASE WHEN g.n % 2 = 0 THEN '석남 ↔ 까치울' ELSE '까치울 ↔ 석남' END,
    '기관사 더미 ' || lpad(g.n::text, 3, '0'),
    CASE WHEN g.n % 2 = 0 THEN 'UP' ELSE 'DOWN' END,
    CASE WHEN g.n % 20 = 0 THEN 'FAULT'
         WHEN g.n % 6 = 0 THEN 'MAINTENANCE'
         ELSE 'NORMAL' END,
    (g.n % 17 <> 0)
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.train t
    WHERE t.train_no = '7L-DUMMY-' || lpad(g.n::text, 4, '0')
);

-- 8. 일별 대시보드 통계: 최근 120일 (날짜별 중복은 건너뜁니다.)
INSERT INTO public.daily_stat
    (stat_date, member_count, notice_count, qna_pending_count,
     service_total, normal_count, maintenance_count, fault_count)
SELECT
    (current_date - (g.n - 1))::date,
    120 + g.n,
    120 + (g.n / 2),
    20 + (g.n % 18),
    120,
    84 + (g.n % 10),
    26 - (g.n % 5),
    10 - (g.n % 4)
FROM generate_series(1, 120) AS g(n)
ON CONFLICT (stat_date) DO NOTHING;

COMMIT;

-- 생성 결과 확인: 각 테이블의 더미 데이터 개수
SELECT 'member' AS table_name, count(*) AS dummy_count FROM public.member WHERE login_id LIKE 'dummy_member_%'
UNION ALL
SELECT 'notice', count(*) FROM public.notice WHERE title LIKE '더미 공지사항 %'
UNION ALL
SELECT 'resource', count(*) FROM public.resource WHERE title LIKE '7호선 자료실 더미 %'
UNION ALL
SELECT 'qna', count(*) FROM public.qna WHERE title LIKE '교통서비스 문의 더미 %'
UNION ALL
SELECT 'resource_request', count(*) FROM public.resource_request WHERE title LIKE '자료 요청 더미 %'
UNION ALL
SELECT 'transit_service', count(*) FROM public.transit_service WHERE name LIKE '% 관리 더미 %'
UNION ALL
SELECT 'train', count(*) FROM public.train WHERE train_no LIKE '7L-DUMMY-%'
UNION ALL
SELECT 'daily_stat', count(*) FROM public.daily_stat WHERE stat_date >= current_date - 119;
