package com.passengerapp.main.network.model.response;

/**
 * Created by Igor on 19.10.2015.
 */
public class GoogleRoutesResponse {
    public Route[] routes;

        public class Route {
            public OverviewPolyline overview_polyline;
            public Leg[] legs;


            public class OverviewPolyline {
                public String points;
            }

            public class Leg {
                public Distance distance;
                public Duration duration;

                public class Distance {
                    public String text;
                }

                public class Duration {
                    public String text;
                    public Double value;
                }
            }
    }
}
