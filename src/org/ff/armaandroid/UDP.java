package org.ff.armaandroid;

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

			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					//Log.v("UDP", "Waiting to receive UDP packet...");
					socket.receive(receivePacket);

					InetAddress IPAddress = receivePacket.getAddress();
					String ipString = IPAddress.toString().replaceAll("/", "");
					
					String received = new String(receivePacket.getData()).trim();
					if (received.equals("Arma2NETAndroidPlugin")) {
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
