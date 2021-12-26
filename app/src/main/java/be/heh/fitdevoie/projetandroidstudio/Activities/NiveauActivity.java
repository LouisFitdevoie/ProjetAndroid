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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import SimaticS7.S7Client;
import be.heh.fitdevoie.projetandroidstudio.R;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.ReadTaskS7Comprimes;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.ReadTaskS7Niveau;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.WriteTaskS7;

public class NiveauActivity extends AppCompatActivity {

    Button bt_niveau;
    RelativeLayout rl_niveau_parametres;
    EditText et_niveau_ip;
    EditText et_niveau_rack;
    EditText et_niveau_slot;
    TextView tv_niveau_parametres_errorText;

    RelativeLayout rl_niveau_RW;
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
    private S7Client comS7;
    View v;
    ReadTaskS7Niveau readS7;
    WriteTaskS7 writeS7;

    SharedPreferences prefs_data;

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

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Régulation de niveau de liquide");

        rl_niveau_RW = (RelativeLayout) findViewById(R.id.rl_niveau_RW);
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
    }

    public void onNiveauClickManager(View v) {

        final int BT_NIVEAU = R.id.bt_niveau;

        switch(v.getId()) {
            case BT_NIVEAU:
                if(bt_niveau.getText().toString().equals("CONNECT")) {
                    String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                    Boolean ipOk = false;
                    Boolean rackOk = false;
                    Boolean slotOk = false;
                    String ipAddress = "";
                    int rack = 0;
                    int slot = 0;

                    if (et_niveau_ip.getText().toString().trim().length() == 0) {
                        ipOk = false;
                        paramError += "\n- Le champ IP est vide";
                    } else {
                        String[] parts = et_niveau_ip.getText().toString().split("\\.");
                        if (parts.length != 4) {
                            ipOk = false;
                            paramError += "\n- Le champ IP est incorrectement rempli, il doit être au format 192.168.10.134";
                        } else {
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

                    if (et_niveau_rack.getText().toString().trim().length() == 0) {
                        rackOk = false;
                        paramError += "\n- Le champ Rack est vide";
                    } else {
                        rackOk = true;
                        rack = Integer.parseInt(et_niveau_rack.getText().toString());
                    }

                    if (et_niveau_slot.getText().toString().trim().length() == 0) {
                        slotOk = false;
                        paramError += "\n- Le champ Slot est vide";
                    } else {
                        slotOk = true;
                        slot = Integer.parseInt(et_niveau_slot.getText().toString());
                    }

                    if (ipOk && rackOk && slotOk) {

                        if(network != null && network.isConnectedOrConnecting()) {
                            if(bt_niveau.getText().equals("CONNECT")) {
                                rl_niveau_parametres.setVisibility(View.GONE);
                                rl_niveau_RW.setVisibility(View.VISIBLE);

                                Toast.makeText(this,network.getTypeName(),Toast.LENGTH_SHORT).show();
                                bt_niveau.setText("DISCONNECT");
                                readS7 = new ReadTaskS7Niveau(v, bt_niveau, tv_niveau_selecteurMode, tv_niveau_niveauLiquide, tv_niveau_niveauConsigneAuto, tv_niveau_niveauConsigneManuel, tv_niveau_sortie, tv_niveau_valve1, tv_niveau_valve2, tv_niveau_valve3, tv_niveau_valve4);
                                readS7.Start(et_niveau_ip.getText().toString(),et_niveau_rack.getText().toString(),et_niveau_slot.getText().toString());

                                try {
                                    Thread.sleep(1000);
                                } catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                readS7.Stop();
                                bt_niveau.setText("CONNECT");
                                Toast.makeText(getApplicationContext(),"Traitement interrompu par l'utilisateur !",Toast.LENGTH_LONG).show();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                rl_niveau_RW.setVisibility(View.GONE);
                                rl_niveau_parametres.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(this, "Connexion réseau impossible !", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        tv_niveau_parametres_errorText.setText(paramError);
                        tv_niveau_parametres_errorText.setVisibility(View.VISIBLE);
                        tv_niveau_parametres_errorText.setMaxHeight(600);
                    }
                } else {
                    readS7.Stop();
                    bt_niveau.setText("CONNECT");
                    Toast.makeText(getApplicationContext(),"Traitement interrompu par l'utilisateur !",Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rl_niveau_RW.setVisibility(View.GONE);
                    rl_niveau_parametres.setVisibility(View.VISIBLE);
                }


                break;
        }
    }

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
}