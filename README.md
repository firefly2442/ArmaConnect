Arma Connect
===========

An [Android](http://www.android.com/)(&trade;) app to connect to Arma through the Arma2NETConnect Plugin.


### About

This is an Android(&trade;) application that connects through an Arma2NET plugin to Arma.  Development started at [AlphaSquad](http://alphasquad.net/forum/viewtopic.php?f=71&t=3622).  It is a **client-side** mod and plugin
that connects to your Android phone or tablet through a local wireless connection.  For more details,
see the [BIS forum thread](http://forums.bistudio.com/showthread.php?183223).

### Features

##### Current
* Live map with player location through GPS
* Date and time display from the game
* Weather (clouds, rain, fog, etc.) (forecast needs more commands from BIS)

##### Planned/Possible Future Features
* Altitude
* Chat log
* Gear/inventory
* Task listing

### Requirements

* Android phone/tablet version 3.0 or higher (I've only tested with 4.2.2).
* Wireless network setup and your phone/tablet connected.
* [@Arma2NET](https://github.com/ScottNZ/Arma2NET) mod compiled and installed.
* [Arma2NETConnect plugin](https://github.com/firefly2442/Arma2NETConnectPlugin) for @Arma2NET installed.
  * [Visual Studio 2013 Redistributable x86 (32bit version)](http://www.microsoft.com/en-us/download/details.aspx?id=40784)?
  * [Microsoft .NET Framework 4.5](http://www.microsoft.com/en-us/download/details.aspx?id=30653)
* [@Arma2NETConnect](https://github.com/firefly2442/ArmaConnect-mod) mod installed for Arma.
* [ArmaConnect](https://github.com/firefly2442/ArmaConnect) this application installed on your Android device.

### Installation

* Uninstall any previously installed version of the application if applicable.
* Enable installation of third-party apps on your Android device.  This can be found in Settings -> Security.  Then check allow from "Unknown Sources".  [See details here](https://developer.android.com/distribute/tools/open-distribution.html).  This needs to be done because the app is not in Google Play.
* Install this application to your Android device (.apk binary).
* Setup @Arma2NET mod and the Arma2NETConnect plugin.
* Setup the @Arma2NETConnect mod.
* Start Arma with the appropriate mods in your startup mod parameters.  For example:
````
-mod=@Arma2NET;@Arma2NETConnect
````

### Troubleshooting

* Make sure your firewall isn't blocking the connection.  Allow UDP port 65041 and TCP port 65042. Most programs should prompt you at the beginning when it tries to make a connection. All network connections should be on the same subnet and will not leave your local network.
* Check the Arma2NET logs.  These can be found in `Users/username/AppData/Local/Arma2NET/`.
* Check the ArmaConnect logs.  These can be found in `Users/userame/AppData/Local/Arma2NETConnect/logs/`.
* If you found a bug, please create a ticket on the appropriate Github project page.

### License

Arma 3 is a product of [Bohemia Interactive](http://www.bistudio.com).  Map images, markers, and other image assets are copyrighted and are used under the [Arma Public License](http://www.bistudio.com/community/licenses/arma-public-license).  No assets from Arma will be stored on Github.

Please do not release this application under any other name or on the Google Play(&trade;) store.  This application will always remain free under the GPLv3 license.

Android is a trademark of Google Inc.  Google Play is a trademark of Google Inc.

The Android application uses two third-party libraries.  [TileView](https://github.com/moagrius/TileView) is released under the MIT license.  It in turn uses [DiskLruCache](https://github.com/JakeWharton/DiskLruCache) which is released under the Apache version 2 license.

Some images are part of the [Tango Icon Library](http://tango.freedesktop.org/Tango_Icon_Library) which is released under Public Domain.

### For Developers

Opening the Android project:

* Use [Android Studio](https://developer.android.com/studio/index.html) and import via Gradle

The overview of events for networking between the Arma plugin and Android device is the following:

* Plugin sends out UDP message on port 65041. This is similar to a heartbeat message since we don't know what IP address the Android device is running on.
* Plugin sets up a TCP thread on port 65042 to listen for incoming Android connections and data.
* Android device receives UDP message on port 65041 and remembers the IP address of the Arma computer.
* Android device sends TCP connection request on port 65042.
* At this point, the handshake is complete and all data transfer is done between the two using TCP.
* If there is a network failure or one system dies, the connection can be re-established via the UDP heartbeat and the above steps.


### Thanks To

* [AlphaSquad](http://alphasquad.net) for help and testing.
* Robalo for help with the Arma mod and scripting.
* Hatchet for help with images.
