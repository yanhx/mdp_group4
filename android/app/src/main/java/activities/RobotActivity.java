package activities;

import threads.BluetoothConnectedThread;
import utils.Constants;
import utils.ReferencesHolder;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.mdp.R;

import fragments.ConfigurationFragment;
import fragments.DashboardFragment;
import fragments.MapFragment;

public class RobotActivity extends Activity {
	
	protected boolean autoUpdate = true;
	protected int robotMode = Constants.EXPLORE;
	protected int robotCameraMode = Constants.CAMERA_NORMAL;

	private MapFragment fragmentMap;
	private DashboardFragment fragmentDashboard;
	private float lastY = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_activity);
		if (savedInstanceState == null) {
			
			// Dashboard fragment
			fragmentDashboard = new DashboardFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container_dashboard, fragmentDashboard).commit();
			
			// Map fragment
			fragmentMap = new MapFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container_map, fragmentMap).commit();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.robot_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		if (item.getItemId() == R.id.configure) {
			showConfig();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (robotCameraMode == Constants.CAMERA_NORMAL){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				
				lastY = event.getY();
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				
				float currentY = event.getY();
				fragmentMap.panCamera(lastY-currentY);
				lastY = currentY;
				
				break;
				
			default:
				
				break;
				
			}
			
			return true;
		}
		else {
			return super.onTouchEvent(event);
		}
	}
	
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	public int getRobotMode() {
		return robotMode;
	}
	
	public void setRobotMode(int mode) {
		this.robotMode = mode;
		
		if (mode == Constants.EXPLORE) {
			//sendControlSignal(Constants.EXPLORE_PHASE);
			fragmentMap.startMode(Constants.EXPLORE);
		}
		else if (mode == Constants.SPEEDRUN) {
			//sendControlSignal(Constants.SPEED_PHASE);
			fragmentMap.startMode(Constants.SPEEDRUN);
		}
	}
	
	/**
	 * Switch to the config fragment
	 */
	private void showConfig() {
		
		getFragmentManager().beginTransaction()
		.replace(R.id.container_dashboard, new ConfigurationFragment())
		.addToBackStack(null)
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.commit();
		
	}
	
	/**
	 * Switch to the dashboard fragment
	 */
	public void showDashboard() {
		
		getFragmentManager().beginTransaction()
		.replace(R.id.container_dashboard, new DashboardFragment())
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.commit();
		
	}
	
	/**
	 * Updates the positions of the obstacles on the map
	 */
	public void updatesMap() {
		fragmentMap.updatesMap();
	}
	
	/**
	 * Starts the robot
	 */
	public void start() {
		//sendControlSignal(Constants.START);
		switch (robotMode) {
		case Constants.EXPLORE:
			sendControlSignal(Constants.TO_PC + Constants.EXPLORE_PHASE);
			break;
		case Constants.SPEEDRUN:
			sendControlSignal(Constants.TO_PC + Constants.SPEED_PHASE);
			break;
		}
	}
	
	/**
	 * Stops the robot
	 */
	public void stop() {
		sendControlSignal(Constants.TO_PC + Constants.STOP);
	}
	
	/**
	 * Moves the robot forward
	 */
	public void moveForward() {
		fragmentMap.moveForward();
		sendControlSignal(Constants.TO_ARDUINO + Constants.MOVE_FORWARD);
	}
	
	/**
	 * Rotates the robot right (clockwise)
	 */
	public void rotateRight() {
		fragmentMap.rotateRight();
		sendControlSignal(Constants.TO_ARDUINO + Constants.TURN_RIGTH);
	}
	
	/**
	 * Rotates the robot left (anti-clockwise)
	 */
	public void rotateLeft() {
		fragmentMap.rotateLeft();
		sendControlSignal(Constants.TO_ARDUINO + Constants.TURN_LEFT);
	}
	
	/**
	 * Sends the string data after function button press
	 * @param which - identify which function button was pressed
	 */
	public void functionButton(int which) {
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		if (which == 1) {
			sendControlSignal(settings.getString("config1", "config1"));
		}
		else if (which == 2) {
			sendControlSignal(settings.getString("config2", "config2"));
		}
	}

	/**
	 * send the control signal message to bluetooth socket
	 * @param signal
	 */
	private void sendControlSignal(String signal) {
		BluetoothConnectedThread thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		if (thread != null) {
			if (!thread.isAlive()) {
				thread.start();
			}
			thread.write(signal.getBytes());
		}
	}

	public void changeCameraView(int mode) {
		this.robotCameraMode = mode;
		fragmentMap.changeCameraView(mode);
	}
}
