package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import be.heh.fitdevoie.projetandroidstudio.R;

public class ComprimesActivity extends AppCompatActivity {

    Button bt_comprimes_read;
    ProgressBar pb_comprimes_connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprimes);

        bt_comprimes_read = (Button) findViewById(R.id.bt_comprimes_read);
        pb_comprimes_connexion = (ProgressBar) findViewById(R.id.pb_comprimes_connexion);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Conditionnement de comprimés");

    }

    public void onComprimesClickManager(View v) {
        switch(v.getId()) {
            case R.id.bt_comprimes_read:
                bt_comprimes_read.setEnabled(false);
                bt_comprimes_read.setVisibility(View.GONE);
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
}