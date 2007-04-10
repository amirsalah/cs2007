/**
* MMU using least recently used replacement strategy
* Need to keep track of reference order 
*/

public class LruFeng implements MMU {
	private int[] pages; //define total memory space
	private int[] pagesCopy; //
    private int freeFrame=0;
    private boolean[] modified;//define dirty bit
    private int a =0;
    private boolean lastVictimStatus;
    
    public LruFeng (int frames) {
	 // each page has a resident and a modified bit. 
        //to do
        pages=new int[frames]; //create memory space. tatal number of memory space is the total number of frames
        pagesCopy=new int[frames]; 
        modified=new boolean[frames]; 
    }
    
    public void readMemory(int page_number) {
        //this method updates the reference status of a resident page
        //modified[a]=false;
        	return;
    }
    
    public void writeMemory(int page_number) {
        //this method updates the reference status of a resident page and
	// records it has been modified
    	modified[a]=true; 
    }

    public int checkInMemory(int page_number) {
        // check if a page is resident
	// returns its location (frame number) or -1 if not resident
		for(int i=0;i<freeFrame;i++){
                    if(pagesCopy[i]== page_number)
                    {
                    	
                    	for(int d=i;d<freeFrame-1;d++)
                    	{
                    		pagesCopy[d]=pagesCopy[d+1];
                    	}
                    	pagesCopy[freeFrame-1]=page_number;
                    }
                }
		for(int i=0;i<freeFrame;i++){
                     if(pages[i] == page_number) 
                     {
                     	a=i;
                     	return i;
                     }
                }
        return -1;
    }
    
    public void allocateFrame(int page_number) {
        // it allocate a page into a free frame
	// returns the frame number
	// to do
			pages[freeFrame] = page_number;
			pagesCopy[freeFrame] = page_number;
			a=freeFrame;
			modified[a]=false;
            freeFrame++;
    }
    
    
    public int selectVictim(int p) {
        //it select the victim, allocates the new page into the selected frame 
	//and returns the number of the page replaced
		int c=pagesCopy[0];
		for (int i=0;i<pagesCopy.length-1;i++)
		{
			pagesCopy[i]=pagesCopy[i+1];
		}
		pagesCopy[pagesCopy.length-1]=p;
		for (int i=0;i<pages.length;i++){
		if (pages[i]==c)
		{
			
			pages[i]=p;
			a=i;
			lastVictimStatus=modified[a];
			modified[a]=false;
			}
			}
		return c;
    }
    
    public boolean  lastVictimStatus( ) {
	// it returns true if the last victim was a modified page
	// false otherwise
        //todo
       return lastVictimStatus;
    }
}
