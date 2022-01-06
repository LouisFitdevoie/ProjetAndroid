package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import be.heh.fitdevoie.projetandroidstudio.R;

public class MainActivity extends Activity {

    //Initialisation des SharedPreferences
    SharedPreferences prefs_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Récupération des Shared Preferences avec les droits et l'ID de l'utilisateur
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            int rights = prefs_data.getInt("rights", -1);
            //Si les droits enregistrés dans les SharedPreferences -> réinitialisation des valeurs d'ID et des droits des SharedPreferences
            if(rights != -1) {
                SharedPreferences.Editor editeur_prefs = prefs_data.edit();
                editeur_prefs.putInt("rights", -1);
                editeur_prefs.putInt("userId", -1);
                editeur_prefs.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMainClickManager(View v) {

        final int BT_MAIN_CONNEXION = R.id.bt_main_connexion;
        final int BT_MAIN_INSCRIPTION = R.id.bt_main_inscription;

        switch(v.getId()) {
            //Au clic sur le bouton de connexion -> redirige vers ConnexionActivity
            case BT_MAIN_CONNEXION:
                Intent toConnexion = new Intent(this, ConnexionActivity.class);
                startActivity(toConnexion);
                break;

            //Au clic sur le bouton d'inscription -> redirige vers InscriptionActivity
            case BT_MAIN_INSCRIPTION:
                Intent toInscription = new Intent(this, InscriptionActivity.class);
                startActivity(toInscription);
                break;
        }
    }
}