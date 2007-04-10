
/**
* MMU using random replacement strategy
* No need to keep reference record of pages
**/

/**************************/
/* Author: Bo CHEN        */
/* Student ID: 1139520    */
/* 14th, Oct. 2006        */ 
/**************************/

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Random;

public class RandMMU implements MMU {
	private LinkedList<Integer> pageNumbers;
	private Set<Integer> pagesModified;  // Store the pages that have been modified
	private Integer lastVictim = -1;     // The default should be negative value
	private Random randomFrame;
	private int frames;
	
    public RandMMU(int frames){
	    /* Store the page numbers in the linked list */
    	pageNumbers = new LinkedList<Integer>();
    	pagesModified = new HashSet<Integer>();
    	randomFrame = new Random();
    	this.frames = frames;
    }
    
    /* Nothing needed to be done for read memory operation */
    public void readMemory(int page_number) {
    	
    }

    /* Record the modified page to Hash set */
    public void writeMemory(int page_number) {
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
    
    /* Randomly selecting a victim from the linked list */
    public int selectVictim(int page_number) {
    	lastVictim = pageNumbers.remove(randomFrame.nextInt(frames));
    	
    	/* The new page should be added after the selection of victim page */
    	pageNumbers.add(page_number);    	
        return lastVictim;
    }
    
	/* It returns true if the last victim was a modified page, false otherwise
	 * The page number is also deleted from the hash set */
    public boolean  lastVictimStatus( ) {
    	return pagesModified.remove(lastVictim);
    }
    
}
