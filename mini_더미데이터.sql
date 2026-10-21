-- ============================================================
-- mini 프로젝트 시연용 더미 데이터
-- PostgreSQL / public 스키마 / pgAdmin Query Tool에서 실행
--
-- 생성량
--   7호선 역: 석남(761) ~ 까치울(751), 총 11개 역
--   회원: 일반 USER 320명 + WEB_ADMIN 1명 + ADMIN 1명
--   공지사항: 120건 / 자료실: 120건
--   Q&A: 답변 대기 120건 + 답변 완료 120건
--   열차: 운행(NORMAL) 120대 + 점검(MAINTENANCE) 120대 + 고장(FAULT) 120대
--
-- 주의
--   * 이 스크립트는 DELETE/TRUNCATE를 수행하지 않습니다.
--   * seed_ 접두어를 사용하는 고정 키를 확인해 재실행 시 중복을 건너뜁니다.
--   * 테스트 회원 비밀번호는 password 입니다 (BCrypt 저장).
--   * 실제 운영 자료가 아닌 시연용 데이터입니다.
--   * 자료실은 admin_id FK를 사용하므로 백엔드를 한 번 실행해 admin 계정을
--     먼저 생성한 뒤 실행하세요. 기본 관리자 계정은 이 스크립트에서 추가하지 않습니다.
--   * 기관사, 역무원, 작성자 이름에는 숫자를 넣지 않았습니다.
--   * 열차 번호는 7L-0001 형식(7L- + 숫자 4자리)입니다.
-- ============================================================

BEGIN;

DO $$
BEGIN
    IF to_regclass('public.admin') IS NULL THEN
        RAISE EXCEPTION 'public.admin 테이블이 없습니다. 먼저 백엔드를 실행해 JPA 테이블을 생성하세요.';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.admin) THEN
        RAISE EXCEPTION 'public.admin 계정이 없습니다. 백엔드를 한 번 실행해 관리자 계정을 만든 뒤 다시 실행하세요.';
    END IF;
    IF to_regclass('public.member') IS NULL OR to_regclass('public.railway_line') IS NULL
       OR to_regclass('public.railway_station') IS NULL OR to_regclass('public.station') IS NULL
       OR to_regclass('public.train') IS NULL OR to_regclass('public.notice') IS NULL
       OR to_regclass('public.resource') IS NULL OR to_regclass('public.qna') IS NULL THEN
        RAISE EXCEPTION '필요한 테이블이 없습니다. 백엔드를 먼저 실행해 JPA 테이블을 생성하세요.';
    END IF;
END $$;

-- 1. 7호선 노선 및 피그마 화면 기준 인천·부천 구간 11개 역
INSERT INTO public.railway_line (name, display_color, active)
VALUES ('7호선', '#8C914F', TRUE)
ON CONFLICT (name) DO UPDATE
SET display_color = EXCLUDED.display_color, active = TRUE;

