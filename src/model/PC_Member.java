package model;

public class PC_Member { //회원 테이블
	private String memberId; //회원 아이디, 기본키
	private String memberPassword; //회원 비밀번호
	private String memberName; //회원 이름
	private int remainingTime; //잔여 시간
	private String gradeType; //등급, 외래키
	private int totalPaymentAmount; //총 결제 금액
	private String memberType; //회원 종류
	
	public PC_Member() {} //기본 생성자

	public PC_Member(String memberId, String memberPassword, String memberName, int remainingTime, String gradeType,
			int totalPaymentAmount, String memberType) {
		super();
		this.memberId = memberId;
		this.memberPassword = memberPassword;
		this.memberName = memberName;
		this.remainingTime = remainingTime;
		this.gradeType = gradeType;
		this.totalPaymentAmount = totalPaymentAmount;
		this.memberType = memberType;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberPassword() {
		return memberPassword;
	}

	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public String getGradeType() {
		return gradeType;
	}

	public void setGradeType(String gradeType) {
		this.gradeType = gradeType;
	}

	public int getTotalPaymentAmount() {
		return totalPaymentAmount;
	}

	public void setTotalPaymentAmount(int totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	
	
	
	

}
