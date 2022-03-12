package scheduler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import floorSubsystem.FloorRequest;

/**
 * This class represents handling floor requests
 * It acts as a framework between floor subsystem and scheduler
 * @author Ali Alvi
 * @version 2022-03-12
 *
 */
public class FloorHandler implements Runnable{
	
	DatagramSocket receiveSocket;
	
	DatagramSocket sendSocket;
	
	DatagramPacket floorData;
	
	DatagramPacket responseData;
	
	FloorRequest dataRequest;

	Scheduler scheduler;
	
	public FloorHandler(Scheduler scheduler) {
		
		this.scheduler = scheduler;
		
		try {
			receiveSocket = new DatagramSocket(69);
			sendSocket = new DatagramSocket();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method is used to convert request Datagram packet
	 * into request object
	 * @param request is the request in datagram packet
	 * @return the request object
	 */
	public Object bytesToObj(DatagramPacket request) {
    	
    	ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getData());
    	ObjectInputStream objectStream = null;
    	Object requestObject = null;
    	
    	try {
			objectStream = new ObjectInputStream(new BufferedInputStream(inputStream));
			requestObject = objectStream.readObject();
			objectStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch(ClassNotFoundException ce) {
			ce.printStackTrace();
			System.exit(1);
		}
    	return requestObject;
    }
    
	/**
	 * Handle floor requests
	 */
	public void handleFloor() {
		
		//Establish Datagram Packets to recieve and response to. 
		try {
			floorData = new DatagramPacket(new byte[700], 700);
			responseData = new DatagramPacket(new byte[700], 700);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//Recieve Data and send back to Floor Subsystem.
		try {
			//Receive data from floor. 
    		receiveSocket.receive(floorData);
    		
    		//Converts to Floor request object. 
    		dataRequest = (FloorRequest) bytesToObj(floorData);
    		
    		
    		scheduler.addFloorRequest(dataRequest);
    		
    		//Set response data. 
    		String response = "Request acknowledged.";
    		responseData.setData(response.getBytes());
    		responseData.setLength(response.getBytes().length);
            responseData.setAddress(floorData.getAddress());
            responseData.setPort(23);
    		
    		//Send response data to floor. 
    		sendSocket.send(responseData);
    		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	//Run the thread
	public void run() {
		
		while(true) {
			handleFloor();
		}
	}
}
