package map_simulator;

import java.io.BufferedReader;
import java.util.Stack;

public class Explorer implements Runnable {

	private Stack<Position> stack;
	private Robot r;
	private Map m;
	private ControlPanel cp;
	private MapDescriptor md;
	private int count = 0;
	
	public Explorer(Map m, Robot r, ControlPanel cp) {
		stack = new Stack<Position>();

		this.r = Simulator.robot;
		this.m = m;
		this.cp = cp;
		
		stack.push(r.getPos());
	}
	
	public void startSimulatedExplorerPath(){
		int count = 0;
		boolean reachStart = false;
		boolean reachGoal = false;
		int percentage = cp.setPercentage();
		boolean finished = false;
		int noOfMovement = 0;
//		int noOfTimesFacingWall = 0;
		
		while(!finished){	
			
			//calibration
//			if (r.checkRobotFrontFacingWall()){
//				System.out.println("Calibrate front!!!");
//				updateSensorFromSimulator();
//			}
//			else if (r.checkRobotRightFacingWall()){
//				if (noOfTimesFacingWall % RIGHT_FACING_WALL_NUMBER == 0) {
//					System.out.println("Calibrate side!!!");
//					updateSensorFromSimulator();
//				}
//				noOfTimesFacingWall++;
//			}
			System.out.println("Out of the while loop!");
			updateSensorFromSimulator();
			
			if(r.isFrontClear(m)){
				r.goForward();
				noOfMovement++;
				Simulator.boardPanel.repaint();
			}
			else{
				count -= 1;
				r.turnLeft();
				noOfMovement++;
				Simulator.boardPanel.repaint();
				updateSensorFromSimulator();
				
				while(count != 0){

					if (cp.getInterval() <= 0) {
						finished = true;
						break;
					}
					if (reachStart && reachGoal) {
						if (cp.getInterval() != 0 && cp.getInterval() <= 30) {
							finished = true;
							break;
						}
						
						//System.out.println("##Percentage: " + m.checkExplorationState());
						
						if (m.checkExplorationState() >= percentage) {
							finished = true;
							break;
						}
					}
					
					if (r.getPos().getX() == 13 && r.getPos().getY() == 1) {
						reachGoal = true;
					}
					
					if (r.getPos().getX() == 1 && r.getPos().getY() == 18) {
						if (reachStart && reachGoal) {	//reach start point again
							int[] position = m.getOneUnexploredBlock();
							while(position != null) {
								System.out.println("#position: " +position[0] + "  "+position[1]);
								AStar a = new AStar(m,r,new Position(position[0],position[1],Direction.NORTH),cp);
								a.myExplorer = this;
								if(a.compute_shortest_path()) {
									position = m.getOneUnexploredBlock();
								}
								
								else {
									m.setLocInfo(position[0], position[1], 2);
									position = m.getOneUnexploredBlock();
									
							    }
								//System.out.println("&&" + r.getPos().getX() + "," +r.getPos().getY()  );
							}
							AStar a = new AStar(m,r,new Position(1,18,Direction.SOUTH),cp);
							a.compute_shortest_path();
							finished = true;
							break;
						}
						

						
						reachStart = true;
					}
					
					//calibration
//					if (r.checkRobotFrontFacingWall()){
//						System.out.println("Calibrate front!!!");
//						updateSensorFromSimulator();
//					}
//					else if (r.checkRobotRightFacingWall()){
//						if (noOfTimesFacingWall % RIGHT_FACING_WALL_NUMBER == 0) {
//							System.out.println("Calibrate side!!!");
//							updateSensorFromSimulator();
//						}
//						noOfTimesFacingWall++;
//					}
					
					if(r.isRightClear(m)){
						r.turnRight();
						noOfMovement++;
						Simulator.boardPanel.repaint();
						updateSensorFromSimulator();
						
						r.goForward();
						noOfMovement++;
						updateSensorFromSimulator();
						Simulator.boardPanel.repaint();
						count += 1;
					}
					else{
						if(r.isFrontClear(m)){
							r.goForward();
							noOfMovement++;
							Simulator.boardPanel.repaint();
							updateSensorFromSimulator();
						}
						else{
							r.turnLeft();
							noOfMovement++;
							Simulator.boardPanel.repaint();
							updateSensorFromSimulator();
							count -= 1;
						}
					}
				}	//while(count != 0){	
			}	//else
		}	//while(!reachGoal || !reachStart){
		
//		System.out.println("Start shortest path back to start point!");
//		AStar a = new AStar(Simulator.boardPanel.getMap(),r,new Position(1,18,Direction.NORTH), cp);
//		a.compute_shortest_path();
		
		System.out.println("Exploration end!");
		System.out.println("Total steps: " + noOfMovement);
		cp.stopTimer();

			
		Config.m.copyMap(m.getMap());
		m.convertUnexploredToClear();
		Simulator.boardPanel.repaint();
		if(r.getPos().getX() != 1 || r.getPos().getY() != 18) {
		    AStar a = new AStar(m,r,new Position(1,18,Direction.SOUTH),cp);
		    a.compute_shortest_path();
		}
		
		//print map descriptor
		md = new MapDescriptor();
		System.out.println("-----Map Descriptor-----");
		System.out.println("String P1: " + md.encodeExploredUnexplored(m.getMap()));
		System.out.println("String P2: " + md.encodeObstacle(m.getMap()));
		cp.setString1(md.encodeExploredUnexplored(m.getMap()));
		cp.setString2(md.encodeObstacle(m.getMap()));
	}
	
