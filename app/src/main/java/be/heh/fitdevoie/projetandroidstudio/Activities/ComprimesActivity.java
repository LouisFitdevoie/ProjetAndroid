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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import SimaticS7.S7Client;
import be.heh.fitdevoie.projetandroidstudio.R;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.ReadTaskS7Comprimes;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.WriteTaskS7;

public class ComprimesActivity extends AppCompatActivity {

    Button bt_comprimes;
    RelativeLayout rl_comprimes_parametres;
    EditText et_comprimes_ip;
    EditText et_comprimes_rack;
    EditText et_comprimes_slot;
    TextView tv_comprimes_parametres_errorText;

    RelativeLayout rl_comprimes_RW;
    LinearLayout ll_comprimes_flaconsVides;
    TextView tv_comprimes_flaconsVides;
    LinearLayout ll_comprimes_selecteurService;
    TextView tv_comprimes_selecteurService;
    LinearLayout ll_comprimes_nbComprimesSelectionne;
    TextView tv_comprimes_nbComprimesSelectionne;
    LinearLayout ll_comprimes_nbComprimes;
    TextView tv_comprimes_nbComprimes;
    TextView tv_comprimes_nbBouteillesRemplies;
    private NetworkInfo network;
    private ConnectivityManager connexStatus;
    private S7Client comS7;
    View v;
    ReadTaskS7Comprimes readS7;
    WriteTaskS7 writeS7;

    SharedPreferences prefs_data;

