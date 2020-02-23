package FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Client.Logger;
 

// Sequencer takes the msg and forwards it to the server


public class Sequencer {

	    public static int sequenceNumber = 0;
	    final static String INET_ADDR = "224.0.0.3";
	    final static int sendPORT = 8888;
	    public static  Sequencer seqObj;
	    
	    // to receive from the front end
	    final static int receivePORT = 1236;
//	    
//	    final static String replicaManagerAddress1 = "localhost";
//	    final static String replicaManagerAddress2 = "132.205.46.130";
//	    final static String replicaManagerAddress3 = "132.205.46.139";
//	    final static String replicaManagerAddress4 = "132.205.46.152";
//	    
//	    
	    public void logWritter(String log)
		{
			String Path1 = "LogServer/" + "SequencerLog.txt";
			try {
				Logger.writeLog(log, Path1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    
	    
	    
	    public static void main(String[] args) throws UnknownHostException, InterruptedException {
	        
	    	seqObj = new Sequencer();
	    	InetAddress addr = InetAddress.getByName(INET_ADDR);

	    	// to do
	    	// this try block to be eliminated as we get the data from the front end
	    	// this data hyas to be forwarded to the server
	    	
	        try {
	        	
	        	DatagramSocket serverSocket = new DatagramSocket(receivePORT);    
	                 
	         while(true) {
	        	 	byte[] buf = new byte[256];
	     	       
	            DatagramPacket msgPacket2 = new DatagramPacket(buf, buf.length);
	            serverSocket.receive(msgPacket2);
	     
	            String msg2 = new String(buf, 0, buf.length);
	            String request = msg2.trim();
	            System.out.println("Message received by the sequencer is : " + msg2);
	            seqObj.logWritter("Message received by the sequencer is : " + msg2);
	            String requestarr[] = request.split("#");
	            
	            if(requestarr.length > 1)
	            {
	            	request = Integer.toString(sequenceNumber) + "#" + request;
					sequenceNumber++;
	            	/*DatagramPacket msgPacket = new DatagramPacket(request.getBytes(),request.getBytes().length, addr, sendPORT);
		            serverSocket.send(msgPacket);*/
		            
					DatagramSocket aSocket = null;
					try
	            	{
	            		InetAddress address = InetAddress.getByName(INET_ADDR);
//	            		InetAddress address2 = InetAddress.getByName(replicaManagerAddress2);
//	            		InetAddress address3 = InetAddress.getByName(replicaManagerAddress3);
//	            		InetAddress address4 = InetAddress.getByName(replicaManagerAddress4);
//	            		
	            		
	            		aSocket = new DatagramSocket();
	    				//System.out.println("######### Message to be sent is ######## : " + request);
	    				byte[] message1 = new byte[1000];
	    				message1 = request.getBytes();
	    				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, sendPORT);
//	    				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address2, sendPORT);
//	    				DatagramPacket request3 = new DatagramPacket(message1, message1.length, address3, sendPORT);
//	    				DatagramPacket request4 = new DatagramPacket(message1, message1.length, address4, sendPORT);
//	    				
	    				System.out.println("######### Request message sent from the sequencer : "
	    						+ new String(request1.getData())  + "  to the replica manager with IP address is :"+INET_ADDR +"#######");
	    				seqObj.logWritter("######### Request message sent from the sequencer : "
	    						+ new String(request1.getData())  + "  to the replica manager with IP address is :"+INET_ADDR +"#######");
	    				aSocket.send(request1);
//	    				aSocket.send(request2);
//	    				aSocket.send(request3);
//	    				aSocket.send(request4);
//	    				
	    				
	            	}
	            	catch(Exception e)
	            	{
	            		System.out.println("Exception while sending multi cast request to replica managers :"+e.getMessage());
	            		e.printStackTrace();
	            	}
					
					
	            }
	  
	         }
	            
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	 
}
