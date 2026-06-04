CREATE DATABASE IF NOT EXISTS EZPC;
USE EZPC;


-- ** 다른 테이블을 참조하는 외래키(FK)가 있다면,
-- 참조 대상이 되는 부모 테이블이 무조건 먼저 생성되어야 함


-- 1. PC방
CREATE TABLE pc_cafe (
    pc_cafe_id VARCHAR(30) NOT NULL,
    -- 지점(name)
    pc_cafe_name VARCHAR(100) NOT NULL,

    -- 별점은 최대 0 - 5점까지 가능
    -- DECIMAL(2,1): 전체 2자리, 소수점 아래 1자리
    average_star_rating DECIMAL(2,1) DEFAULT 0.0
        CHECK (average_star_rating >= 0.0 AND average_star_rating <= 5.0),

    -- PC방 전체 매출: 음식 주문 매출 + 충전 매출
    total_sales INT DEFAULT 0,

    total_seats INT NOT NULL
	 	 CHECK (total_seats >= 0),

    PRIMARY KEY (pc_cafe_id)
);


-- 2. 등급
CREATE TABLE grade (
    grade_type VARCHAR(30) NOT NULL,
     benefit DECIMAL(3,2) NOT NULL
        CHECK (benefit >= 0.00 AND benefit <= 1.00),
        -- 0.00 ~ 1.00 사이
    -- 등급 기준: 총 결제 금액 기준
    grade_standard INT NOT NULL
    	CHECK (grade_standard >= 0),


    PRIMARY KEY (grade_type)
);


-- 3. 음식
CREATE TABLE food (
    food_name VARCHAR(50) NOT NULL,
    price INT NOT NULL
	 CHECK (price >= 0),

    PRIMARY KEY (food_name)
);


-- 4. 이용권
CREATE TABLE ticket (
    ticket_time INT NOT NULL,
    price INT NOT NULL
	 	CHECK (price >= 0),

    PRIMARY KEY (ticket_time)
);


-- 5. 이벤트 정보
-- 이벤트 종류와 이벤트 내용을 저장하는 부모 테이블
CREATE TABLE event_info (
    event_type VARCHAR(50) NOT NULL,
    event_content VARCHAR(255),
    
    event_type_num INT,
    -- 주문:0 , 충전:1
    payment_rate DECIMAL(3,2) NOT NULL DEFAULT 1.00
	 CHECK (payment_rate BETWEEN 0.00 AND 1.00),
	 
    -- 실제 결제비율(할인율)

    PRIMARY KEY (event_type)
);


-- 6. 이벤트 일정
-- 어떤 PC방에서 어떤 이벤트를 언제부터 언제까지 진행하는지 저장
-- event_schedule은 event_info와 pc_cafe를 참조하는 관계 테이블
CREATE TABLE event_schedule (
    event_type VARCHAR(50) NOT NULL,
    pc_cafe_id VARCHAR(30) NOT NULL,
    event_start_date DATE NOT NULL,
    event_end_date DATE NOT NULL,

    -- 같은 이벤트를 같은 PC방에서 여러 번 진행할 수 있으니 PK가 3개
    PRIMARY KEY (event_type, pc_cafe_id, event_start_date),

    FOREIGN KEY (event_type)
        REFERENCES event_info(event_type)
		  ON UPDATE CASCADE
		  ON DELETE CASCADE,

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
		  ON UPDATE CASCADE
		  ON DELETE CASCADE,
        
   
   CHECK (event_start_date <= event_end_date)
);


-- 7. 회원
CREATE TABLE pc_member (
    member_id VARCHAR(50) NOT NULL,
    member_password VARCHAR(100) NOT NULL,
    member_name VARCHAR(50),
    remain_time INT DEFAULT 0,
    grade_type VARCHAR(30),
    total_payment_amount INT DEFAULT 0,
    member_type VARCHAR(30) NOT NULL,
    -- 운영자인지, 사용자인지

    PRIMARY KEY (member_id),

    FOREIGN KEY (grade_type)
        REFERENCES grade(grade_type)
        ON UPDATE CASCADE
        ON DELETE SET NULL 
);


