package entity;



import java.util.ArrayList;
import java.util.List;

public class EventDetails {


    public int bookingCapacity = 0;
    public int seatsFilled = 0;
    public int seatsLeft = 0;
    public String bookingLocation = "";
    public List<String> customerRegistered = new ArrayList<String>();

    
    
    public int getBookingCapacity() {
		return bookingCapacity;
	}



	public void setBookingCapacity(int bookingCapacity) {
		this.bookingCapacity = bookingCapacity;
	}



	public int getSeatsFilled() {
		return seatsFilled;
	}



	public void setSeatsFilled(int seatsFilled) {
		this.seatsFilled = seatsFilled;
	}



	public int getSeatsLeft() {
		return seatsLeft;
	}



	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}



	public String getBookingLocation() {
		return bookingLocation;
	}



	public void setBookingLocation(String bookingLocation) {
		this.bookingLocation = bookingLocation;
	}



	public List<String> getCustomerRegistered() {
		return customerRegistered;
	}



	public void setCustomerRegistered(List<String> customerRegistered) {
		this.customerRegistered = customerRegistered;
	}



	@Override
	public String toString() {
		return "EventDetails [bookingCapacity=" + bookingCapacity + ", seatsFilled=" + seatsFilled + ", seatsLeft="
				+ seatsLeft + ", bookingLocation=" + bookingLocation + ", customerRegistered=" + customerRegistered
				+ "]";
	}
    
    

    
}
