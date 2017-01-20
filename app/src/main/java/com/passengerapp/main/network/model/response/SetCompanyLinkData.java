package com.passengerapp.main.network.model.response;

import com.google.gson.JsonObject;
import com.passengerapp.main.network.model.data.AirportData;

import java.util.List;

public class SetCompanyLinkData {
	public String PassengerID;
	public String CompanyLogo;
	public boolean ShownFV;
	public boolean ShownStyleImages;
	public boolean DisplayFilterBar;
	public boolean HourlyColumns;
	public int DefaultSearchRadius;
	public JsonObject TermsAndConditions;
	public List<AirportData> Airports;
}
