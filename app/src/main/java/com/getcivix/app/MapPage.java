package com.getcivix.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


public class MapPage extends FragmentActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener  {

    //getting bottom navigation view
    BottomNavigationView navigation;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.mapmenu:
                fragment = new MapFragment();
                ((MapFragment) fragment).setThisActivity(this);
                break;

            case R.id.notificationmenu:
                fragment = new SignalReport();
                break;

            case R.id.voicemenu:
                fragment = new SignalReport();
                break;

            case R.id.profilemenu:
                fragment = new ProfileFragment();
                break;

            case R.id.othermenu:
                fragment = new SignalReport();
                break;
        }

        return loadFragment(fragment);
    }



    private boolean loadFragment(Fragment fragment) {

        //switching fragment
        if (fragment != null) {
            //Toast.makeText(MapPage.this, "fragment loaded", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    //.show(fragment)
                    .replace(R.id.mapfragment_container, fragment)
                    .commit();
            return true;
            //(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        }
        return false;
    }


    private static final String TAG="MapActivity";

    private static final String FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION= Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1234;
    //vars
    private Boolean mLocationPermissionGranted=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_page);

        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getLocationPermission();


    }



    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
//        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//
//        mapFragment.getMapAsync(MapPage.this);

        Fragment mapFragment = new MapFragment();
        ((MapFragment) mapFragment).setThisActivity(this);
        ((MapFragment) mapFragment).setmLocationPermissionGranted(true);
        loadFragment(mapFragment);

    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[]permissions={Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted=false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                            mLocationPermissionGranted=false;
                        Log.d(TAG, "onRequestPermissionsResult: permission failed");
                        return;

                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                mLocationPermissionGranted=true;
                //initialize our map
                initMap();
            }
        }
    }







}

