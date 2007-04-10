/**
* MMU using least recently used replacement strategy
* author - Sung Jin Lee(a1150001)
* last modified - 13/OCT/2006
*/

//import java.util.Stack;
import java.util.LinkedList;
//import java.util.Enumeration;

public class Kelilon implements MMU {
	
	LinkedList<lruPage> pageStack = new LinkedList<lruPage>();	// <lruPage> for generic
    	       
    public Kelilon(int frames) {
    	// each page has a resident and a modified bit. 
    	//leave this alone.
    }
    
    public void readMemory(int page_number) {
		int framePage=0;
		for(int i=0;i<pageStack.size();i++) {
			lruPage p = (lruPage)(pageStack.get(i));
			if(page_number == p.pageNumber) {
				framePage = i;	// return index
			}
		}
 		lruPage pg = (lruPage)pageStack.remove(framePage);
		pg.setType("R");
		pageStack.addLast(pg);
        //this method updates the reference status of a resident page
    }
    
    public void writeMemory(int page_number) {
		int framePage=0;
		for(int i=0;i<pageStack.size();i++) {
			lruPage p = (lruPage)(pageStack.get(i));
			if(page_number == p.pageNumber) {
				framePage = i;	// return index
			}
		}
 		lruPage pg = (lruPage)pageStack.remove(framePage);
		pg.setType("W");	// 
		pageStack.addLast(pg);
    }

    public int checkInMemory(int page_number) { 
    	// check if a page is resident
	// returns its location (frame number) or -1 if not resident

		for(int i=0;i<pageStack.size();i++) {
			lruPage p = (lruPage)(pageStack.get(i));
			if(page_number == p.pageNumber) {
				return i;	// return index
			}
		}
       	return -1;
    }
    
    public void allocateFrame(int page_number) {	
        // it allocate a page into a free frame	
        lruPage page = new lruPage(page_number);
        pageStack.addLast(page);
    }
    
    
    public int selectVictim(int page_number) {
        //it select the victim, allocates the new page into the selected frame 
	//and returns the number of the page replaced
		lruPage pg = (lruPage)pageStack.removeFirst();
		int replacedPage = pg.pageNumber;
		
		if((pg.pageStatus).equals("W")) {  // if status is writing.
			pg.setPage(page_number, 1);
			pageStack.addLast(pg);
		} else {
			pg.setPage(page_number, 0);	// not modified
			pageStack.addLast(pg);
		}
		return replacedPage;
    }
    
    public boolean  lastVictimStatus() {
    	lruPage pg = (lruPage)pageStack.getLast();
    	if(pg.modified) {
    		pg.resetPage();
    		return true;
    	}
    	return false;
    	
	// it returns true if the last victim was a modified page
	// false otherwise
    }
}

class lruPage {
	int pageNumber;
	boolean modified;	
	String pageStatus;	// "W" and "R" 
	
    public lruPage(int pageNum) {
    	pageNumber = pageNum;
    }

    public void setPage(int pageNum, int checkModified) {
    	pageNumber = pageNum;
    	if(checkModified == 1) modified = true;
    	else modified = false;
    }

    public void resetPage() {
    	modified = false;
  	}

  	public void setType(String status) {
  		pageStatus = status;
  	}
}