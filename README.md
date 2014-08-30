ArmaAndroid
===========

An Android app to connect to Arma through the Arma2NETAndroid Plugin.


### About

This is an Android application that connects through an Arma2NET plugin to Arma.  Development started at [AlphaSquad](http://alphasquad.net/forum/viewtopic.php?f=71&t=3622).  It is a **client-side** mod and plugin
that connects to your Android phone or tablet through a local wireless connection.

### Features

##### Current
* Live map with player location through GPS
* Date and time display from the game

##### Planned/Possible Future Features
* Altitude
* Chat log
* Weather (clouds, rain, fog, etc.)
* Gear/inventory

### Requirements

* Android phone/tablet version 3.0 or higher (I've only tested with 4.2.2).
* Wireless network setup and your phone/tablet connected.
* [@Arma2NET](https://github.com/ScottNZ/Arma2NET) mod compiled and installed.
* [Arma2NETAndroid plugin](https://github.com/firefly2442/Arma2NETAndroid-Plugin) for @Arma2NET installed.
* [@Arma2NETAndroid](https://github.com/firefly2442/ArmaAndroid-mod) mod installed for Arma.
* [ArmaAndroid](https://github.com/firefly2442/ArmaAndroid) this application installed on your Android device.

### Installation

* Enabled third-party apps on your Android device.  This can be found in the settings.
* Install this application to your phone (.apk binary).
* Setup @Arma2NET mod and the Arma2NETAndroid plugin.
* Setup the @Arma2NETAndroid mod.
* Start Arma with the appropriate mods in your startup mod parameters.  For example:
````
-mod=@Arma2NET;@Arma2NETAndroid
````

### Troubleshooting

* Make sure your firewall isn't blocking the connection.
* Check the Arma2NET logs.  These can be found in `Users/username/AppData/Local/Arma2NET/`.
* Check the ArmaAndroid logs.  These can be found in `Users/userame/AppData/Local/Arma2NETAndroid/logs/`.

### License

Arma 3 is a product of [Bohemia Interactive](http://www.bistudio.com).  Map images, markers, and other image assets are copyrighted and are used under the [Arma Public License](http://www.bistudio.com/community/licenses/arma-public-license).  No assets from Arma will be stored on Github.

Please do not release this application under any other name or on the Google Play store.  This application will always remain free under the GPLv3 license.

The Android application uses two third-party libraries.  [TileView](https://github.com/moagrius/TileView) is released under the MIT license.  It in turn uses [DiskLruCache](https://github.com/JakeWharton/DiskLruCache) which is released under the Apache version 2 license.

### Thanks To

* [AlphaSquad](http://alphasquad.net) for help and testing.
