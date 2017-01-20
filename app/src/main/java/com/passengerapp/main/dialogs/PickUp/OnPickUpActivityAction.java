package com.passengerapp.main.dialogs.PickUp;

import android.view.View;

import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.VerfiyCouponData;

import java.util.ArrayList;

/**
 * Created by adventis on 8/18/15.
 */
public interface OnPickUpActivityAction {
    public void openCouponView(SearchDriversData driver);
    public void close();
    public void ride(boolean isCheckedTermsAndConditions, SearchDriversData driver);
    public void updateActionBarTitle(String title);
    public ArrayList<View> updateFareDetailsItem(SearchDriversData driver);
    public SearchDriversData applyCouponToDriver(SearchDriversData driver);
    public VerfiyCouponData getCouponData();
    public Boolean isValidCouponCode();
    public void showDetailPickUpForLater(SearchDriversData driver);
    public void showListPickUpForLater();
}
