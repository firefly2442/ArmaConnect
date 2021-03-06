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

import com.qozix.tileview.markers.MarkerLayout;
import com.qozix.tileview.widgets.ZoomPanLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class MapTileViewActivity extends TileViewActivity implements Runnable {

	// player marker
	private ImageView player;
	private boolean mutex;
	private Thread mapThread;
	private boolean followPlayer = false;

	@SuppressLint("DefaultLocale")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (SettingsActivity.keepScreenOn())
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Map current_map = MainActivity.maps.getCurrentMap();

		Log.v("MapTileViewActivity", "MapTileView onCreate.");

		// size of original image at 100% scale
		getTileView().setSize(current_map.x, current_map.y);

		// detail levels
		getTileView().addDetailLevel(1.000f, getApplicationContext().getFilesDir()+"/maps/" + current_map.name.toLowerCase() + "/1000/%d_%d.png");
		getTileView().addDetailLevel(0.500f, getApplicationContext().getFilesDir()+"/maps/" + current_map.name.toLowerCase() + "/500/%d_%d.png");
		getTileView().addDetailLevel(0.250f, getApplicationContext().getFilesDir()+"/maps/" + current_map.name.toLowerCase() + "/250/%d_%d.png");
		getTileView().addDetailLevel(0.125f, getApplicationContext().getFilesDir()+"/maps/" + current_map.name.toLowerCase() + "/125/%d_%d.png");

		// allow scaling past original size
		getTileView().setScaleLimits(0, 4);

		// we're running from local internal files, should be fairly fast decodes, go ahead and render asap
		getTileView().setShouldRenderWhilePanning(true);

		// let's center all markers both horizontally and vertically
		getTileView().setMarkerAnchorPoints(-0.5f, -0.5f);

		player = placeMarker(R.drawable.player_icon, 0, 0);

		// frame to the player
		frameTo(current_map.player_x, (current_map.y - current_map.player_y));

		//add event listener if user taps on player marker
		getTileView().setMarkerTapListener(playerMarkerEventListener);

		//add event listener to see if the user is dragging
		//getTileView().addZoomPanListener(dragListener);

		// sets scale (zoom level)
		getTileView().setScale(0.5f);

		//add event listener to see if the user is dragging
		getTileView().addZoomPanListener(new ZoomPanLayout.ZoomPanListener() {
			@Override
			public void onPanBegin(int x, int y, Origination origin) {
				followPlayer = false;
			}

			@Override
			public void onPanUpdate(int x, int y, Origination origin) {

			}

			@Override
			public void onPanEnd(int x, int y, Origination origin) {

			}

			@Override
			public void onZoomBegin(float scale, Origination origin) {

			}

			@Override
			public void onZoomUpdate(float scale, Origination origin) {

			}

			@Override
			public void onZoomEnd(float scale, Origination origin) {

			}
		});

		if (mapThread == null) {
			mapThread = new Thread(this);
			mapThread.start();
			mutex = true;
		}
	}

	private ImageView placeMarker(int resId, double x, double y) {
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(resId);
		getTileView().addMarker(imageView, x, y, null, null);
		return imageView;
	}

	private void updatePlayerMarker(Map cur_map) {

		final Map current_map = cur_map;
		final ImageView player = this.player;

		getTileView().post(new Runnable() {
			@Override
			public void run() {
				//Log.v("MapTileViewActivity", "Player info: " + current_map.player_x + ", " + current_map.player_y + ", " + current_map.player_rotation + ", " + current_map.vehicle);
				//update existing information

				//make sure it's been initialized and we actually have data
				if (current_map.player_x != 0.0f || current_map.player_y != 0.0f) {
					//the two systems have a different origin 0,0 position, thus the subtraction
					getTileView().moveMarker(player, current_map.player_x, (current_map.y - current_map.player_y));
				}

				//set whether we use the normal icon or vehicle icon
				if (!current_map.vehicle) {
					player.setImageResource(R.drawable.player_icon);
				} else {
					player.setImageResource(R.drawable.player_vehicle_icon);
				}

				player.setRotation(current_map.player_rotation);

				if (followPlayer) {
					getTileView().scrollToAndCenter(Math.round(player.getX()), Math.round(player.getY()));
				}
			}
		});
	}

	private final MarkerLayout.MarkerTapListener playerMarkerEventListener = new MarkerLayout.MarkerTapListener() {
		@Override
		public void onMarkerTap(View v, int x, int y) {
			followPlayer = true;
			getTileView().scrollToAndCenter(x, y);
			Toast.makeText(getApplicationContext(), "Following player", Toast.LENGTH_SHORT).show();
		}
	};

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
				if (MainActivity.maps.getCurrentMap() != null)
					updatePlayerMarker(MainActivity.maps.getCurrentMap());
			}
			
			//TODO: check if the map changed or we got disconnected (more than 8 seconds without data)
			//if so, go back to "connecting" activity
			if (UDP.ipaddress == null || (System.currentTimeMillis()/1000 - MainActivity.maps.getLastUpdateEpoch()) >= 8) {
				Log.v("MapTileViewActivity", "Disconnected, running reset.");
				MainActivity.maps.resetMap();
				Intent intent = new Intent( MapTileViewActivity.this, ConnectingActivity.class );
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("launching", "show_map");
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
