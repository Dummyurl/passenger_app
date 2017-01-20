package com.passengerapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.model.http.requests.HttpGetFlightInfofromFV;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.DepArrInfoFlightData;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SaveFlightDetailData;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 8/14/15.
 */
public class FlightSpecialInstructions extends PassengerBaseActivity {


    private SaveFlightDetailRequest flightData;

    private DriverViewModel viewModel;

    private SaveFlightDetailData saveFlghtInfoResult;

    private SearchDriversData searchdriverdata;
    private String flightDataReservationId;
    private String updateFlightDataDriverClass;
    private SaveFlightDetailRequest editableFlightData;
    private HttpGetFlightInfofromFV flightInforequest;

    private int REQUEST_SEND_FLIGHT_DATE = 2;
    private int REQUEST_VERIFY_FLIGHT_DATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_instructions_driver);

        viewModel = new DriverViewModel();
        flightData = (SaveFlightDetailRequest)getIntent().getSerializableExtra(Const.FLIGHT_DATA_EXTRA_ID);
        searchdriverdata = (SearchDriversData)getIntent().getSerializableExtra(Const.SEARCH_DRIVER_DATA);
        editableFlightData = (SaveFlightDetailRequest)getIntent().getSerializableExtra(Const.MODIFY_FLIGHT_DETAIL);
        updateFlightDataDriverClass = getIntent().getStringExtra(Const.MODIFY_FLIGHT_DETAIL_DRIVER_CAR_CLASS);
        flightInforequest = (HttpGetFlightInfofromFV)getIntent().getSerializableExtra(Const.SEND_FLIGHT_DATA_WITHOUT_REQUEST);

        if(searchdriverdata!=null) {
            updateFlightDataDriverClass = searchdriverdata.VehicleShape.Vclass;
        }

        initUI();
        updateUI();
        if(flightInforequest != null) {
            boolean isArrival = flightData != null && flightData.is_arrival;
            StringBuilder text = new StringBuilder();
            text.append("<b>"+getResources().getString(R.string.activity_flight_departure_hint) + ":</b> ");
            text.append(flightInforequest.DepartureAirport);
            text.append("<br>");
            text.append("<b>" + getResources().getString(R.string.activity_flight_airline_hint) + ":</b> " + flightInforequest.Airline + " # " + flightInforequest.FlightId);
            text.append("<br>");
            text.append("<b>" + getResources().getString(R.string.activity_flight_arrival_hint_simple) + ":</b> ");
            text.append(flightInforequest.ArrivalAirport);
            text.append("<br>");

            Date departureDate = null;
            try {
                text.append("<b>"+getResources().getString(R.string.common_date) + ":</b> ");
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
                departureDate = inputFormat.parse(flightInforequest.DepartureDate);
                text.append(outputFormat.format(departureDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            specialInstructions.setVisibility(View.GONE);
            selectedFlightNumberLayout.setVisibility(View.GONE);

            specialInstructionsEdittextTitle.setText(getResources().getText(R.string.activity_flight_manual_flight_info));
            additionalInfoTitleView.setVisibility(View.VISIBLE);
            additionalInfoTitleView.setText(Html.fromHtml(text.toString()));

            SaveFlightDetailRequest saveFlightInfo = new SaveFlightDetailRequest();
            saveFlightInfo.arrival = new DepArrInfoFlightData();
            saveFlightInfo.arrival.airport_code = flightInforequest.ArrivalAirport;
            saveFlightInfo.arrival.date_time = flightInforequest.ArrivalAirport;
            saveFlightInfo.departure = new DepArrInfoFlightData();
            saveFlightInfo.departure.airport_code = flightInforequest.DepartureAirport;
            if(departureDate != null) {
                SimpleDateFormat outputFlightDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFlightTimeFormat = new SimpleDateFormat("HH:mm:ss");
                saveFlightInfo.arrival.date = outputFlightDateFormat.format(departureDate);
                saveFlightInfo.arrival.time = outputFlightTimeFormat.format(departureDate);
                saveFlightInfo.departure.time = outputFlightTimeFormat.format(departureDate);
                saveFlightInfo.departure.date = outputFlightDateFormat.format(departureDate);
            }
            saveFlightInfo.flight_number = Integer.parseInt(flightInforequest.FlightId);
            saveFlightInfo.flight_id = flightInforequest.FlightId;
            saveFlightInfo.special_instructions = "";
            saveFlightInfo.is_arrival = flightData != null && flightData.is_arrival;
            saveFlightInfo.airline_code = flightInforequest.Airline;
            saveFlightInfo.is_verified = false;

            requestSaveFlightInfo(saveFlightInfo, REQUEST_SEND_FLIGHT_DATE);
        }
    }

    private TextView cancelFlightBtn;
    private TextView backBtn;
    private TextView doneBtn;
    private TextView flightNumber;
    private TextView specialInstructionsEdittextTitle;
    private EditText specialInstructions;
    private TextView additionalInfoTitleView;
    LinearLayout arrivalRadioButtonsCarService;
    RadioGroup arrivalRadioGroupCarService;
    LinearLayout arrivalRadioButtonsTaxi;
    RadioGroup arrivalRadioGroupTaxi;
    LinearLayout selectedFlightNumberLayout;

    private void initUI() {
        cancelFlightBtn = (TextView) findViewById(R.id.cancel_flight_btn);
        cancelFlightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        backBtn = (TextView) findViewById(R.id.back_btn_textview);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        doneBtn = (TextView) findViewById(R.id.done_btn_textview);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flightInforequest!=null) {
                    Intent data= new Intent();
                    data.putExtra(Const.FLIGHT_DETAIL_HAS_TEMP_FLIGHT_ID, true);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    SaveFlightDetailRequest saveFlghtInfo = prepeareFlightInfoData(flightData);
                    if (saveFlghtInfo != null) {
                    /*if (searchdriverdata !=null) {
                        Intent data= new Intent();
                        if(flightInforequest==null) {
                            data.putExtra(Const.FLIGHT_DETAIL_TO_SEND_AFTER_PICKUP_REQUEST, saveFlghtInfo);
                        }
                        setResult(RESULT_OK, data);
                        finish();
                    } else {*/
                            requestSaveFlightInfo(saveFlghtInfo, REQUEST_VERIFY_FLIGHT_DATE);
                    } else {
                        Utils.showWarningMsg(FlightSpecialInstructions.this, getResources().getString(R.string.activity_flight_data_error));
                    }
                }
            }
        });

        flightNumber = (TextView) findViewById(R.id.selected_flight_number);
        specialInstructions = (EditText) findViewById(R.id.special_instructions_edittext);
        specialInstructions.setHorizontallyScrolling(false);
        //specialInstructions.setImeOptions(EditorInfo.IME_ACTION_DONE);

        arrivalRadioButtonsCarService = (LinearLayout)findViewById(R.id.linear_car_service_radiogroup);
        arrivalRadioGroupCarService = (RadioGroup)findViewById(R.id.radio_group_car_service);
        arrivalRadioButtonsTaxi = (LinearLayout)findViewById(R.id.linear_taxi_radiogroup);
        arrivalRadioGroupTaxi = (RadioGroup)findViewById(R.id.radio_group_taxi);

        selectedFlightNumberLayout = (LinearLayout)findViewById(R.id.selected_flight_number_layout);
        selectedFlightNumberLayout = (LinearLayout)findViewById(R.id.selected_flight_number_layout);
        specialInstructionsEdittextTitle = (TextView)findViewById(R.id.special_instructions_edittext_title);
        additionalInfoTitleView = (TextView)findViewById(R.id.additional_info_titleview);
    }

    private SaveFlightDetailRequest prepeareFlightInfoData(SaveFlightDetailRequest data) {
        if(data == null) return null;

        data.special_instructions = specialInstructions.getText().toString();
        data.is_verified = true;
        if(editableFlightData != null) {
            data.reservation = editableFlightData.reservation;
        }
        if(flightData.is_arrival) {
            if(updateFlightDataDriverClass.isEmpty() && editableFlightData!=null) {
                data.meet = editableFlightData.meet;
            } else {
                if (updateFlightDataDriverClass.equalsIgnoreCase(Const.CAR_SERVICE_NAME)) {
                    data.meet = getMeetIdByRadioButtonId(arrivalRadioGroupCarService.getCheckedRadioButtonId());
                } else {
                    data.meet = getMeetIdByRadioButtonId(arrivalRadioGroupTaxi.getCheckedRadioButtonId());
                }
            }
        }

        return data;
    }

    private String getMeetIdByRadioButtonId(int radioButtonId) {

        if(radioButtonId == R.id.car_service_baggage_area || radioButtonId == R.id.taxi_baggage_area) {
            return "B";
        } else if(radioButtonId == R.id.car_service_limo_pickup) {
            return "L";
        } else if(radioButtonId == R.id.taxi_taxi_pickup) {
            return "T";
        }

        return "";
    }

    private void updateUI() {
        if(flightData!=null) {
            flightNumber.setText(String.valueOf(flightData.flight_number));
        }

        if(flightData!=null && flightData.is_arrival) {
            if(!updateFlightDataDriverClass.isEmpty()) {
                if (updateFlightDataDriverClass.equalsIgnoreCase(Const.CAR_SERVICE_NAME)) {
                    arrivalRadioButtonsCarService.setVisibility(View.VISIBLE);
                } else {
                    arrivalRadioButtonsTaxi.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void requestSaveFlightInfo(final SaveFlightDetailRequest saveflghtInfo, final int identifier) {

        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        saveflghtInfo.unique_id = StorageDataHelper.getInstance(getBaseContext()).getPhoneToken();
        //SaveFlightDetailRequest request = new SaveFlightDetailRequest(saveflghtInfo);


        NetworkApi api = (new NetworkService()).getApi();
        api.saveFlightDetail(saveflghtInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<SaveFlightDetailData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);

                    }

                    @Override
                    public void onNext(JsonServerResponse<SaveFlightDetailData> saveFlightDetailDataJsonServerResponse) {
                        if (saveFlightDetailDataJsonServerResponse.IsSuccess) {
                            saveFlghtInfoResult = saveFlightDetailDataJsonServerResponse.Content;
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), saveFlightDetailDataJsonServerResponse.Message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        /*new Thread() {
            public void run() {

                JsonResponse<HttpSaveFlightInfo> saveFlightRes = viewModel.saveFlightDetails(saveflghtInfo, StorageDataHelper.getInstance(getBaseContext()).getPhoneToken());

                if (saveFlightRes.IsSuccess) {
                    DisplayProcessMessage(false);
                    saveFlghtInfoResult = saveFlightRes.Content;
                    Message msg = new Message();
                    msg.what = identifier;
                    msg.arg1 = 0;
                    Const.flightId = Integer.parseInt(saveFlightRes.Content.flight_id);
                    //Const.strFlightId = saveflghtInfo.Content.flight_id;
                    Const.saveflghtInfo = saveflghtInfo;
                    handlerWeb.sendMessage(msg);
                    this.interrupt();
                    return;

                } else {
                    DisplayProcessMessage(false);
                    Message msg = new Message();
                    msg.what = identifier;
                    msg.arg1 = 1;
                    handlerWeb.sendMessage(msg);
                    this.interrupt();
                    return;

                }

            }
        }.start();*/
    }


    /*@SuppressLint("HandlerLeak")
    private Handler handlerWeb = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && msg.arg1 == 0) {
                setResult(RESULT_OK);
                finish();
                *//*Const.mQouteState = 0;
                Intent intent = new Intent(FlightSpecialInstructions.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("s", true);
                startActivity(intent);

                finish();*//*
            } else if(msg.what == REQUEST_SEND_FLIGHT_DATE && msg.arg1 == 0) {

            }
        }
    };*/
}
