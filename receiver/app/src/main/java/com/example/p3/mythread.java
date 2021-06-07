package com.example.p3;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.Image;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;

import androidx.annotation.ColorInt;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static androidx.core.math.MathUtils.clamp;

public class mythread extends Thread{
    List<byte[]> data1;
    Camera camera;
    public mythread(List<byte[]> data1, Camera camera) {
        this.data1 = data1;
        this.camera = camera;
    }
    public void run(){
        double[] color_data = new double[16];
        int frameHeight = camera.getParameters().getPreviewSize().height;
        int frameWidth = camera.getParameters().getPreviewSize().width;
        int rgb[] = new int[frameWidth * frameHeight];
        int rgbp[] = new int[frameWidth * frameHeight];
        for (int num = 0; num < 16; num++){
            byte[] data = data1.get(num);
            // number of pixels//transforms NV21 pixel data into RGB pixels
            int R = 0, G = 0, B = 0;
            // convertion
            decodeYUV420SP(rgb, data, frameWidth, frameHeight);
            Bitmap bmp = Bitmap.createBitmap(rgb, frameWidth, frameHeight, Bitmap.Config.ARGB_8888);

            bmp.getPixels(rgbp, 0, frameWidth, 0, 0, frameWidth, frameHeight);
            for (int i = 0; i < frameWidth * frameHeight; i++) {
                @ColorInt int argbPixel = rgbp[i];
                int red = Color.red(argbPixel);
                int green = Color.green(argbPixel);
                int blue = Color.blue(argbPixel);
                R = R + red;
                G = G + green;
                B = B + blue;
            }
            double color = (R + G + B) / 3.0 / frameWidth / frameHeight;
            color_data[num] = color;
        }
        String out = "";
        for (int i = 0; i < color_data.length; i++) {
             out = out + color_data[i] + " ";
        }
        Log.d("res1", out);
        double[] img = new double[16];
        Arrays.fill(img, 0.0);
        double[] fft_data = fftCalculator(color_data, img);
        out = "";
        double max = 0;
        int index = 0;
        for (int i = 2; i < fft_data.length / 2; i++) {
            out = out + fft_data[i] + " ";
            if (fft_data[i] > max){
                max = fft_data[i];
                index = i;
            }
        }
        Log.d("res2", out);
        Log.d("max", index+"");
        if (fft_data[3] > fft_data[4]) {
            Log.d("msg", "0");
        } else {
            Log.d("msg", "1");
        }

    }

    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    public double[] fftCalculator(double[] re, double[] im) {
        if (re.length != im.length) return null;
        FFT fft = new FFT(re.length);
        fft.fft(re, im);
        double[] fftMag = new double[re.length];
        for (int i = 0; i < re.length; i++) {
            fftMag[i] = Math.sqrt(Math.pow(re[i], 2) + Math.pow(im[i], 2));
        }
        return fftMag;
    }


}
