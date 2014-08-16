package org.ff.armaandroid;

public class Map {
	
	public final String name; //name, Stratis, Altis, etc.
	//map dimensions
	public final int x;
	public final int y;
	//player position
	public float player_x;
	public float player_y;
	//player orientation/rotation
	public float player_rotation;
	
	public Map(String name, int x, int y)
	{
		//constructor
		this.name = name;
		this.x = x;
		this.y = y;
	}
}
