package com.passengerapp.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.StorageDataHelper;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 4/26/15.
 */
public class HistoryListActivity extends Activity {

    private DriverViewModel viewModel;
    private LinearLayout sQuotes_data_table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list_ordered);
        viewModel = new DriverViewModel();

        initView();

        List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();
        if(pickUpForPassanger ==null || pickUpForPassanger.size() == 0) {
            requestGetPendingPickup();
        }

        applyMyTripsData();
    }

    public void initView() {
        sQuotes_data_table = (LinearLayout) findViewById(R.id.quotes_history_data_layout);
    }

    private void createTable() {
        sQuotes_data_table.removeAllViews();

        LinearLayout lRow;

        TextView new_column_1_date_txt, new_column_2_dropoff_txt, new_column_3_amount_txt;
        LinearLayout new_column_1_date, new_column_2_dropoff, new_column_3_amount;


        List<PickUpReservationData> filtered_adapter = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();


        if(filtered_adapter == null) {
            return;
        }

        for (int current = 0; current < filtered_adapter.size(); current++) {
            lRow = new LinearLayout(this);
            lRow.setOrientation(LinearLayout.HORIZONTAL);
            lRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));



            final PickUpReservationData driver = filtered_adapter.get(current);
                View.OnClickListener clickRow = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_MY_TRIPS_TAB);
                        intent.putExtra(SettingActivity.EXTRA_INTENT_ONLYDETAIL, true);
                        intent.putExtra(SettingActivity.EXTRA_INTENT_ORDER_ID, driver.ReservationID);

                        startActivity(intent);
                    }
                };

                int paddingBorder = 0;

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
                new_column_1_date = new LinearLayout(this);
                new_column_1_date.setOrientation(LinearLayout.VERTICAL);
                new_column_1_date.setBackgroundColor(colorCell);

                new_column_1_date_txt = new TextView(this);
                new_column_1_date_txt.setGravity(Gravity.CENTER_HORIZONTAL);
                new_column_1_date_txt.setText(driver.TimeOfPickup+"");
                new_column_1_date_txt.setTextColor(getResources().getColor(R.color.dark_graylight));

                new_column_1_date.addView(new_column_1_date_txt);

                // ------------------------ //

                // drop off
                new_column_2_dropoff = new LinearLayout(this);
                new_column_2_dropoff.setOrientation(LinearLayout.VERTICAL);
                new_column_2_dropoff.setBackgroundColor(colorCell);

                new_column_2_dropoff_txt = new TextView(this);
                new_column_2_dropoff_txt.setGravity(Gravity.CENTER_HORIZONTAL);
                new_column_2_dropoff_txt.setText(driver.DestinationLocation.location_name);
                new_column_2_dropoff_txt.setTextColor(getResources().getColor(R.color.dark_graylight));

                new_column_2_dropoff.addView(new_column_2_dropoff_txt);

                // amount
                new_column_3_amount = new LinearLayout(this);
                new_column_3_amount.setOrientation(LinearLayout.VERTICAL);
                new_column_3_amount.setBackgroundColor(colorCell);

                new_column_3_amount_txt = new TextView(this);
                new_column_3_amount_txt.setGravity(Gravity.CENTER_HORIZONTAL);
                //String fare = "$" + Utils.modifyFare(driver.EstimateFare);
                new_column_3_amount_txt.setText("$" + driver.EstimateFare);
                new_column_3_amount_txt.setTextColor(getResources().getColor(R.color.dark_graylight));

                new_column_3_amount.addView(new_column_3_amount_txt);

            new_column_1_date.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            new_column_2_dropoff.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            new_column_3_amount.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            new_column_1_date.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
            new_column_2_dropoff.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            new_column_3_amount.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));

            new_column_1_date.setPadding(0, 0, paddingBorder, 0);
            new_column_2_dropoff.setPadding(0, 0, paddingBorder, 0);
            new_column_3_amount.setPadding(0, 0, paddingBorder, 0);

            lRow.addView(new_column_1_date);
            lRow.addView(new_column_2_dropoff);
            lRow.addView(new_column_3_amount);

            lRow.setPadding(0, 0, 0, 5);
            lRow.setBackgroundColor(Color.BLACK);

            sQuotes_data_table.addView(lRow, new LinearLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

//    IDriverServiceAsyncTaskListener<List<ReservationsData>> pickUpsForPassangerListener = new IDriverServiceAsyncTaskListener<List<ReservationsData>>() {
//        @Override
//        public void onReceiveResponse(List<ReservationsData> response) {
//            StorageDataHelper.getInstance(getBaseContext()).setDriverPickUpsForPassanger(response);
//            applyMyTripsData();
//        }
//    };

    public void requestGetPendingPickup() {

        NetworkApi api = (new NetworkService()).getApi();

        api.getPickupsForPassenger(StorageDataHelper.getInstance(getBaseContext()).getPhoneToken())
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
                            StorageDataHelper.getInstance(getBaseContext()).setDriverPickUpsForPassanger(pickUpDataJsonServerResponse.Content);
                            applyMyTripsData();
                        }
                    }
                });
    }

    private void applyMyTripsData() {
        createTable();
    }
}
