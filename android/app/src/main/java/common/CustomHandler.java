package common;

import java.util.ArrayList;
import java.util.List;

import listeners.SocketListener;
import utils.Constants;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class CustomHandler extends Handler {
	private List<SocketListener> listeners;
	
	public CustomHandler(Looper looper) {
		super(looper);
	}
	
	public void attach(SocketListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<SocketListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void detach(SocketListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<SocketListener>();
		}
		listeners.remove(listener);		
	}
	
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case (Constants.MESSAGE_BLUETOOTH_CONNECTED):
				notifyConnected();
				break;
			case (Constants.MESSAGE_READ):
				byte[]buffer = (byte[])msg.obj;
				String s = "";
				for (int i = 0; i < msg.arg1; i++) {
					s += (char)buffer[i];
				}
				notifyMessage(s);
				break;
			case (Constants.MESSAGE_BLUETOOTH_DISCONNECTED):
				notifyDisconnected();
		}
	}

	private void notifyDisconnected() {
		for (SocketListener listener : listeners) {
			listener.bluetoothDisconnected();
		}			
	}

	private void notifyMessage(String msg) {
		for (SocketListener listener : listeners) {
			listener.receiverMessage(msg);
		}		
	}

	private void notifyConnected() {
		for (SocketListener listener : listeners) {
			listener.bluetoothConnected();
		}
	}

}
