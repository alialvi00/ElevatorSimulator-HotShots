package floorSubsystem;

import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class FloorSubsystem implements Runnable{

    private Scheduler buf;
    private ArrayList<String> input_data;
    private ArrayList<String> new_data;
    private static LinkedBlockingQueue<ArrayList<String>> inputTextBuffer;
    private int counter;


    public FloorSubsystem(Scheduler buf){
        this.buf = buf;
        this.counter = 0;
        inputTextBuffer = new LinkedBlockingQueue<>();
    }
    
    public static void sendToInputTextBuffer(ArrayList<String> input) {
    	 try {
             inputTextBuffer.put(input);
             System.out.println("Reader placed " + input + " in the input queue.");
         }catch (InterruptedException e){
             e.printStackTrace();
         }
    }
    
    public ArrayList<String> getInputFromTextBuffer(){
    	
    	try {
            input_data = inputTextBuffer.take();
            System.out.println("Floor Removed " + input_data + " from the input queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return input_data;
    }

    
    public ArrayList<String> getData() {
    	return new_data;
    }

    @Override
    public void run() {

        while(counter < Reader.getLineCounter()) {
        	
        	getInputFromTextBuffer();
            System.out.println(Thread.currentThread().getName() + " is sending " + input_data + " to Scheduler.");
            buf.sendToScheduler(input_data);

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
