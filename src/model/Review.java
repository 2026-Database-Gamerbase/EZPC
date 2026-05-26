	package model;

import java.math.BigDecimal;

public class Review { //리뷰 테이블
	private String memberId; //회원아이디 기본키, 외래키
	private int reviewId; //리뷰 번호 기본키, 식별자
	private String pcCafeId; //pc방 번호, 외래키
	private double starRating; //평균 평점
	private String reviewTitle; //리뷰 제목
	private String reviewContent; //리뷰 내용
	
	public Review() {} //기본 생성자

	public Review(String memberId, int reviewId, String pcCafeId, double starRating, String reviewTitle,
			String reviewContent) {
		super();
		this.memberId = memberId;
		this.reviewId = reviewId;
		this.pcCafeId = pcCafeId;
		this.starRating = starRating;
		this.reviewTitle = reviewTitle;
		this.reviewContent = reviewContent;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getPcCafeId() {
		return pcCafeId;
	}

	public void setPcCafeId(String pcCafeId) {
		this.pcCafeId = pcCafeId;
	}

	public double getStarRating() {
		return starRating;
	}

	public void setStarRating(double starRating) {
		this.starRating = starRating;
	}

	public String getReviewTitle() {
		return reviewTitle;
	}

	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	
	
	
	
}