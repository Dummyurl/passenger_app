package com.passengerapp.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.GiveReviewToDriverRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.network.model.response.PickUpReservationData;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.util.Const;
import com.passengerapp.util.StorageDataHelper;
import com.passengerapp.util.Utils;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RateActivity extends PassengerBaseActivity implements OnClickListener {

	ImageView cancel;
	ImageView rate;

	private TextView passengerName;
	private RatingBar passengerRating;
	private EditText edtComment;
	private RelativeLayout rlMainRate;
	private InputMethodManager imm;

	private PickUpReservationData reservationData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate);

		this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		initUI();

	}

	public void initUI() {
		cancel = (ImageView) findViewById(R.id.confirm_rate);
		cancel.setOnClickListener(this);

		rate = (ImageView) findViewById(R.id.close_rate);
		rate.setOnClickListener(this);

		edtComment = (EditText) findViewById(R.id.edtComment);
		
		rlMainRate= (RelativeLayout) findViewById(R.id.rlMainRate);
		rlMainRate.setOnClickListener(this);

		passengerName = (TextView) findViewById(R.id.passengerName);
		passengerRating = (RatingBar) findViewById(R.id.passengerRating);

		List<PickUpReservationData> pickUpForPassanger = StorageDataHelper.getInstance(this).getDriverPickUpsForPassanger();
		if (pickUpForPassanger != null && pickUpForPassanger.size() > Const.currentItem) {
			reservationData = pickUpForPassanger.get(Const.currentItem);
			passengerName.setText(reservationData.DriverName);
			passengerRating.setRating((float) reservationData.Rating);
			Const.RESRAVTION_ID = reservationData.ReservationID+"";
		}
	}

	@Override
	public void onClick(View v) {

		if (v == cancel) {

			if (reservationData != null) {
				if (edtComment.getText().toString().trim() != null
						&& !edtComment.getText().toString().trim().equals("")) {

					if(!isNetworkAvailable())
						return;

					DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

					NetworkApi api = (new NetworkService()).getApi();
					final GiveReviewToDriverRequest request = new GiveReviewToDriverRequest();
					request.ReservationID = reservationData.ReservationID;
					request.Comments = edtComment.getText().toString().trim();
					request.Rating = passengerRating.getRating() + "";

					request.UniqueID = Const.deviceId;


					api.giveReviewToDriver(request)
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
									DisplayMessage(stringJsonServerResponse.Message);
									saveReviewToStore(request);
									requestGetPendingPickup();
								}
							});
				} else {
					AlertDailogView.showAlert(RateActivity.this,
							Utils.getStringById(R.string.rateactivity_please_enter_your_comments), Utils.getStringById(R.string.common_OK)).show();
				}
			} else {
				AlertDailogView.showAlert(RateActivity.this,
						Utils.getStringById(R.string.rateactivity_have_no_select_any_driver), Utils.getStringById(R.string.common_OK)).show();
			}
		}

		if (v == rate) {
			finish();
		}
		
		if( v == rlMainRate)
		{
			if(this.imm != null)
			{
				this.imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
			}
		}

	}

	public void saveReviewToStore(GiveReviewToDriverRequest request) {
		StorageDataHelper.getInstance(this).addReviewToDeriver(request);
	}

	public void requestGetPendingPickup() {
		NetworkApi api = (new NetworkService()).getApi();

		api.getPickupsForPassenger(StorageDataHelper.getInstance(this).getPhoneToken())
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonServerResponse<List<PickUpReservationData>>>() {
					@Override
					public void onCompleted() {
						finish();
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onNext(JsonServerResponse<List<PickUpReservationData>> pickUpDataJsonServerResponse) {
						if (pickUpDataJsonServerResponse.IsSuccess) {
							StorageDataHelper.getInstance(getApplicationContext()).setDriverPickUpsForPassanger(pickUpDataJsonServerResponse.Content);
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
					Toast.makeText(context, msg, duration).show();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	protected Handler handler = new Handler();
}
