package dao;

import java.util.List;
import model.Customer;

public interface CustomerDAO {
	
	//pc방 선택 -> 좌석 선택 -> 로그인 또는 비회원 이용 선택 후 최종적으로 customer 테이블에 삽입
	void insertCustomer(Customer customer);
	
	//자리 이동
	void updateSeatNo(String pcCafeId, int oldSeatNum, int newSeatNum);
	
	//로그아웃
	void deleteCustomer(String pcCafeId, int seatNum);
	
	//실시간 잔여 시간 갱신 - 로그 테이블 연동 필요
	void updateRemainingTime(String pcCafeId, int seatNum, int remainingTime);
	
	//특정 pc방의 손님 조회 - 좌석 선택 단계에서 호출
	List<Customer> findByPcCafeId(String pcCafeId);
	
	//특정 좌석의 상태 조회 - 빈자리 여부
	Customer findBySeatNo(String pcCafeId, int seatNum);
	
	//특정 손님의 세션 조회 (중복 로그인 방지 단계에서 호출)
	Customer findByMemberId(String memberId);
	
	//체인 pc방을 이용중인 전체 손님 조회
	List<Customer> findAll();

	
}