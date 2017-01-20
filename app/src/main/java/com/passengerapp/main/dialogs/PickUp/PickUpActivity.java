package com.passengerapp.main.dialogs.PickUp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.FlightActivity;
import com.passengerapp.main.dialogs.EnterCouponActivity;
import com.passengerapp.main.dialogs.RegisterPassengerActivity;
import com.passengerapp.main.fragments.MyQuotesFragment;
import com.passengerapp.main.network.model.data.FareDetail;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.VerfiyCouponData;
import com.passengerapp.main.services.OnImageLoadedListener;
import com.passengerapp.main.widget.TermsAndCondition.OnShowTermsAndCondition;
import com.passengerapp.util.Const;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adventis on 8/3/15.
 */
public class PickUpActivity extends ActionBarActivity implements OnImageLoadedListener, OnPickUpActivityAction, OnShowTermsAndCondition {
    public static final int ENTER_COUPON_ACTIVITY = 1;
    public static final int ENTER_FLIGHT_DETAIL_ACTIVITY = 2;
    private SearchDriversData searchdriverdata = new SearchDriversData();
    private List<SearchDriversData> searchDriversData = null;
    private VerfiyCouponData couponCode;
    private Fragment currentFragment;

    private final String DETAIL_PICKUP_LATER_TAG = "detailPickupLaterTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pickup_container);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_pickup_actionbar);

        searchdriverdata = (SearchDriversData)getIntent().getSerializableExtra(Const.SEARCH_DRIVER_DATA);
        searchDriversData = (List<SearchDriversData>)getIntent().getSerializableExtra(Const.SEARCH_DRIVERS_DATA);

