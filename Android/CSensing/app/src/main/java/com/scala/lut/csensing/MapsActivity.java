package com.scala.lut.csensing;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    static final int REQUEST_LOCATION = 1;
    String urlForGet = "https://csensing-angular-ajesh12k.c9users.io/getEvent";
    String type = "";
    String users = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        JSONObject resp = getData("hello");
        Log.i("Hellllooooooooooooooo", resp.toString());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String location = getLocation();
        Double latitude = Double.valueOf(location.split("#")[0]);
        Double longitude = Double.valueOf(location.split("#")[1]);
        // Add a marker in Sydney and move the camera
        markOnMap(latitude, longitude, "");
    }

    public void markOnMap(Double latitude, Double longitude, String title){
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title(title));
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(15);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    String getLocation() {
        String output = "";
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                output = latitude + "#" + longitude;
            } else {
                Log.i("location", "Unable to find location");
            }
        }
        return output;
    }

    public JSONObject getData(String macId){
        ArrayList array = new ArrayList();
        JSONObject resp = new JSONObject();
        Bundle extras = getIntent().getExtras();
        String type = (String)extras.get("type");
        String users = (String)extras.get("users");
        try {
            JSONObject obj = new JSONObject();
            obj.put("event_id", "");
            obj.put("event_type", "");
            obj.put("device_id", macId);
            obj.put("fromDate", "");
            obj.put("toDate", "");
            obj.put("status", "");
            obj.put("changedBy", "");
            obj.put("changedDate", "");

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlForGet, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response for get", response.toString());
                            try {
                                Object status = response.get("status");
                                Log.i("Status Tag", status.toString());
                                try {
                                    JSONArray maps = response.getJSONArray("result");
                                    for(int i = 0; i < maps.length(); i++){
                                        Log.i("Response Test", maps.get(i).toString());
                                        JSONObject map =  (JSONObject)maps.get(i);
                                        String latitude = (String)map.get("latitude");
                                        String longitude = (String)map.get("longitude");
                                        markOnMap(Double.valueOf(latitude), Double.valueOf(longitude), "Marker");
                                        Log.i("map status","Latitude - " + latitude + "------- Longitude"+ longitude);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            queue.add(jsObjRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }
}