package com.passengerapp.main.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.passengerapp.R;
import com.passengerapp.main.FlightDetailView;
import com.passengerapp.main.SettingActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.ReservationIdRequest;
import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by itishnik on 25.02.2016.
 */
public class MyTripDetailFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Bind(R.id.pickUpEditText)
    EditText pickUpEditText;

    @Bind(R.id.dropOffEditText)
    EditText dropOffEditText;

    @Bind(R.id.whenEditText)
    EditText whenEditText;

    @Bind(R.id.countPassengersEditText)
    EditText countPassengersEditText;

    @Bind(R.id.rentByEditText)
    EditText rentByEditText;

    @Bind(R.id.vehicleStyleEditText)
    EditText vehicleStyleEditText;

    @Bind(R.id.notesEditText)
    EditText notesEditText;

    @Bind(R.id.estimate_fare_value)
    EditText estimateFareValue;

    @Bind(R.id.reservation_id_value)
    EditText reservationIdValue;

    @Bind(R.id.driver_value)
    EditText driverValue;

    @Bind(R.id.flight_layout)
    LinearLayout flightLayout;

    @Bind(R.id.call_company_layout)
    LinearLayout callCompanyLayout;

    @Bind(R.id.call_driver_layout)
    LinearLayout callDriverLayout;

    @Bind(R.id.status_ride_layout)
    LinearLayout statusRideLayout;

    @Bind(R.id.modify_btn)
    LinearLayout modifyBtn;

    @Bind(R.id.cancel_btn)
    LinearLayout cancelBtn;

    @Bind(R.id.read_review_btn)
    LinearLayout readReviewBtn;

    @Bind(R.id.write_review_btn)
    LinearLayout writeReviewBtn;

    SettingActivity settingActivity;
    PickUpReservationData displayedTripData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting_trip_detail, container, false);
        ButterKnife.bind(this, view);
        settingActivity = (SettingActivity)getActivity();
        return view;
    }

    public void hideBottomButtons() {
        modifyBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        writeReviewBtn.setVisibility(View.GONE);
        readReviewBtn.setVisibility(View.GONE);
    }
    public void updateTripDetail(PickUpReservationData tripData) {
        hideBottomButtons();

        pickUpEditText.setText(tripData.PickupLocation.location_name);
        dropOffEditText.setText(tripData.DestinationLocation.location_name);
        whenEditText.setText(Utils.millisToDate(Long.parseLong(tripData.TimeOfPickup) * 1000, "hh:mm a MM/dd/yyyy"));
        countPassengersEditText.setText(tripData.NumberOfPassenger);

        vehicleStyleEditText.setText(tripData.CabStyleRequired);
        notesEditText.setText(tripData.SpecialInstructions);

        estimateFareValue.setText("$"+String.format("%.02f", tripData.EstimateFare));
        reservationIdValue.setText(tripData.ReservationID);
        driverValue.setText(tripData.DriverName);

        if(!tripData.HaveFlightInfo || tripData.FlightData == null) {
            flightLayout.setVisibility(View.GONE);
        }
        if(tripData.DriverPhoneNumber == null || tripData.DriverPhoneNumber.isEmpty()) {
            callDriverLayout.setVisibility(View.GONE);
        }
        if(tripData.CompanyPhoneNumber == null || tripData.CompanyPhoneNumber.isEmpty()) {
            callCompanyLayout.setVisibility(View.GONE);
        }

        if(!tripData.isTripInProgress() || tripData.canModify()) {
            statusRideLayout.setVisibility(View.GONE);
        }

        if(tripData.canModify()) {
            modifyBtn.setVisibility(View.VISIBLE);
        }

        if(tripData.isTripInProgress()) {
            cancelBtn.setVisibility(View.VISIBLE);
        }

        if(tripData.canWriteReview()) {
            writeReviewBtn.setVisibility(View.VISIBLE);
        }

        if(tripData.canWriteReview()) {
            readReviewBtn.setVisibility(View.VISIBLE);
        }

        this.displayedTripData = tripData;
    }

    public void callToPhonenumber(String number) {
        startActivity(new Intent(Intent.ACTION_CALL)
                .setData(Uri.parse("tel:" + number))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void showFlightDetailActivity(GetFlightDetailsData resFlightData) {
        if(resFlightData != null && resFlightData.flights.size() > 0) {
            startActivity(new Intent(getActivity(), FlightDetailView.class)
                    .putExtra(Const.FLIGHT_DETAIL, resFlightData.flights.get(0)));
        }
    }

    public void requestGetFlightDetails(int reservationId) {
        if (!settingActivity.isNetworkAvailable())
            return;

        settingActivity.DisplayProcessMessage(Utils.getStringById(R.string.my_trips_fragment_get_getting_flight_detail));

        ReservationIdRequest request = new ReservationIdRequest();
        request.ReservationID = reservationId;

        NetworkApi api = (new NetworkService()).getApi();

        api.getFlightDetails(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<GetFlightDetailsData>>() {
                    @Override
                    public void onCompleted() {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<GetFlightDetailsData> getDriverLocationDataJsonServerResponse) {
                        if (getDriverLocationDataJsonServerResponse.IsSuccess) {
                            showFlightDetailActivity(getDriverLocationDataJsonServerResponse.Content);
                        }
                    }
                });

    }

    @OnClick(R.id.call_company_btn)
    public void callCompanyPhoneNumber() {
        if(displayedTripData != null) {
            callToPhonenumber(displayedTripData.CompanyPhoneNumber);
        }
    }
    @OnClick(R.id.call_driver_btn)
    public void callDriverPhoneNumber() {
        if(displayedTripData != null) {
            callToPhonenumber(displayedTripData.DriverPhoneNumber);
        }
    }
    @OnClick(R.id.view_flight_btn)
    public void showFlightInfo() {
        if(displayedTripData != null) {
            requestGetFlightDetails(displayedTripData.ReservationID);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        this.displayedTripData = null;
    }
}
