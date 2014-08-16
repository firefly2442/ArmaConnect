package org.ff.armaandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	//https://developer.android.com/guide/topics/data/data-storage.html
	private final static String PREFS_NAME = "ArmaAndroidPreferences";
	private static SharedPreferences settings;

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		
		
		if (keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//setup UI
		CheckBox checkBox = (CheckBox)findViewById(R.id.screenOnCheckBox);
        checkBox.setChecked(keepScreenOn());
        
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("keepScreenOn", isChecked);
				editor.commit();
				showSaveMessage();
			}
      	});
	}
	
	public static void initializeSettings(Context context) {
		settings = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	}
	
	private void showSaveMessage() {
		//https://developer.android.com/guide/topics/ui/notifiers/toasts.html
		Toast msg = Toast.makeText(this, "Preferences Saved", Toast.LENGTH_SHORT);
		msg.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
		msg.show();
	}
	
	public static boolean keepScreenOn() {
		//SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getBoolean("keepScreenOn", true);
	}
}
