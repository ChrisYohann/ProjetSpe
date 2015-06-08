package com.mathildeprojet.mathtest;

import android.content.BroadcastReceiver;
        import android.app.Activity;
        import android.app.ListActivity;
        import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
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
import java.util.Enumeration;


public class WifiP2PActivity extends Activity implements ChannelListener,OnClickListener,ConnectionInfoListener {
    private WifiP2pManager mManager;
    private Button buttonFind;
    private Button buttonsocket;
    private Button buttonEnvoyer;
    private EditText messages;
    private Channel channel;
    private WifiP2pDevice device;
    private Button buttonConnect;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2_p);
        context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Looper looper = getMainLooper();
//on d�finit les actions du filtres, on ne s'occupe que de ces actions
        filtre.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        this.channel = mManager.initialize(context, looper, null);
        //initialisation de la connection
        registerReceiver(mReceiver, filtre);
        mReceiver = new WifiP2Pconnection(context, mManager, channel, this);
        // this.buttonConnect = (Button) this.findViewById(R.id.buttonConnect);
        //this.buttonConnect.setOnClickListener(this);
        this.buttonFind = (Button) this.findViewById(R.id.buttonFind);
        this.buttonFind.setOnClickListener(this);
        this.buttonsocket = (Button) this.findViewById(R.id.buttonsocket);
        this.buttonsocket.setOnClickListener(this);
        this.buttonEnvoyer = (Button) this.findViewById(R.id.buttonEnvoyer);
        this.buttonEnvoyer.setOnClickListener(this);
        this.messages = (EditText) this.findViewById(R.id.messages);
        //peerlist = (ListView)findViewById(R.id.peer_list);
        //peerlist.setAdapter(wifiConnection.adapter);
        //peerlist.setOnItemClickListener(this);

        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

            while (enumeration.hasMoreElements()) {
                NetworkInterface eth0 = null;
                eth0 = enumeration.nextElement();
                String myIP = "";

                Log.d("net", "interface : " + eth0.toString());
                Enumeration<InetAddress> en2 = eth0.getInetAddresses();
                while (en2.hasMoreElements()) {
                    InetAddress inet = en2.nextElement();
                    if (!inet.isLoopbackAddress() && inet instanceof Inet4Address) {
                        myIP = inet.getHostAddress();
                    }

                    if (eth0.getName().length()>2&&eth0.getName().substring(0,3).equals("p2p") && myIP != "0.0.0.0" && myIP != null) {

                        inter = eth0;
                    }
                }


            }
        } catch (SocketException e) {
            e.printStackTrace();
            ;
        }
        Log.d("NOUS", getLocalIpAddress());
        Thread receiver = new Thread(new SocketListener());
        receiver.start();
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

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("NOUS", "on rentre bien dans OnResume");
        registerReceiver(mReceiver, filtre);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("NOUS", "on rentre bien dans OnPause");
        unregisterReceiver(mReceiver);
    }
/*
    public void startScan(View v){
        if(((Button)findViewById(R.id.bouton)).getText().equals("Start Scanning")){
            mReceiver.startDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Stop Scanning");
        }else{
            mReceiver.stopDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Start Scanning");
        }
    }*/

    public void closeConnections(View v) {
        mReceiver.closeConnections();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonFind) {
            Thread sender = new Thread(new SocketSender());
            sender.start();
         /*    mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiP2PActivity.this, "Finding Peers", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiP2PActivity.this, "Couldnt find peers ",
                                Toast.LENGTH_SHORT).show();
                    }
                });*/

        } else if (v == buttonsocket) {

            Thread yoo = new Thread(new SocketListener());
            yoo.start();


        }

    }

  /*  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mReceiver.tryConnection(position);
    }*/

    public void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (device != null) {
            config.deviceAddress = device.deviceAddress;
            mManager.connect(channel, config, new ActionListener() {

                public void onSuccess() {
                    //success
                }


                public void onFailure(int reason) {
                    //fail
                }
            });
        } else {
            Toast.makeText(WifiP2PActivity.this, "Couldn't connect, device is not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void find() throws SocketException, UnknownHostException {


        Toast.makeText(WifiP2PActivity.this, "envoie", Toast.LENGTH_SHORT).show();
        Sender envoie = new Sender("bonjour JM", context);
        try {
            envoie.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();
    }

    @Override
    public void onChannelDisconnected() {

    }


    public void receiveSocket() {

        WifiManager wifiMan = (WifiManager) context.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String myMAC = wifiInf.getMacAddress();

        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(myMAC, 5353)), 500);

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
            Log.d("NOUS", e.getMessage());
            ;
        }

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
                Log.i("Socket Thread ", "Thread running");

                // Inet6Address address = (Inet6Address)InetAddress.getByName ("fe80::983b:16ff:feb3:a5f7");
                Log.i("Socket Thread ", "Dans le try");
                socket = new DatagramSocket();
                //      multisocket.joinGroup(InetAddress.getByName("192.168.49.180"));
                //     multisocket.joinGroup(InetAddress.getByName("192.168.49.1"));

                Log.d("BONJOUR", "bonjour: ");

                while (true) {


                    packet = new DatagramPacket(buf, buf.length);
                    multisocket = new MulticastSocket(8888);
                    Log.i("Socket Thread ", "avant le receive");
                    multisocket.receive(packet);
                    Log.i("Socket Thread ", "Apres");

                    String s = new String(packet.getData(), packet.getOffset(), packet.getLength());

                    Log.d("BONJOUR", "message reçu: " + s + "port : " + packet.getPort() + "host add: " + packet.getAddress().getHostAddress());


                }
            } catch (IOException e) {
                Log.i("Socket Thread ", "Dans le catch");
                Log.e(getClass().getName(), e.getMessage());
            }
            Log.i("Socket Thread ", "Après try catch");
