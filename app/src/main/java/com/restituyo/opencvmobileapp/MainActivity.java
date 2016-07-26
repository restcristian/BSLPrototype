package com.restituyo.opencvmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import dalvik.system.BaseDexClassLoader;

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
    //private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
   // private static long MIN_HESSIAN = 400;

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


    public List<Mat> ConvertedMat(Bitmap bmp,String color)
    {

        List<Mat> list = new ArrayList<>();

        Mat src2 = new Mat();
        //Converting the Bitmaps to Mat objects
        //Utils.bitmapToMat(inputImage, src);
        Utils.bitmapToMat(bmp, src2);
        Imgproc.cvtColor(src2, src2, Imgproc.COLOR_RGBA2BGR);
        Imgproc.GaussianBlur(src2, src2, new Size(35, 35), 0);

        List<MatOfPoint> contours2 = new ArrayList<>();

        Mat hier2 = new Mat();

        Mat threshold2_out;

        threshold2_out = (color.contains("YEL"))?findColor(src2,15):findRedColor(src2,15);

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
            Imgproc.drawContours(src2, convexHullContour2, 0, new Scalar(0, 0, 255), 3);
            Imgproc.drawContours(src2, contours2, LargestContour2, new Scalar(255), -1);

            //Get convexhull into grayscale

            tempoutput = convexHullContour2.get(0);
            //tempoutput.convertTo(tempoutput, CvType.CV_8UC1);
            //Imgproc.cvtColor(tempoutput,tempoutput,Imgproc.COLOR_BGR2GRAY);


            /*

            MatOfInt4 defects = new MatOfInt4();
            int defect_count = 0;
            Imgproc.convexityDefects(contours2.get(LargestContour2),hull2,defects);
            List<Integer> cdList = defects.toList();
            Point data[] = contours2.get(LargestContour2).toArray();
            for(int x = 0; x < defects.toList().size(); x+=4)
            {
                Point start = data[cdList.get(x)];
                Point end = data[cdList.get(x+1)];
                Point defect = data[cdList.get(x+2)];

                //Imgproc.circle(src2,start,5, new Scalar(0,255,0),2);
                //Imgproc.circle(src2,end,5, new Scalar(0,255,0),2);
                Imgproc.circle(src2,defect, 5, new Scalar(0,255,0),2);


            }
            */
            /*
            for(int x = 0; x < contours2.size(); x++)
            {

                Imgproc.convexHull(contours2.get(x), hull2, false);
                MatOfPoint hullContour2 = hull2Points(hull2, contours2.get(x));
                convexHullContour2.add(hullContour2);
                Imgproc.drawContours(src2, convexHullContour2, x, new Scalar(0, 0, 255), 3);
                Imgproc.drawContours(src2, contours2, x, new Scalar(255), -1);

            }
            */
            /*
            Imgproc.drawContours(tempoutput,convexHullContour2,0, new Scalar(255,0,0),-1);
            Imgproc.drawContours(tempoutput, contours2, LargestContour2, new Scalar(255, 0, 0), -1);

             */
            list.add(src2);
            list.add(tempoutput);
            //list.add(convexHullContour2.get(0));


        }else
        {
            list.add(src2);
        }

        return list;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.1);

        resourceIndex = 0;
        imgResources = getIntent().getExtras();
        if(imgResources!=null)
        {
           resourcesArray = imgResources.getIntArray("ResourcesArray");
            inputImage2 = BitmapFactory.decodeResource(getResources(),resourcesArray[resourceIndex]);
        }

        //inputImage2 = BitmapFactory.decodeResource(getResources(),R.drawable.count5);



        setContentView(R.layout.activity_main);
        if(!OpenCVLoader.initDebug())
        {

        }

        mCameraManager = (CameraBridgeViewBase)findViewById(R.id.myCameraView1);
        mCameraManager.setVisibility(SurfaceView.VISIBLE);
        mCameraManager.setCvCameraViewListener(this);
        mCameraManager.setCameraIndex(1);//0 for back 1 for front



        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       // imageView = (ImageView) this.findViewById(R.id.imageView);
        imageView2 = (ImageView) this.findViewById(R.id.imageView2);
        //txtMatching = (TextView) this.findViewById(R.id.TextViewMatching);

        nextTemplateConvert();
        /*
        cvxhullcontour = new MatOfPoint();
        List<Mat> src = ConvertedMat(inputImage2,"RED");
        cvxhullcontour = src.get(1);
        Contour1 = src.get(0);
        Utils.matToBitmap(Contour1, inputImage2);
        imageView2.setImageBitmap(inputImage2);
         */


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
    public Mat findColor(Mat inputBGRimage, int rng)
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


        //Imgproc.blur(input,input,input.size());
        Imgproc.cvtColor(input, imageHSV, Imgproc.COLOR_BGR2HSV);



        //Core.inRange(imageHSV, new Scalar(0, minS, minV, 0), new Scalar(rng, maxS, maxV, 0), imgThreshold0);
        //Yellow Color
        Core.inRange(imageHSV, new Scalar(20, 100, 100), new Scalar(30, 255, 255), imgThreshold);
        /*******************THRESHOLDING FOR RED*********************************/
        /*
        Core.inRange(imageHSV, new Scalar(0, 190, 30), new Scalar(10,255,255), imgThreshold0);
        Core.inRange(imageHSV, new Scalar(170, 190, 30), new Scalar(180, 255, 255), imgThreshold1);

        Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);
        */
        /*************************************************************************/
        //Core.inRange(imageHSV, new Scalar(170,70,50), new Scalar(180, 255, 255), imgThreshold1);

        //Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);

        //Imgproc.dilate(imgThreshold, imgThreshold, new Mat());

        /*
        if(rng > 0)
        {
            Core.inRange(imageHSV, new Scalar(180-rng,minS,maxV,0), new Scalar(180, maxS, maxV, 0), imgThreshold1);
            Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);
        }
        else {
            imgThreshold = imgThreshold0;
        }
        */

        Imgproc.erode(imgThreshold,imgThreshold,new Mat());
        Imgproc.dilate(imgThreshold, imgThreshold, new Mat());
        return imgThreshold;
    }

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


        //Core.inRange(imageHSV, new Scalar(0, minS, minV, 0), new Scalar(rng, maxS, maxV, 0), imgThreshold0);

        //Core.inRange(imageHSV, new Scalar(23,41,133), new Scalar(40,255,255), imgThreshold);
        /*******************THRESHOLDING FOR RED*********************************/

        Core.inRange(imageHSV, new Scalar(0, 190, 30), new Scalar(10,255,255), imgThreshold0);
        Core.inRange(imageHSV, new Scalar(170, 190, 30), new Scalar(180, 255, 255), imgThreshold1);

        Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);

        /*************************************************************************/
        //Core.inRange(imageHSV, new Scalar(170,70,50), new Scalar(180, 255, 255), imgThreshold1);

        //Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);

        //Imgproc.dilate(imgThreshold, imgThreshold, new Mat());

        /*
        if(rng > 0)
        {
            Core.inRange(imageHSV, new Scalar(180-rng,minS,maxV,0), new Scalar(180, maxS, maxV, 0), imgThreshold1);
            Core.bitwise_or(imgThreshold0, imgThreshold1, imgThreshold);
        }
        else {
            imgThreshold = imgThreshold0;
        }
        */

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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //return inputFrame.gray();
        Mat src = inputFrame.rgba();
        List<Mat> templist = new ArrayList<>();

        Bitmap bmp = Bitmap.createBitmap(src.cols(), src.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, bmp);
        MatOfPoint cvxpoint = new MatOfPoint();
        templist = ConvertedMat(bmp,"YEL");

        Mat output1 = new Mat(src.size(), CvType.CV_8UC3);

        src.convertTo(src,-1,2,50);//adjusting brightness
        Imgproc.cvtColor(src,output1,Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(output1,output1,Imgproc.COLOR_BGR2HSV);

          try {

              if(templist.size() > 1)
                  {
                      System.out.println("Channels-A:"+cvxhullcontour.channels());
                      System.out.println("Channels-B:"+templist.get(1).channels());

                  double x = Imgproc.matchShapes(cvxhullcontour, templist.get(1),Imgproc.CV_CONTOURS_MATCH_I1,0);
                  double m = 0.05;
                  double y = Double.compare(x,m);
                  Scalar diff_color = (x <= m)? new Scalar(0,255,0):new Scalar(255,0,255);
                  if(y <= 0)
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


    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8,this,mBaseLoaderCallback);
    }



    public int findBiggestContour(List<MatOfPoint> contours)
    {
        int indexOfBiggest = -1;
        double sizeOfBiggest = 0;
        for(int x = 0; x < contours.size(); x++)
        {
            if(contours.get(x).size().area() > sizeOfBiggest)
            {
                sizeOfBiggest = contours.get(x).size().area();
                indexOfBiggest = x;
            }
        }

        return indexOfBiggest;
    }
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