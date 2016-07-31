package com.example.rav.part4projectv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private String Help;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Help =  "1. New users Sign up using your email and password.\n"+
                "    Existing users Log in using your credentials.\n" +
                "2. Click on the sign you're interested in.\n"+
                "3. View Parameters using graph.\n" +
                "    Tap on graph points to view more information.";
        fetchButton = (Button) findViewById(R.id.fetch_button);






        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Fetch Button Clicked\n");
                sendMessage(v);


            }
        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, LocationsGMap.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void alert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Help);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            System.out.println("Help clicked");
            alert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}