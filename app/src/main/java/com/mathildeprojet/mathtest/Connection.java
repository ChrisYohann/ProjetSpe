package com.mathildeprojet.mathtest;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import android.net.wifi.p2p.WifiP2pManager.Channel;

import com.mathildeprojet.mathtest.WifiP2Pconnection;

/**
 * Created by Rafaelle on 27/05/2015.
 */
public class Connection {
    Channel mChannel = null;
    Context cntxt = null;
    WifiP2pDevice dev = null;
    WifiP2pManager mManager = null;
    WifiP2Pconnection mConMan=null;
    String peerIP;
    String peerMAC;
    String myIP;
    String myMAC;
    int groupowner = 0; //pour determiner s'il envoie ou s'il reçcoit
    int childport;
    int sendport;
    int recvport;

    public Connection(Context ctx, Channel mChannel, WifiP2pManager mManager, WifiP2Pconnection cmgr, WifiP2pDevice device) {
        cntxt = ctx;
        mChannel = mChannel;
        mManager = mManager;
        mConMan = cmgr;
        dev = device;
    }


    public void setDevice(WifiP2pDevice d){ dev = d; }

    public void setMyInfo(String mac, String ip){
        this.myMAC = mac;
        this.myIP = ip;
    }

    public void setPeerInfo( String mac, String ip, int cp, int go){
        this.groupowner = go;
        this.peerMAC = mac;
        this.peerIP = ip;

        this.childport = cp;
        this.sendport = (this.groupowner==1) ? this.childport : this.childport+1;
        this.recvport = (this.groupowner==1) ? this.childport+1 : this.childport;
    }
}
