package model;

public class Employee {
    private int employeeId;
    private String employeeName;
    private String pcId;
    private String employeePosition;
    private int hourWage;
    private boolean currentlyWorking;

    public Employee(int employeeId, String employeeName, String pcId, String employeePosition, int hourWage, boolean currentlyWorking) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.pcId = pcId;
        this.employeePosition = employeePosition;
        this.hourWage = hourWage;
        this.currentlyWorking = currentlyWorking;
    }

    public Employee(String employeeName, String pcId, String employeePosition, int hourWage, boolean currentlyWorking) {
        this(0, employeeName, pcId, employeePosition, hourWage, currentlyWorking);
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPcId() {
        return pcId;
    }

    public void setPcId(String pcId) {
        this.pcId = pcId;
    }

    public String getEmployeePosition() {
        return employeePosition;
    }

    public void setEmployeePosition(String employeePosition) {
        this.employeePosition = employeePosition;
    }

    public int getHourWage() {
        return hourWage;
    }

    public void setHourWage(int hourWage) {
        this.hourWage = hourWage;
    }

    public boolean isCurrentlyWorking() {
        return currentlyWorking;
    }

    public void setCurrentlyWorking(boolean currentlyWorking) {
        this.currentlyWorking = currentlyWorking;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", pcId='" + pcId + '\'' +
                ", employeePosition='" + employeePosition + '\'' +
                ", hourWage=" + hourWage +
                ", currentlyWorking=" + currentlyWorking +
                '}';
    }
}
