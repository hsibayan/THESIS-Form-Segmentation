package com.getbetter.formsegment;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hannah on 1/27/2017.
 */
public class ComputerVision {

    private static ComputerVision cv = new ComputerVision();

    public static ComputerVision getInstance() { return cv; }

    private ComputerVision() {
    }

    public Mat grayscale(Mat fromMat) {
        Mat toMat = new Mat();
        Imgproc.cvtColor(fromMat, toMat, Imgproc.COLOR_BGR2GRAY);
        return toMat;
    }

    public Mat threshold(Mat fromMat) {
        Mat toMat = new Mat();
        Imgproc.threshold(fromMat, toMat, 100, 255, Imgproc.THRESH_OTSU);
        Imgproc.threshold(toMat, toMat, 100, 255, Imgproc.THRESH_BINARY_INV);
        return toMat;
    }

    public Mat canny(Mat fromMat, int t1, int t2) {
        Mat toMat = new Mat();
        Imgproc.Canny(fromMat, toMat, t1, t2);
        return toMat;
    }

    public ArrayList<Mat> contour(Mat fromMat, Mat origMat) {
        ArrayList<Mat> allMats = new ArrayList<>();

        Mat newMat = Mat.zeros(fromMat.rows(), fromMat.cols(), CvType.CV_8UC3);
        allMats.add(newMat);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(fromMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Random rand = new Random();

        for(int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(newMat, contours, i, new Scalar(255, 255, 255));

            Rect temp = Imgproc.boundingRect(contours.get(i));
            int midX = temp.x + temp.width/2;
            int midY = temp.y + temp.height/2;
            int a = rand.nextInt(230) + 20;
            int b = rand.nextInt(230) + 20;
            int c = rand.nextInt(230) + 20;

            Imgproc.floodFill(newMat, new Mat(),
                    new Point(midX, midY), new Scalar(a, b, c, 30));

            Imgproc.circle(newMat, new Point(midX, midY), 6, new Scalar(255, 255, 255));

            Log.i("HANNAH x, y > ", temp.x + ", " + temp.y);

            Mat subMat = origMat.submat(temp);
            allMats.add(subMat);
        }

        Log.i("HANNAH contour count > ", contours.size() + "");

        return allMats;
    }

    public Mat fill(Mat fromMat) {
        Mat toMat = new Mat();
        Imgproc.floodFill(fromMat, toMat,
                new Point(0,0), new Scalar(100, 100, 100), new Rect(),
                new Scalar(20,20,20), new Scalar(20,20,20), 0);
        return toMat;
    }

    public Mat watershed(Mat mat, Mat markers) {
//        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2BGR);
        Imgproc.watershed(mat, markers);
        return mat;
    }

    public String test(Mat mat) {
        Mat label = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(mat, label, stats, centroids);
        String mats = stats.dump();
        return mats;
    }

    public Mat test2(Mat mat, Mat mat_) {
        Mat marked = mat_.clone(), label = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(mat, label, stats, centroids);
        // left,top,width,height,area
        String s = "";

        for(int i = 1; i < stats.height(); i++) {
            int x = (int)stats.get(i, 0)[0];
            int y = (int)stats.get(i, 1)[0];
            int x2 = x + (int)stats.get(i, 2)[0];
            int y2 = y + (int)stats.get(i, 3)[0];

            s += "(" + x + "," + y + ")\n";
//            Imgproc.drawMarker(marked, new Point(x, y), new Scalar(255, 10, 10),
//                    Imgproc.MARKER_SQUARE, (int)stats.get(i, 3)[0], 4, Imgproc.LINE_4);
            Imgproc.rectangle(marked, new Point(x, y), new Point(x2, y2), new Scalar(0, 0, 255));
        }

        return marked;
    }

    public Mat test3(Mat mat) {
        Mat filled = new Mat(mat.rows(), mat.cols(), 0), label = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(mat, label, stats, centroids);
        // left,top,width,height,area
        String s = "";

        for(int i = 1; i < stats.height(); i++) {
            int x = (int)stats.get(i, 0)[0];
            int y = (int)stats.get(i, 1)[0];
            int x2 = x + (int)stats.get(i, 2)[0];
            int y2 = y + (int)stats.get(i, 3)[0];

            s += "(" + x + "," + y + ")\n";
//            Imgproc.drawMarker(marked, new Point(x, y), new Scalar(255, 10, 10),
//                    Imgproc.MARKER_SQUARE, (int)stats.get(i, 3)[0], 4, Imgproc.LINE_4);
//            Imgproc.floodFill(filled, new Mat(), new Point(x, y), new Scalar(200, 0, 0));

            filled = createFilledBox(filled, x, y, x2, y2);
        }

        return filled;
    }

    public Mat createFilledBox(Mat mat, int x, int y, int x2, int y2) {
        int white[] = new int[1];
        white[0] = 1;

        for(int i = x; i <= x2; i++) {
            for(int j = y; j < y2; j++) {
                mat.put(i, j, white);
            }
        }

        return mat;
    }

    public Mat segmentOuter(Mat original) {
        Mat segmented = original.clone();
        Mat label = new Mat(), stats = new Mat(), centroids = new Mat();
        Imgproc.connectedComponentsWithStats(original, label, stats, centroids);
//        Mat areas = stats.submat(0, stats.height()-1, 4, 4);

        List areas = getList(stats, 4);
        int largest = getLargestArea(areas) + 1;
        int x = (int) stats.get(largest, 0)[0];
        int y = (int) stats.get(largest, 1)[0];
        int x2 = x + (int) stats.get(largest, 2)[0];
        int y2 = y + (int) stats.get(largest, 3)[0];
        Log.i("STATS LARGE", " > " + largest);
        Log.i("STATS X", " > " + x);
        Log.i("STATS Y", " > " + y);
        Log.i("STATS X2", " > " + x2);
        Log.i("STATS Y2", " > " + y2);
//        segmented = original.submat(y, y2, x, x2);
        Imgproc.floodFill(segmented, new Mat(), new Point(x, y), new Scalar(200, 0, 0));
//        Imgproc.circle(segmented, new Point(x, y), 20, new Scalar(200, 0, 0));

//        original.submat(y, y2, x, x2) = Mat.ones(new Size((int) stats.get(largest, 2)[0], (int) stats.get(largest, 3)[0]), 1);

        return segmented;
    }

    public List getList(Mat mat, int col) {
        List list = new ArrayList<>();
        for(int i = 1; i < mat.height(); i++) {
            list.add((int)mat.get(i, col)[0]);
        }
        return list;
    }

    public int getLargestArea(List areas) {
        int largest = (int) areas.get(0);
        int index = 0;
        int curr;

        for(int i = 1; i < areas.size(); i++) {
            curr = (int) areas.get(i);
            if(curr > largest) {
                largest = curr;
                index = i;
            }
        }

        return index;
    }

//    public String test3(Mat mat) {
//        Mat marked = mat, label = new Mat(), stats = new Mat(), centroids = new Mat();
//        Imgproc.connectedComponentsWithStats(mat, label, stats, centroids);
//        // left,top,width,height,area
//        String s = "";
//
//        for(int i = 1; i < stats.height(); i++) {
////            double x = centroids.get(i, 0)[0];
////            double y = centroids.get(i, 1)[0];
//            double x = stats.get(i, 0)[0];
//            double y = stats.get(i, 1)[0];
//            s += "(" + x + "," + y + ")\n";
////            Imgproc.drawMarker(mat, new Point(x, y), new Scalar(255, 10, 10),
////                    Imgproc.MARKER_CROSS, 20, 10, Imgproc.LINE_8);
//
//            Random random = new Random();
//            int b = random.nextInt(256);    int g = random.nextInt(256);    int r = random.nextInt(256);
//            Imgproc.floodFill(mat, marked, new Point(x,y), new Scalar(r,g,b));
//        }
//
//        return s;
//    }

}
