/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 17th, May 2007
=========================================================*/

public class MyPlayer implements PilesPlayer {
	private int numPiles = 3;
	private boolean initTree;
	private MyTree minimaxTree;
	private static MyTreeNode activeNode; // is the node myplayer selected in the previous turn
	
	public MyPlayer(){
		// Indicating whether the tree is initialized
		initTree = false;
	}
	
	public byte[] makeMove( byte otherPlayersMove[] ) {
		byte[] movedPiles = new byte[numPiles];
		MyTreeNode selectedNode;
		int[] imovedPiles = new int[numPiles];
		MyTreeNode root = new MyTreeNode(otherPlayersMove[0], otherPlayersMove[1], otherPlayersMove[2]);
		
		// Generate minimax tree before making any move
		if(!initTree){
			minimaxTree = new MyTree(root);
			
			selectedNode = root.GetBestChild();
			imovedPiles = selectedNode.GetPilesNum();
			
			// Transfer the integer to byte
			for(int i=0; i<numPiles; i++){
				movedPiles[i] = (byte)imovedPiles[i];
			}
			activeNode = selectedNode;
			initTree = true;
		}else{
			int oponentNodeID = -1;
			
			for(int i=0; i<activeNode.GetChildren().size(); i++){
				if( activeNode.GetChildren().get(i).equals(root) ){
					oponentNodeID = i;
					break;
				}
			}
			
			if( oponentNodeID == -1 ){
				System.out.println("cannot find oponent node");
			}
			
			selectedNode = activeNode.GetChildren().get(oponentNodeID).GetBestChild();
			imovedPiles = selectedNode.GetPilesNum();
			// Transfer the integer to byte
			for(int i=0; i<numPiles; i++){
				movedPiles[i] = (byte)imovedPiles[i];
			}
			activeNode = selectedNode;
		}
		
		return movedPiles;
	}
}