package com.passengerapp.main.network.model.request;

import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;

/**
 * Created by Igor on 19.10.2015.
 */
public class SearchDriversRequest {
    public String VipCode;
    public String TripType;
    public float TripTime;
    public DistanceData SearchRadius;
    public LocationData PickupLocation;
    public LocationData DestinationLocation;
    public DistanceData Distance;
    public String BookingTime;
    public int NumberOfPassenger;
    public boolean CarPool;
    public int StyleType;
    public int VehicleClass;
    public String Style;
    public String TimeOfPickup;
}
