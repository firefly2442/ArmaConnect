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
