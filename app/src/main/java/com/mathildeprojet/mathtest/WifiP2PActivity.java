package com.mathildeprojet.mathtest;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;

import java.nio.channels.Channel;


public class WifiP2PActivity extends Activity {
    private WifiP2pManager mManager;
    private WifiP2Pconnection WifiConnection;
    private Button buttonFind;
    private Button buttonConnect;
    private BroadcastReceiver mReceiver = null;

    private Context context;
    private TextView blabla;
    private IntentFilter mIntentFilter = new IntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("NOUS", "on rentre bien dans OnCreate");
        setContentView(R.layout.activity_wifi_p2_p);
        context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Looper looper= getMainLooper();
        Log.v("NOUS", "avant");
        //blabla =new TextView(this);
        Log.v("NOUS", "milieu");

        //blabla.setText("hello je suis bien connecte");
        Log.v("NOUS", "apres");

        mReceiver=new WifiP2Pconnection(context,mManager,looper,mReceiver,this);
        //registerReceiver(mReceiver,mReceiver)
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



}
