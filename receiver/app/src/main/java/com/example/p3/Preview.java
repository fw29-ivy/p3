package com.example.p3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;
import android.util.Range;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Preview extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "Preview";

    SurfaceHolder mHolder;  // <2>
    public Camera camera; // <3>
    FFT fft = new FFT(16);
    CaptureRequest.Builder mPreviewRequestBuilder;
    List<byte[]> data1 = new ArrayList<byte[]>();
    int count = 0;

    Preview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();  // <4>
        mHolder.addCallback(this);  // <5>
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // <6>

    }

    // Called once the holder is ready
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void surfaceCreated(SurfaceHolder holder) {  // <7>
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open(); // <8>
        try {


            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewSize(320,240);
            parameters.setPreviewFpsRange(60000,60000);
            parameters.set("orientation", "landscape");
            camera.setParameters(parameters);
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);


            camera.setPreviewCallback(new Camera.PreviewCallback() {
                // Called for each frame previewed
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.d(TAG, System.currentTimeMillis() + " ");
                    //Preview.this.invalidate();
                    data1.add(data);
                    count = count + 1;
                    if (count == 16) {
                        Thread mythread = new mythread(data1, camera);
                        mythread.start();
                        data1 = new ArrayList<byte[]>();
                        count = 0;
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Called when the holder is destroyed
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.d(TAG,"Stopping preview in SurfaceDestroyed().");
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    // Called when holder has changed
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        camera.startPreview();
    }


}
