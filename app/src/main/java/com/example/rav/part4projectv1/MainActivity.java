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

public class MainActivity extends Activity {
    private Button fetchButton;
    private Button machineOn;
    private Button machineOff;
    private TextView textView;
    private Firebase ref;
    private Firebase refAtStart;
    private ImageView motorView;
    private DataSnapshot currentSnapshot;
    boolean toggleFlag = false;
    private SmsManager smsManager;






    private static final Random RANDOM = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        System.out.println("Fetch Button Clicked\n");
        ref = new Firebase("https://bottling-station-firebase.firebaseio.com/");

        fetchButton = (Button) findViewById(R.id.fetch_button);
        machineOn = (Button) findViewById(R.id.machine_on);
        machineOff = (Button) findViewById(R.id.machine_off);

        textView = (TextView) findViewById(R.id.rpm_view);
        motorView = (ImageView) findViewById(R.id.motor_view);

        smsManager = SmsManager.getDefault();




        machineOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ref.child("machine_status").setValue("ON");
                //Log.d("My App: ", "ON Button Clicked\n");
                System.out.println("ON Button Clicked\n");

                smsManager.sendTextMessage("+64210328301", null, "1", null, null);// william - +640211879860  // iot - +642102307205
                Toast.makeText(MainActivity.this, "ON Button Clicked ", Toast.LENGTH_SHORT).show();

            }
        });

        machineOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ref.child("machine_status").setValue("OFF");
                System.out.println("OFF Button Clicked\n");

                smsManager.sendTextMessage("+64210328301", null, "0", null, null);
                Toast.makeText(MainActivity.this, "OFF Button Clicked ", Toast.LENGTH_SHORT).show();
            }
        });

        ref.child("stages/stage1").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                currentSnapshot = snapshot;
                readStage1();

                if (toggleFlag){
                    updateAnalytics(readStage1());
                }

                toggleFlag = true;
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.out.println(error.getMessage());
            }

        });

        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Fetch Button Clicked\n");
            }
        });
    }


    private void updateAnalytics (Map<String, String> dataToUpdate) {

        String a = (String) dataToUpdate.get("time");
        String b = (String) dataToUpdate.get("status");


        Map<String, String> updateFirebase = new HashMap<String, String>();
        updateFirebase.put("status", b.toString());
        updateFirebase.put("time", a.toString());
        ref.child("analytics").push().setValue(updateFirebase);
    }

    private Map<String, String> readStage1 (){
        Map<Object, Object> map = (Map<Object, Object>) currentSnapshot.getValue();
        Object a = (Object) map.get("time");
        Object b = (Object) map.get("status");
        System.out.println(b.toString() + " -- " + a.toString() + "\n");

//                        50(20%) 2000-3000
//                        64(25%)5000-6000
//                        77(30%) 6400-7200

        Integer rpmValue = 0;
        try {
            rpmValue = Integer.parseInt(b.toString());
        } catch (NumberFormatException e) {
            rpmValue = 0;
            e.printStackTrace();
        }
        String rpmDisplay = b.toString();
        int duration;
//        if ((rpmValue >= 2000) && (rpmValue <= 3000)) {
//            duration = 5000;
//        } else if ((rpmValue >= 5000) && (rpmValue <= 6000)) {
//            duration = 1500;
//        } else if ((rpmValue >= 6400) && (rpmValue <= 7200)) {
//            duration = 500;
//        }
        if ((rpmValue > 0) ) {
            duration = 500;
        } else {
            rpmDisplay = "fault";
            duration = 0;

        }
        textView.setText("RPM: " + rpmDisplay + "\n");
        Animation rotation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setDuration(duration);

        motorView.startAnimation(rotation);
        System.out.println("Duration: " + duration + "\n");

        Map<String, String> returnMap = new HashMap<String, String>();
        returnMap.put("status", rpmDisplay);
        returnMap.put("time", a.toString());
        return returnMap;
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
}
