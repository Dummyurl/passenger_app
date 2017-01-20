package com.passengerapp.main.network.model.response;


import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.VehicleShapeData;

import java.io.Serializable;

public class GetDriverLocationData implements Serializable {
	public String TaxiID;
	public LocationData Location;
    public VehicleShapeData VehicleDetails;
}
