package com.passengerapp.main.dialogs.PickUp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.passengerapp.R;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.services.VolleyLoader;
import com.passengerapp.main.widget.TermsAndCondition.OnShowTermsAndCondition;
import com.passengerapp.main.widget.TermsAndCondition.TermsAndConditionView;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by adventis on 8/18/15.
 */
public class PickUpLaterFragment  extends Fragment implements OnPickUpFragmentListener {

    private OnPickUpActivityAction activityActionCallback;
    private OnShowTermsAndCondition activityShowTermsAndConditionsCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.activity_pickup_category, container, false);
        if(searchdriverdata == null) {
            searchdriverdata = (SearchDriversData) getArguments().get(Const.SEARCH_DRIVER_DATA);
        }

        initUI();
        updateUI();

        return mainView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityActionCallback = (OnPickUpActivityAction) activity;
            activityShowTermsAndConditionsCallback = (OnShowTermsAndCondition)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    NetworkImageView carStyleImage;
    NetworkImageView carInteriorImage;
    TextView descriptionVehicle;
    TextView haveCoupon;
    LinearLayout areaForFareDetails;
    TextView totalFareValue;
    TextView bookBtn;
    TermsAndConditionView termsAndConditionsLayout;
    View mainView;

    private SearchDriversData searchdriverdata = new SearchDriversData();

    public void setDriver(SearchDriversData driver) {
        searchdriverdata = driver;
    }

    public void initUI(){
        carStyleImage = (NetworkImageView)mainView.findViewById(R.id.car_style_image);
        carInteriorImage = (NetworkImageView)mainView.findViewById(R.id.car_interior_image);

        descriptionVehicle = (TextView)mainView.findViewById(R.id.description_full_vehicle);
        haveCoupon = (TextView)mainView.findViewById(R.id.have_coupon_button);
        totalFareValue = (TextView)mainView.findViewById(R.id.total_fare_value_textview);
        bookBtn = (TextView)mainView.findViewById(R.id.request_ride_btn);
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityActionCallback.ride(termsAndConditionsLayout.isAccepted(), searchdriverdata);
            }
        });

        termsAndConditionsLayout = (TermsAndConditionView) mainView.findViewById(R.id.terms_and_condition_layout);
        termsAndConditionsLayout.setParentActivity(activityShowTermsAndConditionsCallback);
    }

    private void updateUI() {
        carStyleImage.setImageUrl(this.searchdriverdata.VehicleShape.ExteriorImage, VolleyLoader.getInstance(getActivity()).getImageLoader());
        carInteriorImage.setImageUrl(this.searchdriverdata.VehicleShape.InteriorImage, VolleyLoader.getInstance(getActivity()).getImageLoader());

        descriptionVehicle.setText(this.searchdriverdata.VehicleShape.VStyleDesc);

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
        updateFareDetailsItem();

        totalFareValue.setText("$" + Utils.modifyFare(this.searchdriverdata.Fare));

        JSONObject termsAndCondition = StorageDataHelper.getInstance(getActivity()).getTermsAndConditions();
        if (termsAndCondition == null || !termsAndCondition.keys().hasNext()) {
            termsAndConditionsLayout.setVisibility(View.GONE);
        }

        activityActionCallback.updateActionBarTitle(this.searchdriverdata.VehicleShape.VStyleShortDesc);
    }

    private void updateFareDetailsItem() {
        ArrayList<View> listOfElements = activityActionCallback.updateFareDetailsItem(this.searchdriverdata);
        areaForFareDetails = (LinearLayout) mainView.findViewById(R.id.area_for_fare_details_category);
        areaForFareDetails.removeAllViews();
        for (View item: listOfElements) {
            areaForFareDetails.addView(item);
        }
    }

    @Override
    public void updateCoupon() {
        if(activityActionCallback.isValidCouponCode() && this.searchdriverdata.MerchantId.equalsIgnoreCase(activityActionCallback.getCouponData().MerchantId))
        this.searchdriverdata = activityActionCallback.applyCouponToDriver(this.searchdriverdata);
        updateUI();
    }
}
