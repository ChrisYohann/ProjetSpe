package com.mathildeprojet.mathtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class IntroActivity extends ActionBarActivity {
    Button valider;
    EditText pseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        valider = (Button) findViewById(R.id.buttonpseudo);
        pseudo = (EditText) findViewById(R.id.pseudo);


        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(this);



        //On donne un titre à l'AlertDialog
        adb.setTitle("Avertissement");
        adb.setMessage("Si jamais vous n'arrivez pas a recevoir/envoyer des messages, il se peut que la connexion se soit mal faite. Connectez-vous manuellement a votre correspondant dans les parametres Wi-Fi Direct");
        //Bouton du dialogue
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adb.show();





        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent secondeActivite = new Intent(IntroActivity.this, WifiP2PActivity.class);

                // On rajoute un extra
                secondeActivite.putExtra("pseudo",pseudo.getText().toString());

                // Puis on lance l'intent !
                startActivity(secondeActivite);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
