package com.ble.dreamworld.battery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
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
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class MainActivity extends AppCompatActivity {

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    Button sendData;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    String urlForRest = "http://192.168.0.118:8080/saveData";

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String status;
        if(bluetooth.isEnabled()){
            String mydeviceaddress = bluetooth.getAddress();
            String mydevicename = bluetooth.getName();
            status = mydevicename + " - " + mydeviceaddress;
        }else{
            status = "Bluetooth is not enabled";
        }

        status = status + " - " + bluetooth.getName();

        TextView tv = (TextView) findViewById(R.id.ble);

        tv.append(status);
        Toast.makeText(this, status, Toast.LENGTH_LONG).show();

        String location = getLocation();


        sendData = (Button)findViewById(R.id.button);
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
            }
        });
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();
        TextView tv = (TextView) findViewById(R.id.ble);

        tv.append("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            TextView tv = (TextView) findViewById(R.id.ble);

                                            tv.append(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    String location;
    private void insertData() {
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
            params.put("username", "test");
            params.put("latitude", location.split("#")[0]);
            params.put("longitude", location.split("#")[1]);
            params.put("value", String.valueOf(test));
            final String requestBody = params.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlForRest, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                    Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    Toast.makeText(MainActivity.this, error+"", Toast.LENGTH_SHORT).show();
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