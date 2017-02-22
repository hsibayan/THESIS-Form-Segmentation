package com.getbetter.formsegment;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Hannah on 1/27/2017.
 */

public class Tesseract {

    private static final Tesseract OCR = new Tesseract();
    private TessBaseAPI tesseract = new TessBaseAPI();

    public static Tesseract getInstance() { return OCR; }

    private Tesseract() {}

    public  void init(String datapath, AssetManager assets) {
        checkFile(new File(datapath + "tessdata/"), datapath, assets);
        tesseract.init(datapath, "eng");
    }

    public String text(Bitmap bitmap) {
        String text = "";
        if(bitmap != null) {
            tesseract.setImage(bitmap);
            text = tesseract.getUTF8Text();
        }
        return text;
    }

    public void end() {
        tesseract.end();
    }

    private void copyFiles(String datapath, AssetManager assets) {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = assets;

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("HANNAH > ", "check file done");
    }

    private void checkFile(File dir, String datapath, AssetManager assets) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles(datapath, assets);
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(datapath, assets);
            }
        }
    }

}