	public void startExplorerPath(){
		boolean reachStart = false;
		boolean reachGoal = false;
		boolean finished = false;
		String code = "";
		String asciiCode = "";
		
		md = new MapDescriptor();
		
		//testing
		//code = Config.MODE_CALIBRATE + Config.MOVEMENT_TURN_BACK + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
//		code = Config.MODE_CALIBRATE + Config.MOVEMENT_TURN_RIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
//		Client.pauseThreadListening();
//		Client.send(RobotDescriptor.convert(code));
//		updateSensorFromRobot();
		
		//start exploration
		code = "10100000000000";
		for (int i = 0; i < code.length(); i += 7){
			asciiCode += (char)Integer.parseInt(code.substring(i, i+7), 2);
		}
		
		Client.pauseThreadListening();
		Client.send(asciiCode);
		updateSensorFromRobot();
		Simulator.boardPanel.repaint();
		
		while(!finished){	

			//calibration
//			if (r.checkRobotRightFacingWall()){
//				if (noOfTimesFacingWall % RIGHT_FACING_WALL_NUMBER == 0) {
//					calibrateSide();
//				}
//				noOfTimesFacingWall++;
//			}
			
			System.out.println("Out of the while loop!");
			
			if(r.isFrontClear(m)){	
				r.goForward();
				updateSensorFromRobot();
				Simulator.boardPanel.repaint();
			}
			else{
				count -= 1;
				r.turnLeft();
				updateSensorFromRobot();
				Simulator.boardPanel.repaint();
				
				while(count != 0){
					System.out.println("count= " + count);
					
					///////////////////////////////////////////////////////////////////////////
					//robot reaches goal and calibrates at goal
					//normal case
					if (r.getPos().getX() == 13 && r.getPos().getY() == 1) {
						reachGoal = true;
						robotParkingAtGoal();
					} 
					//robot shift 1 block to West
					else if (r.getPos().getX() == 12 && r.getPos().getY() == 1 && (m.getLocInfo(14, 0) == 2 || m.getLocInfo(14, 1) == 2 || m.getLocInfo(14, 2) == 2)) {
						reachGoal = true;			
						robotParkingAtGoal();
					} 
					//robot shift 1 block to South
					else if (r.getPos().getX() == 13 && r.getPos().getY() == 2 && (m.getLocInfo(12, 0) == 2 || m.getLocInfo(13, 0) == 2 || m.getLocInfo(14, 0) == 2)) { 
						reachGoal = true;					
						robotParkingAtGoal();
					} 
					//robot shift 1 block to West and 1 block to South
					else if (r.getPos().getX() == 12 && r.getPos().getY() == 2 && ((m.getLocInfo(14, 1) == 2 || m.getLocInfo(14, 2) == 2) && (m.getLocInfo(12, 0) == 2 || m.getLocInfo(13, 0) == 2))) {
						reachGoal = true;	
						robotParkingAtGoal();
					}		
					
					//robot reaches start and calibrates at start
					//terminating condition: if the robot reaches the start after reached goal, the robot exploration will terminate and do parking				
					//normal case
					if (r.getPos().getX() == 1 && r.getPos().getY() == 18) {
						reachStart = true;
						robotParkingAtStart();
					}					
					//robot shift 1 block to North
					else if (r.getPos().getX() == 1 && r.getPos().getY() == 17 && (m.getLocInfo(0, 19) == 2 || m.getLocInfo(1, 19) == 2 || m.getLocInfo(2, 19) == 2)) {
						reachStart = true;
						robotParkingAtStart();
					}
					//robot shift 1 block to East
					else if (r.getPos().getX() == 2 && r.getPos().getY() == 18 && (m.getLocInfo(0, 17) == 2 || m.getLocInfo(0, 18) == 2 || m.getLocInfo(0, 19) == 2)) {
						reachStart = true;
						robotParkingAtStart();
					}					
					//robot shift 1 block to North and 1 block to East
					else if (r.getPos().getX() == 2 && r.getPos().getY() == 17 && (m.getLocInfo(0, 17) == 2 || m.getLocInfo(0, 18) == 2) && (m.getLocInfo(1, 19) == 2 || m.getLocInfo(2, 19) == 2)) {
						reachStart = true;
						robotParkingAtStart();
					}
					
					if (reachStart && reachGoal) {	//reach start point again
						int[] position = m.getOneUnexploredBlock();
						while(position != null) {
							System.out.println("#position: " +position[0] + "  "+position[1]);
							AStar a = new AStar(m,r,new Position(position[0],position[1],Direction.NORTH),cp);
							a.myExplorer = this;
							if(a.compute_shortest_path()) {
								position = m.getOneUnexploredBlock();
							}
							
							else {
								m.setLocInfo(position[0], position[1], 2);
								position = m.getOneUnexploredBlock();
								
						    }
							//System.out.println("&&" + r.getPos().getX() + "," +r.getPos().getY()  );
						}
						AStar a = new AStar(m,r,new Position(1,18,Direction.SOUTH),cp);
						a.compute_shortest_path();
						robotParkingAtStart();
						finished = true;
						break;
					}
					
					//special terminating condition: if the time less than 30 second with start and goal explored,
					//the robot exploration will terminate and return to start point by using fastest path algorithm
//					if (reachStart && reachGoal) {	
//						if (cp.getInterval() != 0 && cp.getInterval() <= 30) {
//							finished = true;
//							System.out.println("Start shortest path back to Start Point!");
//							AStar a = new AStar(Simulator.boardPanel.getMap(),r,new Position(1,18,Direction.WEST), cp);
//							a.compute_shortest_path();
//							Config.SHORTESTPATH = false;
//							Simulator.boardPanel.repaint();
//							robotParkingAtStart();
//							break;
//						}
//					}
					///////////////////////////////////////////////////////////////////////////////////////
					
					//algo
					if(r.isRightClear(m)){
						r.turnRight();
						updateSensorFromRobot();
						Simulator.boardPanel.repaint();
						
						r.goForward();
						updateSensorFromRobot();
						Simulator.boardPanel.repaint();
						
						count += 1;
					}
					else{
						if(r.isFrontClear(m)){
							r.goForward();
							updateSensorFromRobot();
							Simulator.boardPanel.repaint();
						}
						else{
							r.turnLeft();
							updateSensorFromRobot();
							Simulator.boardPanel.repaint();
							count -= 1;
						}
					}	//else
				}	//while(count != 0){	
			}	//else
		}	//while(!reachGoal || !reachStart){
		
		System.out.println("Exploration end!");
		cp.stopTimer();

		Config.m.copyMap(m.getMap());
		m.convertUnexploredToClear();
		Simulator.boardPanel.repaint();
		
		//print map descriptor
		System.out.println("-----Map Descriptor-----");
		System.out.println("String P1: " + md.encodeExploredUnexplored(m.getMap()));
		System.out.println("String P2: " + md.encodeObstacle(m.getMap()));
		cp.setString1(md.encodeExploredUnexplored(m.getMap()));
		cp.setString2(md.encodeObstacle(m.getMap()));
		
		Client.resumeThreadListening();
	}

