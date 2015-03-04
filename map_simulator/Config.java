package map_simulator;

public class Config {
	//Simulator
	public final static boolean SIMULATOR = false; // simulated or real run
	public static Map m = new Map();
	public final static boolean SHORTESTPATH_SIMULATOR = false;
	
	//Shortest path pressed
	public static boolean SHORTESTPATH = false;
	
	//MODE
	public final static String MODE_STOP = "00";
	public final static String MODE_EXPLORE = "01";
	public final static String MODE_SHORTESTPATH = "10";
	public final static String MODE_CALIBRATE = "11";
	
	//SPEED
	public final static String SPEED_ONE_QUARTER = "00";
	public final static String SPEED_HALF = "01";
	public final static String SPEED_THREE_QUARTER = "10";
	public final static String SPEED_FULL = "11";
	
	//MOVEMENT
	public final static String MOVEMENT_TURN_BACK = "00";
	public final static String MOVEMENT_TURN_RIGHT = "01";
	public final static String MOVEMENT_TURN_LEFT = "10";
	public final static String MOVEMENT_GO_STRAIGHT = "11";
	
}
