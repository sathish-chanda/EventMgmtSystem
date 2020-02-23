package FrontEnd;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;




// front end communicates with the sequencer
// sends a request to the sequencer using multi cast

// gets the data from the sequencer and validates the data and 
// sends the most prominant result to the client


// to do
// add sequence number so that they can be grouped into 4 sets
// needed so that if u get to nmany msgs to differentite b/w msgs

public class FrontEnd {

	//method to send request to the sequencer
	
	//final static String INET_ADDR = "224.0.0.3";
	final static int sendPORT = 1234;
	final static int receivePORT = 1235;
	
	public static String Msg(String msg)
	{
		DatagramSocket socket1 = null;
		String sendMsg =  null;
		sendMsg = msg;
		byte[] send = sendMsg.getBytes();
		System.out.println(""+sendMsg);
		
		try {
			
			// send the msg when the function is been called
			InetAddress address = InetAddress.getByName("localhost");
			socket1 = new DatagramSocket();
	        DatagramPacket seq = new DatagramPacket(send,send.length,address,Sequencer.receivePORT);				
			socket1.send(seq);
			
							
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			socket1.close();	
		}
		
		ArrayList<String> al = new ArrayList<String>(); 
		DatagramSocket socket = null;
		
		try {
			
			socket = new DatagramSocket(receivePORT);
			//long startTime = System.currentTimeMillis(); //fetch starting time
		//	System.out.println("a1 data size is one :"+al.size());
			socket.setSoTimeout(2000);
			while(al.size() < 2) 
			{
				// receive should be implemented seperately as it gets data from the server
				byte[] get = new byte[2000];
				DatagramPacket receive = new DatagramPacket(get,get.length);
				socket.receive(receive);
				String strdata = (new String(receive.getData()).trim());
			//	System.out.println("mtl"+ strdata);
				al.add(strdata);
				
			}
			
			System.out.println("a1 data size is :"+al.size());
			System.out.println("the list is :"+al);
			
			
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
			//System.out.println("the map is :"+hm);
		}
		
		
		String returnMesg = Collections.max(hm.entrySet(),Map.Entry.comparingByKey()).getKey();
		
		return returnMesg;
		
		
//		String y[] = hm.keySet().toArray(new String[0]);
//		
//		for(int i = 0; i < y.length; i++ )
//		{
//			if(hm.get(y[i]) >= 2 )
//			{
//				System.out.println("y[i]"+y[i]);
//				return y[i];
//			}
//			else
//			{
//				return "There is is a byzantine fault";
//			}
//		}
		
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
		return "No data to return";
		
	}
	
	public static void main(String args[]) {
		
		// to call the front end function so as to run
		

		//System.out.println("Front return "+Msg("MTLport#MTLM100629#C#10"));
	}
}
