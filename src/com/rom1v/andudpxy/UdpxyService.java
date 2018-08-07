package com.rom1v.andudpxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Manager for the native command {@code udpxy}.
 * 
 * The client (typically a video player) can connect to {@code udpxy} using HTTP on
 * localhost:UDPXY_PORT specifying the multicast address to subscribe (for instance:
 * {@code http://localhost:8379/udp/239.0.0.1:1234/}).
 * 
 * This address can be generated using {@link #proxify(int, String) proxify(udpxyPort,
 * videoAddress)}.
 * 
 * @author rom
 */
public class UdpxyService extends Service {

    private static final String TAG = UdpxyService.class.getSimpleName();

    public static final String EXTRA_PORT = "port";

    /** The HTTP port to be used by the clients. */
    public static final int DEFAULT_PORT = 8379;

    /** Location of the {@code udpxy} binary. */
    private File udpxyBin;

    /** The {@code udpxy} process associated to this service. */
    private Process udpxyProcess;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra(EXTRA_PORT, DEFAULT_PORT);
        startUdpxy(port);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not a bound service
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        udpxyBin = new File(getFilesDir(), "udpxy");
        if (!udpxyBin.exists()) {
            extractUdpxyBinary(udpxyBin);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop udpxy when this service is destroyed, in order to avoid conflicts with future udpxy
        // instances
        stopUdpxy();
    }

    /**
     * Start the {@code udpxy} daemon.
     * 
     * @param port
     *            the listening port
     * @return {@code true} it the command call worked, {@code false} otherwise
     */
    private boolean startUdpxy(int port) {
        stopUdpxy();
        try {
            String[] command = { udpxyBin.getAbsolutePath(), "-p", String.valueOf(port) };
            udpxyProcess = Runtime.getRuntime().exec(command);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Cannot start udpxy", e);
            return false;
        }
    }

    /**
     * Stop the {@code udpxy} process.
     * 
     * Do nothing if there was no active {@code udpxy} process in this service.
     */
    private void stopUdpxy() {
        if (udpxyProcess != null) {
            udpxyProcess.destroy();
            udpxyProcess = null;
        }
    }

    /**
     * The binary file {@code udpxy} is included in assets. This method extracts it to the
     * file-system.
     * 
     * @param target
     *            the target location
     */
    private void extractUdpxyBinary(File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getResources().openRawResource(R.raw.udpxy);
            out = new FileOutputStream(target);
            // copy from R.raw.udpxy to /data/data/<package>/files/udpxy
            FileUtils.copy(in, out);
            // make the file executable
            FileUtils.chmod(target, 0755);
        } catch (IOException e) {
            Log.e(TAG, "Cannot copy udpxy", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // don't care
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // don't care
                }
            }
        }
    }

    /**
     * Send an intent starting {@code udpxy}.
     * 
     * @param context
     *            the context
     * @param port
     *            the listening port of {@code udpxy} ({@code 0} for the default port)
     */
    public static void startUdpxy(Context context, int port) {
        Intent intent = new Intent(context, UdpxyService.class);
        if (port != 0) {
            intent.putExtra(EXTRA_PORT, port);
        }
        context.startService(intent);
    }

    /**
     * Like {@link #startUdpxy(Context, int) startUdpxy(context, 0)}.
     * 
     * @param context
     *            the context
     */
    public static void startUdpxy(Context context) {
        startUdpxy(context, 0);
    }

    /**
     * Send an intent stopping {@code udpxy}.
     * 
     * @param context
     *            the context
     */
    public static void stopUdpxy(Context context) {
        Intent intent = new Intent(context, UdpxyService.class);
        context.stopService(intent);
    }

    /**
     * Wrap a channel address ({@code ip:port}) to a {@code udpxy} url (
     * {@code http://udpxy_ip:udpxy_port/udp/ip:port/}).
     * 
     * The input {@code channelAddress} is wrapped unchanged, and is not validated.
     * 
     * @param udpxyPort
     *            the listening port of {@code udpxy} ({@code 0} for the default port)
     * @param videoAddress
     *            the video address to wrap
     * @return the {@code udpxy} url
     */
    public static String proxify(int udpxyPort, String videoAddress) {
        if (udpxyPort == 0) {
            udpxyPort = DEFAULT_PORT;
        }
        return "http://localhost:" + udpxyPort + "/udp/" + videoAddress + "/";
    }

    /**
     * Like {@link #proxify(int,String) proxify(0, videoAddress)}.
     * 
     * @param videoAddress
     *            the video address to wrap
     * @return the {@code udpxy} url
     */
    public static String proxify(String videoAddress) {
        return proxify(0, videoAddress);
    }

}
