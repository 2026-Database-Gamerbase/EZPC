package model;

import java.sql.Timestamp;

public class Charge {
    private int chargeId;
    private String pcCafeId;
    private int seatNum;
    private int ticketTime;
    private String memberId;
    private int chargePayAmount;
    private double paymentRate;
    private Timestamp chargedAt;

    public Charge() {
    }

    public Charge(int chargeId, String pcCafeId, int seatNum, int ticketTime, String memberId, int chargePayAmount, double paymentRate, Timestamp chargedAt) {
        this.chargeId = chargeId;
        this.pcCafeId = pcCafeId;
        this.seatNum = seatNum;
        this.ticketTime = ticketTime;
        this.memberId = memberId;
        this.chargePayAmount = chargePayAmount;
        this.paymentRate = paymentRate;
        this.chargedAt = chargedAt;
    }


    public int getChargeId() {
        return chargeId;
    }

    public void setChargeId(int chargeId) {
        this.chargeId = chargeId;
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

    public int getTicketTime() {
        return ticketTime;
    }

    public void setTicketTime(int ticketTime) {
        this.ticketTime = ticketTime;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getChargePayAmount() {
        return chargePayAmount;
    }

    public void setChargePayAmount(int chargePayAmount) {
        this.chargePayAmount = chargePayAmount;
    }
    

	public double getPaymentRate() {
		return paymentRate;
	}

	public void setPaymentRate(double paymentRate) {
		this.paymentRate = paymentRate;
	}

	public Timestamp getChargedAt() {
        return chargedAt;
    }

    public void setChargedAt(Timestamp chargedAt) {
        this.chargedAt = chargedAt;
    }

    @Override
    public String toString() {
        return "Charge{" +
                "chargeId=" + chargeId +
                ", pcCafeId='" + pcCafeId + '\'' +
                ", seatNum=" + seatNum +
                ", ticketTime=" + ticketTime +
                ", memberId='" + memberId + '\'' +
                ", chargePayAmount=" + chargePayAmount +
                ", chargedAt=" + chargedAt +
                '}';
    }
}
