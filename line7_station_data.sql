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
