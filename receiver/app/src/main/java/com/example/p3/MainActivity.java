package com.example.p3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Preview preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SurfaceView mPreview;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.surfaceView)).addView(preview);


    }
}