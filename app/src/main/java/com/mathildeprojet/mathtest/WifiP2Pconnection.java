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
import 	java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Rafaelle on 21/05/2015.
 */

public class WifiP2Pconnection extends BroadcastReceiver implements  WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener, Parcelable {


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
    String deviceAddress;
    View view;

    public String message;

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

        final String action = intent.getAction();
        message="Coucou Rafi";

        WifiManager wifiMan = (WifiManager) ctx.getSystemService(
                Context.WIFI_SERVICE);

    	WifiInfo wifiInf = wifiMan.getConnectionInfo();
    	String myMAC = wifiInf.getMacAddress();
        info = wifiInf;
    	if(myMAC != null){


            Log.d("NOUS", "l'adresse mac est :" + myMAC);

        }

      //  Log.v("NOUS", "on rentre bien dans OnReceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
     //       Log.v("NOUS", "l'etat de la wifi est changé");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v("NOUS", "le wifi est activé !");
                //TODO: ajout en dernier
                Toast.makeText(mActivity, "Wifi direct is enabled", Toast.LENGTH_LONG).show();

                // Wifi P2P is enabled
            } else {
                Log.v("NOUS", "le wifi n'est pas activé");
                //TODO:nouveau
                Toast.makeText(mActivity, "wifi direct is disabled", Toast.LENGTH_LONG).show();

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
                            boolean unefois=true;
                            while (it.hasNext()) {
                                WifiP2pDevice device = (WifiP2pDevice) it.next();
                                if (!device.isGroupOwner()) {
                                    Log.v("NOUS", "Je suis Maître");
                                } else {
                                    Log.v("NOUS", "Je suis Esclave");
                                }

                                config.deviceAddress = device.deviceAddress;
                                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                                    @Override
                                    public void onSuccess() {
                                        Log.v("NOUS", "Connexion établie");
                                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                                            @Override
                                            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                                                //  Log.v("NOUS", "Bonjour et bienvenue conconnection");
                                                if (info.isGroupOwner) {

                                                    Log.v("NOUS", "Etablissement connexion du maître)");
                                                    //setup the server handshake with the group's IP, port, the device's mac, and the port for the conenction to communicate on
                                                    Serveuur serv = new Serveuur();
                                                    serv.setIP(info.groupOwnerAddress);
                                                    serv.execute();

                                                } else {
                                                    Log.v("NOUS", "Etablissement connexion de l'esclave");
                                                    //give server a second to setup the server socket
                                                    try {
                                                        //       Log.v("NOUS", "DANS le try");
                                                        Thread.sleep(1000);
                                                    } catch (Exception e) {
                                                        System.out.println(e.toString());
                                                    }
                                                    //      Log.v("NOUS", "après le catch");
                                                    String myIP = "";
                                                    try {
                                                        //         Log.v("NOUS", "dans le 2eme try");
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
                                                    //     Log.v("NOUS", "après le 2eme catch");


                                                    //setup the client handshake to connect to the server and trasfer the device's MAC, get port for connection's communication

                                                    Client client = new Client(info.groupOwnerAddress);
                                                    //       Log.v("NOUS", "avant setIP ");
                                                    client.setIPserv(info.groupOwnerAddress);
                                                    Log.v("NOUS", "Début de la connexion de l'esclave à la socket");
                                                    client.execute();
                                                    //       Log.v("NOUS", "après exécute");



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


                            // DO WHATEVER YOU WANT HERE
                            // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT

                        }
                    });


                                    }


                // Call WifiP2pManager.requestPeers() to get a list of current peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
              // Log.v("NOUS", "repondre à une nouvelle co ou se deco");
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
            Serveuur serv = null;
            try {
                serv = new Serveuur(info.groupOwnerAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            serv.setIP(info.groupOwnerAddress);
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
            Client client = new Client(info.groupOwnerAddress);
            client.setIPserv(info.groupOwnerAddress);
            client.execute();

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


    public class Serveuur extends AsyncTask<Void, Void, String> {



        InetAddress IP;
        InetAddress servaddr;
        SocketAddress sockaddr;
        String mes=message;


        public Serveuur() {

        }

        public Serveuur(InetAddress seerv) throws UnknownHostException {

            servaddr=seerv;
            IP=seerv;

        }
        public void setIP(InetAddress ip) {
            IP=ip;


            servaddr=ip;


        }



        public String doInBackground(Void...params) {

            Log.v("NOUS", "Bonjour socket");
            try {
               Log.v("NOUS", "Bonjour socket 2");
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(11000,50,servaddr);

                serverSocket.setReuseAddress(true);
                serverSocket.setSoTimeout(10000);
                //Port = serverSocket.getLocalPort();
                //adrr=serverSocket.getInetAddress();

                Log.v("NOUS", "Bonjour socket 3");
                Log.v("NOUS", "Bonjour socket 7, je suis la socket à l'adresse "+ serverSocket.getLocalSocketAddress() );



                // serverSocket.bind(new InetSocketAddress(IP,Port));


                Log.v("NOUS", "La Socket est prêt à être acceptée");
                Socket client = serverSocket.accept();
                sockaddr=serverSocket.getLocalSocketAddress();
                Log.v("NOUS", "l/'adresse de la socket est" +sockaddr);



                Log.v("NOUS", "socket créée avec succès");
                DataOutputStream dOut = new DataOutputStream(client.getOutputStream());

// Send first message
                dOut.writeByte(1);
                dOut.writeUTF(message);
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


                DataInputStream dIn = new DataInputStream(client.getInputStream());

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
                        default:

                            done=true;
                    }
                }


                client.close();
                serverSocket.close();

                return "reussi" ;
            } catch (IOException e) {
                Log.d("NOUS"," Erreur côté serveur: " + e.getMessage());

            }
            return null;
        }

    }

    public class Client extends AsyncTask<Void, Void, String> implements Parcelable {


        InetAddress IPserv;
        SocketAddress sockaddr;

        public Client(InetAddress serv) {
            IPserv = serv;
        }

        public void setIPserv(InetAddress IP) {
            IPserv = IP;
        }


        public String doInBackground(Void... params) {



            try {

                Log.v("Nous", "log1 niveau client");

                InetAddress servaddr = IPserv;


                // socket.bind(new InetSocketAddress(IPserv, 5560));

                Log.v("NOUS","test avant log2");
                Log.v("Nous", "log2 niveau client avec adresse du maître : " + IPserv + " numero de port " + 11000);
                Socket socket = new Socket(IPserv,11000);
                sockaddr=socket.getLocalSocketAddress();
                Log.v("NOUS"," et socket adresse : " + sockaddr);

                Log.v("Nous", "log3 niveau client");

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
                        default:

                            done=true;
                    }
                }


                Log.v("NOUS", "log4 juste avant le close()");
                dIn.close();
                Log.v("NOUS", "log5 juste après le close()");

                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

// Send first message
                dOut.writeByte(1);
                dOut.writeUTF("coucou jm");
                dOut.flush(); // Send off the data

// Send the second message
                dOut.writeByte(2);
                dOut.writeUTF("Maintenant message du client pour le serveur");
                dOut.flush(); // Send off the data

// Send the third message
                dOut.writeByte(3);
                dOut.writeUTF("1er message");
                dOut.writeUTF("2ème message");
                dOut.flush(); // Send off the data

// Send the exit message
                dOut.writeByte(-1);
                dOut.flush();

                dOut.close();

            } catch (IOException e) {
                Log.d("NOUS", "Erreur coté client: " + e.getMessage());
                ;
            }

            return IPserv.toString();

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            
        }
    }

}
