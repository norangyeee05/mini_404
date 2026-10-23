-- 통합 데모 데이터 스크립트 (Flyway migration 아님)
--
-- 중요: 첫 번째 SQL이 mini404의 앱 테이블 데이터를 비우고 시퀀스를 1로 초기화한 뒤
--       데모 데이터를 다시 넣습니다. 테이블 구조 자체는 Flyway/JPA가 관리하므로 유지합니다.
--       파일 전체를 실행해야 마지막에 제목/작성자/조회수 무작위화, 등록일 정렬,
--       7L-숫자 형식이 아닌 열차 제거까지 적용됩니다.
-- 전체 실행 후 게시물 수: 공지사항 100건, 자료실 100건, Q&A 100건.
-- public.admin 계정은 보존하지만 member, notice, resource, qna, attachment,
-- station, railway_station, train, transit_service, resource_request, daily_stat는 초기화됩니다.
-- DB 이름이 정확히 mini404가 아니면 첫 단계의 안전 검사에서 중단됩니다.
-- 먼저 백엔드를 실행해 테이블과 관리자 계정을 만들고 Flyway V1/V2를 적용하세요.
-- 이 파일은 새 V1 migration으로 복사하거나 애플리케이션 자동 실행에 연결하지 마세요.
-- 수동 실행할 때만 사용하며, 실행 전 DB 백업을 권장합니다.


-- ============================================================================
-- 1. mini404_demo_reset_seed.sql (전체 데이터 초기화 후 기본 데모 데이터 생성)
-- ============================================================================
-- mini404 시연 데이터 전체 초기화 및 재생성
-- 대상: PostgreSQL public 스키마, mini404 데이터베이스
-- 실행: pgAdmin에서 mini1 서버 > mini404 > Query Tool로 전체 실행
--
-- 이 스크립트는 앱 데이터(member, station, railway_line, railway_station,
-- train, transit_service, notice, resource, qna, resource_request,
-- attachment, daily_stat)를 비우고 시연 데이터를 새로 만듭니다.
-- TRUNCATE ... RESTART IDENTITY CASCADE를 사용해 기존 행과 연결된 데이터를 지우고
-- 해당 테이블들의 자동 증가 번호를 1부터 다시 시작합니다. 테이블 구조와 Flyway 기록,
-- 관리자 로그인 계정(public.admin)은 보존합니다.
-- 관리자 인증 테이블 public.admin은 보존합니다. 자료실 작성자 FK와 관리자
-- 로그인을 유지하기 위해서입니다. attachment 메타데이터는 삭제하지만
-- 서버의 실제 업로드 파일은 삭제하지 않습니다.
--
-- 데모 회원 로그인: seed_user_0001 / password
-- seed_user_0001은 ACTIVE이며 개인 문의 내역 12건을 갖습니다.
-- 다른 seed_user 계정의 비밀번호도 password입니다.
-- 모든 데이터는 합성 시연용입니다. 운영 데이터로 사용하지 마세요.

BEGIN;

DO $$
BEGIN
    IF current_database() <> 'mini404' THEN
        RAISE EXCEPTION '안전 중단: 현재 DB는 % 입니다. mini404에서만 실행하세요.', current_database();
    END IF;
    IF to_regclass('public.admin') IS NULL OR NOT EXISTS (SELECT 1 FROM public.admin) THEN
        RAISE EXCEPTION '안전 중단: public.admin 테이블/관리자 계정이 없습니다. 백엔드를 먼저 실행하세요.';
    END IF;
    IF to_regclass('public.member') IS NULL
       OR to_regclass('public.station') IS NULL
       OR to_regclass('public.railway_line') IS NULL
       OR to_regclass('public.railway_station') IS NULL
       OR to_regclass('public.train') IS NULL
       OR to_regclass('public.transit_service') IS NULL
       OR to_regclass('public.notice') IS NULL
       OR to_regclass('public.resource') IS NULL
       OR to_regclass('public.qna') IS NULL
       OR to_regclass('public.resource_request') IS NULL
       OR to_regclass('public.attachment') IS NULL
       OR to_regclass('public.daily_stat') IS NULL THEN
        RAISE EXCEPTION '안전 중단: 필요한 프로젝트 테이블이 없습니다. 백엔드를 먼저 실행하세요.';
    END IF;
END $$;

-- 모든 대상 테이블의 행과 참조 데이터는 초기화하고 ID 시퀀스를 1부터 재시작합니다.
-- railway_station/train은 railway_line을 참조하므로 CASCADE로 의존 데이터를 함께 비웁니다.
-- public.admin은 대상에 넣지 않아 관리자 로그인은 보존됩니다.
TRUNCATE TABLE
    public.attachment,
    public.resource_request,
    public.qna,
    public.resource,
    public.notice,
    public.train,
    public.transit_service,
    public.daily_stat,
    public.railway_station,
    public.station,
    public.member,
    public.railway_line
RESTART IDENTITY CASCADE;

-- 7호선 인천·부천 구간 11개 역
INSERT INTO public.railway_line (name, display_color, active)
VALUES ('7호선', '#8C914F', TRUE);

