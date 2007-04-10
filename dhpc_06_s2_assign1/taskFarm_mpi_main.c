/*****************************************/
/* Author: Bo CHEN						           */	
/* Student ID: 1139520					         */
/* Date: 1st, Oct. 2006					         */
/*****************************************/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "mpi.h"


/* Global values and subroutines */
#define MAXN 1000000
double x[MAXN];   // Positions of particles 
double move();    // Routine for changing position of particles  
                  // based on current position and applied force 
void init();      // Initialize particle positions

/* External routines */
extern double update();
extern double updateNN();
extern double averageDistance();

/* Other variables */
int N = 1000;         // Number of particles (N<MAXN) 
int Ntime = 100;      // Number of timesteps
double deltaT = 0.1;  // Timestep

int numProcs = 0;     // Number of processes
int procNum = 0;      // The process number

/***********************************************************************/
/*                 N-body simulation                                   */
/***********************************************************************/
int main(argc,argv)
int argc;
char *argv[];
{
   int i;
   double avgeDist;
   double time1, time2;

   /* Initialize MPI environment */ 
   MPI_Init(&argc, &argv);
   MPI_Comm_rank(MPI_COMM_WORLD, &procNum);
   MPI_Comm_size(MPI_COMM_WORLD, &numProcs);
   
   /* Read in parameters from root */
   if(procNum == 0){
       int numTasks, tasksCompleted = 0, i, activeProcs;
       int* particleNum, currentParticle = 0, currentIndex = 0;
       double dataRecv[5];
       MPI_Status status;
       
       /* numProcs - 1 processors will be responsible for updating the particles, except the master processor */
       activeProcs = numProcs - 1;
       
       printf("There are %i working processors and 1 Master processor \n", numProcs - 1);
   	   printf("How many different values of N do you want to execute?");
   	   scanf("%d", &numTasks);
   	   particleNum = (int *) malloc(numTasks * sizeof(int)); // Store the number of particles for all tasks
   	   
   	   printf("Enter number of TIMESTEPS [e.g. 100]: ");
   	   scanf("%d", &Ntime);
   	   
   	   /* All the particles perform the same number of time step */
   	   MPI_Bcast(&Ntime, 1, MPI_INT, 0, MPI_COMM_WORLD);
   	   
   	   /* Get all the values of particle numbers */
   	   for(i=0; i<numTasks; i++){
   	   	   printf("Enter number of particles [e.g. 1000]");
	       scanf("%d", &N);
	       /* Number should be positive, or the number should be re-input */ 
	       if(N > 0){
	           particleNum[i] = N;
	       }else{
	       	   i--;
	       	   printf("Incorrect number, please re-input the POSITIVE number of particle.\n");
	       }
   	   }
   	   /* Start record time for computing execution time */
   	   time1 = MPI_Wtime();
   	   while(1){
   	   	   /* Recv requests OR processed data from other processors */
   	       MPI_Recv(dataRecv, 5, MPI_DOUBLE, MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
   	       /* dataRecv[0] == 0 indicates request for N,
   	        * dataRecv[1] == 1 indicates return the processed result */
   	       if(dataRecv[0] == 0){
   	       	   /* No tasks needed to be performed */
   	           if(tasksCompleted == numTasks){
   	           	   currentParticle = 0;
   	           	   /* Send terminal signal to the sender. 0 indicates stop */
   	               MPI_Send(&currentParticle, 1, MPI_INT, status.MPI_SOURCE, 0, MPI_COMM_WORLD);
   	               activeProcs--;
   	               /* Once all the processor have received the terminal signal, the master process exit */
   	               if(activeProcs == 0){
			   							 time2 = MPI_Wtime();
   	               	   MPI_Finalize();
			                 /* Print the execution time */
			                 printf("Time usage: %f\n", time2 - time1);
   	               	   return 0;
   	               }
   	           }else{
   	               /* Send the task to the client */
   	               tasksCompleted++;
   	               currentParticle = particleNum[currentIndex];
   	               /* the index move from 0 to numTasks-1 */
   	               currentIndex++;
   	               MPI_Send(&currentParticle, 1, MPI_INT, status.MPI_SOURCE, 0, MPI_COMM_WORLD);
   	           }
   	       }
   	       
   	       /* The client processor returns the processed data. dataRecv[1] == 1 */ 
   	       if(dataRecv[0] == 1){
   	           printf("From processor %i, number of particle: %f \n", status.MPI_SOURCE, dataRecv[1]);
   	           printf("Average distance between particles at timestep 0 is %f\n", dataRecv[2]);
   	           printf("Using update based on forces over all particles\n");
   	           printf("Average distance between particles at timestep %d is %f\n", Ntime, dataRecv[3]);
   	           printf("Using update based on forces from nearest neighbour particles\n"); 
   	           printf("Average distance between particles at timestep %d is %f\n\n", Ntime, dataRecv[4]); 
   	       }
   	   }
   }else{
	/* The client processors perform the updating of particles */
	double dataSend[5] = {0, 0, 0, 0, 0};
	MPI_Status status;
	
	/* Get the timesteps, Note: master and other processors should performance this broadcast */
  MPI_Bcast(&Ntime, 1, MPI_INT, 0, MPI_COMM_WORLD);
	while(1){
		dataSend[0] = 0;
		/* Ask master processor for a task, dataSend[0] == 0 indicates requestment */
		MPI_Send(dataSend, 5, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
		MPI_Recv(&N, 1, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
		
		if(N <= 0){
			/* No task needed to be processed */
			printf("No task available, processor %i terminates!\n", procNum);
			MPI_Finalize();
   	        return 0;
 		}else{
			/* Indicate this message is the updated particles */
			dataSend[0] = 1;
			
			/* Record the number of particles */
			dataSend[1] = N;
			
            /* Initialize positions */
            init();

            /* Average position after initialization */
            avgeDist = averageDistance();
            dataSend[2] = avgeDist; 

            /* Loop over timesteps for update using all particles */ 
            for (i=0;i<Ntime;i++) {
               update();
            }

            /* Average position after the update() */
            avgeDist = averageDistance();
            dataSend[3] = avgeDist;

            /* Reinitialize positions to the same values as before */
            init();

            /* Loop over timesteps for update using nearest neighbour particles */ 
            for (i=0;i<Ntime;i++) {
               updateNN();
            }

            /* Average position after updateNN() */
            avgeDist = averageDistance();
            dataSend[4] = avgeDist;

			      /* Send the result to master processor */
            MPI_Send(dataSend, 5, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
         }
	}
  }
}

/**************************************************************/
/* Initialize the position values to be evenly distributed.   */
/* (alternatively could give them random initial values)      */
/**************************************************************/
void init() {
   int i;
   for (i=0;i<N;i++) {
      x[i] = (double)i / (double) N;
   }
}


/**************************************************************/
/* Update position of particle using a bogus version of       */
/* Newton's laws of motion (ignore masses and velocities).    */
/**************************************************************/
double move(double xinit, double force) 
{
double xfinal;

   xfinal = xinit + 0.5 * force * deltaT * deltaT;
   return xfinal;
}

