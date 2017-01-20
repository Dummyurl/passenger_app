package com.passengerapp.main.services.locationManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by adventis on 10/7/15.
 */
public class LocationManagerHelper {
    public static LocationManagerHelper instance;
    Context mCtx;
    Map<Location, String> cacheLocations;

    public LocationManagerHelper(Context ctx) {
        mCtx = ctx;
        cacheLocations = new HashMap<Location, String>();
    }

    public static LocationManagerHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new LocationManagerHelper(ctx);
        }

        return instance;
    }

    public boolean isAvailableGPSData() {
        LocationManager lm = (LocationManager) mCtx.getSystemService(mCtx.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }

        return true;
    }

    public void sendRequestToGetLocation() {
        mCtx.startService(new Intent(mCtx, LocationManagerService.class));
    }

    public float getLastAccuracy() {
        if(isAvailableGPSData()) {
            return getLatestLocation().getAccuracy();
        }

        return 0;
    }

    public Location getLatestLocation() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(LocationManagerService.LOCATION_STORAGE_KEY, Context.MODE_PRIVATE);
        String locationObjString = sharedPreferences.getString(LocationManagerService.LOCATION_STORAGE_VALUE, "");
        if(!locationObjString.isEmpty()) {
            Location location = (new Gson()).fromJson(locationObjString, Location.class);
            return location;
        }

        return null;
    }

    public String getLatestLocationName() {
        return getLocationName(getLatestLocation());
    }

    public String getLocationName(Location location) {
        for (Map.Entry<Location, String> entry : cacheLocations.entrySet()) {
            if(entry.getKey().getLongitude() == location.getLongitude() && entry.getKey().getLatitude() == location.getLatitude()) {
                return entry.getValue();
            }
        }


        Geocoder geocoder = new Geocoder(mCtx, Locale.getDefault());
        String locationName = null;

        if (location != null && geocoder != null) {
            try {
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (list != null & list.size() > 0) {
                    Address address = list.get(0);

                    locationName = address.getSubThoroughfare() + ","
                            + address.getThoroughfare() + ","
                            + address.getLocality() + ","
                            + address.getAdminArea() + ","
                            + address.getCountryName();

                    cacheLocations.put(location, locationName);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return locationName;
            }
        }

        return locationName;
    }
}
