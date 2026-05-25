package model;

public class EventSalesReport {
	
    private String periodType;  // 기간 종류 ("이벤트기간", "직전기간")
    private int foodSales;      // 음식 매출
    private int chargeSales;    // 요금 매출
    private int totalSales;     // 총 매출 합계

    public EventSalesReport() {}

    // Getter & Setter
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    
    public int getFoodSales() { return foodSales; }
    public void setFoodSales(int foodSales) { this.foodSales = foodSales; }
    
    public int getChargeSales() { return chargeSales; }
    public void setChargeSales(int chargeSales) { this.chargeSales = chargeSales; }
    
    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }
}