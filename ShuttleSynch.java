/*
 * CS350: HW8
 *
 * shuttle synch
 *
 * Author: Megan Horan(mrhoran@bu.edu)
 * 
 */

import java.util.concurrent.Semaphore;
import java.util.Random;

class ShuttleSynch extends Thread
{
    private int id;
    
    private String type;
   
    static int riders = 50;
    
    static int K = 6;
    
    static int N = 10; // number of riders allowed on each shuttle
    
    volatile int available = N; // number of spots left on the shuttle
    
    static int THIS_STOP = -1;
    static int NEXT_STOP = 0;
    
    static int random_stop;
    static int next_stop;
    
    // these are the semaphore arrays to coordinate the different stops, one for waiting and running
    // they are all set to 0 because you only want people riding or waiting on when the elevator releases the semaohores
    // ie the elevator reaches their floor
   
    static Semaphore [] waiting = new Semaphore[K]; 
    
    static Semaphore [] riding = new Semaphore[K]; 
    
    // these two int arrays specify who is waiting on what floor and who is riding
    
    static int [] waitingCounts = new int [N]; 
    
    static int [] ridingCounts = new int [N];
    
    // these are two mutexies to ensure mutual exclusion of the waitingCounts and ridingCounts array (initlized to 1) 
    
    static Semaphore mutex_wait = new Semaphore(1, false);
    
    static Semaphore mutex_ride = new Semaphore(1, false);
    
    // this mutex ensures that the elevator door will wait to close until a certain passenger tells it to shut
    static Semaphore door = new Semaphore(0, false); 
    
    public ShuttleSynch(int i, String t)
    {
        id = i;
        type = t;
    }

    public int min(int x, int y){
        
        if(x < y)
            
            return x;
        
        else
            return y;
    }
    public void run()
    {
        try {
            
           
        // shuttle thread
        if(this.type == "shuttle"){
           
            int count = 0;
            
            while(true){
                
                //wait on riding shuttle and the waiting shuttle
                mutex_wait.acquire();
                mutex_ride.acquire();
                
                // variable to stop the simulation after a certain point
                count++;
                
                // go to each stop in order
                NEXT_STOP = ((THIS_STOP +1) % K);
                 
                //ride for a bit:
                
                sleep((int) Math.random() * 3000);
                
                THIS_STOP = NEXT_STOP;
                
                System.out.println("Shuttle is now going to stop " + THIS_STOP);
             
                
                //**open the door
                System.out.println("opening the door"); 
                
                
                 // if there are not enough  riders just shut the door
                
                if(ridingCounts[THIS_STOP] == 0 && waitingCounts[THIS_STOP] < 1){ // dont even stop
                    
                    System.out.println("Elevator will not stop here. Not enough passengers");
                    
                    //riding[THIS_STOP].release();
                    System.out.println("Door is shutting");
                   
                }
                
                // let people walk out if there are people getting off at the floor
                
                for(int m = 0; m < ridingCounts[THIS_STOP]; m++){
                    
                    riding[THIS_STOP].release(); // release the riding semaphore to actually let those riding out
                
                    System.out.println("A rider is leaving the shuttle at stop " + THIS_STOP);
                    available++;
                    
                }
                
                
                // let people walk on if there is availible space
                
                // see how much space we need, ie the smaller of these two values
                
                int n = min(waitingCounts[THIS_STOP], available);
               
                
                for(int k = 0; k < n; k++){
                    
                    // release waiting floor mutex
                    waiting[THIS_STOP].release();
                    
                    available--;
                   
                    
                }
                 
            //**wait on closing the dor door
                
                door.release();
                
                mutex_wait.release();
                mutex_ride.release();
               
                sleep(2000);
               // keep repeating
               if(count ==10)
                   return;
            }
          
        }
            
    // rider thread

        else if(this.type == "rider"){
    
                  //choose thisFloor at random
            
                mutex_wait.acquire();
                
                random_stop = (int)(Math.random() * K + 0);
                
                System.out.println("Rider" + this.id + " waiting at stop " + random_stop);
                
                waitingCounts[random_stop]++;

                mutex_wait.release();
                
                // wait on the the semaphore for that stop until the elevator gets there
                
                waiting[random_stop].acquire();
                
                // elevator has stopped at their stop and let them in
                
                System.out.println("Rider" + this.id + " got to their stop and is now on the shuttle.");
                
                mutex_wait.acquire();
                
                //decrement the waiting counter 
                
                waitingCounts[random_stop]--;// no longer waiting
   
                mutex_ride.acquire();
               
                // choose the desination floor;
                 
                next_stop = (int)(Math. random() * K + 0);
                
                // now they are riding!!!
                
                ridingCounts[next_stop]++;
                 
                System.out.println("Rider" + this.id + " selected stop " + next_stop+ " as their destination and is now riding");
                
                mutex_ride.release();
                mutex_wait.release();
                
                // wait on the riding semaphore until the floor releases it 
                
                riding[next_stop].acquire();
                
                System.out.println("Rider" + this.id + " got to their next floor " + THIS_STOP+ " and was released");
                
                mutex_ride.acquire();
                
                ridingCounts[next_stop]--;
               
                mutex_ride.release();
                
                // if it is the last rider to get off, close the door
                if(ridingCounts[next_stop] == 0){
                    
                    System.out.println("door is closed");
                    door.acquire();
                }
                    
                random_stop = next_stop;
            
        }
        
        }catch(InterruptedException e) {
                
                System.out.println(e);
            }
    }
    
   public static void main(String[] args)
    {
        
         
        ShuttleSynch[] p = new ShuttleSynch[riders];
        
        for(int j = 0; j < K; j++){
        
            waiting[j] = new Semaphore(0, false);
            riding[j] = new Semaphore(0, false);
         
        }
    
        
        for (int i = 0; i < riders; i++)
        {
            if(i == 1){
                p[i] = new ShuttleSynch(i, "shuttle");
                p[i].start();
            }
            
            else{
                
                p[i] = new ShuttleSynch(i, "rider");
                p[i].start();
                
            }
        }
    }
    
}