package fragments;

import utils.Constants;
import activities.RobotActivity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mdp.R;

public class ConfigurationFragment extends Fragment {
	
	private View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_configuration, container, false);
		
		// Set the values of the current config onto the edit texts
		final EditText edit1 = (EditText) rootView.findViewById(R.id.config_f1_content);
		final EditText edit2 = (EditText) rootView.findViewById(R.id.config_f2_content);
		
		SharedPreferences settings = rootView.getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
		String config1 = settings.getString("config1", "config1");
		String config2 = settings.getString("config2", "config2");
		
		edit1.setText(config1);
		edit2.setText(config2);
		
		
		// Set onclicklistener to "save" and "back" buttons
		Button btnSave = (Button) rootView.findViewById(R.id.config_save);
		Button btnBack = (Button) rootView.findViewById(R.id.config_back);
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveConfig(edit1.getText().toString(), edit2.getText().toString());				
			}
		});
		
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Call MainActivity's method to switch fragment
				((RobotActivity)rootView.getContext()).showDashboard();
			}
		});
		
		return rootView;
	}
	
	/**
	 * Commit the new config values to shared preferences
	 * @param config1 - value for the F1 button
	 * @param config2 - value for the F2 button
	 */
	private void saveConfig(String config1, String config2) {
		
	      // We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = rootView.getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("config1", config1);
	      editor.putString("config2", config2);

	      // Commit the edits!
	      editor.commit();

	      Toast.makeText(rootView.getContext(), "Values saved.", Toast.LENGTH_SHORT).show();

	}

}
