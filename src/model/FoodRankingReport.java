package model;

public class FoodRankingReport {
	private String pcCafeId;
    private int ranking;
    private String foodName;
    private int foodPrice;
    private int totalQuantity;
    private int totalSales;

    public FoodRankingReport() {}

    public FoodRankingReport(String pcCafeId, int ranking, String foodName, int foodPrice, int totalQuantity, int totalSales) {
        this.pcCafeId =pcCafeId;
    	this.ranking = ranking;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.totalQuantity = totalQuantity;
        this.totalSales = totalSales;
    }

    
	public String getPcCafeId() {
		return pcCafeId;
	}

	public void setPcCafeId(String pcCafeId) {
		this.pcCafeId = pcCafeId;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public int getFoodPrice() {
		return foodPrice;
	}

	public void setFoodPrice(int foodPrice) {
		this.foodPrice = foodPrice;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public int getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(int totalSales) {
		this.totalSales = totalSales;
	}
	

    @Override
    public String toString() {
        return "FoodSalesRankingReport{" +
                "ranking=" + ranking +
                ", foodName='" + foodName + '\'' +
                ", foodPrice=" + foodPrice +
                ", totalQuantity=" + totalQuantity +
                ", totalSales=" + totalSales +
                '}';
    }

}
