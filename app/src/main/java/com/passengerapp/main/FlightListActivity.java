package com.passengerapp.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import java.util.ArrayList;

/**
 * Created by adventis on 8/12/15.
 */
public class FlightListActivity extends Activity {
    private final int FLIGHT_SPECIAL_INSTRUCTION_ACTIVITY_START = 0;
    private ArrayList<SaveFlightDetailRequest> flightList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_list);

        selectedFlight = null;

        flightList = (ArrayList<SaveFlightDetailRequest>)getIntent().getSerializableExtra(Const.FLIGHT_LIST_RACES);

        if(flightList != null && flightList.size()==1) {
            selectedFlight = flightList.get(0);
            startFlightSpecialInstruction();
        }

        initUI();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FLIGHT_SPECIAL_INSTRUCTION_ACTIVITY_START && resultCode == RESULT_OK) {
            setResult(RESULT_OK,data);
            finish();
        }
    }

    private TextView cancelFlightBtn;
    private TextView departureAirport;
    private TextView departureDate;
    private TextView arrivalAirport;
    private TextView arrivalDate;
    private LinearLayout flightTable;

    private TextView backBtn;
    private TextView nextBtn;
    SaveFlightDetailRequest selectedFlight;

    private void startFlightSpecialInstruction(){
        Intent intent = new Intent(FlightListActivity.this, FlightSpecialInstructions.class);
        intent.putExtra(Const.FLIGHT_DATA_EXTRA_ID, selectedFlight);
        intent.putExtras(getIntent().getExtras());
        startActivityForResult(intent, FLIGHT_SPECIAL_INSTRUCTION_ACTIVITY_START);
    }

    private void initUI(){
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
        nextBtn = (TextView) findViewById(R.id.next_btn_textview);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFlight == null) {
                    Utils.showWarningMsg(FlightListActivity.this, getResources().getString(R.string.activity_flight_pls_select_flight_msg));
                    return;
                }

                startFlightSpecialInstruction();
            }
        });


        View departureView = findViewById(R.id.flight_list_departing_include_view);
        departureAirport = (TextView)departureView.findViewById(R.id.flight_airport_texview);
        departureDate = (TextView)departureView.findViewById(R.id.flight_date_texview);

        View arrivalView = findViewById(R.id.flight_list_arrival_include_view);
        arrivalAirport = (TextView)arrivalView.findViewById(R.id.flight_airport_texview);
        arrivalDate = (TextView)arrivalView.findViewById(R.id.flight_date_texview);

        flightTable = (LinearLayout) findViewById(R.id.flight_list);
    }

    private void updateUI(){
        if(flightList != null && !flightList.isEmpty()) {
            flightTable.removeAllViews();

            for (SaveFlightDetailRequest flight : flightList) {
                View item = View.inflate(getBaseContext(), R.layout.activity_flight_list_table_item, null);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout parent = (LinearLayout)v.getParent();
                        for(int i=0; i < parent.getChildCount(); i++) {
                            parent.getChildAt(i).setBackgroundColor(0x00000000);
                        }

                        v.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                        SaveFlightDetailRequest flight = (SaveFlightDetailRequest)v.getTag();
                        selectedFlight = flight;
                        departureAirport.setText(flight.departure.airport_code);
                        departureDate.setText(flight.departure.date);
                        arrivalAirport.setText(flight.arrival.airport_code);
                        arrivalDate.setText(flight.arrival.date);

                    }
                });
                TextView airlineNumber = (TextView)item.findViewById(R.id.flight_airline_number);
                TextView status = (TextView)item.findViewById(R.id.flightlist_status);
                TextView departureTime = (TextView)item.findViewById(R.id.flightlist_departure_time);
                TextView arrivalTime = (TextView)item.findViewById(R.id.flightlist_arrival_time);

                airlineNumber.setText(flight.airline_name+"\n"+flight.flight_number);
                status.setText(flight.status);
                departureTime.setText(flight.departure.time);
                arrivalTime.setText(flight.arrival.time);

                item.setTag(flight);

                flightTable.addView(item);
            }
        }
    }
}
