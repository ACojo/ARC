package com.myapp.arc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ImageAnalysis.Analyzer{

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {


        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.d("TAG", "OpenCV Loaded Sucessfully");
                }break;
                default:
                {
                    super.onManagerConnected(status);
                }break;
            }
        }
    };


    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    PreviewView previewView;
    Button infoBtn;
    TextView markerIdText;
    ImageView imageView;
    //    ImageCapture imageCapture;
    ConstraintLayout constraintLayout;
    private ImageAnalysis imageAnalysis;

    private static final long ANALYZER_DELAY = (long) 5; // wait 3 ms between the frames so that the phone can process
    private static final long FIRST_ANALYZER_CALL = (long) -1;
    private long lastAnalyzerCall = FIRST_ANALYZER_CALL;

    Aruco_c myAruco;
    List<Mat> replacementImageMat;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //for the opencv initialization
        if(!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for Initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mOpenCVCallBack);
            //OpenCVLoader.O
        }else{
            Log.e("OpenCV", "OpenCV library found inside package. Using it!");
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


        myAruco = new Aruco_c();
        replacementImageMat = new ArrayList<>();
        Mat replacementMat = new Mat();
        //adding the lightbulb
        Bitmap replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img11);
        Utils.bitmapToMat(replaceBitmap, replacementMat);
        Imgproc.cvtColor(replacementMat, replacementMat, Imgproc.COLOR_RGBA2RGB);

        replacementImageMat.add(replacementMat);

        //if not used another mat I would have the same img twice in the list
        Mat replacementMat2 = new Mat();
        replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img21);
        Utils.bitmapToMat(replaceBitmap, replacementMat2);
        Imgproc.cvtColor(replacementMat2, replacementMat2, Imgproc.COLOR_RGBA2RGB);

        replacementImageMat.add(replacementMat2);

        Mat replacementMat3 = new Mat();
        replaceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img100);
        Utils.bitmapToMat(replaceBitmap, replacementMat3);
        Imgproc.cvtColor(replacementMat3, replacementMat3, Imgproc.COLOR_RGBA2RGB);

        replacementImageMat.add(replacementMat3);

        previewView = findViewById(R.id.previewView);
        imageView = findViewById(R.id.imageView);
        //infoBtn = findViewById(R.id.infoBtn);
        markerIdText = findViewById(R.id.markerIdText);
        constraintLayout = findViewById(R.id.ConstraintLayout);

        //infoBtn.setOnClickListener(this);

        //previewView.setOnTouchListener(onTouchListener);
        imageView.setOnTouchListener(onTouchListener);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }, getExecutor() );
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        // camera selector use case
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        //preview use case
        Preview preview =  new Preview.Builder().build();

        //the preview is ready to receive data
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

//        imageCapture = new ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build();

        // image analysis use case
        imageAnalysis = new ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                //Log.d("TAG","in analyse loop");
                //Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250);
//                Aruco_c myAruco = new Aruco_c();
                //Aruco_c_f myAruco = new Aruco_c_f(dict);
                Image img;
                //img = image.getImage();


                // this augments the frame once every ANALYZER_DELAY ms
               // final long now = System.currentTimeMillis();

                //if ( lastAnalyzerCall != -1  && (now - lastAnalyzerCall < ANALYZER_DELAY)){
                //    image.close();
                //    return;
                //}
//C:\AAAndroid_Licenta\MyAruco\app

                //lastAnalyzerCall = now;