/*
  //          WifiManager wim = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wim != null) {
                WifiManager.MulticastLock mcLock = wim.createMulticastLock("yo");
                mcLock.acquire();
            }

            byte[] buffer = new byte[4096];
            DatagramPacket rPacket = new DatagramPacket(buffer, buffer.length);
            MulticastSocket rSocket;

            try {
                rSocket = new MulticastSocket(8888);
            } catch (IOException e) {
                Log.d("socket", "Impossible to create a new MulticastSocket on port " + 8888);
                e.printStackTrace();
                return;
            }


            try {
                rSocket.receive(rPacket);
            } catch (IOException e1) {
                Log.d("yyo", "There was a problem receiving the incoming message.");
                e1.printStackTrace();

            }

            String s = new String(rPacket.getData(), rPacket.getOffset(), rPacket.getLength());

            Log.d("BONJOUR", "message reçu: " + s + "port : " + rPacket.getPort() + "host add: " + rPacket.getAddress().getHostAddress());

  //      }*/
        }
    }

        class SocketSender implements Runnable {
            String str;
            String username = "JM";

            @Override
            public void run() {


                String s = null;
                final EditText editTextSender = (EditText) findViewById(R.id.messages);
                try {
                    s = username + " : " + editTextSender.getText().toString();
                    Log.d("Bonjour", s);
                } catch (Exception e) {
                    Log.i("Socket Sender ", e.getMessage());
                }


                //DatagramSocket socket;
                try {
                 /*   group = InetAddress.getByName("FF01:0:0:0:0:0:0:101 ");
                    multisocket = new MulticastSocket(8888);
                    multisocket.joinGroup(new InetSocketAddress(group, 8888), inter);*/

//                socket.setBroadcast(true);



                    WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiManager.MulticastLock multicastLock = wm.createMulticastLock("mylock");

                    multicastLock.acquire();
                    byte[] buf = new byte[256];
                    buf = s.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.49.98"), 8888);
                    InetAddress address = InetAddress.getByName("192.168.49.255");
                     packet = new DatagramPacket(buf, buf.length, address,8888);
                    Log.i("Socket Sender", "About to send message" + multisocket.getLocalSocketAddress());
                    socket.send(packet);
                    Log.i("Socket Sender", "Sent message");


                } catch (SocketException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (UnknownHostException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                } catch (IOException e3) {
                    // TODO Auto-generated catch block
                    e3.printStackTrace();
                }
/*
            // Create the send socket
  //          if(socket == null) {
                try {
                    socket = new DatagramSocket();
                } catch (SocketException e) {
                    Log.d("yop", "There was a problem creating the sending socket. Aborting.");
                    e.printStackTrace();

                }
            }

            // Build the packet
            byte data[] = s.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length);



            try {
                packet = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.49.1"),8888);
            } catch (UnknownHostException e) {
                Log.d("yop", "It seems that " + "192.168.49.1" + " is not a valid ip! Aborting.");
                e.printStackTrace();

            }

            try {
                socket.send(packet);
            } catch (IOException e) {
                Log.d("yop", "There was an error sending the UDP packet. Aborted.");
                e.printStackTrace();

            }
            Log.d("Socket sender :", "Message Sent");
*/
            }

        }

        InetAddress getBroadcastAddress() throws IOException {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp = wifi.getDhcpInfo();
            // handle null somehow

            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            return InetAddress.getByAddress(quads);
        }

}
