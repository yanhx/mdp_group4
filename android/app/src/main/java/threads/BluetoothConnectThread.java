package threads;

import java.io.IOException;

import common.Logger;

import utils.Constants;
import utils.ReferencesHolder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothConnectThread extends Thread {
	private static Logger LOG = Logger.getLogger(BluetoothConnectThread.class);
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private Handler handler;

	public static void runNewInstance(BluetoothDevice device) {
		if (ReferencesHolder.BLUETOOTH_CONNECT_THREAD != null) {
			ReferencesHolder.BLUETOOTH_CONNECT_THREAD.interrupt();;
			ReferencesHolder.BLUETOOTH_CONNECT_THREAD = null;
		}
		BluetoothConnectThread thread = new BluetoothConnectThread();
		thread.handler = ReferencesHolder.MAIN_THREAD_HANDLER;
		BluetoothSocket tmp = null;
		thread.device = device;
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MDP_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(Constants.MDP_UUID);
		} catch (IOException e) {
			Log.v("BluetoothConnectThread", "Cannot create RfcommSocket ...");
		}
		thread.socket = tmp;
		ReferencesHolder.BLUETOOTH_CONNECT_THREAD = thread;
		thread.start();
	}


	public void run() {
		// Cancel discovery because it will slow down the connection
		ReferencesHolder.BLUETOOTH_ADAPTER.cancelDiscovery();
		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			if (!socket.isConnected()) {
				Log.e("BluetoothConnectThread", "Socket is not connected!");
				socket.connect();
			}
			BluetoothConnectedThread.runNewInstance(socket);
		} catch (IOException connectException) {
			Log.e("BluetoothConnectThread", "IOException occurred!");
			Log.e("BluetoothConnectThread", connectException.getMessage());
			// Unable to connect; close the socket and get out
			try {
				socket.close();
			} catch (IOException closeException) {
			}
		}
	}
}