        if(Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_NOW)) {
            currentFragment = new PickUpNowFragment();
        } else if(Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_LATER) && searchdriverdata!=null) {
            currentFragment = new PickUpLaterFragment();
        } else if(Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_LATER) && searchDriversData!=null) {
            currentFragment = new PickUpLaterListFragment();
        }
        currentFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().add(R.id.pickup_fragment_container, currentFragment).commit();
    }

    private PickUpLaterListFragment tempPickUpLaterListFragment;
    public void showDetailPickUpForLater(SearchDriversData driver) {
        tempPickUpLaterListFragment = (PickUpLaterListFragment)currentFragment;
        PickUpLaterFragment fragment = new PickUpLaterFragment();
        fragment.setDriver(driver);
        currentFragment = fragment;
        currentFragment.setArguments(getIntent().getExtras());

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.pickup_fragment_container, currentFragment, DETAIL_PICKUP_LATER_TAG);
        transaction.addToBackStack(null);
        transaction.commit();

        //getSupportFragmentManager().beginTransaction().add(R.id.pickup_fragment_container, currentFragment, DETAIL_PICKUP_LATER_TAG).commit();
    }

    public void showListPickUpForLater() {
        currentFragment = tempPickUpLaterListFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.pickup_fragment_container, currentFragment).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DETAIL_PICKUP_LATER_TAG);
        if(fragment != null) {
            showListPickUpForLater();
        } else {
            super.onBackPressed();
        }
    }

    public void updateActionBarTitle(String title) {
        TextView titleView = (TextView)getSupportActionBar().getCustomView().findViewById(R.id.actionbar_title);
        titleView.setText(title);
    }


    public boolean requestFlightDetailed() {
        if (((Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_NOW) && StorageDataHelper.getInstance(this).getIsAirportFromGetDriversDatasNow())
                || (Const.bookingTime.equalsIgnoreCase(Const.TIME_LABEL_FOR_PICKUP_LATER) && StorageDataHelper.getInstance(this).getIsAirportFromDriversDatasLater()))) {
            Intent intent = new Intent(PickUpActivity.this, FlightActivity.class);
            intent.putExtra(Const.SEARCH_DRIVER_DATA, searchdriverdata);
            startActivityForResult(intent, ENTER_FLIGHT_DETAIL_ACTIVITY);
            return true;
        }

        return false;
    }

    public void requestSendPickUp(boolean isCancelFlight) {
        Const.DRIVER_TOKEN = searchdriverdata.Driver.token;
        Pref.setValue(PickUpActivity.this, Const.MERCHANT_ID, searchdriverdata.MerchantId + "");

        Intent intent = new Intent(PickUpActivity.this, RegisterPassengerActivity.class);
        intent.putExtra(Const.SEARCH_DRIVER_DATA, searchdriverdata);
        intent.putExtra(Const.COUPON_CODE_INTENT_PARAM, (couponCode == null ? "" : couponCode.CouponValue));
        intent.putExtra(Const.FLIGHT_DETAIL_HAS_TEMP_FLIGHT_ID, isCancelFlight);
        startActivityForResult(intent, MyQuotesFragment.SEND_PICKUP_REQUEST);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENTER_COUPON_ACTIVITY && resultCode == RESULT_OK) {
            couponCode = (VerfiyCouponData)data.getSerializableExtra(EnterCouponActivity.COUPON_VALID_NAME);

            if(isValidCouponCode()) {
                ((OnPickUpFragmentListener) currentFragment).updateCoupon();
            }
        }

        if (requestCode == MyQuotesFragment.SEND_PICKUP_REQUEST && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }

        if(requestCode == ENTER_FLIGHT_DETAIL_ACTIVITY && resultCode == RESULT_OK) {
            if(data == null) {
                requestSendPickUp(true);
            } else {
                flightDataToSave = (SaveFlightDetailRequest) data.getSerializableExtra(Const.FLIGHT_DETAIL_TO_SEND_AFTER_PICKUP_REQUEST);
                if(data.getBooleanExtra(Const.FLIGHT_DETAIL_HAS_TEMP_FLIGHT_ID, false)) {
                    requestSendPickUp(false);
                } else {
                    requestSendPickUp(true);
                }
                /*if (flightDataToSave == null) {
                    requestSendPickUp(true);
                } else {
                    requestSendPickUp(false);
                }*/
            }
        }
    }

    SaveFlightDetailRequest flightDataToSave = null;

    public Boolean isValidCouponCode() {
        return  couponCode != null && couponCode.CouponValue!=null && couponCode.IsValid;
    }

    public VerfiyCouponData getCouponData() {
        return couponCode;
    }

    public void showTermsAndConditions() {
        JSONObject termsAndCondition = StorageDataHelper.getInstance(this).getTermsAndConditions();
        if (termsAndCondition == null || !termsAndCondition.keys().hasNext()) {
            return;
        }

        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle(Utils.getStringById(R.string.pick_up_taxi_dialog_terms_and_condition));
        TextView msg = new TextView(this);

        //String[] titleArray = {Utils.getStringById(R.string.pick_up_taxi_dialog_cancel_fees_sedan), Utils.getStringById(R.string.pick_up_taxi_dialog_cancel_fees_limo), Utils.getStringById(R.string.pick_up_taxi_dialog_no_show_charge), Utils.getStringById(R.string.pick_up_taxi_dialog_other)};
        String msgString = "";

        Iterator<String> iter = termsAndCondition.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONArray tempArray =  termsAndCondition.getJSONArray(key);
                for (int j = 0; j < tempArray.length(); j++) {
                    msgString += tempArray.getString(j) + "<br>";
                }
                msgString += "<br>";
            } catch (JSONException e) {
                // Something went wrong!
            }
        }

        msg.setText(Html.fromHtml(msgString));
        int padding = Utils.convertToDp(this, 10);
        msg.setPadding(padding,padding,padding,padding);
        ab.setView(msg);
        ab.setPositiveButton(R.string.common_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();
    }

    public void openCouponView(SearchDriversData driver) {
        startActivityForResult(new Intent(PickUpActivity.this, EnterCouponActivity.class)
                .putExtra(EnterCouponActivity.MERCHANT_ID_INTENT, driver.MerchantId), ENTER_COUPON_ACTIVITY);
    }

    public void ride(boolean isCheckedTermsAndConditions, SearchDriversData driver) {
        if(driver != null) {
            searchdriverdata = driver;
        }

        JSONObject termsAndCondition = StorageDataHelper.getInstance(this).getTermsAndConditions();
        if (!isCheckedTermsAndConditions && (termsAndCondition != null && termsAndCondition.keys().hasNext())) {
            showTermsAndConditionDialogWithChoose();
            return;
        }

        if(!requestFlightDetailed()) {
            requestSendPickUp(false);
        }
    }

    public void showTermsAndConditionDialogWithChoose() {

        final Dialog dlg = new Dialog(this);
        dlg.setContentView(R.layout.activity_pickup_dialog_accept_terms);
        dlg.setTitle(R.string.pick_up_taxi_dialog_terms_and_condition);

        TextView txtView = (TextView)dlg.findViewById(R.id.pickup_terms_main_text);
        String textToShow = getResources().getString(R.string.pickup_activity_terms_and_conditions_warn_msg_choose);
        int startSpannable = textToShow.indexOf('[');
        int endSpannable = textToShow.indexOf(']');
        textToShow = textToShow.replace("[", "");
        textToShow = textToShow.replace("]", "");
        SpannableString ss = new SpannableString(textToShow);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                showTermsAndConditions();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, startSpannable-1, endSpannable-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtView.setText(ss);
        txtView.setMovementMethod(LinkMovementMethod.getInstance());
        txtView.setHighlightColor(Color.TRANSPARENT);

        TextView cancelBtn = (TextView)dlg.findViewById(R.id.pickup_terms_dialog_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });
        TextView acceptBtn = (TextView)dlg.findViewById(R.id.pickup_terms_dialog_accept_btn);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ride(true, null);
            }
        });

        dlg.show();
    }

    public SearchDriversData applyCouponToDriver(SearchDriversData driver) {
        if(isValidCouponCode()) {
            FareDetail fareDetail = new FareDetail();
            if (couponCode.DiscountType.equalsIgnoreCase("percentage")) {
                fareDetail.FeeDescription = fareDetail.FeeCode = (int) (couponCode.Amount * 100) + "% " + getResources().getString(R.string.enter_coupon_activity_discount);
                if(driver.FareDetails != null) {
                    for(FareDetail tmpFareDetail : driver.FareDetails) {
                        if(tmpFareDetail.FeeCode.equalsIgnoreCase("EF")) {
                            fareDetail.Amount = (-1) * (couponCode.Amount * tmpFareDetail.Amount);
                            break;
                        }
                    }
                }
            } else {
                fareDetail.FeeDescription = getResources().getString(R.string.enter_coupon_activity_discount);
                fareDetail.Amount = (-1) * couponCode.Amount;
            }

            driver.Fare = driver.Fare + fareDetail.Amount;

            if(driver.FareDetails == null) {
                driver.FareDetails = new ArrayList<FareDetail>();
            }
            driver.FareDetails.add(fareDetail);
        }

        return driver;
    }

    public ArrayList<View> updateFareDetailsItem(SearchDriversData driver) {
        ArrayList<View> listOfElements = new ArrayList<View>();
        if(driver.FareDetails == null) {
            driver.FareDetails = new ArrayList<FareDetail>();
        }

            for (int i=0; i < driver.FareDetails.size(); i++) {
                View fareItem = getLayoutInflater().inflate(R.layout.activity_pickup_dare_details_item, null);

                TextView fareDetails = (TextView) fareItem.findViewById(R.id.fare_description);
                TextView fareValue = (TextView) fareItem.findViewById(R.id.fare_value);

                fareDetails.setText(driver.FareDetails.get(i).FeeDescription+":");
                if(driver.FareDetails.get(i).Amount < 0) {
                    fareValue.setText("($" + Utils.modifyFare(driver.FareDetails.get(i).Amount*(-1)) + ")");
                } else {
                    fareValue.setText("$" + Utils.modifyFare(driver.FareDetails.get(i).Amount));
                }
                if(i == driver.FareDetails.size() - 1) {
                    fareValue.setPaintFlags(fareValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }

                listOfElements.add(fareItem);
            }
        return  listOfElements;
    }

    public void close(){
        finish();
    }

    @Override
    public void imageAsyncLoaded(String url, Bitmap bmp) {

    }
}


