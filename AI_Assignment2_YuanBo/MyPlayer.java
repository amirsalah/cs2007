import java.util.Vector;

/**
 * 
 * @author YuanBo
 *
 */
public class MyPlayer implements PilesPlayer {
	private boolean firstMove = true;
	
	public byte[] makeMove(byte otherPlayersMove[]){
		TreeNode root = new TreeNode(otherPlayersMove);
		root.myturn = true;
		
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
		
		public Vector<TreeNode> GetChildren(){
			return children;
		}
	}

	public void MinimaxTree(TreeNode parent){
		if(parent.isLeaf()) {
			return;
		}else{
			
			int childrenID = 0;
			if(parent.pilesNums[0] > 0){
				byte newPile = parent.pilesNums[0];
				for (int i = 0; i < newPile; i++){
					parent.pilesNums[0]--;
					parent.children.add(childrenID, new TreeNode(parent.pilesNums));
					parent.children.get(childrenID).myturn = !parent.myturn;
					MinimaxTree(parent.children.get(childrenID));
					childrenID++;
				}
			 	parent.pilesNums[0] = newPile;
			}
			
			if(parent.pilesNums[1] > 0){
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
		}
		
		minMax(parent);
	}

	public int minMax(TreeNode parent){
		int MMV = 0;
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