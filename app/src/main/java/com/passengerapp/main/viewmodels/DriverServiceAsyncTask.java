package com.passengerapp.main.viewmodels;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by adventis on 10/9/15.
 */
public class DriverServiceAsyncTask<T1, T2> extends AsyncTask<T1, Void, T2> {
    private boolean mIsShowLoadingDialog = false;
    private ProgressDialog progressDialog;
    private final String TAG = "DriverServiceAsyncTask";

    public DriverServiceAsyncTask() {
        super();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected T2 doInBackground(T1... params) {
        return null;
    }

    @Override
    protected void onPostExecute(T2 t2) {
        Log.d(TAG, "onPostExecute");

        super.onPostExecute(t2);
    }
}
