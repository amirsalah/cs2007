/**
* MMU using enchanced second chance replacement strategy
* Page replacement based on the R and M bits
*/

/************************/
/* Author: Bo CHEN      */
/* Student ID: 1139520  */
/* 15th, Oct. 2006      */ 
/************************/

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


public class EscMMU implements MMU {
	private LinkedList<Integer> pageNumbers;
	private Set<Integer> pagesModified;   // (M) Store the pages that have been modified
	private Set<Integer> pagesReferred;   // (R) Store the pages referenced
	private int framePointer = 0, frames;
	private int oriFramePointer = 0;
	private Integer lastVictim = -1;      // The default should be negative value
    
    public EscMMU(int frames) {
	    /* Store the page numbers in the linked list */
    	pageNumbers = new LinkedList<Integer>();
    	pagesModified = new HashSet<Integer>();
    	pagesReferred = new HashSet<Integer>();
    	
    	this.frames = frames;
    }
    
    /* Record the page number to R Hashset when it is referenced */
    public void readMemory(int page_number) {
        pagesReferred.add(page_number);
    }
    
    /* Record the page number to both R and W Hashsets when it is written */
    public void writeMemory(int page_number) {
    	pagesReferred.add(page_number);
    	pagesModified.add(page_number);
    }
    
    /* 
     * Check the List to see whether the page is in the memory
     * Return non negative number if it is in the memroy (index in the LinkedList), 
     * Returning -1 indicates not in the memory
     * Note: It doesn't return the exact frame address.
     */
    public int checkInMemory(int page_number) {
    	return pageNumbers.indexOf(page_number);
    }
    
    /* 
     * Allocate a page into a free frame
     * Add the page number to the end of the Linked list
     */
    public void allocateFrame(int page_number) {
    	pageNumbers.add(page_number);
    }
    
    
    public int selectVictim(int page_number) {
    	int classes = 0, i = 0, j = 0;
    	boolean victimFound = false;
    	oriFramePointer = framePointer; // Record the original position of frame pointer 
    	
    	/* 
    	 * Search for the 4 classes (4 loop on the linkedlist) to find the victim
    	 * The number of searching might be less than 4, since it will return once a victime in lower class is found
    	 */
    	for(classes=0; classes<4; classes++){
    		/* Find victim in the lowest class (R=0 && W=0) */
    		if(classes == 0){
    			for(i=0; i<frames; i++){
    				if( !pagesModified.contains(pageNumbers.get(framePointer)) && !pagesReferred.contains(pageNumbers.get(framePointer)) ){
    					victimFound = true;
    					break;
    				}else{
    					/* Advance the frame pointer if current element has set any R or W bit */
    					framePointer++;	
    					/* Go back to the head once the frame pointer get to the end of queue */
    					if(framePointer == frames){
    						framePointer = 0;
    					}
    				}
    			}
    			// If no frame belongs to the first class, the framePointer will get to the original position
    		}		
    		if(victimFound == true){
    			break;
    		}
    		
    		/* Find victime in class 1 (R=0 && W=1) */
    		if(classes == 1){
    			for(i=0; i<frames; i++){
    				/* A frame belongs to class 1 as long as its R bit is not set, based 
    				 * on the completion of test of class 0 */
    				if( !pagesReferred.contains(pageNumbers.get(framePointer)) ){
    					victimFound = true;
    					break;
    				}else{
    					framePointer++;
    					if(framePointer == frames){
    						framePointer = 0;
    					}
    				}
    			}
    		}
    		if(victimFound == true){
    			break;
    		}
    		
    		/* Find victime in class 2 (R=1 && W=0) */
    		if(classes == 2){
    			for(i=0; i<frames; i++){
    				if( !pagesModified.contains(pageNumbers.get(framePointer)) ){
    					victimFound = true;
    					break;
    				}else{
    					framePointer++;
    					if(framePointer == frames){
    						framePointer = 0;
    					}
    				}
    			}
    		}
    		if(victimFound == true){
    			break;
    		}
    		
    		/* Find victime in class 3 (R=1 && W=1) */
    		/* If it comes to this stage, then all the frames have gotten their R and W bits set */
    		if(classes == 3){
    			victimFound = true;
    			break;
    		}
    	}
    	
    	/* The framePointer points to the victim frame now */
		lastVictim = pageNumbers.remove(framePointer);  // Remove the victim
		pagesReferred.remove(lastVictim);               // Clear the reference bit of the victim
		pageNumbers.add(framePointer, page_number);     // Add the new page into the linked list
		
		/* Clear the reference bit of elements inbetween original frame pointer and current frame pointer */
		if(framePointer > oriFramePointer){
			/* Note: the deletion of reference bit should include the victim frame */
			for(j=oriFramePointer; j<=framePointer; j++){
				pagesReferred.remove(pageNumbers.get(j));
			}
		}
		
		/* The case that victim frame pointer come to front of its original position */
		if(framePointer < oriFramePointer){
			for(j=oriFramePointer; j<frames; j++){
				pagesReferred.remove(pageNumbers.get(j));
			}
			for(j=0; j<=framePointer; j++){
				pagesReferred.remove(pageNumbers.get(j));
			}
		}
		
		/* Advance the frame pointer to the element NEXT to the victim */
		framePointer++;
		if(framePointer == frames){
			framePointer = 0;
		}
		return lastVictim;
    }
    
	/* It returns true if the last victim was a modified page, false otherwise
	 * The page number is also deleted from the hash set */
    public boolean  lastVictimStatus( ) {
        return pagesModified.remove(lastVictim);
    }
}
