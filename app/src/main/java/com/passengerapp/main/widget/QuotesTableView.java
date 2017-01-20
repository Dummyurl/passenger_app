package com.passengerapp.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.passengerapp.main.network.model.response.SearchDriversData;

import java.util.List;

/**
 * Created by adventis on 11/8/14.
 */
public class QuotesTableView extends TableLayout {

    public final String TAG = "QuotesTableView";

    private List<SearchDriversData> adapter;
    private TextView column_1_ride;
    private ImageView column_2_location;
    private TextView column_3_rider_rating;
    private TextView column_4_fare;
    private TextView column_5_tip_icluded;

    private int column_1_ride_width;
    private int column_2_location_width;
    private int column_3_rider_rating_width;
    private int column_4_fare_width;
    private int column_5_tip_icluded_width;

    public QuotesTableView(Context context) {
        super(context);
        //--- Additional custom code --
    }

    public QuotesTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //--- Additional custom code --
    }

    public void setData(List<SearchDriversData> adapter) {
        this.adapter = adapter;
        //createTable();
    }


    public void setColumns(TextView column_1_ride, ImageView column_2_location, TextView column_3_rider_rating, TextView column_4_fare, TextView column_5_tip_icluded) {
        this.column_1_ride = column_1_ride;
        this.column_1_ride_width = column_1_ride.getLayoutParams().width;

        this.column_2_location = column_2_location;
        this.column_2_location_width = column_2_location.getLayoutParams().width;

        this.column_3_rider_rating = column_3_rider_rating;
        this.column_3_rider_rating_width = column_3_rider_rating.getLayoutParams().width;

        this.column_4_fare = column_4_fare;
        this.column_4_fare_width = column_4_fare.getLayoutParams().width;

        this.column_5_tip_icluded = column_5_tip_icluded;
        this.column_5_tip_icluded_width = column_5_tip_icluded.getLayoutParams().width;
    }





}
