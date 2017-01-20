package com.passengerapp.main;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.passengerapp.BuildConfig;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;

import io.fabric.sdk.android.Fabric;


public class HGBPassengerApplication extends Application{
    private static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //Crashlytics.start(this);
        //FlurryAgent.init(this, getResources().getString(R.string.flurry_key));

        this.ctx = getBaseContext();

        LocationLibrary.showDebugOutput(true);

        Const.domain = BuildConfig.URL;
        Const.isVIP = BuildConfig.IS_VIP;
        StorageDataHelper.getInstance(this).setVipCode(BuildConfig.VIP_DEFAULT);
        Const.isGeneric = false;
        Const.splashScreenDrawableImageID = BuildConfig.SPLASHSCREEN_FILE_NAME;
        //com.herebygps
        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), 30 * 1000, 1 * 60 * 1000, "com.passengerapp");
            LocationLibrary.forceLocationUpdate(getBaseContext());
        }
        catch (UnsupportedOperationException ex) {
            Log.d("HGBDriverApplication", "UnsupportedOperationException thrown - the device doesn't have any location providers");
        }
    }

    public static Context getContext() {
        return ctx;
    }
}