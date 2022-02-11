package floorSubsystem;

import input.InputBuffer;
import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class FloorSubsystem implements Runnable{

    private Scheduler buf;
    private InputBuffer text;
    private ArrayList<String> new_data;
    private ArrayList<String> input_data;
    private int counter;


    public FloorSubsystem(Scheduler buf, InputBuffer text){
        this.buf = buf;
        this.text = text;
        this.counter = 0;
    }
    
    public ArrayList<String> getNewData() {
    	return new_data;
    }
    
    public ArrayList<String> getInputData(){
    	return input_data;
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
        	input_data = text.recieveFromInputBuffer();
            System.out.println(Thread.currentThread().getName() + " is sending " + input_data + " to Scheduler.");
            buf.sendToScheduler(input_data);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new_data = buf.recieveFromScheduler();
            System.out.println(Thread.currentThread().getName() + " has recieved " + new_data + " from Scheduler.\n");
            
            counter++;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        
        	}
       }
}
