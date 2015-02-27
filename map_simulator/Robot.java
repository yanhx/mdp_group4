package map_simulator;

import java.util.ArrayList;
import java.util.Arrays;

public class Robot {
//	private int x, y;
	private Position pos;
	private Direction direction;
	private Sensor sensorF1;
	private Sensor sensorF2;
	private Sensor sensorF3;
	private Sensor sensorL;
	private Sensor sensorR;	
	//private Image robot_up, robot_left, robot_right, robot_down;

//	private static final int NORTH = 0;
//	private static final int WEST = 1;
//	private static final int EAST = 2;
//	private static final int RIGHT = 3;
	//0 = front, 1 = left, 2 = right, 3 = back
//	private int rot;
	
	public Robot(Map m){

		//exploration
		if (Config.SHORTESTPATH_SIMULATOR) {
			this.direction = Direction.NORTH;
			this.pos = new Position(1, 18, this.direction);
		} 
		else {
			this.direction = Direction.WEST;
			this.pos = new Position(7, 10, this.direction);
		}		
		//this.pos = new Position(1, 18, this.direction);
		//shortest path
//		this.pos = new Position(1, 18, this.direction);
		
		sensorF1 = new Sensor(SensorType.SENSOR_F1, new Position(pos.getX()-1, pos.getY()+1, Direction.WEST), Direction.WEST, m);
		sensorF2 = new Sensor(SensorType.SENSOR_F2, new Position(pos.getX()-1, pos.getY(), Direction.WEST), Direction.WEST, m);
		sensorF3 = new Sensor(SensorType.SENSOR_F3, new Position(pos.getX()-1, pos.getY()-1, Direction.WEST), Direction.WEST, m);
		sensorL = new Sensor(SensorType.SENSOR_L, new Position(pos.getX(), pos.getY()+1, Direction.SOUTH), Direction.SOUTH, m);
		sensorR = new Sensor(SensorType.SENSOR_R, new Position(pos.getX()-1, pos.getY()-1, Direction.NORTH), Direction.NORTH, m);
		
//		ImageIcon img = new ImageIcon("images/robot up.jpg");
//		robot_up = img.getImage();	
//		img = new ImageIcon("images/robot down.jpg");
//		robot_down = img.getImage();
//		img = new ImageIcon("images/robot left.jpg");
//		robot_left = img.getImage();
//		img = new ImageIcon("images/robot right.jpg");
//		robot_right = img.getImage();
	}
	
//	
//	public int getX() {
//		return x;
//	}
//	
//	public int getY() {
//		return y;
//	}
//
//	public void setX(int x) {
//		this.x = x;
//	}
//
//	public void setY(int y) {
//		this.y = y;
//	}

	// public Image getRobot() {
	// if(rot == 0)
	// return robot_up;
	// if(rot == 1)
	// return robot_left;
	// if(rot == 2)
	// return robot_right;
	// if (rot == 3)
	// return robot_down;
	// else
	// return null;
	// }

//	public int getRobot() {
//		return rot;
//	}
//
//	public void setRot(int value) {
//		this.rot = value;
//	}

//	public int getRot() {
//		return this.rot;
//	}

//	public void setRobot(Image robot) {
//		this.robot = robot;
//	}
	
//	public void move(int dx, int dy){	
//		x += dx;
//		y += dy;
//	}
	
//	public boolean checkRobotRightFacingWall() {
//		switch(direction) {
//		case NORTH:
//			if (pos.getX() == 13)
//				return true;
//			break;
//		case SOUTH:
//			if (pos.getX() == 1)
//				return true;
//			break;
//		case EAST: 
//			if (pos.getY() == 18)
//				return true;
//			break;
//		case WEST:
//			if (pos.getY() == 1)
//				return true;
//			break;
//		}
//		
//		return false;
//	}
//	
//	public boolean checkRobotFrontFacingWall() {
//		switch(direction) {
//		case NORTH:
//			if (pos.getY() == 1)
//				return true;
//			break;
//		case SOUTH:
//			if (pos.getY() == 18)
//				return true;
//			break;
//		case EAST: 
//			if (pos.getX() == 13)
//				return true;
//			break;
//		case WEST:
//			if (pos.getX() == 1)
//				return true;
//			break;
//		}
//		
//		return false;
//	}
	
