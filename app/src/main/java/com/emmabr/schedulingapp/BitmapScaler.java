package com.emmabr.schedulingapp;

import android.graphics.Bitmap;

public class BitmapScaler {
    // Scale and maintain aspect ratio given a desired width
    // com.emmabr.schedulingapp.BitmapScaler.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    // Scale and maintain aspect ratio given a desired height
    // com.emmabr.schedulingapp.BitmapScaler.scaleToFitHeight(bitmap, 100);
    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

}