package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PC_MemberDAO;
import model.PC_Member;

public class PC_MemberDAOImpl implements PC_MemberDAO{
	
	private Connection conn;

    public PC_MemberDAOImpl(Connection conn) {
        this.conn = conn;
    }

    //회원 삽입 기능 - user만 회원 가입 가능, owner는 불가능, type = user 고정
	@Override
    public void insertMember(PC_Member member) {
        String sql = "INSERT INTO pc_member (member_id, member_password, member_name, remain_time, grade_type, total_payment_amount, member_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getMemberPassword());
            pstmt.setString(3, member.getMemberName());
            pstmt.setInt(4, member.getRemainTime());
            pstmt.setString(5, member.getGradeType());
            pstmt.setInt(6, member.getTotalPaymentAmount());
            pstmt.setString(7, member.getMemberType()); // 무조건 'user'로 들어오게 Service에서 통제

            pstmt.executeUpdate();
              
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	 
	//회원 정보 수정 기능 (이름, 비밀번호만)
	@Override
	public void updateMember(PC_Member member) {
		// TODO Auto-generated method stub
		String sql = "UPDATE pc_member SET member_password = ?, member_name = ? WHERE member_id = ?";
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, member.getMemberPassword());
			pstmt.setString(2, member.getMemberName());
			pstmt.setString(3, member.getMemberId());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//회원 삭제
	@Override
	public void deleteMember(PC_Member member) {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM pc_member WHERE member_id = ?";
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, member.getMemberId());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	//id로 회원 조회
	@Override
	public PC_Member findByID(String memberId) {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM pc_member WHERE member_id = ?";
		PC_Member member = null;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);

			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					member = mapRowToMember(rs);
				}	
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return member;
	}
	
	//전체 회원 조회
	@Override
	public List<PC_Member> findAll() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM pc_member";
		List<PC_Member> members = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				members.add(mapRowToMember(rs));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return members;
	}
	
	//잔여 시간 조회
	@Override
	public int getRemainTime(String memberId) {
		String sql = "SELECT remain_time FROM pc_member WHERE member_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("remain_time");
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("잔여 시간 조회 중 오류 발생");
		}
		
		return 0;
	}

	//잔여 시간 갱신 - 이용권 충전시 호출하는 함수
	@Override
	public void updateRemainTime(String memberId, int time) {
		//누적 시간으로 계산함
		String sql = "UPDATE pc_member SET remain_time = remain_time + ? WHERE member_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, time);
			pstmt.setString(2, memberId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	//총 결제 금액 누적 - 이용권 충전시 호출하는 함수
	@Override
	public void addTotalPayment(String memberId, int amount) {
		//누적 합으로 계산함
		String sql = "UPDATE pc_member SET total_payment_amount = total_payment_amount + ? WHERE member_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, amount);
			pstmt.setString(2, memberId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//회원 등급 갱신 - 이용권 충전시 호출하는 함수
	@Override
	public void updateUserGrade(String memberId, String gradeType) {
		String sql = "UPDATE pc_member SET grade_type = ? WHERE member_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, gradeType);
			pstmt.setString(2, memberId);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	private PC_Member mapRowToMember(ResultSet rs) throws SQLException {
		return new PC_Member(
			rs.getString("member_id"),
			rs.getString("member_password"),
			rs.getString("member_name"),
			rs.getInt("remain_time"),
			rs.getString("grade_type"),
			rs.getInt("total_payment_amount"),
			rs.getString("member_type")
		);
	}



}
