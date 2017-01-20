package com.passengerapp.main.network.model.response;

public class GoogleGetLocationByNameApiResponse {
	public int code;
	public Response[] results;

    public class Response {
        public String formatted_address;
        public Geometry geometry;

        public class Geometry {
            public LocationGoogleData location;

            public class LocationGoogleData {
                public double lat;
                public double lng;
            }
        }


    }
}
