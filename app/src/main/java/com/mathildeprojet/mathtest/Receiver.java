package com.mathildeprojet.mathtest;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Rafaelle on 06/06/2015.
 */
public class Receiver {
    DatagramPacket pack;
    DatagramSocket socket;
    InetAddress senderadr;
    

    public void Receiver() throws SocketException {
        this.socket=new DatagramSocket();
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[256];
        DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
        Log.d("Nous", "taille: " + rPacket.getLength() );
        Log.d("Nous", "socket: " + socket.getPort() );
        socket.receive(pack);
        return new String(pack.getData()) ;
    }

    public InetAddress getSender() {
        return pack.getAddress();
    }
}
