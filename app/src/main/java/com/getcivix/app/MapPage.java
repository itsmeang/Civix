package com.getcivix.app;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.getcivix.app.Models.ReportInfo;
import com.getcivix.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapPage extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    //getting bottom navigation view
    BottomNavigationView navigation;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.mapmenu:
                fragment = new ProfileFragment();
                break;

            case R.id.notificationmenu:
                fragment = new ProfileFragment();
                break;

            case R.id.voicemenu:
                fragment = new ProfileFragment();
                break;

            case R.id.profilemenu:
                fragment = new ProfileFragment();
                break;

            case R.id.othermenu:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }



    private boolean loadFragment(Fragment fragment) {

        //switching fragment
        if (fragment != null) {
            Toast.makeText(MapPage.this, "fragment loaded", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager()
                    .beginTransaction()
                    //.show(fragment)
                    .replace(R.id.map, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private static final String TAG="MapActivity";

    private static final String FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION= Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE=1234;
    private static final float DEFAULT_ZOOM=15;
    private static final String MAIN = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST=9001;
    private static final LatLngBounds LAT_LNT_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    //vars
    private Boolean mLocationPermissionGranted=false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private ReportInfo mPlace;

    //Button to Start Report
    private FloatingActionButton  floatingActionButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_page);
        isServicesOK();
        mSearchText=(AutoCompleteTextView) findViewById(R.id.input_search);
        mGps=(ImageView)findViewById(R.id.ic_gps);


        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        getLocationPermission();

        floatingActionButton = findViewById(R.id.floatingActionButton);


        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        //myButton.setOnClickListener(new View.OnClickListener() {


        floatingActionButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                showMyDialog (MapPage.this);
            }
        });

    }
    private void showMyDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.fragment_report_screen);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        Button buttonRoad = (Button) dialog.findViewById(R.id.buttonRoad);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyDialog(MapPage.this);
                // do whatever you want here
            }
        });

        /**
         * if you want the dialog to be specific size, do the following
         * this will cover 85% of the screen (85% width and 85% height)
         */
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.9);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.9);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();
    }
    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNT_BOUNDS,null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH
                        ||actionId==EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER) {

                    //execute method for searching
                    geoLocate();
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeybard();
    }
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString=mSearchText.getText().toString();

        Geocoder geocoder=new Geocoder(MapPage.this);
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException: " +e.getMessage() );
        }
        if(list.size()>0){
            Address address=list.get(0);

            Log.d(TAG, "geoLocate: found a location: "+address.toString());
           //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }

    }

    public boolean isServicesOK(){
        Log.d(MAIN, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapPage.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            Log.d(MAIN, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(MAIN, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapPage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this, "you can't make map requestd", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        try{
            if (mLocationPermissionGranted) {
                final Task location=mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation=(Location)task.getResult();

                            if(currentLocation == null) {
                                return;
                            }
                            GeoPoint geoPoint=new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
                            Log.d(TAG, "onComplete: latitude: "+currentLocation.getLatitude());
                            Log.d(TAG, "onComplete: longitude: "+currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                   "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapPage.this, "unable to get current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch(SecurityException e){

            Log.e(TAG, "getDeviceLocation: SecurityException"+ e.getMessage() );
        }
    }


    private void moveCamera(LatLng latlng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        if (!title.equals("My Location")) {

            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);

            mMap.addMarker(options);
        }

    }



    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapPage.this);
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

private void hideSoftKeybard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

}

    //google places API
    private AdapterView.OnItemClickListener mAutocompleteClickListener= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
            hideSoftKeybard();

            final AutocompletePrediction item=mPlaceAutocompleteAdapter.getItem(i);
            final String placeId=item.getPlaceId();

            PendingResult<PlaceBuffer>placeResult=Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer>mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: "+places.getStatus().toString());
                places.release();
                return;

            }
            final Place place=places.get(0);

            try {

                mPlace=new ReportInfo();
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setId(place.getId());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setName(place.getName().toString());

                Log.d(TAG, "onResult: place: "+mPlace.toString());

            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: "  );
            }

            moveCamera (new LatLng((place.getViewport().getCenter().latitude), place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mPlace.getName());

            places.release();




        }
    };



}

