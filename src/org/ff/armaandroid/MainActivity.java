package org.ff.armaandroid;

import java.util.HashMap;
import java.util.Map.Entry;

import org.ff.armaandroid.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public static UDP udp;
	public static TCP tcp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//start networking
		if (udp == null)
			udp = new UDP();
		if (tcp == null)
			tcp = new TCP();
		
		SettingsActivity.initializeSettings(getApplicationContext());
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		HashMap<Integer, Class<?>> implementations = new HashMap<Integer, Class<?>>();
		implementations.put( R.id.show_map, MapTileViewActivity.class );
		implementations.put( R.id.show_datetime, DateTimeActivity.class );
		
		for (Entry<Integer, Class<?>> entry : implementations.entrySet()) {
			TextView label = (TextView) findViewById( entry.getKey() );
			label.setTag( entry.getValue() );
			label.setOnClickListener( labelClickListener );
		}
		
		//have a separate listener for the settings button
		TextView settings_button = (TextView) findViewById( R.id.show_settings );
		settings_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent intent = new Intent( MainActivity.this, SettingsActivity.class );
		    	startActivity( intent );
		    }
		});
	}


	private View.OnClickListener labelClickListener = new View.OnClickListener() {
		@Override
		public void onClick( View v ) {
			Intent intent = new Intent( MainActivity.this, ConnectingActivity.class );
			//https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android
			intent.putExtra("launching", getResources().getResourceEntryName(v.getId()));
			startActivity( intent );
		}
	};

}
