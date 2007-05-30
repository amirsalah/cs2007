import java.util.Vector;

/**
 * The myplayer will take the move that maximizes the minimax value from its children.
 * @author YuanBo
 */
public class MyPlayer implements PilesPlayer {
	private boolean firstMove = true;
	
	public byte[] makeMove(byte otherPlayersMove[]){
		TreeNode root = new TreeNode(otherPlayersMove);
		root.myturn = true;
		
		// The tree is generated when the player is first created
		if(firstMove){
			MinimaxTree(root);
			firstMove = false;
		}
		
		for(int i=0; i<root.children.size(); i++){
			if(root.children.get(i).minimaxValue == 1){
				return root.children.get(i).pilesNums;
			}
		}

		return root.children.get(0).pilesNums;
	}
	
	// The inner tree node class used to produce a whole minimax tree
	class TreeNode {
		byte[] pilesNums = new byte[3];
		boolean myturn;
		Vector<TreeNode> children = new Vector<TreeNode>();
		int minimaxValue;
		
		public TreeNode(byte[] pilesNums){
			this.pilesNums = pilesNums.clone();
		}
		
		public void SetPiles(byte[] newPiles){
			for(int i=0; i<3; i++){
				pilesNums[i] = newPiles[i];
			}
		}
		
		// test if the node is a leaf
		public boolean isLeaf(){
			boolean leaf = true;
			
			for(int i=0; i<3; i++){
				if(pilesNums[i] != 0){
					leaf = false;
					break;
				}
			}
			
			return leaf;
		}
		
		// return all the children of current node
		public Vector<TreeNode> GetChildren(){
			return children;
		}
	}

	// Generate minimax tree
	// The tree is generated and then the minimax values are assigned from bottom to top
	public void MinimaxTree(TreeNode parent){
		if(parent.isLeaf()) {
			return;
		}else{
			
			int childrenID = 0;
			
			for(int p=0; p<3; p++){
				if( (p==0 && parent.pilesNums[p]>0)
						|| (p==1 && parent.pilesNums[p]>0 && parent.pilesNums[p] != parent.pilesNums[0])
						|| (p==2 && parent.pilesNums[p]>0 && parent.pilesNums[p] != parent.pilesNums[1] 
						    && parent.pilesNums[p] != parent.pilesNums[0]) ){
					byte newPile = parent.pilesNums[p];
					for (int i = 0; i < newPile; i++){
						parent.pilesNums[p]--;
						parent.children.add(childrenID, new TreeNode(parent.pilesNums));
						parent.children.get(childrenID).myturn = !parent.myturn;
						// Recursively invoke
						MinimaxTree(parent.children.get(childrenID));
						childrenID++;
					}
				 	parent.pilesNums[p] = newPile;
					}
				}
/*
			if(parent.pilesNums[0] > 0){
				byte newPile = parent.pilesNums[0];
				for (int i = 0; i < newPile; i++){
					parent.pilesNums[0]--;
					parent.children.add(childrenID, new TreeNode(parent.pilesNums));
					parent.children.get(childrenID).myturn = !parent.myturn;
					// Recursively invoke
					MinimaxTree(parent.children.get(childrenID));
					childrenID++;
				}
			 	parent.pilesNums[0] = newPile;
			}
			
			if(parent.pilesNums[1] > 0){
				// prevent duplicated piles
				if(parent.pilesNums[1] != parent.pilesNums[0]){
					byte newPile = parent.pilesNums[1];
					for (int i = 0; i < newPile; i++){
						parent.pilesNums[1]--; 
						parent.children.add(childrenID, new TreeNode(parent.pilesNums));
						parent.children.get(childrenID).myturn = !parent.myturn;
						MinimaxTree(parent.children.get(childrenID));
						childrenID++;
					}
					parent.pilesNums[1] = newPile;
				}
			}
			
			if(parent.pilesNums[2] > 0) {
				if(parent.pilesNums[2] != parent.pilesNums[1]){
					if(parent.pilesNums[2] != parent.pilesNums[0]){
						byte newPile = parent.pilesNums[2];
						for (int i = 0; i < newPile; i++) {
							parent.pilesNums[2]--;
							parent.children.add(childrenID, new TreeNode(parent.pilesNums));
							parent.children.get(childrenID).myturn = !parent.myturn;
							MinimaxTree(parent.children.get(childrenID));
							childrenID++;
						}
						parent.pilesNums[2] = newPile;
					}
				}
			}
			*/
//		}
		}
		minMax(parent);
	}

	// Assign the minimax to the tree with root: parent
	private int minMax(TreeNode parent){
		int MMV = 0; // Minimax value
		parent.minimaxValue = 0;
		if(parent.isLeaf()){
			if(parent.myturn == true){
				MMV = 1;
				parent.minimaxValue = 1;
			}
			return MMV;
		}

		if(parent.myturn == true){
			for(int i = 0; i < parent.GetChildren().size(); i++) {
				if(parent.children.get(i) != null){
					if(minMax(parent.children.get(i)) == 1) {
						MMV = 1;
						parent.minimaxValue = 1;
						break;
					}
				}
			}
			return MMV;
		}
		
		MMV = 1;
		parent.minimaxValue = 1;
		for(int i = 0; i < parent.GetChildren().size(); i++){
			if(parent.children.get(i) != null) {
				if(minMax(parent.children.get(i)) == 0){
					MMV = 0;
					parent.minimaxValue = 0;
					break;
				}
			}
		}
		return MMV;
	}

}