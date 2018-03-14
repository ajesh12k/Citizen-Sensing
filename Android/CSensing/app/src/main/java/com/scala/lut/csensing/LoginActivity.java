package com.scala.lut.csensing;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationManager;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;


import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity  {

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public int ENABLE_BT_REQUEST_CODE = 1;
    private ProximityManager proximityManager;


    String urlForRest = "https://citizen-sensing-api-ajesh12k.c9users.io/saveEvent";
    String urlForGet = "https://citizen-sensing-api-ajesh12k.c9users.io/getEvent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button submit = (Button)findViewById(R.id.button);


        /*
        KontaktSDK.initialize("knLToeFcNOHHHuPgXAeCkjdvPOcXIaUX");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());
*/
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        if(!bluetooth.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
            scan();

        }else{
            Intent intent = new Intent();
        }
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }
*/
    int i = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    i++;
                    Log.i("Clicked", "vol up" + i);
                    if(i % 2 == 0) {
                        TextView tv = (TextView) findViewById(R.id.ble);
                        tv.append("\n Vol up button clicked!");
                        insertData("hello");
                        Log.i("Clicked", "vol up");
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    TextView tv = (TextView) findViewById(R.id.ble);
                    tv.setText("Vol down button clicked");
                    Log.i("Clicked", "vol down");
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

/*
    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    Handler h = new Handler();
    int delay = 8000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;

    @Override
    protected void onResume() {
        //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    private IBeaconListener createIBeaconListener() {
        Log.i("call", "create CAlled");
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                TextView tv = (TextView) findViewById(R.id.ble);
                tv.append("called!");
                Log.i("inside", "First Disover loop");
                String macID = ibeacon.getAddress();
                String mac = readFromFile(LoginActivity.this);
                if(macID.equals(mac)) {
                    getData(macID);
                    Log.i("Calling Get Data", "Called");
                    UUID uuid = ibeacon.getProximityUUID();
                    tv.append(" \n Ibeacon Found " + uuid.toString() + " -- " + macID + "\n");
                    String uuidback = uuid.toString();
                    String last = uuidback.substring(uuidback.length() - 1, uuidback.length());
                    int val = Integer.valueOf(last);
                    if(val > 1){
                        tv.append("Clicked");
                        insertData(macID);
                        SystemClock.sleep(10000);
                        createIBeaconListener();
                    }
                }
                Log.i("Sample", "IBeacon discovered: " + ibeacon.toString());
            }
        };
    }


    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
 //               TextView tv = (TextView) findViewById(R.id.ble);
//                tv.setText("EddyStone Found");

                Log.i("Sample", "Eddystone discovered: " + eddystone.toString());
            }
        };
    }

*/

    public Set scan(){
        return bluetooth.getBondedDevices();
    }

    String location;
    private void insertData(String macId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                location = getLocation();
            }
        });

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject params = new JSONObject();

            double test = Math.random();
            params.put("device_id", macId);
            params.put("latitude", location.split("#")[0]);
            params.put("longitude", location.split("#")[1]);
            params.put("type", "smell");
            final String requestBody = params.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlForRest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
                    addNotification();
                    getData("hello");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(LoginActivity.this, error+"", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            Log.d("hello", "insertData: ");
        }
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
        try {
            JSONObject obj = new JSONObject();
            obj.put("event_id", "");
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

                            TextView tv = (TextView) findViewById(R.id.ble);
                            tv.append("\n" + response);
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

    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.information)
                        .setContentTitle("cSensing")
                        .setContentText("Your Observation has been saved Successfully!");

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
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