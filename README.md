


# Android udpxy
Android library-project wrapper for **udpxy** UDP-to-HTTP Proxy Server.

udpxy is a RTP-to-HTTP proxy server that will convert the RTP/UDP multicast stream to HTTP unicast.

## Download & Usage
Download *udpxy* binary for Android from [Release](https://github.com/ewwink/android-udpxy/releases) page, install `Termux` or any terminal emulator for Android and run the following command

for single network interface

    ./udpxy -p 8888

for multiple network interface

    ./udpxy -vT -a wlan0 -m eth0 -p 8888
    # or
    ./udpxy -vT -a 192.168.1.2 -m 10.70.1.2 -p 8888

For **non-rooted** device you can't execute udpxy on `sdcard` or `internal storage` try to copy it to `/data/local/`  and change permission to executable but if you can't you must use `wget` and download it from server. Open terminal emulator run the following command

    cd $HOME
    wget https://github.com/ewwink/android-udpxy/releases
    # or
    wget http://yourServer/udpxy
    chmod +x udpxy

and then run previous command above.
 
If it’s working you can see status page in your browser

    http://192.168.1.2:8888/status

![udpxy-status](https://user-images.githubusercontent.com/760764/43703419-f2cf42a0-9986-11e8-8768-2a6d0dc109e9.png)

### Play the stream
Open your video player like `VLC`, `GoodPlayer` or `MX Player` and input the stream URL like

    http://192.168.1.2:8888/rtp/239.1.1.159:8928

This will start receiving an RTP multicast stream from `239.1.1.159` on port `8928` (which is ANTV in my setup) and will relay it over HTTP.

## Options

udpxy accepts the following options:

`-v` Enable verbose output [default = disabled].

`-S` Enable client statistics [default = disabled].

`-T` Do NOT run as a daemon [default = daemon if root].

`-a` <listenaddr> IPv4 address/interface to listen on [default = 0.0.0.0].

`-m <mcast_ifc_addr>` IPv4 address/interface of (multicast) source [default = 0.0.0.0].

`-c <clients>` Maximum number of clients to accept [default = 3, max = 5000].

`-l <logfile>` Log output to file [default = stderr].

`-B <sizeK>` Buffer size (65536, 32Kb, 1Mb) for inbound (multicast) data [default = 2048 bytes].

`-R <msgs>` Maximum number of messages to buffer (-1 = all) [default = 1].

`-H <sec>` Maximum time (in seconds) to hold data in a buffer (-1 = unlimited) [default = 1].

`-n <nice_incr>` Nice value increment [default = 0].

`-M <sec>` Renew multicast subscription every M seconds (skip if 0) [default = 0].

`-p <port>` Port to listen on.

## Compile for Android

This will compile udpxy to binary, to create APK please read rom1v blog in the bottom. Download [NDK](https://developer.android.com/ndk/downloads/), extract and add the directory to your system `path`.

    git clone https://github.com/ewwink/android-udpxy.git
    cd android-udpxy/jni/
    ndk-build

The binary will be generated in `libs/`

to compile to another OS just run `make` in `jni/` directory.

---------------------
### credits:
- Android wrapper by [rom1v](http://blog.rom1v.com/2014/03/compiler-un-executable-pour-android)
- Original repo [udpxy](https://github.com/pcherenkov/udpxy)

