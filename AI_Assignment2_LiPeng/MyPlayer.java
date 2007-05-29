import java.util.*;
public class MyPlayer implements PilesPlayer 
{
	private int temp = 0;
			
	public byte[] makeMove(byte otherPlayersMove[]) 
	{
		int[] trans =new int[3];
		byte[] result =new byte[3];
		Vector<Node> availableMoves = null;
		Node bestNode = null;
		
		for(int i=0; i<3; i++)
		{
			trans[i]=otherPlayersMove[i];
		}
		
		Node start = new Node(trans);
		start.BulidGameTree();
		availableMoves = start.ObtainChildren();

		bestNode = start.MyMove(availableMoves);
		for(int i=0; i<3; i++){
			trans[i] = bestNode.GetPiles()[i];
			result[i] = (byte)trans[i];
		}
		return result;
	}
	
	private class Node 
	{
		private int[] pilesValue = new int[3];
		private Vector<Node> myChildren = null;
		private String state; //my turn
		private int minimaxScore = 0;
		
		public Node(int[] InitialValue)
		{ 
			for(int i = 0; i< 3; i++)
			{
				pilesValue[i] = InitialValue[i];
			}
		}
		
		public int[] GetPiles()
		{
			return pilesValue;
		}
			
		public Vector<Node> GenerateOffspring()
		{
			Vector<Node> offspring = new Vector<Node>();
			int[] CurNodeValue = new int[3];
		
			for(int j =0; j<3; j++)
			{
				for(int i =0; i<3; i++)
				{
					CurNodeValue[i] = pilesValue[i];
				}
				
				temp = CurNodeValue[j];
				while(temp > 0)
				{
					temp--;
					CurNodeValue[j] = temp;
					int[] newNodeValue = new int[3];
					for(int k=0; k<3; k++){
						newNodeValue[k] = CurNodeValue[k];
					}
					Arrays.sort(newNodeValue);
					Node ChildNode = new Node(newNodeValue);
					
					boolean repeat = false;
					
					for(int k=0; k<offspring.size(); k++)
					{
						if((offspring.get(k).GetPiles()[0] == newNodeValue[0]) && 
								(offspring.get(k).GetPiles()[1] == newNodeValue[1]) &&
								(offspring.get(k).GetPiles()[2] == newNodeValue[2]))
						{
							repeat = true;
							break;
						}
					}
					if(repeat == false)
					{
						offspring.add(ChildNode);
					}
				}
			}
			this.myChildren = (Vector<Node>) offspring.clone();
			return offspring;
		}
		
		public Node MyMove(Vector<Node> offspring)
		{
				for(int i =0; i<offspring.size(); i++)
				{
					if(offspring.get(i).minimaxScore == 1)
						return offspring.get(i);
				}
			return offspring.get(offspring.size() - 1);
		}
		
		public void state(boolean myturn)
		{
			if(myturn)
			{
				state = "Max";
			}
			else
			{
				state = "Min";
			}
		}
		
		public Vector<Vector<Node>> BulidGameTree()
		{
			Vector<Vector<Node>> Tree = new Vector<Vector<Node>>();
			Vector<Node> nodesInOneLevel = new Vector<Node>();
			int nthLevel = 0;
			
			//Vector<Node> layers = new Vector<Node>();
			boolean myTurn = true;
			
			state(myTurn);
			nodesInOneLevel.add(this);
			Tree.add(nodesInOneLevel);
			
			boolean terminate = true;
			
			while(true)
			{
				terminate = true;
				myTurn = !myTurn;
				Vector<Node> newLevel = new Vector<Node>();
				for(int i =0; i< Tree.get(nthLevel).size();i++)
				{
					Vector<Node> partChildren = null;
					partChildren = Tree.get(nthLevel).get(i).GenerateOffspring();
					for(int k=0; k<partChildren.size(); k++)
					{
						partChildren.get(k).state(myTurn);
						newLevel.add(partChildren.get(k));
					}
				}
				if(nthLevel == 0){
					myChildren = (Vector<MyPlayer.Node>)newLevel.clone();
				}
				Tree.add(newLevel);
				nthLevel++;
				
				//Check if the tree is generated
				for(int i=0; i<newLevel.size(); i++)
				{
					if( (newLevel.get(i).GetPiles()[0] != 0) || (newLevel.get(i).GetPiles()[1] != 0) 
							|| (newLevel.get(i).GetPiles()[2] != 0) )
					{
						terminate = false;
						break;
					}
				}
				
				if(terminate){
					break;
				}
			}
			
			//set minimax score for each node, from bottom to the top
			for(int i=Tree.size()-1; i>=0; i--)
			{
				int numNodes = Tree.get(i).size();
				
				for(int j=0; j<numNodes; j++)
				{
					// set the node with (0, 0, 0)
					if( (Tree.get(i).get(j).GetPiles()[0] == 0) && (Tree.get(i).get(j).GetPiles()[1] == 0) 
							&& (Tree.get(i).get(j).GetPiles()[2] == 0) )
					{
						if(Tree.get(i).get(j).state == "Min")
						{
							Tree.get(i).get(j).minimaxScore = 0;
						}else
						{
							Tree.get(i).get(j).minimaxScore = 1;
						}
					}else{
						// max node
						if(Tree.get(i).get(j).state == "Max")
						{
							Tree.get(i).get(j).minimaxScore = 0;
							for(int k=0; k<Tree.get(i).get(j).myChildren.size(); k++)
							{
								if(Tree.get(i).get(j).myChildren.get(k).minimaxScore == 1)
								{
									Tree.get(i).get(j).minimaxScore = 1 ;
									break;
								}
							}
						}else{
							// min node
							Tree.get(i).get(j).minimaxScore = 1;
							for(int k=0; k<Tree.get(i).get(j).myChildren.size(); k++)
							{
								if(Tree.get(i).get(j).myChildren.get(k).minimaxScore == 0)
								{
									Tree.get(i).get(j).minimaxScore = 0 ;
									break;
								}
							}
						}
					}
					
				}
			}
			return Tree;
		}
		
		public Vector<Node> ObtainChildren()
		{
			return myChildren;
		}
		
	}
}	
