package map_simulator;

public class Position {
	private int x;
	private int y;
	
	private Direction direction;
	
	public Position() {
		this(-1, -1, null);
	}
	
	public Position(int x, int y, Direction direction) {
		super();
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public int getX() {
		return x;
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
	
	public Position copyObject() {
		return new Position(this.x, this.y, this.direction);
	}
	
	public String toString() {
		return "row : " + x + ", col : " + y + ", direction : " + direction;
	}
	
//	public String toNumString() {
//		String dir = "";
//		switch(Integer.parseInt(Simulator.extraInfo[2])){
//		case 0:
//			dir = "N";
//			break;
//		case 1:
//			dir = "S";
//			break;
//		case 2:
//			dir = "E";
//			break;
//		case 3:
//			dir = "W";
//			break;
//		}
//		return  "Grid 15 20 " +  dir + " " + Simulator.extraInfo[1] + " " + Simulator.extraInfo[0];
//	}
	
}
