package com.example.imagepro2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class camera_activity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "static initializer: OpenCV is loaded successfully");
        }else{
            Log.d(TAG, "static initializer: OpenCV failed to load. Try running the app again");
            OpenCVLoader.initDebug();
        }
    }
    Mat mRgba; //mat variable image in rgb
    Mat mGray; // mat variable for gray image
    JavaCameraView javaCameraView; //java cameraview
    Button buttonCapture;
    CascadeClassifier cascadeClassifier;


    //java2camera callback
    BaseLoaderCallback loaderCallback = new BaseLoaderCallback(camera_activity.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:{
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //removes the title bar on the inteface
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //keeps the screen on once app is executed.

        if(ContextCompat.checkSelfPermission(camera_activity.this, Manifest.permission.CAMERA)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(camera_activity.this,new String[]{Manifest.permission.CAMERA},100);
        }//checks if camera permission is granted

        if(ContextCompat.checkSelfPermission(camera_activity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(camera_activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
        } //checks if read permission is granted

        if(ContextCompat.checkSelfPermission(camera_activity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(camera_activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        } //checks if reading external storage permission is granted
        if(ContextCompat.checkSelfPermission(camera_activity.this,Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(camera_activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }

        //setting the content view to avitivity_camera.xml
        setContentView(R.layout.activity_camera);

        javaCameraView = (JavaCameraView)findViewById(R.id.java_cameraView); //binding the cameraview
        javaCameraView.setCameraIndex(1); //index camera of 1 indicate front camera to be used.
        javaCameraView.setCvCameraViewListener(this); //setting the listener on this frame
        javaCameraView.setVisibility(View.VISIBLE);

        //reading haar cascade for image detection
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
            File fileDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(fileDir, "haarcascade_frontalface_default.xml");
            FileOutputStream fileOutputStream = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            inputStream.close();
            fileOutputStream.close();

            cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //binding the capture button
        buttonCapture = findViewById(R.id.btn_capture);

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(mRgba);
                Intent intent = new Intent(camera_activity.this,confirmation_activity.class);
                startActivity(intent);

            }
        });


    }

    //implemented method of java camera
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height,width, CvType.CV_8UC4);
        mGray = new Mat(height,width,CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        //face_recognition.recognizeImage(mRgba);
        Mat out = new Mat();
        //faceRecognition.recognizeImage(mRgba);

        out = cascadeRec(mRgba);

        return out;
    }

    //defines the mat
    private Mat cascadeRec(Mat mRgba) {
        Core.flip(mRgba,mRgba,-1);
        Mat mRgb = new Mat();
        Imgproc.cvtColor(mRgba,mRgb,Imgproc.COLOR_RGBA2RGB);
        int height = mRgb.height();
        int absoluteFaceSize = (int) (height*0.1);

        MatOfRect faces = new MatOfRect();
        //mat image to the classifier
        if(cascadeClassifier!=null){
            cascadeClassifier.detectMultiScale(mRgb,faces,1.1,2,2,new Size(absoluteFaceSize,absoluteFaceSize),new Size());
        }

        //finds the faces in the image
        Rect[] faceArrays = faces.toArray();
        for(int i = 0; i<faceArrays.length; i++){
            Imgproc.rectangle(mRgba,faceArrays[i].tl(),faceArrays[i].br(),new Scalar(0,255,0,255),2);
        }
        return mRgba;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(OpenCVLoader.initDebug()){
            javaCameraView.enableView();
        }else{
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,loaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(javaCameraView!=null){
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        javaCameraView.disableView();
    }

    protected void onResume(){
        super.onResume();
        if(OpenCVLoader.initDebug()){
            javaCameraView.enableView();
        }
    }

    public void saveImage(Mat mRgba){
        Bitmap bmp = null;
        Imgproc.cvtColor(mRgba,mRgba,Imgproc.COLOR_RGBA2RGB);


        //creating a bitmap from mRgba
        try {
            //  Core.flip(mRba1,mRba1,-1);
            bmp = Bitmap.createBitmap(mRgba.cols(),mRgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgba,bmp);
        }catch (Exception e){
            e.printStackTrace();
        }

        Matrix mat = new Matrix();
        mat.postRotate(90);

        Bitmap bmpRotate = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),mat,true);


        mRgba.release();


        String fileName = "frame.jpg";
        FileOutputStream outputStream = null;
        //Creating folder in the phones directory
        File folder = new File(Environment.getExternalStorageDirectory()+"/ImagePro2");

        //make folder if folder doesn't exist
        boolean success = true;
        if(!folder.exists()){
            success = folder.mkdirs();
        }

        if(success){
            File destination = new File(folder,fileName);
            try {
                outputStream = new FileOutputStream(destination);
                bmpRotate.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(outputStream!=null){
                        outputStream.close();
                        Toast.makeText(this, "Frame is saved", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }


}