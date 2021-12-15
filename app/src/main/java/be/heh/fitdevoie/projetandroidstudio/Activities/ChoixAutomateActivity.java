package be.heh.fitdevoie.projetandroidstudio.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import be.heh.fitdevoie.projetandroidstudio.R;

public class ChoixAutomateActivity extends Activity {

    Button bt_choixAutomate_comprimes;
    Button bt_choixAutomate_niveau;
    SharedPreferences prefs_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choix_automate);
        bt_choixAutomate_comprimes = (Button) findViewById(R.id.bt_choixAutomate_comprimes);
        bt_choixAutomate_niveau = (Button) findViewById(R.id.bt_choixAutomate_niveau);

        prefs_data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(this, "ID=" + prefs_data.getInt("userId", -1) + " RIGHTS=" + prefs_data.getInt("rights", -1), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onChoixAutomateClickManager(View v) {
        final int BT_COMPRIMES = R.id.bt_choixAutomate_comprimes;
        final int BT_NIVEAU = R.id.bt_choixAutomate_niveau;
        final int BT_DECONNEXION = R.id.bt_choixAutomate_deconnexion;
        switch (v.getId()) {
            case BT_DECONNEXION:
                SharedPreferences.Editor editeur_prefs = prefs_data.edit();
                editeur_prefs.putInt("rights", -1);
                editeur_prefs.putInt("userId", -1);
                editeur_prefs.commit();
                Intent toMain = new Intent(this, MainActivity.class);
                startActivity(toMain);
                finish();
                break;
        }
    }
}