WITH line_row AS (
    SELECT id FROM public.railway_line WHERE name = '7호선'
), station_data (
    station_no, name, name_en, sequence_no, latitude, longitude, address,
    facilities, transfer_line, transfer_info, staff_name, contact, usage_info
) AS (
    VALUES
    ('761','석남','Seoknam',1,37.5061,126.6763,'인천광역시 서구 길주로 120','고객센터|장애인 화장실|물품 보관함|자전거 보관소|스마트도서관|엘리베이터|에스컬레이터','인천 2호선','7호선 ↔ 인천 2호선 환승','김민수','032-560-7601','석남 방면 승강장과 환승 통로 이용 안내'),
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
    line_id, station_no, name, name_en, sequence_no, latitude, longitude,
    address, facilities, transfer_line, transfer_info, staff_name, contact,
    usage_info, registered_at, inspection_date, facility_status,
    last_inspection_date, next_inspection_due_date, active
)
SELECT l.id, s.station_no, s.name, s.name_en, s.sequence_no, s.latitude,
       s.longitude, s.address, s.facilities, s.transfer_line, s.transfer_info,
       s.staff_name, s.contact, s.usage_info,
       current_date - (365 + s.sequence_no),
       current_date - (s.sequence_no * 3),
       CASE s.sequence_no WHEN 1 THEN 'INSPECTED' WHEN 2 THEN 'IN_INSPECTION'
            ELSE (ARRAY['INSPECTED','IN_INSPECTION'])[1 + floor(random()*2)::int] END,
       current_date - (s.sequence_no * 3),
       current_date + (30 + s.sequence_no * 5), TRUE
FROM line_row l CROSS JOIN station_data s;

-- 구형 역 API용 station. NORMAL / MAINTENANCE / FAULT가 모두 포함되며 나머지는 무작위입니다.
WITH station_data(station_no, name, staff) AS (
    VALUES
    ('761','석남','김민수'),('760','산곡','이지은'),('759','부평구청','박서준'),
    ('758','굴포천','최유진'),('757','삼산체육관','정하늘'),('756','상동','오지훈'),
    ('755','부천시청','한서연'),('754','신중동','윤도현'),('753','춘의','강민지'),
    ('752','부천종합운동장','임수현'),('751','까치울','조현우')
)
INSERT INTO public.station(name, station_no, staff, status)
SELECT name, station_no, staff,
       CASE station_no WHEN '761' THEN 'NORMAL' WHEN '760' THEN 'MAINTENANCE'
            WHEN '759' THEN 'FAULT'
            ELSE (ARRAY['NORMAL','NORMAL','NORMAL','MAINTENANCE','FAULT'])[1 + floor(random()*5)::int] END
FROM station_data;

-- 일반 회원 320명. 상태는 무작위이며 다섯 상태를 앞의 다섯 계정에서 보장합니다.
INSERT INTO public.member (
    login_id, password, name, email, phone, role, status, created_at, updated_at,
    terms_accepted_at, privacy_accepted_at
)
SELECT
    'seed_user_' || lpad(g.n::text,4,'0'),
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    (ARRAY['김민수','이지은','박서준','최유진','정하늘','오지훈','한서연','윤도현','강민지','임수현',
           '조현우','문채원','장민재','신예린','백승우','서지민','남도윤','유나연','권태희','송하준'])[1 + ((g.n-1)%20)],
    'seed_user_' || lpad(g.n::text,4,'0') || '@example.com',
    '010-' || lpad((7000 + ((g.n-1)/100))::text,4,'0') || '-' || lpad((1000 + ((g.n-1)%1000))::text,4,'0'),
    'USER',
    CASE g.n WHEN 1 THEN 'ACTIVE' WHEN 2 THEN 'BLOCKED' WHEN 3 THEN 'INACTIVE'
             WHEN 4 THEN 'BANNED' WHEN 5 THEN 'WITHDRAWN'
             ELSE (ARRAY['ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE','ACTIVE','BLOCKED','INACTIVE','BANNED','WITHDRAWN'])[1 + floor(random()*10)::int] END,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1,320) AS g(n);

-- 공지 50건. 제목과 본문에 반복 검색어를 두어 검색 결과 비교가 가능합니다.
INSERT INTO public.notice(title, content, writer, view_count, created_at)
SELECT
    (ARRAY['7호선 시설 이용 안내','엘리베이터 점검 안내','역사 편의시설 안내','운영 상태 변경 안내','환승 통로 이용 안내','7호선 시설 이용 안내'])[1 + ((g.n-1)%6)],
    (ARRAY[
      '7호선 시설 이용 안내입니다. 엘리베이터와 역사 편의시설의 위치를 확인해 주세요.',
      '엘리베이터 점검 안내입니다. 점검 중인 시설은 현장 표지를 확인해 주세요.',
      '역사 편의시설 안내입니다. 삼산체육관과 부평구청 등 각 역의 시설 정보를 확인해 주세요.',
      '운영 상태 변경 안내입니다. 정상, 점검, 고장 표시는 시연 데이터 기준입니다.',
      '환승 통로 이용 안내입니다. 부평구청역과 부천종합운동장역 정보를 확인해 주세요.',
      '같은 제목의 시설 안내 검색 시 본문과 등록일을 비교할 수 있는 시연 공지입니다.'
    ])[1 + ((g.n-1)%6)] || E'\n\n프로젝트 화면 시연을 위한 안내입니다.',
    '운영관리자', (g.n * 19) % 901,
    now() - (g.n || ' days')::interval
FROM generate_series(1,50) AS g(n);

