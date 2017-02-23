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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDP implements Runnable {
	
	public static String ipaddress;

	public UDP() {
		// Constructor
		Thread listenerThread = new Thread(this);
		listenerThread.start();
	}

	public void run() {
		//Log.v("UDP", "Started listening for UDP packets...");

		DatagramSocket socket;
		try {
			socket = new DatagramSocket(65041);
			byte[] receiveData = new byte[1024];

			//noinspection InfiniteLoopStatement
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					//Log.v("UDP", "Waiting to receive UDP packet...");
					socket.receive(receivePacket);

					InetAddress IPAddress = receivePacket.getAddress();
					String ipString = IPAddress.toString().replaceAll("/", "");
					
					String received = new String(receivePacket.getData()).trim();
					if (received.equals("Arma2NETConnectPlugin")) {
						ipaddress = ipString;
					}

					//Log.v("UDP", "IPAddress: " + ipString + " - Received: " + received);

				} catch (IOException e) {
					//e.printStackTrace();
					ipaddress = null;
				}
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}		
	}
}
