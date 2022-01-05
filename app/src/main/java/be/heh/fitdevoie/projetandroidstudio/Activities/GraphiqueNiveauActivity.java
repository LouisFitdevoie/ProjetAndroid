package be.heh.fitdevoie.projetandroidstudio.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
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
        serie.setTitle("Niveau de liquide");
        serie.setColor(Color.parseColor("#3800ff"));
        graph.setTitle("Niveau de liquide en fonction du temps");
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(50);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxY(1000);
        graph.getViewport().setMinY(0);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.addSeries(serie);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


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