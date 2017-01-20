package com.passengerapp.main.network.model.response;


import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;

public class PickUpData {

    public int PassengerID;
    public String UserName;
    public LocationData PickupLocation;
    public LocationData DestinationLocation;
    public String TimeOfPickup;
    public int NumberOfPassenger;
    public float EstimateFare;
    public int EstimateTime;
    public String  PhoneNumber;
    public int ReviewNumber;
    public DistanceData Distance;
    public String VehicleNumber;
    public String DriverName;
    public int TripNumber;
    public String ReservationStatus;

}
