package threads;

import java.io.IOException;

import utils.Constants;
import utils.ReferencesHolder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BluetoothAcceptThread extends Thread {
  private BluetoothServerSocket mmServerSocket;
  private Handler handler;
  
	public static void runNewInstance() {
		if (ReferencesHolder.BLUETOOTH_ACCEPT_THREAD != null) {
			ReferencesHolder.BLUETOOTH_ACCEPT_THREAD.cancel();
			ReferencesHolder.BLUETOOTH_ACCEPT_THREAD = null;
		}
		BluetoothAcceptThread thread = new BluetoothAcceptThread();
		thread.handler = ReferencesHolder.MAIN_THREAD_HANDLER;
	    BluetoothServerSocket tmp = null;
	    try {
	        tmp = ReferencesHolder.BLUETOOTH_ADAPTER
	        		.listenUsingRfcommWithServiceRecord("Bluetooth", Constants.MDP_UUID);
	    } catch (IOException e) { }
	    thread.mmServerSocket = tmp;
		ReferencesHolder.BLUETOOTH_ACCEPT_THREAD = thread;
		thread.start();
	}
 
  public void run() {
    BluetoothSocket socket = null;
    // Keep listening until exception occurs or a socket is returned
    while (true) {
      try {
          socket = mmServerSocket.accept();
      } catch (IOException e) {
          break;
      }
      // If a connection was accepted
      if (socket != null) {
        // Do work to manage the connection (in a separate thread)
        BluetoothConnectedThread.runNewInstance(socket);
        Log.v("BluetoothAcceptThread", "accepted a socket now!");
        try {
			mmServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        break;
      }
    }
  }
 
	/** Will cancel the listening socket, and cause the thread to finish */
  public void cancel() {
    try {
        mmServerSocket.close();
    } catch (IOException e) { }
  }
}