-- 자료실 50건. admin 테이블은 위에서 보존한 기존 관리자를 참조합니다.
INSERT INTO public.resource(title, content, admin_id, view_count, created_at, updated_at)
SELECT
    (ARRAY['7호선 역사 편의시설 안내','엘리베이터 이용 자료','교통서비스 운영 상태 안내','환승 안내 자료','7호선 역사 편의시설 안내'])[1 + ((g.n-1)%5)],
    (ARRAY[
      '7호선 역사 편의시설 안내 자료입니다. 엘리베이터, 에스컬레이터와 화장실 위치를 확인할 수 있습니다.',
      '엘리베이터 이용 자료입니다. 교통약자 이동 경로와 승강장 연결 정보를 설명합니다.',
      '교통서비스 운영 상태 안내 자료입니다. 정상, 점검, 고장 상태를 구분합니다.',
      '환승 안내 자료입니다. 부평구청역 및 부천종합운동장역의 환승 정보를 확인해 주세요.',
      '같은 제목의 편의시설 자료를 검색해 본문과 작성일을 비교하기 위한 시연 자료입니다.'
    ])[1 + ((g.n-1)%5)] || E'\n\n실제 운영 지침이 아닌 프로젝트 시연용 자료입니다.',
    (SELECT id FROM public.admin ORDER BY id LIMIT 1),
    (g.n * 23) % 1201,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1,50) AS g(n);

-- Q&A 50건. 모든 문의 유형 및 답변 상태를 보장하고, 작성자는 활성 회원으로 연결합니다.
-- seed_user_0001의 개인 문의 내역 12건을 포함합니다. 제목은 일부러 반복하고
-- 본문은 '문의 유형 ...의 시연 내용입니다'로 구체적으로 작성합니다.
WITH qna_data AS (
    SELECT g.n,
           CASE g.n WHEN 1 THEN 'FACILITY' WHEN 2 THEN 'OPERATION' WHEN 3 THEN 'ETC'
                    ELSE (ARRAY['FACILITY','OPERATION','ETC'])[1 + floor(random()*3)::int] END AS inquiry_type,
           CASE g.n WHEN 1 THEN FALSE WHEN 2 THEN TRUE ELSE random() < 0.5 END AS answered,
           random() AS public_roll
    FROM generate_series(1,50) AS g(n)
)
INSERT INTO public.qna(title, writer, author_id, inquiry_type, content, answer, view_count, created_at, answered, is_public)
SELECT
    CASE q.n % 4 WHEN 0 THEN '엘리베이터 이용 가능 여부 문의'
                 WHEN 1 THEN '역사 시설 상태 확인 문의'
                 WHEN 2 THEN '7호선 운영 안내 문의'
                 ELSE '역사 시설 위치 문의' END,
    m.name, m.id, q.inquiry_type,
    CASE q.inquiry_type
      WHEN 'FACILITY' THEN '시설 문의의 시연 내용입니다. ' || s.name || '역 엘리베이터와 화장실 위치, 교통약자 이동 동선을 안내해 주세요. 같은 시설 검색어가 포함된 문의입니다.'
      WHEN 'OPERATION' THEN '운영 문의의 시연 내용입니다. ' || s.name || '역 시설이 점검 또는 고장으로 표시될 때 이용 가능 여부와 안내 방법을 알려 주세요. 같은 운영 검색어가 포함된 문의입니다.'
      ELSE '기타 문의의 시연 내용입니다. ' || s.name || '역 이용 안내와 회원 서비스 관련 내용을 확인하고 싶습니다. 같은 문의 검색어가 포함된 문의입니다.'
    END,
    CASE WHEN q.answered THEN
      CASE q.inquiry_type
        WHEN 'FACILITY' THEN '시설 문의 답변의 시연 내용입니다. 역사 안내 표지와 시설 위치 정보를 함께 확인해 주세요.'
        WHEN 'OPERATION' THEN '운영 문의 답변의 시연 내용입니다. 상태 정보는 시연 데이터이며 현장 안내를 우선 확인해 주세요.'
        ELSE '기타 문의 답변의 시연 내용입니다. 문의해 주셔서 감사합니다. 담당 안내를 확인해 주세요.'
      END
      ELSE NULL END,
    (q.n * 7) % 301,
    now() - ((q.n % 90) || ' days')::interval,
    q.answered,
    q.public_roll >= 0.2
FROM qna_data q
JOIN LATERAL (
    SELECT m0.id, m0.name
    FROM public.member m0
    WHERE m0.status = 'ACTIVE'
      AND ((q.n <= 12 AND m0.login_id = 'seed_user_0001')
        OR (q.n > 12 AND m0.login_id <> 'seed_user_0001'))
    ORDER BY random()
    LIMIT 1
) m ON TRUE
JOIN LATERAL (
    SELECT name FROM public.station ORDER BY random() LIMIT 1
) s ON TRUE;

-- 자료실 추가 요청 120건. PENDING / COMPLETED 상태 모두 포함하며 나머지는 무작위입니다.
INSERT INTO public.resource_request(member_id, title, content, status, created_at, updated_at)
SELECT m.id,
       (ARRAY['엘리베이터 이용 자료 요청','역사 편의시설 자료 요청','운영 상태 안내 자료 요청','환승 안내 자료 요청'])[1 + ((g.n-1)%4)],
       CASE g.n % 3
         WHEN 0 THEN '엘리베이터와 교통약자 이동 동선 안내 자료를 추가해 주세요. 검색 시 비슷한 요청과 함께 확인할 수 있습니다.'
         WHEN 1 THEN '역사 편의시설 위치와 운영 상태를 설명하는 자료를 자료실에 추가해 주세요.'
         ELSE '7호선 환승 통로와 역사 이용 안내 자료를 요청합니다. 프로젝트 시연용 요청입니다.'
       END,
       CASE g.n WHEN 1 THEN 'PENDING' WHEN 2 THEN 'COMPLETED'
                ELSE (ARRAY['PENDING','COMPLETED'])[1 + floor(random()*2)::int] END,
       now() - (g.n || ' days')::interval,
       now() - (g.n || ' days')::interval
