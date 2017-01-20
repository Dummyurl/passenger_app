package com.passengerapp.main.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseAppCompatActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.VerfiyCouponData;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by adventis on 8/4/15.
 */
public class EnterCouponActivity extends PassengerBaseAppCompatActivity {

    public static final String MERCHANT_ID_INTENT = "merchantIdValue";
    public static final String COUPON_VALID_NAME = "couponValidName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_coupon);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.activity_enter_coupon_actionbar);

        initUI();
        /*updateUI();*/
    }

    EditText couponEditText;
    TextView clearBtn;
    TextView doneBtn;
    String mMerchantID;

    private void initUI() {

        getSupportActionBar().getCustomView().findViewById(R.id.back_btn_actionbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.mMerchantID = getIntent().getStringExtra(MERCHANT_ID_INTENT);

        couponEditText = (EditText) findViewById(R.id.coupon_edittext);
        couponEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponEditText.setError(null);
            }
        });
        clearBtn = (TextView) findViewById(R.id.clear_text_invisible_button);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponEditText.setText("");
            }
        });
        doneBtn = (TextView) findViewById(R.id.done_coupone_btn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponEditText.getText().toString().length() == 0) {
                    couponEditText.setError(getResources().getString(R.string.pickup_enter_coupon_warn_msg));
                    return;
                }
                requestVerifyCoupon(mMerchantID, couponEditText.getText().toString());
            }
        });

    }

    public void requestVerifyCoupon(final String merchantID, final String couponCode) {

        if(!isNetworkAvailable())
            return;

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();
        api.sendVeridyCoupon(merchantID, couponCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<VerfiyCouponData>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<VerfiyCouponData> verfiyCouponData) {
                        if (verfiyCouponData.Content.IsValid) {
                            Intent result = new Intent();
                            verfiyCouponData.Content.CouponValue = couponEditText.getText().toString().trim();
                            verfiyCouponData.Content.MerchantId = mMerchantID;
                            result.putExtra(Const.COUPON_VALID_NAME, verfiyCouponData.Content);
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            couponEditText.setError(verfiyCouponData.Message);
                        }

                    }
                });
    }

    /*public void requestVerifyCoupon(String merchantID, String couponCode) {
        if (!WebInterface.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(
                    getApplicationContext(),
                    Utils.getStringById(R.string.common_no_internet_connections_after_smth),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        Call<JsonServerResponse<VerfiyCouponData>> response =  new NetworkService().getApi().sendVeridyCoupon(merchantID, couponCode);
        response.enqueue(new Callback<JsonServerResponse<VerfiyCouponData>>() {
            @Override
            public void onResponse(Response<JsonServerResponse<VerfiyCouponData>> response, Retrofit retrofit) {
                DisplayProcessMessage(false);

                JsonServerResponse<VerfiyCouponData> data = response.body();

                if (data.Content.IsValid) {
                    Intent result = new Intent();
                    data.Content.CouponValue = couponEditText.getText().toString().trim();
                    data.Content.MerchantId = mMerchantID;
                    //result.putExtra(Const.COUPON_VALID_NAME, data.Content);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    couponEditText.setError(data.Message);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                DisplayProcessMessage(false);
            }
        });
    }*/


    /*private DriverViewModel viewModel;
    private JsonResponse<HttpVerifyCouponData> couponData;
    public void requestVerifyCoupon(final HttpVerifyCoupon requestCouponData) {
        viewModel = new DriverViewModel();
        if (!WebInterface.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(
                    getApplicationContext(),
                    Utils.getStringById(R.string.common_no_internet_connections_after_smth),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        new Thread() {
            public void run() {
                couponData = viewModel.sendVerifyCoupon(requestCouponData);

                if (couponData != null) {
                    DisplayProcessMessage(false);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = 0;
                    handlerWeb.sendMessage(msg);
                    this.interrupt();
                    return;
                } else {
                    DisplayProcessMessage(false);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = 1;
                    handlerWeb.sendMessage(msg);
                    this.interrupt();
                    return;
                }
            }
        }.start();
    }*/



    /*@SuppressLint("HandlerLeak")
    private Handler handlerWeb = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 ) {
                if(msg.arg1 == 0) {
                    try {
                        if (couponData.Content.IsValid) {
                            Intent result = new Intent();
                            couponData.Content.CouponValue = couponEditText.getText().toString().trim();
                            couponData.Content.MerchantId = merchantID;
                            result.putExtra(Const.COUPON_VALID_NAME, couponData.Content);
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            couponEditText.setError(couponData.Message);
                        }
                    } catch(Exception e) {
                        couponEditText.setError(couponData.Message);
                    }
                }
            }
        }
    };*/
}
