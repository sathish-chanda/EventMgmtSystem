package Server4;



import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.omg.CORBA_2_3.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Client.Logger;
import ClientServerInterfaceApp.*;
import ClientServerInterfaceApp.ClientServerInterfaceHelper;
import ClientServerInterfaceApp.ClientServerInterfacePOA;
import entity.CustomerDetails;
import entity.EventDetails;

import java.io.IOException;
import java.net.*;

public class TorontoServerImpl1 extends ClientServerInterfacePOA implements Runnable {

	//private static Logger LOGGER = LogManager.getLogger("tor");
	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventDetails>> torMap = new HashMap<String, Map<String, EventDetails>>();
	public static List<CustomerDetails> customerLis = new ArrayList<CustomerDetails>();
	public static Map<String,Integer> cusMapCount = new HashMap<String,Integer>();
	public static TorontoServerImpl1 add;

	private org.omg.CORBA.ORB orbTornoto;
	
	public void setORB(org.omg.CORBA.ORB orb) {
		orbTornoto = orb;
	}
	
	public  void shutdown() {
		orbTornoto.shutdown(false);
	}
	
	public TorontoServerImpl1()  {
		super();
	}
	
	public void logWritter(String log)
	{
		String Path1 = "LogServer/" + "TorontoServerImpl1Log.txt";
		try {
			Logger.writeLog(log, Path1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		try {

			
			add = new TorontoServerImpl1();
			add.addEvent("C", "TORM000000", 1);
			add.addEvent("T", "TORM000000", 1);
			add.addEvent("S", "TORM000000", 1);
	
			
			Thread threadSocket = new Thread(new TorontoServerImpl1());
			threadSocket.start();
			System.out.println("######### Toronto Server is up and running.########");
		
			
			DatagramSocket socket = null;
			System.out.println("###### Toronto cControl started #######");
			try
			{
				socket = new DatagramSocket(6172);
			
				while(true)
				{
					byte[] get = new byte[256];
					byte[] send = new byte[2000];
					DatagramPacket request = new DatagramPacket(get, get.length);
					socket.receive(request);
	                String requestStr = new String(request.getData());

	                requestStr = requestStr.trim();
	                System.out.println("request"+requestStr);
	                String[] requestarr=requestStr.split("#");
	            	String responseStr="";
	            	
	            	if (requestarr.length == 1) {
						responseStr = add.getBookingSchedule(requestarr[0].trim());
						} 
					 
					else if (requestarr.length == 2) {
						add.logWritter("Recived request for view events");
						responseStr = add.deleteEvent(requestarr[0].trim(),requestarr[1]);
						}

					else if (requestarr.length == 3) {
							
						System.out.println("mtlserver id");
						responseStr = add.addEvent(requestarr[0], requestarr[1],Integer.parseInt(requestarr[2]));
						}
					else if (requestarr.length == 4){
						add.logWritter("Recived request for booked events");
						responseStr = add.listEventAvailability(requestarr[0]);
					}
					else if(requestarr.length == 5) {
						responseStr = add.bookEvent(requestarr[0],requestarr[1],requestarr[2]);
						
					}
					else if (requestarr.length == 6) {
						responseStr = add.cancelEvents(requestarr[0], requestarr[1], requestarr[2]);
						}
					else if(requestarr.length == 7) {
						responseStr = add.swapEvent(requestarr[0],requestarr[1],requestarr[2],requestarr[3],requestarr[4]);			
					}
					else
							responseStr = "Invalid Request!";
	              
	              
	                send = responseStr.getBytes();        
					DatagramPacket reply = new DatagramPacket(send, send.length, request.getAddress(),request.getPort());
					socket.send(reply);
				}
					
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket.close();
			}












		} catch (Exception e) {
			System.out.println("Toronto Server Start up failed: " + e);
			e.printStackTrace();
		}

	}

	
	/*public static void startRegistry(int rmiPort)  {
		Registry reg = LocateRegistry.createRegistry(rmiPort);
		System.out.println(reg);
	}

	public static void listRegistry(String registry) , MalformedURLException {
		String[] urlArr = Naming.list(registry);

		for (String s : urlArr) {
			System.out.println(s);
		}
	}

	public String Username(String name)  {

		return name;
	}*/

	public  synchronized String addEvent(String eventType, String eventId,int bookingCapactiy)  {

		boolean returnvalue = false;
		Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
		EventDetails e = new EventDetails();
		e.bookingCapacity = bookingCapactiy;
		e.seatsLeft = bookingCapactiy;
		String returnMessage = "";

		System.out.println("########## Toronto Server :: Add Event ############");
		add.logWritter("########## Toronto Server :: Add Event ############");
		if(eventId.substring(0, 3).equalsIgnoreCase("tor"))
		{
		if (!torMap.isEmpty()) {

			if (torMap.containsKey(eventType)) {
				submap = torMap.get(eventType);

				if (!submap.containsKey(eventId)) {
					submap.put(eventId, e);
					torMap.put(eventType, submap);
					returnMessage = "Event added successfully";
					returnvalue = true;
				} else {
					submap.put(eventId, e);
					torMap.put(eventType, submap);
					returnMessage = "Successfully updated the Event seating capacity";
					returnvalue = false;
				}
			} else {
				submap.put(eventId, e);
				torMap.put(eventType, submap);
				returnMessage = "Event added successfully";
				returnvalue = true;
			}

		} else {
			submap.put(eventId, e);
			torMap.put(eventType, submap);
			returnMessage = "Event added successfully";
			returnvalue = true;

		}
		}
		else
		{
			returnMessage = "###### Toronto manager can not add an event to another location ######";
			System.out.println("###### Toronto manager can not add an event to another location ######");
		}
		
		System.out.println("############ EventType : " + eventType + "\t with Event ID : " + eventId + "  added with capacity "
				+ bookingCapactiy + " from toronto server ##########");
		add.logWritter("########## EventType : " + eventType + "\t with Event ID : " + eventId + "  was added  with capacity "
				+ bookingCapactiy + " from toronto server ##########");
		
		
		return returnMessage;

	}

	
	public synchronized  String deleteEvent(String eventType,String eventId)  {

		boolean returnValue = false;
		String returnMessage = "";

		Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
		
		System.out.println("########## Toronto Server :: Remove Event ############");
		add.logWritter("########## Toronto Server :: Remove Event ############");
		
		if (torMap.get(eventType) != null) {
			
			
			List<CustomerDetails> customerLisClone = new ArrayList<CustomerDetails>();
			customerLisClone = customerLis;
			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerDetails cd = new CustomerDetails();
				cd = customerLisClone.get(i);

				if (cd.getEventtype().equalsIgnoreCase(eventType)) {
					customerLis.remove(i);
			
				}

			}
			
			
			submap = torMap.get(eventType);

			if (submap.get(eventId) != null) {
				submap = torMap.get(eventType);
				submap.remove(eventId);
				torMap.put(eventType, submap);
				returnMessage = "Event deleted successfully";
				System.out.println("########## EventType :" + eventType + " : with Event ID : " + eventId + " : is removed ##########");
				add.logWritter("########## EventType :" + eventType + " : with Event ID : " + eventId + " : is removed ##########");
				
				returnValue = true;
			}
		} else {
			returnMessage = "Event doen't exist can't be deleted!";
			System.out.println("######### EventType : " + eventType + " : with Event ID : " + eventId + " : does not exist ##########");
			add.logWritter("######### EventType : " + eventType + " : with Event ID : " + eventId + " : does not exist ##########");
		
			returnValue = false;
		}

		return returnMessage;

	}

	
	public  void printHashMap()  {

		System.out.println(" Printing Map Details");

		for (Map.Entry<String, Map<String, EventDetails>> entry : torMap.entrySet()) {

			System.out.println("The event type is              :" + entry.getKey());
			Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
			submap = entry.getValue();

			if (submap != null) {
				for (Map.Entry<String, EventDetails> e : submap.entrySet()) {
					System.out.println("The event id is                  :" + e.getKey());
					EventDetails event = new EventDetails();
					event = e.getValue();
					System.out.println("The event booking cappacity is   :" + event.bookingCapacity);
					System.out.println("The event booking seat left is   :" + event.seatsLeft);
					System.out.println("The event booking seat booked is :" + event.seatsFilled);
				}

				System.out.println();
			}
		}
	}

	

	
	public synchronized String bookEvent(String customerId, String eventType,String eventId)  {

		System.out.println("########## Toronto Server :: Book Event ############");
		add.logWritter("########## Toronto Server :: Book Event ############");

		
		int count =0;
		String returnMessage = "";
		String key = customerId + eventId.substring(6);
		System.out.println("key for outside event is :"+key);
		if(cusMapCount.containsKey(key))
		{
			count = cusMapCount.get(key);
		}

		Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
		boolean returnValue = false;
		String city = eventId.substring(0, 3);
		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";

		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;


		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();

		if (city.equalsIgnoreCase("tor")) {

			if (torMap.get(eventType) != null) {
				submap = torMap.get(eventType);

				if (submap.containsKey(eventId)) {
					EventDetails e = new EventDetails();
					e = submap.get(eventId);
					List<String> custLis = new ArrayList<String>();
					custLis = e.customerRegistered;

					if (e.seatsLeft > 0 && e.seatsFilled <= e.bookingCapacity) {
						e.seatsFilled = e.seatsFilled + 1;
						e.seatsLeft = e.bookingCapacity - e.seatsFilled;
						custLis.add(customerId);
						e.customerRegistered = custLis;
						submap.put(eventId, e);
						torMap.put(eventType, submap);
						
						
						System.out.println("########## Customer with customer ID  :" + customerId + " booked event type of  : "
								+ eventType + " with event ID : ########### " + eventId);
						add.logWritter("########## Customer with customer ID  :" + customerId + " booked event type of  : "
								+ eventType + " with event ID : ########### " + eventId);
						
						
						
						returnMessage ="Event booking successful!";
						CustomerDetails cd = new CustomerDetails();
						cd.setCustomerID(customerId);
						cd.setEventId(eventId);
						cd.setEventtype(eventType);
						customerLis.add(cd);

						returnValue = true;

					} else {
						
						returnMessage = "No seats available booking failed!";
						System.out.println("######## EventType :" + eventType + " with Event ID : " + eventId
								+ "does not have enough seats left ############");
						
						
						add.logWritter("######## EventType :" + eventType + " with Event ID : " + eventId
								+ "does not have enough seats left ############");
						
						returnValue = false;
					}

				} else {
					returnMessage = "The event doesnt exist";
					System.out.println("######### EventType :" + eventType + " with Event ID : " + eventId + " : does not exist #########");
					add.logWritter("########## EventType :" + eventType + " with Event ID : " + eventId + " : does not exist ############");
				
					returnValue = false;
				}

			} else {
				returnMessage = "The event doesnt exist";
				System.out.println("########## EventType :" + eventType + " does not exist ##########");
				add.logWritter("########## EventType :" + eventType + " does not exist ##########");
			
				returnValue = false;
			}
		} else if (city.equalsIgnoreCase("mtl")) {

			if(count < 3) {
				try {

					System.out.println(" ####### Booking an montreal event from tornoto server through udp server communication ######### ");
					add.logWritter("    ####### Booking an montreal event from tornoto server through udp server communication ######### ");
					System.out.print("count is :"+count);
					socket1 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 7180);
					socket1.send(request1);
					System.out.println("########## Request message sent from the client is ########## : " + new String(request1.getData()));
					add.logWritter("########## Request message sent from the client is        ########## : " + new String(request1.getData()));
				

					byte[] receive1 = new byte[1000];
					DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
					socket1.receive(reply1);
					System.out.println("######### Request message received by the client is ########## : " + new String(reply1.getData()));
					add.logWritter("######### Request message received by the client is        ########## : " + new String(reply1.getData()));
					
					String ret = new String(reply1.getData()).trim();
					cusMapCount.put(key,count + 1);
					System.out.println("Map Key :"+key +"Value is :"+cusMapCount.get(key));
					System.out.println("ret message is :"+ret);
					if (ret.equalsIgnoreCase("t")) {
						returnMessage ="Event booking successful!";
						returnValue = true;

					} else {
						returnMessage = "Event Booking failed!";
						returnValue = false;
					}

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			else{
				returnMessage="You can't register this event because In a month more than 3 events outside the city is not allowed";
				System.out.println("##########  Outside city event booking threadshold breached ##########");
				returnValue = false;
			}
			

		}



		else if (city.equalsIgnoreCase("otw")) {

			System.out.println(" ####### Booking an ottawa event from tornoto server through udp server communication ######### ");
			add.logWritter("    ####### Booking an ottawa event from tornoto server through udp server communication ######### ");

			if (count < 3) {

				try {
					System.out.print("count is :"+count);
					socket2 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 7181);
					socket2.send(request2);
					System.out.println("Request message sent from the client is : " + new String(request2.getData()));

					byte[] receive1 = new byte[1000];
					DatagramPacket reply2 = new DatagramPacket(receive1, receive1.length);
					socket2.receive(reply2);
					System.out.println("######### Request message sent from the client is : ########## :  " + new String(request2.getData()));
					add.logWritter("######### Request message sent from the client is : ########## :  " + new String(request2.getData()));

					String ret = new String(reply2.getData()).trim();
					cusMapCount.put(key,count + 1);
					System.out.println("Map Key :"+key +"Value is :"+cusMapCount.get(key));
					
					System.out.println("######## Request message received by the client is ########### : " + new String(reply2.getData()));
					add.logWritter("######## Request message received by the client is ########### : " + new String(reply2.getData()));
				
					if (ret.equalsIgnoreCase("t")) {
						returnMessage ="Event booking successful!";
						returnValue = true;

					} else {
						returnMessage = "Event Booking failed!";
						returnValue = false;
					}

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}

		}
		else{
			returnMessage="You can't register this event because In a month more than 3 events outside the city is not allowed";
			System.out.println("##########  Outside city event booking threadshold breached ##########");
			returnValue = false;
		}
		
		return returnMessage;
	}

	


	
	public   String getBookingSchedule(String customerId)  {

		List<CustomerDetails> lis = new ArrayList<CustomerDetails>();
		String returnStr = "";
		List<String> st = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = senderOne(customerId,null,null,"getBooking",7180);
			if(s1.equals(" "))
			{
			st.add(s1);
			}
		};
		Runnable task2 = () -> {
			String  s2 =  senderTwo(customerId,null,null,"getBooking",7181);
			if(!s2.equals(" "))
			{
			st.add(s2);
			}
		};
		Thread t1 = new Thread(task1);
		Thread t2 = new Thread(task2);
		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		System.out.println("st list size  : "+st.size());

		for(String i : st)
		{
			System.out.println(" i is :"+i.trim());
			returnStr = returnStr + i.trim();
			System.out.println(" returnStr is :"+returnStr);
		}


		returnStr = returnStr + getCurrentBookingSchedule(customerId);


		System.out.println("########## The bookings schedules are : ########## "+ returnStr);
		add.logWritter("########## The bookings schedules are : ########## "+ returnStr);



		return returnStr;

	}



	public synchronized String getCurrentBookingSchedule(String customerId) {

		List<CustomerDetails> lis = new ArrayList<CustomerDetails>();
		String returnStr = "";

		if (customerLis.size() == 0) {
			System.out.println("######## No customers are registered ###########");
			add.logWritter("######## No customers are registered ###########");
		} else {

			for (CustomerDetails c : customerLis) {
				if (c.getCustomerID().equals(customerId)) {
					lis.add(c);
					//returnStr = returnStr + c.getEventType() + " " + c.getEventId() + ",";
					returnStr =  c.getEventId().trim()  +"#"+ c.getEventType().trim() +"," + returnStr;
				}
			}

		}


		List<String> ans = new ArrayList<String>();
		for (Map.Entry<String, Map<String, EventDetails>> entry : torMap.entrySet()) {
					Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
					submap = entry.getValue();
	
			  	if (submap != null) {
				for (Map.Entry<String, EventDetails> e : submap.entrySet()) {
					
					EventDetails event = new EventDetails();
					event = e.getValue();
					
					List<String> customerRegistered = new ArrayList<String>();
					customerRegistered = event.getCustomerRegistered();
					
					for(String s : customerRegistered)
					{
						
						if(s.equals(customerId))
						{
							
							for(CustomerDetails c : customerLis)
							{
								
								if(s.equalsIgnoreCase(c.getCustomerID()))
								{
									
									lis.add(c);
									returnStr =  c.getEventId().trim()  +"#"+ c.getEventType().trim() +"," + returnStr;
									
								}
							}
							
							
							
						}
						
						
						
					}
				
				
				}
	
			System.out.println();
		}
}

		System.out.println("The booking schedule returned is :"+ returnStr);
		return returnStr;

	
	}






	
	/*public synchronized boolean cancelEvent(String cutomerId, String eventId,String eventType)  {

		
		String city = eventId.substring(0, 3);

		String requestMessage = cutomerId + "," + eventId +"," + eventType + "," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		boolean returnVal = false;
		String key = cutomerId + eventId.substring(6);

		if (city.equalsIgnoreCase("tor")) {

			List<CustomerDetails> customerLisClone = new ArrayList<CustomerDetails>();
			customerLisClone = customerLis;

			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerDetails cd = new CustomerDetails();
				cd = customerLisClone.get(i);

				if (cd.getCustomerID().equalsIgnoreCase(cutomerId) && cd.getEventId().equalsIgnoreCase(eventId)) {
					eventType = cd.getEventtype();
					customerLis.remove(i);

				}

			}

			Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
			submap = torMap.get(eventType);
			if(submap != null)
			{
			for (Map.Entry<String, EventDetails> e : submap.entrySet()) {
				System.out.println("The event id is                  :" + e.getKey());
				EventDetails event = new EventDetails();
				event = e.getValue();

				List<String> customerRegistered = new ArrayList<String>();
				customerRegistered = event.getCustomerRegistered();

				if (customerRegistered.contains(cutomerId)) {

					System.out.println(
							"Customer with customer ID : " + cutomerId + "cancelled the event with ID : " + eventId);
					customerRegistered.remove(cutomerId);
					event.seatsFilled = event.seatsFilled - 1;
					event.seatsLeft = event.seatsLeft + 1;

					submap.put(eventId, event);
					torMap.put(eventType, submap);

				}

				System.out.println("The event booking cappacity is   :" + event.bookingCapacity);
				System.out.println("The event booking seat left is   :" + event.seatsLeft);
				System.out.println("The event booking seat booked is :" + event.seatsLeft);

			}
		}
		else
		{
			System.out.println("###### No Valid Details exist in the map to delete #######");
		}

		}

		else if (city.equalsIgnoreCase("mtl"))
		{
			System.out.println(" Calling Ottawa Book Event from Montreal");



			try {
				
				int count =0;
				if(cusMapCount.containsKey(key))
				{
					count = cusMapCount.get(key);
				}

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 7180);
				socket1.send(request1);
				System.out.println("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("Request message received by the client is : " + new String(reply1.getData()));
				String ret = new String(reply1.getData()).trim();
				System.out.println("ret is :"+ret);
				
				if(count > 0)
					cusMapCount.put(key, count -1);
					
					System.out.println("ret is :"+ret);
					
					if(ret.equalsIgnoreCase("t"))
					{
						returnVal = true;

					}
					else
					{
						returnVal = false;
					}




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		else if(city.equalsIgnoreCase("otw"))
		{

			System.out.println(" Calling Ottawa Book Event from Montreal");

			int count =0;
			if(cusMapCount.containsKey(key))
			{
				count = cusMapCount.get(key);
			}


			try {

				socket2 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 7181);
				socket2.send(request2);
				System.out.println("Request message sent from the client is : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				System.out.println("Request message received by the client is : " + new String(reply2.getData()));
				String ret = new String(reply2.getData()).trim();
				System.out.println("ret is :"+ret);
				
					if(count > 0)
					cusMapCount.put(key, count -1);
					
					System.out.println("ret is :"+ret);
					
					if(ret.equalsIgnoreCase("t"))
					{
						returnVal = true;

					}
					else
					{
						returnVal = false;
					}




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}


		}
		
		return returnVal;

	}*/

	

public synchronized String cancelEvents(String cutomerId, String eventType,String eventId)  {

		
		String city = eventId.substring(0, 3);
		String requestMessage = cutomerId + "," + eventId +"," + eventType + "," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		boolean returnVal = false;
		String key = cutomerId + eventId.substring(6);
		System.out.println("key for outside event is :"+key);
		String message = "";

		if (city.equalsIgnoreCase("tor")) {

			List<CustomerDetails> customerLisClone = new ArrayList<CustomerDetails>();
			customerLisClone = customerLis;

			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerDetails cd = new CustomerDetails();
				cd = customerLisClone.get(i);

				if (cd.getCustomerID().equalsIgnoreCase(cutomerId) && cd.getEventId().equalsIgnoreCase(eventId) && cd.getEventType().equals(eventType)) {
					eventType = cd.getEventtype();
					customerLis.remove(i);
					returnVal = true;

				}

			}

			Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
			submap = torMap.get(eventType);
			
			
			if(submap!=null)
			{
			for (Map.Entry<String, EventDetails> e : submap.entrySet()) {
				System.out.println("The event id is                  :" + e.getKey());
				EventDetails event = new EventDetails();
				event = e.getValue();

				List<String> customerRegistered = new ArrayList<String>();
				customerRegistered = event.getCustomerRegistered();

				if (customerRegistered.contains(cutomerId)) {

					System.out.println(
							"Customer with customer ID : " + cutomerId + "cancelled the event with ID : " + eventId);
					customerRegistered.remove(cutomerId);
					event.seatsFilled = event.seatsFilled - 1;
					event.seatsLeft = event.seatsLeft + 1;

					submap.put(eventId, event);
					torMap.put(eventType, submap);
					message = "Successfully cancelled the Event";

				}

				System.out.println("The event booking cappacity is   :" + event.bookingCapacity);
				System.out.println("The event booking seat left is   :" + event.seatsLeft);
				System.out.println("The event booking seat booked is :" + event.seatsLeft);

			}

			
			
		}
			else
			{
				System.out.println("###### No Valid Details exist in the map to delete #######");
				message = "Event doesn't exist cancellation not possible!";
			}
		}

		else if (city.equalsIgnoreCase("mtl"))
		{
			System.out.println(" Calling Ottawa Book Event from Montreal");
			int count =0;
			if(cusMapCount.containsKey(key))
			{
				count = cusMapCount.get(key);
			}


			try {

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 7180);
				socket1.send(request1);
				System.out.println("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("Request message received by the client is : " + new String(reply1.getData()));
				String ret = new String(reply1.getData()).trim();
				System.out.println("ret is :"+ret);
				
					if(count > 0)
					cusMapCount.put(key, count -1);
					
					System.out.println("ret is :"+ret);
					
					if(ret.equalsIgnoreCase("t"))
					{
						
						returnVal = true;
						message = "Successfully cancelled the Event";

					}
					else
					{
						message = "Cancel Event Failed";
						returnVal = false;
					}




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		else if(city.equalsIgnoreCase("otw"))
		{

			System.out.println(" Calling Ottawa Book Event from Montreal");

			int count =0;
			if(cusMapCount.containsKey(key))
			{
				count = cusMapCount.get(key);
			}

			try {

				socket2 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 7181);
				socket2.send(request2);
				System.out.println("Request message sent from the client is : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				System.out.println("Request message received by the client is : " + new String(reply2.getData()));
				String ret = new String(reply2.getData()).trim();
				System.out.println("ret is :"+ret);
				
					if(count > 0)
					cusMapCount.put(key, count -1);
					
					System.out.println("ret is :"+ret);
					
					if(ret.equalsIgnoreCase("t"))
					{
						returnVal = true;
						message = "Successfully cancelled the Event";

					}
					else
					{
						message = "Cancel Event Failed";
						returnVal = false;
					}




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		return message;
		
	}



	
	public  String listEventAvailability(String eventType)  {

		
		System.out.println("########## Toronto Server :: List Event ############");
		add.logWritter("########## Toronto Server :: List Event ############");

		
		Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
		List<String> availableEvents = new ArrayList<String>();
		String s = "";




		List<String> st = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = senderOne(null,eventType,null,"listEventAvailability",7180);
			if(!s1.equals(" "))
			{
			st.add(s1);
			}
		};
		Runnable task2 = () -> {
			String  s2 =  senderTwo(null,eventType,null,"listEventAvailability",7181);
			if(!s2.equals(" "))
			{
			st.add(s2);
			}
		};


		Thread t1 = new Thread(task1);
		Thread t2 = new Thread(task2);

		t1.start();
		t2.start();

		System.out.println("st list size  : "+st.size());


		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}


		for(String i : st)
		{
			
			s = s + i.trim();
			
		}
		


		s = s + getCurrentServerEventAvailability(eventType);
		System.out.println("##########  List Available Event ############ : "+s);
		add.logWritter("       ########## List Available Event  ############ : "+s);
		return s;
	}





	public synchronized String getCurrentServerEventAvailability(String eventType)
	{
		System.out.println("########## Toronto Server :: Current List Event ############");
		add.logWritter("########## Toronto Server :: Current List Event ############");

		
		Map<String, EventDetails> submap = new HashMap<String, EventDetails>();
		List<String> availableEvents = new ArrayList<String>();
		String s = "";

		if (torMap.get(eventType) != null) {
			submap = torMap.get(eventType);

			for (Map.Entry<String, EventDetails> e : submap.entrySet()) {
				System.out.println("The event id is                  :" + e.getKey());
				EventDetails event = new EventDetails();
				event = e.getValue();
				System.out.println("The event booking cappacity is   :" + event.bookingCapacity);
				System.out.println("The event booking seat left is   :" + event.seatsLeft);
				System.out.println("The event booking seat booked is :" + event.seatsLeft);

				if (event.seatsLeft >= 0) {
					s = s + e.getKey() + " " + event.seatsLeft + ",";
					availableEvents.add(s);
				}
			}

		} else {
			System.out.println("EventType : " + eventType + "is not avaliable");
		}


		System.out.println("########## Ottawa Server :: Current List Event ############");
		add.logWritter("########## Ottawa Server :: Current List Event ############");
		
		return s;

	}




  
	public synchronized String senderOne(String customerId,String eventType,String eventId,String requestType,int port)
	{
		String result = "";
		String returnString = "";


		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = eventType + "," +requestType;
				System.out.println("Message is : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();


				System.out.println("Reuqest Type for server to server communication is :"+requestType);
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				System.out.println("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();
			}

			else if(requestType.equalsIgnoreCase("getBooking"))
			{


				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," +requestType;
				System.out.println("Message is : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();


				System.out.println("Reuqest Type for server to server communication is :"+requestType);
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				System.out.println("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();

			}

		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;

	}



	public  synchronized String senderTwo(String customerId,String eventType,String eventId,String requestType,int port)
	{

		String result = "";
		String returnString = "";


		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = eventType + "," +requestType;
				System.out.println("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				System.out.println("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				System.out.println("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString +  new String(reply2.getData()).trim();
			}


			else if(requestType.equalsIgnoreCase("getBooking"))
			{

				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," +requestType;
				System.out.println("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				System.out.println("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				System.out.println("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString +  new String(reply2.getData()).trim();

			}


		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		TorontoServerImpl1 add = new TorontoServerImpl1();
		try {

			socket = new DatagramSocket(7182);

			while (true) {

				byte[] get = new byte[1000];
				byte[] send = new byte[1000];
				String recivedMessage = "";
				DatagramPacket request = new DatagramPacket(get, get.length);
				socket.receive(request);
				System.out.println(" recieved message :"+recivedMessage);
				recivedMessage = new String(request.getData());
				System.out.println(" recieved message1 :"+recivedMessage);
				String[] splitMsg = recivedMessage.split(",");
				String requestType = splitMsg[splitMsg.length -1];
				requestType = requestType.trim();
				System.out.println("Request Type :"+requestType);

				String result = "";
				if(requestType.equalsIgnoreCase("listEventAvailability"))
				{
					System.out.println("If case inside ottawa server is true");
					//result = add.listEventAvailability(splitMsg[0]);
					result = add.getCurrentServerEventAvailability(splitMsg[0]);
					send = result.getBytes();
				}
				else if(requestType.equalsIgnoreCase("bookEvent"))
				{
					System.out.println("If case inside ottawa server is true");
					String ret = add.bookEvent(splitMsg[0], splitMsg[2], splitMsg[1]);
					if(ret.contains("successful"))
					{
						result = "t";
						send = result.getBytes();
					}
					else
					{
						result = "f";
						send = result.getBytes();

					}
				}
				else if(requestType.equalsIgnoreCase("getBooking"))
				{
					System.out.println("If case inside ottawa server is true");

					
					result  = add.getCurrentBookingSchedule(splitMsg[0]);

					send = result.getBytes();

				}
				else if(requestType.equalsIgnoreCase("cancelEvent"))
				{
					System.out.println("If case inside ottawa server is true");
					String ret = add.cancelEvents(splitMsg[0], splitMsg[2], splitMsg[1]);
					if(ret.contains("Successfully"))
					{
						result = "t";
						send = result.getBytes();
					}
					else
					{
						result = "f";
						send = result.getBytes();

					}

				}
				else
				{
					send = "".getBytes();
				}

				DatagramPacket reply = new DatagramPacket(send, send.length, request.getAddress(),
						request.getPort());
				socket.send(reply);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
		
	}
	
	
	
	
	
	
	
/*	public  String senderOne(String customerId,String eventType,String eventId,String requestType,int port)
	{
		String result = "";
		String returnString = "";

		System.out.println("########## Ottawa Server :: senderOne ############");
		add.logWritter("########## Ottawa Server :: senderOne ############");

		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = eventType + "," +requestType;
				System.out.println("######### Message to be sent is ######## : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();


				System.out.println("######### Reuqest Type for server to server communication is ######### :"+requestType);
				add.logWritter("######### Reuqest Type for server to server communication is ######### :"+requestType);
				
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				
				System.out.println("######### Request message sent from the client is :          ######### :" + new String(request1.getData()));
				add.logWritter("######### Request message sent from the client is :          ######### :" + new String(request1.getData()));
	

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("######## Request message received by the client is :         ######### : " + new String(reply1.getData()));
				add.logWritter("######## Request message received by the client is :         ######### : " + new String(reply1.getData()));
				
				returnString = returnString +  new String(reply1.getData()).trim();
			}

			else if(requestType.equalsIgnoreCase("getBooking"))
			{

				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," +requestType;
				System.out.println("######### Message to be sent is ######## : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();


				System.out.println("######### Reuqest Type for server to server communication is ######### :"+requestType);
				add.logWritter("######### Reuqest Type for server to server communication is ######### :"+requestType);
			
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);

				System.out.println("######### Request message sent from the client is :          ######### :" + new String(request1.getData()));
				add.logWritter("######### Request message sent from the client is :          ######### :" + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				System.out.println("######## Request message received by the client is :         ######### : " + new String(reply1.getData()));
				add.logWritter("######## Request message received by the client is :         ######### : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();



			}

		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		
		System.out.println("########## Ottawa Server :: senderOne End ############");
		add.logWritter("########## Ottawa Server :: senderOne End ############");

		
		
		return returnString;

	}



	public  String senderTwo(String customerId,String eventType,String eventId,String requestType,int port)
	{

		String result = "";
		String returnString = "";
		
		System.out.println("########## Tornoto Server :: senderTwo  ############");
		add.logWritter("########## Tornoto Server :: senderTwo  ############");



		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = eventType + "," +requestType;
				System.out.println("######### Message to be sent is ######## : "+message);
				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				System.out.println("############ Request message sent from the client is ########## : " + new String(request2.getData()));
				add.logWritter("############ Request message sent from the client is ########## : " + new String(request2.getData()));
	
				socket2.receive(reply2);
				System.out.println("############ Request message received by the client is ######## : " + new String(reply2.getData()));
				add.logWritter("############ Request message received by the client is ######## : " + new String(reply2.getData()));
			
				returnString = returnString +  new String(reply2.getData()).trim();
			}


			else if(requestType.equalsIgnoreCase("getBooking"))
			{


				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," +requestType;
				System.out.println("######### Message to be sent is ######## : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				System.out.println("############ Request message sent from the client is ########## : " + new String(request2.getData()));
				add.logWritter("############ Request message sent from the client is ########## : " + new String(request2.getData()));
		
				socket2.receive(reply2);
				System.out.println("############ Request message received by the client is ######## : " + new String(reply2.getData()));
				add.logWritter("############ Request message received by the client is ######## : " + new String(reply2.getData()));

				returnString = returnString +  new String(reply2.getData()).trim();




			}


		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;
	}
*/
	

	

	public String swapEvent(String customerId, String newEventId, String newEventType, String oldEventId,
			String oldEventType) {

		String message = "";

		if (cancelEvents(customerId, oldEventType, oldEventId).contains("Success")) {
			if (bookEvent(customerId, newEventType, newEventId).contains("success")) {
				message =  "Swap sccessfull";
				System.out.println("###### Event successfully swapped   #######");
			} else {
				bookEvent(customerId, oldEventType, oldEventId);
				message =  "Swap uncessfull";
				System.out.println("####### Book Event for the Swap Request Failed!! #########");
				System.out.println("###### Event could not be booked for the swap request.   #######");
			}
		} else {
			message = "No event to cancel";			
			System.out.println("###### Event could not be cancelled for the swap request. #######");
		}

		return message;

	}

}
