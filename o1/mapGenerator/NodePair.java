package o1.mapGenerator;

public class NodePair {
	Node n1;
	Node n2;
	boolean vertical;
	
	public NodePair(Node n1, Node n2, boolean vertical){
		this.n1 = n1;
		this.n2 = n2;
		this.vertical = vertical;
	}
	public int[] getCommonEdge(){
		if(vertical){
			int y = n1.getBlock().y;
			int ay = n2.getBlock().y;
			int height = n1.getBlock().height;
			int aHeight = n2.getBlock().height;
			
			int[] i= {Math.max(y, ay),Math.min(y + height, ay + aHeight)};
			return i;
		}else{
			int x = n1.getBlock().x;
			int ax = n2.getBlock().x;
			int width = n1.getBlock().width;
			int aWidth = n2.getBlock().width;
			
			int[] i= {Math.max(x, ax),Math.min(x + width, ax + aWidth)};
			return i;
		}
	}
}
