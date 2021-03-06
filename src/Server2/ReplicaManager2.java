
package Server2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ReplicaManager2  {
	
	public static MTLserver2 mtlserver ;
	public static OTWserver2 otwserver ;
	public static TORserver2 torserver ;

	public static Queue<String> requestOrder = new LinkedList<>();
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
			       System.out.println("Request Message : " + msg.trim());
			       msg = msg.trim();
			       String arr[] = msg.split("#");
			       
			       int requestnum = Integer.parseInt(arr[0]);
			       String tmp = "";
			       String response ="";
			       for(int i =0 ; i <msg.length();i++ ){
			            if(msg.substring(i,i+1).equals("#"))
			            {
			                tmp = msg.substring(i+1);
			                break;
			            }
			        }
			       
			       //server sleep mode
			       if(requestnum > 2 && requestnum < 5)
				    {
			    	     System.out.println("**************Crashed**********************");
				    	 requestOrder.add(tmp);	
				    }
			        //backup flow
			       else
				    {
			    	    if(requestnum == 5)
			    	    {
			    	       System.out.println("****************Recovered*******************");
			    	    }
			        	requestOrder.add(tmp);
				    	while(!requestOrder.isEmpty())
				    	{
				    		String reqOrder ="";
				    		reqOrder = requestOrder.remove();
				    		String arrnew[] = reqOrder.split("#");
				    		String serverName = arrnew[0];
				    		String tmpx = "";
						       for(int i =0 ; i < msg.length();i++ ){
						            if(reqOrder.substring(i,i+1).equals("#"))
						            {
						                tmpx = reqOrder.substring(i+1);
						                break;
						            }
						       }    	       
						       String[] requestarr = tmpx.split("#");
							   System.out.println("Send Message "+tmpx+" Lenght: "+requestarr.length);
							       
							    //send the msg to the represented server()
							    int sendPort = 0;
							    
							    if(serverName.equals("MTLport")) {
							    	sendPort = 6792;
							    }
							    else if(serverName.equals("OTWport"))
							    {
							    	sendPort = 6793;
							    }
							    else if(serverName.equals("TORport"))
							    {
							    	sendPort = 6794;
							    }
							    System.out.println("Message is being send to: "+serverName);
							    //System.out.println("Message to be send is: "+tmpx);
							    
							    response = "EMPTY";
				
							    DatagramSocket socket= null;
							    try
							    {
							    	socket= new DatagramSocket();
							    	byte[] send = tmpx.getBytes();
								    InetAddress address2 = InetAddress.getByName("localhost");
								    DatagramPacket sendMsg = new DatagramPacket(send, send.length, address2 ,sendPort);
								    socket.send(sendMsg);
								    System.out.println("Message is SENT");
								    
									byte[] receiveBytes = new byte[1000];
									DatagramPacket reply = new DatagramPacket(receiveBytes,receiveBytes.length);
									socket.receive(reply);
									response = new String(reply.getData());
									response = response.trim();
									System.out.println("Response : "+response);
							    }catch(Exception ex)
								{
									System.out.println("Exception! ELSE");
								}
								finally{
									socket.close();
								}
				    	}
				    	
				    }
			        
			    if(requestnum == 1)
			    {
			    	//server has a bug in it
			    	String responseStr = "There is bug in  the system";
					byte[] sendhome = responseStr.getBytes();
					InetAddress addresshome = InetAddress.getByName("132.205.46.151");
					DatagramPacket reply = new DatagramPacket(sendhome, sendhome.length, addresshome ,1235);
					clientSocket.send(reply);
			    }
			    else
			    {
			    	String responseStr = response;
					byte[] sendhome = responseStr.getBytes();
					InetAddress addresshome = InetAddress.getByName("132.205.46.151");
					DatagramPacket reply = new DatagramPacket(sendhome, sendhome.length, addresshome ,1235);
					clientSocket.send(reply);
					    
			    }
			   }
			   }catch (IOException ex) {
				   ex.printStackTrace();
				}finally {
					clientSocket.close();
				}
	}
	
	public static void main(String args[]) throws IOException {
		
		System.out.println("ReplicaMansager2 started ..........");
		startReplicaManager();
			
		}
		
}