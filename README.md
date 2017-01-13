# OS assisted Synchronization: Sempahores

To simulate multiple processes, threads are used in conjunction with the semaphores in all the programs.

N-batched Scan: This code ensures that no more than N many processes execute a critical section at a time.
Priority Scan: gives those processes with higher priority access to the critical section first.

Rendezvous Scan: To explain the code I have writter here- it is required that the execution of the concurrent processes P1
and P2 be required to satisfy the following two properties: (1) No instruction below S2
in P2 can ever execute before all instructions up to S1 in P1 have finished execution
and (2) No instruction below S1 in P1 can ever execute before all instructions up to S2
in P2 have finished execution. ie S1 and S2 be defined as “rendezvous” points for P1 and P2. 

Tunnel Synch: Once a vehicle enters the tunnel, it is guaranteed to get to the other side without crashing into a vehicle going the other way.There are never more than N=4 vehicles in the tunnel. A continuing stream of vehicles traveling in one direction should not starve vehicles going in the other direction.

Shuttle Synch Logan (one shuttle):The shuttles simply go around the airport in a circle from terminal i to terminal (i+1) mod k, where k is the number of terminals to be served (K= 6 for Logan, given the stops at terminals A, B, C, D, and E, and at T station). Each shuttle has a fixed capacity N, i.e., no more than N travelers can ride on the bus. When a shuttle arrives to a terminal stop, some of its riders may leave while others may board up to the capacity limit. To discourage travelers from rushing to catch a shuttle that is already loading its passengers, anyone arriving to the shuttle terminal stop while the shuttle is boarding must wait for the next shuttle. In order for the shuttle to close its doors and leave the terminal, some passenger must tell it to do so. 

Shuttle Fleet Synch Logan (N shuttles): Adjusted the protocol developed for Shuttle Synchronization to allow the airport to operate M > 1 shuttles
