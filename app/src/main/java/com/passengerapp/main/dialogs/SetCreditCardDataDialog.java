package com.passengerapp.main.dialogs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.services.rest.HereApprovedDataServices;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.uc.WheelActivity;
import com.passengerapp.main.uc.WheelDateActivity;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.CreditCardValidator;
import com.passengerapp.util.Pref;
import com.passengerapp.util.Utils;

public class SetCreditCardDataDialog extends PassengerBaseActivity implements
		OnClickListener, AlertDailogView.OnCustPopUpDialogButoonClickListener {

	private EditText edtCardHlerFirstName;
	//private EditText edtCardHlerLastName;
	private TextView txtCardType;
	private EditText edtCardNumber;
	private EditText txtCardExprDate;
	private EditText edtCardSecurCode;
	private TextView txtCardSubmit;
	private Button btnCloseCrdtCard;
	private DriverViewModel viewModel;
	private String expireApiPassDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_creditcard_details);

		edtCardHlerFirstName = (EditText) findViewById(R.id.edtCardHlerFirstName);
		//edtCardHlerLastName = (EditText) findViewById(R.id.edtCardHlerLastName);
		txtCardType = (TextView) findViewById(R.id.txtCardType);
		edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
		txtCardExprDate = (EditText) findViewById(R.id.txtCardExprDate);
		edtCardSecurCode = (EditText) findViewById(R.id.edtCardSecurCode);
		txtCardSubmit = (TextView) findViewById(R.id.txtCardSubmit);
		btnCloseCrdtCard = (Button) findViewById(R.id.btnCloseCrdtCard);

		viewModel = new DriverViewModel();

		txtCardSubmit.setOnClickListener(this);
		btnCloseCrdtCard.setOnClickListener(this);

		txtCardType.setOnClickListener(this);
		txtCardExprDate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.txtCardSubmit) {
			String result = null;
			result = validateData();
			if (result != null) {
				AlertDailogView.showAlert(SetCreditCardDataDialog.this, result)
						.show();
			} else {
				requestEnrollCustomer();
			}
		} else if (id == R.id.btnCloseCrdtCard) {
			finish();
		} else if (id == R.id.txtCardExprDate) {
			String currentDate = Utils.millisToDate(System.currentTimeMillis(), "dd-MM-yyyy");
			String date[] = currentDate.split("-");
			Intent openWheel1 = new Intent(SetCreditCardDataDialog.this,
					WheelDateActivity.class);
			openWheel1.putExtra("selected-date", date[0]);
			openWheel1.putExtra("selected-month", date[1]);
			openWheel1.putExtra("selected-year", date[2]);
			startActivityForResult(openWheel1, 2);
		} else if (id == R.id.txtCardType) {
			String[] cardType = { "Visa", "Master Card", "Discover", "American Express" };
			Intent openWheel = new Intent(SetCreditCardDataDialog.this, WheelActivity.class);
			openWheel.putExtra("content_array", cardType);
			openWheel.putExtra("selected_value", txtCardType.getText().toString());
			startActivityForResult(openWheel, 1);
		}

	}

	public void requestEnrollCustomer() {
		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));


		new Thread() {
			public void run() {

				String paymentId = HereApprovedDataServices
						.getDataServiceObject(SetCreditCardDataDialog.this)
						.EnrollCustomer(
								edtCardHlerFirstName.getText().toString().trim(),
								//edtCardHlerLastName.getText().toString().trim(),
								edtCardNumber.getText().toString().trim(),
								expireApiPassDate.toString().trim(),
								txtCardType.getText().toString().trim(),
								Pref.getValue(SetCreditCardDataDialog.this, Const.MERCHANT_ID, ""));

                Log.d(".", "paymentId + " + paymentId);
				
				if (paymentId != null && Utils.isInteger(paymentId) && !paymentId.equals("0")) {

					DisplayProcessMessage(false);

					Pref.setValue(SetCreditCardDataDialog.this, Const.CC_PAYMENT_ID,paymentId + "");
					Pref.setValue(SetCreditCardDataDialog.this, Const.CC_NUMBER, edtCardNumber.getText().toString().trim());

					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = 0;
					handler.sendMessage(msg);
					this.interrupt();
					return;

				} else {
					DisplayProcessMessage(false);
					Message msg = new Message();
					msg.what = 1;
					msg.arg1 = 1;
					handler.sendMessage(msg);
					this.interrupt();
					return;

				}

			}
		}.start();
	}

	/*public void requestStorePaymentId() {

		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));


		final NetworkApi api = (new NetworkService()).getApi();
		api.sen(request)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonServerResponse<RegisterPassengerData>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonServerResponse<RegisterPassengerData> registerPassengerDataJsonServerResponse) {
						if (registerPassengerDataJsonServerResponse.IsSuccess) {
							Pref.setValue(
									RegisterPassengerActivity.this,
									Const.PASSENGER_NAME,
									edtPassengerName.getText().toString().trim());
							Const.Name = edtPassengerName.getText().toString().trim();
							Pref.setValue(
									RegisterPassengerActivity.this,
									Const.PASSENGER_NO, edtPhoneNo.getText().toString().trim());
							Pref.setValue(
									RegisterPassengerActivity.this,
									Const.PASSENGER_EMAIL, edtEmailAdd.getText().toString().trim());
							Pref.setValue(
									RegisterPassengerActivity.this,
									Const.REGI_PASSENEGRID,
									registerPassengerDataJsonServerResponse.Content.PassengerID + "");

							sendPickUpRequest();
						}
					}
				});

		new Thread() {
			public void run() {

				JsonResponse<RegisterPassData> storeData = viewModel
						.StorePaymentID(Integer.parseInt(Pref.getValue(
								SetCreditCardDataDialog.this,
								Const.REGI_PASSENEGRID, "0")), Integer
								.parseInt(Pref.getValue(
										SetCreditCardDataDialog.this,
										Const.CC_PAYMENT_ID, "0")));

				if (storeData.IsSuccess) {

					DisplayProcessMessage(false);

					Pref.setValue(SetCreditCardDataDialog.this,
							Const.CC_PAYMENT_ID, storeData.Content.PaymentID
									+ "");

					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = 0;
					handler.sendMessage(msg);
					this.interrupt();
					return;

				}

				else {
					DisplayProcessMessage(false);
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = 1;
					handler.sendMessage(msg);
					this.interrupt();
					return;

				}

			}
		}.start();
	}*/

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (msg.arg1 == 0) {
					AlertDailogView.showAlert(SetCreditCardDataDialog.this,
							Utils.getStringById(R.string.alert), Utils.getStringById(R.string.set_credit_card_dialog_card_was_added),
							Utils.getStringById(R.string.common_OK), SetCreditCardDataDialog.this).show();
				} else {
					AlertDailogView.showAlert(SetCreditCardDataDialog.this,
							Utils.getStringById(R.string.set_credit_card_dialog_card_not_accepted),
							Utils.getStringById(R.string.common_OK), true, Utils.getStringById(R.string.common_cancel), SetCreditCardDataDialog.this,
							2).show();
				}
			}
		}
	};
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK) { // success
			txtCardExprDate.setText(data.getStringExtra("selected-month") + "/"
					+ data.getStringExtra("selected-date") + "/"
					+ data.getStringExtra("selected-year"));
			expireApiPassDate = data.getStringExtra("selected-year")+ "" +data.getStringExtra("selected-month");
		} else if (requestCode == 1 && resultCode == RESULT_OK) { // success
			txtCardType.setText(data.getStringExtra("selected_value"));

		}
	}

	private String validateData() {

		String result = null;
		if (edtCardHlerFirstName.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.set_credit_card_dialog_provide_firstname);
		/*else if (edtCardHlerLastName.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.set_credit_card_dialog_provide_lastname);*/
		else if ( txtCardType.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.set_credit_card_dialog_provide_cardtype);
		else if ( edtCardNumber.getText().toString().trim().equals("")) {
			if (txtCardType.getText().toString().trim().equals("Visa")) {
				if (!CreditCardValidator.validate(edtCardNumber.getText()
						.toString().trim(), CreditCardValidator.VISA)) {
					result = Utils.getStringById(R.string.set_credit_card_dialog_invalid_card_visa);
				}
			} else if (txtCardType.getText().toString().trim()
					.equals("Master Card")) {
				if (!CreditCardValidator.validate(edtCardNumber.getText()
						.toString().trim(), CreditCardValidator.MASTERCARD)) {
					result = Utils.getStringById(R.string.set_credit_card_dialog_invalid_card_master);
				}
			} else if (txtCardType.getText().toString().trim()
					.equals("Discover")) {
				if (!CreditCardValidator.validate(edtCardNumber.getText()
						.toString().trim(), CreditCardValidator.DISCOVER)) {
					result = Utils.getStringById(R.string.set_credit_card_dialog_invalid_card_discover);
				}
			} else if (txtCardType.getText().toString().trim()
					.equals("American Express")) {
				if (!CreditCardValidator.validate(edtCardNumber.getText()
						.toString().trim(), CreditCardValidator.AMEX)) {
					result = Utils.getStringById(R.string.set_credit_card_dialog_invalid_card_american);
				}
			} else {
				result = Utils.getStringById(R.string.set_credit_card_dialog_invalid_card);
			}

		}
		return result;
	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 2:
			if (buttonIndex == AlertDailogView.BUTTON_CANCEL) {
				finish();
			}
			break;
		case 1:

			finish();

			break;

		}
	}

}
