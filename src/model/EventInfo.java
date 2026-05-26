package model;

public class EventInfo {
    private String eventType;
    private String eventContent;
    private int eventTypeNum;
    private double paymentRate;

    public EventInfo(String eventType, String eventContent, int eventTypeNum , double paymentRate) {
        this.eventType = eventType;
        this.eventContent = eventContent;
        this.eventTypeNum = eventTypeNum;
        this.paymentRate = paymentRate;
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
    
    

    public int getEventTypeNum() {
		return eventTypeNum;
	}

	public void setEventTypeNum(int eventTypeNum) {
		this.eventTypeNum = eventTypeNum;
	}

	

	public double getPaymentRate() {
		return paymentRate;
	}

	public void setPaymentRate(double paymentRate) {
		this.paymentRate = paymentRate;
	}

	@Override
	public String toString() {
	    return "EventInfo{" +
	            "eventType='" + eventType + '\'' +
	            ", eventContent='" + eventContent + '\'' +
	            ", eventTypeNum=" + eventTypeNum +
	            ", paymentRate=" + paymentRate +
	            '}';
	}
}
