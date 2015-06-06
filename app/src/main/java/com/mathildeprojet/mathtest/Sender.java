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


    public Sender(String message) throws SocketException {
        Log.v("Nous", "j'entre dans sender");
        this.message=message;
        socket=new DatagramSocket();
    }

    public void send() throws IOException {

        InetAddress adr = InetAddress.getByName("224.0.0.1");
       // InetAddress moi= socket.getLocalAddress();
        byte[] data = new byte[message.length()];
        data=message.getBytes();
        Log.v("Nous", "mon message "+message+ " adresse multicast "+adr  + "mon ip" + getLocalIpAddress());

        socket.send(new DatagramPacket(data,data.length,adr,8888));

    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()  && inetAddress instanceof Inet4Address) {
                        String ip =  inetAddress.getHostAddress();

                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("NOUS", ex.toString());
        }
        return null;
    }


}