package map_simulator;

public class Cell {
	private int x,y,g,h,f;
	private Cell parent;
	private int type;
	private Direction robot_direction;
	private Direction direction;
	
	public Cell(int x, int y, int type){
		this.x=x;
		this.y=y;
		this.type=type;
		this.g=0;
		this.h=0;
		this.f=0;
		this.parent=null;
		this.robot_direction = null;
		this.direction = null;
	}
	
	public Direction getDirection() {
		return direction;
	}



	public void setDirection(Direction direction) {
		this.direction = direction;
	}



	public Direction getRobot_direction() {
		return robot_direction;
	}



	public void setRobot_direction(Direction robot_direction) {
		this.robot_direction = robot_direction;
	}



	public int getX() {
		return x;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getF() {
		return f;
	}
	public void setF(int f) {
		this.f = f;
	}
	public Cell getParent() {
		return parent;
	}
	public void setParent(Cell parent) {
		this.parent = parent;
	}

	
	
}