	public void clearStack(){
		stack.clear();
	}

	public void updateSensorFromRobot(){
		String sensorData = "";	
		
		BufferedReader reader = Client.getBufferedReader();
		
		try {
			while(sensorData.equals("")) {
				System.out.println("sensor data: "+sensorData);
				System.out.println("waiting for sensor input...");
				sensorData = reader.readLine();
			}
			System.out.println();
			System.out.println("Received: " + sensorData);
			sensorData = RobotDescriptor.convertSensors(Integer.parseInt(sensorData));
		} catch (Exception e) {
			System.out.println("We are screwed.");
			//print map descriptor
			System.out.println("-----Map Descriptor-----");
			System.out.println("String P1: " + md.encodeExploredUnexplored(m.getMap()));
			System.out.println("String P2: " + md.encodeObstacle(m.getMap()));
			cp.setString1(md.encodeExploredUnexplored(m.getMap()));
			cp.setString2(md.encodeObstacle(m.getMap()));
			e.printStackTrace();
		}
		
		int[] SensorDataForShort = new int[4];
		int[] SensorDataForLeftLong = {1,1,1};
		
		for (int i = 0; i < 4; i++) {
			if (sensorData.charAt(i) == '1') {
				SensorDataForShort[i] = 2;
			}
			else {
				SensorDataForShort[i] = 1;
			}
		}
		
		switch(sensorData.substring(4, 6)) {
		case "00":
			break;
		case "01":
			SensorDataForLeftLong[0] = 2;
			break;
		case "10":
			SensorDataForLeftLong[1] = 2;
			break;
		case "11":
			SensorDataForLeftLong[2] = 2;
			break;
		}

		m.setLocInfo(r.getSensorR().getPositionNextToSensor().getX(), r.getSensorR().getPositionNextToSensor().getY(), SensorDataForShort[0]);	
		m.setLocInfo(r.getSensorF3().getPositionNextToSensor().getX(), r.getSensorF3().getPositionNextToSensor().getY(), SensorDataForShort[1]);
		m.setLocInfo(r.getSensorF2().getPositionNextToSensor().getX(), r.getSensorF2().getPositionNextToSensor().getY(), SensorDataForShort[2]);
		m.setLocInfo(r.getSensorF1().getPositionNextToSensor().getX(), r.getSensorF1().getPositionNextToSensor().getY(), SensorDataForShort[3]);	
		
		Position[] list = r.getSensorL().getPositionfLongRangeSensor();

		for(int i = 0; i < list.length; i++){
			m.setLocInfo(list[i].getX(), list[i].getY(), SensorDataForLeftLong[i]);
			if(SensorDataForLeftLong[i]==2){
				break;
			}
		}
		m.setExplored(r);
		Simulator.boardPanel.repaint();
	}
	
