package com.scala.lut.csensing;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebviewActivity extends AppCompatActivity {

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    String urlForRest = "https://csensing-angular-ajesh12k.c9users.io/saveEvent";
    String uiURL = "https://csensing-angular-ajesh12k.c9users.io";
    String urlForEventGet = "https://csensing-angular-ajesh12k.c9users.io/getEventTypes";
    String urlForRegisterCheck = "https://csensing-angular-ajesh12k.c9users.io/checkDevice";
    int i = 0;
    String address = "";
    JSONArray types = new JSONArray();
    String monitoring = "";
    String prevMonitoring = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Button submit = (Button)findViewById(R.id.maps);
        Button register = (Button)findViewById(R.id.register);
        TextView tv = (TextView) findViewById(R.id.ble);
        Button how = (Button)findViewById(R.id.how);
        address = getMacAddr();
        getEventTypes();
        checkDevice(address);
        Log.d("MAC",address);

        final Spinner sItems = (Spinner) findViewById(R.id.spinner);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uiURL));
                startActivity(browserIntent);
            }
        });

        how.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
            }
        });
    }

    public void checkDevice(String macId){
        Log.i("Device", "Checking Started");
        try {
            JSONObject obj = new JSONObject();
            obj.put("device_mac", macId);

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlForRegisterCheck, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response for get", response.toString());
                            try {
                                Object status = response.get("status");
                                if (status.toString().equalsIgnoreCase("success")){
                                    TextView device = (TextView) findViewById(R.id.deviceStatus);
                                    Object deviceStatus = response.get("device");
                                    Log.i("Device status", deviceStatus.toString());
                                    if(deviceStatus.toString().equalsIgnoreCase("found")){
                                        Object id = response.get("device_id");
                                        checkRegisterStatus("found",id.toString());
                                    }else{
                                        checkRegisterStatus("notFound","none");
                                    }
                                }
                                Log.i("Status Tag", status.toString());
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
    }

    public void checkRegisterStatus(String status, String id){
        Button register = (Button)findViewById(R.id.register);
        TextView deviceId = (TextView)findViewById(R.id.deviceId);
        if (status.equalsIgnoreCase("notfound")) {
            register.setText(" Register!");
            //register.setOnClickListener(new View.OnClickListener(){
              //  @Override
                //public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);
            //    //}
            //});
            Log.i("Not Found", "Device");
        } else if (status.equalsIgnoreCase("found")) {
            deviceId.append(" "+id+"!");
            register.setText(" Try out!");
            register.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                    startActivity(intent);
                }
            });
            Log.i("Found", "Device");
        }
    }


    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < macBytes.length; i++) {
                    sb.append(String.format("%02X%s", macBytes[i], (i < macBytes.length - 1) ? "-" : ""));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return getMacAddress();
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            data.append(readData);
        }
        reader.close();
        return data.toString();
    }

    public static String getMacAddress(){
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return "02:00:00:00:00:00";
        }
    }

    private long mLastClickTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    i++;
                    TextView tv = (TextView) findViewById(R.id.ble);
                    if(i % 2 == 0) {
                        if(address != "") {
                            Spinner sItems = (Spinner) findViewById(R.id.spinner);
                            monitoring = sItems.getSelectedItem().toString();
                            Log.i("Event", "Triggered with address " + address + " for monitoring " + monitoring);
                            Log.i("Compare", "Previous mon - " + prevMonitoring + " & monitoring - " + monitoring);
                            if (SystemClock.elapsedRealtime() - mLastClickTime > 30000 || !prevMonitoring.equalsIgnoreCase(monitoring)) {
                                Log.i("Insert", "New Event");
                                insertEvent(address, monitoring);
                                prevMonitoring = monitoring;
                                Log.i("Set prev Mon ", prevMonitoring);
                                Log.i("Time", String.valueOf(mLastClickTime));
                            }else{
                                tv.setText("Same observation has already been received from this device for the same place.");
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
                                }else{
                                    v.vibrate(500);
                                }
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                        }else{
                            Log.i("MAC address", "Not Found");
                            tv.append("Unable to get Device Information. Please try again!");
                        }
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    String location;

    public void insertEvent(String macId, String monitoring){
        findViewById(R.id.loadPanel).setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.ble);
//        tv.append("\n" + monitoring);

        Log.i("saveEvent", "Started");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                location = getLocation();
            }
        });

        try {
            JSONObject obj = new JSONObject();
            obj.put("device_mac", macId);
            obj.put("device_id", macId);
            obj.put("latitude", location.split("#")[0]);
            obj.put("longitude", location.split("#")[1]);
            obj.put("type", monitoring);

            RequestQueue queue = Volley.newRequestQueue(this);
            Spinner sItems = (Spinner) findViewById(R.id.spinner);
            final String monitorEvent= sItems.getSelectedItem().toString();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlForRest, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response for get", response.toString());
                            try {
                                Object status = response.get("status");
                                TextView device = (TextView) findViewById(R.id.ble);
                                if (status.toString().equalsIgnoreCase("failed")){
                                    Object deviceStatus = response.get("device_id");
                                    Log.i("Device status", deviceStatus.toString());
                                    if(deviceStatus.toString().toLowerCase().contains("contact")){
                                        device.setText("Sorry your device is marked inactive. Please Contact admin!");
                                        findViewById(R.id.loadPanel).setVisibility(View.GONE);
                                        addNotification("Sorry! Your device is marked inactive! Please contact admin!");
                                    }else if(deviceStatus.toString().toLowerCase().contains("register")){
                                        device.append("Device Not Registerd. Please register and try again!");
                                        findViewById(R.id.loadPanel).setVisibility(View.GONE);
                                        addNotification("Sorry! Your device is not registered! PLesae register and try again!");
                                    }
                                }else{
                                    JSONObject result = (JSONObject)response.get("result");
                                    Object event_id = result.get("event_id");
                                    Log.i("Event id generated - ", event_id.toString());
                                    device.setText("Your observation has been successfully registered for "+monitorEvent+"!");
                                    findViewById(R.id.loadPanel).setVisibility(View.GONE);
                                    addNotification("Your observation has been saved succesfully for "+monitorEvent+"!");
                                }
                                Log.i("Status Tag", status.toString());
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
    }

    public void getEventTypes(){
        findViewById(R.id.loadPanel).setVisibility(View.VISIBLE);
        Log.i("get Event", "Started");
        try {
            JSONObject obj = new JSONObject();
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlForEventGet, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response for get", response.toString());
                            try {
                                Object status = response.get("status");
                                if (status.toString().equalsIgnoreCase("success")){
                                    types = (JSONArray) response.get("result");
                                    populateSpinner(types);
                                }else{
                                }
                                Log.i("Status Tag", status.toString());
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
    }

    private void populateSpinner(final JSONArray types){
        List<String> spinnerArray =  new ArrayList<String>();
        try {
            for (int i = 0; i < types.length(); i++) {
                Log.i("Types - ", types.get(i).toString());
                spinnerArray.add(types.get(i).toString().split("#")[0]);
            }
            Log.i("populating spinner", spinnerArray.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
            Log.i("inside", "populate spinner ");
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner sItems = (Spinner) findViewById(R.id.spinner);
            sItems.setAdapter(adapter);
            findViewById(R.id.loadPanel).setVisibility(View.GONE);
            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.i("checking", "spinner change");
                    Spinner sItems = (Spinner) findViewById(R.id.spinner);
                    final String monitoring = sItems.getSelectedItem().toString();
                    TextView details = (TextView) findViewById(R.id.details);
                    try {
                        for (int i = 0; i < types.length(); i++) {
                            if (monitoring.equalsIgnoreCase(types.get(i).toString().split("#")[0])) {
                                details.setText(types.get(i).toString().split("#")[1]);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
        }catch (Exception e){
            e.printStackTrace();
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

    private void addNotification(String status) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.information)
                .setContentTitle("cSensing")
                .setContentText(status);

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}
