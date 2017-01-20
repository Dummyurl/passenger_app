package com.passengerapp.data;

import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.util.Const;

import java.io.Serializable;

/**
 * Created by Igor on 19.10.2015.
 */
public class SearchDriversTempData implements Serializable{
    public LocationData pickUpLocation;
    public LocationData destinationLocation;
    public String bookingTime;
    public long timeToPick;
    public int numberOfPassanger;
    public String tripType;
    public float timeByHourMode;
    public String vehicleStyle;
    public int StyleType;
    public String replyPickupUnit;
    public float replyPickupValue;


    public SearchDriversTempData() {
        this.pickUpLocation = new LocationData();
        this.destinationLocation = new LocationData();
        this.numberOfPassanger = 1;
        this.vehicleStyle = "Any";
        this.tripType = Const.BY_THE_DURATION_OF_TRIP;
    }
}
