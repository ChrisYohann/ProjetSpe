package com.example.rafaelle.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import java.nio.channels.Channel;

/**
 * Created by Rafaelle on 21/05/2015.
 */
public class WifiP2Pconnection extends BroadcastReceiver implements  WifiP2pManager.ActionListener, WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener,
        WifiP2pManager.PeerListListener {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiP2PActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    private IntentFilter mIntentFilter = new IntentFilter();


    public WifiP2Pconnection(Context ctxt, WifiP2pManager manager, Looper looper, Channel channel,
                             WifiP2PActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

        mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        //il me dise de créer une méthode getsystemservice dans context
        mChannel = (Channel) mManager.initialize(ctxt, looper, null);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers((WifiP2pManager.Channel) mChannel, myPeerListListener);
            }
        }

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int reason) {

    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {

    }

}

