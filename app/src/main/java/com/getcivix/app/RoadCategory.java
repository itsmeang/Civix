package com.getcivix.app;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class RoadCategory extends Fragment {

    public Activity thisActivity;


    public RoadCategory() {
        // Required empty public constructor
    }

    public void setThisActivity(Activity thisActivity) {
        this.thisActivity = thisActivity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View mView = inflater.inflate(R.layout.fragment_road_category, null);

        Button buttonRoadCancel = mView.findViewById(R.id.buttonRoadCancel);
        Button buttonPotholes =mView.findViewById(R.id.buttonPotholes);
        Button buttonSnow = mView.findViewById(R.id.buttonSnow);
        Button buttonSigns = mView.findViewById(R.id.buttonSigns);
        Button buttonCrosswalk = mView.findViewById(R.id.buttonCrosswalk);
        Button buttonBikeLane = mView.findViewById(R.id.buttonBikeLane);
        Button buttonLight = mView.findViewById(R.id.buttonLight);
        Button buttonSignal = mView.findViewById(R.id.buttonSignal);
        Button buttonRoadOther = mView.findViewById(R.id.buttonRoadOther);

        buttonRoadCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment fragment=new MapFragment();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        buttonSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignalReport fragment = new SignalReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        buttonPotholes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PotholesReport fragment = new PotholesReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);

            }
        });

        buttonSnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnowReport fragment = new SnowReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        buttonSigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignsReport fragment = new SignsReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        /*


        buttonCrosswalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrosswalkReport fragment = new CrosswalkReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        buttonBikeLane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BikeLaneReport fragment = new BikeLaneReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LightReport fragment = new LightReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });



        buttonRoadOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoadOtherReport fragment = new RoadOtherReport();
                fragment.setThisActivity(thisActivity);
                ((MapPage)thisActivity).loadFragment(fragment);
            }
        });

        */

        return mView;

    }

}