FROM generate_series(1,120) AS g(n)
JOIN LATERAL (
    SELECT id FROM public.member WHERE status='ACTIVE' ORDER BY random() LIMIT 1
) m ON TRUE;

-- 역사 서비스 120건. NORMAL / MAINTENANCE / FAULT를 보장하고 무작위 배정합니다.
INSERT INTO public.transit_service(name, status)
SELECT
    (ARRAY['엘리베이터','에스컬레이터','장애인 화장실','고객센터','물품 보관함','자전거 보관소'])[1 + ((g.n-1)%6)]
    || ' - ' || (ARRAY['석남','산곡','부평구청','굴포천','삼산체육관','상동','부천시청','신중동','춘의','부천종합운동장','까치울'])[1 + ((g.n-1)%11)] || '역',
    CASE g.n WHEN 1 THEN 'NORMAL' WHEN 2 THEN 'MAINTENANCE' WHEN 3 THEN 'FAULT'
             ELSE (ARRAY['NORMAL','NORMAL','NORMAL','NORMAL','MAINTENANCE','FAULT'])[1 + floor(random()*6)::int] END
FROM generate_series(1,120) AS g(n);

-- 열차 360대. 세 운영 상태를 보장하고 나머지는 무작위로 섞습니다.
INSERT INTO public.train(line_id, train_no, route, driver, direction, status, active, registered_at, inspection_date)
SELECT l.id,
       '7L-' || lpad(g.n::text,4,'0'),
       CASE WHEN g.n%2=0 THEN '석남 ↔ 까치울' ELSE '까치울 ↔ 석남' END,
       (ARRAY['김민수','이지은','박서준','최유진','정하늘','오지훈','한서연','윤도현','강민지','임수현'])[1 + ((g.n-1)%10)],
       CASE WHEN random() < 0.5 THEN 'UP' ELSE 'DOWN' END,
       CASE g.n WHEN 1 THEN 'NORMAL' WHEN 2 THEN 'MAINTENANCE' WHEN 3 THEN 'FAULT'
                ELSE (ARRAY['NORMAL','NORMAL','NORMAL','NORMAL','MAINTENANCE','FAULT'])[1 + floor(random()*6)::int] END,
       random() > 0.08,
       current_date - (g.n % 730),
       current_date - (g.n % 180)
FROM generate_series(1,360) AS g(n)
CROSS JOIN (SELECT id FROM public.railway_line WHERE name='7호선') l;

-- 최근 120일의 시연용 대시보드 스냅샷
INSERT INTO public.daily_stat(stat_date, member_count, notice_count, qna_pending_count,
                              service_total, normal_count, maintenance_count, fault_count)
SELECT current_date - (g.n - 1),
       (SELECT count(*) FROM public.member),
       (SELECT count(*) FROM public.notice),
       (SELECT count(*) FROM public.qna WHERE answered=FALSE),
       (SELECT count(*) FROM public.transit_service),
       (SELECT count(*) FROM public.transit_service WHERE status='NORMAL'),
       (SELECT count(*) FROM public.transit_service WHERE status='MAINTENANCE'),
       (SELECT count(*) FROM public.transit_service WHERE status='FAULT')
FROM generate_series(1,120) AS g(n);

-- 필수 값 누락 시 전체 트랜잭션을 롤백합니다.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM (VALUES ('ACTIVE'),('BLOCKED'),('INACTIVE'),('BANNED'),('WITHDRAWN')) v(s)
               WHERE NOT EXISTS (SELECT 1 FROM public.member m WHERE m.status=v.s)) THEN
        RAISE EXCEPTION '회원 상태 누락: member';
    END IF;
    IF EXISTS (SELECT 1 FROM (VALUES ('NORMAL'),('MAINTENANCE'),('FAULT')) v(s)
               WHERE NOT EXISTS (SELECT 1 FROM public.station x WHERE x.status=v.s)
                  OR NOT EXISTS (SELECT 1 FROM public.train x WHERE x.status=v.s)
                  OR NOT EXISTS (SELECT 1 FROM public.transit_service x WHERE x.status=v.s)) THEN
        RAISE EXCEPTION '운영 상태 누락: station/train/transit_service';
    END IF;
    IF EXISTS (SELECT 1 FROM (VALUES ('FACILITY'),('OPERATION'),('ETC')) v(t)
               WHERE NOT EXISTS (SELECT 1 FROM public.qna q WHERE q.inquiry_type=v.t)) THEN
        RAISE EXCEPTION '문의 유형 누락: qna';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM public.qna WHERE answered=FALSE)
       OR NOT EXISTS (SELECT 1 FROM public.qna WHERE answered=TRUE)
       OR NOT EXISTS (SELECT 1 FROM public.resource_request WHERE status='PENDING')
       OR NOT EXISTS (SELECT 1 FROM public.resource_request WHERE status='COMPLETED') THEN
        RAISE EXCEPTION 'Q&A 또는 자료 요청 상태 누락';
    END IF;
    IF (SELECT count(*) FROM public.qna WHERE author_id=(SELECT id FROM public.member WHERE login_id='seed_user_0001')) < 12 THEN
        RAISE EXCEPTION 'seed_user_0001의 개인 Q&A 내역이 충분하지 않습니다.';
    END IF;
