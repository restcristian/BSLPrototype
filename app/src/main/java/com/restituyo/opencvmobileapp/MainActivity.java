 /************************************
 * Filename: MainActivity.java
 * Created by: Cristian Restituyo
 * File containing the class for the Main Activity.
 * Core of the application. Receives set of gestures.
 */
package com.restituyo.opencvmobileapp;

import android.app.Activity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import android.view.SurfaceView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;

import org.opencv.core.Mat;

import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

 //Main class implements CameraViewListener to use the CameraView
public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");

    }
    private Bundle imgResources;
    private ImageView imageView;
    private ImageView imageView2;
    private TextView txtMatching;
    private int resourcesArray[];
    private int resourceIndex;


    private Bitmap inputImage; // make bitmap from image resource
    private Bitmap inputImage2;
    private Mat cvxhullcontour;
    private Mat Contour1;

    private CameraBridgeViewBase mCameraManager;
    private BaseLoaderCallback mBaseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("TAG", "OpenCV loaded successfully");
                    mCameraManager.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    //Function that returns the convexhull of both the CameraView current frame and the current gesture
     //along with their corresponding contours
    public List<Mat> ConvertedMat(Bitmap bmp,String color)
    {

        List<Mat> list = new ArrayList<>();

        Mat src2 = new Mat();
        //Mat ROI = new Mat();
        //Converting the Bitmaps to Mat objects
        //Utils.bitmapToMat(inputImage, src);
        Utils.bitmapToMat(bmp, src2);
        Imgproc.cvtColor(src2, src2, Imgproc.COLOR_RGBA2BGR);
        Imgproc.GaussianBlur(src2, src2, new Size(35, 35), 0);

        List<MatOfPoint> contours2 = new ArrayList<>();

        Mat hier2 = new Mat();

        Mat threshold2_out;

        //Mat ROI = src2.clone();

        Mat ROI2 = new Mat();

        Imgproc.putText(src2, Integer.toString(resourceIndex+1) + "/" + Integer.toString(resourcesArray.length), new Point(0, 40), Core.FONT_ITALIC, 1.0, new Scalar(0, 255, 0));


        if(color.contains("SKIN"))
        {

            Imgproc.rectangle(src2, new Point(bmp.getWidth() / 4, bmp.getHeight() / 4), new Point(bmp.getWidth() / 2 + 150, bmp.getHeight() / 2 + 100), new Scalar(0, 255, 0));
            org.opencv.core.Rect rect1 = new org.opencv.core.Rect(new Point(bmp.getWidth() / 4, bmp.getHeight() / 4), new Point(bmp.getWidth() / 2 + 150, bmp.getHeight() / 2 + 100));
            ROI2 = new Mat(src2,rect1);
            //ROI = ROI.submat(rect1);

            //threshold2_out = findSkin(src2,15,bmp);
            threshold2_out = findSkin(ROI2, 15, bmp);


            //ROI = ROI.submat(new org.opencv.core.Rect(new Point(bmp.getWidth() / 4, bmp.getHeight() / 4), new Point(bmp.getWidth() / 2 + 150, bmp.getHeight() / 2 + 100)));
        }else
        {
            threshold2_out = findRedColor(src2,15);
        }

       // Imgproc.rectangle(src2, new Point(bmp.getWidth() / 4, bmp.getHeight() / 4), new Point(bmp.getWidth() / 2 + 150, bmp.getHeight() / 2 + 100), new Scalar(0, 255, 0));
        //ROI = ROI.submat(new org.opencv.core.Rect(new Point(bmp.getWidth() / 4, bmp.getHeight() / 4), new Point(bmp.getWidth() / 2 + 150, bmp.getHeight() / 2 + 100)));

        //threshold2_out = (color.contains("YEL"))?findSkin(src2,15,bmp):findRedColor(src2,15);

        Imgproc.findContours(threshold2_out, contours2, hier2, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        int LargestContour2 = findBiggestContour(contours2);


        MatOfInt hull2 = new MatOfInt();

        //Mat output2 = new Mat(src2.size(), CvType.CV_8UC3);

        Mat tempoutput = new Mat(src2.size(), CvType.CV_8UC1);

        List<MatOfPoint> convexHullContour2 = new ArrayList<>();

        List<MatOfInt> hulls2 = new ArrayList<>();

        if(LargestContour2!= -1)
        {
            /*************Convex Hull 2***********/

            Imgproc.convexHull(contours2.get(LargestContour2), hull2, false);
            MatOfPoint hullContour2 = hull2Points(hull2, contours2.get(LargestContour2));
            convexHullContour2.add(hullContour2);

            if(color.contains("SKIN"))
            {
                Imgproc.drawContours(ROI2, convexHullContour2, 0, new Scalar(0, 0, 255), 3);
                Imgproc.drawContours(ROI2, contours2, LargestContour2, new Scalar(255), -1);

            }
            else
            {
                Imgproc.drawContours(src2, convexHullContour2, 0, new Scalar(0, 0, 255), 3);
                Imgproc.drawContours(src2, contours2, LargestContour2, new Scalar(255), -1);

            }




            //Get convexhull into output

            tempoutput = convexHullContour2.get(0);



            list.add(src2);
            list.add(tempoutput);
            //list.add(convexHullContour2.get(0));


        }else
        {
            list.add(src2);
        }

        return list;
    }
     //Function that executes when the activity is created.
     //Obtains the list of gestures passed through an intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resourceIndex = 0;
        imgResources = getIntent().getExtras();
        if (imgResources != null)
        {
           resourcesArray = imgResources.getIntArray("ResourcesArray");
            inputImage2 = BitmapFactory.decodeResource(getResources(),resourcesArray[resourceIndex]);
        }


        setContentView(R.layout.activity_main);
        if(!OpenCVLoader.initDebug()) {}

        mCameraManager = (CameraBridgeViewBase)findViewById(R.id.myCameraView1);
        mCameraManager.setVisibility(SurfaceView.VISIBLE);
        mCameraManager.setCvCameraViewListener(this);
        mCameraManager.setCameraIndex(1);//0 for back 1 for front



        imageView2 = (ImageView) this.findViewById(R.id.imageView2);

        nextTemplateConvert();


    }

    public void nextTemplateConvert()
    {
        cvxhullcontour = new MatOfPoint();
        List<Mat> src = ConvertedMat(inputImage2,"RED");
        cvxhullcontour = src.get(1);
        Contour1 = src.get(0);
        Utils.matToBitmap(Contour1, inputImage2);

        imageView2.setImageBitmap(inputImage2);
    }
     //function to perform skin segmentation using YCRCB colour space
    public Mat findSkin(Mat inputBGRimage, int rng, Bitmap bmp)
    {
        int minS = 190;
        int minV = 80;
        int maxS = 255;
        int maxV = 255;
        Mat input;
        input = inputBGRimage.clone();
        Mat imageHSV = new Mat(input.size(), CvType.CV_8UC3);
        Mat imgThreshold;

        imgThreshold = new Mat(input.size(), CvType.CV_8UC1);


        //Imgproc.blur(input,input,input.size());
        //Imgproc.cvtColor(input, imageHSV, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(input, imageHSV, Imgproc.COLOR_BGR2YCrCb);

        //General Skin Color//*Run this piece of Code ASAP!!!
        Core.inRange(imageHSV, new Scalar(0, 133, 77), new Scalar(255, 173, 127), imgThreshold);


        Imgproc.erode(imgThreshold, imgThreshold, new Mat());
        Imgproc.dilate(imgThreshold, imgThreshold, new Mat());


        //ROI = src2.submat(new org.opencv.core.Rect(new Point(bmp.getWidth()/4, bmp.getHeight()/4), new Point(bmp.getWidth()/2 + 150, bmp.getHeight()/2 + 100)));

        return imgThreshold;
    }
     //Function to perform colour thresholding based on red value and HSV color space
    public Mat findRedColor(Mat inputBGRimage, int rng)
    {
        int minS = 190;
        int minV = 80;
        int maxS = 255;
        int maxV = 255;
        Mat input;

        input = inputBGRimage.clone();
        Mat imageHSV = new Mat(input.size(), CvType.CV_8UC3);
        Mat imgThreshold, imgThreshold0, imgThreshold1;

        imgThreshold = new Mat(input.size(), CvType.CV_8UC1);
        imgThreshold0 = new Mat(input.size(), CvType.CV_8UC1);
        imgThreshold1 = new Mat(input.size(), CvType.CV_8UC1);


        Imgproc.cvtColor(input, imageHSV, Imgproc.COLOR_BGR2HSV);

        /*******************THRESHOLDING FOR RED*********************************/

        Core.inRange(imageHSV, new Scalar(0, 190, 30), new Scalar(10,255,255), imgThreshold0);
        Core.inRange(imageHSV, new Scalar(170, 190, 30), new Scalar(180, 255, 255), imgThreshold1);

        Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);


        Imgproc.erode(imgThreshold,imgThreshold,new Mat());
        Imgproc.dilate(imgThreshold, imgThreshold, new Mat());
        return imgThreshold;
    }
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

     //Function that runs for every frame passed in the CameraView
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Mat src = inputFrame.rgba();
        List<Mat> templist = new ArrayList<>();

        Core.flip(src.t(),src,1);//Flipping Frame
        Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(),Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(src, bmp);
        MatOfPoint cvxpoint = new MatOfPoint();
        templist = ConvertedMat(bmp,"SKIN");

        Mat output1 = new Mat(src.size(), CvType.CV_8UC3);

        src.convertTo(src,-1,2,50);//adjusting brightness
        Imgproc.cvtColor(src,output1,Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(output1,output1,Imgproc.COLOR_BGR2HSV);

          try {

              if(templist.size() > 1)
                  {
                      System.out.println("Channels-A:"+cvxhullcontour.channels());
                      System.out.println("Channels-B:"+templist.get(1).channels());

                  double x = Imgproc.matchShapes(cvxhullcontour, templist.get(1),Imgproc.CV_CONTOURS_MATCH_I3,0);
                  double m = 0.07;
                  //double y = Double.compare(x,m);
                  Scalar diff_color = (x <= m)? new Scalar(0,255,0):new Scalar(255,0,255);
                  if(x <= m && x != 0)
                  {
                      runOnUiThread(new Runnable() {
                          public void run() {
                              resourceIndex = (resourceIndex < (resourcesArray.length - 1))?(resourceIndex+1):(resourcesArray.length -1);
                              inputImage2 = BitmapFactory.decodeResource(getResources(),resourcesArray[resourceIndex]);
                              nextTemplateConvert();
                              Toast.makeText(getApplicationContext(), "MATCH!!", Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
                  Imgproc.putText(templist.get(0),Double.toString(x),new Point(src.rows()/2, src.cols()/2),Core.FONT_ITALIC,1.0, diff_color);

              }
              output1 = templist.get(0);
              //return templist.get(0);


          }catch(Exception e)
          {
              Log.e("La Exception on tobul:",e.toString());
          }
        return output1;
    }

    //Runs When Application resumes.
    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8,this,mBaseLoaderCallback);
    }


    //Function to return the contour with the biggest area within a list
    public int findBiggestContour(List<MatOfPoint> contours)
    {
        int indexOfBiggest = -1;
        double sizeOfBiggest = 0;
        for(int x = 0; x < contours.size(); x++)
        {
            if(contours.get(x).size().area() > sizeOfBiggest) //EYE
            {
                sizeOfBiggest = contours.get(x).size().area();
                indexOfBiggest = x;
            }
        }

        return indexOfBiggest;
    }
   //Function to convert a MatOfInt into MatOfPoint to be drawn.
   public MatOfPoint hull2Points(MatOfInt hull, MatOfPoint contour)
   {
       List<Integer> indxs = hull.toList();
       List<Point> points = new ArrayList<>();
       MatOfPoint point = new MatOfPoint();
       for(Integer index:indxs)
       {
           points.add(contour.toList().get(index));
       }
       point.fromList(points);
       return point;
   }


}