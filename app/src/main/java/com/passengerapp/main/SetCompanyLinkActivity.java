package com.passengerapp.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.model.http.responses.JsonResponse;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.SetCompanyLinkRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.SetCompanyLinkData;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.regex.Matcher;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SetCompanyLinkActivity extends PassengerBaseActivity implements
		OnClickListener, AlertDailogView.OnCustPopUpDialogButoonClickListener {

	private EditText edtCompLinkEmail;
	private EditText edtCompLinkName;
	private EditText edtCompLinkNo;
	private EditText edtCompLinkVipCode;
	private TextView txtCompLinkNext;
	private Button btnBackSetCompLink;
	private DriverViewModel viewModel;
	private SetCompanyLinkRequest companyLinkData;
	private JsonResponse<SetCompanyLinkData> setCompanyresponce;
	private Matcher macher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setcompanylink);

		viewModel = new DriverViewModel();

		edtCompLinkEmail = (EditText) findViewById(R.id.edtCompLinkEmail);
		edtCompLinkName = (EditText) findViewById(R.id.edtCompLinkName);
		edtCompLinkNo = (EditText) findViewById(R.id.edtCompLinkNo);
		edtCompLinkVipCode = (EditText) findViewById(R.id.edtCompLinkVipCode);
        edtCompLinkVipCode.setEnabled(false);
 //       edtCompLinkVipCode.setText(Const.VIP_DEFAULT);

		btnBackSetCompLink = (Button) findViewById(R.id.btnBackSetCompLink);
		txtCompLinkNext = (TextView) findViewById(R.id.txtCompLinkNext);

		txtCompLinkNext.setOnClickListener(this);
		btnBackSetCompLink.setOnClickListener(this);

        String regid = StorageDataHelper.getInstance(this).getPhoneToken();
        // regid = "reply";
        if ((regid == null) || regid.equals("")) {

            registerReceiver(OnGCMRegistrationFinish, new IntentFilter(
                    CommonUtilities.REGISTRATION_FINISH_ACTION));

            Intent registrationIntent = new Intent(
                    "com.google.android.c2dm.intent.REGISTER");
            // sets the app name in the intent
            registrationIntent.putExtra("app",
                    PendingIntent.getBroadcast(this, 0, new Intent(), 0));
            registrationIntent.putExtra("sender", CommonUtilities.SENDER_ID);
            startService(registrationIntent);
        }
	}

    // GCM BrodCasrRegisteration UnRegister here when Registration Finish
    private BroadcastReceiver OnGCMRegistrationFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                unregisterReceiver(OnGCMRegistrationFinish);
            } catch (Exception e) {}

        }
    };

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(OnGCMRegistrationFinish);
        } catch (Exception e) {}

        super.onDestroy();
    }

	@Override
	public void onClick(View v) {

		int id = v.getId();
		if (id == R.id.txtCompLinkNext) {
			String result = null;
			result = validateData();
			if (result != null) {
				AlertDailogView.showAlert(SetCompanyLinkActivity.this, result)
						.show();
			} else {
				companyLinkData = new SetCompanyLinkRequest();
				companyLinkData.Email = edtCompLinkEmail.getText().toString()
						.trim();
				companyLinkData.Name = edtCompLinkName.getText().toString()
						.trim();
				companyLinkData.PhoneNumber = edtCompLinkNo.getText()
						.toString().trim();
				companyLinkData.UniqueID = Utils
						.getDeviceId(SetCompanyLinkActivity.this);
				companyLinkData.VipCode = edtCompLinkVipCode.getText()
						.toString().trim();
				companyLinkData.PhoneToken = StorageDataHelper.getInstance(this).getPhoneToken();
				requestSetCompanyLink();
			}
		} else if (id == R.id.btnBackSetCompLink) {
			/*startActivity(new Intent(SetCompanyLinkActivity.this,
					CompanyLinkActivity.class));*/
			finish();
		}
	}

	public void requestSetCompanyLink() {
		if(!isNetworkAvailable())
			return;


		DisplayProcessMessage(Utils.getStringById(R.string.companylink_setting_companylink));

		NetworkApi api = (new NetworkService()).getApi();
		api.setCompanyLink(companyLinkData)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonServerResponse<SetCompanyLinkData>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonServerResponse<SetCompanyLinkData> setCompanyLinkDataJsonServerResponse) {
						if (setCompanyLinkDataJsonServerResponse.IsSuccess) {
							StorageDataHelper.getInstance(SetCompanyLinkActivity.this).setCompanyLinkData(setCompanyLinkDataJsonServerResponse.Content);

							try {
								if (setCompanyLinkDataJsonServerResponse.Content.DefaultSearchRadius != 0) {
									Const.DefaultSearchRadius = setCompanyLinkDataJsonServerResponse.Content.DefaultSearchRadius;
								}
							} catch (Exception e) {}



							Pref.setValue(SetCompanyLinkActivity.this,
									Const.SET_COMPANYLINK_EMAIL, edtCompLinkEmail
											.getText().toString().trim());
							Pref.setValue(SetCompanyLinkActivity.this,
									Const.SET_COMPANYLINK_PHONENO, edtCompLinkNo
											.getText().toString().trim());
							Pref.setValue(SetCompanyLinkActivity.this,
									Const.SET_COMPANYLINK_NAME, edtCompLinkName
											.getText().toString().trim());
							AlertDailogView
									.showAlert(
											SetCompanyLinkActivity.this,
											Utils.getStringById(R.string.common_alert),
											Utils.getStringById(R.string.companylink_congrat_vip_registred),
											Utils.getStringById(R.string.common_OK), SetCompanyLinkActivity.this).show();
						} else {
							AlertDailogView
									.showAlert(SetCompanyLinkActivity.this,
											Utils.getStringById(R.string.companylink_vipcode_not_valid))
									.show();
						}

					}

				});
	}

	private String validateData() {

		String result = null;
		if (edtCompLinkName.getText().toString().trim() == null
				|| edtCompLinkName.getText().toString().trim().equals("")) {
			result = Utils.getStringById(R.string.companylink_please_provide_name);
		} else if (edtCompLinkEmail.getText().toString().trim() == null
				|| edtCompLinkEmail.getText().toString().trim().equals("")) {
			result = Utils.getStringById(R.string.companylink_please_provide_email);
		} else if (edtCompLinkEmail.getText().toString().trim() != null
				|| edtCompLinkEmail.getText().toString().trim().equals("")) {

			macher = Utils.EMAIL_ADDRESS_PATTERN.matcher(edtCompLinkEmail
					.getText().toString().trim());
			if (!macher.matches()) {
				result = Utils.getStringById(R.string.companylink_Invalid_Email_Address);
			}
		} else if (edtCompLinkNo.getText().toString().trim() == null
				|| edtCompLinkNo.getText().toString().trim().equals("")) {
			result = Utils.getStringById(R.string.companylink_Please_provide_your_phone_no);
		} else if (edtCompLinkVipCode.getText().toString().trim() == null
				|| edtCompLinkVipCode.getText().toString().trim().equals("")) {
			result = Utils.getStringById(R.string.companylink_Please_provide_your_VIP_code);
		}
		return result;
	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 1:
			startActivity(new Intent(SetCompanyLinkActivity.this,
					MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			finish();
			break;

		}
	}
}
