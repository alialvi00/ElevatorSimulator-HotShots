package elevatorSubsystem;

import input.Reader;
import scheduler.Scheduler;

import java.util.ArrayList;

public class ElevatorSubsystem implements Runnable {

    private Scheduler buf;
    private ArrayList<String> new_data;
    private int counter;

    public ElevatorSubsystem(Scheduler buf){
        this.buf = buf;
        this.counter = 0;
    }

    @Override
    public void run() {
    	
        while(counter < Reader.getLineCounter()){

            new_data = buf.recieveFromScheduler();
            System.out.println(Thread.currentThread().getName() + " has pulled " + new_data + " from Scheduler.");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " is sending " + new_data + " to Scheduler.");
            buf.sendToScheduler(new_data);

            counter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