END $$;

COMMIT;

-- 생성 결과 요약
SELECT 'member status' AS category, status AS value, count(*) AS row_count
FROM public.member GROUP BY status
UNION ALL
SELECT 'train status', status, count(*) FROM public.train GROUP BY status
UNION ALL
SELECT 'service status', status, count(*) FROM public.transit_service GROUP BY status
UNION ALL
SELECT 'Q&A type', inquiry_type, count(*) FROM public.qna GROUP BY inquiry_type
UNION ALL
SELECT 'Q&A answer', CASE WHEN answered THEN 'ANSWERED' ELSE 'WAITING' END, count(*) FROM public.qna GROUP BY answered
UNION ALL
SELECT 'my Q&A seed_user_0001', 'TOTAL', count(*) FROM public.qna
 WHERE author_id=(SELECT id FROM public.member WHERE login_id='seed_user_0001')
UNION ALL
SELECT 'station', '7호선', count(*) FROM public.railway_station
 WHERE line_id=(SELECT id FROM public.railway_line WHERE name='7호선')
ORDER BY category, value;



-- ============================================================================
-- 2. line7_station_data.sql (7호선 역 정보 upsert)
-- ============================================================================
-- 인천 7호선 구간 역 데이터
-- 기준: 업로드된 화면 자료의 역 목록 (761 석남 ~ 751 까치울)
-- 대상 DB: backend 프로젝트의 PostgreSQL / public 스키마
-- 이 스크립트는 기존 데이터를 삭제하지 않습니다.
-- railway_station은 (line_id, station_no) 기준으로 갱신하고,
-- 별도 station 테이블은 station_no가 없는 경우에만 추가합니다.
-- 좌표/주소/시설 정보는 프로젝트 시연용으로 일부 보완한 값이며,
-- 실제 운영 데이터로 사용하기 전 공식 자료와 대조하세요.

BEGIN;

-- 1) 7호선 노선 등록 또는 갱신
INSERT INTO public.railway_line (name, display_color, active)
VALUES ('7호선', '#8C914F', TRUE)
ON CONFLICT (name)
DO UPDATE SET
    display_color = EXCLUDED.display_color,
    active = EXCLUDED.active;

