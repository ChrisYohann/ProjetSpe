package com.example.rafaelle.wifip2p;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import java.nio.channels.Channel;
import java.util.Iterator;

/**
 * Created by Rafaelle on 21/05/2015.
 */
public class WifiP2Pconnection extends BroadcastReceiver implements  WifiP2pManager.ActionListener, WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener,
        WifiP2pManager.PeerListListener {

    private WifiP2pManager mManager;
    private Channel mChannel; //on suppose que le channel est la connection entre 2 appareils
    private WifiP2PActivity mActivity;
    private Context ctx;
    private Boolean discoveryOn=false;
    AlertDialog.Builder adbldr;
    private WifiP2pManager.PeerListListener myPeerListListener;
    private IntentFilter mIntentFilter = new IntentFilter();
    private WifiP2pDevice device;
    private WifiP2pConfig config = new WifiP2pConfig();
    String deviceAddress;


    public WifiP2Pconnection(Context ctxt, WifiP2pManager manager, Looper looper,
                             WifiP2PActivity activity) {
        super();
        Log.v("Dans WifiP2pCo", "on rentre bien dans WifiP2PCo");
        ctx=ctxt;

        this.mManager = manager;
        this.mActivity = activity; //pour relier � l'activit� principale
        adbldr = new AlertDialog.Builder(ctx);
        //mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        //j'appelle directement cette m�thode dans activity
        this.mChannel = (Channel) mManager.initialize(ctxt, looper, null);
        //j'initialise la connection

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.v("Dans onreceive", "on rentre bien dans OnReceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.v("Dans onReceive", "l'etat de la wifi est changé");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v("Dans onReceive", "l'etat de la wifi est ok");

                // Wifi P2P is enabled
            } else {            Log.v("Dans onReceive", "l'etat de la wifi est pas ok");

                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.v("Dans onReceive", "on a de nouveau pairs à rechercher");

            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.v("Dans onReceive", "repondre à une nouvelle co ou se deco");

            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.v("Dans onReceive", "dire que notre etat de co change");

            // Respond to this device's wifi state changing
        }

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                Log.v("Dans onReceive", "on cherche de nouveau pairs");

                mManager.requestPeers((WifiP2pManager.Channel) mChannel, myPeerListListener);
            }
        }

    }

    //trouve les ports disponibles
    public void discoverPeers(){
        mManager.discoverPeers((WifiP2pManager.Channel) mChannel, this);
    }

    //allow manager to discover peers and connect to other devices
    public void startDiscovery(){
        discoveryOn = true;
        Intent i = new Intent("scanAlarm");
        ctx.sendBroadcast(i);
    }

    public void closeConnections(){
        mManager.removeGroup((WifiP2pManager.Channel) mChannel, this);
        //TODO: ici j'ai remplacé connection par notre classe c'est possible que je me soit trompé !sorry
      //  Iterator<WifiP2Pconnection> it = connections.iterator();
      //  while(it.hasNext()){
      //      P2PConnection con = it.next();
      //      con.disconnect();
       // }
       // adapter.notifyDataSetChanged();
    }

    //stop trying to connect to other devices
    public void stopDiscovery(){ discoveryOn = false; }

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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        //essayer de se connecter � un port available
        device=peers.get(deviceAddress); //faut touver le deviceAdress quelque part pas trop compris
        config.deviceAddress = device.deviceAddress;
        mManager.connect((WifiP2pManager.Channel) mChannel, config, this);
    }

}

