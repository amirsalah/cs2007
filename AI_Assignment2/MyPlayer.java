/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 17th, May 2007
=========================================================*/

public class MyPlayer implements PilesPlayer {
	private int numPiles = 3;
	private boolean initTree;
	private MyTree minimaxTree;
	
	public MyPlayer(){
		// Indicating whether the tree is initialized
		initTree = false;
	}
	
	public byte[] makeMove( byte otherPlayersMove[] ) {
		byte[] movedPiles = new byte[numPiles];
		
		// Generate minimax tree before making any move
		if(!initTree){
			MyTreeNode root = new MyTreeNode(otherPlayersMove[0], otherPlayersMove[1], otherPlayersMove[2]);
			minimaxTree = new MyTree(root);
			
			
			initTree = true;
		}else{
			
		}
		
		return movedPiles;
	}
}