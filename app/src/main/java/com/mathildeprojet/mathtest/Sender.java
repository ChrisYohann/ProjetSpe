package com.mathildeprojet.mathtest;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;

import com.mathildeprojet.mathtest.WifiP2Pconnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Formatter;

import java.net.UnknownHostException;

/**
 * Created by Rafaelle on 27/05/2015.
 */
class Sender implements Runnable {

    Sender(String message, DatagramSocket socket, WifiP2PActivity act, String pseudo) {

        this.act = act;
        this.message = message;
        this.pseudo = pseudo;
        this.socket = socket;
        this.renvoi = true;


    }
    Sender(DatagramSocket socket, WifiP2PActivity act, String pseudo) {

        this.act = act;
        this.message = message;
        this.pseudo = pseudo;
        this.socket = socket;
        this.renvoi = false;

    }

    String message;
    DatagramSocket socket;
    String pseudo;
    WifiP2PActivity act;
    boolean renvoi;


    @Override
    public void run() {

        if (!renvoi) {

            String s = "";


            final EditText editTextSender = (EditText) act.findViewById(R.id.messages);
            try {
                message = pseudo + ": " + editTextSender.getText().toString();

                Log.d("Message", message);
            } catch (Exception e) {
                Log.i("Socket Sender ", e.getMessage());
            }

        }
        else {
            // cette methode échoue, car le device routeur reçoit également le message et le renvoi de façon infinie
            message = "(renvoi de message) " + message;
        }

        try {

            WifiManager wm = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mylock");

            multicastLock.acquire();
            byte[] buf = new byte[256];
            buf = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.49.98"), 8888);
            InetAddress address = InetAddress.getByName("192.168.49.255");
            packet = new DatagramPacket(buf, buf.length, address, 8888);
            // Log.i("Socket Sender", "About to send message" + multisocket.getLocalSocketAddress());
            socket.send(packet);


        } catch (SocketException e1) {
            e1.printStackTrace();
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }


    }
}