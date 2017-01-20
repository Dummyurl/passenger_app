package com.passengerapp.main.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.data.SearchDriversTempData;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.model.http.data.PendingPickupData;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.data.DistanceData;
import com.passengerapp.main.network.model.data.LocationData;
import com.passengerapp.main.network.model.data.VehiclePropsData;
import com.passengerapp.main.network.model.request.RegisterPassengerRequest;
import com.passengerapp.main.network.model.request.SaveFlightDetailRequest;
import com.passengerapp.main.network.model.request.SendPickupRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.RegisterPassengerData;
import com.passengerapp.main.network.model.response.SaveFlightDetailData;
import com.passengerapp.main.network.model.response.SearchDriversData;
import com.passengerapp.main.network.model.response.SendPickupData;
import com.passengerapp.main.services.DriverTracker;
import com.passengerapp.main.services.backendPushService.BackendPushNotificationReceiver;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Pref;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.Calendar;
import java.util.regex.Matcher;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RegisterPassengerActivity extends PassengerBaseActivity implements
		OnClickListener, AlertDailogView.OnCustPopUpDialogButoonClickListener {

	private EditText edtPassengerName;
	private EditText edtPhoneNo;
	private ImageView close_rate;
	private ImageView confirm_rate;
	private EditText edtEmailAdd;
	private Matcher macher;

	private DriverViewModel driverViewModel;
	private SearchDriversData searchdriverdata;

	private Message msg;
	private String couponCode;
	private SaveFlightDetailRequest flightDataToSave;
	private boolean hasTempFlightDetail;

    private SearchDriversTempData searchDriversTempData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_send_pickuprequest);

		driverViewModel = new DriverViewModel();
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		searchdriverdata = (SearchDriversData) getIntent().getSerializableExtra(Const.SEARCH_DRIVER_DATA);
		couponCode =  getIntent().getStringExtra(Const.COUPON_CODE_INTENT_PARAM);
		flightDataToSave = (SaveFlightDetailRequest) getIntent().getSerializableExtra(Const.FLIGHT_DETAIL_TO_SEND_AFTER_PICKUP_REQUEST);
		hasTempFlightDetail = getIntent().getBooleanExtra(Const.FLIGHT_DETAIL_HAS_TEMP_FLIGHT_ID, false);

		Const.searchDriver = searchdriverdata;

        searchDriversTempData = StorageDataHelper.getInstance(this).getSearchDriversCurrentDataForRequest();
		initUI();
	}

	public void initUI() {

		edtPassengerName = (EditText) findViewById(R.id.edtPassengerName);

		edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
		edtPhoneNo
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		close_rate = (ImageView) findViewById(R.id.close_rate);
		confirm_rate = (ImageView) findViewById(R.id.confirm_rate);
		edtEmailAdd = (EditText) findViewById(R.id.edtEmailAdd);


        String registeredName = Pref.getValue(RegisterPassengerActivity.this, Const.SET_COMPANYLINK_NAME, "");
        String registeredNo = Pref.getValue(RegisterPassengerActivity.this, Const.SET_COMPANYLINK_PHONENO, "");
        String registeredEmail = Pref.getValue(RegisterPassengerActivity.this, Const.SET_COMPANYLINK_EMAIL, "");

		edtPassengerName.setText(Pref.getValue(RegisterPassengerActivity.this, Const.PASSENGER_NAME, registeredName));
		edtPhoneNo.setText(Pref.getValue(RegisterPassengerActivity.this, Const.PASSENGER_NO, registeredNo));
		edtEmailAdd.setText(Pref.getValue(RegisterPassengerActivity.this, Const.PASSENGER_EMAIL, registeredEmail));

		Const.Name = edtPassengerName.getText().toString().trim();

		confirm_rate.setOnClickListener(this);
		close_rate.setOnClickListener(this);

        boolean skipEnterAccInfo = true;
        if (edtPassengerName.getText().toString().trim() == null
                || edtPassengerName.getText().toString().trim().equals("")) {
            skipEnterAccInfo = false;
        }
        if (edtPhoneNo.getText().toString().trim() == null
                || edtPhoneNo.getText().toString().trim().equals("")) {
            skipEnterAccInfo = false;
        }
        if (edtEmailAdd.getText().toString().trim() == null
                || edtEmailAdd.getText().toString().trim().equals("")) {
            skipEnterAccInfo = false;
        }

		Boolean isAutomaticalySentRequest = getIntent().getBooleanExtra(Const.IS_CANCEL_FLIGHT_DATA_EXTRA_ID, false);
        if(skipEnterAccInfo && isAutomaticalySentRequest) {
			sendPickUpRequest();
		}
	}

	private String ValidationScreen() {

		String result = null;
		if (edtPassengerName.getText().toString().trim() == null
				|| edtPassengerName.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.register_passenger_provide_name);
		else if (edtPhoneNo.getText().toString().trim() == null
				|| edtPhoneNo.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.register_passenger_provide_phone);
		else if (edtEmailAdd.getText().toString().trim() == null
				|| edtEmailAdd.getText().toString().trim().equals(""))
			result = Utils.getStringById(R.string.register_passenger_provide_email);
		else if (edtEmailAdd.getText().toString().trim() != null
				|| edtEmailAdd.getText().toString().trim().equals("")) {

			macher = Utils.EMAIL_ADDRESS_PATTERN.matcher(edtEmailAdd.getText()
					.toString());
			if (!macher.matches()) {
				result = Utils.getStringById(R.string.register_passenger_invalid_email);
			}
		}
		return result;
	}

	@Override
	public void onClick(View v) {

		if (v == confirm_rate) {
			if (searchdriverdata != null) {
				if(searchdriverdata.AskForCCData) {
					if (Pref.getValue(RegisterPassengerActivity.this, Const.CC_PAYMENT_ID, "0").equals("0")) {
						AlertDailogView
								.showAlert(
										RegisterPassengerActivity.this,
										Utils.getStringById(R.string.register_passenger_provide_credit_card),
										Utils.getStringById(R.string.common_OK), true, Utils.getStringById(R.string.common_cancel),
										RegisterPassengerActivity.this, 1).show();

						return;
					}
				}

				String result = null;

				result = ValidationScreen();

				if (result != null) {
					AlertDailogView.showAlert(RegisterPassengerActivity.this, result).show();
				} else {

					if(!isNetworkAvailable())
						return;

					DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));


					RegisterPassengerRequest request = new RegisterPassengerRequest();
					request.PhoneNumber = edtPhoneNo.getText().toString().trim();
					request.Email = edtEmailAdd.getText().toString().trim();
					request.Name = edtPassengerName.getText().toString().trim();
					request.UniqueID = Const.deviceId;
					request.DevOS = "Android";
					request.PaymentID = Pref.getValue(RegisterPassengerActivity.this, Const.CC_PAYMENT_ID, "0");
					request.PhoneToken = StorageDataHelper.getInstance(getBaseContext()).getPhoneToken();
					//request.LastRequestWasCaptive;
					//request.LastRequestWasFarmed;

					final NetworkApi api = (new NetworkService()).getApi();
					api.registerPassenger(request)
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
									if(registerPassengerDataJsonServerResponse.IsSuccess) {
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
					}
				}
		}

		if (v == close_rate) {
			finish();
		}
	}

	public static int IS_ADDIDNG_FLIGHT_INFO = 1;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == IS_ADDIDNG_FLIGHT_INFO) {
			sendPickUpRequest();
		}
	}

	private SendPickupRequest prepeareRequestData() {
		SendPickupRequest sendPickData = new SendPickupRequest();

		sendPickData.TempFlightId = Const.flightId;
		Const.flightId = 0;
		sendPickData.DriverToken = searchdriverdata.Driver.token;
		sendPickData.TripType = searchDriversTempData.tripType;
		sendPickData.CarPool = false;
		sendPickData.UniqueID = Utils.getDeviceId(this);
		if(couponCode != null && !couponCode.isEmpty()) {
			sendPickData.CouponCode = couponCode;
		}
		sendPickData.MerchantID = searchdriverdata.MerchantId;

		sendPickData.Distance = new DistanceData();
		sendPickData.Distance.Unit = searchDriversTempData.replyPickupUnit;
		sendPickData.Distance.Value = searchDriversTempData.replyPickupValue;

		sendPickData.PhoneToken = StorageDataHelper.getInstance(getBaseContext()).getPhoneToken();

		sendPickData.PickupLocation = new LocationData();
		sendPickData.PickupLocation.latitude = String.valueOf(Const.srcLat);
		sendPickData.PickupLocation.longitude = String.valueOf(Const.srcLong);
		sendPickData.PickupLocation.location_name = Const.CURRENT;

		sendPickData.DestinationLocation = new LocationData();
		sendPickData.DestinationLocation.latitude = String.valueOf(Const.destLat);
		sendPickData.DestinationLocation.longitude = String.valueOf(Const.destLong);
		sendPickData.DestinationLocation.location_name = Const.DESTINATION;

		sendPickData.TimeOfPick = Integer.parseInt(Const.timeOfPic != null && !Const.timeOfPic.equals("") ? Const.timeOfPic : (System.currentTimeMillis() / 1000) + "");
		sendPickData.EstimateTime = (float)(searchDriversTempData.timeByHourMode * 0.000277778);
		sendPickData.NumberOfPassenger = searchDriversTempData.numberOfPassanger;
		sendPickData.EstimateFare =  (int)searchdriverdata.Fare;
		sendPickData.BookingTime = Const.bookingTime;
		if(Const.comment == null) {
			sendPickData.Comment = "OK";
		} else {
			sendPickData.Comment = Const.comment;
		}
		sendPickData.VehicleNumber = searchdriverdata.TaxiID;
		sendPickData.VehicleProps = new VehiclePropsData();
		sendPickData.VehicleProps.VehicleShape = searchdriverdata.VehicleShape;
		sendPickData.VehicleProps.Accepts = searchdriverdata.Accepts;

		return sendPickData;
	}

	private void sendPickUpRequest() {
		final SendPickupRequest requestData = prepeareRequestData();

		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

		final NetworkApi api = (new NetworkService()).getApi();
		api.sendPickup(requestData)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.map(new Func1<JsonServerResponse<SendPickupData>, PendingPickupData>() {
					@Override
					public PendingPickupData call(JsonServerResponse<SendPickupData> sendPickupDataJsonServerResponse) {
						if (sendPickupDataJsonServerResponse.IsSuccess) {
							DisplayMessage(sendPickupDataJsonServerResponse.Message);

							PendingPickupData pendingPickupData = new PendingPickupData();
							pendingPickupData.driverToken = searchdriverdata.Driver.token;
							pendingPickupData.createdDate = Calendar.getInstance().getTimeInMillis();
							pendingPickupData.reservationId = Integer.parseInt(sendPickupDataJsonServerResponse.Content.reservation_id);
							pendingPickupData.tripNumber = sendPickupDataJsonServerResponse.Content.TripNumber;


							Const.RESRAVTION_ID = sendPickupDataJsonServerResponse.Content.reservation_id;
							Const.TRIP_NUMBER = sendPickupDataJsonServerResponse.Content.TripNumber;
							//Const.vehicleStyle = searchdriverdata.VehicleShape.Vstyle;

							return pendingPickupData;
						} else {
							return null;
						}
					}
				}).flatMap(new Func1<PendingPickupData, Observable<PendingPickupData>>() {
			@Override
			public Observable<PendingPickupData> call(final PendingPickupData pickupData) {
				if (flightDataToSave != null) {
					flightDataToSave.reservation = pickupData.reservationId;
					flightDataToSave.unique_id = StorageDataHelper.getInstance(getBaseContext()).getPhoneToken();

					return api.saveFlightDetail(flightDataToSave)
							.subscribeOn(Schedulers.newThread())
							.observeOn(AndroidSchedulers.mainThread())
							.map(new Func1<JsonServerResponse<SaveFlightDetailData>, PendingPickupData>() {
								@Override
								public PendingPickupData call(JsonServerResponse<SaveFlightDetailData> saveFlightDetailDataJsonServerResponse) {
									return pickupData;
								}
							});
				}

				return Observable.just(pickupData);
			}
		}).subscribe(new Subscriber<PendingPickupData>() {
			@Override
			public void onCompleted() {
				DisplayProcessMessage(false);
			}

			@Override
			public void onError(Throwable e) {
				DisplayProcessMessage(false);
				AlertDailogView
						.showAlert(
								RegisterPassengerActivity.this,
								Utils.getStringById(R.string.register_passenger_vehicle_not_available),
								Utils.getStringById(R.string.common_OK), RegisterPassengerActivity.this, 4)
						.show();
			}

			@Override
			public void onNext(PendingPickupData pickupData) {
				if (pickupData == null) {
					onError(null);
					return;
				}

				if (Const.bookingTime != null && Const.bookingTime.equals(Const.TIME_LABEL_FOR_PICKUP_LATER)) {
					String specialId = Const.RESRAVTION_ID;
					if (Const.TRIP_NUMBER != null && !Const.TRIP_NUMBER.isEmpty()) {
						specialId = Const.TRIP_NUMBER;
					}

					String msgAlert = String.format(Utils.getStringById(R.string.register_passenger_request_for_later), specialId);

					AlertDailogView.showAlert(RegisterPassengerActivity.this,
							msgAlert,
							Utils.getStringById(R.string.common_OK),
							RegisterPassengerActivity.this, 2).show();
				} else {

					StorageDataHelper.getInstance(getBaseContext()).setPendingPickupStatus(pickupData);

					Intent broadcast = new Intent().setAction(CommonUtilities.SENDPICKUP_ACTION);
					broadcast.putExtra(BackendPushNotificationReceiver.PENDING_PICKUP_DATA, pickupData);
					sendBroadcast(broadcast);

					Intent result = new Intent().putExtra("RESULT", true);
					setResult(RESULT_OK, result);
					finish();
				}
			}
		});


	}

	public void DisplayMessage(final String msg) {
		final Context context = getApplicationContext();
		handler.post(new Runnable() {
			public void run() {
				try {
					int duration = Toast.LENGTH_LONG;
                    if(!msg.equalsIgnoreCase("")) {
                        Toast.makeText(context, msg, duration).show();
                    }
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected Handler handler = new Handler();


	@Override
	public void OnButtonClick(int tag, int buttonIndex) {

		switch (tag) {
		case 1:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				startActivity(new Intent(RegisterPassengerActivity.this, SetCreditCardDataDialog.class));
			}
			break;
		case 2:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				finish();
			}
			else {
				//sendCancelDriver();
			}
			break;
		case 3:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				finish();
			}
			break;
		case 4:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				finish();
			}
			break;
		}

	}
	
	/*private void sendCancelDriver()
	{
		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

		new Thread() {
			public void run() {

				JsonResponse<String> jsonResponce = driverViewModel
						.SendCancelToDriver(Utils.getStringById(R.string.register_passenger_not_want_accept_deposite), Const.RESRAVTION_ID, "Decline", true);

				if (jsonResponce.IsSuccess) {

					DisplayProcessMessage(false);
					DisplayMessage(jsonResponce.Message);
					this.interrupt();
					finish();

					return;

				} else {
					DisplayProcessMessage(false);
					DisplayMessage(jsonResponce.Message);
					this.interrupt();
					finish();
					return;

				}
			}
		}.start();
	}*/

}
