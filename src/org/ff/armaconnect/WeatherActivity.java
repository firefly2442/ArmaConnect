package org.ff.armaconnect;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class WeatherActivity extends FragmentActivity {
	
	//https://developer.android.com/reference/android/support/v4/app/FragmentTabHost.html
    private FragmentTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayout);

        tabHost.addTab(tabHost.newTabSpec("Current Weather").setIndicator("Current Weather", null), WeatherFragmentTab.class, null);
        tabHost.addTab(tabHost.newTabSpec("Forecast").setIndicator("Forecast", null), WeatherFragmentTab.class, null);
    }
}