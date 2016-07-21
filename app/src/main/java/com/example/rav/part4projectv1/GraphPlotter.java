package com.example.rav.part4projectv1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class GraphPlotter extends Activity {
    private Button appendButton;
    LineGraphSeries<DataPoint> series;
    int x_increment = 11;
    int y_increment = 0;
    double xAxisTime = 00.15;
   Date d5;
    Date datePoint;
    Calendar calendar;
    String parametersLocation = "https://bottling-station-firebase.firebaseio.com/locations/";
    private Firebase ref;
    Map<Object, Object> signLocationFromDB;
    Map<Object, Object> voltageMap;
//    List<Float> voltageValues = new ArrayList<Float>();
//    List<String> voltageTimes = new ArrayList<String>();
    Float voltageValues;
    String voltageTimes;
    GraphView graph;
     String noOfGraphEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_graph_plotter);
        graph = (GraphView) findViewById(R.id.graph);
        appendButton = (Button) findViewById(R.id.append);
        Bundle extras = getIntent().getExtras();
        noOfGraphEntries= extras.getString("noOfGraphEntries");

//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//                noOfGraphEntries= null;
//
//            } else {
//                noOfGraphEntries= extras.getString("noOfGraphEntries");
//            }
//        } else {
//            noOfGraphEntries= (String) savedInstanceState.getSerializable("noOfGraphEntries");
//        }
        System.out.println("noOfGraphEntries GraphPlotter " + noOfGraphEntries);//PrbolemHereWHenComingBckFromGraphPlotter
        calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        System.out.println("Date:d1  " + d1);
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        System.out.println("Date:d2  " + d2);
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        System.out.println("Date:d3  " + d3);
        String s = "2016:07:23:13:33:20"; // 07/23/2016 17:16:20
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss"); // MM/dd/yyyy HH:mm:ss
        try
        {
            d5 = simpleDateFormat.parse(s);
            // System.out.println("date : "+simpleDateFormat.format(d5));
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
        System.out.println("Date:d5  " + d5);
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
//        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//                new DataPoint(d1, 1),
//                new DataPoint(d2, 5.01),
//                new DataPoint(d3, 3),
//                new DataPoint(d5, 2)
//        });
        graph.addSeries(series);

        calendar = Calendar.getInstance();
       // Date d2 = calendar.getTime();
//        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(d2,0)
//        });
//        graph.addSeries(series);
//
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphPlotter.this));

       // graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(146000);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        series.setDataPointsRadius(15);

        series.setDrawDataPoints(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setPadding(50);

        graph.getGridLabelRenderer().setVerticalAxisTitle("Volts");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graph.setTitle("Battery Voltage");
        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        Firebase ref1 = new Firebase("https://bottling-station-firebase.firebaseio.com/locations/location_z/parameters");
        Query queryRef = ref1.orderByChild("time").limitToLast(Integer.parseInt(noOfGraphEntries));
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot nextSnapshot, String s) {
                System.out.println("xxabcdsnapshotCUrrent new" + nextSnapshot.getValue().toString());
                System.out.println("snapshots ");
                System.out.println("xxabcdsnapshotCUrrent" + nextSnapshot.getValue().toString());
                Map<Object, Object> voltageMap = (Map<Object, Object>) nextSnapshot.getValue();
                System.out.println("asdfgBatteryVoltage" + voltageMap.get("battery_voltage").toString());
//                        System.out.println("asdfgload_current" + voltageMap.get("load_current").toString());
//                        System.out.println("asdfgsecurity_switch" + voltageMap.get("security_switch").toString());
//                        System.out.println("asdfgsolar_voltage" + voltageMap.get("solar_voltage").toString());
//                        System.out.println("asdfgtime" + voltageMap.get("time").toString());
                voltageValues = Float.parseFloat(voltageMap.get("battery_voltage").toString());
                voltageTimes = voltageMap.get("time").toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss"); // MM/dd/yyyy HH:mm:ss
                try {
                    datePoint = simpleDateFormat.parse(voltageTimes);
                    // System.out.println("date : "+simpleDateFormat.format(d5));
                } catch (ParseException ex) {
                    System.out.println("Exception " + ex);
                }
                System.out.println("absdatePoint:  " + datePoint);
                System.out.println("absvoltageValues " + voltageValues);
                calendar.add(Calendar.DATE, 1);
                Date d10 = calendar.getTime();
                System.out.println("absd3 " + d10);

                series.appendData(new DataPoint(datePoint, voltageValues), true, 100);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshotx, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("firebaseError" +firebaseError);
            }
        });

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {

                System.out.println("dataPoint: " + dataPoint.getX());
                long l = (new Double(dataPoint.getX())).longValue();

                Date dateClicked = new Date(l); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // the format of your date
                sdf.setTimeZone(TimeZone.getTimeZone("UTC+12:00")); // give a timezone reference for formating (see comment at the bottom
                String formattedDate = sdf.format(dateClicked);
//                System.out.println(formattedDate);
                double voltageDataPoint = dataPoint.getY();
                String toastVoltage = String.format("%.2f ", voltageDataPoint);

                System.out.println("dataPointVoltage: " + toastVoltage);
                Toast.makeText(GraphPlotter.this, "Captured @: " + formattedDate + "\n " + " Battery Voltage: " + toastVoltage + " Volts", Toast.LENGTH_SHORT).show();
            }
        });

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
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.


        savedInstanceState.putString("MyString", noOfGraphEntries);
        // etc.
        super.onSaveInstanceState(savedInstanceState);
    }
    //onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        noOfGraphEntries = savedInstanceState.getString("MyString");
    }
}
