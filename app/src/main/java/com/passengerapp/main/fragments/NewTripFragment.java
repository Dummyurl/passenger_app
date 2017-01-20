package com.passengerapp.main.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.SettingActivity;
import com.passengerapp.main.dialogs.SetDestinationDialog;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.request.SearchDriversRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.SearchDriversLaterData;
import com.passengerapp.main.services.rest.WebInterface;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.uc.WheelActivity;
import com.passengerapp.main.uc.WheelDateTimeActivity;
import com.passengerapp.main.uc.WheelHourMinuteActivity;
import com.passengerapp.main.widget.VehicleStyleScrollable;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 11/9/14.
 */
public class NewTripFragment extends Fragment {

    public static final int RESULT_SPEECH = 1;
    public static final int RESULT_SPEECH_CURR = 2;
    private final String CURRENTLOCATION = "Current Location";
    private SettingActivity settingActivity;

    @Bind(R.id.pickup_location_textview) TextView pickupLocationTextView;
    @Bind(R.id.destination_location_textview) TextView destinationLocationTextView;
    @Bind(R.id.settime_textview) TextView setTimeTextView;
    @Bind(R.id.number_of_passenger_textview)TextView numberOfPassanger;
    @Bind(R.id.rent_by_option_select_combobox) TextView rentByOptionSelect;
    @Bind(R.id.by_hour_hours) TextView byHourHours;
    @Bind(R.id.by_hour_minutes) TextView byHourMinutes;
    @Bind(R.id.txt_car_style_combo_list) TextView carStyleComboListTextView;
    @Bind(R.id.vehicleStyleScrollable_newtrip) VehicleStyleScrollable vehicleStyleScrollable;
    @Bind(R.id.txt_additional_notes) EditText additionalNotesTextView;

    @Bind(R.id.car_style_choose_title) LinearLayout carStyleChooseTitle;
    @Bind(R.id.destination_row) LinearLayout destination_row;
    @Bind(R.id.by_hour_row) LinearLayout by_hour_row;
    @Bind(R.id.comboboxSelectCarStyle) LinearLayout comboboxSelectCarStyle;


    private SearchDriversTempData searchDriversTempData;
    private final String[] fareType = {"Point A to Point B", "By the hour"};

    @OnClick({R.id.pickup_location_textview, R.id.destination_location_textview})
    public void openChooseDestinationActivity() {
        startActivityForResult(new Intent(getActivity(),SetDestinationDialog.class), 3);
    }

    @OnClick(R.id.settime_textview)
    public void showDatePicker(){
        Intent intent = new Intent(getActivity(), WheelDateTimeActivity.class)
                .putExtra(WheelDateTimeActivity.DATE_IN_MILLIS, System.currentTimeMillis())
                .putExtra("tag", 4);

        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.rent_by_option_select_combobox)
    public void openRentByDialog(){
            Intent intent = new Intent(getActivity(), WheelActivity.class)
                    .putExtra("content_array", fareType)
                    .putExtra("selected_value", rentByOptionSelect.getText().toString())
                    .putExtra("tag", 8);
            startActivityForResult(intent, 1);
    }


    @OnClick(R.id.number_of_passenger_textview)
    public void openNumberPassangerDialog() {
        String[] num_passengers = {"1", "2", "3", "4", "5", "6", "7", "8",
                "9", "10", "11", "12"};
        Intent intent = new Intent(getActivity(), WheelActivity.class)
                .putExtra("content_array", num_passengers)
                .putExtra("selected_value", numberOfPassanger.getText().toString())
                .putExtra("tag", 1);
        startActivityForResult(intent, 1);
    }

    @OnClick({R.id.by_hour_hours, R.id.by_hour_minutes})
    public void openPickupHourDialog(){
        Intent intent = new Intent(getActivity(), WheelHourMinuteActivity.class)
                .putExtra("selected-hour", byHourHours.getText().toString())
                .putExtra("selected-minute", byHourMinutes.getText().toString())
                .putExtra("tag", 9);
        startActivityForResult(intent, 1);
    }


