/*
 * CS350: HW7
 *
 * N-batch concurrent execution
 *
 * Author: Megan Horan(mrhoran@bu.edu)
 * 
 */

import java.util.concurrent.Semaphore;
import java.util.Random;

class TunnelSych extends Thread
{
    private int id;
    
    private int direction;
    
    static int direction_0 = 0; // counter for direction 0
      
    static Semaphore mutex0 = new Semaphore(1, false); // protection for direction_0
    
    static int direction_1 = 0; // counter for direction 1
    
    static Semaphore mutex1 = new Semaphore(1, false); // protection for direction_1
    
    static int N = 4; // number of cars that can be in the tunnel at the same time
       
    // makes sure there are no more than N cars in the tunnel at a time
    static Semaphore car_limit = new Semaphore(N, false);  
    
    // binary turn semaphore: only one direction has it at a time
    static Semaphore turn = new Semaphore(1, false);
    
    // make sure no more than one car vies for the turn semaphore at a time to stem starvation
    static Semaphore z = new Semaphore(1, false); 
     
    
    public TunnelSych(int i, int d)
    {
        id = i;
        direction = d;
    }

    public void run()
    {
       
        for(int i = 0; i < 2; i++){
            
            if(this.direction == 0){  // direction 0
                
                try {
                    
                    z.acquire();
                    
                    mutex0.acquire();
                
                    direction_0++;
               
                    if(direction_0 == 1){ // if its the first one in a batch so interrupt the other direction for the turn 
                    
                        turn.acquire();
                    
                    }
                    z.release();
                    
                    mutex0.release();
                    
                    // wait on the car limit sempahore to ensure that no more than 4 cars are in the tunnel at a time
                    car_limit.acquire();
                    
                    System.out.println("Thread " + this.id + "in the tunnel. Going direction " + this.direction);
                    
                     sleep((int) Math.random() * 1000);
                    
                     System.out.println("Thread " + this.id + "is leaving the tunnel.");
                        
                     //release the car semaphore
                     
                     car_limit.release();
                        
                     mutex0.acquire();
                   
                     direction_0--;
                    
                     if(direction_0 == 0){ // if its the last one from direction release the turn!!
                        
                        turn.release();
                        
                    }
                    
                    mutex0.release();
                    
                    
                }
                catch(InterruptedException e) {
                    
                    System.out.println(e);
                }
            }
       else{  // direction 1
            
            try {
                
                z.acquire();
                mutex1.acquire();
                
                direction_1++;
               
                if(direction_1 == 1){ // if its the one in a batch so interrupt other direction for the turn 
                    
                    turn.acquire();
                }
                
                z.release();
                mutex1.release();
               
                // youve got the turn, so now you want to wait on the tunnel (only 4 at a time)
                
                 // wait on the tunnel semaphore
                car_limit.acquire();
               
                System.out.println("Thread " + this.id + "in the tunnel. Going direction " + this.direction);
                
                  // enter the tunnel
                sleep((int) Math.random() * 3000);
                
                // release the tunnel semaphore
                car_limit.release();
                

                System.out.println("Thread " + this.id + "is leaving the tunnel.");
                
               
                mutex1.acquire();
                
                direction_1--;
                
                if(direction_1 == 0){ // if you reach the last one of that batch, then signal turn for the other side
                
                    turn.release();
                    
                }
                
                mutex1.release();
                
            }
             catch(InterruptedException e) {
                
                System.out.println(e);
            }
        }
    }
    
        
    }

    public static void main(String[] args)
    {
        
        TunnelSych[] p = new TunnelSych[20];

        double xx = Math.random();
       
        for (int i = 0; i < 20; i++)
        {
            xx = Math.random();
            if(xx < .9){
                
                p[i] = new TunnelSych(i, 1);
                p[i].start();
            }
            else{
                
                //System.err.println("Iget here");
                p[i] = new TunnelSych(i, 0);
                p[i].start();
            }
        }
    }
}