package com.passengerapp.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.util.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adventis on 9/21/15.
 */
public class FlightDetailView extends Activity {
    private SaveFlightDetailRequest flightData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_flightdetails_driver);

        flightData = (SaveFlightDetailRequest)getIntent().getSerializableExtra(Const.FLIGHT_DETAIL);
        initUI();
        updateUI();
    }

    TextView flightTitle, flightStatus, departureCodeDiagram, arriveCodeDiagram;
    TextView departurePlace, departureDate, departureTime, departureTerminal, departureGate;
    TextView arrivePlace, arriveDate, arriveTime, arriveTerminal, arriveGate;
    TextView pickupMeAt, specialInstructions;
    TextView okBtn;

    public void initUI() {
        flightTitle = (TextView) findViewById(R.id.flight_detail_main_title);
        flightStatus = (TextView) findViewById(R.id.flight_detail_status);
        departureCodeDiagram = (TextView) findViewById(R.id.txtFltDepaAirCode);
        arriveCodeDiagram = (TextView) findViewById(R.id.txtFltArrAirCode);

        departurePlace = (TextView) findViewById(R.id.flight_view_departure_airport);
        departureDate = (TextView) findViewById(R.id.flight_info_departure_time);
        LinearLayout departureLayout = (LinearLayout) findViewById(R.id.departure_flight_info);
        if(departureLayout != null) {
            departureTime = (TextView) departureLayout.findViewById(R.id.time_arrive_departure);
            departureTerminal = (TextView) departureLayout.findViewById(R.id.terminal_arrive_departure);
            departureGate = (TextView) departureLayout.findViewById(R.id.gate_arrive_departure);
        }

        LinearLayout arriveLayout = (LinearLayout) findViewById(R.id.arrive_flight_info);
        if(arriveLayout != null) {
            arriveTime = (TextView) arriveLayout.findViewById(R.id.time_arrive_departure);
            arriveTerminal = (TextView) arriveLayout.findViewById(R.id.terminal_arrive_departure);
            arriveGate = (TextView) arriveLayout.findViewById(R.id.gate_arrive_departure);
        }

        arrivePlace = (TextView) findViewById(R.id.flight_view_arrive_airport);
        arriveDate = (TextView) findViewById(R.id.flight_info_arrive_time);

        pickupMeAt = (TextView) findViewById(R.id.pick_me_at_value);
        specialInstructions = (TextView) findViewById(R.id.special_instructions_value);

        okBtn = (TextView) findViewById(R.id.txtMapLink);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String convertToNeededDate(String dateStr) {
        SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat returnedFormat = new SimpleDateFormat("EEEE,MMMM dd");
        try {
            Date date = formatter.parse(dateStr);
            String returnDate = returnedFormat.format(date);

            return returnDate;
        } catch (ParseException e) {
            return "";
        }
    }

    private String convertToNeededTime(String timeStr) {
        SimpleDateFormat formatter =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat returnedFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = formatter.parse(timeStr);
            String returnDate = returnedFormat.format(date);

            return returnDate;
        } catch (ParseException e) {
            return "";
        }
    }

    public void updateUI() {
        if(flightData == null) {
            finish();
            return;
        }

        flightTitle.setText(flightData.airline_name+" "+flightData.flight_number);
        flightStatus.setText(flightData.status);

        departureCodeDiagram.setText(flightData.departure.airport_code.toUpperCase());
        arriveCodeDiagram.setText(flightData.arrival.airport_code.toUpperCase());

        departurePlace.setText(flightData.departure.airport_code);
        departureDate.setText(convertToNeededDate(flightData.departure.date_time));
        try {
            departureTime.setText(convertToNeededTime(flightData.departure.date_time));
            departureTerminal.setText(flightData.departure.terminal);
            departureGate.setText(flightData.departure.gate);
        } catch (Exception e) {

        }


        arrivePlace.setText(flightData.arrival.airport_code);
        arriveDate.setText(convertToNeededDate(flightData.arrival.date_time));
        try {
            arriveTime.setText(convertToNeededTime(flightData.arrival.date_time));
            arriveTerminal.setText(flightData.arrival.terminal);
            arriveGate.setText(flightData.arrival.gate);
        }catch (Exception e) {

        }

        pickupMeAt.setText(flightData.meet);
        specialInstructions.setText(flightData.special_instructions);
    }
}
