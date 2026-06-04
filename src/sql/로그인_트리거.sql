DELIMITER //

CREATE TRIGGER trigger_login_log
AFTER INSERT ON customer
FOR EACH ROW
BEGIN
    INSERT INTO use_log (
        pc_cafe_id,
        seat_num,
        member_id
    )
    VALUES (
        NEW.pc_cafe_id,
        NEW.seat_num,
        NEW.member_id
    );
END //

DELIMITER ;