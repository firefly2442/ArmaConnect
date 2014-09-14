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

public class Weather {
	
	public float overcast; //0-1
	public float fog; //0-1
	public float rain; //0-1
	public float lightning; //0-1
	public float waves; //0-1
	public float wind_strength; //0-1
	public float wind_gusts; //0-1
	public float wind_speed; //in m/s
	public float wind_direction_degrees; //0-360
	public float humidity; //0-1
	
	public Weather(float o, float f, float r, float l, float w, float wi_strength, float wi_g, float wi_spd, float wi_d, float h)
	{
		//constructor
		overcast = o;
		fog = f;
		rain = r;
		lightning = l;
		waves = w;
		wind_strength = wi_strength;
		wind_gusts = wi_g;
		wind_speed = wi_spd;
		wind_direction_degrees = wi_d;
		humidity = h;
	}
}
