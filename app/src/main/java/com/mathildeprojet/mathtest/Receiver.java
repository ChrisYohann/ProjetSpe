package com.mathildeprojet.mathtest;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by Rafaelle on 06/06/2015.
 */
public class Receiver {
    DatagramPacket pack;
    DatagramSocket socket;
    InetAddress senderadr;
    Context context;
    private Thread receiverThread;
    private boolean receiveMessages = false;
    protected static final Integer BUFFER_SIZE = 4096;
    protected int MULTICAST_PORT = 8888;
    private String incomingMessage;


    public  Receiver(Context context) throws SocketException {
        Log.d("NOUS", "entrons dans receive");
        this.context = context;
    }

    public String receive() throws IOException {
        Runnable receiver = new Runnable() {

            @Override
            public void run() {
                WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wim != null) {
                    MulticastLock mcLock = wim.createMulticastLock("Recevoir");

                    mcLock.acquire();
                }

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
                MulticastSocket rSocket;

                try {
                    rSocket = new MulticastSocket(MULTICAST_PORT);
                } catch (IOException e) {
                    Log.d("NOUS", "Impossible to create a new MulticastSocket on port " + MULTICAST_PORT);
                    e.printStackTrace();
                    return;
                }

                while (receiveMessages) {
                    try {
                        rSocket.receive(rPacket);
                    } catch (IOException e1) {
                        Log.d("NOUS", "There was a problem receiving the incoming message.");
                        e1.printStackTrace();
                        continue;
                    }

                    if (!receiveMessages)
                        break;

                    byte data[] = rPacket.getData();
                    int i;
                    for (i = 0; i < data.length; i++) {
                        if (data[i] == '\0')
                            break;
                    }


                    try {
                        incomingMessage = new String(data, 0, i, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.d("NOUS", "UTF-8 encoding is not supported. Can't receive the incoming message.");
                        e.printStackTrace();
                        continue;
                    }


                }
            }

        };

        receiveMessages = true;
        if (receiverThread == null)
            receiverThread = new Thread(receiver);

        if (!receiverThread.isAlive())
            receiverThread.start();

        return incomingMessage;
    }
}


