package dao;

import java.util.List;
import model.PC_Member;

public interface PC_MemberDAO {
	
	//회원 삽입, 회원 가입 기능으로 연결
	void insertMember(PC_Member memeber);
	
	//회원 정보 수정 (이름, 비밀번호만)
	void updateMember(PC_Member member);
	
	//회원 삭제
	void deleteMember(PC_Member member);
	
	//특정 회원 조회, 로그인 기능으로 연결
	PC_Member findByID(String memberId);
	
	//회원 전체 조회
	List<PC_Member> findAll();
	
	//잔여 시간 조회
	int getRemainTime(String memberId);
	
	//잔여 시간 갱신 - 이용권 충전시 호출하는 함수
	void updateRemainTime(String memberId, int time);
	
	//총 결제 금액 누적 - 이용권 충전시 호출하는 함수
	void addTotalPayment(String memberId, int amount);
	
	//회원 등급 갱신 - 등급 조건 충족 시 호출
	void updateUserGrade(String memberId, String gradeType);
	
	//로그아웃 시 회원의 잔여 시간 갱신 
	void updateRemainTimeAfterUse(String memberId, int remainTime, int usedTime);
    
    // 30일 이상 미방문 휴면 회원 조회
    List<PC_Member> findDormantMembers();
}
