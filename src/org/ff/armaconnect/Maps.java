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

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Maps {

	private ArrayList<Map> available_maps = new ArrayList<>();
	private int current_map = -1;
	private long last_update;
	
	public Maps() {
		//constructor
	}

	private void addMap(String mapname, int x, int y, float dimension) {
		//https://community.bistudio.com/wiki/BIS_fnc_mapSize
		//Use this to get the map size via SQF, for example:
		//"Altis" call BIS_fnc_mapSize

		//Stratis, 8192, 8192, 1.0     -    8192 x 8192 original size
		//Altis, 11520, 11520, 0.375   -    30720 x 30720 original size
		//Tanoa, 1928, 1928, 0.125520833    -     15360 x 15360 original size

		//map name, x and y size, and dimension scaling
		available_maps.add(new Map(mapname, x, y, dimension));
		current_map = 0;
	}

	public void loadMapsFromFile(Context c) {
		//read internal storage and load maps/maps.txt
		//this stores the names and dimensions of the maps
		String line;
		try {
			InputStream fis = new FileInputStream(c.getFilesDir()+"/maps/maps.txt");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			while ((line = br.readLine()) != null) {
				String split[] = line.split(",");
				addMap(split[0].trim(), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()), Float.parseFloat(split[3].trim()));
				Log.v("Maps", "Loading map: " + split[0].trim()+" "+Integer.parseInt(split[1].trim())+" "+Integer.parseInt(split[2].trim())+" "+Float.parseFloat(split[3].trim()));
			}
		} catch (Exception e) {
			Log.e("Maps", "Error loading maps/maps.txt: " + e);
		}
	}
	
	public Map getCurrentMap() {
		if (current_map != -1) {
			return available_maps.get(current_map);
		} else {
			return null;
		}
	}
	
	public void resetMap() {
		current_map = -1;
	}
	
	public long getLastUpdateEpoch() {
		return last_update;
	}
	
	public boolean setPlayerPosition(String mapname, float x, float y, float rot, boolean v)
	{
		for (int i = 0; i < available_maps.size(); i++) {
			if (available_maps.get(i).name.equals(mapname)) {
				available_maps.get(i).player_x = x * available_maps.get(i).scale;
				available_maps.get(i).player_y = y * available_maps.get(i).scale;
				available_maps.get(i).player_rotation = rot;
				available_maps.get(i).vehicle = v;
				current_map = i;
				last_update = System.currentTimeMillis()/1000; //epoch time
				return true;
			}
		}
		return false;
	}
}
