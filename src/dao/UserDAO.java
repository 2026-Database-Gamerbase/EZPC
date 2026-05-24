package dao;

import model.User;

public interface UserDAO extends PC_MemberDAO {
	
	//회원 가입
	void insertUser(User user);
	
	//잔여 시간 조회
	int getRemainingTime(String memberId);
	
	//잔여 시간 갱신 - 이용권 충전시 호출하는 함수
	void updateRemainingTime(String memberId, int time);
	
	//총 결제 금액 누적 - 이용권 충전시 호출하는 함수
	void addTotalPayment(String memberId, int amount);
	
	//회원 등급 갱신 - - 이용권 충전시 호출하는 함수
	void updateUserGrade(String memberId, String gradeType);
}