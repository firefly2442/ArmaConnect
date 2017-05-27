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

import java.util.Calendar;

import android.util.Log;

public class ParseData {

	public static void parseData(String data)
	{
		//Log.v("ParseData", "Data: " + data);
		String[] split = data.split(",");
		//Log.v("ParseData", "Split: " + Arrays.toString(split));
		
		//account for when we have multiple messages in one stream of data
		int i = 0;
		while (i < split.length) {
			switch (split[i]) {
				case "player":
					if (!MainActivity.maps.setPlayerPosition(split[i + 1], Float.parseFloat(split[i + 2]), Float.parseFloat(split[i + 3]), Float.parseFloat(split[i + 4]), Boolean.parseBoolean(split[i + 5])))
						Log.v("ParseData", "Unable to set the player position.");
					i = i + 6;
					break;
				case "datetime":
					//returned milliseconds is not used
					//Java calendars start with 0 for January, thus the subtraction
					Calendar gc = Calendar.getInstance();
					gc.set(Integer.parseInt(split[i + 1]), Integer.parseInt(split[i + 2]) - 1, Integer.parseInt(split[i + 3]), Integer.parseInt(split[i + 4]), Integer.parseInt(split[i + 5]), Integer.parseInt(split[i + 6]));
					DateTimeActivity.updateDateTime(gc);
					i = i + 8;
					break;
				case "weather":
					Weather w = new Weather(Float.parseFloat(split[i + 1]), Float.parseFloat(split[i + 3]), Float.parseFloat(split[i + 5]), Float.parseFloat(split[i + 7]), Float.parseFloat(split[i + 9]), Float.parseFloat(split[i + 11]), Float.parseFloat(split[i + 13]), Float.parseFloat(split[i + 15]), Float.parseFloat(split[i + 17]), Float.parseFloat(split[i + 19]));
					Weather f = new Weather(Float.parseFloat(split[i + 2]), Float.parseFloat(split[i + 4]), Float.parseFloat(split[i + 6]), Float.parseFloat(split[i + 8]), Float.parseFloat(split[i + 10]), Float.parseFloat(split[i + 12]), Float.parseFloat(split[i + 14]), Float.parseFloat(split[i + 16]), Float.parseFloat(split[i + 18]), Float.parseFloat(split[i + 20]));
					WeatherActivity.updateWeather(w, f);
					i = i + 21;
					break;
				default:
					i++;
					break;
			}
		}
	}
}
