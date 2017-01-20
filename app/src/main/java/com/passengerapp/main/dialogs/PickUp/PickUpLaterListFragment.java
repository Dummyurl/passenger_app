package com.passengerapp.main.dialogs.PickUp;

import android.app.Activity;
import android.graphics.Color;
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
import com.passengerapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adventis on 10/1/15.
 */
public class PickUpLaterListFragment extends Fragment implements OnPickUpFragmentListener {

    private OnPickUpActivityAction activityActionCallback;
    private OnShowTermsAndCondition activityShowTermsAndConditionsCallback;

    private String lastMerchantIdForCoupon = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.activity_pickup_category_list, container, false);
        searchDriversList = (List<SearchDriversData>)getArguments().get(Const.SEARCH_DRIVERS_DATA);

        initUI();
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

    LinearLayout listOfDriversTable;
    TermsAndConditionView termsAndConditionsLayout;
    View mainView;

    private List<SearchDriversData> searchDriversList = null;

    public void initUI(){
        listOfDriversTable = (LinearLayout) mainView.findViewById(R.id.list_of_drivers_category_table);

        termsAndConditionsLayout = (TermsAndConditionView) mainView.findViewById(R.id.terms_and_condition_layout);
        termsAndConditionsLayout.setParentActivity(activityShowTermsAndConditionsCallback);
    }

    private void updateUI() {
        listOfDriversTable.removeAllViews();
        for(int current = 0; current < searchDriversList.size(); current++) {
            final SearchDriversData driver = searchDriversList.get(current);

            final View itemByTripNow = View.inflate(getActivity(), R.layout.quotes_both_later_item, null);

            // Go to detail fragment for pickUp
            //itemByTripNow.setOnClickListener(clickRow);

            itemByTripNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityActionCallback.showDetailPickUpForLater(driver);
                }
            });

            TextView bootBtn = (TextView) itemByTripNow.findViewById(R.id.boot_btn);
            bootBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activityActionCallback.ride(termsAndConditionsLayout.isAccepted(), driver);
                }
            });

            final TextView haveCouponBtn = (TextView) itemByTripNow.findViewById(R.id.have_coupon_btn);
            if (activityActionCallback.isValidCouponCode() && driver.MerchantId.equalsIgnoreCase(activityActionCallback.getCouponData().MerchantId)) {
                haveCouponBtn.setText(getResources().getString(R.string.pickup_activity_coupon_applied));
                haveCouponBtn.setTextColor(getResources().getColor(R.color.white));
                haveCouponBtn.setBackgroundColor(getResources().getColor(R.color.green));
                haveCouponBtn.setOnClickListener(null);
            } else {
                haveCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activityActionCallback.openCouponView(driver);
                    }
                });
            }

            NetworkImageView imageView = (NetworkImageView) itemByTripNow.findViewById(R.id.image_of_vehicle);
            imageView.setImageUrl(driver.VehicleShape.ExteriorImage, VolleyLoader.getInstance(getActivity()).getImageLoader());

            TextView totalFare = (TextView) itemByTripNow.findViewById(R.id.total_fare_value_textview);
            totalFare.setText("$" + Utils.modifyFare(driver.Fare));

            ArrayList<View> listOfElements = activityActionCallback.updateFareDetailsItem(driver);
            LinearLayout areaFareTable = (LinearLayout) itemByTripNow.findViewById(R.id.area_for_fare_details_later);
            areaFareTable.removeAllViews();
            for (View item: listOfElements) {
                areaFareTable.addView(item);
            }

            try {
                TextView passangerView = (TextView) itemByTripNow.findViewById(R.id.category_number_of_pass_textview);
                passangerView.setTextColor(Color.BLACK);
                passangerView.setText(driver.VehicleShape.numberOfPass + "");
                TextView suitcaseView = (TextView) itemByTripNow.findViewById(R.id.category_num_of_suitcases_textview);
                suitcaseView.setTextColor(Color.BLACK);
                suitcaseView.setText(driver.VehicleShape.numberOfSuitcase + "");
            } catch (Exception e) {

            }

            listOfDriversTable.addView(itemByTripNow);
        }
    }

    @Override
    public void updateCoupon() {
        for(int i=0; i< searchDriversList.size(); i++) {
            if(this.searchDriversList.get(i).MerchantId.equalsIgnoreCase(activityActionCallback.getCouponData().MerchantId)) {
                this.searchDriversList.set(i,activityActionCallback.applyCouponToDriver(this.searchDriversList.get(i)));
            }
        }
        updateUI();
    }
}