WITH line_row AS (
    SELECT id FROM public.railway_line WHERE name = '7호선'
), station_data(station_no, name, name_en, seq, lat, lon, address, facilities, transfer_line, transfer_info, staff_name, phone, usage_info) AS (
    VALUES
    ('761','석남','Seoknam',1,37.5061,126.6763,'인천광역시 서구 길주로 120','고객센터|장애인 화장실|물품 보관함|자전거 보관소|스마트도서관|엘리베이터|에스컬레이터','인천 2호선','7호선 ↔ 인천 2호선 환승','김민수','032-560-7601','석남 방면 승강장 및 환승 통로 이용 안내'),
    ('760','산곡','Sangok',2,37.5086,126.7030,'인천광역시 부평구 길주로 지하 389','엘리베이터|에스컬레이터|장애인 화장실|고객센터',NULL,NULL,'이지은','032-560-7602','출입구와 승강장 이동 시 안내 표지를 확인해 주세요'),
    ('759','부평구청','Bupyeong-gu Office',3,37.5083,126.7219,'인천광역시 부평구 길주로 지하 527','엘리베이터|에스컬레이터|화장실|고객센터','인천 1호선','7호선 ↔ 인천 1호선 환승','박서준','032-560-7603','인천 1호선 환승 통로 이용 안내'),
    ('758','굴포천','Gulpocheon',4,37.5068,126.7313,'인천광역시 부평구 길주로 지하 599','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'최유진','032-560-7604','역사 내 편의시설 위치 안내'),
    ('757','삼산체육관','Samsan Gymnasium',5,37.5069,126.7427,'인천광역시 부평구 길주로 지하 713','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'정하늘','032-560-7605','체육관 방면 출구 안내'),
    ('756','상동','Sang-dong',6,37.5058,126.7531,'경기도 부천시 길주로 지하 104','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'오지훈','032-560-7606','역사 이용 및 출입구 안내'),
    ('755','부천시청','Bucheon City Hall',7,37.5046,126.7636,'경기도 부천시 길주로 지하 202','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'한서연','032-560-7607','시청 방면 출구와 주변 시설 안내'),
    ('754','신중동','Sinjung-dong',8,37.5030,126.7754,'경기도 부천시 길주로 지하 314','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'윤도현','032-560-7608','역사 내 이동 편의시설 안내'),
    ('753','춘의','Chunui',9,37.5037,126.7872,'경기도 부천시 길주로 지하 406','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'강민지','032-560-7609','승강장 및 출입구 이용 안내'),
    ('752','부천종합운동장','Bucheon Stadium',10,37.5054,126.7973,'경기도 부천시 길주로 지하 548','엘리베이터|에스컬레이터|화장실|고객센터','서해선','7호선 ↔ 서해선 환승','임수현','032-560-7610','서해선 환승 및 운동장 방면 출구 안내'),
    ('751','까치울','Kkachiul',11,37.5062,126.8108,'경기도 부천시 길주로 지하 625','엘리베이터|에스컬레이터|화장실|고객센터',NULL,NULL,'조현우','032-560-7611','종착역 이용 및 출입구 안내')
)
INSERT INTO public.railway_station (
    line_id, station_no, name, name_en, sequence_no, latitude, longitude, address,
    facilities, transfer_line, transfer_info, staff_name, contact, usage_info,
    registered_at, inspection_date, facility_status, last_inspection_date,
    next_inspection_due_date, active
)
SELECT l.id, s.station_no, s.name, s.name_en, s.seq, s.lat, s.lon, s.address,
       s.facilities, s.transfer_line, s.transfer_info, s.staff_name, s.phone, s.usage_info,
       DATE '2026-01-15' + (s.seq - 1), DATE '2026-06-01' + (s.seq - 1),
       CASE WHEN s.seq IN (4,9) THEN 'IN_INSPECTION' ELSE 'INSPECTED' END,
       DATE '2026-06-01' + (s.seq - 1), DATE '2028-06-01' + (s.seq - 1), TRUE
FROM line_row l CROSS JOIN station_data s
ON CONFLICT (line_id, station_no) DO UPDATE SET
    name=EXCLUDED.name, name_en=EXCLUDED.name_en, sequence_no=EXCLUDED.sequence_no,
    latitude=EXCLUDED.latitude, longitude=EXCLUDED.longitude, address=EXCLUDED.address,
    facilities=EXCLUDED.facilities, transfer_line=EXCLUDED.transfer_line,
    transfer_info=EXCLUDED.transfer_info, staff_name=EXCLUDED.staff_name,
    contact=EXCLUDED.contact, usage_info=EXCLUDED.usage_info,
    registered_at=EXCLUDED.registered_at, inspection_date=EXCLUDED.inspection_date,
    facility_status=EXCLUDED.facility_status, last_inspection_date=EXCLUDED.last_inspection_date,
    next_inspection_due_date=EXCLUDED.next_inspection_due_date, active=TRUE;

-- 구형 역 목록 API가 사용하는 별도 station 테이블도 동기화
WITH station_data(station_no, name, staff, status) AS (
    VALUES
    ('761','석남','김민수','NORMAL'),('760','산곡','이지은','NORMAL'),
    ('759','부평구청','박서준','NORMAL'),('758','굴포천','최유진','MAINTENANCE'),
    ('757','삼산체육관','정하늘','NORMAL'),('756','상동','오지훈','NORMAL'),
    ('755','부천시청','한서연','NORMAL'),('754','신중동','윤도현','NORMAL'),
    ('753','춘의','강민지','MAINTENANCE'),('752','부천종합운동장','임수현','NORMAL'),
    ('751','까치울','조현우','NORMAL')
)
UPDATE public.station t SET name=s.name, staff=s.staff, status=s.status
FROM station_data s WHERE t.station_no=s.station_no;

WITH station_data(station_no, name, staff, status) AS (
    VALUES
    ('761','석남','김민수','NORMAL'),('760','산곡','이지은','NORMAL'),
    ('759','부평구청','박서준','NORMAL'),('758','굴포천','최유진','MAINTENANCE'),
    ('757','삼산체육관','정하늘','NORMAL'),('756','상동','오지훈','NORMAL'),
    ('755','부천시청','한서연','NORMAL'),('754','신중동','윤도현','NORMAL'),
    ('753','춘의','강민지','MAINTENANCE'),('752','부천종합운동장','임수현','NORMAL'),
    ('751','까치울','조현우','NORMAL')
)
INSERT INTO public.station(name, station_no, staff, status)
SELECT s.name, s.station_no, s.staff, s.status FROM station_data s
WHERE NOT EXISTS (SELECT 1 FROM public.station t WHERE t.station_no=s.station_no);

-- 2. 회원: USER 320명(상태별), WEB_ADMIN 1명, ADMIN 1명
-- 로그인 테스트 비밀번호는 모두 password 입니다.
INSERT INTO public.member
    (login_id, password, name, email, phone, role, status, created_at, updated_at,
     terms_accepted_at, privacy_accepted_at)
SELECT
    'seed_user_' || lpad(g.n::text,4,'0'),
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CASE ((g.n-1) % 20)
        WHEN 0 THEN '김민수' WHEN 1 THEN '이지은' WHEN 2 THEN '박서준' WHEN 3 THEN '최유진'
        WHEN 4 THEN '정하늘' WHEN 5 THEN '오지훈' WHEN 6 THEN '한서연' WHEN 7 THEN '윤도현'
        WHEN 8 THEN '강민지' WHEN 9 THEN '임수현' WHEN 10 THEN '조현우' WHEN 11 THEN '문채원'
        WHEN 12 THEN '장민재' WHEN 13 THEN '신예린' WHEN 14 THEN '백승우' WHEN 15 THEN '서지민'
        WHEN 16 THEN '남도윤' WHEN 17 THEN '유나연' WHEN 18 THEN '권태희' ELSE '송하준' END,
    'seed_user_' || lpad(g.n::text,4,'0') || '@example.com',
    '010-' || lpad((7000 + ((g.n-1)/100))::text,4,'0') || '-' || lpad((1000 + ((g.n-1)%1000))::text,4,'0'),
    'USER',
    CASE WHEN g.n <= 240 THEN 'ACTIVE'
         WHEN g.n <= 260 THEN 'INACTIVE'
         WHEN g.n <= 280 THEN 'BLOCKED'
         WHEN g.n <= 300 THEN 'BANNED'
         ELSE 'WITHDRAWN' END,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1,320) AS g(n)
WHERE NOT EXISTS (SELECT 1 FROM public.member m WHERE m.login_id='seed_user_' || lpad(g.n::text,4,'0'));

INSERT INTO public.member
    (login_id, password, name, email, phone, role, status, created_at, updated_at,
     terms_accepted_at, privacy_accepted_at)
SELECT v.login_id, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
       v.name, v.email, v.phone, v.role, 'ACTIVE', now(), now(), now(), now()
FROM (VALUES
    ('seed_webadmin','웹관리자','seed_webadmin@example.com','010-7000-9001','WEB_ADMIN'),
    ('seed_admin','관리자','seed_admin@example.com','010-7000-9002','ADMIN')
) AS v(login_id,name,email,phone,role)
WHERE NOT EXISTS (SELECT 1 FROM public.member m WHERE m.login_id=v.login_id);

-- 3. 공지사항 120건
INSERT INTO public.notice(title, content, writer, view_count, created_at)
SELECT
    '7호선 운영 공지 ' || lpad(g.n::text,3,'0'),
    CASE (g.n % 6)
      WHEN 0 THEN '석남부터 까치울 구간의 역사 이용 및 시설 안내를 확인해 주세요. 본 데이터는 화면 시연을 위한 예시입니다.'
      WHEN 1 THEN '역사 내 엘리베이터와 에스컬레이터 이용 전 주변 안내 표지와 안전 수칙을 확인해 주세요.'
      WHEN 2 THEN '역사 시설 정기 점검 안내입니다. 점검 시간에는 일부 시설 이용이 제한될 수 있습니다.'
      WHEN 3 THEN '이용객 안전을 위해 승강장 안전선 안쪽에서 열차를 기다려 주세요. 문의는 Q&A로 접수할 수 있습니다.'
      WHEN 4 THEN '7호선 인천·부천 구간의 편의시설 정보는 시연용으로 제공됩니다. 실제 이용 전 현장 안내를 확인해 주세요.'
      ELSE '교통서비스 시스템의 자료가 갱신되었습니다. 등록된 내용은 개발 및 테스트를 위한 더미 정보입니다.' END
      || E'\n\n공지 번호: ' || g.n,
    '운영관리자', (g.n * 13) % 900,
    now() - (g.n || ' days')::interval
FROM generate_series(1,120) AS g(n)
WHERE NOT EXISTS (SELECT 1 FROM public.notice n WHERE n.title='7호선 운영 공지 ' || lpad(g.n::text,3,'0'));

-- 4. 자료실 게시글 120건 (기존 admin 테이블 계정에 연결)
INSERT INTO public.resource(title, content, admin_id, view_count, created_at, updated_at)
SELECT
    '7호선 이용 자료 ' || lpad(g.n::text,3,'0'),
    CASE (g.n % 6)
      WHEN 0 THEN '석남·산곡·부평구청 구간의 역사 이용 및 환승 안내 자료입니다. 본 문서는 시연용 예시입니다.'
      WHEN 1 THEN '엘리베이터, 에스컬레이터, 화장실 등 역사 편의시설을 이용할 때 참고할 기본 안내입니다.'
      WHEN 2 THEN '운행 상태는 정상, 점검, 고장으로 분류해 화면에 표시합니다. 실제 운행 정보가 아닌 테스트 데이터입니다.'
      WHEN 3 THEN '부천시청과 신중동 인근 출입구 및 역사 이용 정보를 확인하기 위한 자료입니다.'
      WHEN 4 THEN '장애인 화장실과 이동 편의시설의 위치를 확인할 수 있도록 구성한 시연용 자료입니다.'
      ELSE '자료실 기능 점검을 위해 작성한 예시 문서입니다. 실제 운영 지침으로 사용하지 마세요.' END
      || E'\n\n자료 번호: ' || g.n,
    (SELECT a.id FROM public.admin a ORDER BY a.id LIMIT 1),
    (g.n * 17) % 1200,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1,120) AS g(n)
WHERE NOT EXISTS (SELECT 1 FROM public.resource r WHERE r.title='7호선 이용 자료 ' || lpad(g.n::text,3,'0'));

-- 5. Q&A 240건: 답변 대기 120건 + 답변 완료 120건
-- 작성자는 모두 활성 일반 회원이며 이름에는 숫자가 없습니다.
INSERT INTO public.qna(title, writer, author_id, inquiry_type, content, answer, view_count, created_at, answered, is_public)
SELECT
    CASE WHEN g.n <= 120 THEN '7호선 문의 답변대기 ' ELSE '7호선 문의 답변완료 ' END || lpad((CASE WHEN g.n <= 120 THEN g.n ELSE g.n-120 END)::text,3,'0'),
    m.name, m.id,
    CASE g.n % 3 WHEN 0 THEN 'FACILITY' WHEN 1 THEN 'OPERATION' ELSE 'ETC' END,
    CASE g.n % 6
      WHEN 0 THEN '석남역에서 까치울 방향 승강장까지 이동할 때 엘리베이터를 이용할 수 있는 위치를 알고 싶습니다.'
      WHEN 1 THEN '부평구청역에서 인천 1호선으로 환승하는 통로와 이동 방향을 안내해 주세요.'
      WHEN 2 THEN '점검 중으로 표시된 역사 시설의 이용 가능 여부와 예상 안내를 확인하고 싶습니다.'
      WHEN 3 THEN '삼산체육관역의 출입구와 주변 시설을 찾는 방법을 알려 주세요.'
      WHEN 4 THEN '역사 편의시설 정보가 현장과 다를 때 수정 요청은 어디로 접수하면 되나요?'
      ELSE '7호선 운행 및 역사 이용과 관련된 안내를 부탁드립니다.' END,
    CASE WHEN g.n <= 120 THEN NULL
         ELSE '문의해 주셔서 감사합니다. 해당 역의 안내와 시설 정보를 확인해 답변드립니다. 본 답변은 시연용 데이터입니다.' END,
    (g.n * 5) % 300,
    now() - (g.n || ' days')::interval,
    (g.n > 120), TRUE
FROM generate_series(1,240) AS g(n)
JOIN public.member m ON m.login_id='seed_user_' || lpad(g.n::text,4,'0') AND m.status='ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM public.qna q
    WHERE q.title = (CASE WHEN g.n <= 120 THEN '7호선 문의 답변대기 ' ELSE '7호선 문의 답변완료 ' END || lpad((CASE WHEN g.n <= 120 THEN g.n ELSE g.n-120 END)::text,3,'0'))
);

-- 6. 열차 360대: 운행/점검/고장 각 120대, 열차 번호 7L-0001~7L-0360
INSERT INTO public.train(line_id, train_no, route, driver, direction, status, active, registered_at, inspection_date)
SELECT
    l.id,
    '7L-' || lpad(g.n::text,4,'0'),
    '석남 ~ 까치울',
    CASE ((g.n-1) % 20)
        WHEN 0 THEN '김민수' WHEN 1 THEN '이지은' WHEN 2 THEN '박서준' WHEN 3 THEN '최유진'
        WHEN 4 THEN '정하늘' WHEN 5 THEN '오지훈' WHEN 6 THEN '한서연' WHEN 7 THEN '윤도현'
        WHEN 8 THEN '강민지' WHEN 9 THEN '임수현' WHEN 10 THEN '조현우' WHEN 11 THEN '문채원'
        WHEN 12 THEN '장민재' WHEN 13 THEN '신예린' WHEN 14 THEN '백승우' WHEN 15 THEN '서지민'
        WHEN 16 THEN '남도윤' WHEN 17 THEN '유나연' WHEN 18 THEN '권태희' ELSE '송하준' END,
    CASE WHEN g.n % 2 = 0 THEN 'UP' ELSE 'DOWN' END,
    CASE WHEN g.n <= 120 THEN 'NORMAL'
         WHEN g.n <= 240 THEN 'MAINTENANCE'
         ELSE 'FAULT' END,
    TRUE,
    current_date - ((g.n % 730)::int),
    CASE WHEN g.n <= 120 THEN current_date + ((g.n % 180)::int)
         WHEN g.n <= 240 THEN current_date - ((g.n % 30)::int)
         ELSE current_date - ((g.n % 90)::int) END
FROM generate_series(1,360) AS g(n)
CROSS JOIN (SELECT id FROM public.railway_line WHERE name='7호선') l
WHERE NOT EXISTS (SELECT 1 FROM public.train t WHERE t.train_no='7L-' || lpad(g.n::text,4,'0'));

COMMIT;

-- 결과 확인: 이번 스크립트가 사용하는 고정 접두어/상태별 수량
SELECT 'member USER' AS data_group, count(*) AS row_count FROM public.member WHERE login_id LIKE 'seed_user_%'
UNION ALL SELECT 'member WEB_ADMIN', count(*) FROM public.member WHERE login_id='seed_webadmin' AND role='WEB_ADMIN'
UNION ALL SELECT 'member ADMIN', count(*) FROM public.member WHERE login_id='seed_admin' AND role='ADMIN'
UNION ALL SELECT '7호선 역', count(*) FROM public.railway_station rs JOIN public.railway_line rl ON rl.id=rs.line_id WHERE rl.name='7호선' AND rs.station_no BETWEEN '751' AND '761'
UNION ALL SELECT '공지사항', count(*) FROM public.notice WHERE title LIKE '7호선 운영 공지 %'
UNION ALL SELECT '자료실', count(*) FROM public.resource WHERE title LIKE '7호선 이용 자료 %'
UNION ALL SELECT 'Q&A 답변대기', count(*) FROM public.qna WHERE title LIKE '7호선 문의 답변대기 %' AND answered=FALSE
UNION ALL SELECT 'Q&A 답변완료', count(*) FROM public.qna WHERE title LIKE '7호선 문의 답변완료 %' AND answered=TRUE
UNION ALL SELECT '열차 NORMAL', count(*) FROM public.train WHERE train_no LIKE '7L-%' AND status='NORMAL'
UNION ALL SELECT '열차 MAINTENANCE', count(*) FROM public.train WHERE train_no LIKE '7L-%' AND status='MAINTENANCE'
UNION ALL SELECT '열차 FAULT', count(*) FROM public.train WHERE train_no LIKE '7L-%' AND status='FAULT';
