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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.Random;

public class ConnectingActivity extends Activity implements Runnable {
	
	private Thread connectionThread;
	private boolean mutex;
	private String launching = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);
		
		Log.v("ConnectingActivity", "ConnectingActivity onCreate.");

		String[] tips = getResources().getStringArray(R.array.tips);

		//pick a random tip to use and display
		Random random = new Random();
		int index = random.nextInt(2);
		((TextView) findViewById(R.id.tipText)).setText(tips[index]);
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//get data that was passed in
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    launching = extras.getString("launching");
		}

		if (launching != null && launching.equals("show_map")) {
			((TextView) findViewById(R.id.textHelpMessage)).setText(getString(R.string.datetime_helpmessage_GPS));
		} else if (launching != null && launching.equals("show_datetime")) {
			((TextView) findViewById(R.id.textHelpMessage)).setText(getString(R.string.datetime_helpmessage_Watch));
		}
		
		if (connectionThread == null) {
			//start thread
			connectionThread = new Thread(this);
			connectionThread.start();
			mutex = true;
		}
	}
	
	public void onDestroy() {
		Log.v("ConnectingActivity", "ConnectingActivity Destroy.");
		mutex = false;
		//wait until the thread is done
		try {
			connectionThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public void run() {
		switch (launching) {
			case "show_map":
				waitForMapInformation();
				break;
			case "show_datetime":
				waitForDateTime();
				break;
			case "show_weather":
				waitForWeather();
				break;
		}
	}
	
	private void waitForDateTime() {
		//Log.v("ConnectingActivity", "Waiting for date time information.");
		while (mutex) {
			//check to make sure we've gotten some data first
			if (DateTimeActivity.isDateTimeSet()) {
				//the map has changed or we need to load in for the first time
				Intent intent = new Intent( ConnectingActivity.this, DateTimeActivity.class );
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    	startActivity( intent );
		    	finish(); //this will "destroy" this activity
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	private void waitForMapInformation() {
		//Log.v("ConnectingActivity", "Waiting for map information.");
		while (mutex) {
			//check to make sure we've gotten some data first
			if (MapTileViewActivity.maps.getCurrentMap() != null) {
				//the map has changed or we need to load in for the first time
				Intent intent = new Intent( ConnectingActivity.this, MapTileViewActivity.class );
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    	startActivity( intent );
		    	finish(); //this will "destroy" this activity
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void waitForWeather() {
		//Log.v("ConnectingActivity", "Waiting for weather information.");
		while (mutex) {
			//check to make sure we've gotten some data first
			if (WeatherActivity.isWeatherSet()) {
				Intent intent = new Intent( ConnectingActivity.this, WeatherActivity.class );
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    	startActivity( intent );
		    	finish(); //this will "destroy" this activity
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
