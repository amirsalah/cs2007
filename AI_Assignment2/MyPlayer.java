/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 17th, May 2007
=========================================================*/

public class MyPlayer implements PilesPlayer {
	private int numPiles = 3;
	private boolean initTree;
	
	public MyPlayer(){
		// Indicating whether the tree is initialized
		initTree = false;
	}
	
	public byte[] makeMove( byte otherPlayersMove[] ) {
		byte[] movedPiles = new byte[numPiles];
		
		// Generate minimax tree before making any move
		if(!initTree){
			
			
			initTree = true;
		}else{
			
		}
		
		return movedPiles;
	}
}