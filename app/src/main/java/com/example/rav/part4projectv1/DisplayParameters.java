package com.example.rav.part4projectv1;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Typeface;
        import android.os.Bundle;
        import android.os.Message;
        import android.renderscript.Sampler;
        import android.telephony.SmsManager;
        import android.text.Editable;
        import android.text.Html;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.LinearInterpolator;
        import android.view.animation.RotateAnimation;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.firebase.client.ChildEventListener;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.GenericTypeIndicator;
        import com.firebase.client.Query;
        import com.firebase.client.ValueEventListener;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
        import com.jjoe64.graphview.series.DataPoint;
        import com.jjoe64.graphview.series.DataPointInterface;
        import com.jjoe64.graphview.series.LineGraphSeries;
        import com.jjoe64.graphview.series.OnDataPointTapListener;
        import com.jjoe64.graphview.series.Series;


        import org.json.JSONObject;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Objects;
        import java.util.Random;
        import java.util.TimeZone;

public class DisplayParameters extends Activity
{
    private TextView battery_voltage_text;
    private TextView load_current_text;
    private TextView security_switch_text;
    private TextView solar_voltage_text;
    private TextView time_text;
    private Button fullScreenButton;
    private EditText graphDataAmount;
    Firebase ref1;
    private Firebase ref;
    private ImageView batteryView;
    private DataSnapshot currentSnapshot;
    Map<Object, Object> signLocationFromDB;
    String parametersLocation = "https://bottling-station-firebase.firebaseio.com/locations/";
    Bitmap bhalfsize;
    Map<Object, Object> voltageMap;
    String graphURL;
    GraphView graph;
    String noOfGraphEntries;
    LineGraphSeries<DataPoint> series;
    Date datePoint;
    Calendar calendar;
    Float voltageValues;
    String voltageTimes;
    boolean graphToExecute =  false;
    String SignCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_display_parameters);
        Spinner spinner = (Spinner) findViewById(R.id.parameters_select);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        graphDataAmount = (EditText) findViewById(R.id.no_of_days);
        fullScreenButton = (Button) findViewById(R.id.full_screen);
        graph = (GraphView) findViewById(R.id.graph_display_parameters);

        series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
//        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//                new DataPoint(d1, 1),
//                new DataPoint(d2, 5.01),
//                new DataPoint(d3, 3),
//                new DataPoint(d5, 2)
//        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(DisplayParameters.this));

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
//        graph.setTitle("Battery Voltage");
//        graph.getLegendRenderer().setVisible(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);


        graphDataAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    System.out.println("Enter was pressed");
                    if (isInteger(graphDataAmount.getText().toString())) {
                        InputMethodManager imm = (InputMethodManager)getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(graphDataAmount.getWindowToken(), 0);
                        noOfGraphEntries = graphDataAmount.getText().toString();
                        System.out.println("noOfGraphEntries12: "+noOfGraphEntries);
                        series.resetData(new DataPoint[]{});
                        updateGraph();
                    } else {
                        noOfGraphEntries = "10";
                        Toast.makeText(DisplayParameters.this, "Enter a Valid Number", Toast.LENGTH_SHORT).show();// display toast
                    }
                    handled = true;
                }
                return handled;
            }
        });


        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("fullScreenButton Clicked\n");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
        });

        calendar = Calendar.getInstance();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                SignCoordinates= null;
            } else {
                SignCoordinates= extras.getString("SignCoordinates");
            }
        } else {
            //SignCoordinates= (String) savedInstanceState.getSerializable("SignCoordinates");

            CharSequence savedText = savedInstanceState.getCharSequence("sign_coordinates_value");
            SignCoordinates = savedText.toString();
        }
        System.out.println("SignCoordinates "  + SignCoordinates);//PrbolemHereWHenComingBckFromGraphPlotter

        batteryView = (ImageView) findViewById(R.id.battery_view);
        battery_voltage_text = (TextView) findViewById(R.id.battery_voltage_view);
        load_current_text = (TextView) findViewById(R.id.load_current_view);
        security_switch_text = (TextView) findViewById(R.id.security_switch_view);
        solar_voltage_text = (TextView) findViewById(R.id.solar_voltage_view);
        time_text = (TextView) findViewById(R.id.time_view);

