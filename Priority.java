/*
 * CS350: HW7
 *
 * N-batch concurrent execution part a
 *
 * Author: Megan Horan(mrhoran@bu.edu)
 * */
import java.util.concurrent.Semaphore;


class Priority extends Thread
{
    private int id;
    
    static int N = 6;
    
    static int num_processes = 0;
    
    static int priority = 0; //a positive value representing the "priority" of Pi
    
     static int [] num_times =  new int[N]; // keeps track of the number of time a thread has asked for the critical section
     
    // this is an array that will hold the priorities of the given processes
    static int [] R =  new int[N];
    // this semaphore will allow mutual exclusion for process when they are incrementing the count variable
    
    static Semaphore mutex1 = new Semaphore(1, false); 
    
    // this semaphore will allow mutual exclusion for process when they are decrementing the count variable
   
    static Semaphore mutex2 = new Semaphore(1, false); 
    
    // lock for print statements
    
    static Semaphore print_lock = new Semaphore(1, false); 
    
    // this semaphore is the binary semaphore that the new functions will be signaling
    
    static Semaphore B[] = new Semaphore[N];
    
    public Priority(int i)
    {
        id = i;
    }

    public void newWait(int i){
        
        // set priority of the process
        try{
        mutex1.acquire();
        } catch (InterruptedException e){} ;
        
        try {
            print_lock.acquire();
        } catch (InterruptedException e){} ;
            
        System.out.println("Process " + id + " is requesting CS");
            
        print_lock.release();
        
        priority++;
        
        R[i] = priority;
        
        num_processes++;
        
        mutex1.release();
        
        if(num_processes > 1){
            
            try{
            
                B[i].acquire(); // wait for the binary semaphre B[i]
            
             } catch (InterruptedException e){} ;
             
            // place this process in a queue ??
        }
          
    }
    

    public void newSignal(int i){
        
        // set priority of the process
        try{
        mutex2.acquire();
         } catch (InterruptedException e){} ;
        
        R[i] = 0; // set priority to 0 
        
        num_processes--; //Decrement the counter N
        
        mutex2.release();
         
        System.out.println("Process " + i + " is exiting CS");
        
        if(num_processes > 0){
       
            //If N > 0, then signal semaphore B[j], where R[j] >= R[k] for all k.
          
                int next_signal = -1;
                
                for(int j = 0; j < R.length; j++){
                    
                    if(R[j] > next_signal)
                        next_signal = R[j];
                
                 
                // signal the next priority process
                B[next_signal].release();
                
                
                }
          
        }
        
    }
    public void run()
    {
        while(true) {
            try {
              
                
                if(num_times[id] < 5){
                    newWait(id);
                    
                    // critical section of the priority semaphore
                    try{
                        print_lock.acquire();
                        
                    } catch (InterruptedException e){} ;
                    
                    
                    sleep((int) Math.random() * 1000);
                    
                    System.out.println("P"+id + " is in the CS.");
                    
                    print_lock.release();
                    
                    newSignal(id);
                    
                    num_times[id]++;
                    
                } 
            }catch(InterruptedException e) {
                    System.out.println(e);
                }
            
        }
    }

    public static void main(String[] args)
    {
        
        for(int j =0; j < N; j++){
        
            B[j] = new Semaphore(1, false); // setting up binary priority queue
        }
    
        Priority[] p = new Priority[N];

        for (int i = 0; i < N; i++)
        {
            p[i] = new Priority(i);
            p[i].start();
            
            if( i == 5)
                return;
        }
    }
}