package com.passengerapp.main.services.locationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.gson.Gson;

/**
 * Created by adventis on 10/7/15.
 */
public class LocationManagerUpdateLocationReceiver extends BroadcastReceiver {
    public static final String FILTER_INTENT_VALUE = "com.passengerapp.RECEIVE_UPDATED_LOCATION";
    ILocationManagerNotification mDelegate;

    public LocationManagerUpdateLocationReceiver() {
        super();
    }

    public LocationManagerUpdateLocationReceiver(ILocationManagerNotification delegate) {
        super();
        mDelegate = delegate;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(mDelegate != null) {
            String locationStringObj = intent.getStringExtra(LocationManagerService.LOCATION_STORAGE_VALUE);
            if(!locationStringObj.isEmpty()) {
                mDelegate.updatedNewLocation((new Gson()).fromJson(locationStringObj, Location.class));
            }
        }
    }
}
