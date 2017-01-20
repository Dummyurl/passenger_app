package com.passengerapp.main.model.http.data;

import java.io.Serializable;

public class HttpDriverData implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String company;
    public int driverID;
    public String firstName;
    public String lastName;
    public String token;
    public String DriverImage;

    public HttpDriverData(){}
}