    Button bt_comprimes_write;
    RelativeLayout rl_comprimes_dataToWrite;
    CheckBox cb_comprimes_flaconsVides;
    CheckBox cb_comprimes_selecteurService;
    CheckBox cb_comprimes_resetCompteur;
    RadioButton rb_comprimes_5demandes;
    RadioButton rb_comprimes_10demandes;
    RadioButton rb_comprimes_15demandes;
    WriteTaskS7 writeTaskS7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprimes);

        bt_comprimes = (Button) findViewById(R.id.bt_comprimes_connect);
        rl_comprimes_parametres = (RelativeLayout) findViewById(R.id.rl_comprimes_parametres);
        et_comprimes_ip = (EditText) findViewById(R.id.et_comprimes_ip);
        et_comprimes_rack = (EditText) findViewById(R.id.et_comprimes_rack);
        et_comprimes_slot = (EditText) findViewById(R.id.et_comprimes_slot);
        tv_comprimes_parametres_errorText = (TextView) findViewById(R.id.tv_comprimes_parametres_errorText);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Conditionnement de comprimés");

        rl_comprimes_RW = (RelativeLayout) findViewById(R.id.rl_comprimes_RW);
        ll_comprimes_flaconsVides = (LinearLayout) findViewById(R.id.ll_comprimes_flaconsVides);
        tv_comprimes_flaconsVides = (TextView) findViewById(R.id.tv_comprimes_flaconsVides);
        ll_comprimes_selecteurService = (LinearLayout) findViewById(R.id.ll_comprimes_selecteurService);
        tv_comprimes_selecteurService = (TextView) findViewById(R.id.tv_comprimes_selecteurService);
        ll_comprimes_nbComprimesSelectionne = (LinearLayout) findViewById(R.id.ll_comprimes_nbComprimesSelectionne);
        tv_comprimes_nbComprimesSelectionne = (TextView) findViewById(R.id.tv_comprimes_nbComprimesSelectionne);
        ll_comprimes_nbComprimes = (LinearLayout) findViewById(R.id.ll_comprimes_nbComprimes);
        tv_comprimes_nbComprimes = (TextView) findViewById(R.id.tv_comprimes_nbComprimes);
        tv_comprimes_nbBouteillesRemplies = (TextView) findViewById(R.id.tv_comprimes_nbBouteillesRemplies);
        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        bt_comprimes_write = (Button) findViewById(R.id.bt_comprimes_write);
        rl_comprimes_dataToWrite = (RelativeLayout) findViewById(R.id.rl_comprimes_dataToWrite);
        cb_comprimes_flaconsVides = (CheckBox) findViewById(R.id.cb_comprimes_flaconsVides);
        cb_comprimes_selecteurService = (CheckBox) findViewById(R.id.cb_comprimes_selecteurService);
        cb_comprimes_resetCompteur = (CheckBox) findViewById(R.id.cb_comprimes_resetCompteur);
        rb_comprimes_5demandes = (RadioButton) findViewById(R.id.rb_comprimes_5demandes);
        rb_comprimes_10demandes = (RadioButton) findViewById(R.id.rb_comprimes_10demandes);
        rb_comprimes_15demandes = (RadioButton) findViewById(R.id.rb_comprimes_15demandes);
    }

    public void onComprimesClickManager(View v) {

        final int BT_COMPRIMES = R.id.bt_comprimes_connect;
        final int BT_COMPRIMES_WRITE = R.id.bt_comprimes_write;
        final int CB_COMPRIMES_FLACONSVIDES = R.id.cb_comprimes_flaconsVides;
        final int CB_COMPRIMES_SELECTEURSERVICE = R.id.cb_comprimes_selecteurService;
        final int CB_COMPRIMES_RESETCOMPTEUR = R.id.cb_comprimes_resetCompteur;
        final int RB_COMPRIMES_5DEMANDES = R.id.rb_comprimes_5demandes;
        final int RB_COMPRIMES_10DEMANDES = R.id.rb_comprimes_10demandes;
        final int RB_COMPRIMES_15DEMANDES = R.id.rb_comprimes_15demandes;

        switch(v.getId()) {
            case BT_COMPRIMES:

                if(bt_comprimes.getText().toString().equals("CONNECT")) {
                    String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                    Boolean ipOk = false;
                    Boolean rackOk = false;
                    Boolean slotOk = false;
                    String ipAddress = "";
                    int rack = 0;
                    int slot = 0;

                    if (et_comprimes_ip.getText().toString().trim().length() == 0) {
                        ipOk = false;
                        paramError += "\n- Le champ IP est vide";
                    } else {
                        String[] parts = et_comprimes_ip.getText().toString().split("\\.");
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

                    if (et_comprimes_rack.getText().toString().trim().length() == 0) {
                        rackOk = false;
                        paramError += "\n- Le champ Rack est vide";
                    } else {
                        rackOk = true;
                        rack = Integer.parseInt(et_comprimes_rack.getText().toString());
                    }

                    if (et_comprimes_slot.getText().toString().trim().length() == 0) {
                        slotOk = false;
                        paramError += "\n- Le champ Slot est vide";
                    } else {
                        slotOk = true;
                        slot = Integer.parseInt(et_comprimes_slot.getText().toString());
                    }

                    if (ipOk && rackOk && slotOk) {

                        if(network != null && network.isConnectedOrConnecting()) {

                            if(prefs_data.getInt("rights", -1) == 0) {
                                bt_comprimes_write.setVisibility(View.VISIBLE);
                                bt_comprimes_write.setEnabled(true);
                            } else {
                                bt_comprimes_write.setVisibility(View.GONE);
                                bt_comprimes_write.setEnabled(false);
                            }

                            if(bt_comprimes.getText().equals("CONNECT")) {
                                rl_comprimes_parametres.setVisibility(View.GONE);
                                rl_comprimes_RW.setVisibility(View.VISIBLE);

                                Toast.makeText(this,network.getTypeName(),Toast.LENGTH_SHORT).show();
                                bt_comprimes.setText("DISCONNECT");
                                readS7 = new ReadTaskS7Comprimes(v,
                                        bt_comprimes,
                                        tv_comprimes_flaconsVides,
                                        tv_comprimes_selecteurService,
                                        tv_comprimes_nbComprimesSelectionne,
                                        tv_comprimes_nbComprimes,
                                        tv_comprimes_nbBouteillesRemplies,
                                        rl_comprimes_dataToWrite,
                                        cb_comprimes_flaconsVides,
                                        cb_comprimes_selecteurService,
                                        cb_comprimes_resetCompteur,
                                        rb_comprimes_5demandes,
                                        rb_comprimes_10demandes,
                                        rb_comprimes_15demandes);
                                readS7.Start(et_comprimes_ip.getText().toString(),et_comprimes_rack.getText().toString(),et_comprimes_slot.getText().toString());

                                try {
                                    Thread.sleep(1000);
                                } catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                readS7.Stop();
                                bt_comprimes.setText("CONNECT");
                                Toast.makeText(getApplicationContext(),"Traitement interrompu par l'utilisateur !",Toast.LENGTH_LONG).show();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                rl_comprimes_RW.setVisibility(View.GONE);
                                rl_comprimes_parametres.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(this, "Connexion réseau impossible !", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        tv_comprimes_parametres_errorText.setText(paramError);
                        tv_comprimes_parametres_errorText.setVisibility(View.VISIBLE);
                        tv_comprimes_parametres_errorText.setMaxHeight(600);
                    }
                } else {
                    readS7.Stop();
                    bt_comprimes.setText("CONNECT");
                    bt_comprimes_write.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Traitement interrompu par l'utilisateur !",Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rl_comprimes_RW.setVisibility(View.GONE);
                    rl_comprimes_parametres.setVisibility(View.VISIBLE);

                    if(rl_comprimes_dataToWrite.getVisibility() == View.VISIBLE) {
                        writeTaskS7.Stop();
                        rl_comprimes_dataToWrite.setVisibility(View.GONE);
                        bt_comprimes_write.setText("Stop writing");
                    }
                }
                break;

            case BT_COMPRIMES_WRITE:
                if(bt_comprimes_write.getText().toString().equals("Start writing")) {
                    rl_comprimes_dataToWrite.setVisibility(View.VISIBLE);
                    bt_comprimes_write.setText("Stop writing");
                    writeTaskS7 = new WriteTaskS7();
                    writeTaskS7.Start(et_comprimes_ip.getText().toString(),et_comprimes_rack.getText().toString(),et_comprimes_slot.getText().toString());
                } else {
                    rl_comprimes_dataToWrite.setVisibility(View.GONE);
                    bt_comprimes_write.setText("Start writing");
                    writeTaskS7.Stop();
                }
                break;

            case CB_COMPRIMES_SELECTEURSERVICE:
                int selecteurService = cb_comprimes_selecteurService.isChecked() ? 1 : 0;
                writeTaskS7.setWriteBool(0, 0, selecteurService);
                break;

            case CB_COMPRIMES_FLACONSVIDES:
                int flaconsVidesToWrite = cb_comprimes_flaconsVides.isChecked() ? 1 : 0;
                writeTaskS7.setWriteBool(1,3, flaconsVidesToWrite);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(rl_comprimes_parametres.getVisibility() == View.VISIBLE) {
                    Intent toChoix = new Intent(this, ChoixActivity.class);
                    startActivity(toChoix);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}