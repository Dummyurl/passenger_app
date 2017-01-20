package com.passengerapp.main.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.SettingActivity;
import com.passengerapp.main.activities.PassengerBaseFragmentActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 9/5/15.
 */
public class MyTripsListFragment extends Fragment {

    private DriverViewModel viewModel;
    private LinearLayout sQuotes_data_table;
    private SettingActivity settingActivity;
    private List<PickUpReservationData> driverPickUpsForPassanger;

    public void setListMode(SettingActivity.ReservationListMode listMode) {
        this.listMode = listMode;
    }

    private SettingActivity.ReservationListMode listMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingActivity = (SettingActivity)getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v;
        v = inflater.inflate(R.layout.activity_history_list_ordered, container, false);

        if (listMode == null) {
            listMode = SettingActivity.ReservationListMode.ALL;
        }

        viewModel = new DriverViewModel();
        sQuotes_data_table = (LinearLayout) v.findViewById(R.id.quotes_history_data_layout);

        driverPickUpsForPassanger = StorageDataHelper.getInstance(getActivity()).getDriverPickUpsForPassanger();

        if(driverPickUpsForPassanger ==null || driverPickUpsForPassanger.size() == 0) {
            requestGetPendingPickup();
        }

        applyMyTripsData();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        driverPickUpsForPassanger = StorageDataHelper.getInstance(getActivity()).getDriverPickUpsForPassanger();
    }

    public List<PickUpReservationData> getFilterReservationByMode(SettingActivity.ReservationListMode mode) {
        List<PickUpReservationData> filteredPickUpForPassanger = new ArrayList<PickUpReservationData>();
        if(driverPickUpsForPassanger != null) {
            for (PickUpReservationData item : driverPickUpsForPassanger) {
                if (mode == SettingActivity.ReservationListMode.ALL) {
                    filteredPickUpForPassanger.add(item);
                } else if (mode == SettingActivity.ReservationListMode.CAN_MODIFY) {
                    if (item.canModify()) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.CAN_WRITE_REVIEW) {
                    if (item.canWriteReview()) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.HAS_REVIEW) {
                    if(item.canViewReview(getActivity())) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.ACTIVE) {
                    if(item.isActive()) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.HISTORICAL) {
                    if(item.isHistorical()) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.WRITE_REVIEW) {
                    if(item.isHistorical()) {
                        filteredPickUpForPassanger.add(item);
                    }
                } else if (mode == SettingActivity.ReservationListMode.GET_EMAIL_RECEIPT) {
                    if(item.isHistorical()) {
                        filteredPickUpForPassanger.add(item);
                    }
                }
            }
        }

        return filteredPickUpForPassanger;
    }

    private void createTable() {
        sQuotes_data_table.removeAllViews();

        TextView new_column_1_date_txt, new_column_2_dropoff_txt, new_column_3_amount_txt;
        LinearLayout new_column_1_date, new_column_2_dropoff, new_column_3_amount;


        List<PickUpReservationData> filtered_adapter = getFilterReservationByMode(this.listMode);

        if(filtered_adapter == null) {
            return;
        }

        for (int current = 0; current < filtered_adapter.size(); current++) {
            View lRow = null;
            lRow = View.inflate(settingActivity, R.layout.my_trips_item, null);

            final PickUpReservationData driver = filtered_adapter.get(current);
            View.OnClickListener clickRow = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    settingActivity.setTabView(SettingActivity.SETTINGS_MY_TRIPS_TAB);
                    settingActivity.showTripInformation(driver.ReservationID);
                }
            };

            lRow.setOnClickListener(clickRow);

            int colorCell = Color.WHITE;
            if (driver.ReservationStatus.equalsIgnoreCase("Active")) {
                colorCell = Color.WHITE;
            } else if (driver.ReservationStatus.equalsIgnoreCase("Completed")) {
                colorCell = Color.GRAY;
            } else if (driver.ReservationStatus.equalsIgnoreCase("CancelledByDriver")) {
                colorCell = Color.YELLOW;
            } else if (driver.ReservationStatus.equalsIgnoreCase("Declined")) {
                colorCell = Color.RED;
            }
            lRow.setBackgroundColor(colorCell);

            // date
            new_column_1_date = (LinearLayout)lRow.findViewById(R.id.mytripslist_date_column_layout);
            new_column_1_date.setBackgroundColor(colorCell);
            new_column_1_date_txt = (TextView)lRow.findViewById(R.id.mytripslist_date_column);
            new_column_1_date_txt.setText(Utils.millisToDate(Long.parseLong(driver.TimeOfPickup) * 1000, "MM/dd/yyyy"));

            // drop off
            new_column_2_dropoff = (LinearLayout)lRow.findViewById(R.id.mytripslist_drop_off_column_layout);
            new_column_2_dropoff.setBackgroundColor(colorCell);
            new_column_2_dropoff_txt = (TextView)lRow.findViewById(R.id.mytripslist_drop_off_column);
            new_column_2_dropoff_txt.setText(driver.DestinationLocation.location_name);

            // amount
            new_column_3_amount = (LinearLayout)lRow.findViewById(R.id.mytripslist_amount_column_layout);
            new_column_3_amount.setBackgroundColor(colorCell);
            new_column_3_amount_txt = (TextView)lRow.findViewById(R.id.mytripslist_amount_column);
            new_column_3_amount_txt.setText("$" + Utils.modifyFare(driver.EstimateFare));


            sQuotes_data_table.addView(lRow, new LinearLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

//    IDriverServiceAsyncTaskListener<List<ReservationsData>> pickupsForPassangerListener = new IDriverServiceAsyncTaskListener<List<ReservationsData>>() {
//        @Override
//        public void onReceiveResponse(List<ReservationsData> response) {
//            StorageDataHelper.getInstance(getActivity()).setDriverPickUpsForPassanger(response);
//            driverPickUpsForPassanger = response;
//            applyMyTripsData();
//        }
//    };
//
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
                        e.printStackTrace();
                        ((PassengerBaseFragmentActivity)getActivity()).showToastLong(e.getMessage());
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<PickUpReservationData>> pickUpDataJsonServerResponse) {
                        if (pickUpDataJsonServerResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getActivity()).setDriverPickUpsForPassanger(pickUpDataJsonServerResponse.Content);
                            driverPickUpsForPassanger = pickUpDataJsonServerResponse.Content;
                            applyMyTripsData();
                        }
                    }
                });
    }

    private void applyMyTripsData() {
        createTable();
    }
}
