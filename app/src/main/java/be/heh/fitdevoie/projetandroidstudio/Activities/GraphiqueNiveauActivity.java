package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import be.heh.fitdevoie.projetandroidstudio.R;

public class GraphiqueNiveauActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphique_niveau);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Graphique de niveau");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent toNiveau = new Intent(this, NiveauActivity.class);
                startActivity(toNiveau);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}