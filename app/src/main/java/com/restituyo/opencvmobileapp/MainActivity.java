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
import java.util.List;
import java.util.regex.Matcher;

import dalvik.system.BaseDexClassLoader;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");

    }
    private ImageView imageView;
    private ImageView imageView2;
    private TextView txtMatching;

    private Bitmap inputImage; // make bitmap from image resource
    private Bitmap inputImage2;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.red2);
        //inputImage2 = BitmapFactory.decodeResource(getResources(),R.drawable.hand1);

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
        //imageView2 = (ImageView) this.findViewById(R.id.imageView2);
        //txtMatching = (TextView) this.findViewById(R.id.TextViewMatching);


        //sift();
        //convex_hull();
        //test();
        //test2();
       // hand_cascade();
       // tryout();

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

        Imgproc.cvtColor(input, imageHSV, Imgproc.COLOR_BGR2HSV);

        //Core.inRange(imageHSV, new Scalar(0, minS, minV, 0), new Scalar(rng, maxS, maxV, 0), imgThreshold0);

        Core.inRange(imageHSV, new Scalar(0, 190, 30), new Scalar(10,255,255), imgThreshold0);
        Core.inRange(imageHSV, new Scalar(170, 190, 30), new Scalar(180,255,255), imgThreshold1);

        Core.bitwise_or(imgThreshold0, imgThreshold1,imgThreshold);

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

          try {

              Mat src = inputFrame.rgba();
              //Mat src2 = new Mat();
              //Converting the Bitmaps to Mat objects
              //Utils.bitmapToMat(inputImage, src);
              //Utils.bitmapToMat((inputImage2.copy(Bitmap.Config.ARGB_8888, false)), src2);

              Mat gray = new Mat();
              //Mat gray2 = new Mat();

              int max_tresh = 255;
              int thresh = 127;
              //Converting the Mat Objects to gray scale

              Mat hsv = new Mat(src.size(), CvType.CV_8UC3);

              //Imgproc.blur(src, src, src.size());
              Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR);

              //Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV_FULL);
              //Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2YCrCb);
              //Imgproc.cvtColor(src2, gray2, Imgproc.COLOR_RGBA2GRAY);

              //Blurring Images
              //Imgproc.blur(src, src, src.size());
              //Imgproc.blur(src2,src2,src.size());


              //Imgproc.blur(gray,hsv, new Size(3,3));
              //Imgproc.blur(gray2, gray2, new Size(3, 3));


              List<MatOfPoint> contours = new ArrayList<>();
              List<MatOfPoint> contours2 = new ArrayList<>();

              Mat hier = new Mat();
              Mat hier2 = new Mat();

              Mat threshold1_out;
              Mat threshold2_out = new Mat();

              threshold1_out = findColor(src,15);

              //Core.inRange(hsv,new Scalar(0,58,88),new Scalar(23,173,229),threshold1_out);

              //**********************************ycbk************************************************//*
              //Core.inRange(hsv, new Scalar(0, 133, 77), new Scalar(255, 173, 127), threshold1_out);

              //**************************************************************************************//*
              //********************Probando con Rojo********************************************//*
              //Core.inRange(hsv, new Scalar(0, 53, 185,0), new Scalar(15, 255, 255,0), threshold1_out);
              //*********************************************************************************//*
              //Core.inRange(gray2, new Scalar(0, 10, 60), new Scalar(20, 150, 255), threshold2_out);
              //Obtaining thresholds
              //Imgproc.threshold(threshold1_out, threshold1_out, thresh, max_tresh, Imgproc.THRESH_BINARY);

              //Imgproc.threshold(gray2, threshold2_out, thresh, max_tresh, Imgproc.THRESH_BINARY);


              Imgproc.findContours(threshold1_out, contours, hier, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
              // Imgproc.findContours(threshold2_out, contours2, hier2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

              MatOfInt hull = new MatOfInt();

              Mat output1 = new Mat(src.size(), CvType.CV_8UC3);
              //Mat output2 = new Mat(threshold2_out.size(), CvType.CV_8UC3);

              List<MatOfPoint> convexHullContour = new ArrayList<>();
              List<MatOfInt> hulls = new ArrayList<>();


            /*
            for(int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
            {
                Imgproc.drawContours(output1, contours, contourIdx, new Scalar(0, 0, 255), 3);
                Imgproc.convexHull(contours.get(contourIdx), hull,false);
                MatOfPoint hullContour = hull2Points(hull, contours.get(contourIdx));
                convexHullContour.add(hullContour);
                hulls.add(hull);
                //org.opencv.core.Rect box = Imgproc.boundingRect(hullContour);

            }
            */

              int LargestContour1 = findBiggestContour(contours);


              if(LargestContour1!= -1)
              {

                  /*
                  for(int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
                  {
                      Imgproc.drawContours(output1, contours, contourIdx, new Scalar(255, 0, 0), -1);
                      //org.opencv.core.Rect box = Imgproc.boundingRect(hullContour);

                  }
                    */

                  Imgproc.convexHull(contours.get(LargestContour1), hull, false);
                  MatOfPoint hullContour = hull2Points(hull, contours.get(LargestContour1));
                  convexHullContour.add(hullContour);
                  Imgproc.drawContours(output1, convexHullContour, 0, new Scalar(0, 0, 255), 3);
                  Imgproc.drawContours(output1, contours, LargestContour1, new Scalar(255), -1);

              }



            /*
            for(int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
            {
                Imgproc.drawContours(output1, convexHullContour, contourIdx, new Scalar(255, 0, 0), 3);
                //MatOfPoint defectContour = defect2Points(defect,contours.get(contourIdx));
               // defectsContours.add(defectContour);
            }*/

             // List<MatOfPoint> convexHullContour2 = new ArrayList<>();
              //List<MatOfInt> hulls2 = new ArrayList<>();


              // Imgproc.drawContours(output2,contours2,findBiggestContour(contours2), new Scalar(255),-1);


            /*
            Utils.matToBitmap(output1, inputImage);
            Utils.matToBitmap(output2, inputImage2);

            imageView.setImageBitmap(inputImage);
            imageView2.setImageBitmap(inputImage2);
    */


              // Mat mRgbaT = output1.t();
              //Core.flip(output1.t().t(), mRgbaT,1);
              //Imgproc.resize(mRgbaT, mRgbaT, output1.size());
              return output1;
          }catch(Exception e)
          {
              Log.e("La Exception on tobul:",e.toString());
          }
        return inputFrame.rgba();
    }


    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8,this,mBaseLoaderCallback);
    }



    public void hand_cascade()
    {

        try
        {

            InputStream is = getResources().openRawResource(R.raw.hand);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir,"hand.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = is.read(buffer))!= -1)
            {
                os.write(buffer,0,bytesRead);
            }
            is.close();
            os.close();

            Log.d("El file:", cascadeFile.getAbsolutePath());
            CascadeClassifier hand_classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            hand_classifier.load(cascadeFile.getAbsolutePath());

            Mat gray = new Mat();
            Mat src = new Mat();
            Utils.bitmapToMat(inputImage, src);
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2BGR);
            Imgproc.cvtColor(gray,gray, Imgproc.COLOR_BGR2GRAY);

            MatOfRect matOfRect = new MatOfRect();

            hand_classifier.detectMultiScale(src,matOfRect);

            List<org.opencv.core.Rect> rectList = new ArrayList<>();
            rectList = matOfRect.toList();

            for(org.opencv.core.Rect rect : rectList)
            {
                Log.d("Hand1:", Double.toString(rect.area()));
                Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 255, 0),2);

                 Log.d("Handx:",Double.toString(rect.x));
                Log.d("Handy:",Double.toString(rect.y));
            }
            Log.d("Total Hands:",Integer.toString(rectList.size()));


            Utils.matToBitmap(src,inputImage);
            imageView.setImageBitmap(inputImage);


        }catch(Exception e)
        {
            Log.e("Esta es la puta:",e.toString());
        }

    }

    public void test2()
    {
        Mat src = new Mat();
        Mat src2 = new Mat();
        //Converting the Bitmaps to Mat objects
        Utils.bitmapToMat(inputImage, src);
        Utils.bitmapToMat((inputImage2.copy(Bitmap.Config.ARGB_8888, false)), src2);

        Mat hsv = new Mat();
        Imgproc.blur(src, src, new Size(3, 3));
        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);

        Mat bw = new Mat();
        Core.inRange(hsv,new Scalar(0,10,60),new Scalar(20,150,255),bw);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();

        Imgproc.findContours(bw,contours,hier,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE,new Point(0,0));
        int s = findBiggestContour(contours);

        Mat drawing = new Mat(src.size(),CvType.CV_8UC1);
        Imgproc.drawContours(drawing,contours,s,new Scalar(255),-1);

        Utils.matToBitmap(drawing, inputImage);
        imageView.setImageBitmap(inputImage);

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

    public void tryout() {

    }


}