package Server1;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Client.Logger;


public class OTWserver {
	
	Map<String,Map<String,ArrayList<String>>> eventsdata = new HashMap<String,Map<String,ArrayList<String>>>();
	ArrayList<String> userIdList=new ArrayList<String>();
	ArrayList<Integer> numberingList=new ArrayList<Integer>();
	
	static String location = "OTWserver";
	public static OTWserver server;
	public static int OTWport = 2346;

	
	Map<String,ArrayList<String>> customerData=new HashMap<String,ArrayList<String>>();
	Map<String,ArrayList<String>> customerDataType=new HashMap<String,ArrayList<String>>();

	public void logWritter(String log)
	{

		String Path1 = "LogServer/" + "OTWserverlog.txt";
		try {
			Logger.writeLog(log, Path1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public synchronized void registerEmployee(String userId)
	{
		if(userIdList.contains(userId))
		{
			//return "User already registered";
		}
		else
		{
			userIdList.add(userId);
			//return "User successfully registered";
		}
	}
	
	
	public String checkUser(String userId)
	{
		if(userIdList.contains(userId))
		{
			return "true";
		}
		else
		{
			return "false";
		}
		
	}
	
	public Map viewEvents()
	{
		return eventsdata ;
	}

	public String addEvent(String eventType,String eventid,int bookingCapacity)
	{
		
		ArrayList<String> eventdatalist=new ArrayList<String>();

		eventdatalist.add(eventid.substring(3,4));
		eventdatalist.add(eventid.substring(4));
		eventdatalist.add(Integer.toString(bookingCapacity));
		
		server.logWritter("received add request"+eventid);
		
		if( !(eventsdata.containsKey(eventType)))
		{
			if(eventType.contains("C"))
			{
				eventsdata.put(eventType, new HashMap<String,ArrayList<String>>());
				eventsdata.get(eventType).put(eventid, eventdatalist);
			}
			else if(eventType.contains("T"))
			{
				eventsdata.put(eventType, new HashMap<String,ArrayList<String>>());
				eventsdata.get(eventType).put(eventid, eventdatalist);
			}
			else  if(eventType.contains("S"))
			{
				eventsdata.put(eventType, new HashMap<String,ArrayList<String>>());
				eventsdata.get(eventType).put(eventid, eventdatalist);
			}	
			return "Event added successfully";
		}
		else if(eventsdata.get(eventType).containsKey(eventid))
		{
			int temp1=Integer.parseInt(eventsdata.get(eventType).get(eventid).get(2));
			temp1=temp1+bookingCapacity;
			eventsdata.get(eventType).get(eventid).add(2, Integer.toString(temp1));
			return "Successfully updated the Event seating capacity";	
		}
			else
		{

				if(eventType.contains("C"))
				{
						eventsdata.get(eventType).put(eventid, eventdatalist);
				}
				else if(eventType.contains("T"))
				{
					eventsdata.get(eventType).put(eventid, eventdatalist);
					
				}
				else  if(eventType.contains("S"))
				{
					eventsdata.get(eventType).put(eventid, eventdatalist);
				}	
				return "Event added successfully";
					
			}
	}
	
	public String deleteEvent(String eventid)
	{
		boolean del = false;
		for(String key : customerData.keySet())
		{
			if(customerData.get(key).contains(eventid)) {
				int index = customerData.get(key).indexOf(eventid);
				customerData.get(key).remove(eventid);
				customerDataType.get(key).remove(index);
			    del = true;
			}
		}
		if(del)
			return "Successfully deleted event from atleast one customer";
		else
			return "No customer has registered for this event";
	}
	
	public String deleteEvent2(String eventype,String eventid)
	{
		StringBuilder builder = new StringBuilder();
		if(eventsdata.get(eventype).containsKey(eventid))
		{
		   eventsdata.get(eventype).remove(eventid);
		   String response = deleteEvent(eventid);		
		   String s = eventid + "#a#b#c#d";
		   DatagramSocket socket1 = null;
		   DatagramSocket socket2 = null;
			byte[] send = eventid.getBytes();

			try {
				
				InetAddress address = InetAddress.getByName("localhost");
	 			socket1 = new DatagramSocket();
	            DatagramPacket otw = new DatagramPacket(send,send.length,address,MTLserver.MTLport);				
				socket1.send(otw);
				byte[] get = new byte[2000];
				DatagramPacket receive = new DatagramPacket(get,get.length);
				socket1.receive(receive);
				builder.append(new String(receive.getData()).trim()).append(",");
				
				socket2 = new DatagramSocket();
				DatagramPacket tor = new DatagramPacket(send,send.length,address,TORserver.TORport);
				socket2.send(tor);
				byte[] get2 = new byte[2000];
				DatagramPacket receive2 = new DatagramPacket(get2,get2.length);
				socket2.receive(receive2);
				builder.append(new String(receive2.getData()).trim());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				socket1.close();
				socket2.close();	
			}
			System.out.println(builder.toString());
			//return builder.toString();	
			return "Event deleted successfully";
		} else {
			return "Event doen't exist can't be deleted!";
		}
	}
	public synchronized String bookEvents(String customerId,String eventType,String eventId)
	{
		if(!userIdList.contains(customerId)){userIdList.add(customerId);}
		customerId=customerId.trim();
		eventType=eventType.trim();
		eventId=eventId.trim();
		String response="";
		System.out.println("server object of ottawa :"+server);
		String bookedEvents = server.viewBookedEvents2(customerId);
		System.out.println("otw saeer"+bookedEvents);
		String[] events = bookedEvents.split(",");
		
		String currentEventDate = eventId.substring(6, 10);
		int dateCount = 0;
		
		if(!(eventId.contains("OTW")))
		{
		if(events.length > 0)
		{
			String date="";
			for(int i = 0; i < events.length; i++)
			{					
				if(events[i].length() > 6)
			{
				date = events[i].substring(6,10).trim();
				  if( date.contains(currentEventDate) )
				  {
					  dateCount++;
					  if(dateCount > 2)
					  {
						  return "You can't register this event because In a month more than 3 events outside the city is not allowed"; 
					  }

				  }
				  }
			}
		}
		}
		
		
		if(eventId.substring(0,3).contains("OTW"))
				{
			if(eventsdata.containsKey(eventType))
			{
				if(!eventsdata.get(eventType).containsKey(eventId))
				{
					return "The event doesnt exist";
				}
			}		
			if(Integer.parseInt(eventsdata.get(eventType).get(eventId).get(2)) > 0)
						{
							int temp1=Integer.parseInt(eventsdata.get(eventType).get(eventId).get(2));
							temp1=temp1-1;
							eventsdata.get(eventType).get(eventId).add(2, Integer.toString(temp1));
							if(customerData.containsKey(customerId))
							{
								customerData.get(customerId).add(eventId);
						        customerDataType.get(customerId).add(eventType); 
							}
							else
							{
								ArrayList<String> al=new ArrayList<String>();
								al.add(eventId);
								ArrayList<String> ax = new ArrayList<String>();
								ax.add(eventType);
								customerData.put(customerId, al);
								customerDataType.put(customerId, ax);
							}
							return "Event booking successful!";
						}
						else
							return "No seats available booking failed!";
				}
		else if(eventId.substring(0,3).equals("TOR"))
		{
			DatagramSocket socket= null;
			try 
			{
			socket= new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			String sendingData = customerId+"#"+eventType+"#"+eventId;
			byte[] sendingBytes = sendingData.getBytes();
			DatagramPacket sendingPacket = new DatagramPacket(sendingBytes,sendingBytes.length, address,TORserver.TORport);
			socket.send(sendingPacket);
			byte[] receiveBytes = new byte[1000];
			DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
			socket.receive(reply);
			response = new String(reply.getData());
			response = response.trim();
		    }
			catch(Exception ex)
			{
				System.out.println("Exception! ELSE");
			}
			finally{
				socket.close();

				//since getting the response from the user is string and meanigful message.
				return response;
//			    if(response.equals("true"))
//					return "Event booking successful!";
//		        else
//			       return "No seats available booking failed!";
		}
		}
		else if(eventId.substring(0,3).equals("MTL"))
		{
			DatagramSocket socket= null;
			try 
			{
			socket= new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			//socket = new DatagramSocket(TORserver.TORport);
			String sendingData = customerId+"#"+eventType+"#"+eventId;
			byte[] sendingBytes = sendingData.getBytes();
			DatagramPacket sendingPacket = new DatagramPacket(sendingBytes,sendingBytes.length, address,MTLserver.MTLport);
			socket.send(sendingPacket);
			byte[] receiveBytes = new byte[1000];
			DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
			socket.receive(reply);
			response = new String(reply.getData());
			response=response.trim();
			}
			catch(Exception ex)
			{
				System.out.println("Exception! ELSE");
			}
			finally{
				socket.close();

				//since getting the response from the user is string and meanigful message.
				return response;
				}
		}
		else
			return "Invalid City Booking not possible";
}
	public String viewBookedEvents(String customerId)
	{	
		if(!userIdList.contains(customerId)){userIdList.add(customerId);}
	StringBuilder builder = new StringBuilder("");
	int i;
	if(customerData.containsKey(customerId.trim()))
	{
	    if(customerData.get(customerId).size()==0)
			return "";
		for(i=0;i<customerData.get(customerId).size();i++)
			if(i!=customerData.get(customerId).size()-1)
				builder.append(customerData.get(customerId).get(i)).append("#").append(customerDataType.get(customerId).get(i)).append(",");
			else
				builder.append(customerData.get(customerId).get(i)).append("#").append(customerDataType.get(customerId).get(i)).append(",");
	        return builder.toString();
	}
	else
		return "";

	}
	
	public String viewBookedEvents2(String customerId)
	{
		if(!userIdList.contains(customerId)){userIdList.add(customerId);}
		StringBuilder builder = new StringBuilder("");
		if(customerData.containsKey(customerId.trim()))
		{
		    if(customerData.get(customerId).size()!=0)
			for(int i=0;i<customerData.get(customerId).size();i++)
				if(i!=customerData.get(customerId).size()-1)
					builder.append(customerData.get(customerId).get(i)).append("#").append(customerDataType.get(customerId).get(i)).append(",");
				else
					builder.append(customerData.get(customerId).get(i)).append("#").append(customerDataType.get(customerId).get(i)).append(",");
		        
		}
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] send = customerId.getBytes();
		try {
			InetAddress address = InetAddress.getByName("localhost");
 			socket1 = new DatagramSocket();
            DatagramPacket mtl = new DatagramPacket(send,send.length,address,MTLserver.MTLport);				
			socket1.send(mtl);
			byte[] get = new byte[2000];
			DatagramPacket receive = new DatagramPacket(get,get.length);
			socket1.receive(receive);
			builder.append(new String(receive.getData()).trim()).append(",");
			
			socket2 = new DatagramSocket();
			DatagramPacket tor = new DatagramPacket(send,send.length,address,TORserver.TORport);
			socket2.send(tor);
			byte[] get2 = new byte[2000];
			DatagramPacket receive2 = new DatagramPacket(get2,get2.length);
			socket2.receive(receive2);
			builder.append(new String(receive2.getData()).trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			socket1.close();
			socket2.close();	
		}
		System.out.println(builder.toString());
		return builder.toString();		
	}
		
	public String cancelEvents(String customerId,String eventType,String eventId)
	{
		if(!userIdList.contains(customerId)){userIdList.add(customerId);}
		
		String response="";
		customerId=customerId.trim();
		eventType=eventType.trim();
		eventId=eventId.trim();
		if(eventId.substring(0,3).equals("OTW"))
		{
			if(customerData.get(customerId).contains(eventId))
		{	
				int indexId = customerData.get(customerId).indexOf(eventId);
		        customerData.get(customerId).remove(eventId);
		        customerDataType.get(customerId).remove(indexId);
			int temp1=Integer.parseInt(eventsdata.get(eventType).get(eventId).get(2));
			temp1=temp1+1;
			eventsdata.get(eventType).get(eventId).add(2, Integer.toString(temp1));	
			return "Successfully cancelled the Event";
		}
		else
		return "Event doesn't exist cancellation not possible!";
		}
		else if(eventId.substring(0,3).equals("MTL"))
		{
			DatagramSocket socket= null;
			try 
			{
			socket= new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			String sendingData = customerId+"#"+eventType+"#"+eventId+"#cancelEvent";
			byte[] sendingBytes = sendingData.getBytes();
			DatagramPacket sendingPacket = new DatagramPacket(sendingBytes,sendingBytes.length, address,MTLserver.MTLport);
			socket.send(sendingPacket);
			byte[] receiveBytes = new byte[1000];
			DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
			socket.receive(reply);
			response = new String(reply.getData());
			response = response.trim();
		    }
			catch(Exception ex)
			{
				System.out.println("Exception! ELSE");
			}
			finally{
				socket.close();
				return response;
//				return response.equals("true");	
			}
		}
		else if(eventId.substring(0,3).equals("TOR"))
		{
			DatagramSocket socket= null;
			try 
			{
			socket= new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			String sendingData = customerId+"#"+eventType+"#"+eventId+"#cancelEvent";
			byte[] sendingBytes = sendingData.getBytes();
			DatagramPacket sendingPacket = new DatagramPacket(sendingBytes,sendingBytes.length, address,TORserver.TORport);
			socket.send(sendingPacket);
			byte[] receiveBytes = new byte[1000];
			DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
			socket.receive(reply);
			response = new String(reply.getData());
			response = response.trim();
		    }
			catch(Exception ex)
			{
				System.out.println("Exception! ELSE");
			}
			finally{
				socket.close();
				return response;	
			}
		}
		else
			return "Invalid City";
	}
	
	public synchronized String swapEvent(String customerID,String newEventID,String newEventType,String oldEventID,String oldEventType)
	{
		
		if(!userIdList.contains(customerID)){userIdList.add(customerID);}
		
		String response = cancelEvents( customerID, oldEventType, oldEventID);
		if(response.contains("Success"))
		{
			response = bookEvents(customerID, newEventType, newEventID) ; 	
			if(response.contains("success"))
			{
				return "Swap sccessfull";
			}
			else
			{
				response = bookEvents( customerID, oldEventType, oldEventID);
				return "Swap uncessfull";
			}
			
		}
		else
		{
			return "No event to cancel";
		}
	}
	
	public String viewEvents(String eventType)
	{
	StringBuilder builder = new StringBuilder();
	server.logWritter("request for view events");
	Map<String, ArrayList<String>> eventsId = new HashMap<String, ArrayList<String>>();
	eventsId = eventsdata.get(eventType);
	System.out.println(eventsId);
	Set<String> eventsList = new HashSet<String>();
	eventsList = eventsId.keySet();
	int count = 0;
	for (String eventID : eventsList) {
		if (count != eventsList.size() - 1)
			builder.append(eventID).append(" ").append(eventsId.get(eventID).get(2)).append(",");
		else
			builder.append(eventID).append(" ").append(eventsId.get(eventID).get(2)).append(",");
	}
	server.logWritter("sent received events");
	return builder.toString();
	}

	public String viewEvents2(String eventType) 
	{

		server.logWritter("Request for view events");
		String totEvents="";
		
		String strdatamtl="";
		String strdatator="";
		
		totEvents=server.viewEvents(eventType);

		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		String sendMsg=eventType+"#"+"viewEvents";
		byte[] send = sendMsg.getBytes();
		System.out.println(""+eventType);
		
		try {
			InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();
	        DatagramPacket mtl = new DatagramPacket(send,send.length,address,MTLserver.MTLport);				
			socket1.send(mtl);
			byte[] get = new byte[2000];
			DatagramPacket receive = new DatagramPacket(get,get.length);
			
			socket1.receive(receive);
			//builder.append(new String(receive.getData())).append(",");
			strdatamtl=(new String(receive.getData()).trim());
			
			System.out.println("mtl"+(new String(receive.getData())).trim());
			
			socket2 = new DatagramSocket();
			DatagramPacket tor = new DatagramPacket(send,send.length,address,TORserver.TORport);
			socket2.send(tor);
			byte[] get2 = new byte[2000];
			DatagramPacket receive2 = new DatagramPacket(get2,get2.length);
			
			socket2.receive(receive2);
			//builder.append(new String(receive2.getData())).append(",");
			strdatator=(new String(receive2.getData()).trim());
			
			System.out.println("tor"+(new String(receive2.getData()).trim()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			socket1.close();
			socket2.close();	
		}
		totEvents=totEvents+strdatamtl+strdatator;
		return totEvents;

	}
	
	public OTWserver()
	{
		super();
	}


	
	public static void cControl()
	{
		
		DatagramSocket socket = null;
		System.out.println("cContol started");
		try
		{
			socket = new DatagramSocket(6790);
		
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
					responseStr = server.viewBookedEvents2(requestarr[0].trim());
					} 
				 
				else if (requestarr.length == 2) {
					server.logWritter("Recived request for delete events");
					responseStr = server.deleteEvent2(requestarr[0].trim(),requestarr[1]);
					}

				else if (requestarr.length == 3) {
						
					System.out.println("mtlserver id");
					responseStr = server.addEvent(requestarr[0], requestarr[1],Integer.parseInt(requestarr[2]));
					}
				else if (requestarr.length == 4){
					server.logWritter("Recived request for booked events");
					responseStr = server.viewEvents2(requestarr[0]);
				}
				else if(requestarr.length == 5) {
					responseStr = server.bookEvents(requestarr[0],requestarr[1],requestarr[2]);
					
				}
				else if (requestarr.length == 6) {
					responseStr = server.cancelEvents(requestarr[0], requestarr[1], requestarr[2]);
					}
				else if(requestarr.length == 7) {
					responseStr = server.swapEvent(requestarr[0],requestarr[1],requestarr[2],requestarr[3],requestarr[4]);			
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
		
	}
	
	
	
	public static void main(String args[])
	{
		server = new OTWserver();
		server.addEvent("C", "OTWM000000", 1);
		server.addEvent("T", "OTWM000000", 1);
		server.addEvent("S", "OTWM000000", 1);
		
		try {

			Runnable task= () -> {
				try {
					cControl();
				}catch(Exception e) {
					e.printStackTrace();
				}
			};
			
			Thread thread1 = new Thread(task);
			thread1.start();
			
			System.out.println("Ottawa server has started");
			DatagramSocket socket = null;
			
			try
			{
				socket = new DatagramSocket(OTWport);
				
				while(true)
				{
					byte[] get = new byte[256];
					byte[] send = new byte[2000];
					DatagramPacket request = new DatagramPacket(get, get.length);
					socket.receive(request);
	        
	                String requestStr = new String(request.getData());
	                System.out.println(requestStr);
	                requestStr = requestStr.trim();
	                String[] requestarr=requestStr.split("#");
					
	            	String responseStr="";
	                if(requestarr.length == 3)
					{
	                	responseStr = server.bookEvents(requestarr[0], requestarr[1],requestarr[2]);

					}
	                else if(requestarr.length == 2)
	                {

	        			server.logWritter("Request for view events");
	                	responseStr=server.viewEvents(requestarr[0].trim());
	                	
	                }
	                else if(requestarr.length == 1)
	                {	
	                	responseStr = server.viewBookedEvents(requestarr[0].trim());                        
	                    if(responseStr.length()>0)
	                       responseStr = ","+responseStr;
	                }
	                else if(requestarr.length == 4)
	                {
	                	responseStr = server.cancelEvents(requestarr[0], requestarr[1],requestarr[2]);

	                }
	                else
	                	responseStr = "Invalid Request!";
	         
	                //System.out.println("bfr send"+responseStr);
	                
	                send = responseStr.getBytes();        
					DatagramPacket reply = new DatagramPacket(send, send.length, request.getAddress(),request.getPort());
					socket.send(reply);
					responseStr="";
				}
					
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket.close();
			}
			
				} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
