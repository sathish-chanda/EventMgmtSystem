package Server1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ReplicaManager1  {
	
	public static MTLserver mtlserver ;
	public static OTWserver otwserver ;
	public static TORserver torserver ;

	
	

	// register into multi cast using port adress
	// receive request from seq\
	// add the nums and array to to array list
	// send the request
	
	public static void startReplicaManager() throws IOException
	{
	
		String INET_ADDR = "224.0.0.3";
		int PORT = 8888;
		InetAddress address = InetAddress.getByName(INET_ADDR);
		MulticastSocket clientSocket = new MulticastSocket(PORT);
	
		try {
			   clientSocket.joinGroup(address);

			   while (true) {
				   
				byte[] buf = new byte[256]; 
				   
			       DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
			       clientSocket.receive(msgPacket);

			       // received msg from the sequencer
			       String msg = new String(buf, 0, buf.length);
			       System.out.println("msg :"+msg.trim());
			       msg = msg.trim();
			       String arr[] = msg.split("#");
			       
			       //** TODO 
			       // add Into Hash map
			       String serverName = arr[1];
			       int requestnum = Integer.parseInt(arr[0]);
			           
			       String tmp = "";
			       
			       //**TODO
			       //adjust for loop to not consider first "2" #
			       for(int i =0 ; i <msg.length();i++ ){
			            if(msg.substring(i,i+1).equals("#"))
			            {
			                tmp = msg.substring(i+1);
			                break;
			            }
			        }
			       
			       String tmpx ="";
			       for(int i =0 ; i <msg.length();i++ ){
			            if(tmp.substring(i,i+1).equals("#"))
			            {
			                tmpx = tmp.substring(i+1);
			                break;
			            }
			        }
			    
			       tmp =tmpx;
			    String[] requestarr = tmp.split("#");
			    System.out.println("Send Message "+tmp+" Lenght: "+requestarr.length);
			       
			    //send the msg to the represented server()
			     int sendPort = 0;
			    
			    if(serverName.equals("MTLport")) {
			    	sendPort = 6789;
			    }
			    else if(serverName.equals("OTWport"))
			    {
			    	sendPort = 6790;
			    }
			    else if(serverName.equals("TORport"))
			    {
			    	sendPort = 6791;
			    }
			    
			    
			    System.out.print("Message is being send to: "+serverName);
			    System.out.println("Message to be send is: "+tmp);
				
			    String response="";
			    DatagramSocket socket= null;
			    try
			    {
			    	socket= new DatagramSocket();
			    	byte[] send = tmp.getBytes();
				    InetAddress address2 = InetAddress.getByName("localhost");
				    DatagramPacket sendMsg = new DatagramPacket(send, send.length, address2 ,sendPort);
				    socket.send(sendMsg);
				    	
				    byte[] receiveBytes = new byte[1000];
					DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
					socket.receive(reply);
					response = new String(reply.getData());
					response = response.trim();
				    
			    }catch(Exception ex)
				{
					System.out.println("Exception! ELSE");
				}
				finally{
					socket.close();
				}
			    
			    // send the msg to the server using udp local host
			    System.out.println("response from server is"+response);
			    
			    
			    // Implement the far from home sender over hear
			    //send the message to the FE using udp 
			    String responseStr = response;
				byte[] sendhome = responseStr.getBytes();
			    InetAddress addresshome = InetAddress.getByName("localhost");
			    DatagramPacket reply = new DatagramPacket(sendhome, sendhome.length, addresshome ,1235);
			    clientSocket.send(reply);
			    
			   }}catch (IOException ex) {
				   ex.printStackTrace();
				}finally {
					clientSocket.close();
				}
	}
	
	public static void main(String args[]) throws IOException {
		
		System.out.println("ReplicaMansager1 started ..........");
		startReplicaManager();
			
		}
		
}
