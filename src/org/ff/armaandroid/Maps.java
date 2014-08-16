package org.ff.armaandroid;

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
