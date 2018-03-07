package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;


public class CameraActivity extends AppCompatActivity {

    private Size previewsize;
    private Size jpegSizes[]=null;

    private JSONObject mUserDetails;
    private String mUserName;
    private String mCvv;
    private String mCardNo;
    private String mMessage;
    private String mPhoneNum;


    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    Button getpicture;

    private static final SparseIntArray ORIENTATIONS=new SparseIntArray();

    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        textureView=(TextureView)findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(surfaceTextureListener);

        getpicture=(Button)findViewById(R.id.getpicture);
        getpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("Click Picture", "Button Clicked");
                getPicture();
                //Intent authenticate = new Intent(CameraActivity.this, AuthenticationActivity.class);
                //startActivity(authenticate);
            }
        });
    }
    void getPicture()
    {
        if(cameraDevice==null)
        {
            Log.d("Camera Screen", "Camera device is NULL");
            return;
        }
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try
        {
            Log.d("Camera Screen", "Trying to click image");
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraDevice.getId());
            if(characteristics!=null)
            {
                jpegSizes=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width=640,height=480;
            if(jpegSizes!=null && jpegSizes.length>0)
            {
                width=jpegSizes[0].getWidth();
                height=jpegSizes[0].getHeight();
            }
            ImageReader reader=ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurfaces=new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder capturebuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation=getWindowManager().getDefaultDisplay().getRotation();
            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener imageAvailableListener=new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (Exception ee) {

                    }
                    finally {
                        if(image!=null)
                        image.close();
                    }
                }
                void save(byte[] bytes)
                {
                    Log.d("Converting to Bytes", bytes.toString());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.d("Stream SHape", String.valueOf(stream.size()));
                    FileOutputStream out = null;
                    try
                    {
                        File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "image_1.png");
                        Log.d("ImageHelper", String.format("Saving %dx%d bitmap to %s.",
                                bmp.getWidth(), bmp.getHeight(), file.getAbsolutePath()));
                        try (FileOutputStream fs = new FileOutputStream(file);
                             BufferedOutputStream outputFile = new BufferedOutputStream(fs))
                        {
                            bmp.compress(Bitmap.CompressFormat.PNG, 99, outputFile);
                        }
                        catch (final Exception e)
                        {
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
                    sendRequest();
                }
            };

            HandlerThread handlerThread=new HandlerThread("takepicture");
            handlerThread.start();

            final Handler handler=new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener,handler);

            final CameraCaptureSession.CaptureCallback  previewSSession=new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {

                    try
                    {
                        session.capture(capturebuilder.build(),previewSSession,handler);

                    }catch (Exception e)
                    {

                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            },handler);
        }
        catch (Exception e)
        {

        }
    }

    public  void openCamera()
    {
        CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try
        {
            String camerId=manager.getCameraIdList()[0];
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(camerId);
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewsize=map.getOutputSizes(SurfaceTexture.class)[0];
            manager.openCamera(camerId,stateCallback,null);
        }catch (Exception e)
        {

        }
    }
    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            startCamera();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice!=null)
        {
            cameraDevice.close();

        }
    }

    void  startCamera()
    {
        if(cameraDevice==null||!textureView.isAvailable()|| previewsize==null)
        {
            return;
        }

        SurfaceTexture texture=textureView.getSurfaceTexture();
        if(texture==null)
        {
            return;
        }

        texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
        Surface surface=new Surface(texture);

        try
        {
            previewBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        }catch (Exception e)
        {
        }
        previewBuilder.addTarget(surface);
        try
        {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession=session;
                    getChangedPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            },null);
        }catch (Exception e)
        {

        }
    }
    void getChangedPreview()
    {
        if(cameraDevice==null)
        {
            return;
        }
        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread=new HandlerThread("changed Preview");
        thread.start();
        Handler handler=new Handler(thread.getLooper());
        try
        {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
        }catch (Exception e){}
    }


    private void sendRequest()
    {
        File face_image = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "image_1.png");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Bitmap myBitmap = BitmapFactory.decodeFile(root + "/image_1.png");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Log.d("sdlvjn", encodedImage);
            HttpPost httppost = new HttpPost("http://139.59.91.68:5000/predict"); // your server

            FileBody bin = new FileBody(face_image); // image for uploading

                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .addPart("cap_img", bin)
                        .build();
                httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());

            try {

                CloseableHttpResponse response = httpclient.execute(httppost);
                try {
                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        System.out.println("Response content length: " + resEntity.getContentLength());

                        StringBuilder s = new StringBuilder();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                response.getEntity().getContent(), "UTF-8"));
                        String sResponse;
                        while ((sResponse = reader.readLine()) != null) {
                            s = s.append(sResponse);
                        }
                        System.out.println("adwoavnwodv"+s.toString());
                        try {

                            Log.d("CameraActivity", "Trying stuff");
                            mUserDetails = new JSONObject(s.toString());
                            mMessage = mUserDetails.getString("message");
                            System.out.println("message"+ mMessage);
                            Log.d("CameraActivity", mMessage);

                            if (mMessage.equals("Sucessfully Executed"))
                            {
                                mUserName = mUserDetails.getJSONObject("valid_user_data").getString("name");
                                mCvv = mUserDetails.getJSONObject("valid_user_data").getString("cvv");
                                mCardNo = mUserDetails.getJSONObject("valid_user_data").getString("card_no");
                                mPhoneNum = mUserDetails.getJSONObject("valid_user_data").getString("ph_no");

                                Intent openAuthActivity = new Intent(getBaseContext(), AuthenticationActivity.class);
                                openAuthActivity.putExtra("userName", mUserName);
                                openAuthActivity.putExtra("cvv", mCvv);
                                openAuthActivity.putExtra("cardNo", mCardNo);
                                openAuthActivity.putExtra("phoneNo", mPhoneNum);
                                Log.d("CameraActivity", "Opening Authentication Activity");
                                setResult(Activity.RESULT_OK, openAuthActivity);
                                finish();
                                startActivityForResult(openAuthActivity, 200);

                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(), "Invalid User. Please try again.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        catch(JSONException e)
                        {
                            Log.d("Converting JSON", "JsonException");
                            e.printStackTrace();
                        }
                    }
                    EntityUtils.consume(resEntity);
                } finally {
                    response.close();
                }
            }
            catch(IOException e) {
                Log.d("akdjvb", "ioasjacn");
            }
        }
        finally
        {
            try {
                httpclient.close();
            }
            catch(IOException e) {
            }
        }

    }
}
