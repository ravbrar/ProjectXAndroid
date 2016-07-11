package com.example.rav.part4projectv1;

import android.app.Activity;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;


import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity
{
    private TextView battery_voltage_text;
    private TextView load_current_text;
    private TextView security_switch_text;
    private TextView solar_voltage_text;
    private TextView time_text;

    private TextView battery_voltage_value_text;
    private TextView load_current_value_text;
    private TextView security_switch_value_text;
    private TextView solar_voltage_value_text;
    private TextView time_value_text;

    private Firebase ref;
    private ImageView batteryView;
    private DataSnapshot currentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        ref = new Firebase("https://bottling-station-firebase.firebaseio.com/");

        battery_voltage_text = (TextView) findViewById(R.id.battery_voltage_view);
        load_current_text = (TextView) findViewById(R.id.load_current_view);
        security_switch_text = (TextView) findViewById(R.id.security_switch_view);
        solar_voltage_text = (TextView) findViewById(R.id.solar_voltage_view);
        time_text = (TextView) findViewById(R.id.time_view);

        battery_voltage_value_text = (TextView) findViewById(R.id.battery_voltage_value_view);
        load_current_value_text = (TextView) findViewById(R.id.load_current_value_view);
        security_switch_value_text = (TextView) findViewById(R.id.security_switch_value_view);
        solar_voltage_value_text = (TextView) findViewById(R.id.solar_voltage_value_view);
        time_value_text = (TextView) findViewById(R.id.time_value_view);

        batteryView = (ImageView) findViewById(R.id.battery_view);

        ref.child("locations/location_x/parameters").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                currentSnapshot = snapshot;
                readParameters();
            }

            @Override
            public void onCancelled(FirebaseError error)
            {
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

        battery_voltage_value_text.setText(a.toString());
        load_current_value_text.setText(b.toString());
        security_switch_value_text.setText(c.toString());
        solar_voltage_value_text.setText(d.toString());
        time_value_text.setText(e.toString());

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
