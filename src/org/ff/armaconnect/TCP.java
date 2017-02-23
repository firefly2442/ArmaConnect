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
					
	                //TODO: check to make sure this is working
	                //keep TCP socket connection open for as long as possible
	                while (true) {
		                //TODO: look to see if we have any data that needs to be sent to Arma
		                //map markers, etc.

		                //all message passing uses UTF-8 format
		                out.writeBytes("This is from java.");
		                out.writeBytes(".Arma2NETConnectEnd.");
		                out.flush();
		                //Log.v("TCP", "Finished writing and flushing.");

		                byte[] returned = new byte[16384]; //16 KB (corresponds to callExtension limit in Arma)
		                //Log.v("TCP", "Started read.");
		                int bytesReceived;
		                String returnedString = "";
		                while ((in.read(returned)) != -1) {
		                	String converted = new String(returned, "UTF-8").trim();
		                	returnedString = returnedString + converted;
		                	//Log.v("TCP", "Finished with one read.");
		                	if (returnedString.contains(".Arma2NETConnectEnd."))
		                		break;
		                }
		                if (!returnedString.contains(".Arma2NETConnectEnd."))
		                	Log.v("TCP", "Unable to find end of message, this will probably result in a parsing error later.");
		                returnedString = returnedString.replace(".Arma2NETConnectEnd.", "");

		                //parse the data and send it on to the appropriate data structure
		                if (!returnedString.equals("")) {
		                	Log.v("TCP", "From Arma: " + returnedString);
		                	ParseData.parseData(returnedString);
		                }

		                //sleep for a little bit so we don't hammer out messages
		                try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }

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