    @OnClick(R.id.txt_car_style_combo_list)
    public void openCarStyleList(){
        Intent intent = new Intent(getActivity(), WheelActivity.class)
                .putExtra("content_array", VehicleStyleScrollable.getCarStyleList())
                .putExtra("selected_value", carStyleComboListTextView.getText().toString())
                .putExtra("tag", 2);

        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.call_office_newtrib_btn)
    public void callToOffice() {
        Intent callIntent = new Intent(Intent.ACTION_CALL)
                .setData(Uri.parse("tel:" + "123"))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingActivity = (SettingActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        searchDriversTempData = StorageDataHelper.getInstance(getActivity()).getSearchDriversTempDataForRequest();

        View v = inflater.inflate(R.layout.activity_setting_new_trip_frgment, container, false);
        ButterKnife.bind(this, v);

        if ((searchDriversTempData.tripType == null) || searchDriversTempData.tripType.isEmpty()) {
            searchDriversTempData.tripType = Const.BY_THE_DURATION_OF_TRIP;
        }
        if ((searchDriversTempData.bookingTime == null) || searchDriversTempData.bookingTime.isEmpty()) {
            searchDriversTempData.bookingTime = Const.TIME_LABEL_FOR_PICKUP_NOW;
        }

        initView(v);


        return v;
    }

    @Override
    public void onPause() {
        StorageDataHelper.getInstance(getActivity()).setSearchDriversTempDataForRequest(searchDriversTempData);
        super.onPause();
    }

    @Override
    public void onResume() {
        searchDriversTempData = StorageDataHelper.getInstance(getActivity()).getSearchDriversTempDataForRequest();
        if (StorageDataHelper.getInstance(getActivity()).getShownStyleImages()) {
            vehicleStyleScrollable.setVisibility(View.VISIBLE);
            comboboxSelectCarStyle.setVisibility(View.GONE);
        } else {
            vehicleStyleScrollable.setVisibility(View.GONE);
            comboboxSelectCarStyle.setVisibility(View.VISIBLE);
        }
        if (!StorageDataHelper.getInstance(getActivity()).getDisplayFilterBar()) {
            vehicleStyleScrollable.setVisibility(View.GONE);
            comboboxSelectCarStyle.setVisibility(View.GONE);
        }

        if (StorageDataHelper.getInstance(getActivity()).getShownStyleImages()) {
            carStyleChooseTitle.setVisibility(View.VISIBLE);
            vehicleStyleScrollable.setVisibility(View.VISIBLE);
            comboboxSelectCarStyle.setVisibility(View.GONE);
        } else {
            vehicleStyleScrollable.setVisibility(View.GONE);
            carStyleChooseTitle.setVisibility(View.GONE);
            comboboxSelectCarStyle.setVisibility(View.GONE);
        }

        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initView(View v) {
        if ((searchDriversTempData.pickUpLocation.location_name != null) && !searchDriversTempData.pickUpLocation.location_name.isEmpty()) {
            pickupLocationTextView.setText(searchDriversTempData.pickUpLocation.location_name);
        } else {
            pickupLocationTextView.setText(CURRENTLOCATION + "\n" + Const.currentLocationName);
        }

        destinationLocationTextView.setText(((searchDriversTempData.destinationLocation.location_name != null) && !searchDriversTempData.destinationLocation.location_name.isEmpty()) ? searchDriversTempData.destinationLocation.location_name : "");

        if (searchDriversTempData.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_NOW)) {
            setTimeTextView.setText(Const.TIME_LABEL_FOR_PICKUP_NOW);
            searchDriversTempData.timeToPick = System.currentTimeMillis();
        } else {
            String date = "";
            if (searchDriversTempData.timeToPick != 0) {
                date = Utils.millisToDate(searchDriversTempData.timeToPick, "hh:mm a MM/dd/yyyy");
            }

            setTimeTextView.setText(date);
        }

        if (searchDriversTempData.tripType == Const.BY_THE_HOUR) {
            rentByOptionSelect.setText(fareType[1]);
        } else {
            rentByOptionSelect.setText(fareType[0]);
        }
    }

    public void newTripButtonPressed() {
        String result = null;
        result = ValidationScreen();
        if (result != null) {
            AlertDailogView.showAlert(getActivity(), result, "Ok").show();
        } else {
            if (StorageDataHelper.getInstance(getActivity()).getLatestLocation() != null) {
                if (WebInterface.isNetworkAvailable(getActivity())) {
                    prepeareGetQuotes();
                } else {
                    Toast.makeText(getActivity(), Utils.getStringById(R.string.common_no_internet_connections_after_smth), Toast.LENGTH_SHORT).show();
                }
           } else {
               Toast.makeText(getActivity(),Utils.getStringById(R.string.common_no_current_location),Toast.LENGTH_SHORT).show();
           }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) { // success
            switch (data.getIntExtra("tag", 0)) {
                case 1:
                    numberOfPassanger.setText(data.getStringExtra("selected_value"));
                    searchDriversTempData.numberOfPassanger = Integer.parseInt(numberOfPassanger.getText().toString());
                    break;
                case 2:
                    String selectedCarStyle = data.getStringExtra("selected_value");
                    carStyleComboListTextView.setText(selectedCarStyle);
                    searchDriversTempData.vehicleStyle = selectedCarStyle;
                    break;
                case 4:
                    long selectedMiliseconds = data.getLongExtra(WheelDateTimeActivity.DATE_IN_MILLIS, System.currentTimeMillis());
                    Boolean isNow = data.getBooleanExtra(WheelDateTimeActivity.IS_NOW, true);

                    if (isNow) {
                        searchDriversTempData.timeToPick = System.currentTimeMillis();
                        searchDriversTempData.bookingTime = Const.TIME_LABEL_FOR_PICKUP_NOW;
                        setTimeTextView.setText(Const.TIME_LABEL_FOR_PICKUP_NOW);
                    } else {
                        searchDriversTempData.timeToPick = selectedMiliseconds;
                        setTimeTextView.setText(Utils.millisToDate(selectedMiliseconds, "hh:mm a MM/dd/yyyy"));
                        searchDriversTempData.bookingTime = Const.TIME_LABEL_FOR_PICKUP_LATER;
                    }

                    break;
                case 8:
                    rentByOptionSelect.setText(data.getStringExtra("selected_value"));
                    if (data.getStringExtra("selected_value").toString().equals("By the hour")) {
                        searchDriversTempData.tripType = Const.BY_THE_HOUR;

                        destination_row.setVisibility(View.GONE);
                        by_hour_row.setVisibility(View.VISIBLE);
                    } else {
                        searchDriversTempData.tripType = Const.BY_THE_DURATION_OF_TRIP;

                        destination_row.setVisibility(View.VISIBLE);
                        by_hour_row.setVisibility(View.GONE);
                    }
                    break;
                case 9:
                    String hour = data.getStringExtra("selected-hour");
                    String minute = data.getStringExtra("selected-minute");


                    byHourHours.setText(hour);
                    if (minute.contentEquals("0")) {
                        byHourMinutes.setText("00");
                    } else {
                        byHourMinutes.setText(minute);
                    }

                    searchDriversTempData.timeByHourMode = (Integer.parseInt(hour.trim())*60+Integer.parseInt(minute))/60;
                    settingActivity.setFooterPanelOfTab(settingActivity.SETTINGS_NEW_TRIP_TAB);

                    break;

            }

        } else if (requestCode == 3) {
            if (searchDriversTempData.pickUpLocation.location_name != null && !searchDriversTempData.pickUpLocation.location_name.equals("")) {
                pickupLocationTextView.setText(searchDriversTempData.pickUpLocation.location_name);
                settingActivity.setFooterPanelOfTab(settingActivity.SETTINGS_NEW_TRIP_TAB);
            }

            if (searchDriversTempData.destinationLocation.location_name!= null && !searchDriversTempData.destinationLocation.location_name.equals("")) {
                destinationLocationTextView.setText(searchDriversTempData.destinationLocation.location_name);
                settingActivity.setFooterPanelOfTab(settingActivity.SETTINGS_NEW_TRIP_TAB);
            }
        }

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    try {
                        ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        destinationLocationTextView.setText(text.get(0));
                    } catch (Exception e) {

                    }
                }
                break;
            }
            case RESULT_SPEECH_CURR: {
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    try {
                        ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        pickupLocationTextView.setText(text.get(0));
                    } catch (Exception e) {

                    }
                }
                break;
            }

        }
    }

    private String ValidationScreen() {

        String result = null;

        if (pickupLocationTextView.getText().toString().trim() == null || pickupLocationTextView.getText().toString().trim().equals("")) {
            result = Utils.getStringById(R.string.set_destination_dialog_please_enter_current_location);
        } else if (searchDriversTempData.tripType.contentEquals(Const.BY_THE_DURATION_OF_TRIP) && (destinationLocationTextView.getText().toString().trim() == null || destinationLocationTextView.getText().toString().trim().equals(""))) {
            result = Utils.getStringById(R.string.set_destination_dialog_please_enter_destination_location);
        } else if (searchDriversTempData.tripType.contentEquals(Const.BY_THE_HOUR) && (byHourHours.getText().toString().trim().contentEquals("0") && byHourMinutes.getText().toString().trim().equals("00"))) {
            result = Utils.getStringById(R.string.my_trips_fragment_get_enter_hours);
        } else if (setTimeTextView.getText().toString() == "" || searchDriversTempData.timeToPick == 0) {
            result = Utils.getStringById(R.string.my_trips_fragment_get_enter_valid_time);
        }

        return result;
    }

    public void prepeareGetQuotes() {
        Const.CURRENT = searchDriversTempData.pickUpLocation.location_name;
        Const.DESTINATION = searchDriversTempData.destinationLocation.location_name;
        Const.srcLat = Double.parseDouble(searchDriversTempData.pickUpLocation.latitude);
        Const.srcLong = Double.parseDouble(searchDriversTempData.pickUpLocation.longitude);
        Const.destLat = Double.parseDouble(searchDriversTempData.destinationLocation.latitude);
        Const.destLong = Double.parseDouble(searchDriversTempData.destinationLocation.longitude);

        if (searchDriversTempData.tripType.equalsIgnoreCase(Const.BY_THE_HOUR)) {
            Const.CURRENT = Const.currentLocationName;//LocationManagerHelper.getInstance(getActivity()).getLocationName(location);
            Const.srcLat = StorageDataHelper.getInstance(getActivity()).getLatestLocation().getLatitude();
            Const.srcLong = StorageDataHelper.getInstance(getActivity()).getLatestLocation().getLongitude();
        }

        Const.timeOfPic = String.valueOf(searchDriversTempData.timeToPick/1000);

        Const.bookingTime = searchDriversTempData.bookingTime;

        SearchDriversRequest cabRequest = new SearchDriversRequest();
        cabRequest.VipCode = StorageDataHelper.getInstance(getActivity()).getVipCode();
        cabRequest.TripTime = searchDriversTempData.timeByHourMode;
        cabRequest.TripType = searchDriversTempData.tripType;
        cabRequest.BookingTime = Const.bookingTime;
        cabRequest.NumberOfPassenger = searchDriversTempData.numberOfPassanger;
        cabRequest.Style = vehicleStyleScrollable.getCurrentVehicleName();
        cabRequest.StyleType = vehicleStyleScrollable.getCurrentVehicleId();

        cabRequest.SearchRadius = StorageDataHelper.getInstance(getActivity()).getSearchRadius();

        cabRequest.Distance = new DistanceData();
        cabRequest.Distance.Unit = searchDriversTempData.replyPickupUnit;
        cabRequest.Distance.Value = searchDriversTempData.replyPickupValue;

        cabRequest.PickupLocation = new LocationData();
        cabRequest.PickupLocation.latitude = String.valueOf(Const.srcLat);
        cabRequest.PickupLocation.longitude = String.valueOf(Const.srcLong);
        cabRequest.PickupLocation.location_name = Const.CURRENT;

        if(searchDriversTempData.tripType.contentEquals(Const.BY_THE_DURATION_OF_TRIP)) {
            cabRequest.DestinationLocation = new LocationData();
            cabRequest.DestinationLocation.latitude = String.valueOf(Const.destLat);
            cabRequest.DestinationLocation.longitude = String.valueOf(Const.destLong);
            cabRequest.DestinationLocation.location_name = Const.DESTINATION;
        }

        cabRequest.TimeOfPickup = Const.timeOfPic != null
                && !Const.timeOfPic.equals("") ? Const.timeOfPic : String.valueOf(System.currentTimeMillis() / 1000);
        Const.timeOfPicLastRequest = Const.timeOfPic;

        requestGetCabsFormSever(cabRequest);
    }

    public void searchDrivers(SearchDriversRequest request) {
        if(!settingActivity.isNetworkAvailable())
            return;

        settingActivity.DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();
        api.searchDrivers(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<SearchDriversData>>>() {
                    @Override
                    public void onCompleted() {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<SearchDriversData>> searchDriversData) {
                        if(searchDriversData.IsSuccess) {
                            StorageDataHelper.getInstance(getActivity()).setDriversDatasNow(searchDriversData);
                            getDriverMethod();
                        }
                    }
                });
    }

    public void searchDriversLater(SearchDriversRequest request) {
        if(!settingActivity.isNetworkAvailable())
            return;

        settingActivity.DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();
        api.searchDriversLater(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<SearchDriversLaterData>>>() {
                    @Override
                    public void onCompleted() {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        settingActivity.DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<SearchDriversLaterData>> searchDriversData) {
                        if(searchDriversData.IsSuccess) {
                            StorageDataHelper.getInstance(getActivity()).setDriversDatasLater(searchDriversData);
                            getDriverMethod();
                        }
                    }
                });

    }

    public void requestGetCabsFormSever(final SearchDriversRequest request) {
       if (Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_LATER)) {
           searchDriversLater(request);
       } else if (Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_NOW)) {
           searchDrivers(request);
       }
    }

    public void getDriverMethod() {
        StorageDataHelper.getInstance(getActivity()).setSearchDriversCurrentDataForRequest(searchDriversTempData);
        try {
            Const.mQouteState = 0;
            Logger.writeSimple("Show Quotes List");
            if(settingActivity != null) {
                settingActivity.setTabView(settingActivity.SETTINGS_QUOTES_TAB);
            } else {
                getActivity().finish();
            }
        } catch(Exception e) {

        }
    }
}
