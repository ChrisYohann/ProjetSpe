package com.mathildeprojet.mathtest;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;



public class WifiP2PActivity extends Activity implements AdapterView.OnItemClickListener {
    private WifiP2pManager mManager;
    private Button buttonFind;
    private Channel channel;
    private Button buttonConnect;
    private WifiP2Pconnection mReceiver = null;
    private Context context;
    private TextView blabla;
    private IntentFilter filtre = new IntentFilter();


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
        Log.v("NOUS", "avant les mIntenderF");
//on d�finit les actions du filtres, on ne s'occupe que de ces actions
        filtre.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filtre.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        Log.v("NOUS", "apres les mIntenderF");
        this.channel = (WifiP2pManager.Channel) mManager.initialize(context, looper, null);
        //j'initialise la connection
        Log.v("NOUS", "apres init canal");
        mReceiver=new WifiP2Pconnection(context,mManager,channel,this);
        //registerReceiver(mReceiver,mReceiver)
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

    public void startScan(View v){
        if(((Button)findViewById(R.id.bouton)).getText().equals("Start Scanning")){
            mReceiver.startDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Stop Scanning");
        }else{
            mReceiver.stopDiscovery();
            ((Button)findViewById(R.id.bouton)).setText("Start Scanning");
        }
    }

    public void closeConnections(View v){
        mReceiver.closeConnections();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mReceiver.tryConnection(position);
    }
}
