package com.example.rav.part4projectv1;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.EditText;
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

public class MainActivity extends Activity {
    private Button fetchButton;

    private EditText userNameEntry;
    private  String noOfGraphEntries;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

               fetchButton = (Button) findViewById(R.id.fetch_button);
        userNameEntry = (EditText) findViewById(R.id.username);

        userNameEntry.setText("10");



        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Fetch Button Clicked\n");
                if (isInteger(userNameEntry.getText().toString())) {
                    noOfGraphEntries = userNameEntry.getText().toString();
                    sendMessage(v);
                }else{
                    Toast.makeText(MainActivity.this, "Please Enter a Valid Number", Toast.LENGTH_SHORT).show();// display toast
                }




            }
        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(MainActivity.this, GraphPlotter.class); // change this back to LocationsGMap.class
        System.out.println("noOfGraphEntries" + noOfGraphEntries);
        intent.putExtra("noOfGraphEntries", noOfGraphEntries);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static boolean isInteger(String s) {
        return isInteger(s,10);
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
}
