package com.passengerapp.main.network.model.data;

public class AirlineData {

//	For GetAirline Api Call
	public String gid;
	public String faa_code;
	
//	below parameter used in both api call
	public String iata_code;
	public String name;
	
//	For GetAirport Apicall

	public String id;
	public String description;
	public String city;
	public String code;

	public AirlineData(){}

}
