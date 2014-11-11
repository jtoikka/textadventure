package o1.mapGenerator;

public class Node{
	private Block block = null;
	
	private Block room = null; // must be in local position
	
	private Node leftChild = null; // left child should always be left or top of rightChild
	private Node rightChild = null;
	private Node parent = null;

	public Node(Block block){
		this(block, null);
	} // Method
	
	public Node(Block block,Node parent){
		this.block = block;
		this.parent = parent;
	} // Method
	
	/** Sets room. Room has to be in nodes local coordinate system*/
	public void setRoom(Block room){
		this.room = room;
	} // Method
	
	/** Sets parent*/
	public void setParent(Node parent){
		this.parent = parent;
	} // Method
	
	/** Sets both children*/
	public void setChilds(Node leftChild,Node rightChild){
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	} // Method
	
	public void setLeftChild(Node child){
		this.leftChild = child;
	} // Method
	
	public void setRightChild(Node child){
		this.rightChild = child;
	} // Method
	
	/** Gets room in local position. So Nodes location is origin*/
	public Block getRoomLocal(){
		return room;
	} // Method
	
	/** Returns true if node has room*/
	public Boolean hasRoom(){
		if(room != null) return true; else return false;
	} // Method
	
	/** Gets room in global position.*/
	public Block getRoomGlobal(){
		if(room == null) return null;
		Block b = new Block(room.x + block.x,room.y + block.y,room.width,room.height);
		return b;
	} // Method
	
	/** Returns parent node if one exists*/
	public Node getParent(){
		return parent;
	} // Method
	
	/** Returns leftChild if one exists */
	public Node getLeftChild(){
		return leftChild;
	} // Method
	
	/** Returns rightChildif one exists */
	public Node getRightChild(){
		return rightChild;
	} // Method
	
	/** Returns block*/
	public Block getBlock(){
		return block;
	} // Method
	
	/** Returns true if node has been split vertically. False if split is horizontal OR if not split ( no children)*/
	public boolean isSplittedVertically(){
		if(!this.hasBothChildren())
			return false;
		else if(leftChild.getBlock().x == rightChild.getBlock().x)
			return false;
		else
			return true;
	} // Method
	
	/** Returns parents another child if one exists. Otherwise returns null*/
	public Node getNeighbour(){
		Node parentsLeft = parent.getLeftChild();
		Node parentsRight = parent.getRightChild();
		
		if(this == parentsLeft){
			return parentsRight;
		}
		
		else if(this == parentsRight){
			return parentsRight;
			
		}else{
			return null;
		}
	} // Method
	
	/** Returns nodes neighbor if neighbor is parents rightChild.
	 * 	Otherwise returns parent if any*/
	public Node getNextNode(){
		Node parentsLeft = parent.getLeftChild();
		Node parentsRight = parent.getRightChild();
		
		if(this == parentsLeft){
			return parentsRight;
		}
		
		else if(this == parentsRight){
			return parent;
			
		}else{
			return null;
		}
	} // Method
	
	@Override
	public String toString(){
		return "Node: " + block.toString();
	} // Method
	
	/** true when node has parent*/
	public boolean hasParent(){
		if(parent == null)
			return false;
		else
			return true;
	} // Method
	
	/** true when node has at least one child*/
	public boolean hasChild(){
		if(leftChild == null && rightChild == null)
			return false;
		else
			return true;
	} // Method
	
	/** true when node has both children*/
	public boolean hasBothChildren(){
		if(leftChild == null || rightChild == null)
			return false;
		else
			return true;
	} // Method
} // Class