//                Long time = System.currentTimeMillis();
//                String timeStamp = time.toString();
//                Log.d("TAG","after "+timeStamp);




                Bitmap originalBitmap = previewView.getBitmap();


                if(originalBitmap==null){
                    markerIdText.setText("NULL");
                    image.close();
                    return;
                }



                Mat originalImageMatColor = new Mat();
                Mat originalImageMatColorGray = new Mat();
                Mat augmentedImageMat = new Mat();
                //Mat replacementImageMat = new Mat();
                Utils.bitmapToMat(originalBitmap, originalImageMatColor);


                //deleting the last channel
                Imgproc.cvtColor(originalImageMatColor,originalImageMatColor, Imgproc.COLOR_RGBA2RGB);

                // gray is needed to detect the markers
                Imgproc.cvtColor(originalImageMatColor, originalImageMatColorGray, Imgproc.COLOR_RGB2GRAY);


                myAruco.marker(originalImageMatColorGray);
                augmentedImageMat = myAruco.draw(originalImageMatColor);



                //TODO: must add the generalisation for the markerd ids
                //this reads from the drawable folder the required image

                if ( !myAruco.corners.isEmpty()) {

                    //for(int i =0; i < myAruco.corners.size();i ++) {


//                        Bitmap replaceBitmapLightbulb = BitmapFactory.decodeResource(getResources(), R.drawable.img21);
//                        Utils.bitmapToMat(replaceBitmapLightbulb, replacementImageMat);
//                        Imgproc.cvtColor(replacementImageMat, replacementImageMat, Imgproc.COLOR_RGBA2RGB);


                        //myAruco.marker(mat);

                        //markerIdText.setText(Arrays.toString(myAruco.id.get(0,0)));
                        //String str  = String.valueOf(myAruco.corners.size()) ;

                        // augmentedImageMat = myAruco.augmentImage(augmentedImageMat,replaceBitmap);
                        augmentedImageMat = myAruco.augmentImage(augmentedImageMat, replacementImageMat);
                        //Log.d("IN FOR:", String.valueOf(i));
                        Log.d("IN FOR:", String.valueOf(myAruco.id));
                        //Bitmap augmentedBitmap = Bitmap.createBitmap(augmentedImageMat.width(),augmentedImageMat.height(),Bitmap.Config.ARGB_8888);
                        //Utils.matToBitmap(augmentedImageMat, augmentedBitmap);

                    //}
                }

                //Bitmap augmentedBitmap = Bitmap.createBitmap(augmentedImageMat.width(),augmentedImageMat.height(),Bitmap.Config.ARGB_8888);
                //Utils.matToBitmap(augmentedImageMat, augmentedBitmap);
                Utils.matToBitmap(augmentedImageMat, originalBitmap);



                String str = new String();
                markerIdText.setText(str);

                //imageView.setImageBitmap(replaceBitmap);
                imageView.setImageBitmap(originalBitmap);
                //imageView.setImageBitmap(augmentedBitmap);



                image.close();









            }
        });
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector,preview, imageCapture);


    }
    @Override
    public void onClick(View view) {
       // if (view.getId() == R.id.infoBtn) {
       //     markerIdText.setText("merge".toString());
       // }
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//
//        float x = event.getX();
//        float y = event.getY();
//        String str = new String("apasat");
//        str = new StringBuilder().append(str).append(" ").append(x).append(" ").append(y).toString();
//
//        markerIdText.setText(str);
//        Log.d("TOUCH",str);
//
//
//
//        return false;
//
//    }

//    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            Log.d("IN TOUCH LISTENER","GOOD");
//            float x = event.getX();
//            float y = event.getY();
//            String str = new String("apasat");
//            str = new StringBuilder().append(str).append(" ").append(x).append(" ").append(y).toString();
//
//            markerIdText.setText(str);
//            Log.d("TOUCH",str);
//            return false;
//        }
//    };
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("IN TOUCH LISTENER","GOOD");
            // the coordonates for touch
            float x = event.getX();
            float y = event.getY();
            Point pct = new Point(x,y);

            //Log.d("TOUCH",str);
            Log.d("REMAINS", String.valueOf(myAruco.id.size()));
            Log.d("REMAINS", String.valueOf(myAruco.corners.size()));

            for(int i =0; i < myAruco.corners.size();i ++){

                //keeping polygone of the marker
                Point topLeft = new Point(myAruco.corners.get(i).get(0, 0)[0], myAruco.corners.get(i).get(0, 0)[1]);
                Point topRight = new Point(myAruco.corners.get(i).get(0, 1)[0], myAruco.corners.get(i).get(0, 1)[1]);
                Point BottomRight = new Point(myAruco.corners.get(i).get(0, 2)[0], myAruco.corners.get(i).get(0, 2)[1]);
                Point BottomLeft = new Point(myAruco.corners.get(i).get(0, 3)[0], myAruco.corners.get(i).get(0, 3)[1]);
                MatOfPoint2f points1 = new MatOfPoint2f(topLeft, topRight, BottomRight,BottomLeft);


                //checking if the click is in the marker polygone
                double result = Imgproc.pointPolygonTest(points1,pct, false);
                if (result >=0){
                    Log.d("FOUND", "am gasit un punct");
                    Log.d("FOUND", String.valueOf(myAruco.id.get(i,0)[0]));
                    String str = new String("apasat");
                    str = new StringBuilder().append(str).append(" ").append(x).append(" ").append(y).append(" ").append(myAruco.id.get(i,0)[0]).toString();

                    markerIdText.setText(str);

                    switch (  (int)Double.parseDouble(String.valueOf(myAruco.id.get(i, 0)[0])) / 10  ){

                        case 1:

                            Intent intent = new Intent(getApplicationContext(), ac_remote.class );
                            intent.putExtra("current_marker_id",(int)Double.parseDouble(String.valueOf(myAruco.id.get(i, 0)[0])));
                            startActivity(intent);
                            break;

                        case 2:

                            intent = new Intent(getApplicationContext(), rgb_remote.class );
                            intent.putExtra("current_marker_id",(int)Double.parseDouble(String.valueOf(myAruco.id.get(i, 0)[0])));
                            startActivity(intent);
                            break;

                        default:
                            intent = new Intent(getApplicationContext(), unknown_remote.class );
                            intent.putExtra("current_marker_id",(int)Double.parseDouble(String.valueOf(myAruco.id.get(i, 0)[0])));
                            startActivity(intent);
                            break;



                    }


                }
            }

        return false;
    }
};

    @Override
    public void analyze(@NonNull ImageProxy image) {
        //Bitmap bitmap = previewView.getBitmap();
        //Image img;

        //Log.d("TAG","in analyse loop");
        //markerIdText.setText("doamne ajuta");


        image.close();
    }


}
