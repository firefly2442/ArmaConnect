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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapDownload implements Runnable {

    public static float progress = 0; //0-1
    private static Context c;
    public static boolean finished;

    public MapDownload()
    {
        Log.v("MapDownload", "Starting constructor.");
        //constructor
        finished = false;
        (new Thread(this)).start();
    }

    public void run()
    {
        //noinspection InfiniteLoopStatement
        while (true)
        {
            if (UDP.ipaddress != null && finished == false)
            {
                try {
                    Log.v("MapDownload", "Connecting to TCP IP: " + UDP.ipaddress);
                    Socket socket = new Socket(UDP.ipaddress, 65043);
                    Log.v("MapDownload", "Finished socket connection.");

                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                    out.writeBytes(".GetListSize.");
                    out.flush();

                    byte[] returned = new byte[32000]; //32 KB
                    String returnedSize = "";
                    while ((in.read(returned)) > 0) {
                        String converted = new String(returned, "UTF-8").trim();
                        returnedSize = returnedSize + converted;
                        if (returnedSize.contains(".GetListSize.")) {
                            break;
                        }
                    }
                    returnedSize = returnedSize.replace(".GetListSize.", ""); //in string characters length
                    Log.v("MapDownload", "Returned file list size: " + returnedSize);

                    //all message passing uses UTF-8 format
                    out.writeBytes("Get map file listing.");
                    out.writeBytes(".GetMapFiles.");
                    out.flush();
                    //Log.v("MapDownload", "Finished request for map file listing.");

                    //https://stackoverflow.com/questions/19839172/how-to-read-all-of-inputstream-in-server-socket-java
                    byte[] customReturned = new byte[32000]; //32 KB
                    String returnedString = "";
                    boolean endReading = false;
                    int bytesRead = 0;
                    while (!endReading) {
                        bytesRead = in.read(customReturned);
                        returnedString += new String(customReturned, 0, bytesRead);
                        if (returnedString.length() == Integer.parseInt(returnedSize)) {
                            endReading = true;
                        }
                    }
                    //Log.v("MapDownload", "Returned string size: " + returnedString.length());
                    //Log.v("MapDownload", "From plugin (truncated message size): " + returnedString);

                    //Create folders if necessary or download the file
                    //default location for me: /data/data/org.ff.armaconnect/files
                    File f = new File(c.getFilesDir(), "maps");
                    if (!f.exists()) {
                        f.mkdir();
                        //Log.v("MapDownload", "Finished creating root maps folder: " + c.getFilesDir()+"/maps");
                    }

                    String[] files = returnedString.split("\n");
                    for (int i = 0; i < files.length; i++) {
                        //Log.v("MapDownload", "Raw file value: " + files[i]);
                        progress = (float)i / files.length;
                        //Log.v("MapDownload", "Raw progress value: " + i + " " + files.length);
                        files[i] = files[i].replace("\\", "/"); //replaces all occurences
                        if (files[i].endsWith(".png")) {
                            //get file
                            String[] size_location = files[i].split("\t");
                            int toWriteSize = Integer.parseInt(size_location[0]);
                            Log.v("MapDownload", "Requesting file: " + size_location[1] + " with size: " + toWriteSize);
                            out.writeBytes(size_location[1]);
                            out.writeBytes(".GetFile.");
                            out.flush();
                            //Log.v("MapDownload", "Finished request for file.");

                            int count = 0;
                            File file_loc = new File(c.getFilesDir()+"/maps"+size_location[1]);
                            FileOutputStream out_stream = new FileOutputStream (new File(file_loc.getAbsolutePath().toString()), false);
                            String returnedFile = "";
                            byte[] buffer = new byte[32000]; //32 KB
                            while ((count = in.read(buffer)) > 0) {
                                out_stream.write(buffer, 0, count);
                                //Log.v("MapDownload", "Wrote bytes: " + count);
                                toWriteSize = toWriteSize - count;
                                if (toWriteSize <= 0)
                                    break;
                            }
                            out_stream.close();
                            //Log.v("MapDownload", "Finished writing file: " + file_loc.getPath());
                        } else {
                            //create folder
                            f = new File(c.getFilesDir(), "/maps"+files[i]);
                            if (!f.exists()) {
                                f.mkdir();
                                //Log.v("MapDownload", "Finished creating folder: " + f.getPath());
                            }
                        }
                    }
                    out.writeBytes(".Shutdown.");
                    out.flush();

                    out.close();
                    in.close();
                    socket.close();
                    finished = true;
                    break; //out of forever while loop
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void initializeMapDownload(Context context) {
        c = context;
    }
}
