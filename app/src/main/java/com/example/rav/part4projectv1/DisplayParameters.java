package com.example.rav.part4projectv1;

        import android.app.Activity;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.os.Message;
        import android.renderscript.Sampler;
        import android.telephony.SmsManager;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.animation.Animation;
        import android.view.animation.LinearInterpolator;
        import android.view.animation.RotateAnimation;
        import android.widget.Button;
        import android.widget.ImageView;
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


        import org.json.JSONObject;

        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Objects;
        import java.util.Random;

public class DisplayParameters extends Activity
{
    private TextView battery_voltage_text;
    private TextView load_current_text;
    private TextView security_switch_text;
    private TextView solar_voltage_text;
    private TextView time_text;

    private Firebase ref;
    private ImageView batteryView;
    private DataSnapshot currentSnapshot;
    Map<Object, Object> signLocationFromDB;
    String parametersLocation = "https://bottling-station-firebase.firebaseio.com/locations/";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_display_parameters);

        String SignCoordinates;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                SignCoordinates= null;
            } else {
                SignCoordinates= extras.getString("SignCoordinates");
            }
        } else {
            SignCoordinates= (String) savedInstanceState.getSerializable("SignCoordinates");
        }
        System.out.println("SignCoordinates " + SignCoordinates);


        batteryView = (ImageView) findViewById(R.id.battery_view);
        battery_voltage_text = (TextView) findViewById(R.id.battery_voltage_view);
        load_current_text = (TextView) findViewById(R.id.load_current_view);
        security_switch_text = (TextView) findViewById(R.id.security_switch_view);
        solar_voltage_text = (TextView) findViewById(R.id.solar_voltage_view);
        time_text = (TextView) findViewById(R.id.time_view);


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
                        Map<Object, Object> voltageMap = (Map<Object, Object>) b;
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
                        Bitmap bhalfsize = Bitmap.createScaledBitmap(largeIcon, largeIcon.getWidth() / 1, largeIcon.getHeight() / 1, false);

                        batteryView.setImageBitmap(bhalfsize);
                        battery_voltage_text.append(voltageMap.get("battery_voltage").toString());
                        load_current_text.append(voltageMap.get("load_current").toString());
                        security_switch_text.append(voltageMap.get("security_switch").toString());
                        solar_voltage_text.append(voltageMap.get("solar_voltage").toString());
                        time_text.append(voltageMap.get("time").toString());
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
        returnMap.put("security_switch",c.toString());
        returnMap.put("solar_voltage",d.toString());
        returnMap.put("time", e.toString());
        return returnMap;
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
}