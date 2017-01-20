package com.passengerapp.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.passengerapp.BuildConfig;
import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseFragmentActivity;
import com.passengerapp.main.adapter.MenuAdapter;
import com.passengerapp.main.dialogs.InformationDialogs.ArrivedDriverDialog;
import com.passengerapp.main.dialogs.InformationDialogs.CancelPickUpDialog;
import com.passengerapp.main.dialogs.InformationDialogs.ConfirmPickUpDialog;
import com.passengerapp.main.dialogs.InformationDialogs.DeclinedPickUpDialog;
import com.passengerapp.main.dialogs.InformationDialogs.IInformationDialogsActions;
import com.passengerapp.main.dialogs.InformationDialogs.WaitingDriverDialog;
import com.passengerapp.main.dialogs.SetCreditCardDataDialog;
import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.model.MenuItemData;
import com.passengerapp.main.model.http.data.PendingPickupData;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.DriverData;
import com.passengerapp.main.network.model.request.GetReservationLocationRequest;
import com.passengerapp.main.network.model.request.SetCompanyLinkRequest;
import com.passengerapp.main.network.model.response.GetReservationLocationData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.SearchDriversLaterData;
import com.passengerapp.main.network.model.response.SetCompanyLinkData;
import com.passengerapp.main.process.MonitoringDriverPosition;
import com.passengerapp.main.services.DriverTracker;
import com.passengerapp.main.services.DriverTrackerCallback;
import com.passengerapp.main.services.ServiceManager;
import com.passengerapp.main.services.backendPushService.BackendPushNotificationReceiver;
import com.passengerapp.main.services.backendPushService.IBackendPushNotificationListener;
import com.passengerapp.main.services.locationManager.ILocationManagerNotification;
import com.passengerapp.main.services.locationManager.LocationManagerHelper;
import com.passengerapp.main.services.locationManager.LocationManagerUpdateLocationReceiver;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends PassengerBaseFragmentActivity implements GoogleMap.OnMarkerClickListener,
        ILocationManagerNotification,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap map;

    @Bind(R.id.myLocation) ImageView myLocation;
    @Bind(R.id.location_name) TextView location_name;

    @Bind(R.id.get_request_layout_btn) FrameLayout getRequestBtn;
    @Bind(R.id.get_quotes_layout_btn) FrameLayout getQuotesBtn;
    @Bind(R.id.locMesge) TextView locMesge;
    @Bind(R.id.rlHeader) RelativeLayout rlHeader;

    @Bind(R.id.main_menu_drawerlayout) DrawerLayout mainMenuDrawerLayout;
    @Bind(R.id.menu_listview) ListView menuListView;

    private DriverViewModel driverViewModel;
    private ServiceManager servicemanager;
    private BackendPushNotificationReceiver backendPushNotificationReceiver;
    private LocationManagerUpdateLocationReceiver locationManagerUpdateLocationReceiver;

    private SearchDriversData searchDriversData;

    private WaitingDriverDialog dlg;
    private Marker vehicleMarker;

    private class driverData {
        public Marker marker;
        public String token;
    }

    private List<Marker> markerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButterKnife.bind(this);

        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        driverViewModel = new DriverViewModel();

        if (Utils.checkGooglePlayServicesAvailable(this)) {
            //setupReceiverToGetUpdatedLocation();
            setupGoogleMap();
        }

        setupReceiverToGetPushNotificationFromBackend();

        servicemanager = new ServiceManager();
        servicemanager.start(MainActivity.this);


        /*boolean AppBackground = getIntent().getBooleanExtra("AppBackground", false);
        if (AppBackground) {
            Intent broadcastintent = new Intent(CommonUtilities.DISPLAY_MESSAGE_ACTION);
            broadcastintent.putExtra(CommonUtilities.EXTRA_MESSAGE, getIntent().getStringExtra(CommonUtilities.EXTRA_MESSAGE));
            broadcastintent.putExtra(CommonUtilities.EXTRA_RESERVATIONID, getIntent().getStringExtra(CommonUtilities.EXTRA_RESERVATIONID));
            broadcastintent.putExtra(CommonUtilities.EXTRA_MESSAGECODE, getIntent().getStringExtra(CommonUtilities.EXTRA_MESSAGECODE));
            broadcastintent.putExtra(CommonUtilities.EXTRA_REPLYCODE, getIntent().getStringExtra(CommonUtilities.EXTRA_REPLYCODE));
            sendBroadcast(broadcastintent);
        }

        boolean isFromIDD = getIntent().getBooleanExtra("tagFromIDD", false);
        if(isFromIDD)
            showDriverMarkers();*/

        // setup get location settings
        setupLocationObject();

        // get settings of company data by vipcode
        getCompanyLinkData();

        // get all pickups for driver
        requestGetPendingPickup();

        // check if in app was active waiting driver dialog
        updateWaitingDialog();

        // set up left main menu
        setupLeftMenu();
    }


    /*private final int GET_A_QUOTE_OR_MAKE_RESERVATION = 0;
    private final int VIEW_RESERVATION = 1;
    private final int MODIFY_RESERVATION = 2;
    private final int WRITE_A_REVIEW = 3;
    private final int VIEW_PAST_REVIEWS = 4;
    private final int UPDATE_CREDIT_CARD = 5;
    private final int ACTIVE_TRIPS = 6;
    private final int HISTORICAL_TRIPS = 7;*/


    public void setupLeftMenu() {
        mainMenuDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        List<MenuItemData> listOfMenuItems = new ArrayList<MenuItemData>();
        listOfMenuItems.add(new MenuItemData("GET A QUOTE OR MAKE RESERVATION", MenuItemData.MenuItem.GET_A_QUOTE_OR_MAKE_RESERVATION));

        listOfMenuItems.add(new MenuItemData("ACTIVE TRIPS", MenuItemData.MenuItem.ACTIVE_TRIPS));
        listOfMenuItems.add(new MenuItemData("HISTORICAL TRIPS", MenuItemData.MenuItem.HISTORICAL_TRIPS));

        listOfMenuItems.add(new MenuItemData("WRITE A REVIEW", MenuItemData.MenuItem.WRITE_A_REVIEW));
        listOfMenuItems.add(new MenuItemData("PREFERENCES", MenuItemData.MenuItem.PREFERENCES));
        listOfMenuItems.add(new MenuItemData("GET EMAIL RECEIPT", MenuItemData.MenuItem.GET_EMAIL_RECEIPT));
        listOfMenuItems.add(new MenuItemData("GO TO MAP", MenuItemData.MenuItem.GO_TO_MAP));


        /*listOfMenuItems.add(new MenuItemData("VIEW RESERVATION", VIEW_RESERVATION));
        listOfMenuItems.add(new MenuItemData("MODIFY RESERVATION", MODIFY_RESERVATION));

        listOfMenuItems.add(new MenuItemData("VIEW PAST REVIEWS", VIEW_PAST_REVIEWS));
        listOfMenuItems.add(new MenuItemData("UPDATE CREDIT CARD", UPDATE_CREDIT_CARD));*/

        menuListView.setAdapter(new MenuAdapter(this, listOfMenuItems));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // MenuItemData.MenuItem menuItem = MenuItemData.MenuItem.values()[(int)view.getTag()];
                switch ((MenuItemData.MenuItem)view.getTag()) {
                    case GET_A_QUOTE_OR_MAKE_RESERVATION:
                        if(isQuoteListEmpty()) {
                            openNewTripActivity();
                        } else {
                            openQuotesListActivity();
                        }
                        break;
                    case ACTIVE_TRIPS:
                        openMyRidesList(SettingActivity.ReservationListMode.ACTIVE);
                        break;
                    case HISTORICAL_TRIPS:
                        openMyRidesList(SettingActivity.ReservationListMode.HISTORICAL);
                        break;
                    /*case VIEW_RESERVATION:
                        openMyRidesList(SettingActivity.ReservationListMode.ALL);
                        break;
                    case MODIFY_RESERVATION:
                        openMyRidesList(SettingActivity.ReservationListMode.CAN_MODIFY);
                        break;*/
                    case WRITE_A_REVIEW:
                        openMyRidesList(SettingActivity.ReservationListMode.WRITE_REVIEW);
                        break;
                    /*case VIEW_PAST_REVIEWS:
                        openMyRidesList(SettingActivity.ReservationListMode.HAS_REVIEW);
                        break;
                    case UPDATE_CREDIT_CARD:
                        openSettingsActivity();
                        break;*/
                    case PREFERENCES:
                        openSettingsActivity();
                        break;
                    case GET_EMAIL_RECEIPT:
                        openMyRidesList(SettingActivity.ReservationListMode.GET_EMAIL_RECEIPT);
                }
                mainMenuDrawerLayout.closeDrawers();
            }
        });
    }



    public void setupReceiverToGetPushNotificationFromBackend() {
        backendPushNotificationReceiver = new BackendPushNotificationReceiver(new IBackendPushNotificationListener() {
            @Override
            public void onReceiveMsg(int code, String data) {
                dismissWaitingDialog();

                switch (code) {
                    case BackendPushNotificationReceiver.ACCEPT_DRIVER_DIALOG:
                        showConfirmDialog();
                        //requestGetPickup(Const.RESRAVTION_ID, Const.DRIVER_TOKEN);
                        startMonitoringDriverPosition();
                        break;
                    case BackendPushNotificationReceiver.DECLINE_DRIVER_DIALOG:
                        showDeclineDialog();
                        break;
                    case BackendPushNotificationReceiver.ARRIVE_DRIVER_DIALOG:
                        showArrivedDialog(data);
                        break;
                    case BackendPushNotificationReceiver.DISMISS_WAITING_DRIVER_DIALOG:
                        dismissWaitingDialog();
                        break;
                    case BackendPushNotificationReceiver.NOT_DRIVER_RESPONSE:
                        showCancelPickUpDialog(data);
                        break;
                    case BackendPushNotificationReceiver.SHOW_WAITING_DRIVER_DIALOG:

                        Pref.setValue(MainActivity.this, Const.IS_SHOW_WAITING_DLG_FOR_DRIVER, Calendar.getInstance().getTimeInMillis()+"");
                        showProgressDialog();
                        break;
                    default:
                        break;
                }
            }
        });

        registerReceiver(backendPushNotificationReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
        registerReceiver(backendPushNotificationReceiver, new IntentFilter(CommonUtilities.SENDPICKUP_ACTION));
        registerReceiver(backendPushNotificationReceiver, new IntentFilter(CommonUtilities.PROGRESSDIALOGDISMISS_ACTION));

    }

    public void setupGoogleMap() {
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = mySupportMapFragment.getMap();

        if (map != null) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(true);

            // replace of the zoom control to the left,top of the screen
            View zoomControls = mySupportMapFragment.getView().findViewById(0x1);

            if (zoomControls != null && zoomControls.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                // ZoomControl is inside of RelativeLayout
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControls.getLayoutParams();

                // Align it to - parent top|left
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                // Update margins, set to 10dp
                final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                params.setMargins(margin, 70, margin, margin);


                //LocationManagerHelper.getInstance(this).sendRequestToGetLocation();

                map.setOnMarkerClickListener(this);
            } else {
                Toast.makeText(
                        this,
                        Utils.getStringById(R.string.routeactivity_error_load_google_map),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void updatedNewLocation(Location location) {
        if (location != null) {
            Const.currLat = location.getLatitude();
            Const.currLong = location.getLongitude();

            Log.d(TAG, "New Location: " + location.getLatitude() + ":" + location.getLongitude());

            StorageDataHelper.getInstance(getApplicationContext()).setLatestLocation(location);
            updateLocationViews(location);
        }
    }

    GoogleApiClient googleApiClient;
    public void setupLocationObject() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }


    LocationRequest locationRequest;
    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // milliseconds
        locationRequest.setFastestInterval(10000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 5 meters so lets quit this service,
            // onDestroy will be called and stop our location updates
            Utils.GPSReception reception = Utils.getGPSReception(location.getAccuracy());
            if(reception  == Utils.GPSReception.GOOD || reception == Utils.GPSReception.OK) {
                updatedNewLocation(location);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    public void setupReceiverToGetUpdatedLocation() {
        locationManagerUpdateLocationReceiver = new LocationManagerUpdateLocationReceiver(this);
        registerReceiver(locationManagerUpdateLocationReceiver, new IntentFilter(LocationManagerUpdateLocationReceiver.FILTER_INTENT_VALUE));
    }

    @OnClick({R.id.main_bt_my_ride, R.id.myLocation})
    public void showMyRide() {
        gotoPosition(StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation(), map);
        detectActivePickups(StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger());
    }

    @OnClick(R.id.menuBtn)
    public void openMenu() {
        mainMenuDrawerLayout.openDrawer(Gravity.START);
    }

    // These are hidden buttons
    //@OnClick(R.id.main_bt_setting)
    public void openCreditCardActivity() {
        startActivity(new Intent(MainActivity.this, SetCreditCardDataDialog.class));
        //mainMenuDrawerLayout.openDrawer(Gravity.START);
    }

    //@OnClick(R.id.main_bt_myride_detail)
    public void showMyRideDetailActivity() {
        Intent intent = new Intent(this, SettingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_MY_TRIPS_TAB)
                .putExtra(SettingActivity.EXTRA_INTENT_ONLYDETAIL, false);

        startActivity(intent);
    }

    //@OnClick(R.id.main_bt_myride_list)
    public void openMyRidesList(SettingActivity.ReservationListMode mode) {
        Intent intent = new Intent(this, SettingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_MY_TRIPS_LIST_TAB)
                .putExtra(SettingActivity.EXTRA_RESERVATION_LIST_MODE, mode);

        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_SETTING_TAB);

        startActivity(intent);
    }

    @OnClick({R.id.get_request_layout_btn, R.id.main_bt_Uni})
    public void openNewTripActivity() {
        stopTracking();
        Intent intent = new Intent(this, SettingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_NEW_TRIP_TAB);
        startActivity(intent);
    }

    @OnClick({R.id.get_quotes_layout_btn, R.id.main_bt_quotes})
    public void openQuotesListActivity() {
        stopTracking();
        Intent intent = new Intent(this, SettingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_QUOTES_TAB);

        startActivity(intent);
    }

    public void getCompanyLinkData() {
        if(StorageDataHelper.getInstance(this).getCompanyLinkData() == null) {
            if(!isNetworkAvailable())
                return;

            NetworkApi api = (new NetworkService()).getApi();
            final SetCompanyLinkRequest request = new SetCompanyLinkRequest();
            request.UniqueID = Utils.getDeviceId(getApplicationContext());
            request.VipCode = StorageDataHelper.getInstance(this).getVipCode();
            request.PhoneToken = StorageDataHelper.getInstance(getApplicationContext()).getPhoneToken();


            api.setCompanyLink(request)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<JsonServerResponse<SetCompanyLinkData>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(JsonServerResponse<SetCompanyLinkData> setCompanyLinkDataJsonServerResponse) {
                            if (setCompanyLinkDataJsonServerResponse.IsSuccess) {
                                StorageDataHelper.getInstance(getApplicationContext()).setCompanyLinkData(setCompanyLinkDataJsonServerResponse.Content);
                            }
                        }
                    });



        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!LocationManagerHelper.getInstance(getApplicationContext()).isAvailableGPSData()) {
            if(!isClickedGPSCancelBtn) {
                showAlertToEnableGPS();
            }
        }

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("DRV_LOC", 0);

        boolean isArrived = prefs.getBoolean("arrived", true);
        if (!isArrived) {
            String token = prefs.getString("token", "");
            if (!token.equals("")) {
                DriverTracker driverTracker = DriverTracker.getInstance(getApplicationContext());
                driverTracker.stopTracking();
                driverTracker.setCallback(new DriverTrackerCallback() {
                    @Override
                    public void driverArrived(final String licenseNo, final String color, final String style) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message = String.format("Your %s %s ", color, style);
                                if (licenseNo != null && !licenseNo.equals(""))
                                    message += String.format("license plate No %s ", licenseNo);
                                message += "has arrived";

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Vehicle arrived");
                                builder.setMessage(message);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                DriverTracker.getInstance(getApplicationContext()).stopTracking();
                                vehicleMarker.remove();
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void updateDriverCoordinates(float lat, float lon) {
                        final float _lat = lat;
                        final float _lon = lon;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (vehicleMarker == null) {
                                    vehicleMarker = map.addMarker(new MarkerOptions().position(new LatLng(_lat, _lon)).title("Your vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.car00)));
                                } else {
                                    vehicleMarker.setPosition(new LatLng(_lat, _lon));
                                }
                            }
                        });
                    }
                });
                driverTracker.startTracking(token);
            }
        }

        showActiveDriverMarkers(map, activeDriverData);
        gotoPosition(StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation(), map);
        detectActivePickups(StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger());

    }

    private boolean isClickedGPSCancelBtn = false;
    private Dialog gpsAletDialog = null;
    public void showAlertToEnableGPS() {
        if(gpsAletDialog != null && gpsAletDialog.isShowing())
            return;

        gpsAletDialog = AlertDailogView.showAlert(this,
                getResources().getString(R.string.common_gps_ask_to_enable_msg),
                getResources().getString(R.string.common_OK), true,
                getResources().getString(R.string.common_cancel), new AlertDailogView.OnCustPopUpDialogButoonClickListener() {
                    @Override
                    public void OnButtonClick(int tag, int buttonIndex) {
                        if(buttonIndex == AlertDailogView.BUTTON_OK) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        } else {
                            isClickedGPSCancelBtn = true;
                        }
                        try {
                            gpsAletDialog.dismiss();
                            gpsAletDialog = null;
                        }catch (Exception e) {

                        }
                    }
                }, 0);

        gpsAletDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        checkBottomPanelBtnStatus();
    }

    @Override
    protected void onPause() {
        stopTracking();
        detectActivePickups(StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger());
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
                        if (pickUpDataJsonServerResponse.IsSuccess) {
                            StorageDataHelper.getInstance(getApplicationContext()).setDriverPickUpsForPassanger(pickUpDataJsonServerResponse.Content);
                            processOfPickupForPassenger();
                        }
                    }
                });
    }

    public void processOfPickupForPassenger() {

        detectPendingPickups(StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger());
        detectActivePickups(StorageDataHelper.getInstance(getBaseContext()).getDriverPickUpsForPassanger());
    }
    public void detectPendingPickups(List<PickUpReservationData> data) {

        PickUpReservationData pickUpData = new PickUpReservationData();
        boolean isWaitingResponse = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).ReservationStatus.equalsIgnoreCase("Pending")) {
                pickUpData = data.get(i);
                isWaitingResponse = true;
            }
        }

        if(isWaitingResponse) {
            PendingPickupData pendingPickupData = new PendingPickupData();
            pendingPickupData.driverToken = pickUpData.DriverToken;
            pendingPickupData.reservationId = pickUpData.ReservationID;
            pendingPickupData.tripNumber = String.valueOf(pickUpData.TripNumber);
            pendingPickupData.createdDate = Calendar.getInstance().getTimeInMillis();


            StorageDataHelper.getInstance(this).setPendingPickupStatus(pendingPickupData);
        } else {
            StorageDataHelper.getInstance(this).setPendingPickupStatus(null);
        }
        updateWaitingDialog();
    }

    private List<GetReservationLocationData> activeDriverData;
    public void detectActivePickups(List<PickUpReservationData> data) {
        if(data == null)
            return;

        activeDriverData = new ArrayList<GetReservationLocationData>();

        final List<Integer> reservationsIds = new ArrayList<Integer>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).ReservationStatus.equalsIgnoreCase("DriverOnWay")) {
                reservationsIds.add(data.get(i).ReservationID );
            }
            if (data.get(i).ReservationStatus.equalsIgnoreCase("Active")) {
                reservationsIds.add(data.get(i).ReservationID);
            }
        }

        NetworkApi api = (new NetworkService()).getApi();

        GetReservationLocationRequest request = new GetReservationLocationRequest();
        request.ReservationsID = reservationsIds;
        api.getReservationsLocation(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<GetReservationLocationData>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonServerResponse<List<GetReservationLocationData>> getReservationLocationDataJsonServerResponse) {
                        if(getReservationLocationDataJsonServerResponse.IsSuccess) {
                            activeDriverData = getReservationLocationDataJsonServerResponse.Content;
                            startMonitoringDriverPosition();
                            showActiveDriverMarkers(map, activeDriverData);
                            if (isQuoteListEmpty()) {
                                showActiveDriverMarkers(map, activeDriverData);
                                //stopTracking();
                                //gotoPosition(LocationManagerHelper.getInstance(getBaseContext()).getLatestLocation(), map);
                            }
                        }
                    }
                });
    }

    Map<Integer, Boolean> listOfArrivedPickupWhichWasNotify = new HashMap<Integer, Boolean>();
    private void showActiveDriverMarkers(GoogleMap map, List<GetReservationLocationData> activeDriverData) {
        try {
            map.clear();

            if(listOfArrivedPickupWhichWasNotify == null)
                listOfArrivedPickupWhichWasNotify = new HashMap<Integer, Boolean>();

//            Location location = LocationManagerHelper.getInstance(this).getLatestLocation();
            Location location = StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation();
            LatLng passengerLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            //Removed flag marker
            //map.addMarker(new MarkerOptions().position(passengerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.userflag)));

            if(activeDriverData == null)
                return;

            for (int i = 0; i < activeDriverData.size(); i++) {

                GetReservationLocationData activeDriverDataItem = activeDriverData.get(i);
                LatLng driverLatLng = new LatLng(Double.parseDouble(activeDriverDataItem.Location.latitude), Double.parseDouble(activeDriverDataItem.Location.longitude));

                //Removed old vehicle's marker
                /*map.addMarker(new MarkerOptions()
                        .position( driverLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(Utils.getCarImage(getApplicationContext(), activeDriverDataItem.VehicleDetails.Style, activeDriverDataItem.VehicleDetails.Color)))
                        .snippet(i + ""));*/

                DriverData data = new DriverData();

                map.addMarker(new MarkerOptions().position(driverLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.car00)));

                if (Utils.CalculationByDistance(passengerLatLng, driverLatLng) <= 50 && listOfArrivedPickupWhichWasNotify.get(activeDriverDataItem.reservation_id) == null) {
                    //showArrivedDialog("Your "+activeDriverDataItem.VehicleDetails.Style+" has arrived");
                    listOfArrivedPickupWhichWasNotify.put(activeDriverDataItem.reservation_id, true);
                    //stopMonitoringDriverPosition();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void showDriverMarkers() {
        try {
            map.clear();
            if(Const.driverData == null || Const.driverData.Content == null) {
                return;
            }
            for (int i = 0; i < Const.driverData.Content.size(); i++) {
                searchDriversData = Const.driverData.Content.get(i);
                map.addMarker(new MarkerOptions()
                        .position( new LatLng(Double.parseDouble(searchDriversData.Location.latitude), searchDriversData.Location.longitude))
                        .icon(BitmapDescriptorFactory.fromResource(Utils.getCarImage(getApplicationContext(), searchDriversData.VehicleShape.Vstyle, searchDriversData.VehicleShape.Vcolor)))
                        .snippet(i + ""));
            }

            //gotoPickUpPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public static String TAG = "MainActivity";

    public void gotoPosition(Location location, GoogleMap map) {
        if(location != null && map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }
    }

    public void gotoPickUpPosition() {
        if (Const.CURRENT != null && map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Const.srcLat, Const.srcLong), 15));
        }

    }


    @SuppressLint("HandlerLeak")
    Handler updateLocationName = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1 && msg.arg1 == 0) {
                try {
                    boolean isEmptyLocationName = false;
                    if (location_name.getText().toString().equalsIgnoreCase("")) {
                        isEmptyLocationName = true;
                    }
                    if ((String) msg.obj != null) {
                        location_name.setText((String) msg.obj);
                        Const.currentLocationName = (String) msg.obj;
                    } else {
                        location_name.setText(Utils.getStringById(R.string.mainactivity_undefinied));
                    }

                    if(isEmptyLocationName) {
                        gotoPosition(StorageDataHelper.getInstance(getApplicationContext()).getLatestLocation(), map);
                    }
                } catch (Exception e) {

                }
            }
        }
    };

    Map<Location, String> cacheLocations = new HashMap<Location, String>();
    public void updateLocationViews(final Location location) {
        if(location != null) {

            new Thread() {
                public void run() {
                    String locationName = null;
                    Boolean isFounded = false;
                    for (Map.Entry<Location, String> entry : cacheLocations.entrySet()) {
                        if(entry.getKey().getLongitude() == location.getLongitude() && entry.getKey().getLatitude() == location.getLatitude()) {
                            locationName = entry.getValue();
                            isFounded = true;
                        }
                    }
                    if(!isFounded) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                        if (location != null && geocoder != null) {
                            try {
                                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (list != null & list.size() > 0) {
                                    Address address = list.get(0);

                                    locationName = address.getSubThoroughfare() + ","
                                            + address.getThoroughfare() + ","
                                            + address.getLocality() + ","
                                            + address.getAdminArea() + ","
                                            + address.getCountryName();

                                    //cacheLocations.put(location, locationName);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = 0;
                        msg.obj = locationName;
                        updateLocationName.sendMessage(msg);

                }
            }.start();
//            new AsyncTask<Void, Void, String>(){
//                @Override
//                protected String doInBackground(Void... params) {
//                    return LocationManagerHelper.getInstance(getBaseContext()).getLatestLocationName();
//                }
//
//                @Override
//                protected void onPostExecute(String s) {
//                    super.onPostExecute(s);
//                    Log.d(TAG, "Location name: "+s);
//                    if(s != null) {
//                        location_name.setText(s);
//                    } else {
//                        location_name.setText(Utils.getStringById(R.string.mainactivity_undefinied));
//                    }
//                }
//            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            Utils.GPSReception reception = Utils.getGPSReception(location.getAccuracy());
            int res = Utils.toArrowImageResource(reception);
            myLocation.setImageResource(res);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // servicemanager.stop();
        try {
            unregisterReceiver(backendPushNotificationReceiver);
            unregisterReceiver(locationManagerUpdateLocationReceiver);
            //LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }


        stopLocationUpdates();

        ButterKnife.unbind(this);

        StorageDataHelper.getInstance(this).setDriversDatasNow(null);
        StorageDataHelper.getInstance(this).setDriversDatasLater(null);
        StorageDataHelper.getInstance(this).setSearchDriversTempDataForRequest(null);
        StorageDataHelper.getInstance(this).setDriverPickUpsForPassanger(null);

        DriverTracker.getInstance(getApplicationContext()).stopTracking();
    }

    private void stopTracking()
    {
        stopMonitoringDriverPosition();
        // servicemanager.start(MainActivity.this);
        //rlHeader.setVisibility(View.VISIBLE);
        //locMesge.setVisibility(View.GONE);
    }

    private boolean isQuoteListEmpty() {
        List<SearchDriversData> driverDataNow = StorageDataHelper.getInstance(this).getDriversDatasNow();
        List<SearchDriversLaterData> driverDataLater = StorageDataHelper.getInstance(this).getDriversDatasLater();
        if((driverDataNow == null || driverDataNow.size() == 0) && (driverDataLater == null || driverDataLater.size() == 0)) {
            return true;
        }

        return false;
    }

    private void checkBottomPanelBtnStatus() {
        // check if quotes list is not empty
        if (!isQuoteListEmpty()) {
            getRequestBtn.setVisibility(View.INVISIBLE);
            getQuotesBtn.setVisibility(View.VISIBLE);
        } else {
            getRequestBtn.setVisibility(View.VISIBLE);
            getQuotesBtn.setVisibility(View.INVISIBLE);
        }
    }

    /*@SuppressLint("HandlerLeak")
    Handler onCurrentDriverUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1 && msg.arg1 == 0) {
                CommonUtilities.pickups = (List<PickUpData>)msg.obj;
                activeDriverData = (List<GetReservationsLocationData>)msg.obj;
                showActiveDriverMarkers(map, activeDriverData);
                mCurrentDriverUpdateRepeater.postDelayed(mCurrentDriverUpdateRunnable, 10000);
           }
        }
    };*/

    Handler mCurrentDriverUpdateRepeater = new Handler();
    Runnable mCurrentDriverUpdateRunnable;

    MonitoringDriverPosition monitoringDriverPosition = null;
    private Timer mTimerToGetDriverPosition = null;
//    public void startMonitoringDriverPosition() {
//        PendingPickupData pendingPickupData = StorageDataHelper.getInstance(getApplicationContext()).getPendingPickupStatus();
//        final List<String> reservationIds = new ArrayList<String>();
//        reservationIds.add(pendingPickupData.reservationId+"");
//
//        mTimerToGetDriverPosition = new Timer();
//        mTimerToGetDriverPosition.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                new Thread() {
//                    public void run() {
//                        JsonResponse<List<GetReservationsLocationData>> driverData = driverViewModel.GetReservationsLocation(reservationIds);
//
//                        if (driverData.IsSuccess) {
//                            Message msg = new Message();
//                            msg.what = 1;
//                            msg.arg1 = 0;
//                            msg.obj = driverData.Content;
//                            onCurrentDriverUpdateHandler.sendMessage(msg);
//                        }
//                    }
//                }.start();
//            }
//        }, 10000, 10000);
//
//
//
////        if(monitoringDriverPosition != null)
////            stopMonitoringDriverPosition();
////
////        monitoringDriverPosition = new MonitoringDriverPosition(reservationIds, 10);
////        monitoringDriverPosition.setDelegate(new ICurrentDriverPositionResult() {
////            @Override
////            public void resultForDriverPosition(int reservationId, GetReservationsLocationData data) {
////                for(int i =0; i < activeDriverData.size(); i++) {
////                    if(activeDriverData.get(i).reservation_id == reservationId) {
////                        activeDriverData.set(i, data);
////                    }
////                }
////
////                showActiveDriverMarkers(map, activeDriverData);
////            }
////        });
////        monitoringDriverPosition.runMonitorig();
//    }

    public void startMonitoringDriverPosition() {
//        PendingPickupData pendingPickupData = StorageDataHelper.getInstance(getApplicationContext()).getPendingPickupStatus();
//        final List<String> reservationIds = new ArrayList<String>();
//        reservationIds.add(pendingPickupData.reservationId+"");
//
//
//        mCurrentDriverUpdateRunnable =new Runnable() {
//            @Override
//            public void run() {
//               final JsonResponse<List<GetReservationsLocationData>> driverData = driverViewModel.GetReservationsLocation(reservationIds);
//
//                //if (driverData.IsSuccess) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //CommonUtilities.pickups = driverData.Content;
//                            if (driverData.IsSuccess) {
//                                activeDriverData = driverData.Content;
//                                showActiveDriverMarkers(map, activeDriverData);
//                            }
//                            //mCurrentDriverUpdateRepeater.postDelayed(mCurrentDriverUpdateRunnable, 10000);
//                        }
//                    });
////                    Message msg = new Message();
////                    msg.what = 1;
////                    msg.arg1 = 0;
////                    msg.obj = driverData.Content;
////                    onCurrentDriverUpdateHandler.sendMessage(msg);
//                //}
//            }
//        };
//
//        //mCurrentDriverUpdateRepeater.postDelayed(mCurrentDriverUpdateRunnable, 10000);
    }

    public void stopMonitoringDriverPosition() {
//        if(monitoringDriverPosition != null) {
//            monitoringDriverPosition.stopMonitoring();
//        }
//        if(mTimerToGetDriverPosition != null)
//            mTimerToGetDriverPosition.cancel();
        if(mCurrentDriverUpdateRunnable!=null && mCurrentDriverUpdateRepeater !=null)
            mCurrentDriverUpdateRepeater.removeCallbacks(mCurrentDriverUpdateRunnable);
    }

    public void updateWaitingDialog() {
        PendingPickupData pendingPickupData = StorageDataHelper.getInstance(this).getPendingPickupStatus();
        if(pendingPickupData != null) {
            if(Calendar.getInstance().getTimeInMillis()-pendingPickupData.createdDate < Const.DELAY_FOR_WAITING_DRIVER_RESPONSE ) {
                    showProgressDialog();
            }
        } else {
            dismissWaitingDialog();
        }
    }

    public void showProgressDialog() {
        Logger.writeFull("Show Waiting Dialog");
        if (!(dlg != null && dlg.isShowing())) {
            dlg = new WaitingDriverDialog(this);
            dlg.setDelegate(new IInformationDialogsActions() {
                @Override
                public void closeDialog() {
                    Pref.setValue(MainActivity.this, Const.IS_SHOW_WAITING_DLG_FOR_DRIVER, false + "");
                    PendingPickupData data = StorageDataHelper.getInstance(getApplicationContext()).getPendingPickupStatus();

                    Intent intent = new Intent(MainActivity.this, CancelActivity.class)
                            .putExtra(Const.RESERVATION_ID_TO_CANCEL_DRIVER, data.reservationId)
                            .putExtra(Const.CANCEL_TO_DRIVER_IMMEDIATELY, false);

                    startActivity(intent);
                }

                @Override
                public void acceptPickUp() {

                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();
        }
    }

    public void showArrivedDialog(String reason) {
        Logger.writeFull("Show Arrived Dialog");
        ArrivedDriverDialog arrivedDriverDialog = new ArrivedDriverDialog(this);
        arrivedDriverDialog.setText(reason);
        arrivedDriverDialog.show();
    }

    public void showCancelPickUpDialog(String reason) {
        Logger.writeFull("Show Cancel Dialog");
        CancelPickUpDialog cancelPickUpDialog = new CancelPickUpDialog(this);
        cancelPickUpDialog.setText(reason);
        cancelPickUpDialog.show();
    }

    public void showConfirmDialog() {
        Logger.writeFull("Show Accept Dialog");
        final Context context = this;
        PendingPickupData pendingPickupData = StorageDataHelper.getInstance(context).getPendingPickupStatus();
        if (pendingPickupData != null) {
            DriverTracker driverTracker = DriverTracker.getInstance(getApplicationContext());
            driverTracker.stopTracking();
            driverTracker.setCallback(new DriverTrackerCallback() {
                @Override
                public void driverArrived(final String licenseNo, final String color, final String style) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String message = String.format("Your %s %s ", color, style);
                            if (licenseNo != null && !licenseNo.equals(""))
                                message += String.format("license plate No %s ", licenseNo);
                            message += "has arrived";

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Vehicle arrived");
                            builder.setMessage(message);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            DriverTracker.getInstance(getApplicationContext()).stopTracking();
                            vehicleMarker.remove();
                            dialog.show();
                        }
                    });
                }

                @Override
                public void updateDriverCoordinates(float lat, float lon) {
                    final float _lat = lat;
                    final float _lon = lon;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (vehicleMarker == null) {
                                vehicleMarker = map.addMarker(new MarkerOptions().position(new LatLng(_lat, _lon)).title("Your vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.car00)));
                            } else {
                                vehicleMarker.setPosition(new LatLng(_lat, _lon));
                            }
                        }
                    });
                }
            });
            driverTracker.startTracking(pendingPickupData.driverToken);

        }
        ConfirmPickUpDialog confirmPickUpDialog = new ConfirmPickUpDialog(this);
        confirmPickUpDialog.setDelegate(new IInformationDialogsActions() {
            @Override
            public void closeDialog() {

            }

            @Override
            public void acceptPickUp() {
                startActivity(new Intent(MainActivity.this, SettingActivity.class).putExtra("flagFrom", 0));
            }
        });
        confirmPickUpDialog.show();
    }

    public void showDeclineDialog() {
        Logger.writeFull("Show Decline Dialog");
        DeclinedPickUpDialog declinedPickUpDialog = new DeclinedPickUpDialog(this);
        declinedPickUpDialog.show();
    }

    private void dismissWaitingDialog() {
        Logger.writeFull("Dismissed Waiting dialog: " + (dlg == null));

        if (dlg != null && dlg.isShowing())
            dlg.dismiss();
    }

   /* public void requestGetPickup(final String reservationId, final String token) {
        NetworkApi api = (new NetworkService()).getApi();

        PickUpRequest request = new PickUpRequest();
        request.ReservationID = reservationId;

        api.getPickup(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<PickUpData>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonServerResponse<List<PickUpData>> pickUpDataJsonServerResponse) {
                        if(pickUpDataJsonServerResponse.IsSuccess) {
                            CommonUtilities.pickups = pickUpDataJsonServerResponse.Content;
                        }
                    }
                });
    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet() != null) {
            try {
                Pref.setValue(MainActivity.this, Const.DRIVER_ID,
                        marker.getSnippet());
                if (StorageDataHelper.getInstance(this).getDriversDatasNow() == null)
                    return false;

                searchDriversData = StorageDataHelper.getInstance(this).getDriversDatasNow().get(Integer.parseInt(marker.getSnippet()));

                Bundle bundle = new Bundle();
                bundle.putSerializable("searchDriverData", searchDriversData);
                //Intent intent = new Intent(MainActivity.this, PicUpTaxiDialog.class);
                //intent.putExtras(bundle);
                //startActivity(intent);
            } catch (Exception e) {

            }
        }

        return false;
    }



}
