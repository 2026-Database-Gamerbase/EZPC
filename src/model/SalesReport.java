package model;

import java.sql.Timestamp;

public class SalesReport {
    
	private String salesType;    // "FOOD" 또는 "CHARGE"
    private String pcCafeId;     // 지점 ID
    private int seatNum;         // 좌석 번호
    private String itemName;     // 판매 내역
    private int payAmount;       // 결제 금액
    private Timestamp salesDate; // 결제 시각

    public SalesReport() {}

    // Getter & Setter
    public String getSalesType() { return salesType; }
    public void setSalesType(String salesType) { this.salesType = salesType; }

    public String getPcCafeId() { return pcCafeId; }
    public void setPcCafeId(String pcCafeId) { this.pcCafeId = pcCafeId; }

    public int getSeatNum() { return seatNum; }
    public void setSeatNum(int seatNum) { this.seatNum = seatNum; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getPayAmount() { return payAmount; }
    public void setPayAmount(int payAmount) { this.payAmount = payAmount; }

    public Timestamp getSalesDate() { return salesDate; }
    public void setSalesDate(Timestamp salesDate) { this.salesDate = salesDate; }

}