package com.passengerapp.main.services;

import android.graphics.Bitmap;

/**
 * Created by adventis on 8/2/15.
 */
public interface OnImageLoadedListener {
    void imageAsyncLoaded(String url, Bitmap bmp);
}
