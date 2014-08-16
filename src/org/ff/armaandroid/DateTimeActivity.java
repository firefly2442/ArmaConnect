package org.ff.armaandroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class DateTimeActivity extends Activity implements Runnable {
	
	private static Calendar datetime;
	private static boolean date_set = false;
	private boolean mutex;
	private Thread datetimeThread;

	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datetime);
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		datetimeThread = new Thread(this);
		datetimeThread.start();
		mutex = true;		
	}
	
	public void onDestroy() {
		Log.v("DateTimeActivity", "DateTimeActivity Destroy.");
		mutex = false;
		//wait until the thread is done
		try {
			datetimeThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		date_set = false;
		super.onDestroy();
	}
	
	private void updateDateTime() {
		
		this.findViewById(android.R.id.content).post( new Runnable() {
			@Override
			public void run() {
				//http://javatechniques.com/blog/dateformat-and-simpledateformat-examples/
				TextView textDateValue = (TextView) findViewById(R.id.textDateValue);
				SimpleDateFormat date_format = new SimpleDateFormat("MMM d, yyyy");
			    textDateValue.setText(date_format.format(datetime.getTime()));
			    
			    TextView textTimeValue = (TextView) findViewById(R.id.textTimeValue);
				date_format = new SimpleDateFormat("HH:mm::ss aa");
				textTimeValue.setText(date_format.format(datetime.getTime()));
			}
		});
	}
	
	public static void updateDateTime(Calendar gc) {
		//http://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html
		datetime = gc;
		date_set = true;
	}
	
	public static boolean isDateTimeSet() {
		return date_set;
	}

	@Override
	public void run() {
		int last_update = 0;
		Calendar previous_cal = null;
		while (mutex) {
			//update UI display of date and time
			updateDateTime();
			if (previous_cal == datetime) {
				last_update++;
			} else {
				previous_cal = datetime;
				last_update = 0;
			}
			
			if (last_update >= 8) {
				//we haven't received any new information in awhile, go back to connecting page
				Intent intent = new Intent( DateTimeActivity.this, ConnectingActivity.class );
				intent.putExtra("launching", "show_datetime");
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
