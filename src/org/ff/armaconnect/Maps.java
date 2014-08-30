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

import java.util.ArrayList;

public class Maps {

	private ArrayList<Map> available_maps = new ArrayList<Map>();
	private int current_map = -1;
	private long last_update;
	
	public Maps()
	{
		//constructor
		//map name and x,y dimension
		available_maps.add(new Map("Stratis", 8192, 8192));
		current_map = 0;
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
	
	public boolean setPlayerPosition(String mapname, float x, float y, float rot)
	{
		for (int i = 0; i < available_maps.size(); i++) {
			if (available_maps.get(i).name.equals(mapname)) {
				available_maps.get(i).player_x = x;
				available_maps.get(i).player_y = y;
				available_maps.get(i).player_rotation = rot;
				current_map = i;
				last_update = System.currentTimeMillis()/1000; //epoch time
				return true;
			}
		}
		return false;
	}
}
