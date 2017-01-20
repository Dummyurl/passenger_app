package com.passengerapp.main.uc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.passengerapp.R;
import com.passengerapp.util.Utils;

import java.util.Calendar;

/**
 * Created by adventis on 4/1/15.
 */
public class WheelDateTimeActivity extends Activity {

    public static final String DATE_IN_MILLIS = "selected-time-millis";
    public static final String SELECTED_DAY_ID = "selected-date";
    public static final String SELECTED_MONTH_ID = "selected-month";
    public static final String SELECTED_YEAR_ID = "selected-year";
    public static final String SELECTED_HOURS_ID = "selected-hours";
    public static final String SELECTED_MINUTES_ID = "selected-minutes";
    public static final String SELECTED_MERIDIEM_ID = "selected-meridiem";
    public static final String IS_NOW = "is-now";

    Calendar localCalendar = null;

    boolean isClickedNow = true;
    int tag = 0;

    private RelativeLayout calendarPopupView;
    private CalendarView calendarView;
    private Button cancelCalendarBtn;
    private Button doneCalendarBtn;
    private Button todayCalendarBtn;

    private RelativeLayout timePopupView;
    private TimePicker timePickerView;
    private Button timeToDateBtn;
    private Button cancelTimeBtn;
    private Button doneTimeBtn;
    private Button nowTimeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendarview_date);

        // init calendar view
        calendarPopupView = (RelativeLayout) findViewById(R.id.calendar_popup_view);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                localCalendar.set(year, month, dayOfMonth);
                isClickedNow = false;
            }
        });
        cancelCalendarBtn = (Button) findViewById(R.id.calendar_btn_cancel);
        cancelCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPopupView();
            }
        });
        doneCalendarBtn = (Button) findViewById(R.id.calendar_btn_done);
        doneCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTimeView();
            }
        });
        todayCalendarBtn = (Button) findViewById(R.id.calendar_btn_today);
        todayCalendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTodayForCalendar();
            }
        });

        // init time view
        timePopupView = (RelativeLayout) findViewById(R.id.time_popup_view);
        timePickerView = (TimePicker) findViewById(R.id.timePicker);
        timePickerView.setIs24HourView(false);
        timePickerView.clearFocus();
        timePickerView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                localCalendar.set(Calendar.HOUR, hourOfDay);
                localCalendar.set(Calendar.MINUTE, minute);
                isClickedNow = false;
            }
        });
        timeToDateBtn = (Button) findViewById(R.id.time_to_date_btn);
        timeToDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToDateView();
            }
        });
        cancelTimeBtn = (Button) findViewById(R.id.time_btn_cancel);
        cancelTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPopupView();
            }
        });
        doneTimeBtn = (Button) findViewById(R.id.time_btn_done);
        doneTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finishSetDateTime();
            }
        });
        nowTimeBtn = (Button) findViewById(R.id.time_btn_now);
        nowTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateToNow();
            }
        });

        localCalendar = Calendar.getInstance();

        Intent gotIntent = getIntent();

        long selectedTime = gotIntent.getLongExtra(DATE_IN_MILLIS, System.currentTimeMillis());
        localCalendar.setTimeInMillis(selectedTime);
        tag = gotIntent.getIntExtra("tag", 0);

        updateCalendarTimeUI();
    }

    private void switchToTimeView() {
        calendarPopupView.setVisibility(View.INVISIBLE);
        timePopupView.setVisibility(View.VISIBLE);
    }

    private void switchToDateView() {
        calendarPopupView.setVisibility(View.VISIBLE);
        timePopupView.setVisibility(View.INVISIBLE);
    }

    private void cancelPopupView() {
        setResults(RESULT_CANCELED);
    }

    private void setDateToNow() {
        isClickedNow = true;
        localCalendar = Calendar.getInstance();
        updateCalendarTimeUI();
    }

    private void finishSetDateTime() {
        if(localCalendar.compareTo(Calendar.getInstance()) != -1 || isClickedNow == true) {
            setResults(RESULT_OK);
        } else {
            AlertDailogView.showAlert(WheelDateTimeActivity.this,
                    Utils.getStringById(R.string.my_trips_fragment_get_select_future_date), Utils.getStringById(R.string.common_OK)).show();
        }
    }

    private void setTodayForCalendar() {
        Calendar calendar = Calendar.getInstance();
        localCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) );

        updateCalendarTimeUI();
    }
    private void updateCalendarTimeUI() {
        calendarView.setDate(localCalendar.getTimeInMillis(),false,false);

        timePickerView.setCurrentHour(localCalendar.get(Calendar.HOUR));
        timePickerView.setCurrentMinute(localCalendar.get(Calendar.MINUTE));
    }

   /* private boolean dateValidation(String selecteddate, String month, String selectedyear) {

        boolean result = true;
        if (month != null && !month.equals("")) {
            {
                if (month.equals("February")) {
                    if ((Integer.parseInt(selectedyear) % 4) == 0) {
                        if ((Integer.parseInt(selecteddate) > 29)) {
                            result = false;
                        }
                    } else {
                        if ((Integer.parseInt(selecteddate) > 28)) {
                            result = false;
                        }
                    }
                } else if (month.equals("April") || month.equals("June")
                        || month.equals("September")
                        || month.equals("November")) {
                    if (Integer.parseInt(selecteddate) == 31) {
                        result = false;
                    }
                }
            }
        }
        return result;
    }*/



    private void setResults(int result) {
        Intent previous = new Intent();
        if(result == RESULT_OK) {
            previous.putExtra(DATE_IN_MILLIS, localCalendar.getTimeInMillis());
        }
        previous.putExtra(IS_NOW, isClickedNow);
        previous.putExtra("tag", tag);

        setResult(result, previous);
        finish();
    }
}
