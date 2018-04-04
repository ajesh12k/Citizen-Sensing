package com.scala.lut.csensing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onNavigateUp(){
        Log.i("Back", "Clicked");
        finish();
        return true;
    }
}
