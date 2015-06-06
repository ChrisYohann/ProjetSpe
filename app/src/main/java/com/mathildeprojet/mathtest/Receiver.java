package com.mathildeprojet.mathtest;

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

    public void receive() throws IOException {
        socket.receive(pack);
    }

    public InetAddress getSender() {
        return pack.getAddress();
    }
}
