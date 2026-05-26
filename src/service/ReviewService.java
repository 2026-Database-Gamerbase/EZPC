package service;

import dao.ReviewDAO;

public class ReviewService {
	private ReviewDAO reviewDao;
	
	public ReviewService(ReviewDAO reviewDao) {
		this.reviewDao = reviewDao;
	}
}
