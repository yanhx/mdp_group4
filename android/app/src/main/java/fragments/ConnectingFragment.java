package fragments;

import java.util.Set;

import listeners.SocketListener;
import threads.BluetoothConnectThread;
import utils.ReferencesHolder;
import activities.RobotActivity;
import adapters.BluetoothDevicesAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import common.Logger;

public class ConnectingFragment extends Fragment implements SocketListener {
	private static final Logger LOG = Logger.getLogger(ConnectingFragment.class);
	private ArrayAdapter<BluetoothDevice> arrayAdapter = null;
	private Set<BluetoothDevice> pairedDevices; 
	private Activity activity;
	private BluetoothAdapter bluetoothAdapter;
	private ListView listDevices;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		bluetoothAdapter = ReferencesHolder.BLUETOOTH_ADAPTER;
		doSetup();		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.connecting_fragment_menu, menu);		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
  	switch (item.getItemId()) {
  		case (R.id.action_robot_activity):
  			Intent intent = new Intent(getActivity(), RobotActivity.class);
  			startActivity(intent);
  		case (R.id.action_refresh):
  			doSetup();
  	}
		return super.onOptionsItemSelected(item);
	}	
  
	private void doSetup() {
	  	arrayAdapter.clear();  			
	    getPairedDevices();  	
	  	bluetoothAdapter.cancelDiscovery();
	  	bluetoothAdapter.startDiscovery();    
	  }
 
	public void receiveNewDevice(BluetoothDevice device) {
		arrayAdapter.add(device);
	}
	
	private void getPairedDevices() {
	    pairedDevices = bluetoothAdapter.getBondedDevices();
	    arrayAdapter.addAll(pairedDevices);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LOG.v("On creatView");
		View view = inflater.inflate(R.layout.connecting_device_fragment, container, false);
	    listDevices = (ListView) view.findViewById(R.id.list_of_bluetooth_devices);
	    listDevices.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BluetoothDevice device = arrayAdapter.getItem(position);
				if (device.equals(ReferencesHolder.BLUETOOTH_DEVICE)) {
					navigateToDemo();
					return;
				}
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					Toast.makeText(activity, "Trying to connect", Toast.LENGTH_LONG).show();					
				} else {					
					Toast.makeText(activity, "Device not paired", Toast.LENGTH_LONG).show();
				}
				BluetoothConnectThread.runNewInstance(device);									
			}
	    });
	    arrayAdapter = new BluetoothDevicesAdapter();
	    listDevices.setAdapter(arrayAdapter);
	    // register to listen to bluetooth changes
		ReferencesHolder.MAIN_THREAD_HANDLER.attach(this);	  
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ReferencesHolder.MAIN_THREAD_HANDLER.detach(this);
	}

	@Override
	public void bluetoothConnected() {
		changeListView();
		Toast.makeText(getActivity(), "Bluetooth Connected", Toast.LENGTH_SHORT).show();		
		//navigateToDemo();
	}

	private void changeListView() {
		int position = arrayAdapter.getPosition(ReferencesHolder.BLUETOOTH_DEVICE);
		LOG.v("Position = " + position);
		TextView v = (TextView) listDevices.getChildAt(position);
		String text = (String) v.getText();
		text = text.replace("(Paired)", "(Connected)");
		LOG.v(text);
		v.setText(text);
	}

	@Override
	public void receiverMessage(String message) {
		// do nothing
	}
	
	private void navigateToDemo() {
		getFragmentManager().beginTransaction()
		.replace(R.id.bluetooth_container, new DemoBluetoothFragment()).addToBackStack(null)
		.commit();
	}

	@Override
	public void bluetoothDisconnected() {
		Toast.makeText(getActivity(), "Bluetooth Disconnected!", Toast.LENGTH_SHORT)
			.show();
	}	
}
