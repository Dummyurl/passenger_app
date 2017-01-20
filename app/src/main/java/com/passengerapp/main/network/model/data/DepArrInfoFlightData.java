package com.passengerapp.main.network.model.data;

import java.io.Serializable;

/**
 * Created by adventis on 10/17/15.
 */
public class DepArrInfoFlightData implements Serializable {
    public String date;
    public String time;
    public String airport_code;
    public String terminal;
    public String gate;
    public String date_time;
    public LocationData coordinates;

    public DepArrInfoFlightData() {}
}
