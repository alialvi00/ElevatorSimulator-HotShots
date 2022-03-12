package scheduler;

import elevatorSubsystem.ElevatorRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class ElevHandler implements Runnable{

    DatagramSocket receiveSocket;

    DatagramPacket elevData;

    DatagramPacket responseData;

    ElevatorRequest dataRequest;

    Scheduler scheduler;

    public ElevHandler(Scheduler scheduler) {

        this.scheduler = scheduler;

        try {
            receiveSocket = new DatagramSocket(79);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //convert bytes mssg to object
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


    public void run() {

        while(true) {
            handleElevator();
        }
    }
}
