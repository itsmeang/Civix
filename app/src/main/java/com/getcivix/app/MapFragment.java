package com.getcivix.app;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getcivix.app.Models.ReportInfo;
import com.google.android.gms.common.ConnectionResult;
//Important line addition below
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    //    GoogleMap mMap;
    MapView mMapView;
    View mView;
    public Activity thisActivity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;


    private static final String TAG="MapFragment";

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

    public void setmLocationPermissionGranted(Boolean mLocationPermissionGranted) {
        this.mLocationPermissionGranted = mLocationPermissionGranted;
    }

    //vars
    private Boolean mLocationPermissionGranted=false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private ReportInfo mPlace;
    private MapPage mapPage;

    //Button to Start Report
    private FloatingActionButton floatingActionButton;


    public MapFragment() {
        // Required empty public constructor
    }

    public void setMapFragment(MapPage mapPage) {
        // Required empty public constructor
        this.mapPage=mapPage;
    }

    public void setThisActivity(Activity thisActivity) {
        this.thisActivity = thisActivity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_map, container, false);

        mView = inflater.inflate(R.layout.fragment_map, container, false);


        isServicesOK();
        mSearchText=(AutoCompleteTextView) mView.findViewById(R.id.input_search);
        mGps=(ImageView)mView.findViewById(R.id.ic_gps);


        floatingActionButton = mView.findViewById(R.id.floatingActionButton);


        final FloatingActionButton floatingActionButton = mView.findViewById(R.id.floatingActionButton);

        //myButton.setOnClickListener(new View.OnClickListener() {


        floatingActionButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                showMyDialog (getContext());

            }
        });


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkUserLocationPermission();

        }
        return mView;
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
                //showMyDialog(getContext());
                //link to second dialog box
                dialog.dismiss();
                ((MapPage)thisActivity).loadFragment(new ProfileFragment());
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.civix_map);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Toast.makeText(thisActivity, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }


    public boolean isServicesOK(){
        Log.d(MAIN, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            Log.d(MAIN, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(MAIN, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{
            Toast.makeText(this.getActivity(), "you can't make map requestd", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }








    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(thisActivity)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this.getActivity(), this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter=new PlaceAutocompleteAdapter(thisActivity,mGoogleApiClient,LAT_LNT_BOUNDS,null);

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
        /*
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        }); */

        hideSoftKeybard();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString=mSearchText.getText().toString();

        Geocoder geocoder=new Geocoder(thisActivity);
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


    //google places API
    private AdapterView.OnItemClickListener mAutocompleteClickListener= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
            hideSoftKeybard();

            final AutocompletePrediction item=mPlaceAutocompleteAdapter.getItem(i);
            final String placeId=item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };



    private void hideSoftKeybard(){
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback=new ResultCallback<PlaceBuffer>() {
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

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        mFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(thisActivity);

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

                            StaticConstants.reportLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                Log.d(TAG,"mlocationPermission   "+ mLocationPermissionGranted);
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


    public boolean checkUserLocationPermission() {

        if(ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {

                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);

            }
            return false;


        }
        else {

            mLocationPermissionGranted = true;

            return true;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_User_Location_Code:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                        if(googleApiClient==null) {
//                            buildGoogleApiClient();
                            mLocationPermissionGranted=true;

                        }

                        mMap.setMyLocationEnabled(true);


                    }

                }
                else {
                    Toast.makeText(thisActivity,"Permission denied...",Toast.LENGTH_SHORT).show();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(42.272809, -83.736936)).title("Ross School Of Business").snippet("Best MBA Program in the Universe"));

                    CameraPosition Ross = CameraPosition.builder().target(new LatLng(42.272809, -83.736936)).zoom(16).bearing(0).tilt(45).build();

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(Ross));

                }
                return;
        }
    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient=new GoogleApiClient.Builder(thisActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }



    /*

    ADDITION TO THE BASIC WORKING CODE ABOVE. METHODS FOR IMPLEMENTED INTERFACES
     OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener

     */




    @Override
    public void onLocationChanged(Location location) {

        lastLocation=location;

        if(currentUserLocationMarker!=null) {
            currentUserLocationMarker.remove();


        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("User current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker=mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if(googleApiClient!=null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        //in milliseconds
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //getting user location
        if(ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}





