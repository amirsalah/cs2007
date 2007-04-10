/*****************************************/
/* Author: Bo CHEN
/* Student ID: 1139520
/* 7th, Oct. 2006
/*****************************************/

/* 
 * The only modification for the OpenMP main.c is in the function init(),
 * where data parallelism is used.
 * 
 * Additionally, the max number of threads should be set to 2 at the beginning.
 */


#include <stdio.h>
#include <stdlib.h>
#include <math.h>
//#include "openmp.h"
#include <omp.h>


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

/***********************************************************************/
/*                 N-body simulation                                   */
/***********************************************************************/
int main(argc,argv)
int argc;
char *argv[];
{

   int i, randseed;
   double avgeDist, time1, time2;

	/* The max number of threads is 2: 2 processors with share memory */
//	omp_set_max_threads(2);
	
   /* Read in parameters */
   printf("Enter number of particles [e.g. 1000]: ");
   scanf("%d", &N);
   printf("Enter number of timesteps [e.g. 100]: ");
   scanf("%d", &Ntime);
   
   time1 = omp_get_wtime();

   /* Initialize positions */
   init();

   /* Print out average position */
   avgeDist = averageDistance();
   printf("Average distance between particles at timestep 0 is %f\n\n", 
          avgeDist); 

   /* Loop over timesteps for update using all particles */
   printf("Using update based on forces over all particles\n"); 
   for (i=0;i<Ntime;i++) {
      update();
   }

   /* Print out average position */
   avgeDist = averageDistance();
   printf("Average distance between particles at timestep %d is %f\n\n", 
          Ntime, avgeDist); 

   /* Reinitialize positions to the same values as before */
   init();

   /* Loop over timesteps for update using nearest neighbour particles */
   printf("Using update based on forces from nearest neighbour particles\n"); 
   for (i=0;i<Ntime;i++) {
      updateNN();
   }

   /* Print out average position */
   avgeDist = averageDistance();
   printf("Average distance between particles at timestep %d is %f\n\n", 
          Ntime, avgeDist); 

   time2 = omp_get_wtime();
   printf("Time usage: %f seconds\n", time2 - time1);
}



/**************************************************************/
/* Initialize the position values to be evenly distributed.   */
/* (alternatively could give them random initial values)      */
/**************************************************************/
void init() {
   int i;
   
   /* The max number of threads is 2: 2 processors with share memory */
//   omp_set_max_threads(2);
   
   /* Data parallel: distribute N initialization tasks to different threads */
   #pragma omp parallel
   {
   	/* Statically assign each processor half of the N particles */
  	 #pragma omp for schedule(static)
   	 for (i=0;i<N;i++) {
         x[i] = (double)i / (double) N;
     }
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
