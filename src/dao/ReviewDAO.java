package dao;

import java.util.List;

import model.PC_Member;
import model.Review;

public interface ReviewDAO {
	
	//리뷰 작성
	void insertReview(Review review);
	
	//리뷰 수정
	void updateReview(Review review);
	
	//리뷰 삭제
	void deleteReview(Review review);
	
	//특정 리뷰를 보여주는 기능
	void showReview(Review review); 
	
	//특정 회원이 작성한 리뷰 목록 보기
	List<Review> findById(String memberId); 
	
	//특정 pc방의 리뷰 보기
	List<Review> findByPcCafeId(String pcCafeId);
	
	//전체 리뷰 보기
	List<Review> findAll(); 
	

}
