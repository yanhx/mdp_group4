package fragments;

import listeners.SocketListener;
import threads.BluetoothConnectedThread;
import utils.ReferencesHolder;
import activities.RobotActivity;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mdp.R;

import common.Logger;

public class DemoBluetoothFragment extends Fragment implements SocketListener {
	private static final Logger LOG = Logger
			.getLogger(DemoBluetoothFragment.class);

	private BluetoothConnectedThread thread;
	private ArrayAdapter<String> chatListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.demo_bluetooth_fragment,
				container, false);
		ListView listView = (ListView) view
				.findViewById(R.id.incoming_text_fields);
		chatListAdapter = new ArrayAdapter<String>(container.getContext(),
				android.R.layout.simple_list_item_1);
		listView.setAdapter(chatListAdapter);

		final EditText editText = (EditText) view
				.findViewById(R.id.entering_field);
		Button button = (Button) view.findViewById(R.id.send_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("Button", "Sending message");
				String text = editText.getText().toString();
				if (text.length() < 1) {
					return;
				}
				LOG.v(text);
				thread.write(text.getBytes());
				chatListAdapter.add(text);
				editText.setText("");
			}
		});
		ReferencesHolder.MAIN_THREAD_HANDLER.attach(this);
		// ensure view has been inflated before start listening
		thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		thread.start();
		return view;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.demo_bluetooth_fragment_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.action_robot_activity):
			Intent intent = new Intent(getActivity(), RobotActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void bluetoothConnected() {
		thread = ReferencesHolder.BLUETOOTH_CONNECTED_THREAD;
		if (!thread.isAlive()) {
			thread.start();
		}
		Toast.makeText(getActivity(), "Bluetooth Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ReferencesHolder.MAIN_THREAD_HANDLER.detach(this);
	}

	@Override
	public void receiverMessage(String message) {
		chatListAdapter.add(message);
		LOG.v("Message received = " + message);
	}

	@Override
	public void bluetoothDisconnected() {
		Toast.makeText(getActivity(), "Bluetooth Disconnected!", Toast.LENGTH_SHORT)
		.show();
	}
}
