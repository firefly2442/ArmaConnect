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

import java.util.HashMap;
import java.util.Map.Entry;

import org.ff.armaconnect.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
		implementations.put( R.id.show_weather, WeatherActivity.class );
		
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
