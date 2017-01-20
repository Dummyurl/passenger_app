package com.passengerapp.main.dialogs.PickUp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.services.ImageDownloader;
import com.passengerapp.main.widget.TermsAndCondition.OnShowTermsAndCondition;
import com.passengerapp.main.widget.TermsAndCondition.TermsAndConditionView;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

;

/**
 * Created by adventis on 8/18/15.
 */
public class PickUpNowFragment extends Fragment implements OnPickUpFragmentListener {

    private OnPickUpActivityAction activityActionCallback;
    private OnShowTermsAndCondition activityShowTermsAndConditionsCallback;


    TermsAndConditionView termsAndConditionsLayout;
    ImageView imageDriver;
    TextView nameOfDriver;
    TextView phoneNumberDriver;

    RatingBar ratingBar;
    TextView ratingDriver;
    TextView reviewsNumberForDriver;

    TextView vehicleNumberDriver;
    TextView descriptionVehicle;
    TextView locationVehicle;
    TextView tripDistance;

    TextView haveCoupon;

    TextView totalFare;

    LinearLayout areaForFareDetails;

    TextView rideBtn;
    TextView closeBtn;

    View mainView;
    LayoutInflater inflater;

    private SearchDriversData searchdriverdata = new SearchDriversData();

    private SearchDriversTempData searchDriversTempData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.activity_pickup, container, false);
        this.inflater = inflater;
        searchdriverdata = (SearchDriversData)getArguments().get(Const.SEARCH_DRIVER_DATA);

        initUI(mainView);
        updateUI();

        return mainView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityActionCallback = (OnPickUpActivityAction) activity;
            activityShowTermsAndConditionsCallback = (OnShowTermsAndCondition) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onResume() {
        searchDriversTempData = StorageDataHelper.getInstance(getActivity()).getSearchDriversCurrentDataForRequest();
        super.onResume();
    }

    private void initUI(View view) {
        termsAndConditionsLayout = (TermsAndConditionView) view.findViewById(R.id.terms_and_condition_layout);
        termsAndConditionsLayout.setParentActivity(activityShowTermsAndConditionsCallback);


        nameOfDriver = (TextView) view.findViewById(R.id.name_of_driver);
        phoneNumberDriver = (TextView) view.findViewById(R.id.phone_number_driver);
        imageDriver = (ImageView) view.findViewById(R.id.driver_image);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingDriverBar);
        ratingDriver = (TextView) view.findViewById(R.id.rating_driver_text_view);
        reviewsNumberForDriver = (TextView) view.findViewById(R.id.review_number_driver_text_view);

        vehicleNumberDriver = (TextView) view.findViewById(R.id.vehicle_number_textview);
        descriptionVehicle = (TextView) view.findViewById(R.id.vehicle_descr_textview);
        locationVehicle = (TextView) view.findViewById(R.id.vehicle_location_textview);
        tripDistance = (TextView) view.findViewById(R.id.trip_distance_textview);

        totalFare = (TextView) view.findViewById(R.id.total_fare_value_textview);

        haveCoupon = (TextView) view.findViewById(R.id.have_coupon_button);


        rideBtn = (TextView) view.findViewById(R.id.request_ride_btn);
        rideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityActionCallback.ride(termsAndConditionsLayout.isAccepted(), searchdriverdata);
            }
        });
        closeBtn = (TextView) view.findViewById(R.id.close_activity_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityActionCallback.close();
            }
        });
    }

    private void updateUI() {
        try {
            new ImageDownloader(null, imageDriver).execute(this.searchdriverdata.Driver.DriverImage);
        } catch (Exception e) {

        }

        if(activityActionCallback.isValidCouponCode() && this.searchdriverdata.MerchantId.equalsIgnoreCase(activityActionCallback.getCouponData().MerchantId)) {
            haveCoupon.setText(getResources().getString(R.string.pickup_activity_coupon_applied));
            haveCoupon.setTextColor(getResources().getColor(R.color.green));
            haveCoupon.setOnClickListener(null);
        } else {
            haveCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityActionCallback.openCouponView(searchdriverdata);
                }
            });
        }

        nameOfDriver.setText(this.searchdriverdata.Driver.firstName + " " + this.searchdriverdata.Driver.lastName);
        phoneNumberDriver.setText(this.searchdriverdata.PhoneNumber);

        ratingBar.setRating((float) this.searchdriverdata.Rating);
        //ratingBar.setRating(Float.parseFloat(this.searchdriverdata.Rating));
        ratingDriver.setText(new DecimalFormat("#.#").format(this.searchdriverdata.Rating));
        reviewsNumberForDriver.setText("("+this.searchdriverdata.ReviewNumber+")");

        vehicleNumberDriver.setText(this.searchdriverdata.TaxiID);
        descriptionVehicle.setText(this.searchdriverdata.VehicleShape.Vcolor + "" + this.searchdriverdata.VehicleShape.Vstyle);
//        tripDistance.setText(new DecimalFormat("##.##").format(searchDriversTempData.replyPickupValue)
//                + " " + searchDriversTempData.replyPickupUnit);
        locationVehicle.setText(new DecimalFormat("##.##").format(searchdriverdata.Distance.Value)
                + " " + searchdriverdata.Distance.Unit);

        updateFareDetailsItem();

        totalFare.setText("$" + Utils.modifyFare(this.searchdriverdata.Fare));

        JSONObject termsAndCondition = StorageDataHelper.getInstance(getActivity()).getTermsAndConditions();
        if (termsAndCondition == null || !termsAndCondition.keys().hasNext()) {
            termsAndConditionsLayout.setVisibility(View.GONE);
        }
    }

    private void updateFareDetailsItem() {
        ArrayList<View> listOfElements = activityActionCallback.updateFareDetailsItem(this.searchdriverdata);
        areaForFareDetails = (LinearLayout) mainView.findViewById(R.id.area_for_fare_details);
        areaForFareDetails.removeAllViews();
        for (View item: listOfElements) {
            areaForFareDetails.addView(item);
        }
    }

    @Override
    public void updateCoupon() {
        if(activityActionCallback.isValidCouponCode() && this.searchdriverdata.MerchantId.equalsIgnoreCase(activityActionCallback.getCouponData().MerchantId)) {
            this.searchdriverdata = activityActionCallback.applyCouponToDriver(this.searchdriverdata);
        }
        updateUI();
    }
}
