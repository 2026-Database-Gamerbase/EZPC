USE EZPC;
DELIMITER //


-- pc_cafe 테이블의 average_star_rating 자동 갱신하기
-- review 테이블에 변경이 생길 때마다 해당 지점의 평균 별점을 재계산하여 pc_cafe에 반영
-- 리뷰 작성 시
CREATE TRIGGER trigger_avg_rating_after_insert
AFTER INSERT ON review
FOR EACH ROW
BEGIN
    UPDATE pc_cafe
    SET average_star_rating = (
        SELECT ROUND(AVG(star_rating), 1)
        FROM review
        WHERE pc_cafe_id = NEW.pc_cafe_id
    )
    WHERE pc_cafe_id = NEW.pc_cafe_id;
END //

-- 리뷰 수정 시
CREATE TRIGGER trigger_avg_rating_after_update
AFTER UPDATE ON review
FOR EACH ROW
BEGIN
    UPDATE pc_cafe
    SET average_star_rating = (
        SELECT ROUND(AVG(star_rating), 1)
        FROM review
        WHERE pc_cafe_id = NEW.pc_cafe_id
    )
    WHERE pc_cafe_id = NEW.pc_cafe_id;
END //

-- 리뷰 삭제 시 (리뷰가 0개면 0.0으로 초기화)
CREATE TRIGGER trigger_avg_rating_after_delete
AFTER DELETE ON review
FOR EACH ROW
BEGIN
    UPDATE pc_cafe
    SET average_star_rating = COALESCE(
        (SELECT ROUND(AVG(star_rating), 1)
         FROM review
         WHERE pc_cafe_id = OLD.pc_cafe_id),
        0.0
    )
    WHERE pc_cafe_id = OLD.pc_cafe_id;
END //


DELIMITER ;