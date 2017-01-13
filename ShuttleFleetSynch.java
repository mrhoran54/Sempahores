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

class ShuttleFleetSynch extends Thread
{
    private int id;
    
    private String type;
   
    static int riders = 50;
    
    static int K = 6;
    
    static int N = 50; // number of riders allowed on each shuttle
    
    static int M = 3; // number of shuttles in the fleet
    
    volatile int count = 0;
    
    volatile int available = N; // number of spots left on the shuttle

    static int THIS_STOP = -1;
    static int NEXT_STOP = 0;
    
    static int random_stop;
    static int next_stop;
    
    // these are the semaphore arrays to coordinate the different floors
   
    static Semaphore [] waiting = new Semaphore[K]; 
    
    static Semaphore [] riding = new Semaphore[K]; 
    
    // they will both all initilized to 0 to allow residents to wait to be picked up by the shuttle or wait to arrive at a stop
    
    // these two arrays specify who is waiting on what floor and who is riding
    
    static int [] waitingCounts = new int [K]; 
    
    static int [] ridingCounts = new int [K];
    
    // these are two mutexies to ensure mutual exclusion of the waitingCounts and ridingCounts array (binary initlized to 1) 
    
    static Semaphore mutex_wait = new Semaphore(1, false);
    
    static Semaphore mutex_ride = new Semaphore(1, false);
   
    // this semaphore keeps set to size k, will all be initialzed ot M
    // that means that at any given time at most M shuttles can be servicing the K floors
    
    static Semaphore [] shuttle_limit = new Semaphore[K];
    
    // only lets one shuttle to go to a stop at a given time: ie there is a mutex around the elevator code,
    // which means that only one shuttle will be at a stop at a given time
    
    static Semaphore shuttle_mutex = new Semaphore(1, false); 
    
    static Semaphore door = new Semaphore(0, false); 
    
    public ShuttleFleetSynch(int i, String t)
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
           int x = 0;
           
            while(true){
                
                x++;
                //wait on riding shuttle and the waiting shuttle
                mutex_wait.acquire();
                mutex_ride.acquire();
              
                // only execute one elevator thread at a time
                shuttle_mutex.acquire();
                
                // go to each stop in order
                NEXT_STOP = ((THIS_STOP +1) % K);
                
                System.out.println("Shuttle  is now going to stop " + NEXT_STOP);
                
                //ride for a bit:
                
                sleep((int) Math.random() * 3000);
                
               
                THIS_STOP = NEXT_STOP;
                
                
            // if there are no riders just shut the door
             
                
                //**open the door
                System.out.println("opening the door");
                
                
                if(ridingCounts[THIS_STOP] == 0){ // dont even stop
                    
                    System.out.println("Elevator will not stop here. Not enough passengers");
                    System.out.println("Closing the door");
                    
               }
                
                // let people walk out if there is room 
                for(int m = 0; m < ridingCounts[THIS_STOP]; m++){
                    
                    shuttle_limit[THIS_STOP].release(); // keeps track of whether a shutle is available
                    
                    riding[THIS_STOP].release();
                    
                    
                    System.out.println("A rider is leaving the shuttle at stop " + THIS_STOP);
                    available++;

                }
                
                
                // let people walk on if there is availible space
                
                // see how much space we need, ie the smaller of these two 
                
                int n = min(waitingCounts[THIS_STOP], available);
                
                for(int k = 0; k < n; k++){
                    
                    // release waiting floor mutex
                    waiting[THIS_STOP].release();
                    
                    shuttle_limit[THIS_STOP].release(); // wait for elevators 
                    
                    available--;
                   
                    
                }
                 //**close the door
                
                door.release();
               
                
                shuttle_mutex.release();
                
                mutex_wait.release();
                mutex_ride.release();
               
                sleep(2000);

                if(x == 10)
                    return;
               // keep repeating
               
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
                
                // wait on the the semaphore for that floor
                waiting[random_stop].acquire();
                
                shuttle_limit[random_stop].acquire(); // wait for elevators 
              
                System.out.println("Rider" + this.id + " got to their stop and is now on a shuttle");
                
                 // elevator got to my floor and let me in
                
                mutex_wait.acquire();
                
                waitingCounts[random_stop]--;// no longer waiting
   
                mutex_ride.acquire();
                
                // choose nextFloor;
                 
                next_stop = (int)(Math. random() * K + 0);
                
                ridingCounts[next_stop]++;
                 
                System.out.println("Rider" + this.id + " selected stop " + next_stop+ " as their destination and is now riding");

                
                mutex_ride.release();
                mutex_wait.release();
                
                riding[next_stop].acquire();
                
                shuttle_limit[next_stop].acquire();
                
                // elevator got to nextFloor and released me
                
                System.out.println("Rider" + this.id + " got to their next floor " + THIS_STOP+ " and was released");
                
                mutex_ride.acquire();
                
                ridingCounts[next_stop]--;
               
                mutex_ride.release();
                
    
                if(ridingCounts[random_stop] == 0){
                    
                    door.acquire();
                    
                    System.out.println("Door is shutting");
                    
                    
              } 
                random_stop = next_stop;
                
            }
        
        }catch(InterruptedException e) {
                
                System.out.println(e);
            }
    }
    
   public static void main(String[] args)
    {
        
         
        ShuttleFleetSynch[] p = new ShuttleFleetSynch[riders];
        
        for(int j = 0; j < K; j++){
        
            waiting[j] = new Semaphore(0, false);
            riding[j] = new Semaphore(0, false);
            
            shuttle_limit[j] = new Semaphore(M, false);
           
        }
        
        for (int i = 0; i < riders; i++)
        {
            if(i == 1){
                p[i] = new ShuttleFleetSynch(i, "shuttle");
                p[i].start();
            }
            
            else{
                
                p[i] = new ShuttleFleetSynch(i, "rider");
                p[i].start();
                
            }
        }
    }
    
}
    