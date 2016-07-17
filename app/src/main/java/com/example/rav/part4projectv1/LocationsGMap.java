package com.example.rav.part4projectv1;


    import com.firebase.client.DataSnapshot;
    import com.firebase.client.Firebase;
    import com.firebase.client.FirebaseError;
    import com.firebase.client.ValueEventListener;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.MapFragment;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.BitmapDescriptorFactory;
    import com.google.android.gms.maps.model.CameraPosition;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.LatLngBounds;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;

    import android.app.Notification;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.drawable.BitmapDrawable;
    import android.graphics.drawable.LevelListDrawable;
    import android.support.v4.app.Fragment;
    import android.app.Activity;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Toast;

    import java.util.HashMap;
    import java.util.Map;

/**
     * This shows how to create a simple activity with a map and a marker on the map.
     */
    public class LocationsGMap extends AppCompatActivity implements OnMapReadyCallback {
//        static final LatLng TutorialsPoint = new LatLng(21 , 57);
        private GoogleMap googleMap;
        static final LatLng AUCKLAND_VIEW =new LatLng(-36.8551775, 174.765273);// newmarket - -36.8645594,174.7659801
        private LatLngBounds AUCKLAND = new LatLngBounds(
                new LatLng(-37.1587727,174.9964483), new LatLng(-36.5346787,174.6653863));


        static final LatLng sign1 = new LatLng(-36.8645594,174.7659801);
        static final LatLng sign2 = new LatLng(-36.9894137,174.8578988);
        static final LatLng sign3 = new LatLng(-36.793349,174.480672);

        private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
        private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    private Firebase noOfLocations;
    private Firebase coordinate;
    private Firebase coordinatesVoltage;
    private DataSnapshot currentSnapshot;
    private DataSnapshot coordinateSnapshot;
    Map<String, String> signsPlotter = new HashMap<String, String>();
    Map<Object, Object> coordinatesValue;
    String voltage ="test";
    String uniqueKey;
    String previousKey;
    public String[] coordinatesKey = new String[10];
    Integer i=0;
    Integer j=0;
    boolean mapReady = false;
    boolean dataReady = false;
    String coordinates;
    GoogleMap map2;
    String value;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_locations_gmap);
            Firebase.setAndroidContext(this);
            noOfLocations = new Firebase("https://bottling-station-firebase.firebaseio.com/");
            coordinatesVoltage = new Firebase("https://bottling-station-firebase.firebaseio.com/");
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            noOfLocations.child("locations").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long numChildren = dataSnapshot.getChildrenCount();
                    System.out.println("xxxxxxasdsdf" + dataSnapshot.toString());
                    System.out.println( "numChildren:" + numChildren);

                   // String key1 = postSnapshot.getKey();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        System.out.println("xxxxxxasdf" + postSnapshot.toString());
                        String key = "locations/" + postSnapshot.getKey();
                        System.out.println("xxkey" + key);

                        value = "locations/" + postSnapshot.getKey()+"/parameters";
                        //coordinateSnapshot = postSnapshot.getValue();
                      //  System.out.println("xxxxxxcoordinateSnapshot " + coordinateSnapshot.toString());
                        System.out.println("xxxxxxcoordkey " + postSnapshot.getKey().toString());
                        coordinatesValue = (Map<Object, Object>) postSnapshot.getValue();
                        coordinates =  coordinatesValue.get("coordinate").toString();
                        System.out.println("xxxxxxcoordvalue " + coordinates);

                        coordinatesKey[j] = coordinates;
                        System.out.println("xxxxxcoordinatesKey" + coordinatesKey[j]);
                        System.out.println("xxxxxcoordinatesKeyivalue " + j);
                        j++;

                        coordinatesVoltage.child(value).orderByChild("time").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                currentSnapshot = snapshot;
                                System.out.println("snapshots ");

                                System.out.println("xxabcd" + snapshot.getValue().toString());

                                Map<Object, Object> voltageMap = (Map<Object, Object>) currentSnapshot.getValue();
                                System.out.println("xxxvoltageMap" + voltageMap.toString());
                                System.out.println("xxxxcurrentSnapshot" + currentSnapshot.getKey().toString());

                                for (Map.Entry<Object, Object> entry : voltageMap.entrySet()) {
                                    uniqueKey = entry.getKey().toString();

                                    // ...
                                    System.out.println("xxxuniqueKey" + uniqueKey);


                                }
                                if (previousKey == uniqueKey) {
                                    // do nothing
                                } else {
                                    Object a = (Object) voltageMap.get(uniqueKey); // contains latest values for a given sign
                                    System.out.println("xxxsdxxVoltage" + a.toString());
                                    Map<Object, Object> voltageInfo = (Map<Object, Object>) a;
                                    Object b = (Object) voltageInfo.get("battery_voltage");
////                                + "/battery_voltage");
                                    voltage = b.toString();
                                    System.out.println("xxxxxVoltage" + voltage);
//                                    int index = 0;
//                                    for (int k=0;k<coordinatesKey.length;k++) {
//                                        if (coordinatesKey[k].equals(uniqueKey)) {
//                                            index = k;
//                                            break;
//                                        }
//                                    }
                                    signsPlotter.put(coordinatesKey[i], voltage);
                                    System.out.println("voltage1234 " + voltage);
                                    System.out.println("xxxxxcoordinates54678 " + coordinatesKey[i]);
                                    System.out.println("xxxxxcoordinatesKeyivalueinside " + i);
                                    System.out.println("xxxxxsignsPlotter" + signsPlotter.toString());
                                    i++;

                                }
                                previousKey = uniqueKey;
                                dataReady = false;
                                if (i == 3) {
                                    dataReady = true;
                                    i = 0;
                                    j = 0;
                                }
                                if (mapReady == true && dataReady == true) {
                                    map2.clear();
                                    // map2.clear();
                                    for (Map.Entry<String, String> entry : signsPlotter.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        // ...
                                        String[] latLng = new String[2];
                                        System.out.println("latLng1234 " + latLng[0]);
                                        System.out.println("latLng1234 " + latLng[1]);
                                        latLng = key.split(",");
                                        double latMap = Double.parseDouble(latLng[0]);
                                        double longMap = Double.parseDouble(latLng[1]);
                                        System.out.println("key1234 " + key);
                                        System.out.println("key1234value " + value);
                                        System.out.println("");
                                        Float voltageAtLocation = Float.parseFloat(value);
//                                        map2.addMarker(new MarkerOptions().position(new LatLng(latMap,longMap)).icon(BitmapDescriptorFactory.fromResource(R.drawable.welcome)).title(value)).setFlat(true);
                                        Bitmap largeIcon;
                                        if (voltageAtLocation > 12.50) {
                                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.full_battery);
                                        } else if (voltageAtLocation > 12.00) {
                                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.halved_battery);
                                        } else {
                                            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.low_battery);

                                        }

                                        Bitmap bhalfsize = Bitmap.createScaledBitmap(largeIcon, largeIcon.getWidth() / 10, largeIcon.getHeight() / 10, false);
                                        map2.addMarker(new MarkerOptions()
                                                        .position(new LatLng(latMap, longMap))
                                                        .title(value)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bhalfsize))
                                        );
                                    }
                                }

                                // System.out.println("key:    " + i + key1);
                            }

                            @Override
                            public void onCancelled(FirebaseError error) {
                                System.out.println(error.getMessage());
                            }

                        });



                    }
                }

                @Override
                public void onCancelled(FirebaseError error) {
                    System.out.println("error on firebase" + error.getMessage());
                }
            });

