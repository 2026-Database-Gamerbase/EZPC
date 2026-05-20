package model;

public class Grade {
    private String gradeType;
    private double benefit;
    private int gradeStandard;

    public Grade() {
    }

    public Grade(String gradeType, double benefit, int gradeStandard) {
        this.gradeType = gradeType;
        this.benefit = benefit;
        this.gradeStandard = gradeStandard;
    }

    public String getGradeType() {
        return gradeType;
    }

    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
    }

    public double getBenefit() {
        return benefit;
    }

    public void setBenefit(double benefit) {
        this.benefit = benefit;
    }

    public int getGradeStandard() {
        return gradeStandard;
    }

    public void setGradeStandard(int gradeStandard) {
        this.gradeStandard = gradeStandard;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "gradeType='" + gradeType + '\'' +
                ", benefit=" + benefit +
                ", gradeStandard=" + gradeStandard +
                '}';
    }
}
