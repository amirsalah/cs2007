/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 18th, May 2007
=========================================================*/
import java.util.Vector;
import java.util.ArrayList;

/**
 * Tree nodes consist of 3 pile numbers, minimax value of this node, its valid children
 * and the tree level to distinguish the player and oponent.
 */
public class MyTreeNode {
	private int pile1;
	private int pile2;
	private int pile3;
	private int minimaxValue;
	private ArrayList<MyTreeNode> children;
	private int treeLevel;
	
	public MyTreeNode(int pile1, int pile2, int pile3){
		this.pile1 = pile1;
		this.pile2 = pile2;
		this.pile3 = pile3;
	}
	
	public void SetMinimax(int minimaxValue){
		this.minimaxValue = minimaxValue;
	}
	
	/**
	 * Generate the valid children, which exclude duplicated children (112 == 121 == 211)
	 * @return children in a arraylist data structure
	 */
	public ArrayList<MyTreeNode> GenerateChildren(){
		ArrayList<MyTreeNode> children= new ArrayList<MyTreeNode>();
		int[] thisPileNum = GetPilesNum();
		int[] newPileNum = new int[3];  // The pile number in the child node
		int counter;
		boolean duplicatedNode = false;
		
		/* Generate children */
		// Varying pile numbers
		for(int pileIndex = 0; pileIndex<3; pileIndex++){
			counter = thisPileNum[pileIndex];
			// Set the new pile number the same as original pile numbers
			for(int i=0; i<3; i++){
				newPileNum[i] = thisPileNum[i];
			}
			
			while(counter > 0){
				duplicatedNode = false;
				counter--;
			
				newPileNum[pileIndex] = counter;
				MyTreeNode newNode = new MyTreeNode(newPileNum[0], newPileNum[1], newPileNum[2]);
				// Check if there is a duplicated node in the children nodes
				for(int j=0; j<children.size(); j++){
					if(children.get(j).equals(newNode)){
						duplicatedNode = true;
						break;
					}
				}
				if( !duplicatedNode ){
					children.add(newNode);
				}
			}
		}
		
		this.children = children;
		return children;
	}
	
	/**
	 * Test if the given node is the same as this node regardless of the pile order
	 * @param node the given node
	 * @return true if they are equal, false otherwise
	 */
	public boolean equals(MyTreeNode node){
		Vector<Integer> pilesNum1 = new Vector<Integer>(3);
		Vector<Integer> pilesNum2 = new Vector<Integer>(3);
		int[] givenPilesNum = node.GetPilesNum();
		
		// Add all the piles numbers of the given node into a vector to be compared with this
		for(int i=0; i<givenPilesNum.length; i++){
			pilesNum1.add(givenPilesNum[i]);
		}
		
		// Add all the piles numbers in this node into a vector
		pilesNum2.add(pile1);
		pilesNum2.add(pile2);
		pilesNum2.add(pile3);
		
		for(int i=0; i<givenPilesNum.length; i++){
			if( !pilesNum2.remove(pilesNum1.remove(0)) ){
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * Return the 3 pile numbers
	 * @return pile numbers
	 */
	public int[] GetPilesNum(){
		int[] pilesNum = new int[3];
		pilesNum[0] = pile1;
		pilesNum[1] = pile2;
		pilesNum[2] = pile3;
		
		return pilesNum;
	}
	
	public int GetMinimax(){
		return minimaxValue;
	}
	
	public int GetNumChildren(){
		return children.size();
	}
	
	/**
	 * @return true if there is more than one child
	 */
	public boolean HasChild(){
		if(children == null){
			return false;
		}
		
		if(children.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void SetTreeLevel(int level){
		treeLevel = level;
	}
	
	public int GetTreeLevel(){
		return treeLevel;
	}
	
	/**
	 * Get the best child depend on the different tree levels
	 * If the node is in the MAX level, the child with highest minimax value will be return
	 * If the node is in the MIN level, the child with lowest minimax value will be return.
	 * @return the best child
	 */
	public MyTreeNode GetBestChild(){
		if( (treeLevel%2) == 0 ){
			// Max nodes ( my turn )
			for(int i=0; i<children.size(); i++){
				if(children.get(i).GetMinimax() == 1){
					return children.get(i);
				}
			}
			
			// No node with the minimax value of 1, return the last node
			return children.get(children.size() - 1);
		}else{
			System.out.println("Oponent's move???");
			return children.get(0);
		}
	}
	
	public ArrayList<MyTreeNode> GetChildren(){
		return children;
	}
}
