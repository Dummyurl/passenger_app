package com.passengerapp.main.model.http.data;

import java.io.Serializable;

public class HttpLocation implements Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpLocation() {
        latitude = 0.0;
        longitude = 0.0;
    }

    public Double latitude;
    public String location_name;
    public Double longitude;
}
