package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.UserDAO;
import model.User;

//PC_MemberDAOImple의 기능들 상속 받음
public class UserDAOImpl extends PC_MemberDAOImpl implements UserDAO {
	
	private Connection conn;

	public UserDAOImpl(Connection conn) {
		super(conn);
		this.conn = conn;
	}

	//회원 가입
	@Override
	public void insertUser(User user) {
		//admin은 회원가입x, member_type은 user로 고정
		String sql = "INSERT INTO pc_member (member_id, member_password, member_name, remain_time, grade_type, total_payment_amount, member_type) VALUES (?, ?, ?, ?, ?, ?, 'user')";
		int result = -1;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, user.getMemberId());
			pstmt.setString(2, user.getMemberPassword());
			pstmt.setString(3, user.getMemberName());
			pstmt.setInt(4, user.getRemainingTime());       //초기 가입 시 0
			pstmt.setString(5, user.getGradeType());        //초기 가입 시 기본 등급
			pstmt.setInt(6, user.getTotalPaymentAmount());  //초기 가입 시 0
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("회원 가입 중 오류 발생");
		}
		
		if (result > 0) {
			System.out.printf("회원 가입이 완료되었습니다. 환영합니다, %s님!\n", user.getMemberName());
		} else {
			System.out.println("회원 가입 실패: 입력하신 정보를 다시 확인해 주세요.");
		}
	}

	//잔여 시간 조회
	@Override
	public int getRemainingTime(String memberId) {
		String sql = "SELECT remain_time FROM pc_member WHERE member_id = ?";
		int remainTime = 0;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					remainTime = rs.getInt("remain_time");
				} else {
					System.out.println("잔여 시간 조회 실패: 해당 아이디가 존재하지 않습니다.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("잔여 시간 조회 중 오류 발생");
		}
		
		return remainTime;
	}

	//잔여 시간 갱신 - 이용권 충전시 호출하는 함수
	@Override
	public void updateRemainingTime(String memberId, int time) {
		String sql = "UPDATE pc_member SET remain_time = remain_time + ? WHERE member_id = ?";
		int result = -1;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, time);
			pstmt.setString(2, memberId);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("잔여 시간 갱신 중 오류 발생");
		}
		
		if (result > 0) {
			System.out.printf("잔여 시간이 갱신되었습니다. 회원 ID: %s\n", memberId);
		} else {
			System.out.println("잔여 시간 갱신 실패: 해당 아이디가 존재하지 않습니다.");
		}
	}

	//총 결제 금액 누적 - 이용권 충전시 호출하는 함수
	@Override
	public void addTotalPayment(String memberId, int amount) {
		String sql = "UPDATE pc_member SET total_payment_amount = total_payment_amount + ? WHERE member_id = ?";
		int result = -1;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, amount);
			pstmt.setString(2, memberId);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("결제 금액 누적 중 오류 발생");
		}
		
		if (result > 0) {
			System.out.printf("결제 금액이 누적되었습니다. 회원 ID: %s\n", memberId );
		} else {
			System.out.println("결제 금액 누적 실패: 해당 아이디가 존재하지 않습니다.");
		}
	}

	//회원 등급 갱신 - 이용권 충전시 호출하는 함수
	@Override
	public void updateUserGrade(String memberId, String gradeType) {
		String sql = "UPDATE pc_member SET grade_type = ? WHERE member_id = ?";
		int result = -1;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, gradeType);
			pstmt.setString(2, memberId);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("회원 등급 갱신 중 오류 발생");
		}
		
		if (result > 0) {
			System.out.printf("회원 등급이 '%s'(으)로 갱신되었습니다. 회원 ID: %s\n", gradeType, memberId);
		} else {
			System.out.println("회원 등급 갱신 실패: 해당 아이디가 존재하지 않습니다.");
		}
	}
}