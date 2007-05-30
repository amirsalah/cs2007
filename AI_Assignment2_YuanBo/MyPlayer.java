
public class MyPlayer implements PilesPlayer {
	class Node {
		byte[] state = new byte[3];//Hold the states of three tokens.
		boolean myturn;
		Node[] children = new Node[15];//Hold the children
		
		public Node(byte[] state){
			this.state = state.clone();
		}
		
		public void SetPiles(byte[] newPiles){
			for(int i=0; i<3; i++){
				state[i] = newPiles[i];
			}
		}
		
		// Test if the node is a leaf of tree
		public boolean isLeaf(){
			boolean leaf = true;
			
			for(int i=0; i<3; i++){
				if(state[i] != 0){
					leaf = false;
					break;
				}
			}
			
			return leaf;
		}
		
		public Node[] GetChildren(){
			return children;
		}
	}

	public void creatTree(Node parent) {
		if(parent.isLeaf()) {
			return;
		}else{
			int childrenIndex = 0;//Count children
			if(parent.state[0] > 0){ //If the first token has element, list every children.
				byte temp = parent.state[0];//Save the first token
				for (int i = 0; i < temp; i++){
					parent.state[0] -= 1;
					parent.children[childrenIndex] = new Node(parent.state);//Initial a new child
						parent.children[childrenIndex].myturn = !parent.myturn;
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
			 	parent.state[0] = temp;//Restore the second token
			}
			if(parent.state[1] > 0 && parent.state[1] != parent.state[0]){//If the second token has element, list every children and ignore the same state
				byte temp = parent.state[1];//Save the second token
				for (int i = 0; i < temp; i++){
					parent.state[1] -= 1; 
					parent.children[childrenIndex] = new Node(parent.state);//Initial a new child
					parent.children[childrenIndex].myturn = !parent.myturn;
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
				parent.state[1] = temp;//Restore the second token
			}
			if(parent.state[2] > 0 && parent.state[2] != parent.state[1] && parent.state[2] != parent.state[0]) {//If the third token has element, list every children and ignore the same state
				byte temp = parent.state[2];//Save the third token
				for (int i = 0; i < temp; i++) {
					parent.state[2] -= 1;
					parent.children[childrenIndex] = new Node(parent.state);//Initial a new child
					parent.children[childrenIndex].myturn = !parent.myturn;
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
				parent.state[2] = temp;//Restore the third token
			}
		}
	}
	
	public int minMax(Node parent) {
		int selection = 0;
		if(parent.isLeaf()){
			if(parent.myturn == true){
				selection = 1;
			}
			return selection;
		}

		if(parent.myturn == true){
			for(int i = 0; i < parent.GetChildren().length; i++) {
				if(parent.children[i] != null){
					if(minMax(parent.children[i]) == 1) {
						selection = 1;
						break;
					}
				}
			}
			return selection;
		}
		
		selection = 1;
		for(int i = 0; i < parent.GetChildren().length; i++){
			if(parent.children[i] != null) {
				if(minMax(parent.children[i]) == 0){
					selection = 0;
					break;
				}
			}
		}
		return selection;
	}
	

	public byte[] makeMove(byte otherPlayersMove[]) {
		Node node = new Node(otherPlayersMove);//Initial node
		node.myturn = true;
	
		creatTree(node);//creat tree
		for(int i = 0; i < node.children.length; i++) {//Try to find the child which its minMax value is 1 and return the child's state
			if(node.children[i] != null) {
			if(minMax(node.children[i]) == 1) {
					return node.children[i].state;
				}
			} else {
			}
		}
		return node.children[0].state;//If the game must lose, return the first child's state
	}
}