	public void updateSensorFromSimulator(){
		sleep();
		
		m.setLocInfo(r.getSensorF1().getPositionNextToSensor().getX(), r.getSensorF1().getPositionNextToSensor().getY(), r.getSensorF1().getData());	
		m.setLocInfo(r.getSensorF2().getPositionNextToSensor().getX(), r.getSensorF2().getPositionNextToSensor().getY(), r.getSensorF2().getData());
		m.setLocInfo(r.getSensorF3().getPositionNextToSensor().getX(), r.getSensorF3().getPositionNextToSensor().getY(), r.getSensorF3().getData());
		m.setLocInfo(r.getSensorR().getPositionNextToSensor().getX(), r.getSensorR().getPositionNextToSensor().getY(), r.getSensorR().getData());
		
		Position[] list = r.getSensorL().getPositionfLongRangeSensor();
		int[] dataList = r.getSensorL().getRangeData();
		for(int i = 0; i < list.length; i++){
//			System.out.println(list[i]);
			m.setLocInfo(list[i].getX(), list[i].getY(), dataList[i]);
			if(dataList[i]==2){
				break;
			}
		}
		m.setExplored(r);
		Simulator.boardPanel.repaint();
		
		sleep();
	}
	
	public void sleep(){
		try {
			Thread.sleep(cp.getspeed());
		} catch (InterruptedException evt) {
		}
	}
	
