package Client;

import FrontEnd.*;
import FrontEndApp.FrontEndInterface;
import FrontEndApp.FrontEndInterfaceHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

//import RemoteInterface.remote;
//import RemoteInterface.remoteHelper;

import java.util.*;

public class Client {

	boolean xMap = true; 
	
		
	public static String serverID;
	public static Client cObj;
	//public static String reply = "";
	
	public static FrontEnd fe = new FrontEnd();
	public void logWritter(String log, String path) {

		String Path1 = "LogServer/" + path + ".txt";
		try {
			Logger.writeLog(log, Path1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException {

		while(true)
		{
		System.out.println("Welcome to Concordia Distribution System");	
		ArrayList<String> alcity = new ArrayList<String>();
		ArrayList<String> alpos = new ArrayList<String>();

		alcity.add("TOR");
		alcity.add("MTL");
		alcity.add("OTW");

		alpos.add("M");
		alpos.add("C");

		FrontEndInterface obj = null;
		cObj = new Client();
		
		//String msgFE = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter User Id:");
		String userId = reader.readLine();

		if (alcity.contains(userId.substring(0, 3)) && alpos.contains(userId.substring(3, 4))) {
			try {
				
				ORB orb = ORB.init(args, null);
				org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
				NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
				obj = (FrontEndInterface) FrontEndInterfaceHelper.narrow(ncRef.resolve_str("FEport"));

			} catch (Exception e) {
				e.printStackTrace();
			}
			//reply = obj.registerEmployee(userId);
			cObj.logWritter("Id created", userId);

			//System.out.println(reply);
		} else {
			System.out.println("InValid user Id");
			System.exit(0);
		}


		if (userId.substring(3, 4).contains("C")) {
			
			//msgFE = msgFE + userId + "#";
			
			System.out.println("Logged in as a Customer!");
			System.out.println("1. Book Event \n2. getBookingSchedule \n3. Cancel Events \n4. Swap Events");
			int cChoice = Integer.parseInt(reader.readLine());
			if (cChoice == 1) {
				System.out.println("Press the first letter (C) Confrences ,  (T) Trade Shows  or (S) Seminars ");
				String eventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the EventID : ");
				String bookingid = reader.readLine();
				String reply = obj.bookEvent(userId, eventType, bookingid);
				//msgFE = msgFE + userId + "#" + eventType + "#" + bookingid + "#a#b#";
				//reply = fe.Msg(msgFE);
				
				cObj.logWritter(reply, userId);  
				System.out.println(reply);
				if (reply.contains("successful")) {
					cObj.logWritter("Customer registered", userId);
				} else {
					cObj.logWritter("Customer unable to registered", userId);
				}
			} else if (cChoice == 2) {
				String bookedEvents = obj.getBookingSchedule(userId);
				cObj.logWritter("Customer registered events", userId);
				System.out.println("Registered Events are :");
				//obj.addEvent(eventype, eventid, bookingcapacity)
				//msgFE = msgFE + userId + "#";
				//reply = fe.Msg(msgFE);
				cObj.logWritter("Calling booked events", userId);  
				System.out.println(bookedEvents);
		
				
			} else if (cChoice == 3) {
				cObj.logWritter("Customer Cancel Events", userId);
				System.out.println("Press the first letter (C) Confrences ,  (T) Trade Shows  or (S) Seminars ");
				String eventType = reader.readLine().toUpperCase();
				System.out.println("please enter EventID ");
				String bookingid = reader.readLine();
				String reply = obj.cancelEvents(userId, eventType, bookingid);
				//msgFE =msgFE + userId + "#" + eventType + "#" + bookingid + "#a#b#c#";
				//reply = fe.Msg(msgFE);
				
				cObj.logWritter(reply, userId);  
				System.out.println(reply);
		
				if (reply.contains("Sucessfull")) {
					cObj.logWritter("Customer cancelled event", userId);
				} else {
					cObj.logWritter("Customer can't cancel the event", userId);
				}
				
			} else if (cChoice == 4) {
				System.out.println("Please enter current event Type");
				String oldEventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the current booking id");
				String oldEventID = reader.readLine();

				System.out.println("Please enter new event Type");
				String newEventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the new booking id");
				String newEventID = reader.readLine();
				String reply = obj.swapEvent(userId, newEventID, newEventType, oldEventID, oldEventType);
				//msgFE =msgFE + userId + "#" + newEventID + "#" + newEventType + "#" + oldEventID + "#" + oldEventType + "#a#b#";
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);  
				System.out.println(reply);
			}
		} else {
			//msgFE = msgFE + userId + "#";
			System.out.println("Logged in as Manager");
			System.out.println(
					"1. Add Event\n2. Remove Event \n3. listEventAvailability  \n4. book Event for Customer \n5. Cancel Customer Event \n6. Swap Customer Event \n7. getBookingSchedule for Customer\n8. Test Multi threading");
			int mChoice = Integer.parseInt(reader.readLine());

			cObj.logWritter("Manager registered", userId);
			String eventid = "";

			if (mChoice == 1) {
				cObj.logWritter("Manager add event", userId);
				System.out.println("Press the first letter (C) Confrences ,  (T) Trade Shows  or (S) Seminars ");
				String eventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the EventID : ");
				eventid = reader.readLine().trim().toUpperCase();
				
				if (!eventid.substring(0, 3).equals(userId.substring(0, 3))) {
					System.out.println("The manager can't add an Event in other cities");
					System.exit(0);
				}
				System.out.println("Please enter the booking capacity : ");
				int bookingCapacity = Integer.parseInt(reader.readLine());
				
				//msgFE = msgFE + eventType + "#" + eventid + "#" + Integer.toString(bookingCapacity) + "#";				
				
				//System.out.println(""+msgFE);
				String time = eventid.substring(3, 4);
				String eventdate = eventid.substring(4);
				String reply = obj.addEvent(userId,eventType, eventid, bookingCapacity);
				//reply = fe.Msg(msgFE);
				
				cObj.logWritter(reply, userId);
				System.out.println(reply);

			} else if (mChoice == 2) {
				
				
				cObj.logWritter("Manager delete event", userId);
				System.out.print("Please enter the type of the Event");
				String eventtype1 = reader.readLine();
				System.out.println("Please enter the EventID ");
				String eventid2 = reader.readLine();
				
				//msgFE = msgFE + eventtype1 + "#" + eventid2 + "#";
				
				String reply = obj.deleteEvent(userId,eventtype1, eventid2);
				
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);
				System.out.println(reply);
			} else if (mChoice == 3) {
				cObj.logWritter("Manager view event", userId);
				System.out.println("Please enter the Type of the Event");
				String eventType = reader.readLine().toUpperCase();
				//msgFE = msgFE + eventType + "#a#b#c#";
				String reply = obj.listEventAvailability(userId,eventType);
//				System.out.println(result);
			    //reply = fe.Msg(msgFE);
			    cObj.logWritter(reply, userId);
			    System.out.println(reply);
			} else if (mChoice == 4) {
				cObj.logWritter("Manager add event for customer", userId);
				System.out.println("Please enter CustomerID ");
				String userIdc = reader.readLine();
				System.out.println("Press the first letter (C) Confrences ,  (T) Trade Shows  or (S) Seminars ");
				String eventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the EventID : ");
				String bookingid = reader.readLine();
				//msgFE = msgFE + userIdc + "#" + eventType + "#" + bookingid + "#a#b#";
				String reply = obj.bookEvent(userIdc, eventType, bookingid);
//				System.out.println(cBooking);
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);  
				System.out.println(reply);
			} else if (mChoice == 5) {
				System.out.println("Please enter CustomerID ");
				String userIdc = reader.readLine();
				System.out.println("Press the first letter (C) Confrences ,  (T) Trade Shows  or (S) Seminars ");
				String eventType = reader.readLine().toUpperCase();
				System.out.println("Please enter the EventID : ");
				String bookingid = reader.readLine();
				String reply = obj.cancelEvents(userIdc, eventType, bookingid);
				//msgFE = msgFE + userIdc + "#" + eventType + "#" + bookingid + "#a#b#c#";
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);
				System.out.println(reply);
				
			} else if (mChoice == 6) {
				System.out.println("Please enter CustomerID ");
				String customerID = reader.readLine();
				System.out.println("Please enter current event Type");
				String oldEventType = reader.readLine().toUpperCase();
				System.out.println("Please Enter the current booking id");
				String oldEventID = reader.readLine();

				System.out.println("Please enter new event Type");
				String newEventType = reader.readLine().toUpperCase();
				System.out.println("Please Enter the new booking id");
				String newEventID = reader.readLine();

				String reply = obj.swapEvent(customerID, newEventID, newEventType, oldEventID, oldEventType);
				System.out.println(reply);
			
				//msgFE = msgFE + customerID + "#" + newEventID + "#" + newEventType + "#" + oldEventID + "#" + oldEventType + "#a#b#";
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);
				System.out.println(reply);
			
			} else if (mChoice == 7) {
				System.out.println("Please enter CustomerID ");
				String customerID = reader.readLine();
				String reply = obj.getBookingSchedule(customerID);
				
				//msgFE = msgFE + customerID + "#";
				//reply = fe.Msg(msgFE);
				cObj.logWritter(reply, userId);
				System.out.println(reply);
			
				
				/*cObj.logWritter("Customer registered events", customerID);
				System.out.println("Registered Events");
				System.out.println(bookedEvents);
				*/
			
			
			
			}
			//				else if(mChoice == 8)
//			{
//				System.out.println("final case running");
//				MTclass.testSync(args);
//				
//			}	
		}
		System.out.println("Do you want to continue or not : yes/no");
		String choice = reader.readLine();
		
		if(choice.equalsIgnoreCase("no"))
		{
			break;
		}
		
		
		}
		
		// obj.displayDatabases();
	}
}