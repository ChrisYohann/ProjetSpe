package com.mathildeprojet.mathtest;

/**
 * Created by matylde on 28/05/2015.
 */
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * Created by Rafaelle on 27/05/2015.
 */
public class Connection {
    Channel mChannel = null;
    Context cntxt = null;
    WifiP2pDevice dev = null;
    WifiP2pManager mManager = null;
    WifiP2Pconnection mConMan=null;

    public Connection(Context ctx, Channel mChannel, WifiP2pManager mManager, WifiP2Pconnection cmgr, WifiP2pDevice device) {
        cntxt = ctx;
        mChannel = mChannel;
        mManager = mManager;
        mConMan = cmgr;
        dev = device;
    }


    public void setDevice(WifiP2pDevice d){ dev = d; }
}

