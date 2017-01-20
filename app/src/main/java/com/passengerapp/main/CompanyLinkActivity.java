package com.passengerapp.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.StorageDataHelper;

public class CompanyLinkActivity extends Activity implements OnClickListener {

	private LinearLayout llCmpLnkYes;
	private LinearLayout llCmpLnkNo;
	private TextView llCmpLnkReset;
	private DriverViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_companylink);

		viewModel = new DriverViewModel();

		llCmpLnkYes = (LinearLayout) findViewById(R.id.llCmpLnkYes);
		llCmpLnkNo = (LinearLayout) findViewById(R.id.llCmpLnkNo);
		llCmpLnkReset = (TextView) findViewById(R.id.llCmpLnkReset);
		llCmpLnkYes.setOnClickListener(this);
		llCmpLnkNo.setOnClickListener(this);
		llCmpLnkReset.setOnClickListener(this);

		String regid = StorageDataHelper.getInstance(this).getPhoneToken();
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

	// unRegister GCM
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
		if (id == R.id.llCmpLnkYes) {
			startActivity(new Intent(CompanyLinkActivity.this,
					SetCompanyLinkActivity.class));
			finish();
		} else if (id == R.id.llCmpLnkNo) {
			//requestResetCompanyLink();
		} else if (id == R.id.llCmpLnkReset) {}
	}

	private ProgressDialog progressDialog = null;

	private void showProcessingDialog(String msg) {
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", msg, true, false);
	}

	private void hideProcessingDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void DisplayProcessMessage(final String msg) {
		showProcessingDialog(msg);
	}

	public void DisplayProcessMessage(final boolean hide) {

		hideProcessingDialog();

	}
}
