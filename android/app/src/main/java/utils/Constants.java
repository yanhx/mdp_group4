package utils;

import java.util.UUID;

public class Constants {
    public static final UUID MDP_UUID = UUID.fromString 
    		("00001101-0000-1000-8000-00805F9B34FB"); 
  	public static final int MESSAGE_READ = 0;
  	public static final int MESSAGE_BLUETOOTH_CONNECTED = 1;
  	public static final int MESSAGE_BLUETOOTH_DISCONNECTED = 2;
  	
  	// Instructions Encoders
  	public static final int TO_PC = 1;
  	public static final int TO_ANDROID = 2;
  	public static final int TO_ARDUINO = 4;
  	
  	//control robot movements go here
  	public static final String MOVE_FORWARD = "F";
  	public static final String TURN_LEFT = "L";
  	public static final String TURN_RIGTH = "R";
  	public static final String STOP = "STOP";
  	public static final String START = "START";
  	public static final String EXPLORE_PHASE = "EXPLORE";
  	public static final String SPEED_PHASE = "SPEED";  	
  	
  	// Camera mode
  	public static final int CAMERA_NORMAL = 1;
  	public static final int CAMERA_FPV = 2;
  	
  	// Robot orientation
	public static final int EAST = 0;
	public static final int SOUTH = 1;
	public static final int WEST = 2;
	public static final int NORTH = 3;
	
	// Robot mode
	public static final int EXPLORE = 0;
	public static final int SPEEDRUN = 1;
	
	// Robot status
	public static final String STATUS_MOVE = "MOVING";
	public static final String STATUS_STOP = "STOPPED";
	public static final String STATUS_ROTATE = "ROTATING";
	
	// For the SharedPreferences
	public static final String PREFS_NAME = "com.example.mdp.config.myPrefsFile";
	
	
	// FOR RECEIVING
	// robot status
	public static final String CTRL_FORWARD = "CTRL_FORWARD";
	public static final String CTRL_RIGHT = "CTRL_RIGHT";
	public static final String CTRL_LEFT = "CTRL_LEFT";
	
	// obstacles
	public static final String UPDATE_ROBOT = "robot";
	
}
