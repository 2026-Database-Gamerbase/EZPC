#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
EZPC 대용량 더미 데이터 생성 스크립트
=====================================================
대상 DB  : MariaDB (HeidiSQL)
목표     : use_log / charge / food_order 각 ~100만 건
사전 실행: db 생성 쿼리.sql 을 먼저 HeidiSQL에서 실행해 테이블 생성
필요 패키지: pip install pymysql faker numpy
=====================================================

[스키마 분석 결과 - 발견된 이슈]
1. (치명) db 데이터 삽입 쿼리.sql 105번째 줄:
       ('user010', 'pw010', '서지안', 720, 'ruby' 920000, 'user')
   'ruby' 와 920000 사이에 쉼표 누락 → INSERT 전체 실패
   수정: 'ruby', 920000 으로 변경 필요

2. (요구사항 불일치) 기존 더미 데이터: owner 5명
   → 이 스크립트에서 요구사항대로 owner 정확히 4명만 생성

3. (코드 버그) Charge.java, Order.java 에 payment_rate 필드 없음
   → DB 컬럼은 존재하나 Java 모델 누락 (DDL 문제 아님)

4. food_order PK = (order_id, food_name): 한 주문에 동일 food_name 행 중복 불가
   → food_quantity 로 동일 음식 여러 개 주문 표현 가능 (설계 의도대로 올바름)

[정합성 구현 로직]
  pc_member 생성 → use_log 1M (세션) →
  charge 1M (세션당 1건, charged_at=login_time) →
  food_order ~1M rows (65% 세션, login~logout 사이 ordered_at) →
  total_payment_amount 집계 → grade_type 갱신 →
  review (방문 회원 중 일부) → customer (활성 세션 스냅샷)
