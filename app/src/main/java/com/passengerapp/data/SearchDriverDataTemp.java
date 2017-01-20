package com.passengerapp.data;

import com.passengerapp.main.network.model.data.FareDetail;
import com.passengerapp.main.network.model.data.AcceptsData;
import com.passengerapp.main.model.http.data.HttpDistance;
import com.passengerapp.main.model.http.data.HttpDriverData;
import com.passengerapp.main.model.http.data.HttpLocation;
import com.passengerapp.main.network.model.data.VehicleShapeData;

import java.io.Serializable;
import java.util.List;

public class SearchDriverDataTemp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//For Request
	public float hours;
	public String distanceUnit;
	public double distanceValue;
	public String searchUnit;
	public double searchVal;
	public double currLat;
	public double currLng;
	public double destLat;
	public double destLng;
	public String locName;
	public String BookingTime;
	public int NumberOfPassenger;
	public String PassengerID;
    public int id;

    // new fields
    public String DriverToken;
	

	//For Responce
	public HttpDriverData Driver;
	public HttpLocation Location;
	public VehicleShapeData VehicleShape;
	public String TaxiID;
	public String Phonenumber;
	public String PhoneNumber;
	public float Fare;
	public List<FareDetail> FareDetails;
	public AcceptsData Accepts;
	public float ReviewNumber;
	public float Rating;
	public HttpDistance Distance;
	public String MerchantId;
	public String FulfillingMerchantId;
	public boolean Farmed;
	public boolean IsCaptive;
	public boolean IsTipIncluded;
	public boolean DepositRequired;
	public boolean AskForCCData;
	public float DepositAmount;

	public float HourlyRate;
	public float NumOfHours;




}
