package com.rohan.mercedeshack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is used to convert a url to
 * a bitmap image, which can be displayed on the
 * screen.
 */

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;
    private String whichView;

    public ImageLoadTask(String url, ImageView imageView, String whichView) {
        this.url = url;
        this.imageView = imageView;
        this.whichView = whichView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        FileOutputStream out = null;
        try
        {
            //out = new FileOutputStream(whichView);
            //result.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            final File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), whichView+".png");
            Log.d("ImageHelper", String.format("Saving %dx%d bitmap to %s.",
                    result.getWidth(), result.getHeight(), file.getAbsolutePath()));

            if (file.exists())
            {
                file.delete();
            }
            try (FileOutputStream fs = new FileOutputStream(file);
                 BufferedOutputStream outputFile = new BufferedOutputStream(fs)) {
                result.compress(Bitmap.CompressFormat.PNG, 99, outputFile);
            } catch (final Exception e) {
                Log.w("ImageHelper", "Could not save image for debugging. " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(result);
    }

}