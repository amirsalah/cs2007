/**
* MMU using least recently used replacement strategy
* Need to keep track of reference order 
*/

/************************/
/* Author: Bo CHEN      */
/* Student ID: 1139520  */
/* 14th, Oct. 2006      */ 
/************************/


import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.lang.Integer;

public class LruMMU implements MMU {
	private LinkedList<Integer> pageNumbers;
	private Set<Integer> pagesModified;   // Store the pages that have been modified in Hash set
	//	private int frames;
	private Integer lastVictim = -1;      // The default should be negative value
	
    
    public LruMMU(int frames) {
	    /* Store the page numbers in the linked list */
    	pageNumbers = new LinkedList<Integer>();
    	pagesModified = new HashSet<Integer>();
//    	this.frames = frames;   // The number of frames is not checked in this implementation.
    }
    
    /* 
     * Remove the referenced page number to the end of the linked list
     * Note: the page at the head of the Linked list is the least recently used page,
     */
    public void readMemory(int page_number) {
    	pageNumbers.add(pageNumbers.remove(pageNumbers.indexOf(page_number)));
    }
    
    /* 
     * Similar to the readMemory().
     * Then add the page number to the hash set indicating that it has been modified
     */
    public void writeMemory(int page_number) {
    	pageNumbers.add(pageNumbers.remove(pageNumbers.indexOf(page_number)));
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
    
    /*
     * The least recently used page number which is stored in the head of the Linked List is dicarding
     * while the new page number is add to the end of the Linked list
     */
    public int selectVictim(int page_number) {
    	pageNumbers.add(page_number);
    	lastVictim = pageNumbers.remove();
        return lastVictim;
    }
    
	/* It returns true if the last victim was a modified page, false otherwise
	 * The page number is also deleted from the hash set */
    public boolean  lastVictimStatus( ) {
        return pagesModified.remove(lastVictim);
    }
}
