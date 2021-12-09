package be.heh.fitdevoie.projetandroidstudio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class InscriptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void onInscriptionClickManager(View v) {
        final int BT_INSCRIPTION = R.id.bt_inscription;
        final int BT_HOME = R.id.home;
        switch(v.getId()) {
            case BT_INSCRIPTION:
                System.out.println("Click sur inscription");
                break;
            case BT_HOME:
                Intent toMain = new Intent(this, MainActivity.class);
                startActivity(toMain);
                finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
