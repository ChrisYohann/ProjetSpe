package com.mathildeprojet.mathtest;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Looper;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.view.View.OnClickListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;


public class WifiP2PActivity extends Activity implements ChannelListener,OnClickListener,PeerListListener,ConnectionInfoListener {
    private WifiP2pManager mManager;
    private Button buttonFind;
    private Channel channel;
    private WifiP2pDevice device;
    private Button buttonConnect;
    private WifiP2Pconnection mReceiver = null;
    private Context context;
    private TextView blabla;
    WifiP2pDeviceList peers;
    private IntentFilter filtre = new IntentFilter();
    ListView peerlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2_p);
        context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Looper looper= getMainLooper();
//on d�finit les actions du filtres, on ne s'occupe que de ces actions
        filtre.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        this.channel = mManager.initialize(context, looper, null);
        //initialisation de la connection
        mReceiver=new WifiP2Pconnection(context,mManager,channel,this);
        registerReceiver(mReceiver, filtre);

       // this.buttonConnect = (Button) this.findViewById(R.id.buttonConnect);
        //this.buttonConnect.setOnClickListener(this);
        this.buttonFind = (Button)this.findViewById(R.id.buttonFind);
        this.buttonFind.setOnClickListener(this);

        //peerlist = (ListView)findViewById(R.id.peer_list);
        //peerlist.setAdapter(wifiConnection.adapter);
        //peerlist.setOnItemClickListener(this);
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

    public void closeConnections(View v){
        mReceiver.closeConnections();
    }

    public void onClick(View v) {
        if(v == buttonConnect)
        {
            //if (mReceiver.tryConnection(0)==null) {
              //  return
            //}
            connect(device);//pour une paire
        }
        //else if(v == buttonFind)
        //{
        //    find();
        //}

    }

  /*  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mReceiver.tryConnection(position);
    }*/

    public void connect(WifiP2pDevice device)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        if(device != null)
        {
            config.deviceAddress = device.deviceAddress;
            mManager.connect(channel, config, new ActionListener() {

                public void onSuccess() {
                    //success
                }


                public void onFailure(int reason) {
                    //fail
                }
            });
        }
        else
        {
            Toast.makeText(WifiP2PActivity.this, "Couldn't connect, device is not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void find()
    {
        mManager.discoverPeers(channel, new
                WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiP2PActivity.this, "Finding Peers", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiP2PActivity.this, "Couldnt find peers ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /*
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers){
        mReceiver.onPeersAvailable(peers);
    }
    */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        for (WifiP2pDevice device : peerList.getDeviceList()) {
            this.device = device;
            break;
        } }
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();
    }

    @Override
    public void onChannelDisconnected() {

    }
}
