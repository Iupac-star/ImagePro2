package com.example.imagepro2;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.imagepro2.ml.FaceRecognition;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static android.content.ContentValues.TAG;

public class confirmation_activity extends AppCompatActivity {
    Button verify;
    ImageView frame;
    Bitmap bmpImage;
    TextView student_name, student_programme, index_number;
    ConnectivityManager connectivityManager;
    String currLocation;
   // String defLocation = "Kwame Nkrumah University of Science and Technology (KNUST)";
    List<Address> geoCoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        //checking for network


        //get current location and assign it to currLocation.
        currLocation = getLocation();

        Log.d(TAG, "onCreate: address is: "+currLocation);

        // binding the frame.
        frame = (ImageView)findViewById(R.id.picture_frame);

        //creating image path.
        String picturePath = Environment.getExternalStorageDirectory()+"/ImagePro2/frame.jpg";

        //converting Mat image to Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmpImage = BitmapFactory.decodeFile(picturePath,options);

        //setting the image frame to the bitmap image taken.
        frame.setImageBitmap(bmpImage);

        //binding the button
        verify = (Button)findViewById(R.id.btnVerify);

        //setting an onclick listener on the button
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(ContextCompat.checkSelfPermission(confirmation_activity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                            && currLocation.contentEquals("Accra Rd, Kumasi, Ghana")){
                        runModel(bmpImage);
                    }else if(ContextCompat.checkSelfPermission(confirmation_activity.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
                        Toast.makeText(confirmation_activity.this, "Granted permission for loacation first", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(confirmation_activity.this, "You are not currently in school", Toast.LENGTH_LONG).show();
                    }


            }
        });

    }

    public void runModel(Bitmap bmp) {
        
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp,200,200,true);
        try {

            FaceRecognition model = FaceRecognition.newInstance(confirmation_activity.this);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 200, 200, 3}, DataType.FLOAT32);
            TensorImage tensorImage = TensorImage.fromBitmap(scaledBitmap);
            TensorImage tensorImage1 = TensorImage.createFrom(tensorImage,DataType.FLOAT32);

            ByteBuffer byteBuffer = tensorImage1.getBuffer();

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            FaceRecognition.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            student_name = (TextView)findViewById(R.id.studentName);
            student_programme = (TextView)findViewById(R.id.programme);
            index_number = (TextView)findViewById(R.id.indexNumber);
            if(outputFeature0.getFloatValue(0)==1.0){
                student_name.setText("ADAMS SUMAILA");
                student_programme.setText("BSC. COMPUTER SCIENCE");
                index_number.setText("4621518");
            }else if(outputFeature0.getFloatValue(1)==1.0){
                student_name.setText("CONFIDENCE KOFI BOATENG");
                student_programme.setText("BSC. PHYSICS");
                index_number.setText("462152");
            }else if(outputFeature0.getFloatValue(2)==1.0){
                student_name.setText("ABDUL-KARIM ABDL - GANIW");
                student_programme.setText("BSC. COMPUTER SCIENCE");
                index_number.setText("4600718");
            }else if(outputFeature0.getFloatValue(3)==1.0){
                student_name.setText("UNKOWN");
                student_programme.setText("NULL");
                index_number.setText("NULL");
            }else if(outputFeature0.getFloatValue(4)==1.0){
                student_name.setText("RANSFORD AMPERSIL");
                student_programme.setText("BSC. COMPUTER SCIENCE");
                index_number.setText("4630718");
            }else {
                Toast.makeText(this, "No Person Detected in frame", Toast.LENGTH_SHORT).show();
            }
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
            e.printStackTrace();
        }

    }

    private String getLocation(){

            try {
                geoCoder = new Geocoder(this).getFromLocationName("Kwame Nkrumah University of Science and Technology (KNUST), Accra Rd, Kumasi",1);
            }catch (Exception e){
                e.printStackTrace();
            }
            currLocation = geoCoder.get(0).getAddressLine(0);
            return currLocation;
        }




}