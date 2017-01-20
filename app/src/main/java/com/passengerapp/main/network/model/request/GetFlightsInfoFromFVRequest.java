package com.passengerapp.main.network.model.request;

import java.io.Serializable;

/**
 * Created by adventis on 10/17/15.
 */
public class GetFlightsInfoFromFVRequest implements Serializable {
    public boolean QueryByFlight;
    public String DepartureAirport;
    public String ArrivalAirport;
    public String DepartureDate;
    public String FlightId;
    public String Airline;
    public boolean IsArrival;
}
