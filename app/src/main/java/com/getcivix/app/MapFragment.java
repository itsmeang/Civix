package com.getcivix.app;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
//Important line addition below
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {




    GoogleMap mMap;
    MapView mMapView;
    View mView;
    public AppCompatActivity thisActivity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;


    public MapFragment() {
        // Required empty public constructor
    }

    public void setThisActivity(AppCompatActivity thisActivity) {
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


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkUserLocationPermission();

        }

        return mView;
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
        //MapsInitializer.initialize(getContext());

        mMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(ContextCompat.checkSelfPermission(thisActivity,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {   // TODO: Consider calling

            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);
        }
        else{

            googleMap.addMarker(new MarkerOptions().position(new LatLng(42.272809,  -83.736936)).title("Ross School Of Business").snippet("Best MBA Program in the Universe"));

            CameraPosition Ross = CameraPosition.builder().target(new LatLng(42.272809,  -83.736936)).zoom(16).bearing(0).tilt(45).build();

            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Ross));

        }





        //

        //Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        //double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();

        //


        //The below is original
        /*
        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247,-74.044502)).title("Statue of Liberty").snippet("I hope to go there some day"));

        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247,-74.044502)).zoom(16).bearing(0).tilt(45).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
        */
        //The above is original

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
                            buildGoogleApiClient();

                        }

                        mMap.setMyLocationEnabled(true);


                    }

                }
           else {
                    Toast.makeText(thisActivity,"Permission denied...",Toast.LENGTH_SHORT).show();

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





