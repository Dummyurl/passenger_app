package com.passengerapp.main.network.model.request;

import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.VehiclePropsData;

/**
 * Created by adventis on 10/17/15.
 */
public class SendPickupRequest {
    public int TempFlightId;
    public String DriverToken;
    public String TripType;
    public boolean CarPool;
    public String UniqueID;
    public String CouponCode;
    public String MerchantID;
    public DistanceData Distance;
    public String PhoneToken;
    public LocationData PickupLocation;
    public LocationData DestinationLocation;
    public int TimeOfPick;
    public float EstimateTime;
    public int NumberOfPassenger;
    public int EstimateFare;
    public String BookingTime;
    public String Comment;
    public String VehicleNumber;
    public VehiclePropsData VehicleProps;
}
