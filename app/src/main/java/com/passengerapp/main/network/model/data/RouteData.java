package com.passengerapp.main.network.model.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RouteData {

	public String estiDistance;

	public String estiDuration;

	public double estiHour;

	public List<LatLng> pointsArray = new ArrayList<LatLng>();

	public RouteData(){}

}
