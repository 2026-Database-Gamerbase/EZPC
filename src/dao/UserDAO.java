package dao;

import model.PC_Member;

public interface UserDAO extends PC_MemberDAO {
	
	// 오버라이딩, 회원 가입, type을 user로 고정
	void insertMember(PC_Member member);
}