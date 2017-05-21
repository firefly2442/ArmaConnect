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
            //Log.v("MapDownloadActivity", "Setting progress: " + Math.round(md.progress * 100));
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
                ((Button) findViewById(R.id.downloadFinishButton)).setEnabled(true);
            }
        });
    }
}
