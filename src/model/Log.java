package model;

import java.time.LocalDateTime;

public class Log {
    private int logId;
    private String pcCafeId;
    private int seatNum;
    private String memberId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public Log(int logId, String pcCafeId, int seatNum, String memberId, LocalDateTime loginTime, LocalDateTime logoutTime) {
        this.logId = logId;
        this.pcCafeId = pcCafeId;
        this.seatNum = seatNum;
        this.memberId = memberId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    public Log(String pcCafeId, int seatNum, String memberId) {
        this(0, pcCafeId, seatNum, memberId, null, null);
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
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

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "logId=" + logId +
                ", pcCafeId='" + pcCafeId + '\'' +
                ", seatNum=" + seatNum +
                ", memberId='" + memberId + '\'' +
                ", loginTime=" + loginTime +
                ", logoutTime=" + logoutTime +
                '}';
    }
}
