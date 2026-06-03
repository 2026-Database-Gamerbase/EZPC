package model;

public class PcCafeReviewGradeReport {
    private String pcCafeId;
    private String pcCafeName;
    private double avgStarRating;
    private int reviewCount;
    private int pcCafeGrade;

    public PcCafeReviewGradeReport() {
    }

    public PcCafeReviewGradeReport(String pcCafeId, String pcCafeName, double avgStarRating, int reviewCount, int pcCafeGrade) {
        this.pcCafeId = pcCafeId;
        this.pcCafeName = pcCafeName;
        this.avgStarRating = avgStarRating;
        this.reviewCount = reviewCount;
        this.pcCafeGrade = pcCafeGrade;
    }

    public String getPcCafeId() {
        return pcCafeId;
    }

    public void setPcCafeId(String pcCafeId) {
        this.pcCafeId = pcCafeId;
    }

    public String getPcCafeName() {
        return pcCafeName;
    }

    public void setPcCafeName(String pcCafeName) {
        this.pcCafeName = pcCafeName;
    }

    public double getAvgStarRating() {
        return avgStarRating;
    }

    public void setAvgStarRating(double avgStarRating) {
        this.avgStarRating = avgStarRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getPcCafeGrade() {
        return pcCafeGrade;
    }

    public void setPcCafeGrade(int pcCafeGrade) {
        this.pcCafeGrade = pcCafeGrade;
    }

    @Override
    public String toString() {
        return "PcCafeReviewGradeReport{" +
                "pcCafeId='" + pcCafeId + '\'' +
                ", pcCafeName='" + pcCafeName + '\'' +
                ", avgStarRating=" + avgStarRating +
                ", reviewCount=" + reviewCount +
                ", pcCafeGrade=" + pcCafeGrade +
                '}';
    }
}