package o1.mapGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MapGenerator{
	private final double blockShapeModifier = 2.8; // if 2, the split will be exactly in the middle
	private final double roomSizeModifier = 2;
	private final int minCommonEdge = 5;
	private final int cWidth = 2;
	
	private final int floor = 1;
//	private final int wall = 0;
	
	private static Date now = new Date();
	
	private long oldseed;
	
	private Random randomizer = new Random(oldseed);
	private boolean firstSplit = getRandBool();
	
	Node root;
	
	public int[] map;
	
	int w;
	int h;
	
	private ArrayList<ArrayList<Node>> nodeLevels;
	private ArrayList<Node> lastLevel; // this should have all the Nodes that will have rooms in them. These nodes should not have children
	
	private ArrayList<Block> corridorList;
	
	private int calcIndex(int x, int y){return y * (w + 1) + x;}
	
	public MapGenerator(int x,int y, int splitIter){
		this(x,y,splitIter, now.getTime());}	
	
	public MapGenerator(int x,int y, int splitIter, long seed){
		if(splitIter < 1){
			System.out.println("Split Iterator count (" + splitIter + "). Has to be at least 1. Setting it to 1");
			splitIter = 1;
		}
		
		oldseed = now.getTime();
		map = new int[x*y];
		
		long startTime = System.nanoTime();
		System.out.println("New dungeon generation started");
		h = y;
		w = x;
		
		corridorList = new ArrayList<Block>();
		nodeLevels = new ArrayList<ArrayList<Node>>();
		root = new Node(new Block(0,0,x,y),null);
		
		ArrayList<Node> rootLevel = new ArrayList<Node>();
		rootLevel.add(root);
		nodeLevels.add(rootLevel);
		
		// splits the map
		for(int i = 0; i < splitIter; i++){	
			ArrayList<Node> currentLevel = new ArrayList<Node>();
			for (Node n : nodeLevels.get(i)) {
				n = splitNode(n,firstSplit);
				currentLevel.add(n.getLeftChild());
				currentLevel.add(n.getRightChild());
			}
			nodeLevels.add(currentLevel);
			firstSplit = !firstSplit;
		}
		
		lastLevel = nodeLevels.get(nodeLevels.size()-1); // sets the last level for further use
		
		createRooms(lastLevel);
		createCorridors();
		createMap();
		
		//timing
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("Created dungeon of size: " + x + "x" + y + ", with "+ splitIter +" iteration steps in " + duration/1000000 + " milliseconds");
		
	} // Method
	
	/** Creates corridor between each children pair*/
	private void createCorridors(){			
		// Iterate through all levels
		for (int i = nodeLevels.size() - 1; i >= 0; i--) {
			ArrayList<Node> currentLevel = nodeLevels.get(i);
			
			// Iterate through all nodes in level
			for (Node n : currentLevel) {
				
				// if hasBothChildren, connect them with corridor
				if(n.hasBothChildren()){
					// All nodes on both sides
					ArrayList<Node> leftLastLevelNodes = getAllLastLevelNodes(n.getLeftChild());
					ArrayList<Node> rightLastLevelNodes = getAllLastLevelNodes(n.getRightChild());
					
					// All nodes that "touch The Edge"
					ArrayList<Node> leftEdgeNodes = new ArrayList<Node>();
					ArrayList<Node> rightEdgeNodes = new ArrayList<Node>();
					
					// All node pairs that can be connected
					ArrayList<ArrayList<Node>> connectPairs = new ArrayList<ArrayList<Node>>();
					
					// fill leftEdgeNodes list
					for(Node l : leftLastLevelNodes){
						if(n.getRightChild().getBlock().isTouching(l.getBlock())) leftEdgeNodes.add(l);
					}
					
					// fill rightEdgeNodes list
					for(Node r :rightLastLevelNodes){
						if(n.getLeftChild().getBlock().isTouching(r.getBlock())) rightEdgeNodes.add(r);
					}
					
					// fill connectPairs
					for(Node l: leftEdgeNodes){
						for(Node r: rightEdgeNodes){
							if(l.getBlock().isTouching(r.getBlock())){ 
								ArrayList<Node> lr = new ArrayList<Node>();
								lr.add(l);
								lr.add(r);
								connectPairs.add(lr);
							}
						}
					}			
					ArrayList<NodePair> usablePairs = new ArrayList<NodePair>();
							
					// calculate common edge length
					int minEdge = minCommonEdge;
					while(usablePairs.size() == 0){
						for(ArrayList<Node> p : connectPairs){	
							Node n1 = p.get(0);
							Node n2 = p.get(1);
							
							if(n.isSplittedVertically()){ // vertical case. Left is left
								int y = n1.getBlock().y;
								int ay = n2.getBlock().y;
								int height = n1.getBlock().height;
								int aHeight = n2.getBlock().height;
								
								int edgeLenght = Math.min(y + height, ay + aHeight) - Math.max(y, ay);
								NodePair t = new NodePair(n1, n2, n.isSplittedVertically());
								
								if(edgeLenght >= minCommonEdge) usablePairs.add(t);
								//if(edgeLenght >= minCommonEdge) usablePairs.add(n);
								
							}else{// horizontal case. Left is up
								int x = n1.getBlock().x;
								int ax = n2.getBlock().x;
								int width = n1.getBlock().width;
								int aWidth = n2.getBlock().width;
								
								int edgeLenght = Math.min(x + width, ax + aWidth) - Math.max(x, ax);
								NodePair t = new NodePair(n1, n2, n.isSplittedVertically());
								
								if(edgeLenght >= minCommonEdge) usablePairs.add(t);
								//if(edgeLenght >= minCommonEdge) usablePairs.add(n);
							}
						}
						
						if(usablePairs.size() == 0){
							minEdge--;	
							System.out.println("Couldn't find possible pairs with current minimum common edge.\n"
											+  "Dropped requirement by one. Current minimum common edge requirement is " + minEdge);
							if(minEdge < 0) break;
						}
						// select pair that will be connected
						
						NodePair pair;
						if(usablePairs.size() > 0){
							int rand = randomizer.nextInt(usablePairs.size());
							pair = usablePairs.get(rand);
							createSingleCorridor(pair);
						}else{
							System.out.println("No usable pairs. Not making corridor");
						}
					}				
					
					//System.out.println(usablePairs.size() + " meet the minimum common edge requirements.");
				}// if hasBothChildren
			}// for node
		}// for int i	
	} // method
	
	private void createSingleCorridor(NodePair i){
		// TODO: Make sure that corridors reach rooms
		//System.out.println("Pair is " + i.toString());
		Block n1 = i.n1.getBlock();
		Block n2 = i.n2.getBlock();
		
		if(i.vertical){ // vertical common edge. Corridor will be horizontal
			int[] commonEdge = i.getCommonEdge();
			int edge_y1 = commonEdge[0];
			int edge_y2 = commonEdge[1];
			
			//System.out.println("Horizontal corridor between y1-y2: " + edge_y1 + "-" + edge_y2);
			
			int corridorY = edge_y1 + (edge_y2 - edge_y1)/2;
			int corridorWidth = n2.GetGlobalMiddlePoint().x - n1.GetGlobalMiddlePoint().x;
			corridorList.add(new Block(n1.GetGlobalMiddlePoint().x, corridorY, corridorWidth,cWidth));
			
		}
		else{
			int[] commonEdge = i.getCommonEdge();
			int edge_x1 = commonEdge[0];
			int edge_x2 = commonEdge[1];
			
			//System.out.println("Vertical corridor between x1-x2: " + edge_x1 + "-" + edge_x2);
			
			int corridorX = edge_x1 + (edge_x2 - edge_x1)/2;
			int corridorHeight = n2.GetGlobalMiddlePoint().y - n1.GetGlobalMiddlePoint().y;
			corridorList.add(new Block(corridorX, n1.GetGlobalMiddlePoint().y, cWidth,corridorHeight));
		}
	}
	
	/** Creates one random sized room to each node in @param level*/
	private void createRooms(ArrayList<Node> level){ 
		double a = roomSizeModifier;
		for (Node n : level) {
			Block b = n.getBlock();
			Block room;
			
			// checks if node is big enough to fit a room in
			if(b.height < 4 || b.width < 4){ // not big enough
				room = null;
				n.setRoom(room);
			}
			else{ // big enough
				int roomWidth = getRand((int)(b.width/a),(int)(b.width-b.width/a));
				int roomHeight = getRand((int)(b.height/a),(int)(b.height-b.height/a));
				//int roomWidth = (int) (b.width/a);
				//int roomHeight = (int) (b.height/a);
				int roomX = (int) (b.width/(2*a));
				int roomY = (int) (b.height/(2*a));
						
				do roomX = getRand(1,b.width-roomWidth-1);
				while(roomX+roomWidth >= b.width);
				
				do roomY = getRand(1,b.height-roomHeight-1);
				while(roomY+roomHeight >= b.height);
				
				room = new Block(roomX,roomY,roomWidth,roomHeight);
				n.setRoom(room);
			}
		} // for
	} // method
	
	private Node splitNode(Node node, boolean vertical){
		Block b = node.getBlock();
		
		double a = blockShapeModifier;
		
		Block newBlock1;
		Block newBlock2;
		Point randPoint = new Point(getRand((int)(b.width/a),(int)(b.width-b.width/a)),getRand((int)(b.height/a),(int)(b.height-b.height/a)));
		//Point randPoint = new Point(b.width/2,b.height/2);
		
		//if(vertical){
		if(b.height >= b.width){
			newBlock1 = new Block(b.x,b.y,b.width,randPoint.y);
			newBlock2 = new Block(b.x, b.y + randPoint.y, b.width, b.height - randPoint.y);	
		}else{
			newBlock1 = new Block(b.x,b.y,randPoint.x,b.height);
			newBlock2 = new Block(b.x + randPoint.x, b.y, b.width - randPoint.x, b.height);	
		}
		
		Node newNode1 = new Node(newBlock1, node);
		Node newNode2 = new Node(newBlock2, node);
		node.setChilds(newNode1, newNode2);
		
		//System.out.println("Node splittd!");
		//System.out.println("\t1 " + newBlock1.toString());
		//System.out.println("\t2 " + newBlock2.toString());
		
		return node;
		
	}
	
	private void setPixel(int x,int y,int value){
		map[calcIndex(x, y)] = value;
	}
	
	private void drawRectangle(int x, int y, int width, int height, int value){
		for(int xa=0; xa < width; xa++){
			for(int ya=0; ya < height; ya++){
				setPixel(x+xa,y+ya,value);
			}
		}
	}
	
	private void createMap(){
		// make corridors
		for (Block b : corridorList) {
			drawRectangle(b.x,b.y,b.width,b.height,floor);
		}
		// make rooms
		for (Node n : lastLevel) {
			Block b = n.getRoomGlobal();
			if(b == null)
				System.out.println("Node without room!");
			else{
				drawRectangle(b.x,b.y,b.width,b.height,floor);
			}
		}
	}
	
	private int getRand(int min, int max) {
		 
//	    Date now = new Date();
//	    long seed = now.getTime() + oldseed;
//	    oldseed = seed;
//	     
//	    Random randomizer = new Random(seed);
	    int n = max - min + 1;
//	    int i = randomizer.nextInt(n);
//	    if (i < 0) i = -i;
//	 
//	    return min + i;
		return min + randomizer.nextInt(n);
	  }
	
	private boolean getRandBool(){

//	    Date now = new Date();
//	    long seed = now.getTime() + oldseed;
//	    oldseed = seed;
//	     
//	    Random randomizer = new Random(seed);
	    return randomizer.nextBoolean();
	}
	
	private ArrayList<Node> getAllLastLevelNodes (Node a ){
		ArrayList<Node> q = new ArrayList<Node>();
		q.add(a);
		Node n = q.get(0);
		
		while(n.hasBothChildren()){
			q.add(n.getLeftChild());
			q.add(n.getRightChild());
			q.remove(n);
			n = q.get(0);
		}
		return q;
	}
	
} // Class