-- 2) 인천·부천 구간 11개 역
-- sequence_no: 석남 -> 까치울 방향
WITH line_row AS (
    SELECT id
    FROM public.railway_line
    WHERE name = '7호선'
),
station_data (
    station_no, name, name_en, sequence_no,
    latitude, longitude, address, facilities, transfer_line, transfer_info
) AS (
    VALUES
    ('761', '석남', 'Seoknam', 1, 37.5061, 126.6763,
     '인천광역시 서구 길주로 120',
     '고객센터|장애인 화장실|물품 보관함|자전거 보관소|스마트도서관|꽃자판기|엘리베이터|에스컬레이터',
     '인천 2호선', '7호선 ↔ 인천 2호선 환승'),
    ('760', '산곡', 'Sangok', 2, 37.5086, 126.7030,
     '인천광역시 부평구 길주로 지하 389',
     '엘리베이터|에스컬레이터|장애인 화장실|고객센터',
     NULL, NULL),
    ('759', '부평구청', 'Bupyeong-gu Office', 3, 37.5083, 126.7219,
     '인천광역시 부평구 길주로 지하 527',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     '인천 1호선', '7호선 ↔ 인천 1호선 환승'),
    ('758', '굴포천', 'Gulpocheon', 4, 37.5068, 126.7313,
     '인천광역시 부평구 길주로 지하 599',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('757', '삼산체육관', 'Samsan Gymnasium', 5, 37.5069, 126.7427,
     '인천광역시 부평구 길주로 지하 713',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('756', '상동', 'Sang-dong', 6, 37.5058, 126.7531,
     '경기도 부천시 길주로 지하 104',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('755', '부천시청', 'Bucheon City Hall', 7, 37.5046, 126.7636,
     '경기도 부천시 길주로 지하 202',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('754', '신중동', 'Sinjung-dong', 8, 37.5030, 126.7754,
     '경기도 부천시 길주로 지하 314',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('753', '춘의', 'Chunui', 9, 37.5037, 126.7872,
     '경기도 부천시 길주로 지하 406',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL),
    ('752', '부천종합운동장', 'Bucheon Stadium', 10, 37.5054, 126.7973,
     '경기도 부천시 길주로 지하 548',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     '서해선', '7호선 ↔ 서해선 환승'),
    ('751', '까치울', 'Kkachiul', 11, 37.5062, 126.8108,
     '경기도 부천시 길주로 지하 625',
     '엘리베이터|에스컬레이터|화장실|고객센터',
     NULL, NULL)
)
INSERT INTO public.railway_station (
    line_id, station_no, name, name_en, sequence_no,
    latitude, longitude, address, facilities, transfer_line, transfer_info, active
)
SELECT
    l.id, s.station_no, s.name, s.name_en, s.sequence_no,
    s.latitude, s.longitude, s.address, s.facilities, s.transfer_line, s.transfer_info, TRUE
FROM line_row l
CROSS JOIN station_data s
ON CONFLICT (line_id, station_no)
DO UPDATE SET
    name = EXCLUDED.name,
    name_en = EXCLUDED.name_en,
    sequence_no = EXCLUDED.sequence_no,
    latitude = EXCLUDED.latitude,
    longitude = EXCLUDED.longitude,
    address = EXCLUDED.address,
    facilities = EXCLUDED.facilities,
    transfer_line = EXCLUDED.transfer_line,
    transfer_info = EXCLUDED.transfer_info,
    active = TRUE;

-- 3) 프로젝트의 별도 station 테이블도 채움
-- 이 테이블은 railway_station과 별개이며, 담당 직원/운영 상태를 관리하는 용도입니다.
-- 직원명은 모두 시연용 가상 데이터입니다.
WITH station_data (station_no, name, staff, status) AS (
    VALUES
    ('761', '석남', '김민수', 'NORMAL'),
    ('760', '산곡', '이서연', 'NORMAL'),
    ('759', '부평구청', '박지훈', 'NORMAL'),
    ('758', '굴포천', '최유진', 'MAINTENANCE'),
    ('757', '삼산체육관', '정하늘', 'NORMAL'),
    ('756', '상동', '강도윤', 'NORMAL'),
    ('755', '부천시청', '윤서준', 'NORMAL'),
    ('754', '신중동', '한지우', 'NORMAL'),
    ('753', '춘의', '오지호', 'FAULT'),
    ('752', '부천종합운동장', '임수빈', 'NORMAL'),
    ('751', '까치울', '조현우', 'NORMAL')
)
INSERT INTO public.station (name, station_no, staff, status)
SELECT s.name, s.station_no, s.staff, s.status
FROM station_data s
WHERE NOT EXISTS (
    SELECT 1
    FROM public.station existing
    WHERE existing.station_no = s.station_no
);

COMMIT;

-- 4) 등록 결과 확인
SELECT
    rl.name AS line_name,
    rs.station_no,
    rs.name AS station_name,
    rs.sequence_no,
    rs.transfer_line,
    rs.active
FROM public.railway_station rs
JOIN public.railway_line rl ON rl.id = rs.line_id
WHERE rl.name = '7호선'
ORDER BY rs.sequence_no;

SELECT station_no, name, staff, status
FROM public.station
WHERE station_no IN ('751','752','753','754','755','756','757','758','759','760','761')
ORDER BY station_no DESC;



-- ============================================================================
-- 3. backend_dummy_data.sql (기본 더미 데이터 추가)
-- ============================================================================
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

-- 2. 공지사항: 30건
INSERT INTO public.notice (title, content, writer, view_count, created_at)
SELECT
    '더미 공지사항 ' || lpad(g.n::text, 3, '0'),
    CASE (g.n - 1) % 5
        WHEN 0 THEN '인천 7호선 교통서비스 관리 시스템 이용 안내입니다. 서비스 상태와 공지사항을 확인해 주세요.'
        WHEN 1 THEN '역사 내 편의시설 이용 시 안전 수칙을 준수해 주시기 바랍니다. 불편 사항은 Q&A로 접수할 수 있습니다.'
        WHEN 2 THEN '시스템 점검 및 데이터 갱신 안내입니다. 화면에 표시되는 상태는 시연용 데이터일 수 있습니다.'
        WHEN 3 THEN '교통서비스 이용 관련 자주 묻는 질문과 답변을 정리했습니다. 자세한 사항은 관리자에게 문의해 주세요.'
        ELSE '관리자 페이지의 서비스 현황은 등록된 데이터 기준으로 집계됩니다. 이상 정보는 담당자에게 알려 주세요.' END
        || E'\n\n프로젝트 화면 시연을 위한 안내입니다.',
    '관리자',
    (g.n * 7) % 350,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 30) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.notice n
    WHERE n.title = '더미 공지사항 ' || lpad(g.n::text, 3, '0')
);

-- 3. 자료실: 30건 (기존 관리자 계정과 연결)
INSERT INTO public.resource (title, content, admin_id, view_count, created_at, updated_at)
SELECT
    '7호선 자료실 더미 ' || lpad(g.n::text, 3, '0'),
    CASE (g.n - 1) % 5
        WHEN 0 THEN '인천·부천 구간 교통서비스 운영 참고 자료입니다. 실제 운영 문서가 아닌 프로젝트 시연용 자료입니다.'
        WHEN 1 THEN '역사 편의시설 관리 시 확인할 항목과 이용자 안내 내용을 정리한 자료입니다.'
        WHEN 2 THEN '교통서비스 상태 분류 예시: NORMAL은 정상, MAINTENANCE는 점검, FAULT는 고장을 의미합니다.'
        WHEN 3 THEN '관리자 업무 참고용 자료입니다. 실제 시설 정보 및 운영 절차는 담당 기관의 공식 자료를 확인해야 합니다.'
        ELSE '시스템 테스트를 위해 생성한 자료입니다. 실제 공사 공지나 공식 운영 지침으로 사용하지 마세요.' END
        || E'\n\n프로젝트 시연용 자료입니다.',
    (SELECT a.id FROM public.admin a ORDER BY a.id LIMIT 1),
    (g.n * 11) % 500,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 30) AS g(n)
