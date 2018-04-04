package com.scala.lut.csensing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Register extends AppCompatActivity {
    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public int ENABLE_BT_REQUEST_CODE = 1;
    String urlForRest = "https://csensing-angular-ajesh12k.c9users.io/saveDevice";
    String urlForRegisterCheck = "https://csensing-angular-ajesh12k.c9users.io/checkDevice";

    String address = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        address = getMacAddr();
        final TextView bleId = (TextView) findViewById(R.id.bleID);
        bleId.setText(address);
        checkDevice(address);
        Button save = (Button)findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText devieName = (EditText) findViewById(R.id.deviceName);
                String address = bleId.getText().toString();
                String deviceName = devieName.getText().toString();
                saveDevice(address, deviceName);
            }
        });

        /*if(!bluetooth.isEnabled()){
            bleId.setText("Bluetooth Turned off! Please turn on bluetooth and connect the device to register.");
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
        }else {
            ListPairedDevices();
            String address = bleId.getText().toString();
            checkDevice(address);
        }*/
    }

    @Override
    public void onBackPressed() {
        return;
        //        moveTaskToBack(true);
        //        finish();
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

    public String saveDevice(String macId, String deviceName){
        Log.i("Device", "Saving Started");
        try {
            JSONObject obj = new JSONObject();
            obj.put("device_mac", macId);
            obj.put("device_id", deviceName);
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlForRest, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Response for get", response.toString());
                            try {
                                Object status = response.get("status");
                                if (status.toString().equalsIgnoreCase("success")){
                                    doRegister("success");
                                }else{
                                    doRegister("failed");
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
        return "wait";
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
                                        checkRegisterStatus("found");
                                    }else{
                                        checkRegisterStatus("notFound");
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

    public void checkRegisterStatus(String status){
        Button save = (Button)findViewById(R.id.button1);
        TextView device = (TextView) findViewById(R.id.deviceStatus);
        EditText devieName = (EditText) findViewById(R.id.deviceName);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        device.setVisibility(View.GONE);
        Log.i("status RE", device.getText().toString());
        if (status.equalsIgnoreCase("notfound")) {
            save.setVisibility(View.VISIBLE);
            devieName.setVisibility(View.VISIBLE);
            Log.i("False", "Not Registered");
        } else if (status.equalsIgnoreCase("found")) {
            save.setVisibility(View.GONE);
            devieName.setVisibility(View.GONE);
            device.setVisibility(View.VISIBLE);
            device.setText("Device already regsteired!");
        }
    }

    public void doRegister(String status){
        Button save = (Button)findViewById(R.id.button1);
        TextView device = (TextView) findViewById(R.id.deviceStatus);
        EditText devieName = (EditText) findViewById(R.id.deviceName);
        if(status.equalsIgnoreCase("success")){
            save.setVisibility(View.GONE);
            devieName.setVisibility(View.GONE);
            device.setVisibility(View.VISIBLE);
            device.setText("Device registered successfully!");
        }else{
            device.setText("Unable to register device! Please try again later!");
        }
    }
}