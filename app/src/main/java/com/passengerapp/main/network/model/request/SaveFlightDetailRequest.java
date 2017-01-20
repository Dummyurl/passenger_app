package com.passengerapp.main.network.model.request;
import com.passengerapp.main.network.model.data.DepArrInfoFlightData;
import com.passengerapp.main.network.model.data.LocationData;

import java.io.Serializable;

/**
 * Created by adventis on 10/17/15.
 */
public class SaveFlightDetailRequest implements Serializable {

    public int reservation;
    public String unique_id;
    public boolean is_verified;
    public String airline_code;
    public String airline_name;
    public String flight_id;
    public DepArrInfoFlightData departure;
    public DepArrInfoFlightData arrival;
    public int flight_number;
    public boolean is_arrival;
    public String meet;
    public LocationData coordinates;
    public String status;
    public String special_instructions;
    public boolean diverted;


    public SaveFlightDetailRequest() {

    }

    /*public SaveFlightDetailRequest(HttpSaveFlightInfo data) {
        this.reservation = Integer.parseInt(data.reservation);
        this.unique_id = data.unique_id;
        this.is_verified = data.is_verified;
        this.airline_code = data.airline_code;
        this.flight_id = data.flight_id;
        this.flight_number = Integer.parseInt(data.flight_number);
        this.is_arrival = data.is_arrival;
        this.meet = data.meet;
        this.status = data.status;
        this.special_instructions = data.special_instructions;

        this.departure = data.departure;
        this.arrival = data.arrival;

        this.coordinates = data.coordinates;
    }*/
}
