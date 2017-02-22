package com.getbetter.formsegment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TestActivity extends AppCompatActivity {

    private Image image = Image.getInstance();
    private Tesseract ocr = Tesseract.getInstance();
    private ComputerVision cv = ComputerVision.getInstance();

    private static final int CAMERA_REQUEST = 1888;
    private String datapath;
    private Mat img;
    private String pictureImagePath;
    private static final String TAG = "MainActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        img = Mat.eye(3, 3, CvType.CV_8UC1);
        datapath = "/storage/extSdCard/Android/tesseract/";

        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.im_buttons);
                layout.removeAllViews();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "tess" + timeStamp + ".jpg";
//                File storageDir = Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES);
                pictureImagePath = datapath + "/Images/" + imageFileName;
                File file = new File(pictureImagePath);
                Uri outputFileUri = Uri.fromFile(file);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        setup(null);

    }

    private void sampleOcr() {
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.abc);
        ocr.init(getFilesDir() + "/tesseract/", getAssets());
        Log.i("HANNAH > ", ocr.text(image));
    }

    private void setup(File imgFile) {
        Bitmap bitmap;
        if(imgFile == null) {
            bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.form1a);
        }
        else {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            bitmap = image.rotate(bitmap, pictureImagePath);
        }
        bitmap = image.scale(bitmap, 4);
        Mat mat = image.getMat(bitmap);

        ImageView imgview = (ImageView) findViewById(R.id.im_main);
        imgview.setImageBitmap(bitmap);

        Mat gray = cv.grayscale(mat);
        Mat bw = cv.threshold(gray);
        Mat can1 = cv.canny(gray, 100, 300);
        Mat label =  bw.clone(); label = cv.test2(label, mat);
        ArrayList<Mat> cons = cv.contour(bw.clone(), mat.clone());

        addImageButton(image.getBitmap(mat), imgview);
        addImageButton(image.getBitmap(bw), imgview);
        addImageButton(image.getBitmap(can1), imgview);

        for(int i = 0; i < cons.size(); i++)
            addImageButton(image.getBitmap(cons.get(i)), imgview);

        TextView mats = (TextView) findViewById(R.id.mats);
        mats.setText(cv.test(bw));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File imgFile = new File(pictureImagePath);
            if(imgFile.exists()) {
                setup(imgFile);
            }
        }
    }

    private void addImageButton(Bitmap bitmap, ImageView imgview) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.im_buttons);
        final Bitmap bitmapx = bitmap;
        final ImageView imgviewx = imgview;

        ImageButton button = new ImageButton(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(100, 100);
        p.setMargins(0, 0, 10, 0);
        button.setLayoutParams(p);
        button.setScaleType(ImageButton.ScaleType.CENTER_CROP);
        button.setBackgroundColor(Color.WHITE);
        button.setImageBitmap(bitmap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgviewx.setImageBitmap(bitmapx);
            }
        });

        layout.addView(button);
    }
}
