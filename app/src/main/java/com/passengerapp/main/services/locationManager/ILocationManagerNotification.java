package com.passengerapp.main.services.locationManager;

import android.location.Location;

/**
 * Created by adventis on 10/7/15.
 */

public interface ILocationManagerNotification {
    public void updatedNewLocation(Location location);
}