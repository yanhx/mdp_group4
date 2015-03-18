package fragments;

import java.util.ArrayList;

import listeners.SocketListener;

import threads.BluetoothConnectedThread;
import utils.Constants;
import utils.ReferencesHolder;

import activities.RobotActivity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mdp.R;
import common.CustomRenderer;

public class MapFragment extends Fragment implements SocketListener {
	
	private View rootView;
	private BluetoothConnectedThread thread;
	
	private GLSurfaceView mySurfaceView;
	private CustomRenderer myRenderer;
	
	private float currentCameraPos = -7.0f;
	private ArrayList<int[]> newObstacles;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		newObstacles = new ArrayList<int[]>();
		
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		mySurfaceView = new GLSurfaceView(container.getContext());
		
		// Check if the system supports OpenGL ES 2.0
		ActivityManager activityManager = (ActivityManager) container.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsES2 = configInfo.reqGlEsVersion >= 0x20000;
		
		if (supportsES2) {
			// Request an OpenGL ES 2.0 compatible context
			mySurfaceView.setEGLContextClientVersion(2);
			
			// Set the renderer to our renderer
			myRenderer = new CustomRenderer();
			mySurfaceView.setRenderer(myRenderer);
		}
		else {
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and 2
			return null;
		}
		
		
		
		ReferencesHolder.MAIN_THREAD_HANDLER.attach(this);
		//ensure view has been inflated before start listening
		thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		if (thread != null && !thread.isAlive()) {
			thread.start();
		}		
		
		return mySurfaceView;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mySurfaceView.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mySurfaceView.onPause();
	}
	
	/**
	 * Pan the camera up and down, based on the dragging motion
	 * @param distance - controls how much to pan
	 */
	public void panCamera(float distance) {
		
		// Smooth out the pan
		currentCameraPos -= (distance * 0.01);
		
		// Constraints the value of currentCameraPos between -7 and 0
		float value = Math.max(-7, Math.min(0, currentCameraPos));
		currentCameraPos = value;
			
		// Updates new camera position
		myRenderer.eyeY = value;
		
	}
	
	/**
	 * Updates the positions of the obstacles on the map
	 * @param obstacles - arrays of obstacle positions
	 */
	public void updatesObstacles(String[] obstacles) {
		
		newObstacles.clear();
		
		if (obstacles.length >= 1 && obstacles[0].length() > 1) {
			for (int i=0; i<obstacles.length; i++) {
				if (obstacles[i].length() > 1) {
					int row = Integer.parseInt(obstacles[i].substring(0, 2));
					int col = Integer.parseInt(obstacles[i].substring(2, 4));
					
					if (((RobotActivity)rootView.getContext()).isAutoUpdate())
						myRenderer.updatesGrid(col-1, row-1);
					else
						newObstacles.add(new int[]{col-1, row-1});
				}
			}
		}
	}
	
	/**
	 * Updates the map after the button is pressed
	 */
	public void updatesMap() {
		for (int i=0; i<newObstacles.size(); i++) {
			int col = newObstacles.get(i)[0];
			int row = newObstacles.get(i)[1];
			myRenderer.updatesGrid(col, row);
		}
		
		newObstacles.clear();
	}
	
	/**
	 * Moves the robot forward
	 */
	public void moveForward() {
		myRenderer.moveForward();
	}
	
	/**
	 * Rotate the robot right (clockwise)
	 */
	public void rotateRight() {
		myRenderer.rotateRight();
	}
	
	/**
	 * Rotate the robot left (anti-clockwise)
	 */
	public void rotateLeft() {
		myRenderer.rotateLeft();
	}

	@Override
	public void bluetoothConnected() {
		//update thread
		thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		if (thread != null && !thread.isAlive()) {
			thread.start();
		}
	}

	@Override
	public void receiverMessage(String message) {
/*		if (message.equalsIgnoreCase(Constants.CTRL_FORWARD)) {
			moveForward();
		}
		else if (message.equalsIgnoreCase(Constants.CTRL_RIGHT)) {
			rotateRight();
		}
		else if (message.equalsIgnoreCase(Constants.CTRL_LEFT)) {
			rotateLeft();
		}
		else if (message.startsWith("_")) { */
		
			// Split the string
			String[] commands = message.split("_");
			
			if (commands.length >= 2 ) {
				
				// Retrieves the robot position
				int robotRow = Integer.parseInt(commands[0].substring(0, 2));
				int robotCol = Integer.parseInt(commands[0].substring(2, 4));
				myRenderer.updateRobot(robotCol-1, robotRow-1);
				
				// Retrieves the robot orientation
				int robotOrientation = Integer.parseInt(commands[1]);
				// Converts the orientation to match Android Team's configuration
				if (robotOrientation == 1)
					myRenderer.updateRobotOrientation(1);
				else if (robotOrientation == 2)
					myRenderer.updateRobotOrientation(0);
				else if (robotOrientation == 3)
					myRenderer.updateRobotOrientation(3);
				else if (robotOrientation == 4)
					myRenderer.updateRobotOrientation(2);
				
				// Checks if there is any obstacle
				if (commands.length > 2) {
					// Retrieves obstacles information
					String[] obstacles = commands[2].split(" ");
					updatesObstacles(obstacles);
				}
			
			}
			
		//}
	}

	public void changeCameraView(int mode) {
		currentCameraPos = -7.0f;
		myRenderer.changeCameraView(mode);
	}

	@Override
	public void bluetoothDisconnected() {
		// TODO Auto-generated method stub
	}

	public void startMode(int mode) {
		myRenderer.setMode(mode);
	}

}
