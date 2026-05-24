package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ReviewDAO;
import model.Customer;
import model.Review;

public class ReviewDAOImpl implements ReviewDAO {
	
	private Connection conn;
	
	public ReviewDAOImpl (Connection conn) {
		this.conn = conn; ////db와 연결된 물리적인 통로(세션) 관리
	}
	
	
	//리뷰 작성하기
	@Override
	public void insertReview(Review review) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO REVIEW (member_id, review_id, pc_cafe_id, star_rating, review_title, review_content) VALUES (?, ?, ?, ?, ?, ?)";
		int nextReviewId = getNextReviewId(review.getMemberId()); //회원별로 리뷰 번호가 적용되므로 해당 회원의 다음 리뷰 번호를 얻기 위한 함수 호출
		
		//try-with-resources 방식, 자동으로 리소스가 해제됨
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){ //pstmt: 컴파일된 sql문을 담고 있는 객체, ?에 값을 바인딩하고 쿼리를 실행하는 역할
			pstmt.setString(1, review.getMemberId());
			pstmt.setInt(2, nextReviewId);
			pstmt.setString(3, review.getPcCafeId());
			pstmt.setBigDecimal(4, review.getStarRating());
			pstmt.setString(5, review.getReviewTitle());
			pstmt.setString(6, review.getReviewContent());
			
			pstmt.executeUpdate();
			
			System.out.println("리뷰 저장 성공");
			showReview(review); //저장된 리뷰 보여줌
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("리뷰 저장 중 오류 발생");
		}
		
	}
	
	//리뷰 수정하기
	@Override
	public void updateReview(Review review) {
		// TODO Auto-generated method stub
		//본인이 작성한 리뷰만 수정 가능
		String sql = "UPDATE review SET review_title = ?, review_content = ?, star_rating = ? WHERE member_id = ? AND review_id = ?";
		int result = -1;
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, review.getReviewTitle());
			pstmt.setString(2, review.getReviewContent());
			pstmt.setBigDecimal(3, review.getStarRating());
			pstmt.setString(4, review.getMemberId());
			pstmt.setInt(5, review.getReviewId());
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("리뷰 수정 중 오류 발생");
		}
		
		if (result > 0) {
		    System.out.printf("리뷰가 수정되었습니다. 수정된 리뷰의 회원 ID: %s, 리뷰 번호: %d\n", review.getMemberId(), review.getReviewId());
		} else {
		    System.out.println("리뷰 수정 실패: 수정할 리뷰가 존재하지 않습니다.");
		}
		
	}
	
	//리뷰 삭제하기
	@Override
	public void deleteReview(Review review) {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM review WHERE member_id = ? AND review_id = ?";
		int result = -1;
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, review.getMemberId());
			pstmt.setInt(2, review.getReviewId());
			
			//쿼리 실행 결과 테이블의 행 개수를 받음
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("리뷰 삭제 중 오류 발생");
		}
		
		if (result > 0) {
			System.out.printf("리뷰가 성공적으로 삭제되었습니다. 삭제된 리뷰의 회원 ID: %s, 리뷰 번호: %d\n", review.getMemberId(), review.getReviewId());
		}
		else {
			System.out.println("리뷰 삭제 실패: 삭제할 리뷰가 존재하지 않습니다");
		}
	}
	
	//특정 회원이 작성한 리뷰 조회
	@Override
	public List<Review> findById(String memberId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM review WHERE member_id = ?";
		List<Review> reviews = new ArrayList<>();
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, memberId);
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()){
					reviews.add(mapRowToReview(rs));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("특정 회원이 작성한 리뷰 조회 중 오류 발생");
		}
		
		return reviews;
	}
	
	
	//특정 PC방의 전체 리뷰 조회
	@Override
	public List<Review> findByPcCafeId(String pcCafeId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM Review WHERE pc_cafe_id = ?";
		List<Review> reviews = new ArrayList<>();
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, pcCafeId);
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()){
					reviews.add(mapRowToReview(rs));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("특정 pc방의 리뷰 조회 중 오류 발생");
		}
		
		return reviews;
	}
	
	
	//전체 리뷰 찾기
	@Override
	public List<Review> findAll() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM review";
		List<Review> reviews = new ArrayList<>();
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()){
					reviews.add(mapRowToReview(rs));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("전체 리뷰 조회 중 오류 발생");
		}
		
		return reviews;
	}
	
	//특정 리뷰 보여주기
	@Override
	public void showReview(Review review){ 
		// TODO Auto-generated method stub
		String pcCafeName = getPcCafeName(review.getPcCafeId());
		
		double rating = review.getStarRating().doubleValue(); //demical -> double
		int fullStar = review.getStarRating().intValue(); //리뷰의 정수 부분, 꽉찬 별
		
		System.out.println("====================================");
		System.out.printf("pc방 지점: %s ", pcCafeName);
		
		System.out.printf("별점: %.1f ", rating);
		for (int i = 0; i < fullStar; i++) {
			System.out.print("★");
		}
		
		if (rating - fullStar >= 0.5) { //리뷰의 소수부가 0.5 이상일 경우 반쪽 별
			System.out.print("☆");
		}
		
		System.out.printf("\n리뷰 제목: %s \n", review.getReviewTitle());
		System.out.printf("회원 ID: %s \n", review.getMemberId());
		System.out.printf("리뷰 내용: %s \n", review.getReviewContent());
		
	}
	
	//회원별로 리뷰 번호가 적용되므로 해당 회원의 다음 리뷰 번호를 얻기 위한 함수
	private int getNextReviewId(String memberId) {
		// TODO Auto-generated method stub
		
		//현재 회원이 작성한 리뷰의 번호보다 + 1 한 값이 다음 리뷰 번호
		String sql = "SELECT IFNULL(MAX(review_id), 0) + 1 FROM review WHERE member_id = ?";
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, memberId);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("회원별 리뷰 번호를 가져오는 중에 오류 발생");
		}
		return -1;
	}
	
	private String getPcCafeName(String pcCafeId) {
		// TODO Auto-generated method stub
		String sql = "SELECT pc_cafe_name FROM pc_cafe WHERE pc_cafe_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, pcCafeId);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
					return rs.getString(1);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("pc방 번호로 pc방 이름을 가져오는 중 오류 발생");
		}
		
		return null;
	}
	
	//ResultSet 데이터를 Review 객체로 매핑
	private Review mapRowToReview(ResultSet rs) throws SQLException {
		Review r = new Review();
		r.setMemberId(rs.getString("member_id"));
		r.setPcCafeId(rs.getString("pc_cafe_id"));
		r.setReviewId(rs.getInt("review_id"));
		r.setStarRating(rs.getBigDecimal("star_rating"));
		r.setReviewTitle(rs.getString("review_title"));
		r.setReviewContent(rs.getString("review_content"));
		return r;
	}

}
