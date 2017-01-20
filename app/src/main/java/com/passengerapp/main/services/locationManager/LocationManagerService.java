package com.passengerapp.main.services.locationManager;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.passengerapp.util.Utils;

/**
 * Created by adventis on 10/7/15.
 */
public class LocationManagerService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private final String TAG = "LocationManagerService";
    public static final String LOCATION_STORAGE_KEY = "LOCATION_STORAGE_KEY";
    public static final String LOCATION_STORAGE_VALUE = "LOCATION_STORAGE_VALUE";

    Boolean currentlyProcessingLocation = false;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and receive new request to start,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 5 meters so lets quit this service,
            // onDestroy will be called and stop our location updates
            Utils.GPSReception reception = Utils.getGPSReception(location.getAccuracy());
            if(reception  == Utils.GPSReception.GOOD || reception == Utils.GPSReception.OK) {
                stopLocationUpdates();

                saveLocationToStorage(location);
                sendBroadcastMessageToApplication(location);
                stopSelf();
            }
        }
    }

    private void sendBroadcastMessageToApplication(Location location) {
        Intent intent = new Intent().setAction(LocationManagerUpdateLocationReceiver.FILTER_INTENT_VALUE)
                .putExtra(LOCATION_STORAGE_VALUE, (new Gson()).toJson(location));

        sendBroadcast(intent);

    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void saveLocationToStorage(Location location) {
        SharedPreferences pref = getSharedPreferences( LOCATION_STORAGE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(LOCATION_STORAGE_VALUE, (new Gson()).toJson(location));

        editor.apply();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
        stopSelf();
    }
}
