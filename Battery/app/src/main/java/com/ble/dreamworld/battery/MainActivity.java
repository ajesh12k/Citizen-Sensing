package com.ble.dreamworld.battery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Button sendData;

    String urlForRest = "http://192.168.0.113:8888/design4green3/ajax.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        String location = getLocation();


        sendData = (Button)findViewById(R.id.button);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    String location;
    private void insertData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                location = getLocation();
            }
        });
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlForRest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                double test = Math.random();
                Log.d("####message#####", location);
                Map<String, String> dataMap = new HashMap<String, String>();
                dataMap.put("action", "saveData");
                dataMap.put("user", "Krishna");
                dataMap.put("latitude", location.split("#")[0]);
                dataMap.put("longitude", location.split("#")[1]);
                dataMap.put("value", String.valueOf(test));

                return dataMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /******* PHP CODE FOR REST ******
     function saveData(){
     $conn = new mysqli($GLOBALS['host'], $GLOBALS['user'], $GLOBALS['password'], $GLOBALS['db']);
     $user = $_POST['user'];
     $lat = $_POST['latitude'];
     $long = $_POST['longitude'];
     $value = $_POST['value'];
     $sql = "insert into temp values('$user', '$lat', '$long', '$value')";
     $list = $conn->query($sql);
     echo "Successfully Saved!!";
     exit;
     }
     ******* PHP CODE FOR REST ******/

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

                ((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latitude);
                ((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longitude);
                output = latitude + "#" + longitude;
            } else {
                ((EditText)findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                ((EditText)findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
            }
        }
        return output;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}