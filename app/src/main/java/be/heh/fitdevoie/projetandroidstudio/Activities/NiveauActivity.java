package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.R;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.ReadTaskS7Niveau;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.WriteTaskS7;

public class NiveauActivity extends AppCompatActivity {

    Button bt_niveau;
    RelativeLayout rl_niveau_parametres;
    EditText et_niveau_ip;
    EditText et_niveau_rack;
    EditText et_niveau_slot;
    TextView tv_niveau_parametres_errorText;

    RelativeLayout rl_niveau_read;
    TextView tv_niveau_selecteurMode;
    TextView tv_niveau_niveauLiquide;
    TextView tv_niveau_niveauConsigneAuto;
    TextView tv_niveau_niveauConsigneManuel;
    TextView tv_niveau_sortie;
    TextView tv_niveau_valve1;
    TextView tv_niveau_valve2;
    TextView tv_niveau_valve3;
    TextView tv_niveau_valve4;

    private NetworkInfo network;
    private ConnectivityManager connexStatus;

    View v;
    ReadTaskS7Niveau readS7;

    SharedPreferences prefs_data;

    Button bt_niveau_write;
    RadioGroup rg_niveau_dbbNumber;
    RadioButton rb_niveau_DBB2;
    RadioButton rb_niveau_DBB3;
    RadioButton rb_niveau_DBB24;
    RadioButton rb_niveau_DBB26;
    RadioButton rb_niveau_DBB28;
    RadioButton rb_niveau_DBB30;
    ArrayList<RadioButton> radioButtons = new ArrayList<>();
    EditText et_niveau_valueToSend;
    RelativeLayout rl_niveau_dataToWrite;
    WriteTaskS7 writeTaskS7;