-- 8. 직원
CREATE TABLE employee (
    employee_id INT AUTO_INCREMENT,
    employee_name VARCHAR(50) NOT NULL,
    pc_cafe_id VARCHAR(30) NOT NULL,
    employee_position VARCHAR(30) NOT NULL,
    hour_wage INT NOT NULL
	 	CHECK (hour_wage >= 0),
    is_currently_working BOOLEAN DEFAULT FALSE,

    PRIMARY KEY (employee_id),

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- 9. 손님
-- 현재 이용 중인 좌석/손님만 저장
CREATE TABLE customer (
    pc_cafe_id VARCHAR(30) NULL,
    seat_num INT NOT NULL
	 	CHECK (seat_num >= 0),
    member_id VARCHAR(50) NULL,
    remain_time INT NOT NULL,

    PRIMARY KEY (pc_cafe_id, seat_num),

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
		  ON UPDATE CASCADE
		  ON DELETE CASCADE,

    FOREIGN KEY (member_id)
        REFERENCES pc_member(member_id)
        ON DELETE SET NULL
);

-- ON DELETE SET NULL:
-- 부모 테이블의 데이터가 삭제되었을 때,
-- 그 데이터를 참조하고 있던 자식 테이블의 외래키(FK) 값을 자동으로 NULL로 바꿔주는 설정


-- 10. 재고
CREATE TABLE stock (
    pc_cafe_id VARCHAR(30) NOT NULL,
    food_name VARCHAR(50) NOT NULL,
    stock_quantity INT DEFAULT 0,

    PRIMARY KEY (pc_cafe_id, food_name),

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
		  ON UPDATE CASCADE
		  ON DELETE CASCADE,

    FOREIGN KEY (food_name)
        REFERENCES food(food_name)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


-- 11. 주문
-- 주문/음식 거래 기록
-- customer는 동적 테이블이므로 직접 FK로 참조하지 않음
-- 주문 금액은 회원 총 결제 금액에는 포함하지 않음
CREATE TABLE food_order (
    order_id INT NOT NULL,
    food_name VARCHAR(50) NOT NULL,
    pc_cafe_id VARCHAR(30) NULL,
    seat_num INT NOT NULL
	 	CHECK (seat_num >= 0),
    food_quantity INT NOT NULL
	 	 CHECK (food_quantity > 0),

    -- 해당 음식 항목의 결제 금액
    -- 같은 order_id를 가진 food_pay_amount를 SUM하면 전체 주문 금액
    food_pay_amount INT NOT NULL,
    
    payment_rate DECIMAL(3,2) NOT NULL DEFAULT 1.00
	 CHECK (payment_rate BETWEEN 0.00 AND 1.00),
	 
    -- 실제 결제비율(할인율)

    -- 주문 발생 시간
    ordered_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    
    

    PRIMARY KEY (order_id, food_name),

    FOREIGN KEY (food_name)
        REFERENCES food(food_name)
		  ON UPDATE CASCADE
		  ON DELETE CASCADE,

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL 
);


-- 12. 충전
-- 충전 거래 기록
-- customer는 동적 테이블이므로 직접 FK로 참조하지 않음
CREATE TABLE charge (
    charge_id INT AUTO_INCREMENT,
    pc_cafe_id VARCHAR(30) NULL,
    seat_num INT NOT NULL
	 CHECK (seat_num >= 0),
    ticket_time INT NULL,
    member_id VARCHAR(50) NULL,
    charge_pay_amount INT NOT NULL,
    
    payment_rate DECIMAL(3,2) NOT NULL DEFAULT 1.00
	 	CHECK (payment_rate BETWEEN 0.00 AND 1.00),
	 	
   -- 실제 결제비율(할인율)


    -- 충전 발생 시간
    charged_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (charge_id),

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
		  ON UPDATE CASCADE
		  ON DELETE SET NULL,

    FOREIGN KEY (ticket_time)
        REFERENCES ticket(ticket_time)
		  ON UPDATE CASCADE
		  ON DELETE SET NULL,

    FOREIGN KEY (member_id)
        REFERENCES pc_member(member_id)
        ON DELETE SET NULL
);


-- 13. 리뷰
-- 리뷰는 회원에 의존하는 형태
-- review_id는 회원별 리뷰번호
CREATE TABLE review (
    member_id VARCHAR(50) NOT NULL,
    review_id INT NOT NULL,
    pc_cafe_id VARCHAR(30) NOT NULL,

    -- 리뷰 별점도 0 - 5점까지만 가능
    star_rating DECIMAL(2,1) NOT NULL
        CHECK (star_rating >= 0.0 AND star_rating <= 5.0),

    review_title VARCHAR(100),
    review_content TEXT,

    PRIMARY KEY (member_id, review_id),

    FOREIGN KEY (member_id)
        REFERENCES pc_member(member_id)
        ON DELETE CASCADE,
        -- CASCADE:
        -- 부모 데이터가 삭제되면, 그걸 참조하던 자식 데이터도 같이 삭제

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE 
);


-- 14. 로그
-- 입실/퇴실 기록
-- customer는 동적 테이블이므로 직접 FK로 참조하지 않음
CREATE TABLE use_log (
    log_id INT AUTO_INCREMENT,
    pc_cafe_id VARCHAR(30) NULL,
    seat_num INT NOT NULL,
    member_id VARCHAR(50) NULL,

    -- 입실 시간
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 퇴실 전에는 NULL, 퇴실 시 UPDATE
    logout_time DATETIME NULL,

    PRIMARY KEY (log_id),

    FOREIGN KEY (pc_cafe_id)
        REFERENCES pc_cafe(pc_cafe_id)
		  ON UPDATE CASCADE
		  ON DELETE SET NULL,

    FOREIGN KEY (member_id)
        REFERENCES pc_member(member_id)
        ON DELETE SET NULL,
        
    CHECK (logout_time IS NULL OR login_time <= logout_time)
);