import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

class MyPlayer implements PilesPlayer
{
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private boolean treeExist = false;
	private String turn = "me";
	private int level = 1;
	private boolean finish = false;
	
	public byte[] makeMove( byte otherPlayersMove[] )
	{
		Arrays.sort(otherPlayersMove); //Sort piles in ascending order
		byte[] last = new byte[]{0,0,1};
		byte[] lose = new byte[]{0,0,0};
		
		if(Arrays.equals(otherPlayersMove,last)) return lose;
		
		if(treeExist == false) //check if the tree is already created or not
		{
			createChildren(otherPlayersMove); //generate children for root
			
			//generate children until finish (all leafs with piles 0,0,0)
			flip();
			level++;
			while(finish==false)
			{
				for(int i=0;i<nodes.size();i++)
				{
					Node n = nodes.get(i);
					if(n.level == level - 1) 
					{
						createChildren(n.piles);
					}
				}
				
				finish = true;
				//Check all children that just created, still need to generate children or not 
				for(int i=0;i<nodes.size();i++)
				{
					Node n = nodes.get(i);
					if(n.level==level)
					{
						if(n.piles[0] != 0 || n.piles[1] != 0 || n.piles[2] > 1){
							finish = false;
							break;
						}
					}
				}
				
				flip();
				level++;
			}
			
			assignMinimaxValue();
			
			treeExist = true;
			return selectMove(otherPlayersMove);
		}
		else 
		{
			return selectMove(otherPlayersMove);
		}
	}
	
	//Generate Children of particular node
	public void createChildren(byte parent[])
	{
/*
		byte temp[] = new byte[3];
		
		for(int i=0;i<3;i++)
		{
			temp = (byte[]) parent.clone();
			for(byte j=temp[i];j>0;j--)
			{
				temp[i]--;
				byte temp2[] = (byte[]) temp.clone();
				
				Arrays.sort(temp2);
				
				//Ignore if same node was generated
				boolean flag=false;
				for(int k=0;k<nodes.size();k++)
				{
					Node h = nodes.get(k);
					if(level==h.level)
					{
						boolean checkParent = Arrays.equals(h.parent,parent);
						if(checkParent == true)
						{
							boolean check = Arrays.equals(h.piles,temp2);
							if(check==true) flag=true;
						}
					}
				}
				
				//piles with 0,0,0 token will not generated
				if(temp2[2]!=0 && flag == false)
				{
					Node n = new Node(temp2,parent,level,turn);
					nodes.add(n);
				}
			}
		}*/
		
		byte[] newChild = new byte[3];
		int pileCounter;
		boolean sameNode = false;
		Vector<byte[]> validNewNodes = new Vector<byte []>();
		
		for(int pileNum=0; pileNum<3; pileNum++){
			pileCounter = parent[pileNum];
			
			newChild[0] = parent[0];
			newChild[1] = parent[1];
			newChild[2] = parent[2];
			
			while(pileCounter > 0){
				pileCounter--;
				sameNode = false;
				
				newChild[pileNum] = (byte) pileCounter;
				Arrays.sort(newChild);
				for(int i=0; i<validNewNodes.size(); i++){
					if( Arrays.equals(validNewNodes.get(i), newChild) ){
						sameNode = true;
						break;
					}
				}
				
				if(!sameNode){
					Node newNode = new Node(newChild,parent,level,turn);
					validNewNodes.add(newChild.clone());
					nodes.add(newNode);
				}
				
			}
		}
	}
	
	//Method to change TURN status (enemy or this player turn)
	//(Min or Max value)
	public void flip()
	{
		if(turn == "me") turn = "enemy";
		else turn = "me";
	}
	
	public void assignMinimaxValue()
	{
		//assign minimax value to all leaf (node with 0,0,1 token)
		byte[] compare = new byte[] {0,0,1};
		for(int i=0;i<nodes.size();i++)
		{
			Node n = new Node();
			n = nodes.get(i);
			if(Arrays.equals(n.piles,compare))
			{
				if(n.turn.equals("me"))
				{
					n.minimax = 1;
					nodes.set(i, n);
				}
				else
				{
					n.minimax = 0;
					nodes.set(i,n);
				}
			}
		}
		level--;
		
		//assign minimax value from the lowest level
		while(level>0)
		{
			level--;
			for(int i=0;i<nodes.size();i++)
			{
				Node n = new Node();
				n = nodes.get(i);
				if(!Arrays.equals(n.piles,compare))
				{
					if(n.level == level)
					{
						//call all this node children to decide it's minimax value
						int mm = 1;
						for(int j=0;j<nodes.size();j++)
						{
							Node temp = new Node();
							temp = nodes.get(j);
							if(Arrays.equals(temp.parent,n.piles))
							{
								if(temp.minimax==0) mm=0;
							}
						}
						if(mm==1) n.minimax = 1;
						else n.minimax = 0;
						nodes.set(i,n);
					}
				}
			}	
		}
	}
	
	//selecting the best move
	//if all nodes minimax value are 0 (cannot win), it will choose the first node with minimax value 0
	public byte[] selectMove(byte move[])
	{
		ArrayList<Node> temp = new ArrayList<Node>();
		for(int i=0;i<nodes.size();i++)
		{
			Node n = new Node();
			n = nodes.get(i);
			if(Arrays.equals(n.parent,move)  && n.turn.equals("me"))
			{
				temp.add(n);
			}
		}
		
		byte[] next = new byte[3];
		
		for(int j=0;j<temp.size();j++)
		{
			Node k = new Node();
			k = temp.get(j);
			if(k.minimax==1) 
			{
				next = k.piles;
				return next;
			}
		}
		
			Node h = new Node();
			h = temp.get(0);
			next = h.piles;
		
		return next;
	}
	
	//inner class containing information needed in a node
	public class Node
	{
		byte[] piles;   	// number of tokens on piles
		String turn;    	// Min or Max Level
		int minimax = -1;   // minimax value which used to check wheter player in win or lose state later
		byte[] parent;  	// parent of this node
		int level;     		// which level this node is
		
		public Node(){}     // empty constractor
		
		public Node(byte p[],byte prt[], int l, String t)
		{
			piles = (byte[]) p.clone();
			parent = (byte[]) prt.clone();
			level = l;
			turn = t;
		}
	}
}