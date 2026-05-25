package model;

public class PeakTimeSalesReport {
	
    private String timeSlot;   // 시간대 (예: "14", "15")
    private int totalSales;    // 해당 시간대 총 매출액

    public PeakTimeSalesReport() {}

    // Getter & Setter
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    
    public int getTotalSales() { return totalSales; }
    public void setTotalSales(int totalSales) { this.totalSales = totalSales; }
}