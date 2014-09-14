package org.ff.armaconnect;



import java.text.DecimalFormat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherActivity extends FragmentActivity implements Runnable {
	
	//https://developer.android.com/reference/android/support/v4/app/FragmentTabHost.html
    private FragmentTabHost tabHost;
    
    private static Weather weather;
    private static Weather weather_forecast;
	private static boolean weather_set = false;
	private boolean mutex;
	private Thread weatherThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
        if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //https://stackoverflow.com/questions/5069614/tabwidget-overlaps-with-my-activity-content
        //https://stackoverflow.com/questions/22124124/tabs-are-on-top-of-my-text-in-my-xml
        //https://stackoverflow.com/questions/19831773/android-tabs-content-overlapping
        
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

        tabHost.addTab(tabHost.newTabSpec(getResources().getString(R.string.weather_Current)).setIndicator(getResources().getString(R.string.weather_Current), null), WeatherFragmentTab.class, null);
        tabHost.addTab(tabHost.newTabSpec(getResources().getString(R.string.weather_Forecast)).setIndicator(getResources().getString(R.string.weather_Forecast), null), WeatherFragmentTab.class, null);
        
        weatherThread = new Thread(this);
		weatherThread.start();
		mutex = true;
    }
    
    public void onDestroy() {
		Log.v("WeatherActivity", "WeatherActivity Destroy.");
		mutex = false;
		//wait until the thread is done
		try {
			weatherThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		weather_set = false;
		super.onDestroy();
	}
    
    private void updateWeatherUI(Weather w) {
    	
    	final Weather weather = w;
    	
		this.findViewById(android.R.id.content).post( new Runnable() {
			@Override
			public void run() {
				ImageView imageWeather = (ImageView) findViewById(R.id.imageWeather);
				TextView textWeather = (TextView) findViewById(R.id.textWeather);
				ImageView imageWind = (ImageView) findViewById(R.id.imageWind);
				TextView textWind = (TextView) findViewById(R.id.textWind);
				TextView textHumidity = (TextView) findViewById(R.id.textHumidity);
				TextView textFog = (TextView) findViewById(R.id.textFog);
				TextView textWaves = (TextView) findViewById(R.id.textWaves);
				
				if (weather.overcast > 0.3 && weather.lightning > 0) {
					imageWeather.setImageResource(R.drawable.weather_storm);
					textWeather.setText(getResources().getString(R.string.weather_Storms));
				} else if (weather.overcast > 0.3 && weather.rain > 0) {
					imageWeather.setImageResource(R.drawable.weather_showers);
					textWeather.setText(getResources().getString(R.string.weather_Raining));
				} else if (weather.overcast > 0.5) {
					imageWeather.setImageResource(R.drawable.weather_overcast);
					textWeather.setText(getResources().getString(R.string.weather_Cloudy));
				} else if (weather.overcast > 0.15) {
					imageWeather.setImageResource(R.drawable.weather_few_clouds);
					textWeather.setText(getResources().getString(R.string.weather_PatchyClouds));
				} else {
					imageWeather.setImageResource(R.drawable.weather_clear);
					textWeather.setText(getResources().getString(R.string.weather_Sunny));
				}
				
				imageWind.setRotation(weather.wind_direction_degrees);
				DecimalFormat df = new DecimalFormat("#.#");
				String wind = df.format(weather.wind_speed) + " m/s";
				if (weather.wind_strength == 0) {
					wind = wind + " - calm";
				} else if (weather.wind_strength < 0.3) {
					wind = wind + " - low strength";
				} else if (weather.wind_strength < 0.7) {
					wind = wind + " - moderately strong";
				} else {
					wind = wind + " - extremely strong";
				}
				textWind.setText(wind);
				
				textHumidity.setText((Math.round(weather.humidity*100)) + "%");
				
				//TODO: convert Arma 0-1 value to a visibility distance (in meters)
				if (weather.fog == 0) {
					textFog.setText("Clear visibility");
				} else if (weather.fog < 0.3) {
					textFog.setText("Mostly clear visibility");
				} else if (weather.fog < 0.7) {
					textFog.setText("Restricted visibility");
				} else {
					textFog.setText("Very low visibility");
				}
				
				//TODO: can we convert Arma 0-1 value to a meter height?
				//then we could have a proper definition
				//https://en.wikipedia.org/wiki/Sea_state
				if (weather.waves == 0) {
					textWaves.setText("Calm seas");
				} else if (weather.waves < 0.3) {
					textWaves.setText("Mostly calm seas");
				} else if (weather.waves < 0.7) {
					textWaves.setText("Moderately choppy seas");
				} else {
					textWaves.setText("Extreme swells");
				}
			}
		});
	}
    
    public static void updateWeather(Weather w, Weather f) {
		weather = w;
		weather_forecast = f;
		weather_set = true;
	}
	
	public static boolean isWeatherSet() {
		return weather_set;
	}

	@Override
	public void run() {
		int last_update = 0;
		Weather previous_weather = null;
		while (mutex) {
			if (previous_weather == weather) {
				last_update++;
			} else {
				//update UI display of weather information
				if (tabHost.getCurrentTabTag().equals(getResources().getString(R.string.weather_Current))) {
					updateWeatherUI(weather);
				} else {
					updateWeatherUI(weather_forecast);
				}
				
				previous_weather = weather;
				last_update = 0;
			}
			
			if (last_update >= 8) {
				//we haven't received any new information in awhile, go back to connecting page
				Intent intent = new Intent( WeatherActivity.this, ConnectingActivity.class );
				intent.putExtra("launching", "show_weather");
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