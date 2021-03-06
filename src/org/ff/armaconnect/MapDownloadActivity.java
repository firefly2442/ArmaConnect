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

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static java.lang.Math.round;

public class MapDownloadActivity extends Activity implements Runnable {

    private Thread mapActivityThread;
    private MapDownload md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SettingsActivity.keepScreenOn())
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        md = new MapDownload();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_download);
        ((ProgressBar) findViewById(R.id.progressMapDLBar)).setProgress(0);

        //start thread
        mapActivityThread = new Thread(this);
        mapActivityThread.start();

        final Button finishButton = (Button) findViewById(R.id.downloadFinishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //load up our new maps settings/dimensions from maps/maps.txt
                MainActivity.maps.loadMapsFromFile(getApplicationContext());

                Intent intent = new Intent( MapDownloadActivity.this, MainActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity( intent );
                finish(); //this will "destroy" this activity
            }
        });
    }

    @Override
    public void onBackPressed() {
        //disable back button
    }

    public void onDestroy() {
        Log.v("MapDownloadActivity", "MapDownloadActivity Destroy.");
        super.onDestroy();
    }

    public void run() {
        while (md.finished == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ProgressBar) findViewById(R.id.progressMapDLBar)).setProgress((int)Math.floor(md.progress * 100));
                    ((TextView) findViewById(R.id.progressTextOverlay)).setText(((ProgressBar) findViewById(R.id.progressMapDLBar)).getProgress() + "%");
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //turn on the button so the user can leave
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) findViewById(R.id.progressMapDLBar)).setProgress(100);
                ((TextView) findViewById(R.id.progressTextOverlay)).setText("100%");
                ((Button) findViewById(R.id.downloadFinishButton)).setEnabled(true);
            }
        });
    }
}
