package scheduler;

import elevatorSubsystem.ElevatorRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



/**
 * This class represents the handling of the elevator requests.
 * It acts as a framework between the elevator and the scheduler
 * @author Hassan Jallad, Akkash Kapoor
 * @version 2022-03-12
 *
 */
public class ElevHandler implements Runnable{

    DatagramSocket receiveSocket; //to receive requests

    DatagramPacket elevData; //to send elevator data

    DatagramPacket responseData; //response packet

    ElevatorRequest dataRequest; //request packet

    Scheduler scheduler; //scheduler

    public ElevHandler(Scheduler scheduler) {

        this.scheduler = scheduler;

        try {
            receiveSocket = new DatagramSocket(79); //port 79 used for receiving
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
     * Used to handle elevator requests
     */
    public void handleElevator() {

        //Establish Datagram Packets to recieve and response to.
        try {
            elevData = new DatagramPacket(new byte[700], 700);
        }catch (Exception e) {
            e.printStackTrace();
        }


        //Recieve Data and send back to Floor Subsystem.
        try {
            //Receive data from elevator subsystem.
            receiveSocket.receive(elevData);

            //Converts to Floor request object.
            dataRequest = (ElevatorRequest) bytesToObj(elevData);


            scheduler.addElevatorRequest(dataRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * To run the thread
     */
    public void run() {

        while(true) {
            handleElevator(); //handle the requests
        }
    }
}
