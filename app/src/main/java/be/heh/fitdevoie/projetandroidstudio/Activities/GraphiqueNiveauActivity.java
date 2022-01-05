package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.R;

public class GraphiqueNiveauActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphique_niveau);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Graphique de niveau");

        Bundle savedValuesBundle = getIntent().getExtras();
        ArrayList<Integer> savedValues = savedValuesBundle.getIntegerArrayList("savedValues");

        GraphView graph = (GraphView) findViewById(R.id.gv_niveau);
        LineGraphSeries<DataPoint> serie = new LineGraphSeries<>();

        int level = 0;
        for(int i = 0 ; i < savedValues.size() ; i++) {
            level = savedValues.get(i);
            serie.appendData(new DataPoint(i * 0.5, level), true, 100000);
        }
        graph.addSeries(serie);
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