#include <stdio.h>
#include <string.h>

typedef struct {
	int pageNo;
	int modified;
} page;
enum	repl { random, lru, enhanced2ndchance};
int	createMMU( int);
int	checkInMemory( int ) ;
void	allocateFrame( int ) ;
page	selectVictim( int, enum repl) ;
int     pageoffset = 12;            /* Page size is fixed to 4 KB */
int	numFrames ;


/* Creates the page table structure to record memory allocation */
int	createMMU (int frames)
{
       
	// to do

	return 0;
}

/* Checks for residency: returns frame no or -1 if not found */
int	checkInMemory( int page_number)
{
        int 	result = -1; 

        // to do


	return result ;
}

/* allocate page to the next free frame and record where it put it */
void     allocateFrame( int page_number)
{
	// to do
	return;
}

/* Selects a victim for eviction/discard according to the replacement algorithm,  returns chosen frame_no  */
page	selectVictim(int page_number, enum repl  mode )
{
	page	victim;
	// to do 
	victim.pageNo = 0;
	victim.modified = 0;
	return (victim) ;
}
		
main(int argc, char *argv[])
{
	char	*tracename;
	int	page_number,frame_no, done ;
	int	do_line;
	int	no_events, disk_writes, disk_reads;
	int     debugmode;
 	enum	repl  replace;
	int	allocated=0, modified=0;
	int	victim_page;
        unsigned address;
    	char 	rw;
	int 	i;
	page	victim;
	FILE	*trace;


        if (argc < 5) {
             printf( "Usage: ./memsim inputfile numberframes replacementmode debugmode \n");
             exit ( -1);
	}
	else {
        tracename = argv[1];	
	trace = fopen( tracename, "r");
        if (trace == NULL ) {
            printf( "Cannot open trace file %s \n", tracename);
            exit ( -1);
         }
	numFrames = atoi(argv[2]);
	if (numFrames < 1) {
            printf( "Frame number must be at least 1\n");
            exit ( -1);
         }

        if (strcmp(argv[3], "lru\0") == 0)
            replace = lru;
	    else if (strcmp(argv[3], "rand\0") == 0)
	     replace = random;
	          else if (strcmp(argv[3], "esc\0") == 0)
                       replace = enhanced2ndchance;		 
        else 
	  {
             printf( "Replacement algorithm must be rand/lru/esc  \n");
             exit ( -1);
	  }

        if (strcmp(argv[4], "quiet\0") == 0)
            debugmode = 0;
	else if (strcmp(argv[4], "debug\0") == 0)
            debugmode = 1;
        else 
	  {
             printf( "Replacement algorithm must be quiet/debug  \n");
             exit ( -1);
	  }
	}
	
	done = createMMU (numFrames);
	if ( done == -1 ) {
		 printf( "Cannot create MMU" ) ;
		 exit(-1);
        }
	no_events = 0 ;
	disk_writes = 0 ;
	disk_reads = 0 ;

        do_line = fscanf(trace,"%x %c",&address,&rw);
	while (do_line == 2) {
		page_number =  address >> pageoffset;
		frame_no = checkInMemory( page_number) ;    /* ask for physical address */

		if ( frame_no == -1 )
		{
		  disk_reads++ ;			/* Page fault, need to load it into memory */
		  if (debugmode) 
		      printf( "Page fault %8d \n", page_number) ;
		  if (allocated < numFrames)  			/* allocate it to an empty frame */
		   {
                     allocateFrame(page_number);
		     allocated++;
                   }
                   else{
		      victim = selectVictim(page_number, replace) ;   /* find frameNo of the victim  */

		   if (victim.modified)           /* need to know victim page and modified  */
	 	      {
                      disk_writes++;			    
                      if (debugmode) printf( "Disk write %8d \n", victim.pageNo) ;
		      }
		   else 
                      if (debugmode) printf( "Discard    %8d \n", victim.pageNo) ;

		   }
		}
		if ( rw == 'R')
		     /* need to update reference status for lru or esc */
			if (debugmode) printf( "reading    %8d \n", page_number) ;
		else if ( rw == 'W')
                     /* need to set this page as modified */	
			if (debugmode) printf( "writting   %8d \n", page_number) ;
		 else {
		      printf( "Badly formatted file. Error on line %d\n", no_events+1); 
		      exit (-1);
		}
	        no_events++;
                do_line = fscanf(trace,"%x %c",&address,&rw);
	}

	printf( "total memory frames:  %d\n", numFrames);
	printf( "events in trace:      %d\n", no_events);
	printf( "total disk reads:     %d\n", disk_reads);
	printf( "total disk writes:    %d\n", disk_writes);
	printf( "page fault rate:      %.2f\n",  (float) disk_reads/no_events);
}
				
