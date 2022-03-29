package elevatorSubsystem;


import scheduler.Scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;


public class ElevatorSubsystem implements Runnable {

    private HashMap<Integer, Elevator> elevatorMapping;
    private DatagramSocket receivingSocket;
    private DatagramSocket sendingSocket;
    private static final int numOfElevators = 4;
    
    public ElevatorSubsystem(){
        elevatorMapping = new HashMap<>();
        try {
            receivingSocket = new DatagramSocket(20);
            sendingSocket = new DatagramSocket();
        } catch (SocketException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * starts elevator threads and receives requests from the scheduler
     * uses the elevator hashmap to assign the received requests based on the request ID
     */
    @Override
    public void run() {
        //populate the elevator dictionary
        for(int i = 1; i <= numOfElevators; i++){
            Elevator elevator = new Elevator(i, this);
            elevatorMapping.put(i, elevator);
        }

        //start all the threads
        for(Elevator elevator : elevatorMapping.values()){
            Thread elevatorThread = new Thread(elevator);
            elevatorThread.start();
        }


        while(true){
            try{
                DatagramPacket receivePacket = new DatagramPacket(new byte[700], 700, InetAddress.getLocalHost(), 20);
                receivingSocket.receive(receivePacket);
                ElevatorRequest receivedRequest = (ElevatorRequest) bytesToObj(receivePacket);
                elevatorMapping.get(receivedRequest.getID()).setExecutingRequest(receivedRequest);
                try{
                    Thread.sleep(500);
                } catch(InterruptedException e){
                    e.printStackTrace();
                    System.exit(-1);
                }
            } catch (Exception e){
                e.printStackTrace();
                System.exit(-1);
            }

        }
    }
    

    /**
     * this is used by elevator threads to send a request to the scheduler
     * @param request
     */
    public synchronized void sendRequest(ElevatorRequest request){
        byte[] requestByteArray = request.byteRepresentation();
        try {
            DatagramPacket packetToSend = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 79);
            sendingSocket.send(packetToSend);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * converts bytes to elevator to object
     * @param request
     * @return
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
     * Getter for elevator mapping hashmap
     * @return
     */
    public HashMap<Integer, Elevator> getElevatorMapping() {
        return elevatorMapping;
    }

    /**
     * Method to close receiving and sending sockets // halting/shuttingdown the subsystem.
     */
    public void shutDown(){
        receivingSocket.close();
        sendingSocket.close();
    }

    public static void main(String args[]) {
        Thread e = new Thread(new ElevatorSubsystem());
        e.start();
    }
}
