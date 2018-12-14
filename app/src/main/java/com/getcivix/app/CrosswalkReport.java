package com.getcivix.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.getcivix.app.civixteam.R;

public class CrosswalkReport extends Activity implements View.OnClickListener{

    // Bottom navigation buttons
    Button buttonHome;
    Button buttonMap;
    Button buttonList;
    Button buttonStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk_report);

        buttonHome = findViewById(R.id.buttonHome);
        buttonMap = findViewById(R.id.buttonMap);
        buttonList = findViewById(R.id.buttonList);
        buttonStatus = findViewById(R.id.buttonStatus);


        buttonHome.setOnClickListener(this);
        buttonMap.setOnClickListener(this);
        buttonList.setOnClickListener(this);
        buttonStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // If we click on "Home" button, then...
        // we should land on Home Page

        if (v == buttonHome) {
            Intent intentHome = new Intent(this, HomePage.class);
            startActivity(intentHome);
        }


        // If we click on "Map" button, then...
        // we should land on Map Page
        if (v == buttonMap) {
            Intent intentMap = new Intent(this, MapPage.class);
            startActivity(intentMap);
        }

        // If we click on "List" button, then...
        // we should land on List Page
        else if (v == buttonList) {
            Intent intentList = new Intent(this, ListPage.class);
            startActivity(intentList);
        }
        // If we click on "Status" button, then...
        // we should land on Status Page
        else if (v == buttonStatus) {
            Intent intentStatus = new Intent(this, StatusPage.class);
            startActivity(intentStatus);
        }

    }
}
