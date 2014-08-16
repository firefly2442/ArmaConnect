package org.ff.armaandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class TCP implements Runnable {

	public TCP()
	{
		//constructor
		Thread listenerThread = new Thread(this);
		listenerThread.start();
	}
	
	public void run()
	{
		while (true)
		{
			if (UDP.ipaddress != null)
			{
				try {
					//http://systembash.com/content/a-simple-java-tcp-server-and-tcp-client/
					
					//TODO: look into keeping the TCP connection open instead of having to close it down
					//and re-open every single time.
					
					//Log.v("TCP", "Connecting to TCP IP: " + UDP.ipaddress);
					Socket socket = new Socket(UDP.ipaddress, 65042);
					//Log.v("TCP", "Finished socket connection.");
					
					DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					
	                //TODO: look to see if we have any data that needs to be sent to Arma
	                //map markers, etc.
	                
	                //all message passing uses UTF-8 format
	                out.writeBytes("This is from java.");
	                out.writeBytes(".Arma2NETAndroidEnd.");
	                out.flush();
	                //Log.v("TCP", "Finished writing and flushing.");
	                
	                byte[] returned = new byte[16384]; //16 KB (corresponds to callExtension limit in Arma)
	                //Log.v("TCP", "Started read.");
	                int bytesReceived;
	                String returnedString = "";
	                while ((bytesReceived = in.read(returned)) != -1) {
	                	String converted = new String(returned, "UTF-8").trim();
	                	returnedString = returnedString + converted;
	                	//Log.v("TCP", "Finished with one read.");
	                	if (returnedString.contains(".Arma2NETAndroidEnd."))
	                		break;
	                }
	                returnedString = returnedString.replace(".Arma2NETAndroidEnd.", "");
	                
	                //parse the data and send it on to the appropriate data structure
	                if (!returnedString.equals("")) {
	                	Log.v("TCP", "From Arma: " + returnedString);
	                	ParseData.parseData(returnedString);
	                }
	                
	                //sleep for a little bit so we don't hammer out messages
	                try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					socket.close();
				} catch (SocketException e1) {
					//e1.printStackTrace();
					UDP.ipaddress = null;
				} catch (UnknownHostException e) {
					//e.printStackTrace();
					UDP.ipaddress = null;
				} catch (IOException e) {
					//e.printStackTrace();
					UDP.ipaddress = null;
				}
			}
		}
	}
}
