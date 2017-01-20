package com.passengerapp.main.services.locationManager;

import android.location.Location;

/**
 * Created by adventis on 10/7/15.
 */
public class ExtendedLocationModel extends Location{
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    private String locationName;

    public ExtendedLocationModel(Location l) {
        super(l);
    }


}
