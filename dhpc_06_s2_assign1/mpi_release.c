/************************/
/* Author: Bo CHEN      */
/* Student ID: 1139520  */
/* 24th, Sept. 2006     */ 
/************************/

#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include "mpi.h"

/* Externally defined global values and subroutines */
#define MAXN 1000000
extern int N;            // Number of particles (N<MAXN) is globally defined 
extern double x[MAXN];   // Positions of particles are globally defined 
extern double move();    // Routine for changing position of particles  
                         // based on current position and applied force 
                         
                         // The processor number (master 0, others 1, 2 ...)
extern int procNum, numProcs;
												 // Number of processors (specified on run time)
                         
extern bool noRemainder;          // The remainder: (N % numProcs)
extern double *dataPart;          // Data partition for each processor
extern int *recvCounts, *displs;  // Receive counts and displacements array for Gatherv
extern int dataStart, dataEnd;    // The start and end index for each processor


/***********************************************************************/
/* Update positions of particles using force from all other particles. */
/***********************************************************************/
void update(){
int i,j;
double totalForce[N];

	  /* Broadcast the new array of positions of particles each time launching updata() */
    MPI_Bcast(x, N, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    /* Loop over all particles in the data partition to find force on each particle */
    for (i = dataStart; i < dataEnd; i++) {
        totalForce[i] = 0.0;
        /* Sum forces over all particles to get force on particle i */
        for (j = 0; j < N; j++) {
            if (j < i) totalForce[i] -= 1.0 / pow(x[i] - x[j],2); 
            if (j > i) totalForce[i] += 1.0 / pow(x[i] - x[j],2); 
        }    
    }
   
    /* Loop over all particles to update position of particle i */
    for(i = 0; i < (dataEnd - dataStart); i++){
        dataPart[i] = move(x[dataStart + i], totalForce[dataStart + i]);
    }

    /* Combine all the processed data partitions to the master process 0 */
    /* Receive counts and displacements array are set in the main program to prevent from repeated computing */
	  MPI_Gatherv(dataPart, dataEnd - dataStart, MPI_DOUBLE, x, recvCounts, displs, MPI_DOUBLE, 0, MPI_COMM_WORLD);

}


/******************************************************************************/
/* Update positions of particles using force from nearest neighbour particles */
/******************************************************************************/
void updateNN() 
{
int i;
double totalForce[N];

	  /* Broadcast the new array of positions of particles each time launching updata() */
	  MPI_Bcast(x, N, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    /* Loop over all particles in the data partition to find force on each particle */
    for (i = dataStart; i < dataEnd; i++) {
        totalForce[i] = 0.0;
        /* Sum forces from the neighbours(left and right) particles to get force on particle i */
        if (i > 0)   totalForce[i] -= 1.0 / pow(x[i] - x[i-1],2); 
        if (i < N-1) totalForce[i] += 1.0 / pow(x[i] - x[i+1],2); 
    }
   
    /* Loop over all particles in the same data partition to update position of each particle */
    for (i = 0; i < (dataEnd - dataStart); i++) {
        /* Update position of particle i */
        dataPart[i] = move(x[dataStart + i], totalForce[dataStart + i]);
    }
   
    /* Combine all the processed data partitions to the master process 0 */
    /* Receive counts and displacements array are set in the main program to prevent from repeated computing */
    MPI_Gatherv(dataPart, dataEnd - dataStart, MPI_DOUBLE, x, recvCounts, displs, MPI_DOUBLE, 0, MPI_COMM_WORLD);
   
}


/***********************************************************/
/* Find average distance between neighbouring particles.   */
/* NOTE: In 1-dimension there is an easier way to do this, */
/* but assume that we have to use the following code.      */
/***********************************************************/
double averageDistance(){
int i;
double distance = 0.0;
double totalDist = 0.0;

    /* 
     * Each processor will compute part of the total distance, and then sum the distances to the master processor
     * Note: the number of distances processed in different processores may vary
     * the last processor will compute the remaining (mostly less) particles
     */
	
    /* Broadcast the new array of positions of particles each time launching updata()
     * without this broadcast, the x array in any other machine will be different from machine 0 */
    MPI_Bcast(x, N, MPI_DOUBLE, 0, MPI_COMM_WORLD);
   
    /* Compute the remaining distance for the last processor */
    if(procNum == numProcs - 1){
   	    for (i = dataStart; i < dataEnd-1; i++) {
      	    distance += x[i+1] - x[i];
   	    }
    }else{
    	  /* Compute the same amount of distance for each processor except the last one */
   	    for (i = dataStart; i < dataEnd; i++) {
            distance += x[i+1] - x[i];
   	    }
    }

    /* Sum the total distance to the master processor */
    MPI_Reduce(&distance, &totalDist, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);

    /* Compute the average distance in the master processor */
    distance = totalDist / (N-1);
 
    /* The master processor returns golobal average distance, while others processes return 0 */
    return distance;
}
