package com.passengerapp.main.model.http.data;

import java.io.Serializable;

/**
 * Created by adventis on 10/10/15.
 */
public class PendingPickupData implements Serializable {
    public String driverToken;
    public long createdDate;
    public int reservationId;
    public String tripNumber;
}
