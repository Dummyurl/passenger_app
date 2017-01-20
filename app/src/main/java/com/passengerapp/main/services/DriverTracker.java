package com.passengerapp.main.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.FloatMath;

import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.request.DriverTokenRequest;
import com.passengerapp.main.network.model.response.GetDriverLocationData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.util.StorageDataHelper;

import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by OzzMAN on 07.11.2015.
 */
public class DriverTracker {
    //Singleton implementation
    protected static DriverTracker instance = null;

    protected DriverTracker() {
        ;
    }

    public static DriverTracker getInstance(Context appContext) {
        if (instance == null)
            instance = new DriverTracker();
        instance.appContext = appContext;
        return instance;
    }

    //
    private String trackedDriverToken;
    private LocationData userLocation;
    private Context appContext;
    private DriverTrackerCallback callback;
    private boolean doStop = false;

    protected double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180/3.14169);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
        float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
        float t3 = FloatMath.sin(a1)*FloatMath.sin(b1);
        double tt = Math.acos((double)t1 + (double)t2 + (double)t3);

        return (double)6366000.0*(double)tt;
    }

    protected static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return Math.abs(dist);
    }

    protected void getUserLocation() {
        if (userLocation == null)
            userLocation = new LocationData();

        userLocation.latitude = String.valueOf(StorageDataHelper.getInstance(appContext).getLatestLocation().getLatitude());
        userLocation.longitude = String.valueOf(StorageDataHelper.getInstance(appContext).getLatestLocation().getLongitude());
    }

    protected void requestDriverLocation() {
        DriverTokenRequest request = new DriverTokenRequest();
        request.DriverToken = trackedDriverToken;

        NetworkApi apiService = new NetworkService().getApi();
        apiService.getDriverLocation(request)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<JsonServerResponse<GetDriverLocationData>, GetDriverLocationData>() {

                    @Override
                    public GetDriverLocationData call(JsonServerResponse<GetDriverLocationData> getDriverLocationDataJsonServerResponse) {
                        if (getDriverLocationDataJsonServerResponse.IsSuccess) {
                            return getDriverLocationDataJsonServerResponse.Content;
                        }
                        return null;
                    }
                }).subscribe(new Subscriber<GetDriverLocationData>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (!doStop)
                    requestDriverLocation();
            }

            @Override
            public void onNext(GetDriverLocationData getDriverLocationData) {
                if (getDriverLocationData == null) {
                    if (!doStop) {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            ;
                        }
                        requestDriverLocation();
                    }
                    return;
                }

                getUserLocation();
                if (userLocation.latitude != null && userLocation.longitude != null) {
                    float uLat = Float.valueOf(userLocation.latitude);
                    float uLon = Float.valueOf(userLocation.longitude);
                    float dLat = Float.valueOf(getDriverLocationData.Location.latitude);
                    float dLon = Float.valueOf(getDriverLocationData.Location.longitude);

                    if (callback != null) {
                        callback.updateDriverCoordinates(dLat, dLon);
                    }

                    double distance = distFrom(uLat, uLon, dLat, dLon);

                    if (distance > 100) {
                        if (!doStop) {
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e) {
                                ;
                            }
                            requestDriverLocation();
                        }
                    } else {
                        if (callback != null) {
                            callback.driverArrived(getDriverLocationData.TaxiID,
                                    getDriverLocationData.VehicleDetails.Color, getDriverLocationData.VehicleDetails.Style);
                            SharedPreferences prefs = appContext.getSharedPreferences("DRV_LOC", 0);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("arrived", true);
                            editor.putString("token", "");
                            editor.commit();
                        }
                    }
                }
            }
        });
    }

    public void startTracking(String driverToken) {
        if (driverToken == null || driverToken.equals(""))
            return;

        doStop = false;
        trackedDriverToken = driverToken;

        SharedPreferences prefs = appContext.getSharedPreferences("DRV_LOC", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("arrived", false);
        editor.putString("token", driverToken);
        editor.commit();

        requestDriverLocation();
    }

    public void setCallback(DriverTrackerCallback callback) {
        this.callback = callback;
    }

    public void stopTracking() {
        doStop = true;
    }
}
