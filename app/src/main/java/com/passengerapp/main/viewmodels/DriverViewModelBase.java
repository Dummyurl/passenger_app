package com.passengerapp.main.viewmodels;


import com.passengerapp.main.services.rest.DriverRestClientService;
import com.passengerapp.main.services.rest.IDriverRestClientService;

public class DriverViewModelBase {

	IDriverRestClientService serviceClient;

	public DriverViewModelBase(String serviceUrl) {
		serviceClient = new DriverRestClientService(serviceUrl);
	}

}
