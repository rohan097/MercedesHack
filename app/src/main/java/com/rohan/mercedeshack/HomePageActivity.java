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
import android.widget.ImageView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HomePageActivity extends Activity {


    private String mInterior;
    private String mExterior;

    private String phNum;
    private String cvv;
    private String cardNum;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userName = getIntent().getExtras().getString("userName");
        phNum = getIntent().getExtras().getString("phoneNum");
        cvv = getIntent().getExtras().getString("cvv");
        cardNum = getIntent().getExtras().getString("cardNo");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        Log.d("Debugging", "Homepage Activity Created.");
        loadVehicleImage();
    }

    private void loadVehicleImage()
    {
        File exterior_file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "exteriorImage.png");
        File interior_file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "interiorImage.png");
        if (interior_file.exists() && exterior_file.exists())
        {
            Log.d("HomePage Debugging", "File Exists");
            //FileInputStream inputFile = new FileInputStream(file);
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Bitmap myBitmap = BitmapFactory.decodeFile(root + "/exteriorImage.png");
            ImageView myImage = (ImageView) findViewById(R.id.car_view);
            myImage.setImageBitmap(myBitmap);
        }
        else
        {
            AsyncTask.execute(new Runnable()
            {
                @Override
                public void run() {
                    // Create URL
                    Log.d("Debugging", "Trying to create url.");
                    try {
                        URL mercedesAPIEndpoint = new URL("https://api.mercedes-benz.com/image/v1/vehicles/WDD2130331A123456/vehicle?apikey=pypOFFAN8fVk2DKYIGrADFWzxjrEkIs9");
                        Log.d("Debugging", "URL Created.");

                        try {
                            HttpsURLConnection myConnection =
                                    (HttpsURLConnection) mercedesAPIEndpoint.openConnection();
                            Log.d("Debugging", "Trying to create connection");

                            if (myConnection.getResponseCode() == 200)
                            {
                                Log.d("Debugging", "Connection Created.");
                                InputStream responseBody = myConnection.getInputStream();
                                InputStreamReader responseBodyReader =
                                        new InputStreamReader(responseBody, "UTF-8");

                                String result = getStringFromInputStream(responseBody);

                                Log.d("Json", responseBodyReader.toString());


                                Log.d("HomePage Debugging", "File doesn't exist.");
                                try {
                                    JSONObject json = new JSONObject(result);
                                    Log.d("Debugging", "object Created.");
                                    mExterior = json.getJSONObject("vehicle").getJSONObject("EXT020").getString("url");
                                    mInterior = json.getJSONObject("vehicle").getJSONObject("INT1").getString("url");
                                    Log.d("Json url", mExterior);
                                    Log.d("Json url", mInterior);
                                    ImageView carView = (ImageView) findViewById(R.id.car_view);
                                    new ImageLoadTask(mExterior, carView, "exteriorImage").execute();

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                            else
                            {
                                Log.d("Debugging", "Error from server");
                                Log.d("Error from server", String.valueOf(myConnection.getResponseCode()));
                                // Error handling code goes here
                            }
                            myConnection.disconnect();
                        }
                        catch (IOException e)
                        {
                            Log.d("Debugging", "Caught IOException");
                            e.printStackTrace();
                        }
                    }
                    catch (MalformedURLException e)
                    {
                        Log.d("Debugging", "Caught malformedURL");
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public void openMapsActivity(View view)
    {
        Intent openMaps = new Intent(HomePageActivity.this, RouteActivity.class);
        startActivity(openMaps);
    }

    public void openCabinActivity(View view)
    {
        Intent openCabin = new Intent(getBaseContext(), CabinActivity.class);
        openCabin.putExtra("interiorImage", mInterior);
        openCabin.putExtra("userName", userName);
        openCabin.putExtra("cvv", cvv);
        openCabin.putExtra("phNum", phNum);
        openCabin.putExtra("cardNo", cardNum);
        setResult(Activity.RESULT_OK, openCabin);
        finish();
        startActivityForResult(openCabin, 200);
    }

    public void endRide(View view)
    {
        Intent openEndRide = new Intent(HomePageActivity.this, endActivity.class);
        openEndRide.putExtra("userName", userName);
        openEndRide.putExtra("cvv", cvv);
        openEndRide.putExtra("phNum", phNum);
        openEndRide.putExtra("cardNo", cardNum);
        setResult(Activity.RESULT_OK, openEndRide);
        finish();
        startActivityForResult(openEndRide, 200);

    }

    //ImageLoadTask loadImage = new ImageLoadTask(url, imageView).execute();
}
