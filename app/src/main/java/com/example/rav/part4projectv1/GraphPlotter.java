package com.example.rav.part4projectv1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Random;

public class GraphPlotter extends Activity {
    private Button appendButton;
    LineGraphSeries<DataPoint> series;
    int x_increment = 11;
    int y_increment = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_plotter);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        appendButton = (Button) findViewById(R.id.append);


        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(6, 11),
                new DataPoint(7, 1),
                new DataPoint(8, 5),
                new DataPoint(9, 3),
                new DataPoint(10, 2),
                new DataPoint(11, 6)
        });

        graph.addSeries(series);

        //graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.5);
        graph.getViewport().setMaxX(3.5);

        //graph.getGridLabelRenderer().setNumVerticalLabels(1);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Volts");

        //graph.getGridLabelRenderer().setNumHorizontalLabels(2);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.setTitle("Battery Voltage");
        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(GraphPlotter.this, "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        appendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                x_increment++;
                y_increment++;
                System.out.println("appendButton Button Clicked");

                series.appendData(new DataPoint(x_increment, y_increment), true, 100);

            }
        });
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            Random mRand = new Random();
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph_plotter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
