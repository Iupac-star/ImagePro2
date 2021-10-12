package com.example.imagepro2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import static android.content.ContentValues.TAG;

public class Dashboard extends AppCompatActivity {
    static   {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is loaded");
        }else{
            Log.d(TAG, "OpenCv failed to load");
        }
    }

    TextView verifyMe, location;
    ConnectivityManager connectivityManager;
    boolean isConnected;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //getting network status
        registeredNetwork();

        //finding views
        verifyMe = findViewById(R.id.bv);
        // location = findViewById(R.id.check_location);
        //fees = findViewById(R.id.check_fees);


        verifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected){
                    Intent intent = new Intent(Dashboard.this, camera_activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else{
                    Toast.makeText(Dashboard.this, "Please make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
                }


            }
        });

        location = findViewById(R.id.check_location);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected){
                    Intent locations = new Intent(Dashboard.this, Location.class).addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(locations);
                }else{
                    Toast.makeText(Dashboard.this, "Please make sure you are connected to the internet", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void registeredNetwork() {
        try {
            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected = true;

                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected = false;
                }

            });


        }catch (Exception e){
            isConnected = false;
        }
    }
}