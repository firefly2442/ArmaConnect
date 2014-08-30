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

package org.ff.armaandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class ConnectingActivity extends Activity implements Runnable {
	
	private Thread connectionThread;
	private boolean mutex;
	private String launching = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//add blinking animation to text message
		TextView message = (TextView) findViewById(R.id.connectingTextView);

		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(2000);
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		
		message.startAnimation(anim);
		
		//get data that was passed in
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    launching = extras.getString("launching");
		}

		if (launching.equals("show_map")) {
			((TextView) findViewById(R.id.textHelpMessage)).setText(getString(R.string.datetime_helpmessage_GPS));
		} else if (launching.equals("show_datetime")) {
			((TextView) findViewById(R.id.textHelpMessage)).setText(getString(R.string.datetime_helpmessage_Watch));
		}
		
		//start thread
		connectionThread = new Thread(this);
		connectionThread.start();
		mutex = true;
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
		if (launching.equals("show_map")) {
			waitForMapInformation();
		} else if (launching.equals("show_datetime")) {
			waitForDateTime();
		}
	}
	
	private void waitForDateTime() {
		//Log.v("ConnectingActivity", "Waiting for date time information.");
		while (mutex) {
			//check to make sure we've gotten some data first
			if (DateTimeActivity.isDateTimeSet()) {
				//the map has changed or we need to load in for the first time
				Intent intent = new Intent( ConnectingActivity.this, DateTimeActivity.class );
		    	startActivity( intent );
		    	finish(); //this will "destroy" this activity
			}
			
			try {
				Thread.sleep(1000);
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
		    	startActivity( intent );
		    	finish(); //this will "destroy" this activity
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
