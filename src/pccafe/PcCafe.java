package pccafe;

public class PcCafe {
    private String pcId;
    private String pcName;
    private double averageStarRating;
    private int totalSales;
    private int totalSeats;

    public PcCafe(String pcId, String pcName, double averageStarRating, int totalSales, int totalSeats) {
        this.pcId = pcId;
        this.pcName = pcName;
        this.averageStarRating = averageStarRating;
        this.totalSales = totalSales;
        this.totalSeats = totalSeats;
    }

    public String getPcId() {
        return pcId;
    }

    public void setPcId(String pcId) {
        this.pcId = pcId;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public double getAverageStarRating() {
        return averageStarRating;
    }

    public void setAverageStarRating(double averageStarRating) {
        this.averageStarRating = averageStarRating;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "PcCafe{" +
                "pcId='" + pcId + '\'' +
                ", pcName='" + pcName + '\'' +
                ", averageStarRating=" + averageStarRating +
                ", totalSales=" + totalSales +
                ", totalSeats=" + totalSeats +
                '}';
    }
}
