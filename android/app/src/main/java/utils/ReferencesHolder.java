package utils;

import threads.BluetoothAcceptThread;
import threads.BluetoothConnectThread;
import threads.BluetoothConnectedThread;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import common.CustomHandler;

public class ReferencesHolder {
	public static BluetoothAdapter BLUETOOTH_ADAPTER;
	public static Activity MAIN_ACTIVITY;
	public static CustomHandler MAIN_THREAD_HANDLER;
	public static BluetoothConnectedThread BLUETOOTH_CONNECTED_THREAD;
	public static BluetoothAcceptThread BLUETOOTH_ACCEPT_THREAD;
	public static BluetoothConnectThread BLUETOOTH_CONNECT_THREAD;
	public static BluetoothDevice BLUETOOTH_DEVICE;
	private ReferencesHolder() {
		// prevent creating instance
	}
}
