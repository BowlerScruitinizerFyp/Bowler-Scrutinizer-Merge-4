package application.scrutinizer.bowler.bowlerscrutinizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.HardwarePropertiesManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.goodFeaturesToTrack;
import static org.opencv.imgproc.Imgproc.line;

public class AnalyseActivity extends AppCompatActivity {

    TextView button;
    ImageView imageview;
    private TextView scan;
    StorageReference myreference;
    DatabaseReference mDatabaseRef;


    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVE_INSTANCE_URI = "uri";
    private static final String SAVE_INSTANCE_RESULT = "result";


    ImageView imageView;
    Bitmap imageBitMap;
    TextView tx1;
    String TAG = "MainActivity";

    Uri ImageUri;
    Bitmap grayBitMap;
    Bitmap cannyMap;
    Bitmap blurMap;


    String UrlDownload;


    // For showing Results

    FirebaseAuth mAuth;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);


        //check the loading of opencv

        if (!OpenCVLoader.initDebug()) {

            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }

        init();
    }

    private void init() {

        imageView = (ImageView)findViewById(R.id.image_view);
        tx1 = (TextView) findViewById(R.id.textView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    //camera button clicked
    public void OpenCamera(View view) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 0);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        imageBitMap = (Bitmap)data.getExtras().get("data");
//
//        imageView.setImageBitmap(imageBitMap);
//    }

    //Save button clicked
    public void Save(View view) {
    }






    //to open the phone gallery

    public void OpenGallery(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 100);

    }


    //after image is selected from phone gallery

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK && data != null){

            imageBitMap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(imageBitMap);

        }

        else

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            ImageUri = data.getData();

            try {

                imageBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            imageView.setImageBitmap(imageBitMap);



        }


    }


    //Analyse button clicked



    //to view only the red part of the image

    public void red1(View view) {


        int width = imageBitMap.getWidth();
        int height = imageBitMap.getHeight();

        //initializing mat

        Mat Rgba = new Mat();
        Mat hsv = new Mat();
        Mat gray = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inDither = false;
        o.inSampleSize = 4;

        grayBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        //convert bitmap to Mat

        Utils.bitmapToMat(imageBitMap, Rgba);


        //getting HSV image for red colour

        Imgproc.cvtColor(Rgba,hsv , Imgproc.COLOR_BGR2HSV);


        // range of red colour

        Scalar lower_red = new Scalar(110,50,50);
        Scalar upper_red = new Scalar(130,255,255);

        Mat red = new Mat(); // mask

        Core.inRange(hsv, lower_red, upper_red , red);

        //getting the red part of image

        Mat redSkin = new Mat(red.rows(), red.cols(), CvType.CV_8U, new Scalar(3));

        Core.bitwise_and(Rgba,Rgba, redSkin , red);

        Bitmap currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        MatOfPoint corners = new MatOfPoint();

        Imgproc.cvtColor(redSkin, gray, Imgproc.COLOR_RGB2GRAY);

        //only getting 3 points that is the elbow , shoulder and wrist

        goodFeaturesToTrack(gray, corners, 3, 0.1, 50);


        TextView tx1 = (TextView) findViewById(R.id.textView);

        double theta = 0;

        Point[] cornerpoints = corners.toArray();

        for (Point points : cornerpoints) {
            circle(redSkin, points, 1, new Scalar(100, 100, 100), 6);

            //for line

            for (int i = 0 ; i < cornerpoints.length ; i++) {

                for(int j = 1 ; j < cornerpoints.length ; j++) {


                    // if(cornerpoints[i].x == 1 && cornerpoints[i].x == 8 && cornerpoints[i].y == 15 && cornerpoints[i].y == 22 && cornerpoints[i].x == 29 && cornerpoints[i].y == 53  && cornerpoints[i].x == 37 && cornerpoints[i].y == 45 ) {

                    line(Rgba, cornerpoints[i], cornerpoints[j], new Scalar(0, 255, 0), 2);
                    //}

                }

            }
            //for line



            //getting the coordinates

            double x1 = cornerpoints[0].x;
            double y1 = cornerpoints[0].y;

            double x2 = cornerpoints[1].x;
            double y2 = cornerpoints[1].y;

            double x3 = cornerpoints[2].x;
            double y3 = cornerpoints[2].y;


            //slope method

            double m1 = x2-x1/y2-y1;
//                 double m2 = y4-y3/x4-x3;

            m1 = m1 - pow(m1,3)/3 + pow(m1,5)/5;
            m1 = ((int)(m1*180/3.14)) % 360; // Convert the angle in radians to degrees
            if(x1 < x2) m1+=180;
            if(m1 < 0) m1 = 360 + m1;


//                 double tangent = m2-m1/1-m1*m2;


            //distance method

            double dis1 = sqrt(pow(x2-x1 , 2) + pow(y2-y1 , 2));   //Distance = √(x2−x1)2+(y2−y1)2

            double dis2 = sqrt(pow(x3-x2 , 2) + pow(y3-y2 , 2));   //Distance = √(x2−x1)2+(y2−y1)2

            double sin = dis1 / dis2 ; // Sinθ = Perpendicular/hypotenuse

            theta = Math.asin(sin) * (180/3.14);



            //tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);


            if(sin >= 1){

                String angle = "0." + Long.toString(Math.round(sin));
                float a = Float.parseFloat(angle);
                theta = Math.asin(a)*(180/3.14);



                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);

            }

            else{

                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);

            }



        }


        //get Dialog activity

        final View vew = getLayoutInflater().inflate(R.layout.activity_results,null);
        final TextView show = (TextView) vew.findViewById(R.id.GetTheta);
        final TextView legal = (TextView) vew.findViewById(R.id.Getresult);
        show.setText("\ntheta = " +theta);

        if(theta <= 15){
            legal.setText("Hurray Your Angle of Bowling is Legal");
        }

        else if(theta > 15){

            legal.setText("Sorry Your Angle of Bowling is not Legal");
        }



        //For Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(AnalyseActivity.this);

        final TextView close = (TextView) vew.findViewById(R.id.ClosePop);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setCancelable(false);

            }
        });


        final double finalTheta = theta;
        builder.setMessage("Angle found").
                setView(vew)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String Result = Double.toString(finalTheta);
                        if(Result != null) {
                            //progressBar.setVisibility(View.VISIBLE);
                            mDatabase.child(mAuth.getUid()).child("Theta").push().setValue(Result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AnalyseActivity.this , "Theta saved" , Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    //  progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AnalyseActivity.this , "Some error accured" , Toast.LENGTH_SHORT).show();

                                }
                            });
                        }


                    }
                }).
                setNegativeButton("cancel",null)
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
        //Converting Mat back to Bitmap
        Utils.matToBitmap(redSkin, currentBitmap);
        imageView.setImageBitmap(currentBitmap);

    }

    public void cord(View view) {

        int width = imageBitMap.getWidth();
        int height = imageBitMap.getHeight();

        //initializing mat

        Mat Rgba = new Mat();
        Mat hsv = new Mat();
        Mat gray = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inDither = false;
        o.inSampleSize = 4;

        grayBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        //convert bitmap to Mat

        Utils.bitmapToMat(imageBitMap, Rgba);


        //getting HSV image for red colour

        Imgproc.cvtColor(Rgba,hsv , Imgproc.COLOR_BGR2HSV);


        // range of red colour

        Scalar lower_red = new Scalar(110,50,50);
        Scalar upper_red = new Scalar(130,255,255);

        Mat red = new Mat(); // mask

        Core.inRange(hsv, lower_red, upper_red , red);

        //getting the red part of image

        Mat redSkin = new Mat(red.rows(), red.cols(), CvType.CV_8U, new Scalar(3));

        Core.bitwise_and(Rgba,Rgba, redSkin , red);

        Bitmap currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        MatOfPoint corners = new MatOfPoint();

        Imgproc.cvtColor(redSkin, gray, Imgproc.COLOR_RGB2GRAY);

        //only getting 3 points that is the elbow , shoulder and wrist

        goodFeaturesToTrack(gray, corners, 3, 0.1, 50);


        TextView tx1 = (TextView) findViewById(R.id.textView);


        Point[] cornerpoints = corners.toArray();

        for (Point points : cornerpoints) {
            circle(redSkin, points, 1, new Scalar(100, 100, 100), 6);

            //for line

            for (int i = 0 ; i < cornerpoints.length ; i++) {

                for(int j = 1 ; j < cornerpoints.length ; j++) {


                    // if(cornerpoints[i].x == 1 && cornerpoints[i].x == 8 && cornerpoints[i].y == 15 && cornerpoints[i].y == 22 && cornerpoints[i].x == 29 && cornerpoints[i].y == 53  && cornerpoints[i].x == 37 && cornerpoints[i].y == 45 ) {

                    line(Rgba, cornerpoints[i], cornerpoints[j], new Scalar(0, 255, 0), 2);
                    //}

                }

            }
            //for line



            //getting the coordinates

            double x1 = cornerpoints[0].x;
            double y1 = cornerpoints[0].y;

            double x2 = cornerpoints[1].x;
            double y2 = cornerpoints[1].y;

            double x3 = cornerpoints[2].x;
            double y3 = cornerpoints[2].y;


            //slope method

            double m1 = y2-y1/x2-x1;
            double m2 = y3-y2/x3-x2;

//            m1 = m1 - pow(m1,3)/3 + pow(m1,5)/5;
//            m1 = ((int)(m1*180/3.14)) % 360; // Convert the angle in radians to degrees
//            if(x1 < x2) m1+=180;
//            if(m1 < 0) m1 = 360 + m1;


            double tangent = m1-m2/1+m1*m2;
            tangent = Math.toRadians(tangent);

            double theta = Math.atan(tangent) * (180/3.14);

            if(theta > 1){

                //  tx1.setText("m1 = "+m1+ "\n m2=" +m2+ "\n Angle="+theta);


                if(theta > 270 && theta >180){

                    theta = theta -360;

                }

                else if(theta < 270 && theta >180){

                    theta = theta -180;

                }

            }

            else if(theta < 1){

                theta = theta + 360;

                // tx1.setText("m1 = "+m1+ "\n m2=" +m2+ "\n Angle="+theta);

                if(theta > 1){

                    // theta = theta;
                    // tx1.setText("m1 = "+m1+ "\n m2=" +m2+ "\n Angle="+theta);


                    if(theta > 270 && theta >180){

                        theta = theta -360;

                    }

                    else if(theta < 270 && theta >180){

                        theta = theta -180;

                    }

                    else{
                        theta = theta;
                    }

                }

                else if(theta < 1){

                    theta = theta + 360;
                    // tx1.setText("m1 = "+m1+ "\n m2=" +m2+ "\n Angle="+theta);

                    if(theta > 270 && theta >180){

                        theta = theta -360;

                    }

                    else if(theta < 270 && theta >180){

                        theta = theta -180;

                    }

                    else{
                        theta = theta;
                    }

                }


            }

            tx1.setText("m1 = "+m1+ "\n m2=" +m2+ "\n Angle="+theta);






            //distance method

//            double dis1 = sqrt(pow(x2-x1 , 2) + pow(y2-y1 , 2));   //Distance = √(x2−x1)2+(y2−y1)2
//
//            double dis2 = sqrt(pow(x3-x2 , 2) + pow(y3-y2 , 2));   //Distance = √(x2−x1)2+(y2−y1)2
//
//            double sin = dis1 / dis2 ; // Sinθ = Perpendicular/hypotenuse
//
//            double theta = Math.asin(sin) * (180/3.14);


            //   tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);


//            if(sin >= 1){
//
//                String angle = "0." + Long.toString(Math.round(sin));
//                float a = Float.parseFloat(angle);
//                Double theta1 = Math.asin(a)*(180/3.14);
//
//                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta1);
//
//
//            }
//
//            else{
//
//                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);
//            }
        }





        //Converting Mat back to Bitmap
        Utils.matToBitmap(redSkin, currentBitmap);
        imageView.setImageBitmap(currentBitmap);


    }



}
