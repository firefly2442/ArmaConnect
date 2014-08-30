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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class MapTileViewActivity extends TileViewActivity implements Runnable {
	
	public static Maps maps = new Maps();
	// player marker
	private View player;
	private boolean mutex;
	private Thread mapThread;

	@SuppressLint("DefaultLocale")
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		
		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Map current_map = maps.getCurrentMap();
		
		// size of original image at 100% scale
		getTileView().setSize( current_map.x, current_map.y );

		// detail levels
		getTileView().addDetailLevel( 1.000f, "tiles/"+current_map.name.toLowerCase()+"/1000/%col%_%row%.png", "samples/"+current_map.name.toLowerCase()+".png");
		getTileView().addDetailLevel( 0.500f, "tiles/"+current_map.name.toLowerCase()+"/500/%col%_%row%.png", "samples/"+current_map.name.toLowerCase()+".png");
		getTileView().addDetailLevel( 0.250f, "tiles/"+current_map.name.toLowerCase()+"/250/%col%_%row%.png", "samples/"+current_map.name.toLowerCase()+".png");
		getTileView().addDetailLevel( 0.125f, "tiles/"+current_map.name.toLowerCase()+"/125/%col%_%row%.png", "samples/"+current_map.name.toLowerCase()+".png");

		// allow scaling past original size
		getTileView().setScaleLimits( 0, 4 );

		// lets center all markers both horizontally and vertically
		getTileView().setMarkerAnchorPoints( -0.5f, -0.5f );

		player = placeMarker( R.drawable.player_icon, 0, 0 );

		// frame to the player
		frameTo( current_map.player_x, (current_map.y-current_map.player_y) );

		// sets scale (zoom level)
		getTileView().setScale( 0.5 );
		
		mapThread = new Thread(this);
		mapThread.start();
		mutex = true;
	}
	
	private View placeMarker( int resId, double x, double y ) {
		ImageView imageView = new ImageView( this );
		imageView.setImageResource( resId );
		getTileView().addMarker( imageView, x, y );
		return imageView;
	}
	
	private void updatePlayerMarker(Map cur_map) {
		
		final Map current_map = cur_map;

		getTileView().post( new Runnable() {
			@Override
			public void run() {
				//Log.v("MapTileViewActivity", "Player info: " + current_map.player_x + ", " + current_map.player_y + ", " + current_map.player_rotation);
				//update existing information
				
				//the two systems have a different origin 0,0 position, thus the subtraction
				getTileView().moveMarker(player, current_map.player_x, (current_map.y-current_map.player_y));
				
				player.setRotation(current_map.player_rotation);
			}
		});
	}
	
	public void onDestroy() {
		Log.v("MapTileViewActivity", "MapTileView Destroy.");
		mutex = false;
		//wait until the thread is done
		try {
			mapThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	public void run() {
		Log.v("MapTileViewActivity", "Getting player data.");
		while (mutex) {
			if (player != null) {
				//update player position
				if (maps.getCurrentMap() != null)
					updatePlayerMarker(maps.getCurrentMap());
			}
			
			//TODO: check if the map changed or we got disconnected (more than 8 seconds without data)
			//if so, go back to "connecting" activity
			if (UDP.ipaddress == null || (System.currentTimeMillis()/1000 - maps.getLastUpdateEpoch()) >= 8) {
				maps.resetMap();
				Intent intent = new Intent( MapTileViewActivity.this, ConnectingActivity.class );
				intent.putExtra("launching", "show_map");
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
