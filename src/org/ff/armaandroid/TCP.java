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
					
					Log.v("TCP", "Connecting to TCP IP: " + UDP.ipaddress);
					Socket socket = new Socket(UDP.ipaddress, 65042);
					Log.v("TCP", "Finished socket connection.");
					
					DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
	                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					
	                //all message passing uses UTF-8 format, TODO: does this need to be checked?
	                out.writeBytes("This is from java.");
	                out.flush();
	                
	                byte[] returned = new byte[16384]; //16 KB (corresponds to callExtension limit in Arma)
	                in.read(returned);
	                
	                String converted = new String(returned).trim();
	                Log.v("TCP", "From Arma: " + converted);
	                
	                try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					socket.close();
				} catch (SocketException e1) {
					e1.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
