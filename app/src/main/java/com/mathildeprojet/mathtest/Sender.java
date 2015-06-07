package com.mathildeprojet.mathtest;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Parcelable;
import android.util.Log;

import com.mathildeprojet.mathtest.WifiP2Pconnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import 	java.net.NetworkInterface;
import java.util.Enumeration;
import 	java.util.Formatter;

import java.net.UnknownHostException;

/**
 * Created by Rafaelle on 27/05/2015.
 */
public class Sender {
    private String message;
    private DatagramSocket socket;
    private Context context;
    protected int MULTICAST_PORT=8888;



    public Sender(String message, Context context) throws SocketException {
        Log.v("Nous", "j'entre dans sender");
        this.message = message;
        socket = new DatagramSocket();
        this.context = context;
    }

    public void send() throws IOException {

        //on crée la socket
        if(socket == null) {
            try {
                socket = new DatagramSocket(MULTICAST_PORT);
            } catch (SocketException e) {
                Log.d("NOUS", "Problème lors de la création de la socket");
                e.printStackTrace();

            }
        }

        DatagramPacket packet;

        byte data[] = message.getBytes();

        try {
            packet = new DatagramPacket(data, data.length, InetAddress.getByName(ipToString(getLocalIpAddress(), true)), MULTICAST_PORT);
            socket.send(packet);
        } catch (UnknownHostException e) {
            Log.d("NOUS", "It seems that " + ipToString(getLocalIpAddress(), true) + " is not a valid ip! Aborting.");
            e.printStackTrace();

        }


        // InetAddress moi= socket.getLocalAddress();

       // Log.v("Nous", "mon message " + message + " adresse multicast " + adr + "mon ip" + getLocalIpAddress());

      //  socket.send(new DatagramPacket(data, data.length, adr, 8888));

    }

    public int getLocalIpAddress() {
        WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ip = wim.getConnectionInfo().getIpAddress();
        return ip;


    }

    public static String ipToString(int ip, boolean broadcast) {
        String result = new String();

        Integer[] address = new Integer[4];
        for(int i = 0; i < 4; i++)
            address[i] = (ip >> 8*i) & 0xFF;
        for(int i = 0; i < 4; i++) {
            if(i != 3)
                result = result.concat(address[i]+".");
            else result = result.concat("255.");
        }
        return result.substring(0, result.length() - 2);
    }
}