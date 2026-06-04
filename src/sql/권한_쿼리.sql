-- ============================================================
-- EZPC DB 계정 권한 설정
-- HeidiSQL에서 root 계정으로 실행
-- ============================================================

-- ------------------------------------------------------------
-- 1. ezpc_auth : 로그인/회원가입 전용 계정
--    로그인 화면에서 아이디/비밀번호 확인 SELECT
--		회원가입 화면에서 새로운 회원 삽입 INSERT
-- ------------------------------------------------------------
CREATE USER IF NOT EXISTS 'ezpc_auth'@'%' IDENTIFIED BY 'auth1234';
GRANT SELECT, INSERT ON EZPC.pc_member TO 'ezpc_auth'@'%';


-- ------------------------------------------------------------
-- 2. ezpc_user : 일반 사용자 계정
--    지점 조회, 좌석 선택, 이용권 충전(프로시저), 음식 주문,
--    리뷰 작성 등 사용자 업무만 수행
-- ------------------------------------------------------------
CREATE USER IF NOT EXISTS 'ezpc_user'@'%' IDENTIFIED BY 'user1234';

-- 조회 가능 테이블
GRANT SELECT ON EZPC.pc_cafe        TO 'ezpc_user'@'%';
GRANT SELECT, UPDATE ON EZPC.pc_member TO 'ezpc_user'@'%';
-- UPDATE: 퇴실 시 remain_time 갱신 (updateRemainTimeAfterUse)
GRANT SELECT ON EZPC.ticket         TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.food           TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.grade          TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.event_info     TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.event_schedule TO 'ezpc_user'@'%';
GRANT SELECT, UPDATE ON EZPC.stock  TO 'ezpc_user'@'%'; -- 음식 주문 시 재고 차감
GRANT SELECT ON EZPC.review         TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.charge         TO 'ezpc_user'@'%';
GRANT SELECT ON EZPC.food_order     TO 'ezpc_user'@'%';

-- 입퇴실 처리 (customer, use_log)
GRANT SELECT, INSERT, UPDATE, DELETE ON EZPC.customer TO 'ezpc_user'@'%';
GRANT SELECT, INSERT, UPDATE        ON EZPC.use_log   TO 'ezpc_user'@'%';

-- 리뷰 작성/수정/삭제
GRANT SELECT, INSERT, UPDATE, DELETE ON EZPC.review   TO 'ezpc_user'@'%';

-- 음식 주문 삽입
GRANT SELECT, INSERT                ON EZPC.food_order TO 'ezpc_user'@'%';

-- 이용권 충전 프로시저 실행 (프로시저 내부에서 charge INSERT + 등급 갱신 처리)
GRANT EXECUTE ON PROCEDURE EZPC.charge_by_customer TO 'ezpc_user'@'%';


-- ------------------------------------------------------------
-- 3. ezpc_owner : 운영자 계정
--    매출 조회, 직원 관리, 회원 관리, 재고 관리 등
--    모든 테이블 접근 가능
-- ------------------------------------------------------------
CREATE USER IF NOT EXISTS 'ezpc_owner'@'%' IDENTIFIED BY 'owner1234';
GRANT ALL PRIVILEGES ON EZPC.* TO 'ezpc_owner'@'%';


-- 권한 즉시 반영
FLUSH PRIVILEGES;


-- ============================================================
-- [확인 쿼리] 계정별 권한 조회
-- ============================================================
SHOW GRANTS FOR 'ezpc_auth'@'%';
SHOW GRANTS FOR 'ezpc_user'@'%';
SHOW GRANTS FOR 'ezpc_owner'@'%';
