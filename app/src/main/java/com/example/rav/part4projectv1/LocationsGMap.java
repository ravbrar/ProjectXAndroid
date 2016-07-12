package com.example.rav.part4projectv1;


    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.MapFragment;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.CameraPosition;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.LatLngBounds;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;

    import android.content.Intent;
    import android.support.v4.app.Fragment;
    import android.app.Activity;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.Toast;

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


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_locations_gmap);
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

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

            map1.addMarker(new MarkerOptions().position(sign2).title("Sign2")).showInfoWindow();
            map1.addMarker(new MarkerOptions().position(sign3).title("Sign3")).showInfoWindow();
            map1.addMarker(new MarkerOptions().position(sign1).title("Sign1")).showInfoWindow();
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
                    .zoom(10)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
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
                    if (arg0 != null && arg0.getTitle().equals("Sign1"))
                        ; // if marker  source is clicked
                    Toast.makeText(LocationsGMap.this, "SIGN CLICKED", Toast.LENGTH_SHORT).show();// display toast
                    Intent intent = new Intent(LocationsGMap.this, DisplayParameters.class);
                    startActivity(intent);

                    return true;
                }

            });
        }
    }