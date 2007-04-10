/************************/
/* Author: Bo CHEN      */
/* Student ID: 1139520  */
/* 24th, Sept. 2006     */ 
/************************/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
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

int numProcs = 0;     // Number of processors (specified on run time)
int procNum = 0;      // The processor number (master 0, others 1, 2 ...)
bool noRemainder = false;
double *dataPart;		   // Data partition for each processor
int *recvCounts, *displs;  // Receive counts and displacements array for Gatherv
int dataStart, dataEnd;    // The start and end index for each processor

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
   	   printf("Enter number of particles [e.g. 1000]: ");
	   scanf("%d", &N);
   	   printf("Enter number of timesteps [e.g. 100]: ");
   	   scanf("%d", &Ntime);
   }
   
   /* Broadcast the number of timesteps and the number of particles */
   MPI_Bcast(&Ntime, 1, MPI_INT, 0, MPI_COMM_WORLD);
   MPI_Bcast(&N, 1, MPI_INT, 0, MPI_COMM_WORLD);
   
   /* Allocate memory for recvCount and displacements array */
   recvCounts = (int *)malloc(numProcs * sizeof(int));
   displs = (int *)malloc(numProcs * sizeof(int));
   
   /* Determine the data partition, receive counts and displacements array according to the remainder */
   if(N % numProcs == 0){   	
       noRemainder = true;
       dataPart = (double *)malloc(N / numProcs * sizeof(double));
       
       /* The start and end index in the whole data array for each individual process */
       dataStart = procNum * (N / numProcs);
       dataEnd = dataStart + (N / numProcs);
       
       /* Each processor has the same number of particles when the remainder == 0 */
       for (i = 0; i < numProcs; i++){
           displs[i] = i * (N / numProcs);
           recvCounts[i] = N / numProcs;
       }
   }else{
       /* The start and end index in the whole data array for each processor */
       dataStart = procNum * (N / numProcs + 1);
       dataEnd = dataStart + (N / numProcs + 1);
        // The end index of data partition for the last processor will be N
	     if(procNum == numProcs -1){
		       dataEnd = N;
	     }
		
	   /* Initialize the displacements array and receive counts array */
       for (i = 0; i < numProcs; i++){
           displs[i] = i * (N / numProcs + 1);
           if(i == (numProcs -1)) { 
           	   /* Do less for the last processor */ 
               recvCounts[i] = dataEnd - dataStart;
           }else{
           	   /* Process one more particles for every processor except the last one */
               recvCounts[i] = N / numProcs + 1;
           }
       }
   		 /* 
   		  * Memory allocated for storing the particles computed in different processor
   		  * Each processor will process one more particle if the remainder != 0, except the last processor
   		  */
       if(procNum != numProcs -1){
       	   dataPart = (double *)malloc((N / numProcs + 1) * sizeof(double));
       }else{
	         dataPart = (double *)malloc((dataEnd - dataStart) * sizeof(double));
       }
   }
   
   time1 = MPI_Wtime();
   /* Initialize positions */
   init();

   /* Print out average distance on the root machine */
   avgeDist = averageDistance();
   if(procNum == 0){
       printf("Average distance between particles at timestep 0 is %f\n\n", 
              avgeDist); 
   }

   /* Loop over timesteps for update using all particles */
   if(procNum == 0){
       printf("Using update based on forces over all particles\n"); 
   }
   for (i=0;i<Ntime;i++) {
      update();
   }

   /* Print out average position */
   avgeDist = averageDistance();
   if(procNum == 0){
   		printf("Average distance between particles at timestep %d is %f\n\n", 
          		Ntime, avgeDist); 
   }

   /* Reinitialize positions to the same values as before */
   init();

   /* Loop over timesteps for update using nearest neighbour particles */
   if(procNum == 0){
   		printf("Using update based on forces from nearest neighbour particles\n"); 
   }
   for (i=0;i<Ntime;i++) {
      updateNN();
   }

   /* Print out average position */
   avgeDist = averageDistance();
   if(procNum == 0){
   		printf("Average distance between particles at timestep %d is %f\n\n", 
          		Ntime, avgeDist); 
   }
   time2 = MPI_Wtime();
   if(procNum == 0){
       printf("Time usage:%f \n", time2 - time1);   
   }

   /* Release the memory after execution */
   free(recvCounts);
   free(displs);
   free(dataPart);
   return 0;
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

