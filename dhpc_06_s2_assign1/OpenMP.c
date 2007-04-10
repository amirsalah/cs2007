/*****************************************/
/* Author: Bo CHEN
/* Student ID: 1139520
/* 7th, Oct. 2006
/*****************************************/

#include <math.h>
//#include "openmp.h"
#include <omp.h>
#include <stdio.h>


/* Externally defined global values and subroutines */
#define MAXN 1000000
extern int N;            // Number of particles (N<MAXN) is globally defined 
extern double x[MAXN];   // Positions of particles are globally defined 
extern double move();    // Routine for changing position of particles  
                         // based on current position and applied force 

/***********************************************************************/
/* Update positions of particles using force from all other particles. */
/***********************************************************************/
void update() 
{
int i,j;
double totalForce[MAXN];

   /* 
    * Loop over all particles to find force on each particle.
    * Most computing time is spent on the O(n^2) computation
    * hence it should be parallelized.
    * There is a barrier at the end of outer for loop.
    */
   #pragma omp parallel for shared(totalForce, x)
   for (i=0;i<N;i++) {
      totalForce[i] = 0.0;
      /* Sum forces over all particles to get force on particle i */
      for (j=0;j<N;j++) {
         if (j < i) totalForce[i] -= 1.0 / pow(x[i] - x[j],2); 
         if (j > i) totalForce[i] += 1.0 / pow(x[i] - x[j],2); 
      } 
   }
   
   /* Loop over all particles to update position of each particle */
   #pragma omp parallel for private(i) shared(x, totalForce)
   for (i=0;i<N;i++) {
      /* Update position of particle i */
      x[i] = move(x[i],totalForce[i]);
   }
}


/******************************************************************************/
/* Update positions of particles using force from nearest neighbour particles */
/******************************************************************************/
void updateNN() 
{
int i, k;
double totalForce[MAXN];

   /* The parallelism in this subroutine is the same as update() subroutine */
   /* Loop over all particles to find force on each particle */
   #pragma omp parallel for private(i) shared(x, totalForce)// default(shared)
   for (i=0;i<N;i++) {
      totalForce[i] = 0.0;
      /* Sum forces over all particles to get force on particle i */
      if (i > 0)   totalForce[i] -= 1.0 / pow(x[i] - x[i-1],2); 
      if (i < N-1) totalForce[i] += 1.0 / pow(x[i] - x[i+1],2); 
   }

   /* Loop over all particles to update position of each particle */
   #pragma omp parallel for private(i) shared(x, totalForce)
   for (i=0;i<N;i++) {
      /* Update position of particle i */
      x[i] = move(x[i],totalForce[i]);
   }

}


/***********************************************************/
/* Find average distance between neighbouring particles.   */
/* NOTE: In 1-dimension there is an easier way to do this, */
/* but assume that we have to use the following code.      */
/***********************************************************/
double averageDistance() 
{
int i;
double distance;

   /* Loop over all particles */
   distance = 0.0;
   
   /* 
    * Compute the total distance in parallel
    * the variable "distance" should be set to private 
    * and add all private distances to the final total distance
    * These 2 steps can be done by reduction
    * In addition, there is a barrier at the end of for loop.
    */
   #pragma omp parallel
   {
   	 #pragma omp for private(i) reduction(+: distance)
   	 for (i=1;i<N;i++) {
        distance += x[i] - x[i-1];
     }
   }
   
   /* Compute average distance */
   distance = distance / (N-1);
   return distance;
}


