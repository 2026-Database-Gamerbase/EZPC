package model;

import java.time.LocalDate;

public class EventSchedule {
    private String eventType;
    private String pcId;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    public EventSchedule(String eventType, String pcId, LocalDate eventStartDate, LocalDate eventEndDate) {
        this.eventType = eventType;
        this.pcId = pcId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPcId() {
        return pcId;
    }

    public void setPcId(String pcId) {
        this.pcId = pcId;
    }

    public LocalDate getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDate eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDate getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDate eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    @Override
    public String toString() {
        return "EventSchedule{" +
                "eventType='" + eventType + '\'' +
                ", pcId='" + pcId + '\'' +
                ", eventStartDate=" + eventStartDate +
                ", eventEndDate=" + eventEndDate +
                '}';
    }
}
