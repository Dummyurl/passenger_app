package com.passengerapp.main.network.model.response;

/**
 * Created by adventis on 10/20/15.
 */
public class GooglePlacesResponse {
    public Prediction[] predictions;

    public class Prediction {
        public String description;
    }
}
