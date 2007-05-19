/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 18th, May 2007
=========================================================*/
import java.util.ArrayList;

/**
 * The tree is constructed level by level, which is represented by Arraylist of arraylist
 * all the nodes in the same level are stored in the same arraylist
 */
public class MyTree {
	private ArrayList<ArrayList<MyTreeNode>> minimaxTree = new ArrayList<ArrayList<MyTreeNode>>();
	private int numTreeLevels;
	
	
	public MyTree(MyTreeNode root){
		numTreeLevels = 0;
		GenerateTree(root);
	}

	public void GenerateTree(MyTreeNode root){
		ArrayList<MyTreeNode> tempChildren = new ArrayList<MyTreeNode>();
		ArrayList<MyTreeNode> levelNodes = new ArrayList<MyTreeNode>();
		boolean hasChild = true;
		MyTreeNode treeLeaf = new MyTreeNode(0, 0, 0);
		
//		root.GenerateChildren();
//		hasChild = root.HasChild();
		levelNodes.add(root);
		minimaxTree.add(levelNodes);
		root.SetTreeLevel(numTreeLevels);
		
		while(hasChild){
			hasChild = false;
			numTreeLevels++;
			ArrayList<MyTreeNode> nextLevelNodes = new ArrayList<MyTreeNode>();
			
			/* Generate children of nodes in current level and save */
			for(int i=0; i<minimaxTree.get(numTreeLevels-1).size(); i++){
				tempChildren = (ArrayList<MyTreeNode>)minimaxTree.get(numTreeLevels-1).get(i).GenerateChildren().clone();
				// Add the children to the next minimax tree level
				for(int j=0; j<tempChildren.size(); j++){
					tempChildren.get(j).SetTreeLevel(numTreeLevels);
					nextLevelNodes.add(tempChildren.get(j));
				}
				tempChildren.clear();
			}
			
			minimaxTree.add(nextLevelNodes);
			
			// travel the whole level to detemine if there is any child exist
			for(int i=0; i<minimaxTree.get(numTreeLevels).size(); i++){
//				if(minimaxTree.get(numTreeLevels).get(i).HasChild()){
				if( !minimaxTree.get(numTreeLevels).get(i).equals(treeLeaf) ){
					hasChild = true;
					break;
				}
			}
		}
		
		Minimax(root);
		
	}
	
	/**
	 * Set minimax value for each node in the tree
	 * @param root the root node of the minimax tree
	 */
	public int Minimax(MyTreeNode root){
		if( !root.HasChild() ){
			// Tremination stage
			if( (root.GetTreeLevel()%2) == 0 ){
				// Max leaves nodes
				root.SetMinimax(1);
				return 1;
			}else{
				// Min leaves nodes
				root.SetMinimax(0);
				return 0;
			}
		}else{
			ArrayList<MyTreeNode> children = root.GetChildren();
			if( (root.GetTreeLevel()%2) == 0 ){
				int max = 0;
				// Max in children nodes
				for(int i=0; i<children.size(); i++){
					if(Minimax(children.get(i)) == 1){
						max = 1;
					}
				}
				root.SetMinimax(max);
				return max;
			}else{
				int min = 1;
				// Min in children nodes
				for(int i=0; i<children.size(); i++){
					if(Minimax(children.get(i)) == 0){
						min = 0;
					}
				} 
				root.SetMinimax(min);
				return min;
			}
		}
	}
	
}
