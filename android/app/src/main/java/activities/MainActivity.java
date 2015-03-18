package activities;

import threads.BluetoothAcceptThread;
import threads.BluetoothConnectedThread;
import utils.Constants;
import utils.ReferencesHolder;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mdp.R;

import common.CustomHandler;
import common.Logger;

import fragments.ConnectingFragment;
import fragments.DemoBluetoothFragment;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private static final Logger LOG = Logger.getLogger(MainActivity.class);
	private BroadcastReceiver receiver;
	private BluetoothAdapter bluetoothAdapter;
	private ConnectingFragment connectingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ReferencesHolder.MAIN_ACTIVITY = this;
		setContentView(R.layout.main_activity);
		createHandler();
		connectingFragment = new ConnectingFragment();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Log.e("Main activity", "Bluetooth not supported");
			finish();
		}
		ReferencesHolder.BLUETOOTH_ADAPTER = bluetoothAdapter;
		if (!bluetoothAdapter.isEnabled()) {
			turnOnBluetooth();
		} else {
			navigate();
		}
		registerReceiver();
	}

	private void createHandler() {
		ReferencesHolder.MAIN_THREAD_HANDLER = new CustomHandler(
				Looper.getMainLooper());
	}

	private void registerReceiver() {
		// Create a BroadcastReceiver for ACTION_FOUND
		// Register the BroadcastReceiver
		receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					connectingFragment.receiveNewDevice(device);
					// Add the name and address to an array adapter to show in a
					// ListView
					Log.v("Acitivity", device.getName());

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {

				} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
					if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
						turnOnBluetooth();
					}
				} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED
						.equals(action)) {
					// no more bluetooth device
					ReferencesHolder.BLUETOOTH_DEVICE = null;
					startBluetoothServer();
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		registerReceiver(receiver, filter);
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (!bluetoothAdapter.enable()) {
			finish();
		} else {
			navigate();
		}
	}

	private void turnOnBluetooth() {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

	private void navigate() {
		startBluetoothServer();
		getFragmentManager().beginTransaction()
				.add(R.id.bluetooth_container, connectingFragment).commit();
	}

	private void startBluetoothServer() {
		BluetoothAcceptThread.runNewInstance();
	}

}