//        battery_voltage_text.setText(Html.fromHtml("<b>Battery Voltage:  </b>"));
//        load_current_text.setText(Html.fromHtml("<b>Load Current:  </b>"));
//        security_switch_text.setText(Html.fromHtml("<b>Security Switch:  </b>"));
//        solar_voltage_text.setText(Html.fromHtml("<b>Solar Voltage:  </b>"));
//        time_text.setText(Html.fromHtml("<b>Time:  </b>"));


        batteryView = (ImageView) findViewById(R.id.battery_view);
                ref = new Firebase("https://bottling-station-firebase.firebaseio.com/locations");

        Query queryRef = ref.orderByChild("coordinate").equalTo(SignCoordinates);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                battery_voltage_text.setText("Battery Voltage: ");
                load_current_text.setText("Load Current: ");
                security_switch_text.setText("Security Switch: ");
                solar_voltage_text.setText("Solar Voltage: ");
                time_text.setText("Time: ");
                parametersLocation = "https://bottling-station-firebase.firebaseio.com/locations/";
                System.out.println("snapshots ");
                System.out.println("xxabcdDisplayParam" + snapshot.getValue().toString());
                signLocationFromDB = (Map<Object, Object>) snapshot.getValue();
                System.out.println("xxxxxxcoordvalue " + signLocationFromDB);
                for (Map.Entry<Object, Object> entry : signLocationFromDB.entrySet()) {
                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    // do stuff
                    System.out.println("xxxxxxcoordvaluekey " + key);
                    parametersLocation = parametersLocation.concat(key);
                    System.out.println("xxxxxxcoordvalueQuery " + parametersLocation);
                    graphURL = parametersLocation;

                }

                Firebase getCurrentParamers = new Firebase(parametersLocation);
                getCurrentParamers.child("parameters").orderByChild("time").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot nextSnapshot) {
                        System.out.println("snapshots ");
                        System.out.println("xxabcdsnapshotCUrrent" + nextSnapshot.getValue().toString());
                        Map<Object, Object> a = (Map<Object, Object>) nextSnapshot.getValue();
                        System.out.println("asdfg " + a.toString());
                        String uniqueKey = null;
                        for (Map.Entry<Object, Object> entry : a.entrySet()) {
                            uniqueKey = entry.getKey().toString();
                            // ...
                            System.out.println("xxxuniqueKey" + uniqueKey);
                        }

                        System.out.println("asdfg12 " + a.get(nextSnapshot.getKey().toString()));
                        Object b = (Object) a.get(uniqueKey);
                        System.out.println("asdfg " + b.toString());
                        voltageMap = (Map<Object, Object>) b;
                        System.out.println("asdfgBatteryVoltage" + voltageMap.get("battery_voltage").toString());
                        System.out.println("asdfgload_current" + voltageMap.get("load_current").toString());
                        System.out.println("asdfgsecurity_switch" + voltageMap.get("security_switch").toString());
                        System.out.println("asdfgsolar_voltage" + voltageMap.get("solar_voltage").toString());
                        System.out.println("asdfgtime" + voltageMap.get("time").toString());
                        Float voltageAtLocation = Float.parseFloat(voltageMap.get("battery_voltage").toString());
                        Bitmap largeIcon;
                        if (voltageAtLocation > 12.50) {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.full_battery);

                        } else if (voltageAtLocation > 12.00) {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.halved_battery);
                        } else {
                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.low_battery);

                        }
                        bhalfsize = Bitmap.createScaledBitmap(largeIcon, largeIcon.getWidth() / 1, largeIcon.getHeight() / 1, false);
                        batteryView.setImageBitmap(bhalfsize);
//                        battery_voltage_text.append(Html.fromHtml("<p>" + voltageMap.get("battery_voltage").toString() + "</p>"));
                       battery_voltage_text.append("  " + voltageMap.get("battery_voltage").toString());
                        load_current_text.append("  " + voltageMap.get("load_current").toString());
                        security_switch_text.append("  " + voltageMap.get("security_switch").toString());
                        solar_voltage_text.append("  " + voltageMap.get("solar_voltage").toString());
                        time_text.append("  " + voltageMap.get("time").toString());
                        System.out.println("graphURL:  " + graphURL);
                        ref1 = new Firebase(graphURL+"/parameters"); //graphURL
                        if (noOfGraphEntries != null){
                            noOfGraphEntries = "1";
                        }

                        updateGraph();



                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                        System.out.println(error.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.out.println(error.getMessage());
            }

        });
