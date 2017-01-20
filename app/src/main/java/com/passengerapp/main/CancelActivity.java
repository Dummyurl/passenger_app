package com.passengerapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.SendCancelToDriverRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CancelActivity extends PassengerBaseActivity implements AlertDailogView.OnCustPopUpDialogButoonClickListener {

    private InputMethodManager imm;
    private SearchDriversData searchData;
    private boolean flagFrom;
    int reservationIdToCancelDriver;
    String driverPhoneNumber = "";


    @Bind(R.id.confirm_cancel)
    ImageView confirmCancel;
    @Bind(R.id.close_cancel)
    ImageView closeCancel;
    @Bind(R.id.rlMainCancel)
    RelativeLayout rlMainCancel;
    @Bind(R.id.edtReason)
    EditText edtReason;
    @Bind(R.id.txtCancelMsg)
    TextView txtCancelMsg;
    @Bind(R.id.txtCnclDvrPhnNo)
    TextView txtCnclDvrPhnNo;


    @OnClick(R.id.confirm_cancel)
    public void clickCancelBtn() {
        if (edtReason.getText().toString().trim() != null && !edtReason.getText().toString().trim().equals("")) {
            sendCancelToDriver(edtReason.getText().toString().trim(), 2);
        } else {
            AlertDailogView.showAlert(CancelActivity.this, getResources().getString(R.string.cancel_activity_wrg_msg_enter_reason), getResources().getString(R.string.common_OK)).show();
        }
    }

    @OnClick(R.id.close_cancel)
    public void closeDialogBtn() {
        finish();
    }

    @OnClick(R.id.rlMainCancel)
    public void layoutClick() {
        if (this.imm != null) {
            this.imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);
        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        ButterKnife.bind(this);

        reservationIdToCancelDriver = getIntent().getIntExtra(Const.RESERVATION_ID_TO_CANCEL_DRIVER, -1);
        driverPhoneNumber = getIntent().getStringExtra(Const.DRIVER_PHONE_NUMBER_TO_CANCEL_DRIVER);
        flagFrom = getIntent().getBooleanExtra(Const.CANCEL_TO_DRIVER_IMMEDIATELY, false);

        initUI();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    public void initUI() {
        txtCnclDvrPhnNo.setText(driverPhoneNumber);

        if (flagFrom) {
            sendCancelToDriver(getResources().getString(R.string.cancel_activity_wrg_msg_driver_not_needed), 1);
        }

    }

    private void sendCancelToDriver(final String msg, final int resultCode) {
        if (!isNetworkAvailable())
            return;

        SendCancelToDriverRequest request = new SendCancelToDriverRequest();
        request.ReservationID = reservationIdToCancelDriver;
        request.Reason = msg;
        request.CancelConfirmation = false;

		/*cancelData.DriverToken = driverToken;
        cancelData.PassengerID = Const.deviceId;
		cancelData.Reason = reason;

		cancelData.Reply = getResources().getString(R.string.common_Decline);*/

        DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

        NetworkApi api = (new NetworkService()).getApi();
        api.sendCancelToDriver(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonServerResponse<String>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonServerResponse<String> stringJsonServerResponse) {
                        if (stringJsonServerResponse.Content != null) {
                            float chargeAmount = stringJsonServerResponse.ChargeAmount;
                            boolean isDeposite = stringJsonServerResponse.IsDeposit;

                            if (resultCode == 1) {
                                String reasonText = "";
                                if (chargeAmount == 0 && isDeposite == false) {
                                    reasonText = "Cancelling after driver has accepted may give driver reason to give you a bad rating. Please call to explain.";
                                } else if (chargeAmount > 0 && isDeposite == false) {
                                    reasonText = "A cancellation penalty of $" + chargeAmount + " will be charged to your stored credit card.";
                                } else if (chargeAmount > 0 && isDeposite == true) {
                                    reasonText = "Your deposite of $" + chargeAmount + " will be forfeited if you cancel.";
                                }

                                AlertDailogView.showAlert(CancelActivity.this, reasonText, getResources().getString(R.string.common_OK)).show();

                            } else if (resultCode == 2) {

                                if (!flagFrom) {
                                    Intent broadcast = new Intent();
                                    broadcast.setAction(CommonUtilities.PROGRESSDIALOGDISMISS_ACTION);
                                    sendBroadcast(broadcast);
                                    finish();
                                } else {
                                    AlertDailogView.showAlert(CancelActivity.this, "Your reservation has been cancelled. A receipt has been mailed to you. \n\n We are sorry that we did not have the opportunity to serve you this time, but keep us mind for your next trip.", "Ok", CancelActivity.this).show();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void OnButtonClick(int tag, int buttonIndex) {
        finish();
    }
}
