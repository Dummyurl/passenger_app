package com.passengerapp.main.uc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.passengerapp.R;
import com.passengerapp.main.adapter.ArrayWheelAdapter;
import com.passengerapp.main.adapter.scrollingDone;
import com.passengerapp.main.widget.WheelView;

import java.util.ArrayList;
import java.util.Date;

public class WheelHourMinuteActivity extends Activity implements scrollingDone {

	public Button btnDone, btnCancel;
	String[] hoursArray = null;
	String[] minutesArray = null;
	//int year = Calendar.getInstance().get(Calendar.YEAR);
	//int nextYear = 0;
	//String[] yearArray = null;
	//private ArrayList<String> yearArrayList;

	String selectedMinute = null;
	String selectedHour = null;
	//String selectedYear = null;
	private ArrayWheelAdapter<String> hoursAdapter;
	private ArrayWheelAdapter<String> minutesAdapter;

	int dPos = 0, mPos = 0, yPos = 0;
	int cdPos = 0, cmPos = 0, cyPos = 0;

    private int tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wheeltime);

		btnDone = (Button) findViewById(R.id.btn_Done_time);
		btnCancel = (Button) findViewById(R.id.btn_cancel_time);

        ArrayList<String> hoursArrayList = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            hoursArrayList.add(String.valueOf(i+1));
        }
        hoursArray = new String[hoursArrayList.size()];
        hoursArray =  hoursArrayList.toArray(hoursArray);

        ArrayList<String> minutesArrayList = new ArrayList<String>();
        for (int i = 0; i < 60; i+=10) {
            minutesArrayList.add(String.valueOf(i));
        }
        minutesArray = new String[minutesArrayList.size()];
        minutesArray =  minutesArrayList.toArray(minutesArray);
		
		Intent gotIntent = getIntent();
		selectedHour = gotIntent.getStringExtra("selected-hour");
		selectedMinute = gotIntent.getStringExtra("selected-minute");
        tag = gotIntent.getIntExtra("tag",0);

		wheeler1();

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				setResults(RESULT_CANCELED, "", "");

			}

		});

		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date date = null;
				String datePart[] = null;
				try {
				
						if (timeValidation(selectedHour, selectedMinute)) {
							setResults(RESULT_OK, selectedHour, selectedMinute);
						} else {
							AlertDailogView.showAlert(WheelHourMinuteActivity.this,
									"Plaease Select proper Time..", "Ok")
									.show();
						}
					
				} catch (Exception e) {
					e.printStackTrace();
					AlertDailogView.showAlert(WheelHourMinuteActivity.this,
							"Please Select Valid Time", "Ok").show();

				}
			}
		});
	}

	private boolean timeValidation(String hour, String minute) {

		boolean result = true;
		if (!(hour != null && !hour.equals("") && minute != null && !minute.equals(""))) {
            result = false;
		}
		return result;
	}

	

	private void setResults(int result, String selectedHour,
			String selectedMinutes) {
		Intent previous = new Intent();
		previous.putExtra("selected-hour", selectedHour);
		previous.putExtra("selected-minute", selectedMinutes);
        previous.putExtra("tag", tag);

        Log.d("Wheel", selectedHour+":"+selectedMinutes);

		setResult(result, previous);
		finish();
	}

	private void wheeler1() {

		final WheelView wheel = (WheelView) findViewById(R.id.wheel1_time);
		wheel.setListner(1, this);
		hoursAdapter = new ArrayWheelAdapter<String>(this, hoursArray);
        hoursAdapter.setItemResource(R.layout.wheel_text_date);
        hoursAdapter.setItemTextResource(R.id.text);
		wheel.setViewAdapter(hoursAdapter);
		wheel.setCyclic(false);
		wheel.setCurrentItem(Integer.parseInt(selectedHour) - 1);

		final WheelView wheel1 = (WheelView) findViewById(R.id.wheelDt_time);
		wheel1.setListner(2, this);
        minutesAdapter = new ArrayWheelAdapter<String>(this, minutesArray);
        minutesAdapter.setItemResource(R.layout.wheel_text_time);
        minutesAdapter.setItemTextResource(R.id.text);
		wheel1.setViewAdapter(minutesAdapter);
		wheel1.setCyclic(false);
		wheel1.setCurrentItem(Integer.parseInt(selectedMinute) - 1);
        selectedMinute = minutesArray[wheel1.getCurrentItem()];
	}

	@Override
	public void OnScrollingDone(int tag, int position) {

		if (tag == 1) {
			dPos = position;
			selectedHour = (String) hoursAdapter.getItemText(position);
		} else if (tag == 2) {
			mPos = position;
			selectedMinute = (String) minutesAdapter.getItemText(position);
		}
	}
}
