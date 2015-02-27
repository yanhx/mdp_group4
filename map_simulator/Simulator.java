package map_simulator;

public class Simulator {

	public static BoardPanel boardPanel;
	public static Robot robot;
	public static MainFrame m;
	public static ControlPanel cp;
	public static Explorer ex;
	private static Thread explorationThread;

	
	public Simulator(MainFrame maze){
		//GUI
		boardPanel = maze.getBoard();
		
		//Robot
		robot = new Robot(boardPanel.getMap());
		boardPanel.updateRobot();
		
		this.m = maze;
	}
	
	public static void startShortestPath(){
		Config.SHORTESTPATH = true;
		AStar a = new AStar(Config.m,robot,new Position(13,1,Direction.NORTH),m.controlPanel);
		
		if (!Config.SIMULATOR) {
			a.compute_shortest_path();
			Config.SHORTESTPATH = false;
			if (Config.SHORTESTPATH_SIMULATOR) {
				ex = new Explorer(boardPanel.getMap(),robot, m.controlPanel);
			}
			ex.robotParkingAtGoal();	
		}
		else {
			new Thread(a).start();
		}
	}
	
	public static void startExploration(){
		ex = new Explorer(boardPanel.getMap(),robot, m.controlPanel);
		explorationThread = new Thread(ex);
		explorationThread.start();
		//Client.send(robotCommand);
	}
	
	@SuppressWarnings("deprecation")
	public static void stopExploration(){
			ex.clearStack();
	}
	
	@SuppressWarnings("deprecation")
	public static void reset(){
		if (ex != null)
			ex.clearStack();
		
		//reset shortestpath boolean
		Config.SHORTESTPATH = false;
		
		boardPanel.reset();
		robot.reset();
		m.setVisible(false);
		m = new MainFrame();
		boardPanel = m.getBoard();
		
		//Robot
		robot = new Robot(boardPanel.getMap());
		boardPanel.updateRobot();
	
	}
}
