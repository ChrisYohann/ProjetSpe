package com.example.rafaelle.wifip2p;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.Menu;
import android.view.MenuItem;

import java.nio.channels.Channel;


public class WifiP2PActivity extends ActionBarActivity {
    WifiP2pManager mManager;
    WifiP2Pconnection WifiConnection;
    BroadcastReceiver mReceiver;
    Context context;
    IntentFilter mIntentFilter = new IntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_p2_p);
        context = getApplicationContext();
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
}
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



}
