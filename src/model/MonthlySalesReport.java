package model;

public class MonthlySalesReport {
	
    private String yearMonth;       // 연월 (예: "2026-04")
    private int totalSales;         // 당월 총 매출액
    private int prevMonthSales;     // 전월 매출액
    private double growthRate;      // 매출 증감률 (%)
    private String status;          // 지점 상태 ("부진", "정상")

    public MonthlySalesReport() {}

    // Getter & Setter
    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    
    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }
    
    public int getPrevMonthSales() { return prevMonthSales; }
    public void setPrevMonthSales(int prevMonthSales) { this.prevMonthSales = prevMonthSales; }
    
    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}