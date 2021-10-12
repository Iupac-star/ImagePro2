package com.example.imagepro2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.android.OpenCVLoader;

import java.util.List;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    List<Geocoder> geocoderList;

    static   {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is loaded");
        }else{
            Log.d(TAG, "OpenCv failed to load");
        }
    }

    //splash screen timeout before.
    private static int SPLASH_SCREEN_TIME_OUT = 6000;


    ImageView splash_image;
    TextView splash_text;


    Animation image, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //finding views
        splash_image = findViewById(R.id.splash_logo);
        splash_text = findViewById(R.id.splash_text);

        text = AnimationUtils.loadAnimation(this,R.anim.text_anim);
        splash_text.setAnimation(text);

        image = AnimationUtils.loadAnimation(this,R.anim.logo_anim);
        splash_image.setAnimation(image);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN_TIME_OUT);


    }


}