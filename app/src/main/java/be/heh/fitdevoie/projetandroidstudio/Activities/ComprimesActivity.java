package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import SimaticS7.S7;
import SimaticS7.S7Client;
import be.heh.fitdevoie.projetandroidstudio.R;
import be.heh.fitdevoie.projetandroidstudio.TaskS7.ReadTaskS7;

public class ComprimesActivity extends AppCompatActivity {

    Button bt_comprimes_read;
    RelativeLayout rl_comprimes_parametres;
    EditText et_comprimes_ip;
    EditText et_comprimes_rack;
    EditText et_comprimes_slot;
    TextView tv_comprimes_parametres_errorText;

    RelativeLayout rl_comprimes_read;
    LinearLayout ll_comprimes_flaconsVides_read;
    TextView tv_comprimes_flaconsVides_read;
    LinearLayout ll_comprimes_selecteurService_read;
    TextView tv_comprimes_selecteurService_read;
    LinearLayout ll_comprimes_nbComprimesSelectionne_read;
    TextView tv_comprimes_nbComprimesSelectionne_read;
    LinearLayout ll_comprimes_nbComprimes_read;
    TextView tv_comprimes_nbComprimes_read;
    Button bt_comprimes_read_disconnect;
    private ReadTaskS7 readS7;
    private NetworkInfo network;
    private ConnectivityManager connexStatus;
    private S7Client comS7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprimes);

        bt_comprimes_read = (Button) findViewById(R.id.bt_comprimes_read);
        rl_comprimes_parametres = (RelativeLayout) findViewById(R.id.rl_comprimes_parametres);
        et_comprimes_ip = (EditText) findViewById(R.id.et_comprimes_ip);
        et_comprimes_rack = (EditText) findViewById(R.id.et_comprimes_rack);
        et_comprimes_slot = (EditText) findViewById(R.id.et_comprimes_slot);
        tv_comprimes_parametres_errorText = (TextView) findViewById(R.id.tv_comprimes_parametres_errorText);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Conditionnement de comprimés");

        rl_comprimes_read = (RelativeLayout) findViewById(R.id.rl_comprimes_read);
        ll_comprimes_flaconsVides_read = (LinearLayout) findViewById(R.id.ll_comprimes_flaconsVides_read);
        tv_comprimes_flaconsVides_read = (TextView) findViewById(R.id.tv_comprimes_flaconsVides_read);
        ll_comprimes_selecteurService_read = (LinearLayout) findViewById(R.id.ll_comprimes_selecteurService_read);
        tv_comprimes_selecteurService_read = (TextView) findViewById(R.id.tv_comprimes_selecteurService_read);
        ll_comprimes_nbComprimesSelectionne_read = (LinearLayout) findViewById(R.id.ll_comprimes_nbComprimesSelectionne_read);
        tv_comprimes_nbComprimesSelectionne_read = (TextView) findViewById(R.id.tv_comprimes_nbComprimesSelectionne_read);
        ll_comprimes_nbComprimes_read = (LinearLayout) findViewById(R.id.ll_comprimes_nbComprimes_read);
        tv_comprimes_nbComprimes_read = (TextView) findViewById(R.id.tv_comprimes_nbComprimes_read);
        bt_comprimes_read_disconnect = (Button) findViewById(R.id.bt_comprimes_read_disconnect);
        connexStatus = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        network = connexStatus.getActiveNetworkInfo();

    }

    public void onComprimesClickManager(View v) {

        final int BT_COMPRIMES_READ = R.id.bt_comprimes_read;
        final int BT_COMPRIMES_READ_DISCONNECT = R.id.bt_comprimes_read_disconnect;

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        switch(v.getId()) {
            case BT_COMPRIMES_READ:
                String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                Boolean ipOk = false;
                Boolean rackOk = false;
                Boolean slotOk = false;
                String ipAddress = "";
                int rack = 0;
                int slot = 0;

                if(et_comprimes_ip.getText().toString().trim().length() == 0) {
                    ipOk = false;
                    paramError += "\n- Le champ IP est vide";
                } else {
                    String[] parts = et_comprimes_ip.getText().toString().split("\\.");
                    if(parts.length != 4) {
                        ipOk = false;
                        paramError += "\n- Le champ IP est incorrectement rempli, il doit être au format 192.168.10.134";
                    } else {
                        Boolean[] ipPartsOk = { false , false , false , false };
                        for(int i = 0 ; i < 4 ; i++) {
                            if(Integer.parseInt(parts[i]) < 0 || Integer.parseInt(parts[i]) > 255) {
                                ipPartsOk[i] = false;
                                paramError = "Veuillez remplir/corriger les erreurs suivantes :\n- L'adresse IP doit contenir des nombres compris entre 0 et 255";
                            } else {
                                ipPartsOk[i] = true;
                            }
                        }
                        if(ipPartsOk[0] && ipPartsOk[1] && ipPartsOk[2] && ipPartsOk[3]) {
                            ipOk = true;
                        } else {
                            ipOk = false;
                        }
                    }
                }

                if(et_comprimes_rack.getText().toString().trim().length() == 0) {
                    rackOk = false;
                    paramError += "\n- Le champ Rack est vide";
                } else {
                    rackOk = true;
                    rack = Integer.parseInt(et_comprimes_rack.getText().toString());
                }

                if(et_comprimes_slot.getText().toString().trim().length() == 0) {
                    slotOk = false;
                    paramError += "\n- Le champ Slot est vide";
                } else {
                    slotOk = true;
                    slot = Integer.parseInt(et_comprimes_slot.getText().toString());
                }

                if(ipOk && rackOk && slotOk) {

                    if(network != null && network.isConnectedOrConnecting()) {
                        readS7 = new ReadTaskS7();
                        readS7.Start(ipAddress, String.valueOf(rack), String.valueOf(slot));

                        bt_comprimes_read.setEnabled(false);
                        rl_comprimes_parametres.setVisibility(View.GONE);
                        rl_comprimes_read.setVisibility(View.VISIBLE);

                        timer.schedule(doTask, 0, 1000);

                    } else {
                        paramError = "La connexion réseau est impossible";
                        tv_comprimes_parametres_errorText.setText(paramError);
                        tv_comprimes_parametres_errorText.setVisibility(View.VISIBLE);
                        tv_comprimes_parametres_errorText.setMaxHeight(600);
                    }
                } else {
                    tv_comprimes_parametres_errorText.setText(paramError);
                    tv_comprimes_parametres_errorText.setVisibility(View.VISIBLE);
                    tv_comprimes_parametres_errorText.setMaxHeight(600);
                }

                break;

            case BT_COMPRIMES_READ_DISCONNECT:
                if(network != null && network.isConnectedOrConnecting()) {
                    readS7 = new ReadTaskS7();
                    readS7.Stop();

                    bt_comprimes_read.setEnabled(true);
                    rl_comprimes_parametres.setVisibility(View.VISIBLE);
                    rl_comprimes_read.setVisibility(View.GONE);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(network != null && network.isConnectedOrConnecting()) {
                    readS7 = new ReadTaskS7();
                    readS7.Stop();

                    bt_comprimes_read.setEnabled(true);
                    rl_comprimes_parametres.setVisibility(View.VISIBLE);
                    rl_comprimes_read.setVisibility(View.GONE);
                }
                Intent toChoix = new Intent(this, ChoixActivity.class);
                startActivity(toChoix);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}