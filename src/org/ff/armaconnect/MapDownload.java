package org.ff.armaconnect;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;

public class MapDownload implements Runnable {

    public static float progress = 0;
    private static Context c;

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
                    //default location for me: /data/data/org.ff.armaconnect/files
                    File f = new File(c.getFilesDir(), "maps");
                    if (!f.exists()) {
                        f.mkdir();
                        Log.v("MapDownload", "Finished creating root maps folder: " + c.getFilesDir()+"/maps");
                    }

                    String[] files = returnedString.split("\n");
                    for (int i = 0; i < files.length; i++) {
                        files[i] = files[i].replace("\\", "/"); //replaces all occurences
                        if (files[i].endsWith(".png")) {
                            //get file
                            Log.v("MapDownload", "Requesting file: " + files[i]);
                            out.writeBytes(files[i]);
                            out.writeBytes(".GetFile.");
                            out.flush();
                            Log.v("MapDownload", "Finished request for file.");

                            File file_loc = new File(c.getFilesDir()+"/maps"+files[i]);
                            FileOutputStream out_stream = new FileOutputStream (new File(file_loc.getAbsolutePath().toString()), true);
                            int count;
                            String returnedFile = "";
                            byte[] buffer = new byte[32000]; //32 KB
                            while ((count = in.read(buffer)) > 0) {
                                out_stream.write(buffer, 0, count);
                            }
                            out_stream.close();
                            Log.v("MapDownload", "Finished writing file: " + c.getFilesDir()+"/maps"+files[i]);
                        } else {
                            //create folder
                            f = new File(c.getFilesDir(), "/maps"+files[i]);
                            if (!f.exists()) {
                                f.mkdir();
                                Log.v("MapDownload", "Finished creating folder: " + c.getFilesDir()+"/maps"+files[i]);
                            }
                        }
                    }
                    out.close();
                    in.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                    UDP.ipaddress = null;
                }
            }
        }
    }

    public static void initializeMapDownload(Context context) {
        c = context;
    }
}
