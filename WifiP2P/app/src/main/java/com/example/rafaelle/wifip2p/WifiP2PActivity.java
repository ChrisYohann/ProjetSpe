package com.example.rafaelle.wifip2p;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.net.wifi.p2p.*;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;



public class WifiP2PActivity extends AppCompatActivity implements ChannelListener,OnClickListener,PeerListListener,ConnectionInfoListener{
    WifiP2pManager mManager;
    WifiP2Pconnection WifiConnection;
    WifiP2Pconnection mReceiver;
    WifiP2pDevice device;
    Channel mChannel;
    private Button buttonFind;
    private Button buttonConnect;
    Context context;
    IntentFilter mIntentFilter = new IntentFilter();

//je pense qu'il faut faire un new wifiConnection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("NOUS", "on rentre bien dans OnCreate");
        setContentView(R.layout.activity_wifi_p2_p);
        Context context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Log.v("NOUS","on récupère bien un WifiP2PManager");
        Looper looper= getMainLooper();
        WifiConnection=new WifiP2Pconnection(context,mManager,looper,this);
        mChannel = WifiConnection.getChannel();

        registerReceiver(mReceiver, mIntentFilter);

        this.buttonConnect = (Button)
                this.findViewById(R.id.buttonConnect);
        this.buttonConnect.setOnClickListener(this);

        this.buttonFind =
                (Button)this.findViewById(R.id.buttonFind);
        this.buttonFind.setOnClickListener(this);

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("NOUS", "on rentre bien dans OnResume");
        registerReceiver(mReceiver, mIntentFilter);
}
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("NOUS", "on rentre bien dans OnPause");
        unregisterReceiver(mReceiver);
    }

    public void startScan(View v){
        if(((Button)findViewById(R.id.bouton)).getText().equals("Start Scanning")){
            WifiConnection.startDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Stop Scanning");
        }else{
            WifiConnection.stopDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Start Scanning");
        }
    }

    public void closeConnections(View v){
        WifiConnection.closeConnections();
    }



    public void onClick(View v) {
        if(v == buttonConnect)
        {
            connect(this.device);
        }
        else if(v == buttonFind)
        {
            find();
        }

    }

    public void connect(WifiP2pDevice device)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        if(device != null)
        {
            config.deviceAddress = device.deviceAddress;
            mManager.connect(mChannel, config, new ActionListener() {

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
        mManager.discoverPeers(mChannel, new
                WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiP2PActivity.this, "Finding Peers",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode)
                    {
                        Toast.makeText(WifiP2PActivity.this, "Couldnt find peers ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onChannelDisconnected() {
        //handle the channel lost event
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        for (WifiP2pDevice device : peerList.getDeviceList()) {
            this.device = device;
            break;


        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();
    }
}
