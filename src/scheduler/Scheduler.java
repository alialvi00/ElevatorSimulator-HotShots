package scheduler;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {
    private LinkedBlockingQueue<ArrayList<String>> buffer;
    private ArrayList<String> data;

    public Scheduler(){
        buffer = new LinkedBlockingQueue<>();
    }
    
    public LinkedBlockingQueue<ArrayList<String>> getBuffer(){
    	return buffer;
    }

    public void sendToScheduler(ArrayList<String> data){
        try {
            buffer.put(data);
            System.out.println("Scheduler placed " + data + " in the queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> recieveFromScheduler(){
        try {
            data = buffer.take();
            System.out.println("Scheduler Removed " + data + " from the queue.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return data;
    }

}