	public String makeRobotFacingWest() {
		switch(direction) {
		case NORTH:
			return "L";
		case WEST:
			return "";
		case EAST:
			return "RR";
		case SOUTH:
			return "R";
		}
		
		return "";
	}
	
	public String makeRobotFacingEast() {
		switch(direction) {
		case NORTH:
			return "R";
		case WEST:
			return "RR";
		case EAST:
			return "";
		case SOUTH:
			return "L";
		}
		
		return "";
	}
	
	public String makeRobotFacingSouth() {
		switch(direction) {
		case NORTH:
			return "RR";
		case WEST:
			return "L";
		case EAST:
			return "R";
		case SOUTH:
			return "";
		}
		
		return "";
	}
	
	public String makeRobotFacingNorth() {
		switch(direction) {
		case NORTH:
			return "";
		case WEST:
			return "R";
		case EAST:
			return "L";
		case SOUTH:
			return "RR";
		}
		
		return "";
	}
	
	public Direction convertDirection180() {
		switch(direction) {
		case NORTH:
			return Direction.SOUTH;
		case WEST:
			return Direction.EAST;
		case EAST:
			return Direction.WEST;
		case SOUTH:
			return Direction.NORTH;
		}
		
		return Direction.EAST;
	}
	
	public String southToGivenDirection(Direction givenDirection) {
		switch(givenDirection) {
		case NORTH:
			return "RR";
		case WEST:
			return "R";
		case EAST:
			return "L";
		case SOUTH:
			return "";
		}
		
		return "";
	}
	
	public String northToGivenDirection(Direction givenDirection) {
		switch(givenDirection) {
		case NORTH:
			return "";
		case WEST:
			return "L";
		case EAST:
			return "R";
		case SOUTH:
			return "RR";
		}
		
		return "";
	}
	
