package threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import common.Logger;

import fragments.ConnectingFragment;

import utils.Constants;
import utils.ReferencesHolder;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BluetoothConnectedThread extends Thread {

	private static final Logger LOG = Logger
			.getLogger(BluetoothConnectedThread.class);

	private BluetoothSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Handler handler;

	public static void runNewInstance(BluetoothSocket socket) {
		ReferencesHolder.BLUETOOTH_DEVICE = socket.getRemoteDevice();

		if (ReferencesHolder.BLUETOOTH_CONNECTED_THREAD != null) {
			ReferencesHolder.BLUETOOTH_CONNECTED_THREAD.interrupt();
			ReferencesHolder.BLUETOOTH_CONNECTED_THREAD = null;
		}

		if (ReferencesHolder.BLUETOOTH_CONNECT_THREAD != null) {
			ReferencesHolder.BLUETOOTH_CONNECT_THREAD.interrupt();
			ReferencesHolder.BLUETOOTH_CONNECT_THREAD = null;
		}

		if (ReferencesHolder.BLUETOOTH_ACCEPT_THREAD != null) {
			ReferencesHolder.BLUETOOTH_ACCEPT_THREAD.interrupt();
			ReferencesHolder.BLUETOOTH_ACCEPT_THREAD = null;
		}

		BluetoothConnectedThread thread = new BluetoothConnectedThread();
		thread.socket = socket;
		thread.handler = ReferencesHolder.MAIN_THREAD_HANDLER;
		try {
			thread.inputStream = socket.getInputStream();
			thread.outputStream = socket.getOutputStream();
		} catch (IOException e) {
		}
		ReferencesHolder.BLUETOOTH_CONNECTED_THREAD = thread;
		thread.handler.obtainMessage(Constants.MESSAGE_BLUETOOTH_CONNECTED)
				.sendToTarget();
	}

	public void run() {
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes; // bytes returned from read()
		while (true) {
			try {
				// Read from the InputStream
				bytes = inputStream.read(buffer);
				LOG.v("number of bytes = " + bytes);
				// Send the obtained bytes to the UI activity
				handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
						.sendToTarget();
			} catch (IOException e) {
				LOG.v(e.getMessage());
				LOG.v("Connection was lost");
				connectionLost();
				break;
			}
		}
	}

	private void connectionLost() {
		handler.obtainMessage(Constants.MESSAGE_BLUETOOTH_DISCONNECTED).sendToTarget();
		BluetoothAcceptThread.runNewInstance();
		BluetoothConnectThread.runNewInstance(ReferencesHolder.BLUETOOTH_DEVICE);
	}

	/* Call this to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
		}
	}

	public void cancel() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

}
