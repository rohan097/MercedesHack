package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

public class CabinActivity extends Activity {

    private String mInterior = null;
    private int mACTemp = 16;

    private String phNum;
    private String cvv;
    private String cardNum;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Cabin Activity", "Activity Opened");
        String interiorURL = getIntent().getExtras().getString("interiorImage");
        super.onCreate(savedInstanceState);
        userName = getIntent().getExtras().getString("userName");
        phNum = getIntent().getExtras().getString("phoneNum");
        cvv = getIntent().getExtras().getString("cvv");
        cardNum = getIntent().getExtras().getString("cardNo");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cabin);
        loadInteriorView(interiorURL);
    }

    protected void loadInteriorView(final String interiorURL) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "interiorImage.png");
        if (file.exists()) {
            Log.d("CabinPage Debugging", "File Exists");
            //FileInputStream inputFile = new FileInputStream(file);
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Bitmap myBitmap = BitmapFactory.decodeFile(root + "/interiorImage.png");
            ImageView myImage = (ImageView) findViewById(R.id.interior_view);
            myImage.setImageBitmap(myBitmap);
        } else {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("Cabin Debugging", "File doesn't exist");
                    try {
                        Log.d("Cabin Activity", interiorURL);
                        ImageView carView = (ImageView) findViewById(R.id.interior_view);
                        new ImageLoadTask(interiorURL, carView, "interiorImage").execute();
                    } catch (NullPointerException e) {
                        Log.d("Cabin Activity", "NullPointerException");
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void increaseTemp(View view) {
        TextView acTempView = (TextView) findViewById(R.id.ac_temp);
        mACTemp += 1;
        if (mACTemp >= 28) {
            mACTemp = 28;
        }
        acTempView.setText(String.valueOf(mACTemp));
    }

    public void decreaseTemp(View view) {
        TextView acTempView = (TextView) findViewById(R.id.ac_temp);
        mACTemp -= 1;
        if (mACTemp <= 14) {
            mACTemp = 14;
        }
        acTempView.setText(String.valueOf(mACTemp));
    }

    public void goHome(View view)
    {
        Intent goHome = new Intent(getBaseContext(), HomePageActivity.class);
        goHome.putExtra("userName", userName);
        goHome.putExtra("cvv", cvv);
        goHome.putExtra("phNum", phNum);
        goHome.putExtra("cardNo", cardNum);
        setResult(Activity.RESULT_OK, goHome);
        finish();
        startActivityForResult(goHome, 200);
    }
}
