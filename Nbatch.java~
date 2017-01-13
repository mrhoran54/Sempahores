/*
 * CS350: HW7
 *
 * N-batch concurrent execution
 *
 * Author: Megan Horan(mrhoran@bu.edu)
 * 
 */

import java.util.concurrent.Semaphore;

class Nbatch extends Thread
{
    private int id;
    
    static int N = 12;
    
    static final int COUNT = 3;        
    
    static int counter = 0;
        
    static int batch = 0;
    
    // this semaphore will allow mutual exclusion for process when they are executing the code in the batch
   
    static Semaphore mutex2 = new Semaphore(1, false); 
    
    // this semaphore will ensure that in the batch only one process is accessing the semaphore at the same time
    
    static Semaphore space = new Semaphore(3, false);
    
    public Nbatch(int i)
    {
        id = i;
    }

    public void run()
    {
      
        try {
            
            sleep(3000);
                
                // wait to see if there is space
                
            space.acquire();
               
            System.out.println("Process " + id + " in batch " + batch );
                            
            sleep((int) Math.random() * 5000);
            
            counter++;
                    
            if(counter == COUNT){
                        
                mutex2.acquire();
               
                //sleep(4000);
                       
                System.out.println("batch " + batch + " is done");
                        
                batch++;
                      
                counter = 0;
                        
                mutex2.release();
                       
                for(int i =0; i< COUNT; i++)
                        
                    space.release();
            }
            
}catch(InterruptedException e) {
    
                System.out.println(e);
            }
        
    }

    public static void main(String[] args)
    {
        
        Nbatch[] p = new Nbatch[N];

        for (int i = 0; i < N; i++)
        {
            p[i] = new Nbatch(i);
            p[i].start();
        }
    }
}