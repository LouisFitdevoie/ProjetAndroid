package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.heh.fitdevoie.projetandroidstudio.R;

public class NiveauActivity extends AppCompatActivity {

    Button bt_comprimes;
    RelativeLayout rl_niveau_parametres;
    EditText et_niveau_ip;
    EditText et_niveau_rack;
    EditText et_niveau_slot;
    TextView tv_niveau_parametres_errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveau);
    }
}