//            ref.child("locations/location_x/parameters").orderByChild("time").limitToLast(1).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    currentSnapshot = snapshot;
//                    int x = currentSnaps
//                    System.out.println("snaphots ");
//                    System.out.println("xx" + currentSnapshot.getValue().toString());
//                    readParameters();
//                }
//
//                @Override
//                public void onCancelled(FirebaseError error) {
//                    System.out.println(error.getMessage());
//                }
//
//            });
//            try {
//                if (googleMap == null) {
//                   // center = googleMap.getCameraPosition().target;
//                    googleMap = ((MapFragment) getFragmentManager().
//                            findFragmentById(R.id.map)).getMap();
//                }
//                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                Marker TP = googleMap.addMarker(new MarkerOptions().
//                        position(center).title("Sign1"));
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we
         * just add a marker near Africa.
         */
        @Override
        public void onMapReady(GoogleMap map1) {
            //map1.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mapReady = true;
            map2 = map1;
            System.out.println("mapReady");
//            for (Map.Entry<String, String> entry : signsPlotter.entrySet()) {
//                String key = entry.getKey();
//                String value = entry.getValue();
//                // ...
//                System.out.println("key1234 " + key);
//                System.out.println("key1234value " + value);
//                map1.addMarker(new MarkerOptions().position(sign2).title(value)).showInfoWindow();
//
//            }
//            map1.addMarker(new MarkerOptions().position(sign2).title("Sign2")).showInfoWindow();
//            map1.addMarker(new MarkerOptions().position(sign3).title("Sign3")).showInfoWindow();
//            map1.addMarker(new MarkerOptions().position(sign1).title("Sign1")).showInfoWindow();


//            map1.moveCamera(CameraUpdateFactory.newLatLngZoom(AUCKLAND.getCenter(), 10));

            // Move the camera instantly to Sydney with a zoom of 15.
//            map1.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
//
//// Zoom in, animating the camera.
//            map1.animateCamera(CameraUpdateFactory.zoomIn());
//
//// Zoom out to zoom level 10, animating with a duration of 2 seconds.
//            map1.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(AUCKLAND_VIEW)      // Sets the center of the map to Mountain View
                    .zoom(9)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map1.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//            map1.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                @Override
//                public void onInfoWindowClick(Marker marker) {
//                    Toast.makeText(LocationsGMap.this, "SIGN CLICKED", Toast.LENGTH_SHORT).show();// display toast
//
//                    Intent intent = new Intent(LocationsGMap.this, DisplayParameters.class);
//                    startActivity(intent);
//
//                }
//            });
            map1.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    System.out.println("xxxxxcoordinates54678 marker clicked at: " + arg0.getPosition().toString());
                    String SignCoordinate = contains(arg0.getPosition().toString());
                    if (arg0 != null && (SignCoordinate  != "null")) {

                        Toast.makeText(LocationsGMap.this, "SIGN CLICKED", Toast.LENGTH_SHORT).show();// display toast
                        Intent intent = new Intent(LocationsGMap.this, DisplayParameters.class);
                        intent.putExtra("SignCoordinates", SignCoordinate);
                        startActivity(intent);
                    }
                        return true;

                }

            });
        }
    public  String contains(String item) {
        for (String n : coordinatesKey) {
            String clickedCoordinate = "lat/lng: ("+n+")";
            System.out.println("xxxxxcoordinates54678 item " + item );
            System.out.println("xxxxxcoordinates54678    n " + clickedCoordinate);

            if (clickedCoordinate.equals(item)) {

                System.out.println("xxxxxcoordinates54678 returning true" );
                return n;
            }
        }
        System.out.println("xxxxxcoordinates54678 returning false" );
        return "null";
    }
    }