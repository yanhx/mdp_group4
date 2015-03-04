package map_simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar implements Runnable {
	private Cell target;
	private Cell source;
	private ArrayList<Cell> cellList;
	private Robot robot;
	private ControlPanel cp;
	private Direction direction, finalDirection;
	private final static int ARENA_WIDTH = 15;
	private final static int ARENA_LENGTH = 20;
	public Explorer myExplorer;

	public AStar(Map map, Robot robot, Position target, ControlPanel cp) {
		// initialize multiarray of cells
		this.cellList = new ArrayList<Cell>();
		for (int x = 0; x < ARENA_WIDTH; x++) {
			for (int y = 0; y < ARENA_LENGTH; y++) {
				cellList.add(new Cell(x, y, map.getLocInfo(x, y)));
			}
		}
		this.robot = robot;
		this.target = getCell(target.getX(), target.getY());

		this.source = getCell(robot.getPos().getX(), robot.getPos().getY());
		this.source.setH(compute_heuristic(source));
		this.source.setF(source.getG() + source.getH());
		this.source.setRobot_direction(this.robot.getDirection());
		this.direction = this.robot.getDirection();
		this.finalDirection = target.getDirection();
		this.cp = cp;
	}

	@SuppressWarnings("unused")
	public boolean compute_shortest_path() {
		ArrayList<Cell> path = new ArrayList<Cell>();
		PriorityQueue<Cell> pQueue = new PriorityQueue<Cell>(150,
				new CellComparator());
		Set<Cell> closeList = new HashSet<Cell>();
		pQueue.add(source);
		int minF = 100000;
		Cell minC = target;
		int count = 5;

		while (pQueue.size() > 0) {
			Cell curr = pQueue.poll();
			closeList.add(curr);
		//	System.out.println("curr: " + curr.getX()+"," + curr.getY() +","+ curr.getH());
			if(curr.getH() < minF) {
//				System.out.println("minF: " + minF);
				minF = curr.getH();
				minC = curr;
			}
			if (curr.equals(target)) {
				count--;
				if(count<=0){
					minC = target;
					break;
				}

			}
			// get adjacent cells
			ArrayList<Cell> neighbors = get_neighbors(curr);
			for (Cell neighbor : neighbors) {
				if (isAllowed(neighbor) && !closeList.contains(neighbor)) {
					if (pQueue.contains(neighbor)) {
						if (neighbor.getG() > curr.getG()
								+ compute_dist(curr, neighbor) + 10) {
							updateCell(curr, neighbor);
						}
					} else {
						updateCell(curr, neighbor);
						pQueue.add(neighbor);
					}
				}
			}
		}

		// retrieve the cells for shortest path
		path = retrieve_shortest_path(minC);
		System.out.println("minC:" + minC.getX() + ", " + minC.getY());
		if(path == null)
			return false;

		// convert the shortestpath into instructions for the robot
		ArrayList<String> path_instructions = new ArrayList<String>();
		path_instructions = convert_path_into_instruction(path);

		if (Config.SIMULATOR == false
				|| (Config.SIMULATOR == false && Config.SHORTESTPATH_SIMULATOR == true)) {
			int dist = 0;
			String current = "";
			boolean wasStillMovingStraight = false;

			// sending start indicator
			Client.pauseThreadListening();
			Client.send(RobotDescriptor.shortestPathStartIndicator());
			waitForOk();

			if (path_instructions.size() == 1) {
				if (path_instructions.get(0)
						.equals(Config.MOVEMENT_GO_STRAIGHT)) {
					dist++;
					// send
					Client.send(RobotDescriptor.convert(Config.MODE_EXPLORE
							+ Config.MOVEMENT_GO_STRAIGHT
							+ Config.SPEED_ONE_QUARTER
							+ RobotDescriptor.convertDistance(dist)));
					waitForOk();
				} else {
					Client.send(RobotDescriptor.convert(Config.MODE_EXPLORE
							+ path_instructions.get(0)
							+ Config.SPEED_ONE_QUARTER
							+ RobotDescriptor.convertDistance(0)));
					waitForOk();
				}
				System.out.println("Only one instruction which is: "
						+ path_instructions.get(0));
			} else {
				for (int i = 0; i < path_instructions.size(); i++) {
					current = path_instructions.get(i);
					if (!current.equals(Config.MOVEMENT_GO_STRAIGHT)) {
						if (wasStillMovingStraight) {
							// send
							System.out.println("---Robot going Straight at "
									+ dist + "---");
							Client.send(RobotDescriptor
									.convert(Config.MODE_EXPLORE
											+ Config.MOVEMENT_GO_STRAIGHT
											+ Config.SPEED_ONE_QUARTER
											+ RobotDescriptor
													.convertDistance(dist)));
							waitForOk();
							dist = 0;
						}
						wasStillMovingStraight = false;
						// send
						System.out
								.println("---Robot doing: " + current + "---");
						Client.send(RobotDescriptor.convert(Config.MODE_EXPLORE
								+ current + Config.SPEED_ONE_QUARTER
								+ RobotDescriptor.convertDistance(0)));
						waitForOk();
					} else {
						wasStillMovingStraight = true;
						dist++;
					}
				}
				// send final instruction
				if (current.equals(Config.MOVEMENT_GO_STRAIGHT)) {
					System.out.println("---Robot doing: " + current + " at "
							+ dist + "---");
					Client.send(RobotDescriptor.convert(Config.MODE_EXPLORE
							+ current + Config.SPEED_ONE_QUARTER
							+ RobotDescriptor.convertDistance(dist)));
				}
			}
			Client.send(RobotDescriptor.shortestPathEndIndicator());
			waitForOk();

			Client.resumeThreadListening();
		}

		if (Config.SIMULATOR) {
			String current = "";
			for (int i = 0; i < path_instructions.size(); i++) {
				current = path_instructions.get(i);
				switch (current) {
				case Config.MOVEMENT_GO_STRAIGHT:
					robot.goForward();
					break;
				case Config.MOVEMENT_TURN_BACK:
					robot.turn180();
					break;
				case Config.MOVEMENT_TURN_LEFT:
					robot.turnLeft();
					break;
				case Config.MOVEMENT_TURN_RIGHT:
					robot.turnRight();
					break;
				}
				if(myExplorer != null) {
					myExplorer.updateSensorFromSimulator();
				}
				Simulator.boardPanel.repaint();
				try {
					Thread.sleep(cp.getspeed());
				} catch (InterruptedException evt) {
				}
			}
		}
		return true;
	}

	public ArrayList<String> convert_path_into_instruction(ArrayList<Cell> path) {
		ArrayList<String> instruction_list = new ArrayList<String>();

		        for(int i = 0; i < path.size(); i++){
		            Cell next = path.get(i);
		            String instruction = "";
		             
		            //robot moving towards East
		            if(next.getX() > robot.getPos().getX()){
		                switch(robot.getDirection()){
		                //if robot facing north, turn 90 degrees clockwise to east
		                case NORTH: robot.turnRight(); instruction = Config.MOVEMENT_TURN_RIGHT;
		                        break;
		                //if robot facing west, turn 90 degrees clockwise to north and then east
		                case WEST: robot.turn180(); instruction = Config.MOVEMENT_TURN_BACK;
		                        break;
		                //if robot facing south, turn 90 degrees anticlockwise to east
		                case SOUTH: robot.turnLeft(); instruction = Config.MOVEMENT_TURN_LEFT;
		                        break;
		                default: robot.goForward(); instruction = Config.MOVEMENT_GO_STRAIGHT;
		                    break;
		                }
		            }
		             
		            //robot moving towards West
		            else if(robot.getPos().getX() > next.getX()){
		                switch(robot.getDirection()){
		                //if robot facing north, turn 90 degrees anticlockwise to west
		                case NORTH: robot.turnLeft(); instruction = Config.MOVEMENT_TURN_LEFT;
		                        break;
		                //if robot facing east, turn 90 degrees clockwise to south and then west
		                case EAST: robot.turn180(); instruction = Config.MOVEMENT_TURN_BACK;
		                        break;
		                //if robot facing south, turn 90 degrees clockwise to west
		                case SOUTH: robot.turnRight(); instruction = Config.MOVEMENT_TURN_RIGHT;
		                        break;
		                default: robot.goForward(); instruction = Config.MOVEMENT_GO_STRAIGHT;
		                    break;
		                }
		            }
		            //robot moving towards North
		            else if(robot.getPos().getY() > next.getY()){
		                switch(robot.getDirection()){
		                //if robot facing west, turn 90 degrees clockwise to north
		                case WEST: robot.turnRight(); instruction = Config.MOVEMENT_TURN_RIGHT;
		                        break;
		                //if robot facing east, turn 90 degrees anticlockwise to north
		                case EAST: robot.turnLeft(); instruction = Config.MOVEMENT_TURN_LEFT;
		                        break;
		                //if robot facing south, turn 90 degrees clockwise to west then to north
		                case SOUTH: robot.turn180(); instruction = Config.MOVEMENT_TURN_BACK;
		                        break;
		                default:robot.goForward(); instruction = Config.MOVEMENT_GO_STRAIGHT;
		                        break;
		                }
		            }
		            //robot moving towards South
		            else if(robot.getPos().getY() < next.getY()){
		                switch(robot.getDirection()){
		                //if robot facing west, turn 90 degrees anticlockwise to south
		                case WEST: robot.turnLeft(); instruction = Config.MOVEMENT_TURN_LEFT;
		                        break;
		                //if robot facing east, turn 90 degrees clockwise to south
		                case EAST: robot.turnRight(); instruction = Config.MOVEMENT_TURN_RIGHT;
		                        break;
		                //if robot facing north, turn 90 degrees clockwise to east then to south
		                case NORTH: robot.turn180(); instruction = Config.MOVEMENT_TURN_BACK;
		                        break;
		                default: robot.goForward(); instruction = Config.MOVEMENT_GO_STRAIGHT;
		                    break;
		                } 
		            }
		            if(!instruction.equals(Config.MOVEMENT_GO_STRAIGHT)){
		                instruction_list.add(instruction);
		                robot.goForward();
		                instruction_list.add(Config.MOVEMENT_GO_STRAIGHT);
		            }
		            else{
		                instruction_list.add(instruction);
		            }
		        }
		String lastInstruction = directionInstruction(finalDirection,
				robot.getDirection());
		System.out.println("This is the target direction: " + finalDirection);
		if (lastInstruction != null) {
			instruction_list.add(lastInstruction);
		}
		robot.setDirection(this.source.getRobot_direction());
		robot.setPos(new Position(this.source.getX(), this.source.getY(),
				this.source.getRobot_direction()));
		return instruction_list;
	}

	public String directionInstruction(Direction target, Direction source) {
		if (target.equals(source))
			return null;
		else {
			switch (target) {
			case NORTH:
				if (source.equals(Direction.SOUTH))
					return Config.MOVEMENT_TURN_BACK;
				else if (source.equals(Direction.EAST))
					return Config.MOVEMENT_TURN_LEFT;
				else if (source.equals(Direction.WEST))
					return Config.MOVEMENT_TURN_RIGHT;

			case EAST:
				if (source.equals(Direction.SOUTH))
					return Config.MOVEMENT_TURN_LEFT;
				else if (source.equals(Direction.WEST))
					return Config.MOVEMENT_TURN_BACK;
				else if (source.equals(Direction.NORTH))
					return Config.MOVEMENT_TURN_RIGHT;

			case SOUTH:
				if (source.equals(Direction.WEST))
					return Config.MOVEMENT_TURN_LEFT;
				else if (source.equals(Direction.NORTH))
					return Config.MOVEMENT_TURN_BACK;
				else if (source.equals(Direction.EAST))
					return Config.MOVEMENT_TURN_RIGHT;

			case WEST:
				if (source.equals(Direction.SOUTH))
					return Config.MOVEMENT_TURN_RIGHT;
				else if (source.equals(Direction.NORTH))
					return Config.MOVEMENT_TURN_LEFT;
				else if (source.equals(Direction.EAST))
					return Config.MOVEMENT_TURN_BACK;
			}
		}
		return null;
	}

	private void waitForOk() {
		BufferedReader reader = Client.getBufferedReader();
		String s = "";

		while (s.equals("")) {
			try {
				s = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Wait OK result: " + s);
	}

	public ArrayList<Cell> retrieve_shortest_path(Cell end) {
		ArrayList<Cell> shortestPath = new ArrayList<Cell>();
		Cell curr = end;

		if(end.getParent() == null)
			return null;
		while (!curr.equals(source)) {
			shortestPath.add(curr);
			curr = curr.getParent();
		}
		Collections.reverse(shortestPath);
//		for(Cell cell : shortestPath) {
//			System.out.println(cell.getX() + " " + cell.getY());
//		}
		return shortestPath;
	}

	public ArrayList<Cell> get_neighbors(Cell cell) {
		ArrayList<Cell> cList = new ArrayList<Cell>();
		// NORTH
		if (cell.getY() > 0)
			cList.add(getCell(cell.getX(), cell.getY() - 1));
		// EAST
		if (cell.getX() < 14)
			cList.add(getCell(cell.getX() + 1, cell.getY()));
		// SOUTH
		if (cell.getY() < 19)
			cList.add(getCell(cell.getX(), cell.getY() + 1));
		// WEST
		if (cell.getX() > 0)
			cList.add(getCell(cell.getX() - 1, cell.getY()));

		return cList;
	}

	public int compute_heuristic(Cell cell) {
//		return 10 * (Math.abs(cell.getX() - target.getX()) + Math.abs(cell
//				.getY() - target.getY()));
		return  (int) (10 *Math.sqrt( Math.abs(cell.getX() - target.getX()) *Math.abs(cell.getX() - target.getX()) + 
		Math.abs(cell.getY() - target.getY()) * Math.abs(cell.getY() - target.getY())));
	}
	
	public int dirToInt(Direction dir) {
		if(dir == Direction.NORTH)
			return -1;
		if(dir == Direction.WEST)
			return -2;
		if(dir == Direction.SOUTH)
			return 1;
		return 2;
	}
	
	public Direction IntToDir(int dir) {
		if(dir == -1)
			return Direction.NORTH;
		if(dir == -2)
			return Direction.WEST;
		if(dir == 1)
			return Direction.SOUTH;
		return Direction.EAST;
	}

	public int compute_dist(Cell cell, Cell neighbor) {
		int total = 0;

		int movingDir = (neighbor.getX() - cell.getX()) * 2 + neighbor.getY() - cell.getY();
		int currDir = dirToInt(cell.getRobot_direction());
		
		direction = IntToDir(movingDir);
		if(movingDir == currDir)
			total = 0;
		else if(movingDir + currDir == 0)
			total = 20;
		else total = 10;
		
		return total;
	}

	public void updateCell(Cell cell, Cell neighbor) {
		neighbor.setG(cell.getG() + 10 + compute_dist(cell, neighbor));
		neighbor.setRobot_direction(direction);
		neighbor.setH(compute_heuristic(neighbor));
		neighbor.setParent(cell);
		neighbor.setF(neighbor.getG() + neighbor.getH());
		neighbor.setDirection(direction);
	}

	public Cell getCell(int x, int y) {
		int num = x * 20 + y;
		return cellList.get(num);
	}

	public boolean isAllowed(Cell cell) {
		if (cell.getX() == 0 || cell.getX() == 14)
			return false;
		if (cell.getY() == 0 || cell.getY() == 19)
			return false;
		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				if (getCell(cell.getX() + i, cell.getY() + j).getType() != 1)
					return false;
		return true;
	}

	public class CellComparator implements Comparator<Cell> {
		@Override
		public int compare(Cell first, Cell second) {
			if (first.getF() < second.getF()) {
				return -1;
			} else if (first.getF() > second.getF()) {
				return 1;
			} else {
				if (first.getG() < second.getG()) {
					return -1;
				} else if (first.getG() > second.getG()) {
					return 1;
				} else
					return 0;
			}
		}
	}

	@Override
	public void run() {
		compute_shortest_path();

	}
}