	public void reset(){
		pos.setX(7);
		pos.setY(10);
		this.setDirection(Direction.NORTH);
	}
	
	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		this.pos.setDirection(direction);
	}

	public Sensor getSensorF1() {
		return sensorF1;
	}

	public void setSensorF1(Sensor sensorF1) {
		this.sensorF1 = sensorF1;
	}

	public Sensor getSensorF2() {
		return sensorF2;
	}

	public void setSensorF2(Sensor sensorF2) {
		this.sensorF2 = sensorF2;
	}

	public Sensor getSensorF3() {
		return sensorF3;
	}

	public void setSensorF3(Sensor sensorF3) {
		this.sensorF3 = sensorF3;
	}

	public Sensor getSensorL() {
		return sensorL;
	}

	public void setSensorL(Sensor sensorL) {
		this.sensorL = sensorL;
	}

	public Sensor getSensorR() {
		return sensorR;
	}

	public void setSensorR(Sensor sensorR) {
		this.sensorR = sensorR;
	}
	
	public void turnLeft() {
		String code;
		
		if(Config.SHORTESTPATH == false){
			code = Config.MODE_EXPLORE + Config.MOVEMENT_TURN_LEFT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
			Client.send(RobotDescriptor.convert(code));	
		}
		System.out.println("Turn Left!");
		
		switch(direction) {
		case NORTH:
			setDirection(Direction.WEST);
			break;
		case SOUTH:
			setDirection(Direction.EAST);
			break;
		case EAST: 
			setDirection(Direction.NORTH);
			break;
		case WEST:
			setDirection(Direction.SOUTH);
			break;
		}

		updateSensorPosition();
	}

	public void turn180() {
		
		switch(direction) {
		case NORTH:
			setDirection(Direction.SOUTH);
			break;
		case SOUTH:
			setDirection(Direction.NORTH);
			break;
		case EAST: 
			setDirection(Direction.WEST);
			break;
		case WEST:
			setDirection(Direction.EAST);
			break;
		}

		updateSensorPosition();
	}

	public void turnRight() {	
		String code;
		
		if(Config.SHORTESTPATH == false){
			code = Config.MODE_EXPLORE + Config.MOVEMENT_TURN_RIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
			Client.send(RobotDescriptor.convert(code));
		}
		System.out.println("Turn Right!");
		
		switch(direction) {
		case NORTH:
			setDirection(Direction.EAST);
			break;
		case SOUTH:
			setDirection(Direction.WEST);
			break;
		case EAST: 
			setDirection(Direction.SOUTH);
			break;
		case WEST:
			setDirection(Direction.NORTH);
			break;
		}

		updateSensorPosition();
	}
	
	public void goForward() {
		String code;
		
		if(Config.SHORTESTPATH == false){
			code = Config.MODE_EXPLORE + Config.MOVEMENT_GO_STRAIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(1);
			Client.send(RobotDescriptor.convert(code));	
		}
		System.out.println("Go Forward!");
		
		switch(direction){
		case NORTH:
			pos.setY(pos.getY()-1);
			break;
		case WEST:
			pos.setX(pos.getX()-1);
			break;
		case EAST:
			pos.setX(pos.getX()+1);
			break;
		case SOUTH:
			pos.setY(pos.getY()+1);
			break;
		}
		updateSensorPosition();
	}
	public boolean isFrontClear(Map map) {
		
		boolean clear = false;
		
		switch(direction) {
		case NORTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2;

				//check all the 3 position is walkable first
				//if all are walkable, check whether is visited or not
				//if one is not visited, set to true
				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;
								
				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2;

				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		}

		return clear;
	}
	
	public boolean isLeftClear(Map map) {
		
		boolean clear = false;
		
		switch(direction) {
		case NORTH:
			for (int i = -1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;

				// check all the 3 position is walkable first
				// if all are walkable, check whether is visited or nt
				// if one is not visited, set to true

				if(map.isVisited(x, y)){
					clear = true;
				}
				else{
					if(map.getLocInfo(x, y) == 2){
						updateSensorPosition();
						return false;
					}
					updateSensorPosition();
					clear = true;
				}
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2 ;
								
				if(map.isVisited(x, y)){
					clear = true;
				}
				else{
					if(map.getLocInfo(x, y) == 2){
						updateSensorPosition();
						return false;
					}
					updateSensorPosition();
					clear = true;
				}
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2;

				if(map.isVisited(x, y)){
					clear = true;
				}
				else{
					if(map.getLocInfo(x, y) == 2){
						updateSensorPosition();
						return false;
					}
					updateSensorPosition();
					clear = true;
				}
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				if(map.isVisited(x, y)){
					clear = true;
				}
				else{
					if(map.getLocInfo(x, y) == 2){
						updateSensorPosition();
						return false;
					}
					//updateSensorPosition();
					clear = true;
				}
			}
			break;
		}

		return clear;
	}

	public boolean isRightClear(Map map) {
		
		boolean clear = false;
		
		switch(direction) {
		case NORTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				//check all the 3 position is walkable first
				//if all are walkable, check whether is visited or nt
				//if one is not visited, set to true
				
				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2 ;
								
				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2;

				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;

				if(map.getLocInfo(x, y) == 2){
					return false;
				}
				else{
					clear = true;
					//updateSensorPosition();
				}
			}
			break;
		}

		return clear;
	}
	
	public boolean isFrontClear2(Map map) {
		
		boolean clear = false;
		int temp = 0;
		
		switch(direction) {
		case NORTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2;
			
				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;
								
				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		}

		return clear;
	}
	
	public boolean isLeftClear2(Map map) {
		
		boolean clear = false;
		int temp = 0;
		
		switch(direction) {
		case NORTH:
			for (int i = -1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2 ;
								
				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		}

		return clear;
	}

	public boolean isRightClear2(Map map) {
		
		boolean clear = false;
		int temp = 0;
		
		switch(direction) {
		case NORTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + 2;
				int y = pos.getY() + i;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case WEST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() - 2 ;
								
				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case EAST:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() + i;
				int y = pos.getY() + 2;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		case SOUTH:
			for(int i=-1; i <= 1; i++) {
				int x = pos.getX() - 2;
				int y = pos.getY() + i;

				if (map.getLocInfo(x, y) == 2) {
					return false;
				}

				if (map.getLocInfo(x, y) == 0) {
					clear = true;
				} else if (map.isVisited(x, y)) {
					clear = true;
					temp++;
				} else {
					clear = true;
				}
			}
			if (temp == 3) {
				return false;
			}
			break;
		}

		return clear;
	}

	// Update the sensors position and direction relative to the robot
	public void updateSensorPosition() {
		Position pos1 = sensorF1.getPosition();
		Position pos2 = sensorF2.getPosition();
		Position pos3 = sensorF3.getPosition();
		Position posL = sensorL.getPosition();
		Position posR = sensorR.getPosition();

		Direction dir1 = null;
		Direction dir2 = null;
		Direction dir3 = null;
		Direction dirL = null;
		Direction dirR = null;

		
		Position [] posList = new Position[5];
		Direction [] dirList = new Direction[5];
		ArrayList<Sensor> senorList = new ArrayList<Sensor>(Arrays.asList(sensorF1, sensorF2, sensorF3, sensorL, sensorR));
//		int [][] refer = new int[2][4] = {
		
		switch(direction) {
		case NORTH:

			// Sensor F1
			pos1.setX(pos.getX()-1);
			pos1.setY(pos.getY()-1);
			dir1 = Direction.NORTH;

			// Sensor F2
			pos2.setX(pos.getX());
			pos2.setY(pos.getY()-1);
			dir2 = Direction.NORTH;

			// Sensor F3
			pos3.setX(pos.getX()+1);
			pos3.setY(pos.getY()-1);
			dir3 = Direction.NORTH;

			// Sensor L
			posL.setX(pos.getX()-1);
			posL.setY(pos.getY());
			dirL = Direction.WEST;

			// Sensor R
			posR.setX(pos.getX()+1);
			posR.setY(pos.getY()-1);
			dirR = Direction.EAST;

			break;
		case SOUTH:

			// Sensor F1
			pos1.setX(pos.getX()+1);
			pos1.setY(pos.getY()+1);
			dir1 = Direction.SOUTH;

			// Sensor F2
			pos2.setX(pos.getX());
			pos2.setY(pos.getY()+1);
			dir2 = Direction.SOUTH;

			// Sensor F3
			pos3.setX(pos.getX()-1);
			pos3.setY(pos.getY()+1);
			dir3 = Direction.SOUTH;

			// Sensor L
			posL.setX(pos.getX()+1);
			posL.setY(pos.getY());
			dirL = Direction.EAST;

			// Sensor R
			posR.setX(pos.getX()-1);
			posR.setY(pos.getY()+1);
			dirR = Direction.WEST;

			break;
		case EAST:

			// Sensor F1
			pos1.setX(pos.getX()+1);
			pos1.setY(pos.getY()-1);
			dir1 = Direction.EAST;

			// Sensor F2
			pos2.setX(pos.getX()+1);
			pos2.setY(pos.getY());
			dir2 = Direction.EAST;

			// Sensor F3
			pos3.setX(pos.getX()+1);
			pos3.setY(pos.getY()+1);
			dir3 = Direction.EAST;

			// Sensor L
			posL.setX(pos.getX());
			posL.setY(pos.getY()-1);
			dirL = Direction.NORTH;

			// Sensor R
			posR.setX(pos.getX()+1);
			posR.setY(pos.getY()+1);
			dirR = Direction.SOUTH;

			break;
		case WEST:

			// Sensor F1
			pos1.setX(pos.getX()-1);
			pos1.setY(pos.getY()+1);
			dir1 = Direction.WEST;

			// Sensor F2
			pos2.setX(pos.getX()-1);
			pos2.setY(pos.getY());
			dir2 = Direction.WEST;

			// Sensor F3
			pos3.setX(pos.getX()-1);
			pos3.setY(pos.getY()-1);
			dir3 = Direction.WEST;

			// Sensor L
			posL.setX(pos.getX());
			posL.setY(pos.getY()+1);
			dirL = Direction.SOUTH;

			// Sensor R
			posR.setX(pos.getX()-1);
			posR.setY(pos.getY()-1);
			dirR = Direction.NORTH;

			break;
		}

		sensorF1.setDirection(dir1);
		sensorF2.setDirection(dir2);
		sensorF3.setDirection(dir3);
		sensorL.setDirection(dirL);
		sensorR.setDirection(dirR);
	}
}