"""

import random
import sys
from datetime import datetime, timedelta

import numpy as np
import pymysql
from faker import Faker

# ══════════════════════════════════════════════
#  0. 설정  ← DB 정보 수정 후 실행
# ══════════════════════════════════════════════
DB_CONFIG = {
    'host'      : 'localhost',
    'port'      : 3306,
    'user'      : 'root',       # ← MariaDB 계정
    'password'  : 'Zxcvbn4657@',           # ← 비밀번호
    'database'  : 'EZPC',
    'charset'   : 'utf8mb4',
    'autocommit': False,
}

BATCH_SIZE       = 5_000    # executemany 한 번에 삽입할 행 수
CHUNK_SIZE       = 50_000   # 한 청크당 세션 수 (20청크 × 50,000 = 1,000,000)
TOTAL_SESSIONS   = 1_000_000
MEMBER_RATIO     = 0.70     # 전체 세션 중 회원 비율
FOOD_ORDER_RATIO = 0.65     # 세션 중 음식 주문 비율

# ── 날짜 구간: 2022~2026년 ──
# · 역사 구간 (70%): 2022-01-01 ~ 2025-12-31  → 완료 세션 (logout_time 있음)
# · 2026 상반기 (15%): 2026-01-01 ~ 2026-04-30  → 완료 세션 (logout_time 있음)
# · 현재 구간 (15%): 2026-05-01 ~ 2026-06-30  → 진행 세션 (logout_time 대부분 NULL)
TODAY         = datetime(2026, 6, 10)
HIST_START    = datetime(2022, 1,  1)
HIST_END      = datetime(2025, 12, 31)
Y2026_HIS_ST  = datetime(2026, 1,  1)
Y2026_HIS_END = datetime(2026, 4, 30)
CURR_START    = datetime(2026, 5,  1)
CURR_END      = TODAY

HIST_SEC      = int((HIST_END     - HIST_START   ).total_seconds())
Y2026_HIS_SEC = int((Y2026_HIS_END - Y2026_HIS_ST).total_seconds())
CURR_SEC      = int((CURR_END     - CURR_START   ).total_seconds())

HIST_ACTIVE   = 0.01   # 완료 구간 세션 중 logout_time 없는 비율 (이상 데이터 소량)
CURR_ACTIVE   = 0.90   # 진행 구간 세션 중 logout_time 없는 비율 (진행 중 대부분)

fake = Faker('ko_KR')

# ══════════════════════════════════════════════
#  1. 레퍼런스 상수
# ══════════════════════════════════════════════
NUM_CAFES  = 26
CAFE_IDS   = [chr(ord('A') + i) for i in range(NUM_CAFES)]
CAFE_NAMES = [
    '강남점', '홍대점', '신촌점', '잠실점', '건대점',
    '합정점', '수유점', '혜화점', '이태원점', '여의도점',
    '노량진점', '서울대입구점', '왕십리점', '신림점', '서초점',
    '강동점', '마포점', '용산점', '성수점', '광화문점',
    '종로점', '인천점', '부산서면점', '대구동성로점', '수원점', '대전은행동점',
]

GRADES = [
    ('브론즈',    0.00,          0),
    ('실버',      0.05,    200_000),   # 20만원 이상
    ('골드',      0.10,    400_000),   # 40만원 이상
    ('다이아몬드', 0.15,    800_000),   # 80만원 이상
    ('루비',      0.20,  1_200_000),   # 120만원 이상
]
# 등급 기준: 이용권(charge) 결제 금액만 포함, 음식 주문 금액 제외
# 목표 분포: 브론즈 40% / 실버 30% / 골드 15% / 다이아몬드 10% / 루비 5%
GRADE_BOUNDS = [
    (1_200_000, '루비'), (800_000, '다이아몬드'),
    (400_000,   '골드'), (200_000,  '실버'), (0, '브론즈'),
]

# 필수 14 + 추가 16 = 30개
FOOD_ITEMS = [
    ('진라면 순한맛',     3500), ('진라면 매운맛',     3500),
    ('불닭볶음면',        4000), ('짜파게티',           3500),
    ('피카츄',            3000), ('소세지',             2000),
    ('타코야끼',          3500), ('물만두',             4000),
    ('불닭게티',          4000), ('핫도그',             3000),
    ('대패삼겹살볶음',    7000), ('김치볶음밥',         6000),
    ('치킨마요덮밥',      7000), ('스팸마요덮밥',       6500),
    ('나가사키짬뽕',      5500), ('치즈불닭볶음면',     4500),
    ('치킨너겟',          4000), ('모짜렐라핫도그',     3500),
    ('고추참치마요덮밥',  6500), ('제육볶음밥',         6500),
    ('카레라이스',        5500), ('오므라이스',         6000),
    ('떡꼬치',            4500), ('콘치즈',             5000),
    ('치즈스틱',          3500), ('베이컨토스트',       4000),
    ('에그샌드위치',      3500), ('아이스아메리카노',   2500),
    ('복숭아아이스티',    2500), ('콜라',               2000),
]
FOOD_NAMES  = [f[0] for f in FOOD_ITEMS]
FOOD_PRICES = {f[0]: f[1] for f in FOOD_ITEMS}
NUM_FOODS   = len(FOOD_ITEMS)  # 30

TICKETS = [(60, 2000), (120, 4000), (180, 5000), (300, 8000), (600, 12000)]
TICKET_TIMES    = [t[0] for t in TICKETS]
TICKET_PRICES   = {t[0]: t[1] for t in TICKETS}
TICKET_WEIGHTS  = np.array([30, 25, 20, 15, 10], dtype=float)
TICKET_WEIGHTS /= TICKET_WEIGHTS.sum()

EVENTS = [
    ('음식_할인_이벤트',   '음식 주문 20% 할인 이벤트',   0, 0.80),
    ('이용권_할인_이벤트', '이용권 충전 10% 할인 이벤트', 1, 0.90),
    ('야간_특가_이벤트',   '야간 이용권 30% 할인 이벤트', 1, 0.70),
    ('주말_음식_특가',     '주말 음식 15% 할인 이벤트',   0, 0.85),
    ('신규_가입_혜택',     '이용권 충전 5% 할인 이벤트',  1, 0.95),
]

# ══════════════════════════════════════════════
#  2. DB 유틸
# ══════════════════════════════════════════════
def get_conn():
    return pymysql.connect(**DB_CONFIG)

def batch_insert(cur, sql, rows):
    for i in range(0, len(rows), BATCH_SIZE):
        cur.executemany(sql, rows[i:i + BATCH_SIZE])

def run_sql(conn, sql):
    cur = conn.cursor()
    cur.execute(sql)
    conn.commit()
    cur.close()

def get_grade(total):
    for threshold, name in GRADE_BOUNDS:
        if total >= threshold:
            return name
    return '브론즈'

def clear_tables(conn):
    """기존 더미 데이터를 전부 지우고 AUTO_INCREMENT를 초기화한다."""
    print('[0] 기존 데이터 초기화...')
    tables = [
        'use_log', 'charge', 'food_order', 'review', 'customer',
        'stock', 'employee', 'event_schedule', 'pc_member',
        'event_info', 'ticket', 'food', 'grade', 'pc_cafe',
    ]
    cur = conn.cursor()
    cur.execute('SET FOREIGN_KEY_CHECKS=0')
    for t in tables:
        cur.execute(f'TRUNCATE TABLE {t}')
    cur.execute('SET FOREIGN_KEY_CHECKS=1')
    conn.commit()
    cur.close()
    print('  완료\n')

# ══════════════════════════════════════════════
#  3. 참조 테이블 삽입
# ══════════════════════════════════════════════
def insert_reference_tables(conn):
    print('[1] 참조 테이블 삽입...')
    rng = np.random.default_rng(1)
    cur = conn.cursor()

    # pc_cafe 26개 (좌석 50~150)
    cafe_seats_vals = rng.integers(50, 151, NUM_CAFES).tolist()
    cafes = [
        (CAFE_IDS[i], CAFE_NAMES[i], 0.0, 0, int(cafe_seats_vals[i]))
        for i in range(NUM_CAFES)
    ]
    batch_insert(cur,
        'INSERT INTO pc_cafe'
        '(pc_cafe_id,pc_cafe_name,average_star_rating,total_sales,total_seats)'
        ' VALUES(%s,%s,%s,%s,%s)', cafes)

    batch_insert(cur,
        'INSERT INTO grade(grade_type,benefit,grade_standard) VALUES(%s,%s,%s)',
        GRADES)
    batch_insert(cur,
        'INSERT INTO food(food_name,price) VALUES(%s,%s)', FOOD_ITEMS)
    batch_insert(cur,
        'INSERT INTO ticket(ticket_time,price) VALUES(%s,%s)', TICKETS)
    batch_insert(cur,
        'INSERT INTO event_info'
        '(event_type,event_content,event_type_num,payment_rate)'
        ' VALUES(%s,%s,%s,%s)', EVENTS)

    # event_schedule: 오늘(2026-05-29) 기준 3가지 유형
    # · 종료 이벤트(60%): end_date < 오늘, 2025년 이후
    # · 진행 중 이벤트(20%): start_date ≤ 오늘 ≤ end_date, 종료일 최대 2026-08-31
    # · 예정 이벤트(20%): start_date > 오늘 (6/10 이후 시작 가능), 최대 2026-08-31
    rng2         = np.random.default_rng(2)
    TODAY_D      = TODAY.date()
    AUG_END_2026 = datetime(2026, 8, 31).date()
    event_types  = [e[0] for e in EVENTS]
    schedules    = []
    seen_sched   = set()

    for cafe_id in CAFE_IDS:
        n_ev   = int(rng2.integers(5, 8))    # 지점당 5~7개 이벤트
        picked = random.sample(event_types, k=min(n_ev, len(event_types)))

        n_non_hist = max(2, round(len(picked) * 0.4))   # 40% 비역사(진행+예정)
        n_active   = max(1, n_non_hist // 2)            # 절반은 현재 진행 중
        n_future   = n_non_hist - n_active              # 나머지는 예정 이벤트
        n_hist     = len(picked) - n_non_hist

        # 진행 중 이벤트: 오늘이 기간 내에 포함, 종료일 최대 2026-08-31
        for et in picked[:n_active]:
            days_ago   = int(rng2.integers(7,  45))
            days_after = int(rng2.integers(10, 95))   # 최대 약 3개월 후 종료
            sd = TODAY_D - timedelta(days=days_ago)
            ed = min(TODAY_D + timedelta(days=days_after), AUG_END_2026)
            key = (et, cafe_id, sd)
            if key not in seen_sched:
                seen_sched.add(key)
                schedules.append((et, cafe_id, sd, ed))

        # 예정 이벤트: start_date > 오늘 (6월 이후 시작 가능), 2026-08-31까지
        for et in picked[n_active:n_active + n_future]:
            days_ahead = int(rng2.integers(2, 60))    # 2~59일 후 시작
            dur        = int(rng2.integers(7,  31))   # 7~30일 기간
            sd = TODAY_D + timedelta(days=days_ahead)
            ed = min(sd + timedelta(days=dur), AUG_END_2026)
            if sd > AUG_END_2026:
                sd = AUG_END_2026 - timedelta(days=7)
            key = (et, cafe_id, sd)
            if key not in seen_sched:
                seen_sched.add(key)
                schedules.append((et, cafe_id, sd, ed))

        # 과거 종료 이벤트: start/end 모두 오늘 이전 (2022-01-01 이후)
        for et in picked[n_active + n_future:]:
            end_ago = int(rng2.integers(30, 1644))  # 최대 ~4.5년 전 종료
            dur     = int(rng2.integers(14,  90))
            ed = TODAY_D - timedelta(days=end_ago)
            sd = ed - timedelta(days=dur)
            if sd < HIST_START.date():
                sd = HIST_START.date()
            key = (et, cafe_id, sd)
            if key not in seen_sched:
                seen_sched.add(key)
                schedules.append((et, cafe_id, sd, ed))

    batch_insert(cur,
        'INSERT INTO event_schedule'
        '(event_type,pc_cafe_id,event_start_date,event_end_date)'
        ' VALUES(%s,%s,%s,%s)', schedules)
    conn.commit()
    cur.close()

    # 이벤트 인덱스: (cafe_id, date) → [food_rate, charge_rate]
    # 미래 이벤트(sd > TODAY)는 트랜잭션에 영향 없으므로 TODAY까지만 인덱싱
    etype_info    = {e[0]: (e[2], e[3]) for e in EVENTS}
    event_map     = {}
    event_map_end = CURR_END.date()
    for (et, cafe_id, sd, ed) in schedules:
        type_num, rate = etype_info[et]
        d = sd
        while d <= min(ed, event_map_end):
            key = (cafe_id, d)
            if key not in event_map:
                event_map[key] = [1.00, 1.00]   # [food_rate, charge_rate]
            if type_num == 0:
                event_map[key][0] = float(rate)
            else:
                event_map[key][1] = float(rate)
            d += timedelta(days=1)

    cafe_seats = {CAFE_IDS[i]: int(cafe_seats_vals[i]) for i in range(NUM_CAFES)}
    print(f'  pc_cafe: {NUM_CAFES}개 | food: {NUM_FOODS}개 '
          f'| event_schedule: {len(schedules)}개 | event_index: {len(event_map):,}건')
    return cafe_seats, event_map, cafes

# ══════════════════════════════════════════════
#  4. pc_member (10,000명: owner 4 + user 9,996)
# ══════════════════════════════════════════════
def insert_members(conn):
    """
    모든 user 초기 total_payment_amount = 0 (bronze).
    등급 다양성은 세션 빈도 가중치(member_weights)로 확보:
      tier별 평균 세션 수 → 평균 충전금액이 각 등급 구간에 들어오도록 calibration
      (1M 세션 기준 target: bronze 16세션 / silver 50 / gold 101 / diamond 185 / ruby 293)
    반환값: (user_ids, member_weights)
    """
    print('[2] pc_member 생성 (10,000명)...')
    rng = np.random.default_rng(10)
    fake.seed_instance(10)
    rows = []

    # owner 4명 (grade_type=NULL, total_payment_amount=0)
    for i in range(1, 5):
        rows.append((f'owner{i:03d}', f'ownerpw{i:03d}',
                     fake.name(), 0, None, 0, 'owner'))

    # user 9,996명 (초기 bronze, total_payment_amount=0)
    for i in range(1, 9997):
        remain = int(rng.integers(0, 601))
        rows.append((f'user{i:05d}', f'pw{i:05d}',
                     fake.name(), remain, '브론즈', 0, 'user'))

    cur = conn.cursor()
    batch_insert(cur,
        'INSERT INTO pc_member'
        '(member_id,member_password,member_name,remain_time,'
        'grade_type,total_payment_amount,member_type)'
        ' VALUES(%s,%s,%s,%s,%s,%s,%s)', rows)
    conn.commit()
    cur.close()

    user_ids = [r[0] for r in rows if r[6] == 'user']
    n_u      = len(user_ids)  # 9,996

    # ── 세션 빈도 가중치 생성 ──────────────────────────────────────────
    # 목표 등급 분포: 브론즈 40% / 실버 30% / 골드 15% / 다이아몬드 10% / 루비 5%
    # 각 tier의 평균 세션 수를 새 기준금액에 맞게 calibration (avg charge ≈ 4,740원/세션)
    #   브론즈  avg  16 sessions → avg  75,840원  < 200,000 (bronze)
	  #   실버    avg  50 sessions → avg 237,000원  ∈ [200,000 ~ 400,000) (silver)
    #   골드    avg 101 sessions → avg 478,740원  ∈ [400,000 ~ 800,000) (gold)
    #   다이아  avg 185 sessions → avg 876,900원  ∈ [800,000 ~ 1,200,000) (diamond)
    #   루비    avg 293 sessions → avg 1,388,820원 ≥ 1,200,000 (ruby)
    TIER_FRACS = [0.40, 0.30, 0.15, 0.10, 0.05]
    TIER_W     = [16.0, 50.0, 101.0, 185.0, 293.0]
    tier_sizes    = [round(n_u * f) for f in TIER_FRACS]
    tier_sizes[-1] = n_u - sum(tier_sizes[:-1])   # 반올림 오차 보정

    raw_w = np.empty(n_u, dtype=float)
    idx = 0
    for size, w in zip(tier_sizes, TIER_W):
        raw_w[idx:idx + size] = w
        idx += size
    rng.shuffle(raw_w)                   # tier를 사용자에게 무작위 배정
    member_weights = raw_w / raw_w.sum()

    print(f'  완료: owner=4, user={n_u:,}  (tier 분포: {[s for s in tier_sizes]})')
    return user_ids, member_weights

# ══════════════════════════════════════════════
#  5. employee (지점당 3~7명, 총 ~130명)
# ══════════════════════════════════════════════
def insert_employees(conn, cafe_seats):
    print('[3] employee 생성...')
    fake.seed_instance(20)
    random.seed(20)
    rows = []
    for cafe_id in CAFE_IDS:
        seats = cafe_seats[cafe_id]
        # 좌석 수에 비례한 직원 수 (50석→3~5명, 100석→5~8명, 150석→7~11명), 랜덤 편차 ±2
        base  = max(3, int(seats / 18))
        n     = max(3, min(11, base + random.randint(-1, 3)))
        mgr_done = False
        for j in range(n):
            is_mgr = (not mgr_done) and (j == 0 or random.random() < 0.3)
            if is_mgr:
                mgr_done = True
                pos, wage = '매니저', random.randint(12000, 15000)
            else:
                pos, wage = '아르바이트', 11000
            rows.append((fake.name(), cafe_id, pos, wage,
                         random.choice([True, False])))
    cur = conn.cursor()
    batch_insert(cur,
        'INSERT INTO employee'
        '(employee_name,pc_cafe_id,employee_position,hour_wage,is_currently_working)'
        ' VALUES(%s,%s,%s,%s,%s)', rows)
    conn.commit()
    cur.close()
    print(f'  {len(rows)}명 삽입')

# ══════════════════════════════════════════════
#  6. stock (26 × 30 = 780건)
# ══════════════════════════════════════════════
def insert_stock(conn):
    print('[4] stock 생성 (780건)...')
    rng = np.random.default_rng(30)
    qtys = rng.integers(20, 201, NUM_CAFES * NUM_FOODS).tolist()
    rows = [
        (CAFE_IDS[ci], FOOD_NAMES[fi], int(qtys[ci * NUM_FOODS + fi]))
        for ci in range(NUM_CAFES)
        for fi in range(NUM_FOODS)
    ]
    cur = conn.cursor()
    batch_insert(cur,
        'INSERT INTO stock(pc_cafe_id,food_name,stock_quantity)'
        ' VALUES(%s,%s,%s)', rows)
    conn.commit()
    cur.close()
    print(f'  {len(rows)}건 삽입')

# ══════════════════════════════════════════════
#  7. use_log + charge + food_order (청크 방식)
# ══════════════════════════════════════════════
def insert_transaction_data(conn, cafe_seats, event_map, user_ids, member_weights):
    print('[5] use_log / charge / food_order 1M 청크 생성 중...')

    rng = np.random.default_rng(42)

    # ── PC방별 세션 배분 가중치 (좌석 규모 40% + 랜덤 인기도 60%) ──
    rng_pop      = np.random.default_rng(99)
    seat_factor  = np.array([cafe_seats[c] for c in CAFE_IDS], dtype=float)
    seat_factor /= seat_factor.max()
    pop_noise    = rng_pop.exponential(1.0, NUM_CAFES)
    pop_noise   /= pop_noise.max()
    cafe_raw_w   = seat_factor * 0.4 + pop_noise * 0.6
    cafe_weights = (cafe_raw_w / cafe_raw_w.sum()).astype(float)

    user_ids_arr     = np.array(user_ids)
    cafe_seats_arr   = np.array([cafe_seats[c] for c in CAFE_IDS], dtype=int)
    ticket_arr       = np.array(TICKET_TIMES, dtype=int)
    ticket_price_arr = np.array([TICKET_PRICES[t] for t in TICKET_TIMES], dtype=int)

    # ── 월별 세션 가중치 ──
    # 각 월을 독립적으로 샘플링 → 특정 연도에 치우치지 않음
    # 단, 전월 대비 ±5~25% 조건을 사후에 적용해 부드러운 증감 유지
    rng_mw = np.random.default_rng(777)
    all_months = []
    yr, mo = 2022, 1
    while (yr, mo) <= (2026, 6):
        all_months.append((yr, mo))
        mo += 1
        if mo > 12:
            mo, yr = 1, yr + 1
    N_MONTHS = len(all_months)  # 54개월

    target = TOTAL_SESSIONS / N_MONTHS  # 월 평균 목표 세션 수 (~18,500)

    # Step 1: 각 월 독립 균등 샘플링 (±25% 범위)
    raw = rng_mw.uniform(target * 0.75, target * 1.25, N_MONTHS)

    # Step 2: 전월 대비 ±25% 초과 시 클램프 → 급격한 변동 방지
    monthly_counts = raw.copy()
    for m in range(1, N_MONTHS):
        lo = monthly_counts[m - 1] * 0.75
        hi = monthly_counts[m - 1] * 1.25
        monthly_counts[m] = float(np.clip(monthly_counts[m], lo, hi))

    monthly_weights = monthly_counts / monthly_counts.sum()

    # 월별 시작·끝 datetime 및 초 단위 길이 사전 계산
    # 마지막 월(2026-06)은 CURR_END(6월 10일)로 상한 제한
    month_starts   = []
    month_ends     = []
    month_dur_secs = []
    for yr, mo in all_months:
        ms = datetime(yr, mo, 1)
        me = datetime(yr + 1, 1, 1) if mo == 12 else datetime(yr, mo + 1, 1)
        # 마지막 월은 TODAY(CURR_END)로 상한 클램프
        me = min(me, CURR_END + timedelta(seconds=1))
        month_starts.append(ms)
        month_ends.append(me - timedelta(seconds=1))
        month_dur_secs.append(max(1, int((me - ms).total_seconds()) - 1))

    # 현재 진행 구간 마스크 (2026-05, 2026-06)
    is_curr_month = np.array(
        [yr == 2026 and mo in (5, 6) for yr, mo in all_months])

    print(f'  월별 세션 계획 ({N_MONTHS}개월, ±5~25% 랜덤 워크):')
    for m in range(0, N_MONTHS, 12):
        print(f'    {all_months[m][0]}-{all_months[m][1]:02d}: '
              f'~{monthly_counts[m]:.0f}건/월')

    sql_log    = ('INSERT INTO use_log'
                  '(pc_cafe_id,seat_num,member_id,login_time,logout_time)'
                  ' VALUES(%s,%s,%s,%s,%s)')
    sql_charge = ('INSERT INTO charge'
                  '(pc_cafe_id,seat_num,ticket_time,member_id,'
                  'charge_pay_amount,payment_rate,charged_at)'
                  ' VALUES(%s,%s,%s,%s,%s,%s,%s)')
    sql_order  = ('INSERT INTO food_order'
                  '(order_id,food_name,pc_cafe_id,seat_num,'
                  'food_quantity,food_pay_amount,payment_rate,ordered_at)'
                  ' VALUES(%s,%s,%s,%s,%s,%s,%s,%s)')

    member_totals   = {}
    active_sessions = []
    order_id_next   = 1
    total_log = total_charge = total_order_rows = 0
    CHUNKS = TOTAL_SESSIONS // CHUNK_SIZE

    cur = conn.cursor()
    for chunk in range(CHUNKS):
        n = CHUNK_SIZE

        # ── numpy 벡터 난수 생성 ────────────────────
        cafe_idx     = rng.choice(NUM_CAFES, n, p=cafe_weights)
        is_member    = rng.random(n) < MEMBER_RATIO
        mem_idx      = rng.choice(len(user_ids), n, p=member_weights)
        tkt_idx      = rng.choice(len(TICKET_TIMES), n, p=TICKET_WEIGHTS)
        extra_min    = rng.integers(-10, 31, n)
        has_food     = rng.random(n) < FOOD_ORDER_RATIO
        n_food_items = rng.choice([1, 2, 3], n, p=[0.50, 0.35, 0.15])
        max_seats    = cafe_seats_arr[cafe_idx]
        seat_nums    = (rng.random(n) * max_seats).astype(int) + 1

        # 월별 가중치로 각 세션의 연·월 샘플링
        month_idx_arr = rng.choice(N_MONTHS, n, p=monthly_weights)
        offset_frac   = rng.random(n)          # [0,1] → 월 내 랜덤 위치
        rand_act      = rng.random(n)
        is_curr_arr   = is_curr_month[month_idx_arr]
        no_logout     = np.where(is_curr_arr,
                                 rand_act < CURR_ACTIVE,
                                 rand_act < HIST_ACTIVE)

        log_buf = []; charge_buf = []; order_buf = []

        for i in range(n):
            ci        = int(cafe_idx[i])
            cafe_id   = CAFE_IDS[ci]
            seat_num  = int(seat_nums[i])
            member_id = str(user_ids_arr[int(mem_idx[i])]) if bool(is_member[i]) else None
            tt        = int(ticket_arr[int(tkt_idx[i])])
            tp        = int(ticket_price_arr[int(tkt_idx[i])])

            # 해당 세션의 월 결정 → 월 내 랜덤 시각
            midx       = int(month_idx_arr[i])
            login_dt   = month_starts[midx] + timedelta(
                             seconds=int(offset_frac[i] * month_dur_secs[midx]))
            period_end = month_ends[midx]

            # logout_time 계산
            if bool(no_logout[i]):
                logout_dt = None
            else:
                logout_dt = login_dt + timedelta(minutes=tt + int(extra_min[i]))
                if logout_dt > period_end:
                    logout_dt = period_end

            login_str  = login_dt.strftime('%Y-%m-%d %H:%M:%S')
            logout_str = logout_dt.strftime('%Y-%m-%d %H:%M:%S') if logout_dt else None

            # use_log
            log_buf.append((cafe_id, seat_num, member_id, login_str, logout_str))

            # charge: 이벤트 할인 적용 (event_type_num=1)
            ev = event_map.get((cafe_id, login_dt.date()), [1.00, 1.00])
            c_rate = float(ev[1])
            c_pay  = round(tp * c_rate)
            charge_buf.append((cafe_id, seat_num, tt, member_id,
                               c_pay, c_rate, login_str))
            if member_id:
                member_totals[member_id] = member_totals.get(member_id, 0) + c_pay

            # food_order: 이벤트 할인 적용 (event_type_num=0)
            if bool(has_food[i]):
                f_rate = float(ev[0])
                nf     = int(n_food_items[i])
                # 30개 음식 중 nf개 완전 균등 랜덤 (PK 중복 없이)
                food_idx = rng.choice(NUM_FOODS, size=nf, replace=False)
                foods    = [FOOD_NAMES[int(fi)] for fi in food_idx]

                if logout_dt:
                    span_sec = max(0, int((logout_dt - login_dt).total_seconds()))
                    offset_s = int(rng.integers(0, span_sec + 1)) if span_sec > 0 else 0
                else:
                    offset_s = int(rng.integers(5, 61)) * 60
                order_dt = (login_dt + timedelta(seconds=offset_s)).strftime('%Y-%m-%d %H:%M:%S')

                for fn in foods:
                    qty   = int(rng.integers(1, 4))
                    f_pay = round(FOOD_PRICES[fn] * qty * f_rate)
                    order_buf.append((order_id_next, fn, cafe_id, seat_num,
                                      qty, f_pay, f_rate, order_dt))
                order_id_next += 1

            if logout_dt is None:
                active_sessions.append((cafe_id, seat_num, member_id, tt, login_dt))

        # 배치 INSERT
        batch_insert(cur, sql_log,    log_buf)
        batch_insert(cur, sql_charge, charge_buf)
        batch_insert(cur, sql_order,  order_buf)
        conn.commit()

        total_log        += len(log_buf)
        total_charge     += len(charge_buf)
        total_order_rows += len(order_buf)
        print(f'\r  [{chunk+1:2d}/{CHUNKS}] '
              f'use_log {total_log:>9,} | '
              f'charge {total_charge:>9,} | '
              f'food_order rows {total_order_rows:>9,}',
              end='', flush=True)

    cur.close()
    print()
    print(f'  use_log:    {total_log:,}건')
    print(f'  charge:     {total_charge:,}건')
    print(f'  food_order: {total_order_rows:,}행  (고유 order_id: {order_id_next-1:,})')
    return member_totals, active_sessions

# ══════════════════════════════════════════════
#  8. total_payment_amount + grade_type 갱신
# ══════════════════════════════════════════════
def update_member_grades(conn, member_totals):
    """
    이용권(charge) 결제 금액만 total_payment_amount 에 반영 (음식 주문 금액 제외).
    초기값 0에서 시작하므로 SET = 세션 기간 누적 충전 금액 그 자체.
    grade_type 은 SET 후 get_grade() 로 동시에 갱신.
    세션이 없는 회원(member_totals 미포함)은 total=0, grade=bronze 유지.
    """
    print('[6] 회원 등급 갱신...')
    rows = [(v, get_grade(v), k) for k, v in member_totals.items()]
    cur  = conn.cursor()
    batch_insert(cur,
        'UPDATE pc_member '
        'SET total_payment_amount=%s, grade_type=%s '
        'WHERE member_id=%s',
        rows)
    conn.commit()
    cur.close()
    print(f'  {len(rows):,}명 갱신 (이용권 충전금액만 반영, 음식 주문 제외)')

# ══════════════════════════════════════════════
#  9. review (~5,000건)
# ══════════════════════════════════════════════
def insert_reviews(conn, user_ids):
    print('[7] review 생성...')
    fake.seed_instance(50)
    random.seed(50)

    # 지점별 품질 수준: 1.5~4.9 구간에 26개 값을 고르게 배치 후 무작위 섞기
    # → 리뷰 평균이 지점마다 크게 다르게 형성됨
    rng_q        = np.random.default_rng(55)
    base_targets = np.linspace(1.5, 4.9, NUM_CAFES)
    base_targets  = np.clip(base_targets + rng_q.normal(0, 0.15, NUM_CAFES), 1.5, 4.9)
    rng_q.shuffle(base_targets)
    cafe_quality = {CAFE_IDS[i]: float(base_targets[i]) for i in range(NUM_CAFES)}

    TITLES = [
        '정말 좋아요', '무난했어요', '별로였어요', '또 방문할게요',
        '시설이 깨끗해요', '직원이 친절해요', '가격이 합리적',
        '음식이 맛있어요', '좌석이 편해요', '인터넷이 빨라요',
    ]
    CONTENTS = [
        '전반적으로 매우 만족스러웠습니다.',
        '자리 간격이 조금 좁았지만 나쁘지 않았습니다.',
        '음식 배달이 빠르고 맛도 좋았습니다.',
        '가격 대비 시설이 훌륭합니다.',
        '직원 응대가 매우 친절했습니다.',
        '다음에도 꼭 이용할 것 같아요.',
        'PC 사양이 높아서 게임하기 쾌적했어요.',
        '위치도 편하고 시설도 깔끔했습니다.',
        '이용 시간이 넉넉해서 좋았습니다.',
        '가격은 조금 비싸지만 그만한 가치가 있어요.',
    ]

    n_reviewers = min(3000, len(user_ids))
    reviewers   = random.sample(user_ids, k=n_reviewers)
    rows = []
    for mid in reviewers:
        n_rev = random.choices([1, 2, 3], weights=[70, 20, 10])[0]
        for rid in range(1, n_rev + 1):
            cafe_id = random.choice(CAFE_IDS)
            # 해당 지점 품질 수준 기반 정규분포 샘플링 → 지점마다 다른 평균 수렴
            raw    = random.gauss(cafe_quality[cafe_id], 0.8)
            raw    = max(1.0, min(5.0, raw))
            rating = round(raw * 2) / 2   # 0.5 단위 (1.0, 1.5, 2.0 … 5.0)
            rows.append((mid, rid, cafe_id, rating,
                         random.choice(TITLES), random.choice(CONTENTS)))

    cur = conn.cursor()
    batch_insert(cur,
        'INSERT INTO review'
        '(member_id,review_id,pc_cafe_id,star_rating,review_title,review_content)'
        ' VALUES(%s,%s,%s,%s,%s,%s)', rows)
    conn.commit()
    cur.close()
    print(f'  {len(rows):,}건 삽입')

# ══════════════════════════════════════════════
#  10. customer 스냅샷 + 회원 remain_time 완전 동기화
# ══════════════════════════════════════════════
def insert_customers(conn, active_sessions, cafe_seats):
    """
    [remain_time 정합성]
    - 회원: member_id 별로 remain_time 을 ONE VALUE 로 결정
            → 해당 회원의 모든 customer 행 + pc_member.remain_time 에 동일한 값 기록
            → customer.member_id = pc_member.member_id 인 행은 remain_time 항상 일치
    - 비회원: ticket_time 기반 임의 잔여시간 (해당 세션의 charge 기록과 연동)
              member_id = NULL 이므로 pc_member 미연동

    [FK 불가 사유]
    - remain_time 은 pc_member 의 PK/UNIQUE KEY 가 아님
    - SQL 표준상 FK 는 PK/UK 만 참조 가능 → 애플리케이션 레벨 동기화로 대체

    [전 지점 커버리지]
    - active_sessions 에 없는 지점(R~Z 포함)은 비회원 1건 강제 삽입
    """
    print('[8] customer 스냅샷 생성...')

    # Step 1: (cafe_id, seat_num) 중복 제거 → 가장 최근 세션만 유지
    seen_seats = {}
    for sess in active_sessions:
        key = (sess[0], sess[1])
        if key not in seen_seats or sess[4] > seen_seats[key][4]:
            seen_seats[key] = sess

    # Step 1b: 지점별 점유율 30~90% 랜덤 캡
    #   빈 자리 보장: 어느 지점도 좌석 전체가 꽉 차지 않음
    #   점유율은 지점마다 완전히 독립적으로 결정
    random.seed(888)
    capped = {}
    for cafe_id in CAFE_IDS:
        total = cafe_seats[cafe_id]
        occ   = random.uniform(0.30, 0.90)      # 30%~90% 사이 균등 분포
        cap   = max(1, int(total * occ))
        # 가장 최근 활성 세션 순으로 cap개만 선택
        cafe_sesh = sorted(
            [(k, v) for k, v in seen_seats.items() if k[0] == cafe_id],
            key=lambda x: x[1][4], reverse=True
        )[:cap]
        for k, v in cafe_sesh:
            capped[k] = v
    seen_seats = capped

    # Step 2: 회원별 remain_time 을 단 하나의 값으로 확정 (첫 등장 기준)
    #         → 같은 member_id 가 여러 (cafe, seat) 에 있어도 동일한 값 사용
    member_remain = {}
    for (cafe_id, seat_num), sess in seen_seats.items():
        _, _, member_id, ticket_time, _ = sess
        if member_id and member_id not in member_remain:
            lo = max(1, int(ticket_time * 0.20))
            hi = max(lo + 1, int(ticket_time * 0.95))
            member_remain[member_id] = random.randint(lo, hi)

    # Step 3: customer 행 생성 (회원은 확정된 단일값, 비회원은 개별 임의값)
    cust_rows  = []
    used_seats = set()
    for (cafe_id, seat_num), sess in seen_seats.items():
        _, _, member_id, ticket_time, _ = sess
        if member_id:
            remain = member_remain[member_id]   # 회원 → 단일 확정값
        else:
            lo     = max(1, int(ticket_time * 0.20))
            hi     = max(lo + 1, int(ticket_time * 0.95))
            remain = random.randint(lo, hi)     # 비회원 → 충전 기록 기반 임의값
        cust_rows.append((cafe_id, seat_num, member_id, remain))
        used_seats.add((cafe_id, seat_num))

    # Step 4: active_sessions 에 없는 지점 보완 (A~Z 전 지점 커버 보장)
    covered_cafes = {row[0] for row in cust_rows}
    for cafe_id in CAFE_IDS:
        if cafe_id not in covered_cafes:
            # 빈 좌석 탐색 (seat 1부터 순서대로)
            seat_num = next(
                (s for s in range(1, cafe_seats[cafe_id] + 1)
                 if (cafe_id, s) not in used_seats),
                1  # 모두 사용 중이면 1번 (사실상 발생 불가)
            )
            remain = random.randint(60, 285)    # 비회원, 300분 티켓 기준 임의 잔여
            cust_rows.append((cafe_id, seat_num, None, remain))
            used_seats.add((cafe_id, seat_num))

    # Step 5: DB 삽입
    cur = conn.cursor()
    batch_insert(cur,
        'INSERT INTO customer(pc_cafe_id,seat_num,member_id,remain_time)'
        ' VALUES(%s,%s,%s,%s)', cust_rows)
    conn.commit()

    # Step 6: 회원 pc_member.remain_time 동기화 (customer 와 동일값)
    if member_remain:
        sync_rows = [(v, k) for k, v in member_remain.items()]
        batch_insert(cur,
            'UPDATE pc_member SET remain_time=%s WHERE member_id=%s',
            sync_rows)
        conn.commit()
    cur.close()

    n_mem   = sum(1 for r in cust_rows if r[2] is not None)
    n_guest = len(cust_rows) - n_mem
    print(f'  customer: {len(cust_rows)}건 삽입 (회원: {n_mem}, 비회원: {n_guest})')
    print(f'  pc_member.remain_time 동기화: {len(member_remain)}명 (회원 전용)')
    covered_now = {r[0] for r in cust_rows}
    print(f'  커버 지점: {sorted(covered_now)} ({len(covered_now)}개)')

# ══════════════════════════════════════════════
#  11. pc_cafe 집계 갱신 (별점, 총매출)
# ══════════════════════════════════════════════
def update_cafe_aggregates(conn):
    print('[9] pc_cafe 평균 별점·총매출 갱신...')

    # 평균 별점: ROUND(AVG, 1) 을 DECIMAL(2,1)로 명시 저장
    # → SELECT ROUND(AVG(star_rating),1) FROM review WHERE pc_cafe_id=X 와 완전 일치
    run_sql(conn, """
        UPDATE pc_cafe c
        LEFT JOIN (
            SELECT pc_cafe_id,
                   CAST(ROUND(AVG(star_rating), 1) AS DECIMAL(2,1)) AS avg_r
            FROM review
            GROUP BY pc_cafe_id
        ) r ON c.pc_cafe_id = r.pc_cafe_id
        SET c.average_star_rating = COALESCE(r.avg_r, CAST(0.0 AS DECIMAL(2,1)))
    """)

    # 총 매출 = charge 합계 + food_order 합계
    run_sql(conn, """
        UPDATE pc_cafe c
        LEFT JOIN (
            SELECT pc_cafe_id, SUM(charge_pay_amount) AS cs
            FROM charge
            GROUP BY pc_cafe_id
        ) ch ON c.pc_cafe_id = ch.pc_cafe_id
        LEFT JOIN (
            SELECT pc_cafe_id, SUM(food_pay_amount) AS fs
            FROM food_order
            GROUP BY pc_cafe_id
        ) fo ON c.pc_cafe_id = fo.pc_cafe_id
        SET c.total_sales = COALESCE(ch.cs, 0) + COALESCE(fo.fs, 0)
    """)
    print('  완료')

# ══════════════════════════════════════════════
#  MAIN
# ══════════════════════════════════════════════
def main():
    print('=' * 58)
    print('  EZPC 더미 데이터 생성 스크립트')
    print(f'  DB: {DB_CONFIG["host"]}:{DB_CONFIG["port"]} / {DB_CONFIG["database"]}')
    print('=' * 58)
    t0 = datetime.now()

    try:
        conn = get_conn()
        print('  DB 연결 성공\n')
    except Exception as e:
        print(f'[DB 연결 실패] {e}')
        print('DB_CONFIG 의 host / user / password / database 를 확인하세요.')
        sys.exit(1)

    try:
        clear_tables(conn)
        cafe_seats, event_map, cafes        = insert_reference_tables(conn)
        user_ids, member_weights            = insert_members(conn)
        insert_employees(conn, cafe_seats)
        insert_stock(conn)
        member_totals, active_sessions = insert_transaction_data(
                                            conn, cafe_seats, event_map,
                                            user_ids, member_weights)
        update_member_grades(conn, member_totals)
        insert_reviews(conn, user_ids)
        insert_customers(conn, active_sessions, cafe_seats)  # cafe_seats 전달
        update_cafe_aggregates(conn)
    except Exception as e:
        print(f'\n[ERROR] {e}')
        conn.rollback()
        raise
    finally:
        conn.close()

    elapsed = (datetime.now() - t0).total_seconds()
    print(f'\n총 소요 시간: {elapsed:.0f}초 ({elapsed/60:.1f}분)')
    print('=== 생성 완료 ===')

    print("""
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
[HeidiSQL 검증 쿼리]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

