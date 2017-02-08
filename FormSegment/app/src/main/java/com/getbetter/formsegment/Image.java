package com.getbetter.formsegment;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by Hannah on 1/27/2017.
 */
public class Image {

    private static Image image = new Image();

    public static Image getInstance() {
        return image;
    }

    private Image() {
    }

    public Bitmap scale(Bitmap bitmap, int n){
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/n, bitmap.getHeight()/n, true);
    }

    public Bitmap rotate(Bitmap bitmap, String path) {
        Bitmap rotated = bitmap;

        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotated = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotated =  rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotated =  rotateImage(bitmap, 270);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotated;
    }

    public static Bitmap rotateImage(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Mat getMat(Bitmap bitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }

    public Bitmap getBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
}
