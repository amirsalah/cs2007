/* Model of a 1-dimensional line of N particles, each with position x[i]
 * on the x axis.
 * Subroutine move() updates the positions based on the force on each particle.
 * Forces are proportional to the inverse of the square of the distance
 * between two particles, and are attractive (i.e. pull particles 
 * together), so the force on a given particle from any particle to 
 * the left of it is a negative value (since it is trying to pull the
 * particle in the negative x direction), whereas the force from any
 * particle to the right of it is positive.
 * The total force can be computed in either of two ways:
 *  - sum of forces from all other particles  
 *    (this is the exact value, which is computed in update)
 *  - sum of forces from nearest neighbour particles  
 *    (this is an approximate value, which is computed in updateNN)
 * Particles always stay in position order, i.e. x[0] < x[1] < x[2] etc 
 * before and after positions are updated using move(), i.e. they can't
 * pass through each other and out the other side. 
 */

/* Externally defined global values and subroutines */
#define MAXN 1000000
extern int N;            // Number of particles (N<MAXN) is globally defined 
extern double x[MAXN];   // Positions of particles are globally defined 
extern double move();    // Routine for changing position of particles  
                         // based on current position and applied force 

#include <math.h>
#include "mpi.h"

/***********************************************************************/
/* Update positions of particles using force from all other particles. */
/***********************************************************************/
void update(){
int i,j;
double totalForce[MAXN];

   /* Loop over all particles to find force on each particle */
   for (i=0;i<N;i++){
      totalForce[i] = 0.0;
      /* Sum forces over all particles to get force on particle i */
      for (j=0;j<N;j++){
         if (j < i) totalForce[i] -= 1.0 / pow(x[i] - x[j],2); 
         if (j > i) totalForce[i] += 1.0 / pow(x[i] - x[j],2); 
      }
   }

   /* Loop over all particles to update position of each particle */
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
int i;
double totalForce[MAXN];

   /* Loop over all particles to find force on each particle */
   for (i=0;i<N;i++) {
      totalForce[i] = 0.0;
      /* Sum forces over all particles to get force on particle i */
      if (i > 0)   totalForce[i] -= 1.0 / pow(x[i] - x[i-1],2); 
      if (i < N-1) totalForce[i] += 1.0 / pow(x[i] - x[i+1],2); 
   }
   /* Loop over all particles to update position of each particle */
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
   for (i=1;i<N;i++) {
      distance += x[i] - x[i-1];
   }
   /* Compute average distance */
   distance = distance / (N-1);
   return distance;
}


