package com.passengerapp.main.network.model.response;

import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;

import java.io.Serializable;
import java.util.List;

public class GetFlightDetailsData implements Serializable {

    public List<SaveFlightDetailRequest> flights;
	public boolean is_arrival;

}
