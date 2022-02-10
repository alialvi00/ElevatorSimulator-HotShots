package floorSubsystem;

import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class FloorSubsystem implements Runnable{

    private Scheduler buf;
    private static ArrayList<String> new_data;
    private int counter;


    public FloorSubsystem(Scheduler buf){
        this.buf = buf;
        this.counter = 0;
    }

    public static void sendData(ArrayList<String> data){
        new_data = data;
    }
    
    public ArrayList<String> getData() {
    	return new_data;
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
            System.out.println(Thread.currentThread().getName() + " is sending " + new_data + " to Scheduler.");
            buf.sendToScheduler(new_data);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new_data = buf.recieveFromScheduler();
            System.out.println(Thread.currentThread().getName() + " has recieved " + new_data + " from Scheduler.");
            counter++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