    Button bt_niveau_toGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveau);

        bt_niveau = (Button) findViewById(R.id.bt_niveau);
        rl_niveau_parametres = (RelativeLayout) findViewById(R.id.rl_niveau_parametres);
        et_niveau_ip = (EditText) findViewById(R.id.et_niveau_ip);
        et_niveau_rack = (EditText) findViewById(R.id.et_niveau_rack);
        et_niveau_slot = (EditText) findViewById(R.id.et_niveau_slot);
        tv_niveau_parametres_errorText = (TextView) findViewById(R.id.tv_niveau_parametres_errorText);

        //Affichage d'une barre d'action avec tite + bouton retour vers ChoixActivity
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Régulation de niveau de liquide");

        rl_niveau_read = (RelativeLayout) findViewById(R.id.rl_niveau_read);
        tv_niveau_selecteurMode = (TextView) findViewById(R.id.tv_niveau_selecteurMode);
        tv_niveau_niveauLiquide = (TextView) findViewById(R.id.tv_niveau_niveauLiquide);
        tv_niveau_niveauConsigneAuto = (TextView) findViewById(R.id.tv_niveau_niveauConsigneAuto);
        tv_niveau_niveauConsigneManuel = (TextView) findViewById(R.id.tv_niveau_niveauConsigneManuel);
        tv_niveau_sortie = (TextView) findViewById(R.id.tv_niveau_sortie);
        tv_niveau_valve1 = (TextView) findViewById(R.id.tv_niveau_valve1);
        tv_niveau_valve2 = (TextView) findViewById(R.id.tv_niveau_valve2);
        tv_niveau_valve3 = (TextView) findViewById(R.id.tv_niveau_valve3);
        tv_niveau_valve4 = (TextView) findViewById(R.id.tv_niveau_valve4);

        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();
        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        bt_niveau_write = (Button) findViewById(R.id.bt_niveau_write);
        rl_niveau_dataToWrite = (RelativeLayout) findViewById(R.id.rl_niveau_dataToWrite);
        rg_niveau_dbbNumber = (RadioGroup) findViewById(R.id.rg_niveau_dbbnumber);
        rb_niveau_DBB2 = (RadioButton) findViewById(R.id.rb_niveau_DBB2);
        rb_niveau_DBB3 = (RadioButton) findViewById(R.id.rb_niveau_DBB3);
        rb_niveau_DBB24 = (RadioButton) findViewById(R.id.rb_niveau_DBB24);
        rb_niveau_DBB26 = (RadioButton) findViewById(R.id.rb_niveau_DBB26);
        rb_niveau_DBB28 = (RadioButton) findViewById(R.id.rb_niveau_DBB28);
        rb_niveau_DBB30 = (RadioButton) findViewById(R.id.rb_niveau_DBB30);
        et_niveau_valueToSend = (EditText) findViewById(R.id.et_niveau_valueToSend);

        //Ajout des RadioButtons dans la liste
        radioButtons.add(rb_niveau_DBB2);
        radioButtons.add(rb_niveau_DBB3);
        radioButtons.add(rb_niveau_DBB24);
        radioButtons.add(rb_niveau_DBB26);
        radioButtons.add(rb_niveau_DBB28);
        radioButtons.add(rb_niveau_DBB30);

        //Masque les RL qui affiche les données lues et qui permettent d'écrire dans les DBB de l'automate
        rl_niveau_read.setVisibility(View.GONE);
        rl_niveau_dataToWrite.setVisibility(View.GONE);
        bt_niveau_write.setVisibility(View.GONE);

        //Masque le bouton qui redirige vers le graphique du niveau de liquide
        bt_niveau_toGraph = (Button) findViewById(R.id.bt_niveau_toGraph);
        bt_niveau_toGraph.setVisibility(View.GONE);
    }

    public void onNiveauClickManager(View v) {

        final int BT_NIVEAU_CONNECT = R.id.bt_niveau;
        final int BT_NIVEAU_WRITE = R.id.bt_niveau_write;
        final int BT_NIVEAU_TOGRAPH = R.id.bt_niveau_toGraph;

        switch(v.getId()) {

            //Au clic sur le bouton de connexion
            case BT_NIVEAU_CONNECT:
                //Si le texte du bouton est "CONNECT"
                if(bt_niveau.getText().toString().equals("CONNECT")) {
                    String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                    Boolean ipOk = false;
                    Boolean rackOk = false;
                    Boolean slotOk = false;

                    //Vérifie que le champ adresse IP soit complété
                    if (et_niveau_ip.getText().toString().trim().length() == 0) {
                        ipOk = false;
                        paramError += "\n- Le champ IP est vide";
                    } else {
                        //Si le champ est rempli -> sépare l'adresse IP en 4 parties avec le caractère "."
                        String[] parts = et_niveau_ip.getText().toString().split("\\.");

                        //S'il n'y a pas 4 parties
                        if (parts.length != 4) {
                            ipOk = false;
                            paramError += "\n- Le champ IP est incorrectement rempli, il doit être au format 192.168.10.134";
                        } else {
                            //Sinon -> vérifie que les 4 parties soient des nombres compris entre 0 et 255
                            Boolean[] ipPartsOk = {false, false, false, false};
                            for (int i = 0; i < 4; i++) {
                                if (Integer.parseInt(parts[i]) < 0 || Integer.parseInt(parts[i]) > 255) {
                                    ipPartsOk[i] = false;
                                    paramError = "Veuillez remplir/corriger les erreurs suivantes :\n- L'adresse IP doit contenir des nombres compris entre 0 et 255";
                                } else {
                                    ipPartsOk[i] = true;
                                }
                            }

                            if (ipPartsOk[0] && ipPartsOk[1] && ipPartsOk[2] && ipPartsOk[3]) {
                                ipOk = true;
                            } else {
                                ipOk = false;
                            }
                        }
                    }

                    //Vérifie si le champ Rack est complété
                    if (et_niveau_rack.getText().toString().trim().length() == 0) {
                        rackOk = false;
                        paramError += "\n- Le champ Rack est vide";
                    } else {
                        rackOk = true;
                    }

                    //Vérifie si le champ Slot est complété
                    if (et_niveau_slot.getText().toString().trim().length() == 0) {
                        slotOk = false;
                        paramError += "\n- Le champ Slot est vide";
                    } else {
                        slotOk = true;
                    }

                    //Si tous les champs sont remplis correctement
                    if (ipOk && rackOk && slotOk) {

                        //Vérifie si le réseau est connecté ou entrain de se connecter
                        if(network != null && network.isConnectedOrConnecting()) {

                            //Masque les paramètres de connexion et affiche le bouton du graphique et le layout de lecture
                            rl_niveau_parametres.setVisibility(View.GONE);
                            rl_niveau_read.setVisibility(View.VISIBLE);
                            bt_niveau_toGraph.setVisibility(View.VISIBLE);

                            //Change le texte du bouton de connexion
                            bt_niveau.setText("DISCONNECT");
                            readS7 = new ReadTaskS7Niveau(v,
                                    bt_niveau,
                                    tv_niveau_selecteurMode,
                                    tv_niveau_niveauLiquide,
                                    tv_niveau_niveauConsigneAuto,
                                    tv_niveau_niveauConsigneManuel,
                                    tv_niveau_sortie,
                                    tv_niveau_valve1,
                                    tv_niveau_valve2,
                                    tv_niveau_valve3,
                                    tv_niveau_valve4,
                                    rl_niveau_dataToWrite);
                            readS7.Start(et_niveau_ip.getText().toString(),et_niveau_rack.getText().toString(),et_niveau_slot.getText().toString());
                        } else {
                            Toast.makeText(this, "Connexion réseau impossible !", Toast.LENGTH_SHORT).show();
                        }

                        //Vérifie les droits de l'utilisateur
                        if(prefs_data.getInt("rights", -1) == 0) {
                            //Si il est admin -> affiche le bouton d'écriture et l'active + affiche le layout d'écriture
                            bt_niveau_write.setVisibility(View.VISIBLE);
                            bt_niveau_write.setEnabled(true);
                            rl_niveau_dataToWrite.setVisibility(View.VISIBLE);
                        } else {
                            //Si il est admin -> masque le bouton d'écriture et l'active + masque le layout d'écriture
                            bt_niveau_write.setVisibility(View.GONE);
                            bt_niveau_write.setEnabled(false);
                            rl_niveau_dataToWrite.setVisibility(View.GONE);
                        }

                    } else {
                        //Affiche message d'erreur
                        tv_niveau_parametres_errorText.setText(paramError);
                        tv_niveau_parametres_errorText.setVisibility(View.VISIBLE);
                    }
                } else {
                    //Arrête la lecture
                    //+ change texte du bouton
                    //+ vide le texte de la valeur à envoyer
                    //+ masque bouton d'affichage de graphique et layout de lecture
                    //+ affiche les paramètres de connexion
                    readS7.Stop();

                    bt_niveau.setText("CONNECT");
                    et_niveau_valueToSend.setText(null);

                    rl_niveau_read.setVisibility(View.GONE);
                    rl_niveau_parametres.setVisibility(View.VISIBLE);
                    bt_niveau_toGraph.setVisibility(View.GONE);

                    if(rl_niveau_dataToWrite.getVisibility() == View.VISIBLE) {
                        //Si le layout d'écriture est visible
                        //-> masque le layout et le bouton d'écriture
                        rl_niveau_dataToWrite.setVisibility(View.GONE);
                        bt_niveau_write.setVisibility(View.GONE);
                    }
                }

                break;

            //Au clic sur le bouton d'écriture
            case BT_NIVEAU_WRITE:
                Boolean writeOk = false;
                //Vérifie si un des RadioButtons est sélectionné ou pas
                for(RadioButton rb : radioButtons) {
                    if(rb.isChecked()) {
                        writeOk = true;
                    }
                }
                //Vérifie si le champ de la valeur à envoyer est rempli ou pas
                if(et_niveau_valueToSend.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Veuillez entrer une valeur", Toast.LENGTH_SHORT).show();
                //Vérifie si aucune des DBB est choisie
                } else if(!writeOk) {
                    Toast.makeText(this, "Veuillez choisir une DBB", Toast.LENGTH_SHORT).show();
                //Si une DBB est choisie
                } else if (writeOk) {
                    writeTaskS7 = new WriteTaskS7();
                    writeTaskS7.Start(et_niveau_ip.getText().toString(),et_niveau_rack.getText().toString(),et_niveau_slot.getText().toString());
                    int value = Integer.parseInt(et_niveau_valueToSend.getText().toString());

                    //Vérifie quel RadioButton est checked et envoie la valeur dans l'automate
                    if(rb_niveau_DBB2.isChecked()) {
                        writeTaskS7.WriteByte(2, value);
                    } else if(rb_niveau_DBB3.isChecked()) {
                        writeTaskS7.WriteByte(3, value);
                    } else if(rb_niveau_DBB24.isChecked()) {
                        writeTaskS7.WriteByte(24, value);
                    } else if(rb_niveau_DBB26.isChecked()) {
                        writeTaskS7.WriteByte(26,value);
                    } else if(rb_niveau_DBB28.isChecked()) {
                        writeTaskS7.WriteByte(28, value);
                    } else if(rb_niveau_DBB30.isChecked()) {
                        writeTaskS7.WriteByte(30, value);
                    }
                    writeTaskS7.Stop();
                }
                break;

            //Au clic sur le bouton du graphique
            case BT_NIVEAU_TOGRAPH:
                //Récupère toutes les valeurs de niveau de liquide lues depuis la connexion à l'automate
                ArrayList<Integer> savedValues = readS7.getSavedValues();
                //Arrête la lecture
                readS7.Stop();

                //Intent vers GraphiqueNiveauActivity en ajoutant les données sauvegardées dans l'Intent
                Intent toGraph = new Intent(this, GraphiqueNiveauActivity.class);
                Bundle valuesToSend = new Bundle();
                valuesToSend.putIntegerArrayList("savedValues", savedValues);
                toGraph.putExtras(valuesToSend);
                startActivity(toGraph);

                break;
        }
    }

    //Si l'utilisateur clique sur le bouton de retour de la barre d'action -> redirige vers ChoixActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(rl_niveau_parametres.getVisibility() == View.VISIBLE) {
                    Intent toChoix = new Intent(this, ChoixActivity.class);
                    startActivity(toChoix);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Si l'utilisateur clique sur le bouton de retour natif (triangle vers la gauche) -> redirige vers ChoixActivity
    @Override
    public void onBackPressed() {
        if(rl_niveau_parametres.getVisibility() == View.VISIBLE) {
            Intent toChoix = new Intent(this, ChoixActivity.class);
            startActivity(toChoix);
            finish();
        }
        return;
    }
}