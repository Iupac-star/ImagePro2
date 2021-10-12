package com.example.imagepro2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;


public class Location extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat, lng;
    ConnectivityManager connectivityManager;

    //list to store geocoder information.
    List<Address> listGeoCoder;

    // request code for location access.
    private static final int locationCode = 101;

    // map view is fragment.
    SupportMapFragment mapFragment;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

            if(isLocationPermissionGranted()){
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                //populate listGeoCoder
                try{
                    listGeoCoder = new Geocoder(this).getFromLocationName("Kwame Nkrumah University of Science and Technology (KNUST), Accra Rd, Kumasi",1);
                }catch (Exception e){
                    e.printStackTrace();
                }

                //get the latitude and longitude;
                lng = listGeoCoder.get(0).getLongitude();
                lat = listGeoCoder.get(0).getLatitude();
                mapFragment.getMapAsync(this);
            }else{
                requestPermission();
            }



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng pointerLocation = new LatLng(lat, lng);
        //creating the marker with marker options
        MarkerOptions options = new MarkerOptions().position(pointerLocation);

        //zooming camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointerLocation,10));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //adds marker to the map
        mMap.addMarker(options);
    }

    // checks if permission is granted.
    private boolean isLocationPermissionGranted(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }

        //request for permission.
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},locationCode);
    }

}