	public void robotParkingAtStart() {
		Direction lastDirection180;
		
		//reverse the direction of last entering direction
		lastDirection180 = r.convertDirection180();
		System.out.println("Last direction at Start: " + lastDirection180);
		
		//clear start zone
		m.setLocInfo(0, 17, 1);
		m.setLocInfo(0, 18, 1);
		m.setLocInfo(0, 19, 1);
		m.setLocInfo(1, 17, 1);
		m.setLocInfo(1, 18, 1);
		m.setLocInfo(1, 19, 1);
		m.setLocInfo(2, 17, 1);
		m.setLocInfo(2, 18, 1);
		m.setLocInfo(2, 19, 1);
		
		//set the position of robot at start zone
		r.setPos(new Position(1, 18, r.getDirection()));
		Simulator.boardPanel.repaint();
				
		/* parking functions */
		//make robot to align with left wall
		if (r.makeRobotFacingWest() == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingWest() == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingWest() == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
		
		//maintain a fixed distance with left wall and do calibration
		calibrateFront();
		calibrateFront();
		
		//make robot to align with bottom wall
		if (r.makeRobotFacingSouth() == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingSouth() == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingSouth() == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
		
		//maintain a fixed distance with bottom wall and do calibration
		calibrateFront();
		calibrateFront();
		
		//make robot to face the reversed direction
		if (r.southToGivenDirection(lastDirection180) == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.southToGivenDirection(lastDirection180) == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.southToGivenDirection(lastDirection180) == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
	}
	
	public void robotParkingAtGoal() {		
		Direction lastDirection180;
		
		lastDirection180 = r.convertDirection180();
		System.out.println("Last direction at Goal: " + lastDirection180);
		
		//set Goal zone clear
		m.setLocInfo(12, 0, 1);
		m.setLocInfo(13, 0, 1);
		m.setLocInfo(14, 0, 1);
		m.setLocInfo(12, 1, 1);
		m.setLocInfo(13, 1, 1);
		m.setLocInfo(14, 1, 1);
		m.setLocInfo(12, 2, 1);
		m.setLocInfo(13, 2, 1);
		m.setLocInfo(14, 2, 1);
		
		r.setPos(new Position(13, 1, r.getDirection()));
		Simulator.boardPanel.repaint();
		
		//parking (1 minute)
		if (r.makeRobotFacingEast() == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingEast() == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingEast() == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
		
		calibrateFront();
		calibrateFront();
		
		if (r.makeRobotFacingNorth() == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingNorth() == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.makeRobotFacingNorth() == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
		
		calibrateFront();
		calibrateFront();
		
		//make robot facing 180 degree of last direction
		if (r.northToGivenDirection(lastDirection180) == "L") {
			r.turnLeft();
			count -= 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.northToGivenDirection(lastDirection180) == "R") {
			r.turnRight();
			count += 1;
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
		}
		else if (r.northToGivenDirection(lastDirection180) == "RR") {
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			r.turnRight();
			updateSensorFromRobot();
			Simulator.boardPanel.repaint();
			count -= 2;
		}
	}
	
	public void calibrateFront() {
		String code = "";
		
		//01101000000000
		code = Config.MODE_CALIBRATE + Config.MOVEMENT_TURN_RIGHT + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
		Client.send(RobotDescriptor.convert(code));	
		System.out.println("Calibrate front!!!");
		updateSensorFromRobot();
		Simulator.boardPanel.repaint();
//		System.out.println("Finish Calibrate front!!!");
		
	}
	
	public void calibrateSide() {
		String code = "";
		
		code = Config.MODE_CALIBRATE + Config.MOVEMENT_TURN_BACK + Config.SPEED_ONE_QUARTER + RobotDescriptor.convertDistance(0);
		Client.send(RobotDescriptor.convert(code));	
		System.out.println("Calibrate side!!!");
		updateSensorFromRobot();
		Simulator.boardPanel.repaint();
//		System.out.println("Finish Calibrate side!!!");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!Config.SIMULATOR) {
			//startExplorerPath();
			Client.pauseThreadListening();
			r.goForward();
			//updateSensorFromRobot();
			r.turnLeft();
			//updateSensorFromRobot();
			r.turnRight();
			//updateSensorFromRobot();
			//calibrateFront();
			//updateSensorFromRobot();
			//calibrateSide();
			//updateSensorFromRobot();
			Client.resumeThreadListening();
		} 
		else {
			startSimulatedExplorerPath();
		}
	}
}
