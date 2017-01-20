package com.passengerapp.main.network.model.request;

/**
 * Created by adventis on 8/20/15.
 */
public class UpdatePickupTimeRequest {
    public int TripNumber;
    public int ReservationID;
    public String DateOfPickup; //”: <string>, – The pickup date with format ‘%m/%d/%Y’.
    public String TimeOfPickup; //”: <string|int>, – The pickup time with format ‘HH:MM S’ where S in [AM,PM], localized. If the type of this pickup is a number then DateOfPickup is ignored and is assumed this value is a timestamp with the new date and time specification.
}
