package org.ff.armaconnect;


import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;

public class MapDownload implements Runnable {

    public MapDownload()
    {
        Log.v("MapDownload", "Starting constructor.");
        //constructor
        Thread listenerThread = new Thread(this);
        listenerThread.start();
    }

    public void run()
    {
        //noinspection InfiniteLoopStatement
        while (true)
        {
            if (UDP.ipaddress != null)
            {
                try {
                    Log.v("MapDownload", "Connecting to TCP IP: " + UDP.ipaddress);
                    Socket socket = new Socket(UDP.ipaddress, 65043);
                    Log.v("MapDownload", "Finished socket connection.");

                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                    //all message passing uses UTF-8 format
                    out.writeBytes("Get map file listing.");
                    out.writeBytes(".GetMapFiles.");
                    out.flush();
                    Log.v("MapDownload", "Finished request for map file listing.");

                    byte[] returned = new byte[32000]; //32 KB
                    String returnedString = "";
                    while ((in.read(returned)) != -1) {
                        String converted = new String(returned, "UTF-8").trim();
                        returnedString = returnedString + converted;
                        if (returnedString.contains(".GetMapFiles."))
                            break;
                    }

                    returnedString = returnedString.replace(".GetMapFiles.", "");
                    Log.v("MapDownload", "From plugin: " + returnedString);

                    //Create folders if necessary or download the file
                    String[] files = returnedString.split("\n");
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].endsWith(".png")) {

                        } else {
                            //create folder
                            File mydir = Context.getDir("mydir", Context.MODE_PRIVATE); //Creating an internal dir;
                            File fileWithinMyDir = new File(mydir, "myfile"); //Getting a file within the dir.
                            FileOutputStream out = new FileOutputStream(fileWithinMyDir);
                        }
                    }



                } catch (IOException e) {
                    //e.printStackTrace();
                    UDP.ipaddress = null;
                }
            }
        }
    }
}
