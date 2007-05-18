/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 18th, May 2007
=========================================================*/
import java.util.Vector;
import java.util.ArrayList;

public class MyTreeNode {
	private int pile1;
	private int pile2;
	private int pile3;
	private int minimaxValue;
	
	public MyTreeNode(int pile1, int pile2, int pile3){
		this.pile1 = pile1;
		this.pile2 = pile2;
		this.pile3 = pile3;
	}
	
	public void SetMinimax(int minimaxValue){
		this.minimaxValue = minimaxValue;
	}
	
	public ArrayList<MyTreeNode> GenerateChildren(){
		ArrayList<MyTreeNode> children= new ArrayList<MyTreeNode>();
		int[] thisPileNum = GetPilesNum();
		int[] newPileNum = new int[3];  // The pile number in the child node
		int counter;
		
		// Set the new pile number the same as original pile numbers
		for(int i=0; i<3; i++){
			newPileNum[i] = thisPileNum[i];
		}
		
		/* Generate children */
		// Varying pile numbers
		for(int pileIndex = 0; pileIndex<3; pileIndex++){
			counter = thisPileNum[pileIndex];
			while(counter > 0){
				counter--;
			
				newPileNum[pileIndex] = counter;
				MyTreeNode newNode = new MyTreeNode(newPileNum[0], newPileNum[1], newPileNum[2]);
				// Check if there is a duplicated node in the children nodes
				for(int j=0; j<children.size(); j++){
					if(children.get(j).equals(newNode)){
						break;
					}
				}
				children.add(newNode);
			}
		}
		
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
		
		/*		
		for(int i=0; i<givenPilesNum.length; i++){
			
			int temp = pilesNum1.get(0);
			if(pilesNum2.contains(temp)){
				pilesNum1.remove(0);
				pilesNum2.remove((Integer)temp);
			}else{
				return false;
			}
		}
		return true;
		*/
		for(int i=0; i<givenPilesNum.length; i++){
			if( !pilesNum2.remove(pilesNum1.remove(0)) ){
				return false;
			}
		}
		return true;
		
	}
	
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
}