-- ① 주요 테이블 건수
SELECT 'use_log'    t, COUNT(*) n FROM use_log
UNION ALL SELECT 'charge',     COUNT(*) FROM charge
UNION ALL SELECT 'food_order', COUNT(*) FROM food_order
UNION ALL SELECT 'pc_member',  COUNT(*) FROM pc_member;

-- ② owner 수 / 등급 분포 (다양해야 함)
SELECT member_type, COUNT(*) FROM pc_member GROUP BY member_type;
SELECT grade_type, COUNT(*) FROM pc_member GROUP BY grade_type
  ORDER BY FIELD(grade_type,'루비','다이아몬드','골드','실버','브론즈');

-- ③ use_log.member_id 정합성: pc_member 에 없는 member_id 존재하면 오류
SELECT COUNT(*) AS orphan_member_in_uselog
FROM use_log ul
WHERE ul.member_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM pc_member m WHERE m.member_id = ul.member_id);
-- → 0 이어야 정상

-- ④ customer.member_id 정합성: pc_member 에 없는 member_id 존재하면 오류
SELECT COUNT(*) AS orphan_member_in_customer
FROM customer c
WHERE c.member_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM pc_member m WHERE m.member_id = c.member_id);
-- → 0 이어야 정상

-- ⑤ customer ↔ pc_member remain_time 불일치 검증
SELECT c.pc_cafe_id, c.seat_num,
       c.remain_time  AS customer_rt,
       m.remain_time  AS member_rt
