package com.mathildeprojet.mathtest;

/**
 * Created by matylde on 28/05/2015.
 */
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.TextView;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Rafaelle on 21/05/2015.
 */
public class WifiP2Pconnection extends BroadcastReceiver implements  WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener {

    private Boolean discoveryOn=false;

    private Context ctx;
    private Looper lpr;
    AlertDialog.Builder adbldr;
    //TextView console;
    private IntentFilter mIntentFilter = new IntentFilter(); //seulement certaines actions sont filtr‘’‘’�es

    private WifiP2pManager mManager;
    private Channel mChannel; //on suppose que le channel est la connection entre 2 appareils
    private WifiP2PActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    private WifiP2pDevice device;
    private WifiP2pDeviceList peers;
    Collection<WifiP2pDevice> devicelist;
    private WifiP2pConfig config = new WifiP2pConfig();
    ArrayList<Connection> connections;
    ArrayAdapter<Connection> adapter;
    String deviceAddress;

    //TODO: remplacer le TextView connecte par WifiP2PActivity activity !
    public WifiP2Pconnection(Context ctxt, WifiP2pManager manager, Channel channel,
                             WifiP2PActivity activity) {
        super();
        Log.v("NOUS", "on rentre bien dans WifiP2PCo");
        this.ctx=ctxt;
        this.mChannel=channel;
        this.mManager = manager;
        //this.console = connecte;
        this.mActivity = activity; //pour relier � l'activit� principale
        adbldr = new AlertDialog.Builder(ctx);
        Log.v("NOUS", "construction de la boite de dialogue(alert)");
        //mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        //j'appelle directement cette m�thode dans activity
    }

    public WifiP2pDevice getDevice() {
        return device;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.v("NOUS", "on rentre bien dans OnReceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.v("NOUS", "l'etat de la wifi est changé");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v("NOUS", "l'etat de la wifi est ok");
                //TODO: ajout en dernier
                Toast.makeText(mActivity,"Wifi direct is enabled",Toast.LENGTH_LONG).show();

                // Wifi P2P is enabled
            } else {            Log.v("NOUS", "l'etat de la wifi est pas ok");
                //TODO:nouveau
                Toast.makeText(mActivity, "wifi direct is disabled",Toast.LENGTH_LONG).show();
                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.v("NOUS", "on a de nouveau pairs à rechercher");
            //TODO:nouveau
            if (mManager !=null) {
            //request peers va permettre de connaître les ports auxquels on PEUT se connecter, il s'appuie sur la liste des pairs disponibles
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {


                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.v("NOUS", String.format("Appareils autour: %d appareils disponible", peers.getDeviceList().size()));
                        Iterator it=peers.getDeviceList().iterator();
                        WifiP2pDevice device= (WifiP2pDevice) it.next();
                        WifiP2pConfig config= new WifiP2pConfig();
                        if (device.isGroupOwner()) {
                            while (it.hasNext()) {
                                Log.v("NOUS", "I am the master");
                                config.deviceAddress = device.deviceAddress;
                                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                                    @Override
                                    public void onSuccess() {
                                        Log.v("NOUS", "succeed connection");
                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                        Log.v("NOUS", "failed connection");
                                    }
                                });
                            }
                        }else {
                            Log.v("NOUS", "Je suis un esclave!");
                            config.deviceAddress = device.deviceAddress;
                            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                                @Override
                                public void onSuccess() {
                                    Log.v("NOUS", "succeed connection");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.v("NOUS", "failed connection");
                                }
                            });
                        }

                        // DO WHATEVER YOU WANT HERE
                        // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT

                    }
                });
            }
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.v("NOUS", "repondre à une nouvelle co ou se deco");
            //TODO:nouveau
            if (mManager ==null) {return;}
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                //TODO: cast ?
                mManager.requestConnectionInfo(mChannel, (WifiP2pManager.ConnectionInfoListener) mActivity);
            }else {//c'est deconnecté
            }
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.v("NOUS", "dire que notre etat de co change");
            // Respond to this device's wifi state changing
        }
    }

    //trouve les pairs disponibles
    public void discoverPeers(){
        mManager.discoverPeers((WifiP2pManager.Channel) mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.v("NOUS", "discovers peers marche");
            }

            @Override
            public void onFailure(int reason) {
                Log.v("NOUS", "discovers peers ne marche pas");
            }
        });
    }

    //allow manager to discover peers and connect to other devices
    public void startDiscovery(){
        discoveryOn = true;
        //Intent i = new Intent("scanAlarm");
        //ctx.sendBroadcast(i);
    }

    public void closeConnections(){
        mManager.removeGroup((WifiP2pManager.Channel) mChannel,new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.v("NOUS", "closeconnection marche");
            }

            @Override
            public void onFailure(int reason) {
                Log.v("NOUS", "closeconnection ne marche pas");
            }
        });
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
    public void onChannelDisconnected() {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {

    }


}