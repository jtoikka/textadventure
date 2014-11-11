package o1.mapGenerator;

import java.awt.Point;

public class Block{
	public int x;
	public int y;
	public int height;
	public int width;
	
	public Block(Block block){
		this(block.x, block.y, block.width, block.height);
		
	} // Method
	
	public Block(int x,int y, int width, int height){
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		
	} // Method
	
	public Point GetMiddlePoint(){
		int tmpX = width/2;
		int tmpY = height/2;
		return new Point(tmpX,tmpY);	
	} // Method
	
	public Point GetGlobalMiddlePoint(){
		int tmpX = width/2 + x;
		int tmpY = height/2 + y;
		return new Point(tmpX,tmpY);	
	} // Method
	
	public Boolean isTouching(Block a){
		if(isBetween(a.x, x, x + width) || isBetween(a.x + a.width, x, x + width )){
			if(isBetween(a.y,y,y + height) || isBetween(a.y + a.height ,y,y + height)){
				return true;
			}
		}
		
		if(isBetween(x, a.x, a.x + width) || isBetween(x + width, a.x, a.x + a.width )){
			if(isBetween(y,a.y,a.y + a.height) || isBetween(y + height ,a.y,a.y + a.height)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "Block: " + x + ", " + y + ", " + width + ", " + height;
	} // Method
	
	private Boolean isBetween(int a, int b1, int b2){ 
		if((b1-b2) > 0) return isBetween(a, b2, b1); // make sure that b2>b1
		int minCommonEdge = 0;
		if(a >= b1-minCommonEdge && a <= b2+minCommonEdge){
			return true;
		}else return false;
	}
} // Class