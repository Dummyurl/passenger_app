package com.passengerapp.main.network.model.response;

import com.passengerapp.main.network.model.data.AcceptsData;
import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.DriverData;
import com.passengerapp.main.network.model.data.FareDetail;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.VehicleShapeData;

import java.io.Serializable;
import java.util.List;

public class SearchDriversData implements Serializable{
    public DriverData Driver;
    public LocationData Location;
    public int NumberOfSuitcases;
    public VehicleShapeData VehicleShape;
    public String TaxiID;
    public String PhoneNumber;
    public float Fare;
    public List<FareDetail> FareDetails;
    public AcceptsData Accepts;
    public int ReviewNumber;
    public float Rating;
    public DistanceData Distance;
    public String MerchantId;
    public String FulfillingMerchantId;
    public boolean Farmed;
    public boolean IsCaptive;
    public boolean IsTipIncluded;
    public boolean DepositRequired;
    public String VehicleStatus;
    public boolean AskForCCData;
    public float DepositAmount;
    public float HourlyRate;
    public float NumOfHours;
    public boolean IsDeparture;
    public String AirportCode;
}
