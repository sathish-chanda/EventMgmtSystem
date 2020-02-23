package ServerInterface;

import java.rmi.*;
import java.util.*;

public interface RemoteInterface extends Remote {

	public String viewEvents(String eventType) throws RemoteException;
	public String viewEvents2(String eventType) throws RemoteException;
	
	public String registerEmployee(String userId)throws RemoteException;
	public String checkUser(String userId)throws RemoteException;
	public String addEvent(String eventype,String eventid,int bookingcapacity) throws RemoteException;
	public Map viewEvents() throws RemoteException;
	public String deleteEvent(String eventype,String eventid) throws RemoteException;
	public String bookEvents(String CustomerId,String eventtype,String eventId) throws RemoteException;
	public String viewBookedEvents(String CustomerId) throws  RemoteException;
	public String viewBookedEvents2(String CustomerId) throws  RemoteException;
	public String cancelEvents(String CustomerId,String eventtype,String eventId)throws  RemoteException;
	public String swapEvent (String customerID,String newEventID,String newEventType,String oldEventID,String oldEventType)throws  RemoteException; 
	
	
}
