package dao;

import model.PC_Member;

public interface PC_MemberDAO {
	// admin과 user의 공통 기능
	
	//로그인
	PC_Member login(String memberId, String password);
	
	//회원 정보 수정
	void updateMember(PC_Member member);
	
	//회원 삭제
	void deleteMember(PC_Member member);
	
	//회원 조회
	PC_Member findByID(String memberId);
	
	//특정 회원 정보 보여주기
	void showMember(PC_Member member);
	
}
