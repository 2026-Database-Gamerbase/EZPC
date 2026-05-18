package model;

import java.sql.Timestamp;

public class Order {
	
    private int orderId;             // 주문 번호 (PK)
    private String foodName;         // 음식 이름 (PK, FK)
    private String pcCafeId;         // PC방 지점 ID (FK)
    private int seatNum;             // 좌석 번호
    private int foodQuantity;        // 주문 수량
    private int foodPayAmount;       // 해당 음식 결제 금액
    private Timestamp orderedAt;     // 주문 시각

    // 기본 생성자
    public Order() {}

    // 모든 필드를 포함한 생성자
    public Order(int orderId, String foodName, String pcCafeId, int seatNum, int foodQuantity, int foodPayAmount, Timestamp orderedAt) {
        this.orderId = orderId;
        this.foodName = foodName;
        this.pcCafeId = pcCafeId;
        this.seatNum = seatNum;
        this.foodQuantity = foodQuantity;
        this.foodPayAmount = foodPayAmount;
        this.orderedAt = orderedAt;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getPcCafeId() { return pcCafeId; }
    public void setPcCafeId(String pcCafeId) { this.pcCafeId = pcCafeId; }

    public int getSeatNum() { return seatNum; }
    public void setSeatNum(int seatNum) { this.seatNum = seatNum; }

    public int getFoodQuantity() { return foodQuantity; }
    public void setFoodQuantity(int foodQuantity) { this.foodQuantity = foodQuantity; }

    public int getFoodPayAmount() { return foodPayAmount; }
    public void setFoodPayAmount(int foodPayAmount) { this.foodPayAmount = foodPayAmount; }

    public Timestamp getOrderedAt() { return orderedAt; }
    public void setOrderedAt(Timestamp orderedAt) { this.orderedAt = orderedAt; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", foodName='" + foodName + '\'' +
                ", pcCafeId='" + pcCafeId + '\'' +
                ", seatNum=" + seatNum +
                ", foodQuantity=" + foodQuantity +
                ", foodPayAmount=" + foodPayAmount +
                ", orderedAt=" + orderedAt +
                '}';
    }
}