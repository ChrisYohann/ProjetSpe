package com.mathildeprojet.mathtest;

/**
 * Created by matylde on 28/05/2015.
 */

import android.annotation.TargetApi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.TextView;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Rafaelle on 21/05/2015.
 */

public class WifiP2Pconnection extends BroadcastReceiver implements WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener, Parcelable {


    //private Boolean discoveryOn=false;

    private Context ctx;
    private Looper lpr;
    AlertDialog.Builder adbldr;
    public boolean onetime = true;


    private WifiP2pManager mManager;
    private Channel mChannel; //on suppose que le channel est la connection entre 2 appareils
    private WifiP2PActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    //private WifiP2pDevice device;
    private WifiP2pDeviceList peers;
    private WifiInfo info;
    Collection<WifiP2pDevice> devicelist;
    private WifiP2pConfig config = new WifiP2pConfig();
    String deviceAddress;
    View view;

    public String message;
    boolean go = true; // savoir si le device sur lequel tourne l'appli est group owner

    public WifiP2Pconnection(Context ctxt, WifiP2pManager manager, Channel channel,
                             WifiP2PActivity activity) {
        super();
        this.ctx = ctxt;
        this.mChannel = channel;
        this.mManager = manager;
        this.mActivity = activity; //pour relier � l'activit� principale
        adbldr = new AlertDialog.Builder(ctx);
    }

    /*
    public WifiP2pDevice getDevice() {
        return device;
    }*/
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        message = "Bienvenue";

        WifiManager wifiMan = (WifiManager) ctx.getSystemService(
                Context.WIFI_SERVICE);

        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String myMAC = wifiInf.getMacAddress();
        info = wifiInf;
        if (myMAC != null) {


            Log.d("NOUS", "l'adresse mac est :" + myMAC);

        }

        //  Log.v("NOUS", "on rentre bien dans OnReceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //       Log.v("NOUS", "l'etat de la wifi est changé");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v("NOUS", "le wifi est activé !");


                // Wifi P2P is enabled
            } else {
                Log.v("NOUS", "le wifi n'est pas activé");

                AlertDialog.Builder adb1 = new AlertDialog.Builder(mActivity);

                //On donne un titre à l'AlertDialog
                adb1.setTitle("Attention");
                adb1.setMessage("Veuillez activer votre connexion Wi-Fi puis relancer l'application");
                //Bouton du dialogue
                adb1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                adb1.show();

                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else {


            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.v("NOUS", "on a de nouveaux pairs à rechercher");
                //TODO:nouveau
                if (mManager != null) {
                    //request peers va permettre de connaître les ports auxquels on PEUT se connecter, il s'appuie sur la liste des pairs disponibles


                    mManager.requestPeers(mChannel, new PeerListListener() {

                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.v("NOUS", String.format("Appareils autour: %d appareils disponible", peers.getDeviceList().size()));
                            Iterator it = peers.getDeviceList().iterator();
                            WifiP2pConfig config = new WifiP2pConfig();
                            boolean unefois = true;

                            while (it.hasNext()) {
                                WifiP2pDevice device = (WifiP2pDevice) it.next();
                                if (!device.isGroupOwner()) {

                                } else {
                                    go = false;
                                    Log.v("group owner", "Je suis Client");
                                }

                                if (go) {
                                    Log.v("group owner", "Je suis GO");
                                }

                                config.deviceAddress = device.deviceAddress;
                                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() { // connexion entre les deux devices qui s'etablit

                                    @Override
                                    public void onSuccess() {
                                        Log.v("NOUS", "Connexion établie");
                                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                            @Override
                                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                                if (go) {

                                                    Log.v("group owner", "Etablissement connexion en tant que maître)");


                                                } else {
                                                    Log.v("group owner", "Etablissement connexion en tant qu'esclave");
                                                    //give server a second to setup the server socket
                                                    try {

                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                        System.out.println(e.toString());
                                                    }
                                                    String myIP = "";
                                                    try {
                                                        Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                                                        while (en.hasMoreElements()) {
                                                            NetworkInterface ni = en.nextElement();
                                                            Enumeration<InetAddress> en2 = ni.getInetAddresses();
                                                            while (en2.hasMoreElements()) {
                                                                InetAddress inet = en2.nextElement();
                                                                if (!inet.isLoopbackAddress() && inet instanceof Inet4Address) {
                                                                    myIP = inet.getHostAddress();
                                                                }
                                                            }
                                                        }
                                                    } catch (SocketException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                        Log.v("NOUS", "Echec de Connextion");
                                    }
                                });


                            }

                        }
                    });


                }


                // Call WifiP2pManager.requestPeers() to get a list of current peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                if (mManager == null) {
                    return;
                }
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {

                    mManager.requestConnectionInfo(mChannel, mActivity);
                } else {//c'est deconnecté
                }
                // Respond to new connection or disconnections
            }

        }
    }


    //trouve les pairs disponibles
    public void discoverPeers() {
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

    public void closeConnections() {
        mManager.removeGroup((WifiP2pManager.Channel) mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.v("NOUS", "closeconnection marche");
            }

            @Override
            public void onFailure(int reason) {
                Log.v("NOUS", "closeconnection ne marche pas");
            }
        });
    }


    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.isGroupOwner) {


        } else {

            //give server a second to setup the server socket
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            String myIP = "";
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while (en.hasMoreElements()) {
                    NetworkInterface ni = en.nextElement();
                    Enumeration<InetAddress> en2 = ni.getInetAddresses();
                    while (en2.hasMoreElements()) {
                        InetAddress inet = en2.nextElement();
                        if (!inet.isLoopbackAddress() && inet instanceof Inet4Address) {
                            myIP = inet.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


    }

}
