package com.mathildeprojet.mathtest;

import android.content.BroadcastReceiver;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.wifi.p2p.WifiP2pGroup;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.InetAddress;

        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.net.wifi.p2p.WifiP2pConfig;
        import android.net.wifi.p2p.WifiP2pDevice;
        import android.net.wifi.p2p.WifiP2pDeviceList;
        import android.net.wifi.p2p.WifiP2pInfo;
        import android.os.Looper;
        import android.net.wifi.p2p.WifiP2pManager.Channel;
        import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
        import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View.OnClickListener;
        import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.net.wifi.p2p.WifiP2pManager;
        import android.net.wifi.p2p.WifiP2pManager.ActionListener;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.util.Log;
        import android.widget.Toast;

import java.net.DatagramPacket;
import 	java.net.InetSocketAddress;
        import java.io.IOException;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.io.OutputStream;
        import        java.io.FilterOutputStream;
        import      java.io.DataOutputStream;
        import java.io.InputStream;
        import  java.io.FilterInputStream;
        import java.io.DataInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;


public class WifiP2PActivity extends Activity implements ChannelListener,OnClickListener,ConnectionInfoListener {
    private WifiP2pManager mManager;
    private Button buttonFind;
    private Button buttonsocket;
    private Button buttonEnvoyer;
    private TextView boitedialogue;
    private EditText messages;
    private Channel channel;
    private WifiP2pDevice device;

    private WifiP2Pconnection mReceiver = null;
    private Context context;
    private InetAddress group;
    private View view;
    WifiP2pDeviceList peers;
    MulticastSocket multisocket;
    NetworkInterface inter = null;
    DatagramSocket socket;
    int IP;
    private IntentFilter filtre = new IntentFilter();
    private static Integer localPort, remotePort;
    private static String destIp;
    private String textbox="";
    private String pseudo="";
    public boolean relais = false ;
    WifiP2PActivity acti = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2_p);
        context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Looper looper = getMainLooper();
        filtre.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        this.channel = mManager.initialize(context, looper, null);
        //initialisation de la connection
        registerReceiver(mReceiver, filtre);

        this.buttonEnvoyer = (Button) this.findViewById(R.id.buttonEnvoyer);
        this.buttonEnvoyer.setOnClickListener(this);
        this.messages = (EditText) this.findViewById(R.id.messages);

        boitedialogue = (TextView) findViewById(R.id.boite);
        boitedialogue.setMovementMethod(new ScrollingMovementMethod());

        Intent i  = getIntent();
        pseudo = i.getStringExtra("pseudo");

        boolean wlan = false;
        boolean p2p = false;



        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

            while (enumeration.hasMoreElements()) {
                NetworkInterface eth0 = null;
                eth0 = enumeration.nextElement();
                String myIP = "";
                Log.d("interface", "interface : " + eth0.toString());
                Enumeration<InetAddress> en2 = eth0.getInetAddresses();
                while (en2.hasMoreElements()) {
                    InetAddress inet = en2.nextElement();
                    if (!inet.isLoopbackAddress() && inet instanceof Inet4Address) {
                        myIP = inet.getHostAddress();
                    }

                    if (eth0.getName().length()>2&&eth0.getName().substring(0,3).equals("p2p") && myIP != "0.0.0.0" && myIP != null) {
                        inter = eth0;
                        // Si jamais le device possede une interface ( = une IP ) via le Wi-Fi Direct
                        p2p = true;

                    }
                    if (eth0.getName().length()>2&&eth0.getName().substring(0,3).equals("wla") && myIP != "0.0.0.0" && myIP != null) {
                        // Si jamais le device possede une interface ( = une IP ) via le Wi-Fi normal
                        wlan = true;
                    }
                }

                relais = p2p&&wlan;

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Thread receiver = new Thread(new SocketListener());
        receiver.start();

        mManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener(){

            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {

                if (group != null) {
                    Collection<WifiP2pDevice> Liste_device = group.getClientList();
                    if (Liste_device != null) {
                        Log.d("device", "liste non vide");
//                        Log.d("pass", group.getPassphrase());
                        Iterator<WifiP2pDevice> it = Liste_device.iterator();
                        WifiP2pDevice deviice;

                        while (it.hasNext()) {
                            deviice = it.next();
                            Log.d("names", deviice.deviceName);

                        }

                    } else {
                        Log.d("device", "liste vide");
                    }


                }
            }

        });

      /*  Thread yoop = new Thread(new SocketListener());
        yoop.start();*/


    }


    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, filtre);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonEnvoyer) {
            Thread sender = new Thread(new Sender(socket,this,pseudo));
            sender.start();
            EditText editTextSender = (EditText) findViewById(R.id.messages);
            editTextSender.setText("");
        }

    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();
    }

    @Override
    public void onChannelDisconnected() {

    }

    class SocketListener implements Runnable {
        String str;

        public void run() {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiManager.MulticastLock lock = wifi.createMulticastLock("Log_Tag");
                lock.acquire();
            }
            try {

                DatagramPacket packet;
                byte[] buf = new byte[256];
                socket = new DatagramSocket(8888);

                while (true) {
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String s = new String(packet.getData(), packet.getOffset(), packet.getLength());
                                    textbox = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String stg = boitedialogue.getText().toString() + "\n" + textbox;
                            boitedialogue.setText(stg);
                        }
                    });

                    // méthode à revoir si jamais quelqu'un reprend le code

                    // si jamais device est sur les 2 interfaces ( wlan0 et p2p0 ) => il est dans 2 groupes différents théoriquement
            /*        if (relais) {

                        Thread sender = new Thread(new Sender(s,socket,acti,pseudo));
                        sender.start();


                    }*/
                }
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }

        }
    }

}
