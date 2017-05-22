/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package org.ff.armaconnect;

import org.ff.armaconnect.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends Activity {
	
	//https://developer.android.com/guide/topics/data/data-storage.html
	private final static String PREFS_NAME = "ArmaConnectPreferences";
	private static SharedPreferences settings;

	private static boolean keepScreenOn;
	private static boolean metricUnits;

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		if (keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//setup UI
		final CheckBox screenCheckBox = (CheckBox)findViewById(R.id.screenOnCheckBox);
        screenCheckBox.setChecked(keepScreenOn());
		final CheckBox metricCheckBox = (CheckBox)findViewById(R.id.metricUnitsCheckBox);
		metricCheckBox.setChecked(metricUnits());

        screenCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("keepScreenOn", isChecked);
					editor.apply();
					showSaveMessage();
					updateSettings();
				}
		});
		metricCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean("metricUnits", isChecked);
					editor.apply();
					showSaveMessage();
					updateSettings();
				}
 		     	});
		final Button clearMapsButton = (Button) findViewById(R.id.clearMapsButton);
		File f = new File(getApplicationContext().getFilesDir(), "maps");
		if (!f.exists()) {
			clearMapsButton.setEnabled(false);
		}
		clearMapsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			File f = new File(getApplicationContext().getFilesDir(), "maps");
			deleteRecursive(f);
			Log.v("Settings", "Deleted maps folder.");
			clearMapsButton.setEnabled(false);

			final TextView restartMessage = new TextView(SettingsActivity.this);
			restartMessage.setText(R.string.restart_message);
			restartMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
			restartMessage.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
																			WindowManager.LayoutParams.WRAP_CONTENT));
			final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutSettings);
			linearLayout.addView(restartMessage);
			}
		});
	}

	private void deleteRecursive(File fileOrDirectory) {
		try {
			if (fileOrDirectory.isDirectory())
				for (File child : fileOrDirectory.listFiles())
					deleteRecursive(child);

			fileOrDirectory.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initializeSettings(Context context) {
		if (settings == null)
			settings = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		updateSettings();
	}

	private static void updateSettings() {
		keepScreenOn = settings.getBoolean("keepScreenOn", true);
		metricUnits = settings.getBoolean("metricUnits", true);
	}
	
	private void showSaveMessage() {
		//https://developer.android.com/guide/topics/ui/notifiers/toasts.html
		Toast msg = Toast.makeText(this, "Preferences Saved", Toast.LENGTH_SHORT);
		msg.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
		msg.show();
	}
	
	public static boolean keepScreenOn() {
		return keepScreenOn;
	}
	public static boolean metricUnits() {
		return metricUnits;
	}
}
