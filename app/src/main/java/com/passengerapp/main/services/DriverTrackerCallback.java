package com.passengerapp.main.services;

/**
 * Created by OzzMAN on 07.11.2015.
 */
public interface DriverTrackerCallback {
    void driverArrived(String licenseNo, String color, String style);
    void updateDriverCoordinates(float lat, float lon);
}
