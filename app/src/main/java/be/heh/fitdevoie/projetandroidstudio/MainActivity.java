package be.heh.fitdevoie.projetandroidstudio;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onMainClickManager(View v) {
        final int BT_MAIN_CONNEXION = R.id.bt_main_connexion;
        final int BT_MAIN_INSCRIPTION = R.id.bt_main_inscription;
        switch(v.getId()) {
            case BT_MAIN_CONNEXION:
                Intent toConnexion = new Intent(this, ConnexionActivity.class);
                startActivity(toConnexion);
                break;

            case BT_MAIN_INSCRIPTION:
                Intent toInscription = new Intent(this, InscriptionActivity.class);
                startActivity(toInscription);
                break;
        }
    }
}