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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class AltimeterActivity extends Activity implements Runnable {

    private static Altimeter altimeter;
    private static boolean altimeter_set = false;
    private boolean mutex;
    private Thread altimeterThread;

    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altimeter);

        if (SettingsActivity.keepScreenOn())
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (altimeterThread == null) {
            altimeterThread = new Thread(this);
            altimeterThread.start();
            mutex = true;
        }
    }

    public void onDestroy() {
        Log.v("AltimeterActivity", "AltimeterActivity Destroy.");
        mutex = false;
        //wait until the thread is done
        try {
            altimeterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        altimeter_set = false;
        super.onDestroy();
    }

    private void updateAltimeterUI() {

        this.findViewById(android.R.id.content).post( new Runnable() {
            @Override
            public void run() {
                TextView textTerrainHeight = (TextView) findViewById(R.id.textTerrainHeight);
                TextView textSeabedDepth = (TextView) findViewById(R.id.textSeabedDepth);
                TextView textHeightAboveASL = (TextView) findViewById(R.id.textHeightAboveASL);
                TextView textHeightBelowASL = (TextView) findViewById(R.id.textHeightBelowASL);

                if (SettingsActivity.metricUnits()) {
                    if (altimeter.TerrainHeight > 0) {
                        textTerrainHeight.setVisibility(View.VISIBLE);
                        textTerrainHeight.setText(getResources().getString(R.string.altimeter_terrain_height) + "\n" + String.format("%.3f", altimeter.TerrainHeight) + " m");
                        textSeabedDepth.setVisibility(View.GONE);
                    } else {
                        textSeabedDepth.setVisibility(View.VISIBLE);
                        textSeabedDepth.setText(getResources().getString(R.string.altimeter_seabed_depth) + "\n" + String.format("%.3f", altimeter.TerrainHeight) + " m");
                        textTerrainHeight.setVisibility(View.GONE);
                    }
                    if (altimeter.Height > 0) {
                        textHeightAboveASL.setVisibility(View.VISIBLE);
                        textHeightAboveASL.setText(getResources().getString(R.string.altimeter_height_ASL) +"\n"+ String.format("%.3f", altimeter.Height) + " m");
                        textHeightBelowASL.setVisibility(View.GONE);
                    } else {
                        textHeightBelowASL.setVisibility(View.VISIBLE);
                        textHeightBelowASL.setText(getResources().getString(R.string.altimeter_height_BSL) +"\n"+ String.format("%.3f", altimeter.Height) + " m");
                        textHeightAboveASL.setVisibility(View.GONE);
                    }
                } else {
                    if (altimeter.TerrainHeight > 0) {
                        textTerrainHeight.setVisibility(View.VISIBLE);
                        textTerrainHeight.setText(getResources().getString(R.string.altimeter_terrain_height) + "\n" + String.format("%.3f", altimeter.TerrainHeight*3.280839895) + " ft");
                        textSeabedDepth.setVisibility(View.GONE);
                    } else {
                        textSeabedDepth.setVisibility(View.VISIBLE);
                        textSeabedDepth.setText(getResources().getString(R.string.altimeter_seabed_depth) + "\n" + String.format("%.3f", altimeter.TerrainHeight*3.280839895) + " ft");
                        textTerrainHeight.setVisibility(View.GONE);
                    }
                    if (altimeter.Height > 0) {
                        textHeightAboveASL.setVisibility(View.VISIBLE);
                        textHeightAboveASL.setText(getResources().getString(R.string.altimeter_height_ASL) +"\n"+ String.format("%.3f", altimeter.Height*3.280839895) + " ft");
                        textHeightBelowASL.setVisibility(View.GONE);
                    } else {
                        textHeightBelowASL.setVisibility(View.VISIBLE);
                        textHeightBelowASL.setText(getResources().getString(R.string.altimeter_height_BSL) +"\n"+ String.format("%.3f", altimeter.Height*3.280839895) + " ft");
                        textHeightAboveASL.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public static void updateAltimeter(Altimeter a) {
        altimeter = a;
        altimeter_set = true;
    }

    public static boolean isAltimeterSet() {
        return altimeter_set;
    }

    @Override
    public void run() {
        int last_update = 0;
        Altimeter previous_alt = null;
        while (mutex) {
            //update UI display of date and time
            updateAltimeterUI();
            if (previous_alt == altimeter) {
                last_update++;
            } else {
                previous_alt = altimeter;
                last_update = 0;
            }

            if (last_update >= 8) {
                //we haven't received any new information in awhile, go back to connecting page
                Intent intent = new Intent( AltimeterActivity.this, ConnectingActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("launching", "show_altimeter");
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