WHERE NOT EXISTS (
    SELECT 1 FROM public.resource r
    WHERE r.title = '7호선 자료실 더미 ' || lpad(g.n::text, 3, '0')
);

-- 4. Q&A: 25건. 작성 회원과 author_id를 연결하고 답변 상태를 섞습니다.
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
FROM generate_series(1, 25) AS g(n)
JOIN LATERAL (
    SELECT id, name FROM public.member
    WHERE status = 'ACTIVE'
    ORDER BY random()
    LIMIT 1
) m ON TRUE
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
SELECT 'train(7L-숫자)', count(*) FROM public.train WHERE train_no ~ '^7L-[0-9]+$'
UNION ALL
SELECT 'daily_stat', count(*) FROM public.daily_stat WHERE stat_date >= current_date - 119;



-- ============================================================================
-- 4. mini_더미데이터.sql (추가 시연 데이터 upsert)
-- ============================================================================
-- ============================================================
-- mini 프로젝트 시연용 더미 데이터
-- PostgreSQL / public 스키마 / pgAdmin Query Tool에서 실행
--
-- 생성량
--   7호선 역: 석남(761) ~ 까치울(751), 총 11개 역
--   회원: 일반 USER 320명 + WEB_ADMIN 1명 + ADMIN 1명
--   공지사항: 20건 / 자료실: 20건
--   Q&A: 25건 (답변 대기)
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

-- 3. 공지사항 20건
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
      || E'\n\n프로젝트 화면 시연을 위한 안내입니다.',
    '운영관리자', (g.n * 13) % 900,
    now() - (g.n || ' days')::interval
FROM generate_series(1,20) AS g(n)
WHERE NOT EXISTS (SELECT 1 FROM public.notice n WHERE n.title='7호선 운영 공지 ' || lpad(g.n::text,3,'0'));

-- 4. 자료실 게시글 20건 (기존 admin 테이블 계정에 연결)
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
      || E'\n\n프로젝트 시연용 자료입니다.',
    (SELECT a.id FROM public.admin a ORDER BY a.id LIMIT 1),
    (g.n * 17) % 1200,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1,20) AS g(n)
WHERE NOT EXISTS (SELECT 1 FROM public.resource r WHERE r.title='7호선 이용 자료 ' || lpad(g.n::text,3,'0'));

-- 5. Q&A 25건: 답변 대기
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
FROM generate_series(1,25) AS g(n)
JOIN LATERAL (
    SELECT id, name FROM public.member
    WHERE status = 'ACTIVE'
    ORDER BY random()
    LIMIT 1
) m ON TRUE
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
UNION ALL SELECT '공지사항 전체', count(*) FROM public.notice
UNION ALL SELECT '자료실 전체', count(*) FROM public.resource
UNION ALL SELECT 'Q&A 답변대기', count(*) FROM public.qna WHERE answered=FALSE
UNION ALL SELECT 'Q&A 답변완료', count(*) FROM public.qna WHERE answered=TRUE
UNION ALL SELECT '열차 NORMAL', count(*) FROM public.train WHERE status='NORMAL'
UNION ALL SELECT '열차 MAINTENANCE', count(*) FROM public.train WHERE status='MAINTENANCE'
UNION ALL SELECT '열차 FAULT', count(*) FROM public.train WHERE status='FAULT';



-- SQL seed inserts bypass JPA callbacks; initialize timestamps for demo rows.
UPDATE public.station SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;
UPDATE public.railway_station SET updated_at = CURRENT_TIMESTAMP WHERE updated_at IS NULL;

-- 최종 시연 데이터 정리
-- ID는 TRUNCATE RESTART IDENTITY 이후 생성된 순번을 유지하고,
-- 제목/작성자/조회수만 무작위로 섞습니다. Q&A 작성자는 실제 회원과 연결된 이름을 유지합니다.
BEGIN;

-- 요구된 형식(7L-숫자)이 아닌 열차 번호는 제거합니다. 예: 7L-DUMMY-0001 삭제.
DELETE FROM public.train
WHERE train_no IS NULL OR train_no !~ '^7L-[0-9]+$';
SELECT setval(
    pg_get_serial_sequence('public.train', 'id'),
    COALESCE((SELECT max(id) FROM public.train), 0) + 1,
    FALSE
);

UPDATE public.notice
SET title = (ARRAY[
        '7호선 운영 안내', '역사 시설 이용 안내', '엘리베이터 점검 안내',
        '교통서비스 변경 안내', '환승 통로 안내', '역사 편의시설 안내'
    ])[1 + floor(random() * 6)::int],
    writer = (ARRAY[
        '운영관리자', '교통서비스팀', '역사관리팀', '고객지원팀', '시설관리자'
    ])[1 + floor(random() * 5)::int],
    view_count = floor(random() * 1201)::int;

UPDATE public.resource AS r
SET title = (ARRAY[
        '7호선 역사 이용 자료', '교통서비스 안내 자료', '편의시설 정보 자료',
        '환승 및 이동 안내', '시설 점검 참고 자료', '열차 운행 참고 자료'
    ])[1 + floor(random() * 6)::int],
    admin_id = COALESCE(
        (SELECT a.id FROM public.admin a
         ORDER BY md5(a.id::text || r.id::text || random()::text)
         LIMIT 1), r.admin_id
    ),
    view_count = floor(random() * 1201)::int;

