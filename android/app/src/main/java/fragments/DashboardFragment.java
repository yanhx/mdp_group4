package fragments;

import listeners.SocketListener;
import threads.BluetoothConnectedThread;
import utils.Constants;
import utils.ReferencesHolder;
import activities.RobotActivity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mdp.R;

public class DashboardFragment extends Fragment implements OnItemSelectedListener, SocketListener {
	
	private View rootView;
	private Button updateButton;
	private BluetoothConnectedThread thread;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		updateButton = (Button) rootView.findViewById(R.id.update_button);
		
		// Set the listener for the update button
		updateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).updatesMap();
			}
		});
		
		// Disable/enable the refresh button based on the toggle value
		ToggleButton updateToggle = (ToggleButton) rootView.findViewById(R.id.update_toggle);
		updateToggle.setChecked(true);
		
		updateToggle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (((ToggleButton)v).isChecked()) { // auto mode
					updateButton.setEnabled(false);
					((RobotActivity)rootView.getContext()).setAutoUpdate(true);
					((RobotActivity)rootView.getContext()).updatesMap();
				}
				else {	// manual mode
					updateButton.setEnabled(true);
					((RobotActivity)rootView.getContext()).setAutoUpdate(false);
				}
			}
		});
		
		
		
		// Set the listener for the robot controls
		((Button)rootView.findViewById(R.id.control_start)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).start();
			}
		});
		
		((Button)rootView.findViewById(R.id.control_stop)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).stop();
			}
		});
		
		((Button)rootView.findViewById(R.id.control_forward)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).moveForward();
			}
		});
		
		((Button)rootView.findViewById(R.id.control_right)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).rotateRight();
			}
		});
		
		((Button)rootView.findViewById(R.id.control_left)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).rotateLeft();
			}
		});
		
		
		
		// Set listeners for the two function buttons
		((Button)rootView.findViewById(R.id.func_button1)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).functionButton(1);
			}
		});
		
		((Button)rootView.findViewById(R.id.func_button2)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((RobotActivity)rootView.getContext()).functionButton(2);
			}
		});
		
		
		
		// Attach the listener to the spinner
		Spinner spinner = (Spinner) rootView.findViewById(R.id.mode);
		spinner.setOnItemSelectedListener(this);
		
		
		
		// Change the camera view between top-down and first person view according to the toggle button value
		ToggleButton changeCameraView = (ToggleButton) rootView.findViewById(R.id.change_camera_view);
		changeCameraView.setChecked(true);
		
		changeCameraView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (((ToggleButton)v).isChecked()) { // auto mode
					((RobotActivity)rootView.getContext()).changeCameraView(Constants.CAMERA_NORMAL);
				}
				else {	// manual mode
					((RobotActivity)rootView.getContext()).changeCameraView(Constants.CAMERA_FPV);
				}
			}
		});
		
		
		
		
		ReferencesHolder.MAIN_THREAD_HANDLER.attach(this);
		//ensure view has been inflated before start listening
		thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		if (thread != null && !thread.isAlive()) {
			thread.start();
		}
		
		
		
		return rootView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (((String) parent.getItemAtPosition(position)).equalsIgnoreCase("explore")) {
			((RobotActivity)rootView.getContext()).setRobotMode(Constants.EXPLORE);
			//Toast.makeText(rootView.getContext(), "Explore mode", Toast.LENGTH_SHORT).show();
		}
		else if (((String) parent.getItemAtPosition(position)).equalsIgnoreCase("speedrun")) {
			((RobotActivity)rootView.getContext()).setRobotMode(Constants.SPEEDRUN);
			//Toast.makeText(rootView.getContext(), "Speedrun mode", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
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
		TextView status = (TextView) rootView.findViewById(R.id.status);
		
		if (message.equalsIgnoreCase(Constants.STATUS_MOVE)) {
			status.setText(Constants.STATUS_MOVE);
			status.setBackgroundColor(Color.GREEN);
		}
		else if (message.equalsIgnoreCase(Constants.STATUS_STOP)) {
			status.setText(Constants.STATUS_STOP);
			status.setBackgroundColor(Color.RED);
		}
		else if (message.equalsIgnoreCase(Constants.STATUS_ROTATE)) {
			status.setText(Constants.STATUS_ROTATE);
			status.setBackgroundColor(0xFFFFD500);
		}
	}

	@Override
	public void bluetoothDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
