package com.passengerapp.main.network.model.response;


import android.content.Context;

import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.request.GiveReviewToDriverRequest;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;

import java.io.Serializable;
import java.util.List;

public class PickUpReservationData implements Serializable {

	public int ReservationID;
	public int TripNumber;
	public int DriverID;
	public String DriverToken;
	public String DriverName;
	public String DriverPhoneNumber;
	public String CompanyPhoneNumber;
	public LocationData PickupLocation;
	public LocationData DestinationLocation;
	public String TimeOfPickup;
	public int NumberOfPassenger;
	public String CabStyleRequired;
	public float EstimateFare;
	public long EstimateTime;
	public int ReviewNumber;
	public double Rating;
	public String SpecialInstructions;
	public float NumOfHours;
	public DistanceData Distance;
	public boolean HaveFlightInfo;
	public int InvoiceID;
	public String ReservationStatus;
	public String ReservationSubStatus;
	public Boolean IsAirport;
	public SaveFlightDetailRequest FlightData;

	public boolean isTripInProgress() {
		if(ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_ACTIVE) ||
				ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_DRIVER_ON_WAY)) {
			return true;
		}

		return false;
	}

	public boolean isTripFinished() {
		if(ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_COMPLETED) ||
				ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_DRIVER_DECLINED)) {
			return true;
		}

		return false;
	}

	public boolean isCanceledByDriver() {
		if(ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_CAMCELLED_BY_DRIVER) ||
				ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_DRIVER_DECLINED)) {
			return true;
		}

		return false;
	}

	public boolean canModify() {
		if(ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_FUTURE) ||
				ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_PENDING) ||
					ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_ACTIVE)) {
			return true;
		}

		return false;
	}

	public boolean isActive() {
		return (isTripInProgress() || canModify());
	}

	public boolean isHistorical() {
		return (isTripFinished() || isCanceledByDriver());
	}

	public boolean canWriteReview() {
		if(ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_COMPLETED) ||
				ReservationStatus.equalsIgnoreCase(Const.RESERVATION_STATUS_DRIVER_DECLINED)) {
			return true;
		}

		return false;
	}

	public boolean canViewReview(Context ctx) {
		List<GiveReviewToDriverRequest> reviewList = StorageDataHelper.getInstance(ctx).getReviewToDriverList();
		for (GiveReviewToDriverRequest item : reviewList) {
			if (item.ReservationID == this.ReservationID) {
				return true;
			}
		}

		return false;
	}

//	“Active”: if was assigned to the driver and if reservation status is in the following sub-status:
//			[“Pickedup”, “Assigned”,”ReassignedAuto”, “ReassignedMan”, “Active”]
//	“Future”: if it has not yet been assigned to a driver
//	“Pending”: Only those reservations that have pushed to the driver to which he has not yet responded and reservation status is in the following sub-status:
//			[“AwaitingConfirmation”, “DriverNotified”]
//	“Completed”: Passenger has been delivered to his destination. Only reservations that have not been reviewed are returned.
//	“DriverOnWay” Driver has accepted but passenger has not yet been picked up.
}
