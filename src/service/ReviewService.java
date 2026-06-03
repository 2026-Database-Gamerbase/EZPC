package service;

import java.util.List;

import dao.ReviewDAO;
import model.PcCafeReviewGradeReport;
import model.Review;

public class ReviewService {
	private ReviewDAO reviewDao;
	
	public ReviewService(ReviewDAO reviewDao) {
		this.reviewDao = reviewDao;
	}
	
	//회원 기능 - 리뷰 작성, 리뷰 수정, 리뷰 삭제, 본인이 작성한 리뷰 목록 보기(checkReviewByMemberId)
	
	//리뷰 작성하기, 매개변수로 리뷰 객체를 전달함 (회원 아이디, 리뷰 아이디(회원별로 생성됨), pc방 번호, 별점, 리뷰 제목, 리뷰 내용)
	public void writeReview(Review review) {
		reviewDao.insertReview(review);
	}
	
	//리뷰 수정하기
	public void editReview(Review review) {
		reviewDao.updateReview(review);
		
	}
	
	//리뷰 삭제하기
	public void deleteReview(Review review) {
		reviewDao.deleteReview(review);
	}
	
	
	//운영자 기능 - 회원별 리뷰 목록 조회, pc방 별 리뷰 조회, 전체 리뷰 조회, 리뷰 기반 등급 조회
	
	//특정 회원이 작성한 리뷰 목록 보기
	//매개변수로 회원아이디 전달하면 해당 회원이 작성한 리뷰 리스트 List<Review> 리턴
	public List<Review> checkReviewByMemberId(String memeberId) {
		return reviewDao.findById(memeberId);
	}
	
	//특정 pc방의 리뷰 목록 보기
	//매개변수로 pc방 아이디를 전달하면 해당 pc방에 작성된 리뷰 리스트 List<Review> 리턴
	public List<Review> checkReviewByPcCafeId(String pcCafeId){
		return reviewDao.findByPcCafeId(pcCafeId);
	}
	
	//체인 pc방 전체 리뷰 목록 보기
	//이거 4000개의 리뷰 리턴하니 조심..
	public List<Review> checkAllReivew(){
		return reviewDao.findAll();
	}
	
	//리뷰를 기반으로 pc방 1-4등급으로 나눈 리포트 보기
	//pc방 번호, pc방 이름, 평균 별점, 해당 pc방의 리뷰 개수, 리뷰에 따른 등급을 필드로 가진 객체를 원소로하는 리스트를 리턴함
	//필드 명: pcCafeId, pcCafeName, avgStarRating, reviewCount, pcCafeGrade
	public List<PcCafeReviewGradeReport> checkPcCafeGradeByReview(){
		return reviewDao.getPcCafeReviewGradeReport();
	}
		
}
