package model;

public class Customer { //손님 테이블
	private String pcCafeId; //pc방 번호
	private int seatNum; //좌석 번호
	private String memberId; //회원 아이디
	private int remainTime; //잔여시간 - 회원 테이블 참조 예정
	
	public Customer() {} //기본생성자

	public Customer(String pcCafeId, int seatNum, String memberId, int remainTime) {
		super();
		this.pcCafeId = pcCafeId;
		this.seatNum = seatNum;
		this.memberId = memberId;
		this.remainTime = remainTime;
	}

	public String getPcCafeId() {
		return pcCafeId;
	}

	public void setPcCafeId(String pcCafeId) {
		this.pcCafeId = pcCafeId;
	}

	public int getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(int remainingTime) {
		this.remainTime = remainingTime;
	}
	
	
	
	
	
	
	

}