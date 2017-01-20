package com.passengerapp.main.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by adventis on 8/1/15.
 */



public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    OnImageLoadedListener listener;
    String url;

    public ImageDownloader(OnImageLoadedListener listener, ImageView bmImage) {
        this.bmImage = bmImage;
        this.listener = listener;
    }

    protected Bitmap doInBackground(String... urls) {
        this.url = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();


            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        if(listener!=null) {
            listener.imageAsyncLoaded(this.url, result);
        }
    }
}
