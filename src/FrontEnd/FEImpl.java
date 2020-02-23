package FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Client.Logger;
import FrontEndApp.FrontEndInterface;
import FrontEndApp.FrontEndInterfaceHelper;
import FrontEndApp.FrontEndInterfacePOA;
import RemoteInterface.remote;
import RemoteInterface.remoteHelper;

public class FEImpl extends FrontEndInterfacePOA {

	private org.omg.CORBA.ORB orbFE;
	public static DatagramSocket ds;
	public static FEImpl FEobj;
	
	public void logWritter(String log)
	{
		String Path1 = "LogServer/" + "FEImplLog.txt";
		try {
			Logger.writeLog(log, Path1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public FEImpl() {
		super();
				try {
					ds= new DatagramSocket(1235);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
	}
	
	// send the msg when the function is been called
	public void sendRequesttoSeq(String data)
	{
		try {
			String sendMsg = data;
			byte[] send = sendMsg.getBytes();
			System.out.println("Message sent to the sequencer is: "+sendMsg);
			FEobj.logWritter("Message sent to the sequencer is: "+sendMsg);
			InetAddress address = InetAddress.getByName("localhost");
	        DatagramPacket seq = new DatagramPacket(send,send.length,address,Sequencer.receivePORT);				
			ds.send(seq);				
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	// receive should be implemented seperately as it gets data from the server
	public static ArrayList<String> al = new ArrayList<String>(); 
	
	public String receiveRequest()
	{
		
		String returnMsg = "";
		try {
		//long startTime = System.currentTimeMillis(); //fetch starting time
		//	System.out.println("a1 data size is one :"+al.size());
			ds.setSoTimeout(10000);
			while(al.size() < 4) 
			{
				
				//System.out.println("running recieve");
				int curs = al.size();
				byte[] get = new byte[2000];
				DatagramPacket receive = new DatagramPacket(get,get.length);
				
				ds.receive(receive);
				String strdata = (new String(receive.getData()).trim());
				al.add(strdata);
				
				if(curs < al.size())
				{
					System.out.println("Received message by the front end : "+al);
					FEobj.logWritter("Received message by the front end : "+al);
				}
				
			}
		//	System.out.println("the list is :"+al);
			
		HashMap <String,Integer> hm = new HashMap<String,Integer>();
		
		for(int i =0; i< al.size();i++)
		{
			if(hm.containsKey(al.get(i))) {			
				hm.put(al.get(i), hm.get(al.get(i)) + 1);
			}
			else
			{
				hm.put(al.get(i), 1);
			}
		}
		al.clear();
		System.out.println(" Key Value pair of the map is  : "+hm);
		int returnVal = Collections.max(hm.entrySet(),Map.Entry.comparingByValue()).getValue();
		
		//hm.get
		for(String s: hm.keySet())
		{
		    if(hm.get(s) == returnVal && s.length() > 1)
		    {
		    	returnMsg = s;
		    }
		}
		
		}catch (SocketTimeoutException e ) {
			System.out.println(e.getMessage());
			if(al.size() > 0)
			{
			for(int i = 0; i < al.size(); i++)
			{
				if(al.get(i).length() > 5)
				{
					String msg = al.get(i);
					al.clear();
					return msg;		
				}
				
			}
			}
			else
			{
				return e.getMessage();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMsg;
		
	}
	
	public String receiveRequest1()
	{

		String returnMsg = "";
		try {
		
			ds.setSoTimeout(10000);
			while(al.size() < 4) 
			{
				
			//	System.out.println("running recieve");
				int curs = al.size();
				byte[] get = new byte[2000];
				DatagramPacket receive = new DatagramPacket(get,get.length);
				
				ds.receive(receive);
				String strdata = (new String(receive.getData()).trim());
				al.add(strdata);
				
//				if(curs < al.size())
//				{
//					System.out.println("Msessage has been received: "+al);
//				}
//				
			}
			//System.out.println("the list is :"+al);
			HashMap <Set<String>,Integer> hm = new HashMap<Set<String>,Integer>();
			
			TreeSet<String> idSet = new TreeSet<String>();
			for(int i =0; i < al.size(); i++)
			{
				String tem = al.get(i);
				String arrtemp[] = tem.split(",");
				tem ="";
				for(int j = 0; j < arrtemp.length; j++)
				{
					if(arrtemp[j].length() > 1)
					{
						idSet.add(arrtemp[j]);
						
					}
				}
			
		
			if(hm.containsKey(idSet)) {			
				hm.put(idSet, hm.get(idSet) + 1);
			}
			else
			{
				hm.put(idSet, 1);
			}
		}
		al.clear();
		System.out.println(" Key Value pair of the map is  : "+hm);
		
		returnMsg = Collections.max(hm.entrySet(),Map.Entry.comparingByValue()).getKey().toString();
		
		}catch (SocketTimeoutException e ) {
			System.out.println(e.getMessage());
			if(al.size() > 0)
			{
			for(int i = 0; i < al.size(); i++)
			{
				if(al.get(i).length() > 5)
				{
					String msg = al.get(i);
					al.clear();
					return msg;		
				}
				
			}
			}
			else
			{
				return e.getMessage();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMsg;
		
	
		
	}
	
	String getServerName(String userID)
	{
		if(userID.contains("MTL"))
			{
				return "MTLport";
			}
		else if(userID.contains("OTW"))
			{
				return "OTWport";
			}
		else if(userID.contains("TOR"))
		   {
				return "TORport";
		   }
			else
			return "Wrong City";
	}
	 
	public void setORB(org.omg.CORBA.ORB orb_val) {
		orbFE = orb_val;
	}
	// implement shutdown() method
	public void shutdown() {
		orbFE.shutdown(false);
	}
	
	
	public static void main(String args[])
	{
		
		FEobj = new FEImpl();
		
		
		try {
			org.omg.CORBA.ORB orb = ORB.init(args, null);
			
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			FEobj.setORB(orb);

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(FEobj);
			FrontEndInterface href = FrontEndInterfaceHelper.narrow(ref);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			System.out.println("FrontEndImpl is up and running");
			FEobj.logWritter("FrontEndImpl is up and running");
			NameComponent path[] = ncRef.to_name("FEport");
			ncRef.rebind(path, href);
			orb.run();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public String addEvent(String userID, String eventType, String eventId, int bookingCapacity) {
	    String portAddress = getServerName(userID);
	    sendRequesttoSeq(portAddress + "#" + eventType + "#" + eventId + "#" + Integer.toString(bookingCapacity)+"#") ;
	    return receiveRequest();
	}

	@Override
	public String deleteEvent(String userID, String eventType, String eventId) {
	      String portAddress = getServerName(userID);
	      sendRequesttoSeq(portAddress + "#" + eventType + "#" + eventId + "#");	
	      return receiveRequest();
	}

	@Override
	//TO DO IS 
	public String listEventAvailability(String userID, String eventType) {
		String portAddress = getServerName(userID);
		sendRequesttoSeq(portAddress + "#" + eventType + "#a#b#c#");	
		return receiveRequest1();
	}

	@Override
	public String bookEvent(String customerId, String eventType, String eventId) {
		String portAddress = getServerName(customerId);
		  sendRequesttoSeq (portAddress + "#" + customerId + "#" + eventType + "#" + eventId + "#a#b#");
		  return receiveRequest();
	}

	@Override
	public String getBookingSchedule( String customerId) {
		String portAddress = getServerName(customerId);
		  sendRequesttoSeq (portAddress +"#" + customerId + "#");
		  return receiveRequest1();
	}

	@Override
	public String cancelEvents(String customerId, String eventType, String eventId) {
		String portAddress = getServerName(customerId);
		  sendRequesttoSeq( portAddress + "#" + customerId + "#" + eventType + "#" + eventId + "#a#b#c#");
		  return receiveRequest();
	}

	@Override
	public String swapEvent(String customerId, String newEventId, String newEventType, String oldEventId,
			String oldEventType) {
		String portAddress = getServerName(customerId);
	      sendRequesttoSeq (portAddress + "#" + customerId + "#" + newEventId + "#" + newEventType + "#" + oldEventId + "#" + oldEventType + "#a#b#");
	      return receiveRequest();
	}
	
	
	
}
