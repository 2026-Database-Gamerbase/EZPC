package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.PC_MemberDAO;
import model.PC_Member;

public class PC_MemberDAOImpl implements PC_MemberDAO{
	
	private Connection conn;

    public PC_MemberDAOImpl(Connection conn) {
        this.conn = conn;
    }

    //로그인 기능
	@Override
	public PC_Member login(String memberId, String password) {
		// TODO Auto-generated method stub
		String sql = "SELECT * from pc_member WHERE member_id = ? AND member_password = ?";
		PC_Member member = null;
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, memberId);
			pstmt.setString(2, password);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					member = new PC_Member(rs.getString("member_id"), rs.getString("member_password"), rs.getString("member_name")
							, rs.getInt("remain_time"), rs.getString("grade_type"), rs.getInt("total_payment_amount"), rs.getString("member_type"));
				}
				else {
					System.out.println("로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
					return member;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("로그인 중 오류 발생");
		}
		
		if (member != null) {
		    System.out.printf("로그인에 성공했습니다. 현재 회원 ID : %s\n", memberId);
		}
		
		return member;
	}
	
	 
	//회원 정보 수정 기능 (이름, 비밀번호만)
	@Override
	public void updateMember(PC_Member member) {
		// TODO Auto-generated method stub
		String sql = "UPDATE pc_member SET member_password = ?, member_name = ? WHERE member_id = ?";
		int result = -1;
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, member.getMemberPassword());
			pstmt.setString(2, member.getMemberName());
			pstmt.setString(3, member.getMemberId());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("회원 정보 수정 중 오류 발생");
		}

		if (result > 0) {
		    System.out.printf("회원 정보가 수정되었습니다. 수정된 회원의 ID: %s\n", member.getMemberId());
		} else {
		    System.out.println("회원 정보 수정 실패: 해당 아이디가 존재하지 않습니다.");
		}
	}

	//회원 삭제
	@Override
	public void deleteMember(PC_Member member) {
		// TODO Auto-generated method stub
		String sql = "DELETE FROM pc_member WHERE member_id = ?";
		int result = -1;
		
		try(PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, member.getMemberId());
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("회원 삭제 중 오류 발생");
		}
		
		if(result > 0) {
			System.out.printf("회원이 삭제되었습니다. 삭제된 회원의 ID: %s\n", member.getMemberId());
		}
		else {
			System.out.println("회원 삭제 실패: 해당 회원이 존재하지 않습니다.");
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
					member = new PC_Member(rs.getString("member_id"), rs.getString("member_password"), rs.getString("member_name")
							, rs.getInt("remain_time"), rs.getString("grade_type"), rs.getInt("total_payment_amount"), rs.getString("member_type"));
				}
				else {
					System.out.println("회원 조회 실패: 아이디를 확인하세요.");
					return member;
				}
			}
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원 정보 조회 중 오류 발생");
        }

        return member;
	}

	@Override
	//회원 정보 보여주기
	public void showMember(PC_Member member) {
		// TODO Auto-generated method stub
		System.out.println("====================================");
		System.out.printf("회원 ID: %s 회원 이름: %s 회원 종류: %s\n", member.getMemberId(), member.getMemberName(), member.getMemberType());
		
		//운영자 회원이면 더 이상 정보를 보여주지 않음
		if (member.getMemberType().equals("owner")) {
			return;
		}
		
		System.out.printf("잔여시간: %d\n", member.getRemainingTime());
		System.out.printf("등급: %s\n", member.getGradeType());
		System.out.printf("총 결제 금액: %d\n", member.getTotalPaymentAmount());
		
	}

}
