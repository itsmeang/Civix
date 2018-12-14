package com.getcivix.app;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.getcivix.app.civixteam.R;

public class HomePage extends Activity implements View.OnClickListener{

    // Bottom navigation buttons
    Button buttonHome;
    Button buttonMap;
    Button buttonList;
    Button buttonStatus;

    /* Buttons to submit report - 9 report categories */
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        buttonHome = findViewById(R.id.buttonHome);
        buttonMap = findViewById(R.id.buttonMap);
        buttonList = findViewById(R.id.buttonList);
        buttonStatus = findViewById(R.id.buttonStatus);

        /*Get references to the widgets*/
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);


        buttonHome.setOnClickListener(this);
        buttonMap.setOnClickListener(this);
        buttonList.setOnClickListener(this);
        buttonStatus.setOnClickListener(this);

        //Make buttons listening to clicks
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        // If we click on "Home" button, then...
        // we should land on Home Page
        /*
        if (v == buttonHome) {
            Intent intentHome = new Intent(this, HomePage.class);
            startActivity(intentHome);
        }
        */

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

        else if(v==button1){
            Intent intent1=new Intent(this,TrashReport.class);
            startActivity(intent1);
        }
        else if(v==button2){
            Intent intent2=new Intent(this,StreetReport.class);
            startActivity(intent2);
        }
        else if(v==button3){
            Intent intent3=new Intent(this,SidewalkReport.class);
            startActivity(intent3);
        }
        else if(v==button4){
            Intent intent4=new Intent(this,PropertyReport.class);
            startActivity(intent4);
        }
        else if(v==button5){
            Intent intent5=new Intent(this,WaterReport.class);
            startActivity(intent5);
        }
        else if(v==button6){
            Intent intent6=new Intent(this,RampReport.class);
            startActivity(intent6);
        }
        else if(v==button7){
            Intent intent7=new Intent(this,BikeReport.class);
            startActivity(intent7);
        }
        else if(v==button8){
            Intent intent8=new Intent(this,CrosswalkReport.class);
            startActivity(intent8);
        }
        else if(v==button9){
            Intent intent9=new Intent(this,OtherReport.class);
            startActivity(intent9);
        }

    }
}
