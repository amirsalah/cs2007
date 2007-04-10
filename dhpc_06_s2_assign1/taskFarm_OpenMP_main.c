/*****************************************/
/* Author: Bo CHEN						 */	
/* Student ID: 1139520					 */
/* Date: 1st, Oct. 2006					 */
/*****************************************/

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
//#include "openmp.h"
#include <omp.h>

/* Global values and subroutines */
#define MAXN 1000000
//double x[MAXN];   // Positions of particles 
//double *x;

//#pragma omp threadprivate(x, totalForce)

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
   int i, numTasks, k;
   double avgeDist;
   int* particleNum;
   double *totalForce;

    /* Read in parameters for each thread */  
    /* Get the number of tasks */
    printf("How many different values of N do you want to execute?");
    scanf("%d", &numTasks);
    particleNum = (int *) malloc(numTasks * sizeof(int));
    
    printf("Enter number of TIMESTEPS [e.g. 100]: ");
    scanf("%d", &Ntime);
    
   	/* Get all the values of numbers of particles */
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

	/* 
	 * Tasks will be performed in parallel
	 * Note: x should not be shared, since every processor will modify the x array during execution
	 */
	#pragma omp parallel for  //private(i, N, avgeDist, k, x) //shared(Ntime) //schedule(static)
//	#pragma omp threadprivate(x)
    for(k=0; k<numTasks; k++){
    	N = particleNum[k];
    	
    	
    	/* Initialize positions */
        init();

        /* Print out average position */
        avgeDist = averageDistance();
        printf("Task %i: Average distance between particles at timestep 0 is %f\n", k,
               avgeDist); 

        /* Loop over timesteps for update using all particles */
        printf("Task %i: Using update based on forces over all particles\n", k); 
        for (i=0;i<Ntime;i++) {
           update();
        }

        /* Print out average position */
        avgeDist = averageDistance();
        printf("Task %i: Average distance between particles at timestep %d is %f\n", k, 
               Ntime, avgeDist); 

        /* Reinitialize positions to the same values as before */
        init();

        /* Loop over timesteps for update using nearest neighbour particles */
        printf("Task %i: Using update based on forces from nearest neighbour particles\n", k); 
        for (i=0;i<Ntime;i++) {
           updateNN();
        }

        /* Print out average position */
        avgeDist = averageDistance();
        printf("Task %i: Average distance between particles at timestep %d is %f\n\n", k, 
               Ntime, avgeDist); 
               
        free(x);
        
    }
    
    
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


void update() 
{
int i,j;
double *totalForce;
double *x;
    	
   x = (double *) malloc( (N+1) * sizeof(double));
   totalForce = (double *) malloc( (N+1) * sizeof(double));
   /* Loop over all particles to find force on each particle */
   for (i=0;i<N;i++) {
      totalForce[i] = 0.0;
      /* Sum forces over all particles to get force on particle i */
      for (j=0;j<N;j++) {
         if (j < i) totalForce[i] -= 1.0 / pow(x[i] - x[j],2); 
         if (j > i) totalForce[i] += 1.0 / pow(x[i] - x[j],2); 
      } 
   }
   /* Loop over all particles to update position of each particle */
   for (i=0;i<N;i++) {
      /* Update position of particle i */
      x[i] = move(x[i],totalForce[i]);
   }
   
   free(totalForce);
}



void updateNN() 
{
int i;
double *totalForce;
double *x;
    	
   x = (double *) malloc( (N+1) * sizeof(double));
   totalForce = (double *) malloc( (N+1) * sizeof(double));
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
   
   free(totalForce);
}



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
