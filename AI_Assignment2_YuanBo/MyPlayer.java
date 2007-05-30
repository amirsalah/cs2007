
public class MyPlayer implements PilesPlayer {

	class Node {
		byte [] state = new byte[3];//Hold the states of three tokens.
		boolean myturn;
		Node [] children = new Node[12];//Hold the children
		
		public void SetPiles(byte[] newPiles){
			for(int i=0; i<3; i++){
				state[i] = newPiles[i];
			}
		}
	}
	

	public void creatTree(Node parent) {
		if(parent.state[0] == 0 && parent.state[1] == 0 && parent.state[2] == 0) {//If the state of this node is {0, 0, 0}, it is the leaf of the tree, do nothing
		} else {
			int childrenIndex = 0;//Count children
			if(parent.state[0] > 0) {//If the first token has element, list every children.
				byte temp = parent.state[0];//Save the first token
				for (int i = 0; i < temp; i++) {
					parent.state[0] -= 1;
					parent.children[childrenIndex] = new Node();//Initial a new child
					parent.children[childrenIndex].SetPiles(parent.state);
					if(parent.myturn) {
						parent.children[childrenIndex].myturn = false;
					} else {
						parent.children[childrenIndex].myturn = true;
					}
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
			 	parent.state[0] = temp;//Restore the second token
			}
			if(parent.state[1] > 0 && parent.state[1] != parent.state[0]) {//If the second token has element, list every children and ignore the same state
				byte temp = parent.state[1];//Save the second token
				for (int i = 0; i < temp; i++) {
					parent.state[1] -= 1; 
					parent.children[childrenIndex] = new Node();//Initial a new child
					parent.children[childrenIndex].SetPiles(parent.state);
					if(parent.myturn) {
						parent.children[childrenIndex].myturn = false;
					} else {
						parent.children[childrenIndex].myturn = true;
					}
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
				parent.state[1] = temp;//Restore the second token
			}
			if(parent.state[2] > 0 && parent.state[2] != parent.state[1] && parent.state[2] != parent.state[0]) {//If the third token has element, list every children and ignore the same state
				byte temp = parent.state[2];//Save the third token
				for (int i = 0; i < temp; i++) {
					parent.state[2] -= 1;
					parent.children[childrenIndex] = new Node();//Initial a new child
					parent.children[childrenIndex].SetPiles(parent.state);
					if(parent.myturn) {
						parent.children[childrenIndex].myturn = false;
					} else {
						parent.children[childrenIndex].myturn = true;
					}
					creatTree(parent.children[childrenIndex]);//Recusion
					childrenIndex++;//Chidren size + 1
				}
				parent.state[2] = temp;//Restore the third token
			}
		}
	}
	
	/*===========================================================================
		This method will load a Node, evaluate and return its minMax Value
	============================================================================*/
	public int minMax(Node parent) {
		int selection = 0;//Initial minMax value
		if(parent.state[0] == 0 && parent.state[1] == 0 && parent.state[2] == 0) {//If the node is a leaf, evaluate its minMax value
			if(parent.myturn) {
				selection = 1;
			} else {
				selection = 0;
			} 
		} else {
			if(parent.myturn) {//If the node is not a leaf and the Player is A, return 1 if it has a node which minMax value is 1, else return 0
				for(int i = 0; i < parent.children.length; i++) {
					if(parent.children[i] != null) {
						if(minMax(parent.children[i]) == 1) {//Recusion, evaluate children's minMax value
							selection = 1;
							break;
						} else {
							selection = 0;
						}
					}
				}		
			} else {//If the node is not a leaf and the Player is B, return 0 if it has a node which minMax value is 0, else return 1
				for(int i = 0; i < parent.children.length; i++) {
					if(parent.children[i] != null) {
						if(minMax(parent.children[i]) == 0){//Recusion, evaluate children's minMax value
							selection = 0;
							break;
						} else {
							selection = 1;
						}
					} 
				}
			}
		}
		return selection;
	}
	

	public byte[] makeMove(byte otherPlayersMove[]) {
		Node node = new Node();//Initial node
		node.SetPiles(otherPlayersMove);
		
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