//        if (graphToExecute == true){
//            updateGraph();
//        }
//    updateGraph();
    }


    public void sendMessage(View view) {
        Intent intent = new Intent(this, GraphPlotter.class);
        System.out.println("asdfg12Intent " + parametersLocation);
        intent.putExtra("parametersLocation", parametersLocation);
        Toast.makeText(DisplayParameters.this, "Button  CLICKED", Toast.LENGTH_SHORT).show();// display toast
        startActivity(intent);
    }

    private Map<String, String> readParameters()
    {
        Map<Object, Object> map = (Map<Object, Object>) currentSnapshot.getValue();
        Object a = (Object) map.get("battery_voltage");
        Object b = (Object) map.get("load_current");
        Object c = (Object) map.get("security_switch");
        Object d = (Object) map.get("solar_voltage");
        Object e = (Object) map.get("time");

        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("battery_voltage", a.toString());
        returnMap.put("load_current", b.toString());
        returnMap.put("security_switch", c.toString());
        returnMap.put("solar_voltage", d.toString());
        returnMap.put("time", e.toString());
        return returnMap;
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("sign_coordinates_value", SignCoordinates);
    }
    @Override
    public void onRestart(){
        super.onRestart();
        // put your code here...
        batteryView.setImageBitmap(bhalfsize);
        battery_voltage_text.setText("Battery Voltage: " + voltageMap.get("battery_voltage").toString());
        load_current_text.setText("Load Current: " + voltageMap.get("load_current").toString());
        security_switch_text.setText("Security Switch: " + voltageMap.get("security_switch").toString());
        solar_voltage_text.setText("Solar Voltage: " + voltageMap.get("solar_voltage").toString());
        time_text.setText("Time: " + voltageMap.get("time").toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    public void updateGraph(){

        if (noOfGraphEntries == null){
            noOfGraphEntries = "10";
        }

        System.out.println("noOfGraphEntries:  " +noOfGraphEntries.toString());

        Query queryRef1 = ref1.orderByChild("time").limitToLast(Integer.parseInt(noOfGraphEntries));
        queryRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot nextSnapshot, String s) {
                System.out.println("xxabcdsnapshotCUrrent new" + nextSnapshot.getValue().toString());
                System.out.println("snapshots ");
                System.out.println("xxabcdsnapshotCUrrent" + nextSnapshot.getValue().toString());
                Map<Object, Object> voltageMap1 = (Map<Object, Object>) nextSnapshot.getValue();
                System.out.println("asdfgBatteryVoltage" + voltageMap1.get("battery_voltage").toString());
//                        System.out.println("asdfgload_current" + voltageMap.get("load_current").toString());
//                        System.out.println("asdfgsecurity_switch" + voltageMap.get("security_switch").toString());
//                        System.out.println("asdfgsolar_voltage" + voltageMap.get("solar_voltage").toString());
//                        System.out.println("asdfgtime" + voltageMap.get("time").toString());
                voltageValues = Float.parseFloat(voltageMap1.get("battery_voltage").toString());
                voltageTimes = voltageMap1.get("time").toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss"); // MM/dd/yyyy HH:mm:ss
                try {
                    datePoint = simpleDateFormat.parse(voltageTimes);
                    // System.out.println("date : "+simpleDateFormat.format(d5));
                } catch (ParseException ex) {
                    System.out.println("Exception " + ex);
                }
                System.out.println("absdatePoint:  " + datePoint);
                System.out.println("absvoltageValues " + voltageValues);
//                calendar.add(Calendar.DATE, 1);
//                Date d10 = calendar.getTime();
//                System.out.println("absd3 " + d10);

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
                System.out.println("firebaseError" + firebaseError);
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
                Toast.makeText(DisplayParameters.this, "Captured @: " + formattedDate + "\n " + " Battery Voltage: " + toastVoltage + " Volts", Toast.LENGTH_SHORT).show();
            }
        });

    }
}