FROM customer c
JOIN pc_member m ON c.member_id = m.member_id
WHERE c.remain_time != m.remain_time
LIMIT 20;
-- → 결과 0행 = 완전 일치

-- ⑥ customer 지점 커버리지 (A~Z 모두 있어야 함)
SELECT pc_cafe_id, COUNT(*) AS seats FROM customer GROUP BY pc_cafe_id ORDER BY pc_cafe_id;

-- ⑦ customer ↔ use_log 정합성
--    customer 의 각 (pc_cafe_id, seat_num, member_id) 에 대응하는
--    logout_time IS NULL 인 use_log 행이 존재해야 함
SELECT COUNT(*) AS unmatched
FROM customer c
WHERE NOT EXISTS (
  SELECT 1 FROM use_log ul
  WHERE ul.pc_cafe_id = c.pc_cafe_id
    AND ul.seat_num   = c.seat_num
    AND (ul.member_id = c.member_id
         OR (ul.member_id IS NULL AND c.member_id IS NULL))
    AND ul.logout_time IS NULL
);
-- → 0 이어야 정상 (단, 지점 보완 행은 보정 필요 시 아래 참고)

-- ⑧ 별점 일치 검증 (diff = 0 이면 완전 일치)
SELECT c.pc_cafe_id,
       c.average_star_rating AS stored,
       CAST(ROUND(AVG(r.star_rating),1) AS DECIMAL(2,1)) AS calculated,
       c.average_star_rating
         - CAST(ROUND(AVG(r.star_rating),1) AS DECIMAL(2,1)) AS diff
FROM pc_cafe c
JOIN review r ON c.pc_cafe_id = r.pc_cafe_id
GROUP BY c.pc_cafe_id, c.average_star_rating;
-- → diff 전부 0 이어야 정상

-- ⑨ 이벤트 전/후 평균 결제금액 비교
SELECT
  CASE WHEN payment_rate < 1.00 THEN '이벤트 적용' ELSE '정상 결제' END AS 구분,
  COUNT(*) AS 건수,
  ROUND(AVG(charge_pay_amount)) AS 평균결제액
FROM charge GROUP BY 구분;
-- → 이벤트 적용 행의 평균결제액 < 정상 결제 행의 평균결제액

-- ⑩ customer remain_time 범위 (0 없어야 함)
SELECT MIN(remain_time), MAX(remain_time), ROUND(AVG(remain_time)) FROM customer;
""")

if __name__ == '__main__':
    main()
