package com.passengerapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.activities.PassengerBaseAppCompatActivity;
import com.passengerapp.main.network.model.request.UpdatePickupTimeRequest;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.ReservationIdRequest;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.response.GetFlightDetailsData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.uc.WheelDateTimeActivity;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.Calendar;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Igor on 01.08.2015.
 */
public class ModifyTripActivity extends PassengerBaseAppCompatActivity {
    public static final String INDEX_IN_PICKUPSDATA = "index_in_pickupsdata";
    public static final int MODIFY_FLIGHT_DETAIL_ACTIVITY = 2;

    private TextView dateTimeEditText;
    Calendar tripPickUpTime;

    private LinearLayout flightInfoPanel;
    private TextView flightButtonView;
    private TextView flightButtonEdit;

    Button cancelButton;
    Button doneButton;

    private LinearLayout cancelTripButton;

    private TextView backBtn;
    private TextView doneBtn;
    private TextView titleActionBar;

    private int currentTripPosition;
    private DriverViewModel viewModel;

    private static int EDIT_FLIGHT_INFO_ACTIVITY = 2;
    private static int VIEW_FLIGHT_INFO_ACTIVITY = 3;

    PickUpReservationData currentTripData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.back_done_actionbar);
        Toolbar parent = (Toolbar) getSupportActionBar().getCustomView().getParent();
        parent.setContentInsetsAbsolute(0, 0);

        currentTripData = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger().get(currentTripPosition);


        viewModel = new DriverViewModel();


        backBtn = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.back_textview_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneBtn = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.done_textview_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isModifyTripDate) {
                    updateDateForTrip();
                } else {
                    finish();
                }
            }
        });
        titleActionBar = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.title_of_activity);
        titleActionBar.setVisibility(View.VISIBLE);


        setContentView(R.layout.activity_modify_my_trip);

        tripPickUpTime = Calendar.getInstance();

        dateTimeEditText = (TextView) findViewById(R.id.textview_time_edit);


        flightInfoPanel = (LinearLayout) findViewById(R.id.modify_trip_flight_info);
        flightButtonView = (TextView) findViewById(R.id.modify_flightinfo_btn);
        flightButtonEdit = (TextView) findViewById(R.id.modify_flightinfo_edit_btn);


        cancelTripButton = (LinearLayout) findViewById(R.id.modify_cancel_trip);

        updateUI();
    }

    private void updateUI() {
        titleActionBar.setText(Utils.getStringById(R.string.my_trips_fragment_get_modify_trip));

        currentTripPosition = getIntent().getIntExtra(INDEX_IN_PICKUPSDATA, 0);

        List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();
        if (pickUpForPassanger == null || pickUpForPassanger.size() == 0) {
            dateTimeEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            cancelTripButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }


        cancelTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyTripActivity.this, CancelActivity.class);
                intent.putExtra(Const.RESERVATION_ID_TO_CANCEL_DRIVER, currentTripData.ReservationID);
                intent.putExtra(Const.DRIVER_PHONE_NUMBER_TO_CANCEL_DRIVER, currentTripData.DriverPhoneNumber);
                intent.putExtra(Const.CANCEL_TO_DRIVER_IMMEDIATELY, true);
                startActivity(intent);
            }
        });


        final Long tripPickUpDateInMillis = Long.parseLong(currentTripData.TimeOfPickup) * 1000;

        dateTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dateTimePicker = new Intent(ModifyTripActivity.this, WheelDateTimeActivity.class);
                dateTimePicker.putExtra(WheelDateTimeActivity.DATE_IN_MILLIS, tripPickUpDateInMillis);
                dateTimePicker.putExtra("tag", 0);
                startActivityForResult(dateTimePicker, 1);
            }
        });

        updateTimeEditText(tripPickUpDateInMillis);


        if (currentTripData.HaveFlightInfo && currentTripData.IsAirport) {
            flightInfoPanel.setVisibility(View.VISIBLE);
            flightButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentTripData.FlightData != null) {
                        showEditFlightDataActivity(currentTripData.FlightData);
                    } else {
                        requestGetFlightDetail(EDIT_FLIGHT_INFO_ACTIVITY);
                    }
                }
            });
            flightButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentTripData.FlightData != null) {
                        showViewFlightDataActivity(currentTripData.FlightData);
                    } else {
                        requestGetFlightDetail(VIEW_FLIGHT_INFO_ACTIVITY);
                    }
                }
            });
        } else {
            flightInfoPanel.setVisibility(View.GONE);
        }
    }


    private void showViewFlightDataActivity(SaveFlightDetailRequest data) {
        Intent intent = new Intent(this, FlightDetailView.class);
        intent.putExtra(Const.FLIGHT_DETAIL, data);

        startActivity(intent);
    }

    private void showEditFlightDataActivity(SaveFlightDetailRequest data) {
        Intent intent = new Intent(ModifyTripActivity.this, FlightActivity.class);
        intent.putExtra(Const.MODIFY_FLIGHT_DETAIL, data);
        intent.putExtra(Const.MODIFY_FLIGHT_DETAIL_DRIVER_CAR_CLASS, "");
        startActivityForResult(intent, MODIFY_FLIGHT_DETAIL_ACTIVITY);
    }

    private boolean isModifyTripDate = false;
    private long currentTime = 0;

    private void updateTimeEditText(long value) {
        currentTime = value;
        tripPickUpTime.setTimeInMillis(value);
        dateTimeEditText.setText(Utils.millisToDate(tripPickUpTime.getTimeInMillis(), "hh:mm a MM/dd/yyyy"));
    }

    private void updateDateForTrip() {
        List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();
        if (pickUpForPassanger != null && pickUpForPassanger.size() > currentTripPosition) {
            pickUpForPassanger.get(currentTripPosition).TimeOfPickup = (currentTime / 1000) + "";
        }

        requestUpdatePickupTime();
    }

    public void requestGetFlightDetail(final int activtyNextToShow) {

        if (!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.my_trips_fragment_get_getting_flight_detail));

        ReservationIdRequest request = new ReservationIdRequest();
        request.ReservationID = currentTripData.ReservationID;

        NetworkApi api = (new NetworkService()).getApi();

        api.getFlightDetails(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<GetFlightDetailsData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<GetFlightDetailsData> getDriverLocationDataJsonServerResponse) {
                        if (getDriverLocationDataJsonServerResponse.IsSuccess) {
                            GetFlightDetailsData data = getDriverLocationDataJsonServerResponse.Content;

                            if (activtyNextToShow == EDIT_FLIGHT_INFO_ACTIVITY) {
                                if (data.flights != null && data.flights.size() != 0) {
                                    showEditFlightDataActivity(data.flights.get(0));
                                }
                            } else if (activtyNextToShow == VIEW_FLIGHT_INFO_ACTIVITY) {
                                if (data.flights != null && data.flights.size() != 0) {
                                    showViewFlightDataActivity(data.flights.get(0));
                                }
                            }

                        } else {
                            Utils.showWarningMsg(ModifyTripActivity.this, getResources().getString(R.string.activity_flight_data_error));
                        }
                    }
                });
    }

    public void requestUpdatePickupTime() {
        if (!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.my_trips_fragment_get_getting_flight_detail));

        UpdatePickupTimeRequest request = new UpdatePickupTimeRequest();
        request.ReservationID = currentTripData.ReservationID;
        request.TripNumber = currentTripData.TripNumber;
        request.DateOfPickup = Utils.millisToDate(currentTime, "M/dd/yyyy");
        request.TimeOfPickup = Utils.millisToDate(currentTime, "hh:mm a");

        NetworkApi api = (new NetworkService()).getApi();

        api.updatePickupTime(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<String>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<String> updatePickupTimeResponse) {
                        if (updatePickupTimeResponse.IsSuccess) {
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            switch (data.getIntExtra("tag", 0)) {
                case 0:
                    long selectedMilliseconds;
                    boolean isNow = data.getBooleanExtra(WheelDateTimeActivity.IS_NOW, false);
                    if (isNow) {
                        selectedMilliseconds = System.currentTimeMillis();
                    } else {
                        selectedMilliseconds = data.getLongExtra(WheelDateTimeActivity.DATE_IN_MILLIS, System.currentTimeMillis());
                    }
                    isModifyTripDate = true;
                    updateTimeEditText(selectedMilliseconds);
            }
        }
    }
}
