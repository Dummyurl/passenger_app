package com.passengerapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.AirlineData;
import com.passengerapp.main.network.model.request.GetFlightsInfoFromFVRequest;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 8/10/15.
 */
public class FlightActivity extends PassengerBaseActivity implements
        View.OnFocusChangeListener {
    private final int FLIGHT_LIST_ACTIVITY_START = 0;
    private Boolean isFromRegisterPassenger;
    private SearchDriversData searchdriverdata;
    private SaveFlightDetailRequest flightData;
    private String updateFlightDataDriverClass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight);
        viewModel = new DriverViewModel();

        isFromRegisterPassenger = getIntent().getBooleanExtra(Const.IS_ADDIDNG_FLIGHT_INFO_REGISTER_PASS, false);
        searchdriverdata = (SearchDriversData)getIntent().getSerializableExtra(Const.SEARCH_DRIVER_DATA);
        flightData = (SaveFlightDetailRequest)getIntent().getSerializableExtra(Const.MODIFY_FLIGHT_DETAIL);
        initUI();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(airportList == null || airportList.isEmpty()) {
            requestGetAirports("");
        }
        if(airlineList == null || airlineList.isEmpty()) {
            requestGetAirLines("");
        }
    }

    TextView cancelFlightBtn;
    AutoCompleteTextView departureAirport;
    AutoCompleteTextView arrivalAirport;
    AutoCompleteTextView airlineATextView;
    EditText flightNumberATextView;
    TextView departureAirportClearBtn;
    TextView arrivalAirportClearBtn;
    TextView airlineATextViewClearBtn;
    TextView flightNumberATextViewClearBtn;
    TextView dateTextView;

    TextView cancelBtn;
    TextView searchBtn;
    TextView doneBtn;

    TextView verifyBtn;
    TextView sendBtn;
    TextView quotesToListBtn;

    private DriverViewModel viewModel;
    private List<AirlineData> airportList;
    private List<AirlineData> airlineList;
    private ArrayList<String> airportNameSearch = new ArrayList<String>();
    private ArrayList<String> airportCodeSearch = new ArrayList<String>();
    private ArrayList<String> airLineNameSearch = new ArrayList<String>();
    private ArrayList<String> airLineCodeSearch = new ArrayList<String>();
    private String airlineCode=null;
    private String arrivalAirPrtCode=null;
    private String departAirPrtCode=null;
    private ArrayList<String> strAirportData;
    private ArrayList<String> strAirportCodeData;
    private ArrayList<String> strAirlineData;
    private ArrayList<String> strAirlineCodeData;
    private GetFlightsInfoFromFVRequest flightInforequest;
   // private JsonResponse<List<GetFlightsInfoFromFVData>> flightInfo;
    private ArrayAdapter<String> adapter;

    public void initUI() {
        cancelFlightBtn = (TextView) findViewById(R.id.cancel_flight_btn);
        cancelFlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(isFromRegisterPassenger) {
                    Intent intent = new Intent(FlightActivity.this, RegisterPassengerActivity.class);
                    intent.putExtra(Const.IS_CANCEL_FLIGHT_DATA_EXTRA_ID, true);
                    startActivity(intent);
                }*/
                cancelFlight();
            }
        });



        departureAirport = (AutoCompleteTextView)findViewById(R.id.autocomplete_departure_airport);
        departureAirport.setOnFocusChangeListener(this);
        departureAirport.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (departureAirport.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.
                    return;
                }
                refreshAirportList(true);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        departureAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (airportList != null && airportList.size() > 0) {
                    if (airportCodeSearch.isEmpty()) {
                        departAirPrtCode = airportList.get(Utils
                                .getIndexFromList(strAirportData, arg0
                                        .getItemAtPosition(arg2).toString())).code;
                        Const.departAirportName = arg0.getItemAtPosition(arg2)
                                .toString();
                        Const.departAirportCode = departAirPrtCode;
                    } else {
                        departAirPrtCode = airportList.get(Utils
                                .getIndexFromList(strAirportData,
                                        airportCodeSearch.get(arg2))).code;
                        Const.departAirportName = airportCodeSearch.get(arg2);
                        Const.departAirportCode = departAirPrtCode;
                    }
                }
            }
        });
        arrivalAirport = (AutoCompleteTextView)findViewById(R.id.autocomplete_arrival_airport);
        arrivalAirport.setOnFocusChangeListener(this);
        arrivalAirport.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                if (arrivalAirport.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.

                    return;
                }
                refreshAirportList(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        arrivalAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (airportList != null && airportList.size() > 0) {
                    if (airportCodeSearch.isEmpty()) {
                        arrivalAirPrtCode = airportList.get(Utils
                                .getIndexFromList(strAirportData, arg0
                                        .getItemAtPosition(arg2).toString())).code;
                        Const.ArrivalAirportName = arg0.getItemAtPosition(arg2)
                                .toString();
                        Const.ArrivalAirportCode = arrivalAirPrtCode;
                    } else {
                        arrivalAirPrtCode = airportList.get(Utils
                                .getIndexFromList(strAirportData,
                                        airportCodeSearch.get(arg2))).code;
                        Const.ArrivalAirportName = airportCodeSearch.get(arg2);
                        Const.ArrivalAirportCode = arrivalAirPrtCode;
                    }
                }
            }
        });
        airlineATextView = (AutoCompleteTextView)findViewById(R.id.autocomplete_airline);
        airlineATextView.setOnFocusChangeListener(this);
        airlineATextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (airlineATextView.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.

                    return;
                }
                refershAirlineList();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        airlineATextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (airlineList != null && airlineList.size() > 0) {

                    if (airLineCodeSearch.isEmpty()) {
                        airlineCode = airlineList.get(Utils
                                .getIndexFromList(strAirlineData, arg0
                                        .getItemAtPosition(arg2).toString())).iata_code;
                    } else {

                        airlineCode = airlineList.get(Utils
                                .getIndexFromList(strAirlineData,
                                        airLineCodeSearch.get(arg2))).iata_code;
                    }

                }
            }
        });
        flightNumberATextView = (EditText)findViewById(R.id.autocomplete_flight_number);

        View.OnClickListener clearBtnAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.autocomplete_departure_airport_clear) {
                    departureAirport.setText("");
                } else if(v.getId() == R.id.autocomplete_arrival_airport_clear) {
                    arrivalAirport.setText("");
                } else if(v.getId() == R.id.autocomplete_airline_clear) {
                    airlineATextView.setText("");
                } else if(v.getId() == R.id.autocomplete_flight_number_clear) {
                    flightNumberATextView.setText("");
                }
            }
        };

        departureAirportClearBtn = (TextView) findViewById(R.id.autocomplete_departure_airport_clear);
        departureAirportClearBtn.setOnClickListener(clearBtnAction);
        arrivalAirportClearBtn = (TextView) findViewById(R.id.autocomplete_arrival_airport_clear);
        arrivalAirportClearBtn.setOnClickListener(clearBtnAction);
        airlineATextViewClearBtn = (TextView) findViewById(R.id.autocomplete_airline_clear);
        airlineATextViewClearBtn.setOnClickListener(clearBtnAction);
        flightNumberATextViewClearBtn = (TextView) findViewById(R.id.autocomplete_flight_number_clear);
        flightNumberATextViewClearBtn.setOnClickListener(clearBtnAction);

        dateTextView = (TextView) findViewById(R.id.date_textview);






        View.OnClickListener actionCancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
                Intent intent = new Intent(FlightActivity.this, SettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(SettingActivity.EXTRA_INTENT_FLAGFROM, SettingActivity.SETTINGS_QUOTES_TAB);
                startActivity(intent);

            }
        };

        View.OnClickListener actionSearchDone = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValidationData()) {
                    requestGetFlightInfo();
                }
            }
        };
        cancelBtn = (TextView) findViewById(R.id.cancel_btn_textview);
        cancelBtn.setOnClickListener(actionCancel);
        searchBtn = (TextView) findViewById(R.id.search_btn_textview);
        searchBtn.setOnClickListener(actionSearchDone);
        doneBtn = (TextView) findViewById(R.id.done_btn_textview);
        doneBtn.setOnClickListener(actionSearchDone);

        quotesToListBtn = (TextView) findViewById(R.id.to_quotes_list_btn_txtview);
        quotesToListBtn.setOnClickListener(actionCancel);
        verifyBtn = (TextView) findViewById(R.id.verify_btn_textview);
        verifyBtn.setOnClickListener(actionSearchDone);
        sendBtn = (TextView) findViewById(R.id.send_btn_textview);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidationData()) {
                    if (flightNumberATextView.getText().toString().trim().isEmpty()) {
                        Utils.showWarningMsg(FlightActivity.this, getResources().getString(R.string.activity_flight_provide_flight_number));
                    } else {

                        // execute saveFlightDetail
                        flightInforequest = getPrepearedFlightInfoRequest();
                        Intent intent = new Intent(FlightActivity.this, FlightSpecialInstructions.class);
                        intent.putExtra("flagFromRoute", true);
                        intent.putExtras(getIntent().getExtras());
                        intent.putExtra(Const.SEND_FLIGHT_DATA_WITHOUT_REQUEST, flightInforequest);

                        startActivityForResult(intent, FLIGHT_LIST_ACTIVITY_START);

                    }
                }
            }
        });
    }

    public void updateUI() {
        if(searchdriverdata != null) {
            if (searchdriverdata.IsDeparture) {
                initDepartureAirportAtFirst(searchdriverdata.AirportCode, Const.DESTINATION);
                departureAirport.setText(Const.DESTINATION);
            } else {
                initArrivalAirportAtFirst(searchdriverdata.AirportCode, Const.CURRENT);
                arrivalAirport.setText(Const.CURRENT);
            }
            //dateTextView.setText(Utils.millisToDate(Long.parseLong(Const.timeOfPic) * 1000, "MM/dd/yy"));
            try {
                dateTextView.setText(Utils.millisToDate(Long.parseLong(Const.timeOfPicLastRequest) * 1000, "MM/dd/yy"));
            }catch (Exception e){}
        } else {
            initDepartureAirportAtFirst(flightData.departure.airport_code, "");
            departureAirport.setText(flightData.departure.airport_code);
            initArrivalAirportAtFirst(flightData.arrival.airport_code, Const.DESTINATION);
            arrivalAirport.setText(flightData.arrival.airport_code);
            initAirlineAtFirst(flightData.airline_code);
            airlineATextView.setText(flightData.airline_name);
            flightNumberATextView.setText(flightData.flight_number);

            sendBtn.setVisibility(View.GONE);
        }
    }

    public void initDepartureAirportAtFirst(String AirportCode, String AirportName) {
        departAirPrtCode = AirportCode;
        Const.departAirportName = AirportName;
        Const.departAirportCode = departAirPrtCode;
    }

    public void initArrivalAirportAtFirst(String AirportCode, String AirportName) {
        arrivalAirPrtCode = AirportCode;
        Const.ArrivalAirportName = AirportName;
        Const.ArrivalAirportCode = arrivalAirPrtCode;
    }

    public void initAirlineAtFirst(String AirlineCode) {
        airlineCode = AirlineCode;
    }

    public boolean isValidationData() {
        String errorText = "";
        if(flightNumberATextView.getText().toString().isEmpty()
                && (departureAirport.getText().toString().isEmpty() || arrivalAirport.getText().toString().isEmpty())) {
            errorText = getResources().getString(R.string.activity_flight_input_depature_and_arrival);
        } else if(!flightNumberATextView.getText().toString().isEmpty() && (departureAirport.getText().toString().isEmpty() && arrivalAirport.getText().toString().isEmpty()) ) {
            errorText = getResources().getString(R.string.activity_flight_input_depature_or_arrival);
        } else if(airlineATextView.getText().toString().isEmpty()) {
            errorText = getResources().getString(R.string.activity_flight_provide_airline);
        }

        if(errorText.isEmpty()) {
            return true;
        }

        Utils.showWarningMsg(FlightActivity.this, errorText);

        return false;
    }

    private void cancelFlight() {
        setResult(RESULT_OK);
        finish();
    }

    private void enableSearchBtn() {
        searchBtn.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FLIGHT_LIST_ACTIVITY_START && resultCode==RESULT_OK) {
            setResult(RESULT_OK,data);
            finish();
        }
        if(requestCode == FLIGHT_LIST_ACTIVITY_START && resultCode==RESULT_CANCELED) {
            enableSearchBtn();
        }
    }

    private GetFlightsInfoFromFVRequest getPrepearedFlightInfoRequest() {
        GetFlightsInfoFromFVRequest tmpFlightInforequest = new GetFlightsInfoFromFVRequest();
        if(flightNumberATextView.getText().toString().trim().isEmpty()) {
            tmpFlightInforequest.QueryByFlight = false;
        } else {
            tmpFlightInforequest.QueryByFlight = true;
            tmpFlightInforequest.FlightId = flightNumberATextView.getText().toString().trim();
        }
        tmpFlightInforequest.ArrivalAirport = arrivalAirPrtCode;
        tmpFlightInforequest.DepartureAirport = departAirPrtCode;
        tmpFlightInforequest.Airline = airlineCode;
        tmpFlightInforequest.DepartureDate = Const.timeOfPicLastRequest != null
                && !Const.timeOfPicLastRequest.equals("") ? Utils
                .millisToDate(Long.parseLong(Const.timeOfPicLastRequest)*1000,
                        "yyyyMMdd") : Utils.millisToDate(
                System.currentTimeMillis(), "yyyyMMdd");
        Const.date = tmpFlightInforequest.DepartureDate;

        return tmpFlightInforequest;
    }

    public void requestGetFlightInfo() {

        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();
        api.getFlightsInfoFromFV(getPrepearedFlightInfoRequest())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<SaveFlightDetailRequest>>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<SaveFlightDetailRequest>> saveFlightDetailRequestJsonServerResponse) {
                        if(saveFlightDetailRequestJsonServerResponse.IsSuccess) {
                            if(saveFlightDetailRequestJsonServerResponse.Content == null || saveFlightDetailRequestJsonServerResponse.Content.isEmpty()) {
                                Utils.showWarningMsg(FlightActivity.this, getResources().getString(R.string.activity_flight_no_results_msg_please_try_again));
                                return;
                            }

                            Intent intent = new Intent(FlightActivity.this, FlightListActivity.class)
                                    .putExtra("flagFromRoute", true)
                                    .putExtra(Const.FLIGHT_LIST_RACES, new ArrayList<SaveFlightDetailRequest>(saveFlightDetailRequestJsonServerResponse.Content))
                                    .putExtras(getIntent().getExtras());

                            startActivityForResult(intent, FLIGHT_LIST_ACTIVITY_START);
                        }
                    }
                });
    }

    public void requestGetAirports(final String strSearch) {
        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();

        api.getAirports(strSearch)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<AirlineData>>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<AirlineData>> airlineDataJsonServerResponse) {
                        if (airlineDataJsonServerResponse.IsSuccess) {
                            airportList = airlineDataJsonServerResponse.Content;
                            requestGetAirLines("");
                        }
                    }
                });
    }

    public void requestGetAirLines(final String strSearch) {
        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();

        api.getAirlines(strSearch)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<List<AirlineData>>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<List<AirlineData>> airlineDataJsonServerResponse) {
                        if (airlineDataJsonServerResponse.IsSuccess) {
                            airlineList = airlineDataJsonServerResponse.Content;
                            makeDataList();
                        }
                    }
                });
    }


    private void refreshAirportList(boolean isDepaAir) {

        if (strAirportData != null && strAirportData.size() > 0) {
            String strSearch = null;
            int len = 0;
            if (isDepaAir) {
                len = departureAirport.getText().length();
                strSearch = departureAirport.getText().toString();
            } else {
                len = arrivalAirport.getText().length();
                strSearch = arrivalAirport.getText().toString();
            }
            airportNameSearch.clear();
            airportCodeSearch.clear();
            for (int i = 0; i < strAirportData.size(); i++) {
                if (len <= strAirportData.get(i).length()) {
                    if (strSearch.equalsIgnoreCase((String) strAirportData.get(
                            i).subSequence(0, len))) {
                        airportNameSearch.add(strAirportData.get(i));
                    }
                }
            }

            if (airportNameSearch.isEmpty()) {

                for (int i = 0; i < strAirportCodeData.size(); i++) {

                    if (len <= strAirportCodeData.get(i).length()) {

                        if (strSearch
                                .equalsIgnoreCase((String) strAirportCodeData
                                        .get(i).subSequence(0, len))) {
                            String name = airportList.get(Utils
                                    .getIndexFromList(strAirportCodeData,
                                            strAirportCodeData.get(i))).name;

                            airportNameSearch.add(strAirportCodeData.get(i)
                                    + "/" + name);
                            airportCodeSearch.add(name);
                        }
                    }
                }

            }

            adapter = new ArrayAdapter<String>(FlightActivity.this,
                    R.layout.dropdown, airportNameSearch);

            if (isDepaAir) {
                departureAirport.setThreshold(1);
                departureAirport.setAdapter(adapter);
                departureAirport.showDropDown();
                if(airportNameSearch.isEmpty())
                    departAirPrtCode = null;
            } else {

                arrivalAirport.setThreshold(1);
                arrivalAirport.setAdapter(adapter);
                arrivalAirport.showDropDown();
                if(airportNameSearch.isEmpty())
                    arrivalAirPrtCode = null;
            }
        }
    }

    private void refershAirlineList() {
        if (strAirlineData != null && strAirlineData.size() > 0) {
            String strSearch = null;
            int len = 0;

            len = airlineATextView.getText().length();
            strSearch = airlineATextView.getText().toString();

            airLineNameSearch.clear();
            airLineCodeSearch.clear();
            for (int i = 0; i < strAirlineData.size(); i++) {
                if (len <= strAirlineData.get(i).length()) {
                    if (strSearch.equalsIgnoreCase((String) strAirlineData.get(
                            i).subSequence(0, len))) {
                        airLineNameSearch.add(strAirlineData.get(i));
                    }
                }
            }

            if (airLineNameSearch.isEmpty()) {

                for (int i = 0; i < strAirlineCodeData.size(); i++) {

                    if (len <= strAirlineCodeData.get(i).length()) {

                        if (strSearch
                                .equalsIgnoreCase((String) strAirlineCodeData
                                        .get(i).subSequence(0, len))) {
                            String name = airlineList.get(Utils
                                    .getIndexFromList(strAirlineCodeData,
                                            strAirlineCodeData.get(i))).name;

                            airLineNameSearch.add(strAirlineCodeData.get(i)
                                    + "/" + name);
                            airLineCodeSearch.add(name);
                        }
                    }
                }

            }

            adapter = new ArrayAdapter<String>(FlightActivity.this,
                    R.layout.dropdown, airLineNameSearch);

            airlineATextView.setThreshold(1);
            airlineATextView.setAdapter(adapter);
            airlineATextView.showDropDown();
            if(airLineNameSearch.isEmpty())
                airlineCode = null;

        }

    }

    private void makeDataList() {
        if (airportList != null && airportList.size() > 0) {

            strAirportData = new ArrayList<String>();
            strAirportCodeData = new ArrayList<String>();
            for (int i = 0; i < airportList.size(); i++) {
                strAirportData.add(airportList.get(i).name);
                strAirportCodeData.add(airportList.get(i).code);
            }

        }

        if (airlineList != null && airlineList.size() > 0) {
            strAirlineData = new ArrayList<String>();
            strAirlineCodeData = new ArrayList<String>();
            for (int i = 0; i < airlineList.size(); i++) {
                strAirlineData.add(airlineList.get(i).name);
                strAirlineCodeData.add(airlineList.get(i).iata_code);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus == false)
            return;

        int id = v.getId();
        if (id == R.id.autocomplete_departure_airport) {
            if (strAirportData != null && strAirportData.size() > 0) {

                adapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                        strAirportData);
                departureAirport.setThreshold(1);
                departureAirport.setAdapter(adapter);
                departureAirport.showDropDown();
            }
        } else if (id == R.id.autocomplete_arrival_airport) {
            if (strAirportData != null && strAirportData.size() > 0) {

                adapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                        strAirportData);
                arrivalAirport.setThreshold(1);
                arrivalAirport.setAdapter(adapter);
                arrivalAirport.showDropDown();
            }
        } else if (id == R.id.autocomplete_airline) {
            if (strAirlineData != null && strAirlineData.size() > 0) {

                adapter = new ArrayAdapter<String>(this, R.layout.dropdown,
                        strAirlineData);
                airlineATextView.setThreshold(1);
                airlineATextView.setAdapter(adapter);
                airlineATextView.showDropDown();
            }
        }

    }

    protected Handler handler = new Handler();

    public void DisplayMessage(final String msg) {
        final Context context = getApplicationContext();
        handler.post(new Runnable() {
            public void run() {
                try {
                    int duration = Toast.LENGTH_LONG;
                    Toast.makeText(context, msg, duration).show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
