package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.heh.fitdevoie.projetandroidstudio.R;

public class ComprimesActivity extends AppCompatActivity {

    Button bt_comprimes_read;
    RelativeLayout rl_comprimes_parametres;
    EditText et_comprimes_ip;
    EditText et_comprimes_rack;
    EditText et_comprimes_slot;
    TextView tv_comprimes_parametres_errorText;

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

    }

    public void onComprimesClickManager(View v) {
        switch(v.getId()) {
            case R.id.bt_comprimes_read:
                String paramError = "Veuillez remplir/corriger les erreurs suivantes :";
                Boolean ipOk = false;
                Boolean rackOk = false;
                Boolean slotOk = false;
                String ipAddress = "";

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
                }

                if(et_comprimes_slot.getText().toString().trim().length() == 0) {
                    slotOk = false;
                    paramError += "\n- Le champ Slot est vide";
                } else {
                    slotOk = true;
                }

                if(ipOk && rackOk && slotOk) {
                    bt_comprimes_read.setEnabled(false);
                    rl_comprimes_parametres.setVisibility(View.GONE);
                } else {
                    tv_comprimes_parametres_errorText.setText(paramError);
                    tv_comprimes_parametres_errorText.setVisibility(View.VISIBLE);
                    tv_comprimes_parametres_errorText.setMaxHeight(600);
                }

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent toChoix = new Intent(this, ChoixActivity.class);
                startActivity(toChoix);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void afterTextChanged(Editable s) {
        double doubleValue = 0;
        if (s != null) {
            try {
                doubleValue = Double.parseDouble(s.toString().replace(',', '.'));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}