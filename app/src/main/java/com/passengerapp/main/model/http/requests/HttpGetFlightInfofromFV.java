package com.passengerapp.main.model.http.requests;

import java.io.Serializable;

public class HttpGetFlightInfofromFV implements Serializable {

    public boolean QueryByFlight;
    public String DepartureAirport;
    public String ArrivalAirport;
    public String DepartureDate;
    public String FlightId;
    public String Airline;
    public boolean IsArrival;
}
