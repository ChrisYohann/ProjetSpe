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
import 	java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Rafaelle on 21/05/2015.
 */

public class WifiP2Pconnection extends BroadcastReceiver implements  WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener {


    //private Boolean discoveryOn=false;

    private Context ctx;
    private Looper lpr;
    AlertDialog.Builder adbldr;
    public boolean onetime=true;

    private WifiP2pManager mManager;
    private Channel mChannel; //on suppose que le channel est la connection entre 2 appareils
    private WifiP2PActivity mActivity;
    private WifiP2pManager.PeerListListener myPeerListListener;
    //private WifiP2pDevice device;
    private WifiP2pDeviceList peers;
    private WifiInfo info;
    Collection<WifiP2pDevice> devicelist;
    private WifiP2pConfig config = new WifiP2pConfig();
    ArrayList<Connection> connections;
    ArrayAdapter<Connection> adapter;
    String deviceAddress;
    View view;

    //TODO: remplacer le TextView connecte par WifiP2PActivity activity !
    public WifiP2Pconnection(Context ctxt, WifiP2pManager manager, Channel channel,
                             WifiP2PActivity activity){
        super();
        this.ctx=ctxt;
        this.mChannel=channel;
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
        String action = intent.getAction();

        WifiManager wifiMan = (WifiManager) ctx.getSystemService(
                Context.WIFI_SERVICE);

    	WifiInfo wifiInf = wifiMan.getConnectionInfo();
    	String myMAC = wifiInf.getMacAddress();
        info = wifiInf;
    	if(myMAC != null){


            Log.d("NOUS", "l'adresse mac est :" + myMAC);

        }

        Log.v("NOUS", "on rentre bien dans OnReceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.v("NOUS", "l'etat de la wifi est changé");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v("NOUS", "l'etat de la wifi est ok");
                //TODO: ajout en dernier
                Toast.makeText(mActivity, "Wifi direct is enabled", Toast.LENGTH_LONG).show();

                // Wifi P2P is enabled
            } else {
                Log.v("NOUS", "l'etat de la wifi est pas ok");
                //TODO:nouveau
                Toast.makeText(mActivity, "wifi direct is disabled", Toast.LENGTH_LONG).show();

                // Wi-Fi P2P is not enabled
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else {
            if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.v("NOUS", "on a de nouveau pairs à rechercher");
                //TODO:nouveau
                if (mManager != null&&onetime) {
                    //request peers va permettre de connaître les ports auxquels on PEUT se connecter, il s'appuie sur la liste des pairs disponibles


                    mManager.requestPeers(mChannel, new PeerListListener() {

                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.v("NOUS", String.format("Appareils autour: %d appareils disponible", peers.getDeviceList().size()));
                            Iterator it = peers.getDeviceList().iterator();
                            WifiP2pConfig config = new WifiP2pConfig();
                            boolean unefois=true;
                            while (it.hasNext()) {
                                WifiP2pDevice device = (WifiP2pDevice) it.next();
                                if (!device.isGroupOwner()) {
                                    Log.v("NOUS", "fuck, I am the master");
                                } else {
                                    Log.v("NOUS", "Je suis un esclave!");
                                }

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

                                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                    @Override
                                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                        Log.v("NOUS", "Bonjour et bienvenue conconnection");
                                        if (info.isGroupOwner) {

                                            Log.v("NOUS", "Maitre ( onconnection)");
                                            //setup the server handshake with the group's IP, port, the device's mac, and the port for the conenction to communicate on
                                            Serveuur serv = new Serveuur();
                                            serv.setIP(info.groupOwnerAddress.getHostAddress());
                                            serv.execute();

                                        } else {
                                            Log.v("NOUS", "Esclave ( onconnection)");
                                            //give server a second to setup the server socket
                                            try {
                                                Log.v("NOUS", "DANS le try");
                                                Thread.sleep(1000);
                                            } catch (Exception e) {
                                                System.out.println(e.toString());
                                            }
                                            Log.v("NOUS", "après le catch");
                                            String myIP = "";
                                            try {
                                                Log.v("NOUS", "dans le 2eme try");
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
                                            Log.v("NOUS", "après le 2eme catch");


                                            //setup the client handshake to connect to the server and trasfer the device's MAC, get port for connection's communication

                                            Client client = new Client();
                                            Log.v("NOUS", "avant setIP ");
                                            client.setIPserv(info.groupOwnerAddress.toString());
                                            Log.v("NOUS", "après setIP");
                                            client.execute();
                                            Log.v("NOUS", "après exécute");


                                        }

                                    }
                                });
                            }


                            // DO WHATEVER YOU WANT HERE
                            // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT

                        }
                    });
                }
                onetime=false;
                // Call WifiP2pManager.requestPeers() to get a list of current peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.v("NOUS", "repondre à une nouvelle co ou se deco");
                //TODO:nouveau
                if (mManager == null) {
                    return;
                }
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    //TODO: cast ?
                    mManager.requestConnectionInfo(mChannel, mActivity);
                } else {//c'est deconnecté
                }
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Log.v("NOUS", "dire que notre etat de co change");
                // Respond to this device's wifi state changing
            }

            Log.v("NOUS", "test data");

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

    public void closeConnections(){
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
        if(info.isGroupOwner){


            //setup the server handshake with the group's IP, port, the device's mac, and the port for the conenction to communicate on
            Serveuur serv = new Serveuur();
            serv.setIP( info.groupOwnerAddress.getHostAddress());
            serv.execute();

        }else{

            //give server a second to setup the server socket
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e.toString());
            }

            String myIP = "";
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while(en.hasMoreElements()){
                    NetworkInterface ni = en.nextElement();
                    Enumeration<InetAddress> en2 = ni.getInetAddresses();
                    while(en2.hasMoreElements()){
                        InetAddress inet = en2.nextElement();
                        if(!inet.isLoopbackAddress() && inet instanceof Inet4Address){
                            myIP = inet.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            //setup the client handshake to connect to the server and trasfer the device's MAC, get port for connection's communication
            Client client = new Client();
            client.setIPserv(info.groupOwnerAddress.getHostAddress());
            client.execute();

        }
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup group) {

    }


    public class Serveuur extends AsyncTask<Void, Void, String> {



        String IP;


        public void setIP(String ip) {
            IP=ip;
        }


        public String doInBackground(Void...params) {

            Log.v("NOUS", "Bonjour socket");
            try {
                Log.v("NOUS", "Bonjour socket 2");
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(5353);
                serverSocket.setReuseAddress(true);
                Log.v("NOUS", "Bonjour socket 3");
                //serverSocket.bind(null);
                Log.v("NOUS", "Bonjour socket 4");
                Socket client = serverSocket.accept();
                Log.v("NOUS", "socket créée avec succès");
                DataOutputStream dOut = new DataOutputStream(client.getOutputStream());

// Send first message
                dOut.writeByte(1);
                dOut.writeUTF("This is the first type of message.");
                dOut.flush(); // Send off the data

// Send the second message
                dOut.writeByte(2);
                dOut.writeUTF("This is the second type of message.");
                dOut.flush(); // Send off the data

// Send the third message
                dOut.writeByte(3);
                dOut.writeUTF("This is the third type of message (Part 1).");
                dOut.writeUTF("This is the third type of message (Part 2).");
                dOut.flush(); // Send off the data

// Send the exit message
                dOut.writeByte(-1);
                dOut.flush();

                dOut.close();

                client.close();
                serverSocket.close();

                return "reussi" ;
            } catch (IOException e) {
                Log.d("NOUS"," Erreur côté serveur: " + e.getMessage());

            }
            return null;
        }
    }

    public class Client extends AsyncTask<Void, Void, String> {


        String IPserv;

        public void setIPserv(String IP) {
            IPserv = IP;
        }

        public String doInBackground(Void... params) {

            Socket socket = new Socket();

            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(IPserv, 5353)), 0);

                DataInputStream dIn = new DataInputStream(socket.getInputStream());

                boolean done = false;
                while (!done) {
                    byte messageType = dIn.readByte();

                    switch (messageType) {
                        case 1: // Type A
                            Log.v("Nous ", "Message A :" + dIn.readUTF());
                            break;
                        case 2: // Type B
                            Log.v("Nous ", "Message B :" + dIn.readUTF());
                            break;
                        case 3: // Type C
                            Log.v("Nous ", "Message C,1 :" + dIn.readUTF());
                            Log.v("Nous ", "Message C,2 :" + dIn.readUTF());
                            break;

                    }
                }

                dIn.close();
            } catch (IOException e) {
                Log.d("NOUS", "Erreur coté client: " + e.getMessage());
                ;
            }

            return IPserv;

        }
    }
    
}