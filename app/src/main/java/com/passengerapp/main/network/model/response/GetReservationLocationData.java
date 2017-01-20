package com.passengerapp.main.network.model.response;

import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.VehicleShapeData;

/**
 * Created by adventis on 11/4/15.
 */
public class GetReservationLocationData {
    public int reservation_id;
    public String TaxiID;
    public VehicleShapeData VehicleDetails;
    public LocationData Location;

}
