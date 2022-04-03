package com.myapp.arc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.util.ArrayUtils;

import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Aruco_c {

    static  private Dictionary dict; // the dict is the same for all instances
    public Mat id; // for the ids
    public List<Mat> corners ;
    public DetectorParameters parameters ;

    //    public OnCreate
    public Aruco_c(){
        dict = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250); // the default value for te dictionary
        id = new Mat();
        corners = new ArrayList<>();
        parameters = DetectorParameters.create();
    }
    public Aruco_c(Dictionary d){
        dict = d; // the default value for te dictionary
        id = new Mat();
        corners = new ArrayList<>();
        parameters = DetectorParameters.create();
    }
    public Aruco_c(int new_dict){
        dict = Aruco.getPredefinedDictionary(new_dict);
        id = new Mat();
        corners = new ArrayList<>();
        parameters = DetectorParameters.create();
    }

    public void marker(Mat originalImage){

        // creating a new mat for id and new list for corners
//        Aruco.detectMarkers(originalImage, dict, corners, id, parameters);
        Aruco.detectMarkers(originalImage, dict, corners, id);
        if (id.empty() == false) {
            Log.d("Aruco", "found the id:" + id.get(0, 0).toString());
            Log.d("Aruco", Arrays.toString(id.get(0,0)));

        }else{
            Log.d("Aruco", "no id found " );
        }
    }

    public Mat draw(Mat originalImage){

        Aruco.drawDetectedMarkers(originalImage,this.corners,this.id);
        return originalImage;

    }
    //public Mat augmentImage(Mat originalImage, Mat replacementMat, int x ){
    public Mat augmentImage(Mat originalImage, List<Mat> replacementMat, String[] user_markers ){


        Mat augmentedMat = new Mat();

        // Must have some corners so that I have what to replace
        if (!this.corners.isEmpty()) {
            for(int i =0; i < this.corners.size();i ++) {
                Log.d("MARKERS-USER", user_markers[0]);
                Log.d("MARKERS-USER", String.valueOf((int)this.id.get(i, 0)[0]));



                    Mat warpPerspectiveMat = new Mat();
                    Mat scaledReplacementMat = new Mat();


                    Log.d("IN aruco:", String.valueOf(i));
                    //coordinates of the marker
                    Point topLeft = new Point(this.corners.get(i).get(0, 0)[0], this.corners.get(i).get(0, 0)[1]);
                    Point topRight = new Point(this.corners.get(i).get(0, 1)[0], this.corners.get(i).get(0, 1)[1]);
                    Point BottomRight = new Point(this.corners.get(i).get(0, 2)[0], this.corners.get(i).get(0, 2)[1]);
                    Point BottomLeft = new Point(this.corners.get(i).get(0, 3)[0], this.corners.get(i).get(0, 3)[1]);
                    MatOfPoint2f points1 = new MatOfPoint2f(topLeft, topRight, BottomLeft, BottomRight);


                    MatOfPoint points1Simple = new MatOfPoint(topLeft, topRight, BottomRight, BottomLeft, topLeft);


                    // if the id starts with 1 => ac
                    //if the id starts with 2 => light
                    //switch (((int)Double.parseDouble(String.valueOf(this.id.get(i, 0)[0]))) /10) {
                //if it is a fan or a LED
                if (ArrayUtils.contains(user_markers,String.valueOf((int)this.id.get(i, 0)[0]))) {
                    if (((int) (this.id.get(i, 0)[0])) / 10 == 1) {


                        // this is an ac
                        Log.d("REPLACE_NEW", String.valueOf(this.id.get(i, 0)[0]));

                        Point imageTopLeft = new Point(0, 0);
                        Point imageTopRight = new Point(replacementMat.get(0).width(), 0);
                        Point imageBottomLeft = new Point(0, replacementMat.get(0).height());
                        Point imageBottomRight = new Point(replacementMat.get(0).width(), replacementMat.get(0).height());
                        MatOfPoint2f points2 = new MatOfPoint2f(imageTopLeft, imageTopRight, imageBottomLeft, imageBottomRight);


                        //getting the transformation matrix
                        warpPerspectiveMat = Imgproc.getPerspectiveTransform(points2, points1);


                        //applying the transformation matrix
                        //Imgproc.warpPerspective(replacementMat, replacementMat, warpPerspectiveMat, originalImage.size());
                        Imgproc.warpPerspective(replacementMat.get(0), scaledReplacementMat, warpPerspectiveMat, originalImage.size());

                        //placing a black box where the marker is
//                    Scalar colors = new Scalar(0, 0, 0);
//                    Imgproc.fillConvexPoly(originalImage, points1Simple, colors);
//
//
//                    //Core.add(scaledReplacementMat, originalImage, augmentedMat);
//                    Core.add(scaledReplacementMat, originalImage, originalImage);
//                    //Core.subtract(augmentedMat,originalImage,augmentedMat);

                        //if it is a LED
                    }else if(((int) (this.id.get(i, 0)[0])) / 10 == 2){
                            //this is the light bulb
                            Log.d("REPLACE", String.valueOf(this.id.get(i, 0)[0]));
                            Point imageTopLeft = new Point(0, 0);
                            Point imageTopRight = new Point(replacementMat.get(1).width(), 0);
                            Point imageBottomLeft = new Point(0, replacementMat.get(1).height());
                            Point imageBottomRight = new Point(replacementMat.get(1).width(), replacementMat.get(1).height());
                            MatOfPoint2f points2 = new MatOfPoint2f(imageTopLeft, imageTopRight, imageBottomLeft, imageBottomRight);


                            //getting the transformation matrix
                            warpPerspectiveMat = Imgproc.getPerspectiveTransform(points2, points1);


                            //applying the transformation matrix
                            //Imgproc.warpPerspective(replacementMat, replacementMat, warpPerspectiveMat, originalImage.size());
                            Imgproc.warpPerspective(replacementMat.get(1), scaledReplacementMat, warpPerspectiveMat, originalImage.size());

                            //if the user does not have access to this marker
                    }else{
                        //the marker is unknown
                        Log.d("REPLACE", String.valueOf(this.id.get(i, 0)[0]));
                        Point imageTopLeft = new Point(0, 0);
                        Point imageTopRight = new Point(replacementMat.get(2).width(), 0);
                        Point imageBottomLeft = new Point(0, replacementMat.get(2).height());
                        Point imageBottomRight = new Point(replacementMat.get(2).width(), replacementMat.get(2).height());
                        MatOfPoint2f points2 = new MatOfPoint2f(imageTopLeft, imageTopRight, imageBottomLeft, imageBottomRight);


                        //getting the transformation matrix
                        warpPerspectiveMat = Imgproc.getPerspectiveTransform(points2, points1);


                        //applying the transformation matrix
                        //Imgproc.warpPerspective(replacementMat, replacementMat, warpPerspectiveMat, originalImage.size());
                        Imgproc.warpPerspective(replacementMat.get(2), scaledReplacementMat, warpPerspectiveMat, originalImage.size());
                    }

//                    //placing a black box where the marker is
//                    Scalar colors = new Scalar(0, 0, 0);
//                    Imgproc.fillConvexPoly(originalImage, points1Simple, colors);
//
//
//                    //Core.add(scaledReplacementMat, originalImage, augmentedMat);
//                    Core.add(scaledReplacementMat, originalImage, originalImage);
//                    //Core.subtract(augmentedMat,originalImage,augmentedMat);
                }else{
                    //the marker is unknown
                    Log.d("REPLACE", String.valueOf(this.id.get(i, 0)[0]));
                    Point imageTopLeft = new Point(0, 0);
                    Point imageTopRight = new Point(replacementMat.get(2).width(), 0);
                    Point imageBottomLeft = new Point(0, replacementMat.get(2).height());
                    Point imageBottomRight = new Point(replacementMat.get(2).width(), replacementMat.get(2).height());
                    MatOfPoint2f points2 = new MatOfPoint2f(imageTopLeft, imageTopRight, imageBottomLeft, imageBottomRight);


                    //getting the transformation matrix
                    warpPerspectiveMat = Imgproc.getPerspectiveTransform(points2, points1);


                    //applying the transformation matrix
                    //Imgproc.warpPerspective(replacementMat, replacementMat, warpPerspectiveMat, originalImage.size());
                    Imgproc.warpPerspective(replacementMat.get(2), scaledReplacementMat, warpPerspectiveMat, originalImage.size());
                }
                //placing a black box where the marker is
                Scalar colors = new Scalar(0, 0, 0);
                Imgproc.fillConvexPoly(originalImage, points1Simple, colors);


                //Core.add(scaledReplacementMat, originalImage, augmentedMat);
                Core.add(scaledReplacementMat, originalImage, originalImage);
                //Core.subtract(augmentedMat,originalImage,augmentedMat);
            } // HERE

            return originalImage;

        }



        return originalImage;
    }




}