-- seed_user_0001의 개인 문의 12건은 유지하고, 나머지 문의 작성자는 활성 회원 중 무작위 배정합니다.
WITH seed_owner AS (
    SELECT id FROM public.member WHERE login_id = 'seed_user_0001'
), randomized_authors AS (
    SELECT q.id AS qna_id, m.id AS member_id, m.name
    FROM public.qna q
    CROSS JOIN LATERAL (
        SELECT candidate.id, candidate.name
        FROM public.member candidate
        WHERE candidate.status = 'ACTIVE'
          AND candidate.login_id <> 'seed_user_0001'
        ORDER BY md5(candidate.id::text || q.id::text || CURRENT_TIMESTAMP::text)
        LIMIT 1
    ) m
    WHERE q.author_id IS DISTINCT FROM (SELECT id FROM seed_owner)
)
UPDATE public.qna q
SET author_id = a.member_id, writer = a.name
FROM randomized_authors a
WHERE a.qna_id = q.id;

UPDATE public.qna
SET title = (ARRAY[
        '역사 시설 이용 문의', '엘리베이터 위치 문의', '7호선 운행 안내 문의',
        '환승 통로 이용 문의', '교통서비스 상태 문의', '역사 편의시설 문의'
    ])[1 + floor(random() * 6)::int],
    view_count = floor(random() * 501)::int;

-- 일부 공지/자료/Q&A에는 실제 저장 파일과 연결되는 시연용 첨부파일을 추가합니다.
-- 파일은 mini-데이터베이스-SQL/demo-uploads의 안내에 따라 backend 업로드 볼륨에 복사해야 합니다.
INSERT INTO public.attachment
    (target_type, target_id, original_name, stored_name, content_type, file_size, created_at)
VALUES
    ('NOTICE', 1, '역사_편의시설_안내.txt', 'demo_facility_guide.txt', 'text/plain', 381, CURRENT_TIMESTAMP),
    ('NOTICE', 2, '환승_열차_이용안내.txt', 'demo_transfer_guide.txt', 'text/plain', 315, CURRENT_TIMESTAMP),
    ('RESOURCE', 1, '역사_편의시설_안내.txt', 'demo_facility_guide.txt', 'text/plain', 381, CURRENT_TIMESTAMP),
    ('RESOURCE', 2, '환승_열차_이용안내.txt', 'demo_transfer_guide.txt', 'text/plain', 315, CURRENT_TIMESTAMP),
    ('RESOURCE', 3, '문의_참고자료.txt', 'demo_qna_reference.txt', 'text/plain', 283, CURRENT_TIMESTAMP),
    ('QNA', 1, '문의_참고자료.txt', 'demo_qna_reference.txt', 'text/plain', 283, CURRENT_TIMESTAMP),
    ('QNA', 2, '환승_열차_이용안내.txt', 'demo_transfer_guide.txt', 'text/plain', 315, CURRENT_TIMESTAMP);

-- 목록의 번호(ID)가 높을수록 등록일도 최근이 되도록 분 단위로 정렬합니다.
WITH ranked AS (
    SELECT id, row_number() OVER (ORDER BY id) AS row_no, count(*) OVER () AS total_rows
    FROM public.notice
)
UPDATE public.notice n
SET created_at = CURRENT_TIMESTAMP - ((r.total_rows - r.row_no)::double precision * INTERVAL '1 minute')
FROM ranked r WHERE r.id = n.id;

WITH ranked AS (
    SELECT id, row_number() OVER (ORDER BY id) AS row_no, count(*) OVER () AS total_rows
    FROM public.resource
)
UPDATE public.resource r
SET created_at = CURRENT_TIMESTAMP - ((x.total_rows - x.row_no)::double precision * INTERVAL '1 minute'),
    updated_at = CURRENT_TIMESTAMP - ((x.total_rows - x.row_no)::double precision * INTERVAL '1 minute')
FROM ranked x WHERE x.id = r.id;

WITH ranked AS (
    SELECT id, row_number() OVER (ORDER BY id) AS row_no, count(*) OVER () AS total_rows
    FROM public.qna
)
UPDATE public.qna q
SET created_at = CURRENT_TIMESTAMP - ((x.total_rows - x.row_no)::double precision * INTERVAL '1 minute')
FROM ranked x WHERE x.id = q.id;

COMMIT;

-- 최종 확인: 정렬 방향 및 비정상 열차 번호가 없는지 점검합니다.
SELECT '열차 번호 형식 오류' AS check_item, count(*) AS row_count
FROM public.train WHERE train_no IS NULL OR train_no !~ '^7L-[0-9]+$'
UNION ALL
SELECT '공지 등록일 역순 위반', count(*) FROM (
    SELECT created_at, lag(created_at) OVER (ORDER BY id) AS previous_created_at
    FROM public.notice
) x WHERE previous_created_at IS NOT NULL AND created_at <= previous_created_at
UNION ALL
SELECT '자료실 등록일 역순 위반', count(*) FROM (
    SELECT created_at, lag(created_at) OVER (ORDER BY id) AS previous_created_at
    FROM public.resource
) x WHERE previous_created_at IS NOT NULL AND created_at <= previous_created_at
UNION ALL
SELECT 'Q&A 등록일 역순 위반', count(*) FROM (
    SELECT created_at, lag(created_at) OVER (ORDER BY id) AS previous_created_at
    FROM public.qna
) x WHERE previous_created_at IS NOT NULL AND created_at <= previous_created_at;
