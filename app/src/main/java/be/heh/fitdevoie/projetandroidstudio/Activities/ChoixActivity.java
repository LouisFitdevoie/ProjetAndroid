package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import be.heh.fitdevoie.projetandroidstudio.R;

public class ChoixActivity extends Activity {

    Button bt_choix_comprimes;
    Button bt_choix_niveau;
    Button bt_choix_profil;
    Button bt_choix_autoriserEcriture;
    Button bt_choix_deconnexion;
    SharedPreferences prefs_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choix);
        bt_choix_comprimes = (Button) findViewById(R.id.bt_choix_comprimes);
        bt_choix_niveau = (Button) findViewById(R.id.bt_choix_niveau);
        bt_choix_profil = (Button) findViewById(R.id.bt_choix_profil);
        bt_choix_autoriserEcriture = (Button) findViewById(R.id.bt_choix_autoriserEcriture);
        bt_choix_deconnexion = (Button) findViewById(R.id.bt_choix_deconnexion);

        //Récupération des Shared Preferences avec les droits et l'ID de l'utilisateur
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Si l'utilisateur a les droits d'administration -> affiche et active le bouton qui dirige vers AutoriserEcritureActivity
        if(prefs_data.getInt("rights", -1) == 0) {
            bt_choix_autoriserEcriture.setEnabled(true);
            bt_choix_autoriserEcriture.setVisibility(View.VISIBLE);
        } else {
            bt_choix_autoriserEcriture.setEnabled(false);
            bt_choix_autoriserEcriture.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onChoixClickManager(View v) {
        final int BT_COMPRIMES = R.id.bt_choix_comprimes;
        final int BT_NIVEAU = R.id.bt_choix_niveau;
        final int BT_PROFIL = R.id.bt_choix_profil;
        final int BT_AUTHORIZEWRITE = R.id.bt_choix_autoriserEcriture;
        final int BT_DECONNEXION = R.id.bt_choix_deconnexion;

        switch (v.getId()) {
            //Au clic sur le bouton "Comprimés" -> renvoie vers ComprimesActivity
            case BT_COMPRIMES:
                Intent toComprimes = new Intent(this, ComprimesActivity.class);
                startActivity(toComprimes);
                finish();
                break;
            //Au clic sur le bouton "Niveau" -> renvoie vers NiveauActivity
            case BT_NIVEAU:
                Intent toNiveau = new Intent(this, NiveauActivity.class);
                startActivity(toNiveau);
                finish();
                break;
            //Au clic sur le bouton "Profil" -> renvoie vers EditProfileActivity
            case BT_PROFIL:
                Intent toProfile = new Intent(this, EditProfileActivity.class);
                startActivity(toProfile);
                finish();
                break;
            //Au clic sur le bouton "AuthorizeWrite" -> renvoie vers AutoriserEcritureActivity
            case BT_AUTHORIZEWRITE:
                Intent toAuthorizeWrite = new Intent(this, AutoriserEcritureActivity.class);
                startActivity(toAuthorizeWrite);
                finish();
                break;
            //Au clic sur le bouton "Deconnexion"
            case BT_DECONNEXION:
                //Crée une intent vers MainActivity
                Intent toMain = new Intent(this, MainActivity.class);
                //AlertDialog qui demande si on veut vraiment se déconnecter
                AlertDialog.Builder alertDialogDeconnexion = new AlertDialog.Builder(this);
                alertDialogDeconnexion.setMessage("Voulez-vous vraiment vous déconnecter ?");
                //Si clic sur "Oui" -> renvoie vers MainActivity et mets à -1 les droits et l'ID de l'utilisateur dans les SharedPreferences
                alertDialogDeconnexion.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editeur_prefs = prefs_data.edit();
                        editeur_prefs.putInt("rights", -1);
                        editeur_prefs.putInt("userId", -1);
                        editeur_prefs.commit();
                        startActivity(toMain);
                        finish();
                    }
                });
                //Si clic sur "Non" -> affiche "Déconnexion annulée"
                alertDialogDeconnexion.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Déconnexion annulée", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialogDeconnexion.setCancelable(false);
                //Affiche l'AlertDialog
                AlertDialog alert = alertDialogDeconnexion.create();
                alert.show();
                break;
        }
    }
}
