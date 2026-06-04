USE EZPC;
DELIMITER //

-- 음식 주문시 pc_cafe의 total_sales 자동 누적 갱신

-- 음식 주문 시 (food_order INSERT 발생)
CREATE TRIGGER trigger_total_sales_after_food_order
AFTER INSERT ON food_order
FOR EACH ROW
BEGIN
    UPDATE pc_cafe
    SET total_sales = total_sales + NEW.food_pay_amount
    WHERE pc_cafe_id = NEW.pc_cafe_id;
END //
DELIMITER ;