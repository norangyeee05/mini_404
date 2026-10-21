-- 기존 시연용 더미 데이터 상태를 랜덤하게 섞는 스크립트 (PostgreSQL)
-- 대상: seed_user_* 회원, 7L-0001~7L-0360 열차, 기존 7호선 더미 Q&A
-- 자료실 요청은 120건을 보장하고 상태를 섞습니다.
-- 실제/관리자 계정, 역·공지·자료 게시물은 수정하거나 삭제하지 않습니다.

BEGIN;

-- 1) 기존 더미 일반 회원 상태를 랜덤 변경
-- seed_user_0001은 로그인 시연이 가능하도록 ACTIVE로 고정하고 나머지를 섞습니다.
WITH draws AS MATERIALIZED (
    SELECT id, login_id, random() AS roll
    FROM public.member
    WHERE login_id ~ '^seed_user_[0-9]{4}$'
)
UPDATE public.member AS m
SET status = CASE
    WHEN d.login_id = 'seed_user_0001' THEN 'ACTIVE'
    WHEN d.roll < 0.70 THEN 'ACTIVE'
    WHEN d.roll < 0.80 THEN 'INACTIVE'
    WHEN d.roll < 0.90 THEN 'BLOCKED'
    WHEN d.roll < 0.96 THEN 'BANNED'
    ELSE 'WITHDRAWN'
END,
updated_at = now()
FROM draws AS d
WHERE m.id = d.id;

-- 2) 기존 7호선 열차 더미 운행 상태를 랜덤 변경
-- 분포 예시: NORMAL 약 75%, MAINTENANCE 약 15%, FAULT 약 10%.
WITH draws AS MATERIALIZED (
    SELECT id, random() AS roll
    FROM public.train
    WHERE train_no BETWEEN '7L-0001' AND '7L-0360'
)
UPDATE public.train AS t
SET status = CASE
    WHEN d.roll < 0.75 THEN 'NORMAL'
    WHEN d.roll < 0.90 THEN 'MAINTENANCE'
    ELSE 'FAULT'
END,
updated_at = now()
FROM draws AS d
WHERE t.id = d.id;

-- 3) 기존 시연용 Q&A 답변 상태를 랜덤 변경
-- 제목의 기존 '답변대기/답변완료' 표시는 상태와 맞지 않을 수 있어 중립 제목으로 바꿉니다.
WITH draws AS MATERIALIZED (
    SELECT id, random() AS roll
    FROM public.qna
    WHERE title LIKE '7호선 문의 답변대기 %'
       OR title LIKE '7호선 문의 답변완료 %'
)
UPDATE public.qna AS q
SET title = regexp_replace(q.title, '^7호선 문의 답변(대기|완료) ', '7호선 문의 '),
    answered = (d.roll >= 0.50),
    answer = CASE
        WHEN d.roll < 0.50 THEN NULL
        ELSE COALESCE(NULLIF(q.answer, ''),
            '문의해 주셔서 감사합니다. 담당자가 확인한 안내 답변입니다. (시연용)')
    END
FROM draws AS d
WHERE q.id = d.id;

-- 4) 자료실 추가 요청 더미가 없으면 120건 생성
-- 계정은 seed_user_0001~seed_user_0120을 연결합니다.
INSERT INTO public.resource_request
    (member_id, title, content, status, created_at, updated_at)
SELECT
    m.id,
    '7호선 자료 요청 ' || lpad(g.n::text, 3, '0'),
    '7호선 역사 시설 이용과 관련된 안내 자료를 추가해 주세요. 이용 방법과 편의시설 정보를 확인하고 싶습니다. (시연용 요청)',
    CASE WHEN random() < 0.55 THEN 'PENDING' ELSE 'COMPLETED' END,
    now() - (g.n || ' days')::interval,
    now() - (g.n || ' days')::interval
FROM generate_series(1, 120) AS g(n)
JOIN public.member AS m
  ON m.login_id = 'seed_user_' || lpad(g.n::text, 4, '0')
WHERE NOT EXISTS (
    SELECT 1
    FROM public.resource_request AS rr
    WHERE rr.title = '7호선 자료 요청 ' || lpad(g.n::text, 3, '0')
);

-- 기존에 만들어져 있던 같은 시연용 요청의 처리 상태도 다시 섞습니다.
WITH draws AS MATERIALIZED (
    SELECT id, random() AS roll
    FROM public.resource_request
    WHERE title ~ '^7호선 자료 요청 [0-9]{3}$'
)
UPDATE public.resource_request AS rr
SET status = CASE WHEN d.roll < 0.55 THEN 'PENDING' ELSE 'COMPLETED' END,
    updated_at = now()
FROM draws AS d
WHERE rr.id = d.id;

COMMIT;

-- 변경 결과 확인
SELECT '회원 상태' AS group_name, status AS value, count(*) AS count
FROM public.member
WHERE login_id ~ '^seed_user_[0-9]{4}$'
GROUP BY status
UNION ALL
SELECT '열차 상태', status, count(*)
FROM public.train
WHERE train_no BETWEEN '7L-0001' AND '7L-0360'
GROUP BY status
UNION ALL
SELECT 'Q&A 상태', CASE WHEN answered THEN 'ANSWERED' ELSE 'WAITING' END, count(*)
FROM public.qna
WHERE title LIKE '7호선 문의 %'
GROUP BY answered
UNION ALL
SELECT '자료실 요청 상태', status, count(*)
FROM public.resource_request
WHERE title ~ '^7호선 자료 요청 [0-9]{3}$'
GROUP BY status
ORDER BY group_name, value;

select * from admin;

INSERT INTO public.transit_service (name, status)
SELECT
    '7호선 교통서비스 시연 ' || lpad(g.n::text, 3, '0'),
    CASE
        WHEN g.n % 10 = 0 THEN 'FAULT'
        WHEN g.n % 4 = 0 THEN 'MAINTENANCE'
        ELSE 'NORMAL'
    END
FROM generate_series(1, 120) AS g(n)
WHERE NOT EXISTS (
    SELECT 1
    FROM public.transit_service ts
    WHERE ts.name = '7호선 교통서비스 시연 ' || lpad(g.n::text, 3, '0')
);
