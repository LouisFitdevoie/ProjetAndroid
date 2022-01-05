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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    RelativeLayout rl_comprimes_read;
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

    SharedPreferences prefs_data;

    Button bt_comprimes_write;
    RadioGroup rg_comprimes_dbbNumber;
    RadioButton rb_comprimes_DBB5;
    RadioButton rb_comprimes_DBB6;
    RadioButton rb_comprimes_DBB7;
    RadioButton rb_comprimes_DBB8;
    RadioButton rb_comprimes_DBB18;
    ArrayList<RadioButton> radioButtons = new ArrayList<>();
    EditText et_comprimes_valueToSend;
    RelativeLayout rl_comprimes_dataToWrite;
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

        rl_comprimes_read = (RelativeLayout) findViewById(R.id.rl_comprimes_read);
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
        rg_comprimes_dbbNumber = (RadioGroup) findViewById(R.id.rg_comprimes_dbbNumber);
        rb_comprimes_DBB5 = (RadioButton) findViewById(R.id.rb_comprimes_DBB5);
        rb_comprimes_DBB6 = (RadioButton) findViewById(R.id.rb_comprimes_DBB6);
        rb_comprimes_DBB7 = (RadioButton) findViewById(R.id.rb_comprimes_DBB7);
        rb_comprimes_DBB8 = (RadioButton) findViewById(R.id.rb_comprimes_DBB8);
        rb_comprimes_DBB18 = (RadioButton) findViewById(R.id.rb_comprimes_DBB18);
        et_comprimes_valueToSend = (EditText) findViewById(R.id.et_comprimes_valueToSend);

        radioButtons.add(rb_comprimes_DBB5);
        radioButtons.add(rb_comprimes_DBB6);
        radioButtons.add(rb_comprimes_DBB7);
        radioButtons.add(rb_comprimes_DBB8);
        radioButtons.add(rb_comprimes_DBB18);

        rl_comprimes_read.setVisibility(View.GONE);
        rl_comprimes_dataToWrite.setVisibility(View.GONE);
        bt_comprimes_write.setVisibility(View.GONE);
    }

    public void onComprimesClickManager(View v) {

        final int BT_COMPRIMES = R.id.bt_comprimes_connect;
        final int BT_COMPRIMES_WRITE = R.id.bt_comprimes_write;

        switch(v.getId()) {
            case BT_COMPRIMES:

                if(bt_comprimes.getText().toString().equals("CONNECT")) {
                    String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                    Boolean ipOk = false;
                    Boolean rackOk = false;
                    Boolean slotOk = false;

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
                    }

                    if (et_comprimes_slot.getText().toString().trim().length() == 0) {
                        slotOk = false;
                        paramError += "\n- Le champ Slot est vide";
                    } else {
                        slotOk = true;
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

                            if(bt_comprimes.getText().equals("CONNECT") || rl_comprimes_parametres.getVisibility() == View.GONE) {
                                rl_comprimes_parametres.setVisibility(View.GONE);
                                rl_comprimes_read.setVisibility(View.VISIBLE);

                                bt_comprimes.setText("DISCONNECT");
                                readS7 = new ReadTaskS7Comprimes(v,
                                        bt_comprimes,
                                        tv_comprimes_flaconsVides,
                                        tv_comprimes_selecteurService,
                                        tv_comprimes_nbComprimesSelectionne,
                                        tv_comprimes_nbComprimes,
                                        tv_comprimes_nbBouteillesRemplies,
                                        rl_comprimes_dataToWrite);
                                readS7.Start(et_comprimes_ip.getText().toString(),et_comprimes_rack.getText().toString(),et_comprimes_slot.getText().toString());
                            } else {
                                readS7.Stop();
                                bt_comprimes.setText("CONNECT");
                                rl_comprimes_read.setVisibility(View.GONE);
                                rl_comprimes_parametres.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(this, "Connexion réseau impossible !", Toast.LENGTH_SHORT).show();
                        }

                        if(prefs_data.getInt("rights", -1) == 0) {
                            bt_comprimes_write.setVisibility(View.VISIBLE);
                            bt_comprimes_write.setEnabled(true);
                            rl_comprimes_dataToWrite.setVisibility(View.VISIBLE);
                        } else {
                            bt_comprimes_write.setVisibility(View.GONE);
                            bt_comprimes_write.setEnabled(false);
                            rl_comprimes_dataToWrite.setVisibility(View.GONE);
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
                    et_comprimes_valueToSend.setText(null);

                    rl_comprimes_read.setVisibility(View.GONE);
                    rl_comprimes_parametres.setVisibility(View.VISIBLE);

                    if(rl_comprimes_dataToWrite.getVisibility() == View.VISIBLE) {

                        rl_comprimes_dataToWrite.setVisibility(View.GONE);
                    }
                }

                break;

            case BT_COMPRIMES_WRITE:
                Boolean writeOk = false;
                for(RadioButton rb : radioButtons) {
                    if(rb.isChecked()) {
                        writeOk = true;
                    }
                }
                if(et_comprimes_valueToSend.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Veuillez entrer une valeur", Toast.LENGTH_SHORT).show();
                } else if(!writeOk) {
                    Toast.makeText(this, "Veuillez choisir une DBB", Toast.LENGTH_SHORT).show();
                } else if (writeOk) {
                    writeTaskS7 = new WriteTaskS7();
                    writeTaskS7.Start(et_comprimes_ip.getText().toString(),et_comprimes_rack.getText().toString(),et_comprimes_slot.getText().toString());
                    int value = Integer.parseInt(et_comprimes_valueToSend.getText().toString());
                    if(rb_comprimes_DBB5.isChecked()) {
                        writeTaskS7.WriteByte(5,value);
                    } else if(rb_comprimes_DBB6.isChecked()) {
                        writeTaskS7.WriteByte(6,value);
                    } else if(rb_comprimes_DBB7.isChecked()) {
                        writeTaskS7.WriteByte(7,value);
                    } else if(rb_comprimes_DBB8.isChecked()) {
                        writeTaskS7.WriteByte(8,value);
                    } else if(rb_comprimes_DBB18.isChecked()) {
                        writeTaskS7.WriteByte(18,value);
                    }
                    writeTaskS7.Stop();
                }
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