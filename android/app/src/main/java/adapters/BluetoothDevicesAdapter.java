package adapters;

import utils.ReferencesHolder;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDevicesAdapter extends ArrayAdapter<BluetoothDevice> {

	public BluetoothDevicesAdapter() {
		super(ReferencesHolder.MAIN_ACTIVITY, android.R.layout.simple_list_item_1);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView result;
		if (convertView == null || !(convertView instanceof TextView)) {
			result = new TextView(parent.getContext());
		} else {
			result = (TextView) convertView;
		}
		
		BluetoothDevice connected = ReferencesHolder.BLUETOOTH_DEVICE;
		BluetoothDevice device = getItem(position);
		result.setTextSize(25);
		if (device.equals(connected)) {
			result.setText(device.getName() + "(Connected)" + "\n" + device.getAddress());
		} else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
			result.setText(device.getName() + " (Paired)" + "\n" + device.getAddress());
		} else {
			result.setText(device.getName() + "\n" + device.getAddress());
		}
		return result;
	}
}
