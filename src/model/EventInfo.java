package model;

public class EventInfo {
    private String eventType;
    private String eventContent;

    public EventInfo(String eventType, String eventContent) {
        this.eventType = eventType;
        this.eventContent = eventContent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    @Override
    public String toString() {
        return "EventInfo{" +
                "eventType='" + eventType + '\'' +
                ", eventContent='" + eventContent + '\'' +
                '}';
    }
}
