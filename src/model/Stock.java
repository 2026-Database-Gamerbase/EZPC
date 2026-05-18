package model;

public class Stock {
	
    private String pcCafeId;     // PC방 지점 ID (PK)
    private String foodName;     // 음식 이름 (PK, FK)
    private int stockQuantity;   // 재고 수량

    public Stock() {}
    public Stock(String pcCafeId, String foodName, int stockQuantity) {
        this.pcCafeId = pcCafeId;
        this.foodName = foodName;
        this.stockQuantity = stockQuantity;
    }

    public String getPcCafeId() { return pcCafeId; }
    public void setPcCafeId(String pcCafeId) { this.pcCafeId = pcCafeId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    @Override
    public String toString() {
        return "Stock{" +
                "pcCafeId='" + pcCafeId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
    
}