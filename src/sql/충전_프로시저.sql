DELIMITER //

CREATE PROCEDURE charge_by_customer(
    IN p_pc_cafe_id VARCHAR(30),
    IN p_seat_num INT,
    IN p_member_id VARCHAR(50),
    IN p_ticket_time INT
)
BEGIN
    DECLARE v_ticket_price INT DEFAULT 0;
    DECLARE v_payment_rate DECIMAL(3,2) DEFAULT 1.00;
     DECLARE v_benefit DECIMAL(3,2) DEFAULT 0.00;
	 DECLARE v_charge_pay_amount INT DEFAULT 0;
    DECLARE v_total_payment INT DEFAULT 0;
    DECLARE v_new_grade VARCHAR(50);

    -- SQL 오류가 발생하면 중단.
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
	-- 트랜잭션 적용 
    START TRANSACTION;

    -- 이용권 가격 조회
    SELECT price
    INTO v_ticket_price
    FROM ticket
    WHERE ticket_time = p_ticket_time;

    --  현재 PC방에서 진행 중인 충전 이벤트 결제비율 조회
    -- event_type_num = 1은 충전
    -- 여러 충전 이벤트가 동시에 적용될 경우 가장 낮은 payment_rate, 즉 가장 큰 할인을 적용한다.
    -- DEFAULT는 1
    SELECT COALESCE(MIN(ei.payment_rate), 1.00)
    INTO v_payment_rate
    FROM event_schedule es
    JOIN event_info ei
      ON es.event_type = ei.event_type
    WHERE es.pc_cafe_id = p_pc_cafe_id
      AND ei.event_type_num = 1
      AND CURDATE() BETWEEN es.event_start_date AND es.event_end_date;

    -- 회원 충전이면 현재 회원 등급의 할인율을 가져온다.
    -- 회원이 아니면 기본값 0.00이 유지된다.
    -- 회원 ID가 잘못된 경우는 아래 pc_member UPDATE에서 잡는다.
    IF p_member_id IS NOT NULL THEN
        SELECT COALESCE(MAX(g.benefit), 0.00)
        INTO v_benefit
        FROM pc_member pm
        JOIN grade g
          ON pm.grade_type = g.grade_type
        WHERE pm.member_id = p_member_id;
    END IF;


 
    
     -- 최종 결제 금액 먼저 계산 (payment_rate이 decimal을 잘라서 정확한 계산이 안됌)
     --  실제 가격 * 결제 비율 * 등급 할인(1-benefit) 
		SET v_charge_pay_amount = ROUND(v_ticket_price * v_payment_rate * (1 - v_benefit));

		-- 그 다음 저장용 payment_rate 계산
		SET v_payment_rate = v_payment_rate * (1 - v_benefit);


    -- 충전 이력 저장
    -- charged_at은 DEFAULT CURRENT_TIMESTAMP이므로 직접 넣지 않는다.
    INSERT INTO charge
    (pc_cafe_id, seat_num, ticket_time, member_id, charge_pay_amount, payment_rate)
    VALUES
    (p_pc_cafe_id, p_seat_num, p_ticket_time, p_member_id, v_charge_pay_amount, v_payment_rate);

    -- 지점 총 매출 누적
    UPDATE pc_cafe
    SET total_sales = total_sales + v_charge_pay_amount
    WHERE pc_cafe_id = p_pc_cafe_id;

    -- 현재 이용 중인 '손님'의 기준 잔여시간 증가
    UPDATE customer
    SET remain_time = remain_time + p_ticket_time
    WHERE pc_cafe_id = p_pc_cafe_id
      AND seat_num = p_seat_num;

    IF ROW_COUNT() = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = '이용 중인 손님 정보가 없습니다.';
    END IF;

    -- 회원이면 총 결제 금액과 등급 갱신
    IF p_member_id IS NOT NULL THEN
        UPDATE pc_member
        SET total_payment_amount = total_payment_amount + v_charge_pay_amount
        WHERE member_id = p_member_id;

        IF ROW_COUNT() = 0 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = '회원 정보가 없습니다.';
        END IF;
			-- 총 결제 금액 조회
        SELECT total_payment_amount
        INTO v_total_payment
        FROM pc_member
        WHERE member_id = p_member_id;

        -- 총 결제 금액 이하의 grade_standard 중 가장 높은 등급을 선택
        SELECT grade_type
        INTO v_new_grade
        FROM grade
        WHERE grade_standard <= v_total_payment
        ORDER BY grade_standard DESC
        LIMIT 1;
			-- 등급 갱신 
        UPDATE pc_member
        SET grade_type = v_new_grade
        WHERE member_id = p_member_id;
    END IF;

    COMMIT;
END //

DELIMITER ;