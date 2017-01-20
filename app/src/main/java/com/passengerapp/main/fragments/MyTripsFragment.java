package com.passengerapp.main.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.FlightDetailView;
import com.passengerapp.main.ModifyTripActivity;
import com.passengerapp.main.RateActivity;
import com.passengerapp.main.SettingActivity;
import com.passengerapp.main.model.http.responses.JsonResponse;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.ReservationIdRequest;
import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.main.widget.VehicleStyleScrollable;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 11/9/14.
 */
public class MyTripsFragment extends Fragment {

    private final String TAG = "MyTripsFragment";

    private DriverViewModel viewModel;
    private ProgressDialog progressDialog = null;
    private LocalBroadcastManager broadcaster;

    private Boolean isSentRequest;

    private SettingActivity settingActivity;

    private JsonResponse<GetFlightDetailsData> getflightdataRes;

    private List<PickUpReservationData> driverPickUpsForPassanger;


    private final AlertDailogView.OnCustPopUpDialogButoonClickListener listenerDialog = new AlertDailogView.OnCustPopUpDialogButoonClickListener() {
        @Override
        public void OnButtonClick(int tag, int buttonIndex) {
        switch (tag) {
            case 0:
                if (buttonIndex == AlertDailogView.BUTTON_CANCEL) {
                    /*Intent intent = new Intent(GetDriverService.COPA_RESULT);
                    broadcaster.sendBroadcast(intent);*/
                    getActivity().finish();
                }
            break;
        }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSentRequest = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        v = inflater.inflate(R.layout.activity_setting_my_trip_fragment, container, false);

        settingActivity = (SettingActivity)getActivity();
        viewModel = new DriverViewModel();

        driverPickUpsForPassanger = StorageDataHelper.getInstance(getActivity()).getDriverPickUpsForPassanger();

        initView(v);

        if(settingActivity.getIntent().getIntExtra(SettingActivity.EXTRA_INTENT_ORDER_ID, -1) == -1) {
            if(driverPickUpsForPassanger ==null || driverPickUpsForPassanger.size() == 0) {
                requestGetPendingPickup();
            }
        }

        settingActivity.hideKeyboard();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        driverPickUpsForPassanger = StorageDataHelper.getInstance(getActivity()).getDriverPickUpsForPassanger();
        updateTripInformation();
        if(StorageDataHelper.getInstance(getActivity()).getShownStyleImages()) {
            carStyleComboTitleLinearLayout.setVisibility(View.VISIBLE);
            carStyleComboLinearLayout.setVisibility(View.VISIBLE);
        } else {
            carStyleComboTitleLinearLayout.setVisibility(View.GONE);
            carStyleComboLinearLayout.setVisibility(View.GONE);
        }
    }

    VehicleStyleScrollable vehicleStyleScrollable;

    TextView current_location, destination_location, trip_time, additional_notes;
    TextView passanger_count, fare_type, fare_hr, estimate_fare, reservation_id, reservation_id_title, driver_value, car_style_value;
    TextView call_driver_btn, review_cancel_textview, call_company_office_btn,flight_info_btn;

    ImageView styleCarImageView;
    TextView styleCarTextView;
    TextView additionalHelpTextView;
    LinearLayout flight_info_panel, my_trip_call_company_office;
    LinearLayout trip_in_progress_notification, trip_reservation_notification, trip_future_reservation_notifcation, review_cancel_btn;
    LinearLayout carStyleComboLinearLayout,carStyleComboTitleLinearLayout;

    private void initView(View v) {
        carStyleComboTitleLinearLayout = (LinearLayout)v.findViewById(R.id.carStyleComboTitleLinearLayout);
        carStyleComboLinearLayout = (LinearLayout)v.findViewById(R.id.carStyleComboLinearLayout);

        vehicleStyleScrollable = (VehicleStyleScrollable) v.findViewById(R.id.vehicleStyleScrollable_mytrip);
        vehicleStyleScrollable.setMyTripStyle();

        trip_in_progress_notification = (LinearLayout) v.findViewById(R.id.trip_in_progress_notification);
        trip_reservation_notification = (LinearLayout) v.findViewById(R.id.trip_reservation_notification);
        trip_future_reservation_notifcation = (LinearLayout) v.findViewById(R.id.trip_future_reservation_notification);
        additionalHelpTextView = (TextView) v.findViewById(R.id.additional_text_in_bottom);

        current_location = (TextView) v.findViewById(R.id.edtCrntLoca_mytrip);
        destination_location = (TextView) v.findViewById(R.id.setDestination_mytrip);
        trip_time = (TextView) v.findViewById(R.id.setTime_mytrip);
        additional_notes = (TextView) v.findViewById(R.id.txt_additional_notes_mytrip);

        passanger_count = (TextView) v.findViewById(R.id.num_passenger_mytrip);

        fare_type = (TextView) v.findViewById(R.id.txtSettNwTrpFareType_mytrip);
        fare_hr = (TextView) v.findViewById(R.id.txtSettNwTrpHrFare_mytrip);

        car_style_value = (TextView) v.findViewById(R.id.car_style_value);

        //Driver trip information
        estimate_fare = (TextView) v.findViewById(R.id.estimate_fare_value);
        reservation_id = (TextView) v.findViewById(R.id.reservation_id_value);
        reservation_id_title = (TextView) v.findViewById(R.id.reservation_id_title);
        driver_value = (TextView) v.findViewById(R.id.driver_value);

        call_driver_btn = (TextView) v.findViewById(R.id.call_driver_btn);
        call_company_office_btn = (TextView) v.findViewById(R.id.my_trip_call_company_office_btn);

        flight_info_panel = (LinearLayout) v.findViewById(R.id.flight_info_panel);
        flight_info_btn = (TextView) v.findViewById(R.id.view_flight_btn);

        my_trip_call_company_office = (LinearLayout) v.findViewById(R.id.my_trip_call_company_office);

        //cancel review btn
        review_cancel_btn = (LinearLayout) v.findViewById(R.id.review_cancel_btn);
        review_cancel_textview = (TextView) v.findViewById(R.id.review_cancel_textview);
        review_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ModifyTripActivity.class);
                intent.putExtra(ModifyTripActivity.INDEX_IN_PICKUPSDATA, currentTripPosition);
                intent.putExtra("fromCancel", "1");
                startActivity(intent);
            }
        });

        styleCarImageView = (ImageView) v.findViewById(R.id.styleCarImageView);
        styleCarTextView = (TextView) v.findViewById(R.id.styleCarTextView);
    }

    int currentTripPosition;
    public void applyMyTripsData() {

        currentTripPosition = 0;

        updateTripInformation();
    }

    public void showTripFromReservationID(int reservationID) {
        if(driverPickUpsForPassanger == null)
            return;

        for (int i=0; i < driverPickUpsForPassanger.size(); i++) {
            if(reservationID == driverPickUpsForPassanger.get(i).ReservationID) {
                currentTripPosition = i;
                break;
            }
        }
        //updateTripInformation();
        isSentRequest = false;
    }

    Boolean isPrepeareTripInProcess = false;
    public void prevTrip() {
        if(isPrepeareTripInProcess) {
            return;
        }

        isPrepeareTripInProcess = true;

        Log.d(TAG, "click before valid prevTrip");

        if(!validatePickUpData()) {
            return;
        }

        Log.d(TAG, "click after valid prevTrip");

        if(currentTripPosition - 1 >=0) {
            currentTripPosition--;
            updateTripInformation();
        }
        isPrepeareTripInProcess = false;
    }

    public void nextTrip() {
        Log.d(TAG, "click before valid nextTrip");
        if(isPrepeareTripInProcess) {
            return;
        }

        isPrepeareTripInProcess = true;
        if(!validatePickUpData()) {
            return;
        }
        Log.d(TAG, "click after valid nextTrip");

        if(currentTripPosition + 1 < driverPickUpsForPassanger.size()) {
            currentTripPosition++;
            updateTripInformation();
        }
        isPrepeareTripInProcess = false;
    }

    public void firstTrip() {
        Log.d(TAG, "click before valid firstTrip");
        if(isPrepeareTripInProcess) {
            return;
        }

        isPrepeareTripInProcess = true;
        if(!validatePickUpData()) {
            return;
        }
        Log.d(TAG, "click after valid firstTrip");

        currentTripPosition = 0;
        updateTripInformation();
        isPrepeareTripInProcess = false;
    }

    public void lastTrip() {
        Log.d(TAG, "click before valid lastTrip");
        if(isPrepeareTripInProcess) {
            return;
        }

        isPrepeareTripInProcess = true;
        if(!validatePickUpData()) {
            return;
        }
        Log.d(TAG, "click after valid lastTrip");

        currentTripPosition = driverPickUpsForPassanger.size()-1;
        updateTripInformation();
        isPrepeareTripInProcess = false;
    }

    public boolean validatePickUpData() {
        if(driverPickUpsForPassanger == null || driverPickUpsForPassanger.size() == 0) {
            return false;
        }

        return true;
    }

    public void requestGetFlightdetailes(int reservationId) {
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
                            GetFlightDetailsData data = getDriverLocationDataJsonServerResponse.Content;

                            if(data != null && data.flights.size() > 0)
                            {
                                if(driverPickUpsForPassanger != null && driverPickUpsForPassanger.size() > currentTripPosition) {
                                    if(data.flights !=null && data.flights.size() > 0){
                                        driverPickUpsForPassanger.get(currentTripPosition).FlightData = data.flights.get(0);
                                    }
                                }

                                showFlightDetailActivity(data);
                            }
                        }
                    }
                });

    }

    public void updateTripInformation() {
        trip_reservation_notification.setVisibility(View.GONE);
        trip_in_progress_notification.setVisibility(View.GONE);
        trip_future_reservation_notifcation.setVisibility(View.GONE);
        // set pageIndicator
        if(settingActivity != null && (driverPickUpsForPassanger != null))
            if(driverPickUpsForPassanger.size() == 0) {
                settingActivity.mytrip_text_main.setText("0/0");
            } else {
                settingActivity.mytrip_text_main.setText((currentTripPosition + 1) + "/" + driverPickUpsForPassanger.size());
            }

        Const.currentItem = currentTripPosition;
        if(driverPickUpsForPassanger == null || driverPickUpsForPassanger.size() == 0) {
            return;
        }

        if(driverPickUpsForPassanger.size() == 0) {
            settingActivity.setTabView(settingActivity.SETTINGS_NEW_TRIP_TAB);
            // go to new trip
            return;
        }
        final PickUpReservationData currentTripData = driverPickUpsForPassanger.get(currentTripPosition);

        if(currentTripData.canModify()) {
            trip_in_progress_notification.setVisibility(View.VISIBLE);
            review_cancel_textview.setText(Utils.getStringById(R.string.my_trips_fragment_get_modify_trip));
            review_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ModifyTripActivity.class);
                    intent.putExtra(ModifyTripActivity.INDEX_IN_PICKUPSDATA, currentTripPosition);
                    intent.putExtra("fromCancel", "1");
                    startActivity(intent);
                }
            });
        } else {
            trip_in_progress_notification.setVisibility(View.GONE);
            review_cancel_textview.setText(Utils.getStringById(R.string.my_trips_fragment_get_write_review));
            review_cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // write review
                    Const.currentItem = currentTripPosition;
                    startActivity(new Intent(getActivity(),
                            RateActivity.class));
                }
            });
        }

        if(currentTripData.ReservationSubStatus != null && currentTripData.ReservationSubStatus.equalsIgnoreCase(Const.RESERVATION_SUB_STATUS_UNASSIGNED)) {
            trip_in_progress_notification.setVisibility(View.GONE);
            trip_reservation_notification.setVisibility(View.VISIBLE);
            trip_future_reservation_notifcation.setVisibility(View.GONE);
            additionalHelpTextView.setText(R.string.my_trips_fragment_text_to_modify_unassign);
        } else if(currentTripData.ReservationSubStatus != null && currentTripData.ReservationSubStatus.equalsIgnoreCase(Const.RESERVATION_SUB_STATUS_AWAITINGCONFIRMATION)) {
            trip_in_progress_notification.setVisibility(View.GONE);
            trip_reservation_notification.setVisibility(View.GONE);
            trip_future_reservation_notifcation.setVisibility(View.VISIBLE);
            additionalHelpTextView.setText(R.string.my_trips_fragment_text_to_modify_unassign);
        } else {
            additionalHelpTextView.setText(R.string.my_trips_fragment_text_to_modify);
        }

        if(currentTripData.isCanceledByDriver()) {
            review_cancel_btn.setVisibility(View.GONE);
        } else {
            review_cancel_btn.setVisibility(View.VISIBLE);
        }

        current_location.setText(currentTripData.PickupLocation.location_name);
        destination_location.setText(currentTripData.DestinationLocation.location_name);
        trip_time.setText(Utils.millisToDate(Long.parseLong(currentTripData.TimeOfPickup)*1000, "hh:mm a MM/dd/yyyy"));
        passanger_count.setText(currentTripData.NumberOfPassenger + "");

        additional_notes.setText(currentTripData.SpecialInstructions);

        int imageStyleId = VehicleStyleScrollable.getImageForVehicleStyle(currentTripData.CabStyleRequired);
        styleCarImageView.setImageResource(imageStyleId);
        styleCarTextView.setText(currentTripData.CabStyleRequired);

        car_style_value.setText(currentTripData.CabStyleRequired);
        //vehicleStyleScrollable.setStyle(currentTripData.CabStyleRequired);

        estimate_fare.setText("$"+String.format("%.02f", currentTripData.EstimateFare));
        if(currentTripData.TripNumber == 0 || currentTripData.TripNumber== -1) {
            reservation_id_title.setText(getResources().getText(R.string.my_trips_fragment_reservation_id_title));
            reservation_id.setText(currentTripData.ReservationID+"");
        } else {
            reservation_id_title.setText(getResources().getText(R.string.my_trips_fragment_trip_number_title));
            reservation_id.setText(String.valueOf(currentTripData.TripNumber));
        }

        driver_value.setText(currentTripData.DriverName);



        if(currentTripData.HaveFlightInfo) {
            flight_info_panel.setVisibility(View.VISIBLE);
        } else {
            flight_info_panel.setVisibility(View.GONE);
        }

        final String driverNumber = currentTripData.DriverPhoneNumber;
        call_driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + driverNumber));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        if(currentTripData.CompanyPhoneNumber == null || currentTripData.CompanyPhoneNumber.equalsIgnoreCase("")) {
            my_trip_call_company_office.setVisibility(View.GONE);
        } else {
            my_trip_call_company_office.setVisibility(View.VISIBLE);
            final String companyNumber = currentTripData.CompanyPhoneNumber;
            call_company_office_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + companyNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            });
        }

        flight_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGetFlightdetailes(currentTripData.ReservationID);
            }
        });

        Const.RESRAVTION_ID_MYTRIP = currentTripData.ReservationID;
        /*if (currentTripData.HaveFlightInfo && currentTripData.IsAirport) {
            requestGetFlightdetailes();
        }*/

    }

    public void requestGetPendingPickup() {
        NetworkApi api = (new NetworkService()).getApi();

        api.getPickupsForPassenger(StorageDataHelper.getInstance(getActivity()).getPhoneToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<PickUpReservationData>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonServerResponse<List<PickUpReservationData>> pickUpDataJsonServerResponse) {
                        if(pickUpDataJsonServerResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getActivity()).setDriverPickUpsForPassanger(pickUpDataJsonServerResponse.Content);
                            driverPickUpsForPassanger = pickUpDataJsonServerResponse.Content;
                            applyMyTripsData();
                        }
                    }
                });
    }

    public void showFlightDetailActivity(GetFlightDetailsData resFlightData) {
        if(resFlightData != null && resFlightData.flights.size() > 0) {
            Intent intent = new Intent(getActivity(), FlightDetailView.class);
            intent.putExtra(Const.FLIGHT_DETAIL, resFlightData.flights.get(0));

            startActivity(intent);
        }
    }
}
