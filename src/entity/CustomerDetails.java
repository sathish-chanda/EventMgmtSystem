package entity;



import java.io.Serializable;

public class CustomerDetails implements Serializable {
	
	
	private String customerID;
	private String eventId;
	private String eventType;
	private int outsideCityEvent;
	
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	
	
	
	public String getEventtype() {
		return eventType;
	}
	public void setEventtype(String eventtype) {
		this.eventType = eventtype;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public int getOutsideCityEvent() {
		return outsideCityEvent;
	}
	public void setOutsideCityEvent(int outsideCityEvent) {
		this.outsideCityEvent = outsideCityEvent;
	}
	
	@Override
	public String toString() {
		return "CustomerDetails [customerID=" + customerID + ", eventId=" + eventId + ", eventType=" + eventType
				+ ", outsideCityEvent=" + outsideCityEvent + "]";
	}

	
	
	
	
	
	
	

}
