package com.passengerapp.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.GetInvoiceDetailsRequest;
import com.passengerapp.main.network.model.request.SendInvoiceToDriverRequest;
import com.passengerapp.main.network.model.response.GetInvoiceDetailsData;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApproveTipActivity extends PassengerBaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private TextView txtAprvTipQuotedAmnt;
	private LinearLayout llAprvTipAd;
	private TextView txtAprvTipSubTotal;
	private TextView txtAprvTip;
	private TextView txtAprvTipTotal;
	private RadioButton rbTrtyPer;
	private RadioButton rbTwntyPer;
	private RadioButton rbTenPer;
	private RadioButton rbNoTip;
	private TextView txtIapprove;
	private TextView txtIdecline;
	private LinearLayout llaprvTipSubTotal;
	private LinearLayout llAprvTip;
	private TextView txtAprvMsgTitle;
	private Message msg;
	private DriverViewModel driverViewModel;
	private int optionValue;

	// For Row File
	private TextView txtAdjustDesc;
	private EditText edtAdjstRowAmnt;
	private RelativeLayout rlRowMain;
	private EditText adjstTemp;
	
	private ProgressDialog progressDialog = null;
	
	private List<GetInvoiceDetailsData.InvoiceItem> getInvoceData;
	
	private double fare;
	private double tip;
	private double adjustTotal;
	
	private boolean flagtTipInclude = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.approve_tip_activity);

		txtAprvTipQuotedAmnt = (TextView) findViewById(R.id.txtAprvTipQuotedAmnt);
		llAprvTipAd = (LinearLayout) findViewById(R.id.llAprvTipAd);
		txtAprvTipSubTotal = (TextView) findViewById(R.id.txtAprvTipSubTotal);
		txtAprvTip = (TextView) findViewById(R.id.txtAprvTip);
		txtAprvTipTotal = (TextView) findViewById(R.id.txtAprvTipTotal);
		rbTrtyPer = (RadioButton) findViewById(R.id.rbTrtyPer);
		rbTwntyPer = (RadioButton) findViewById(R.id.rbTwntyPer);
		rbTenPer = (RadioButton) findViewById(R.id.rbTenPer);
		rbNoTip = (RadioButton) findViewById(R.id.rbNoTip);
		txtIapprove = (TextView) findViewById(R.id.txtIapprove);
		txtIdecline = (TextView) findViewById(R.id.txtIdecline);
		llaprvTipSubTotal = (LinearLayout) findViewById(R.id.llaprvTipSubTotal);
		llAprvTip = (LinearLayout) findViewById(R.id.llAprvTip);
		txtAprvMsgTitle =  (TextView)findViewById(R.id.txtAprvMsgTitle);

		driverViewModel = new DriverViewModel();
		msg = new Message();
		flagtTipInclude = getIntent().getStringExtra("Tip").equalsIgnoreCase("true")? true:false;
		Const.invoiceId = Integer.parseInt(getIntent().getStringExtra("invoice_id"));
		Const.reservationId = Integer.parseInt(getIntent().getStringExtra("reservation_id"));
		fare = Double.parseDouble(getIntent().getStringExtra("fare_amount"));
		if(flagtTipInclude)
		{
			llaprvTipSubTotal.setVisibility(View.GONE);
			llAprvTip.setVisibility(View.GONE);
			txtAprvMsgTitle.setText(getIntent().getStringExtra("message"));
		}
		else{
			llaprvTipSubTotal.setVisibility(View.VISIBLE);
			llAprvTip.setVisibility(View.VISIBLE);
		}
		
		getInvoiceDetailes();
		
		txtIdecline.setOnClickListener(this);
		txtIapprove.setOnClickListener(this);
		rbTrtyPer.setOnCheckedChangeListener(this);
		rbTwntyPer.setOnCheckedChangeListener(this);
		rbTenPer.setOnCheckedChangeListener(this);
		rbNoTip.setOnCheckedChangeListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.txtIdecline) {
			sendInvoiceToDriver();
		} else if (id == R.id.txtIapprove) {
			startActivity(new Intent(ApproveTipActivity.this,
					SignConfirmActivity.class).putExtra("TipAmount",tip));
			finish();
		}

	}
	
	public void sendInvoiceToDriver() {

		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

		NetworkApi api = (new NetworkService()).getApi();
		SendInvoiceToDriverRequest request = new SendInvoiceToDriverRequest();
		request.ReservationID = Const.reservationId;
		request.TipAmount = (float)tip;
		request.PassengerAction = false;
		request.InvoiceID = Const.invoiceId;
		request.Signature = "";
		request.PaymentMethod = "";

		api.sendInvoiceToDriver(request)
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
					public void onNext(JsonServerResponse<String> invoiceToDriverJsonServerResponse) {
						if (invoiceToDriverJsonServerResponse.IsSuccess) {
							finish();
						}
					}
				});
	}
	
	
	public void getInvoiceDetailes() {

		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

		NetworkApi api = (new NetworkService()).getApi();
		GetInvoiceDetailsRequest request = new GetInvoiceDetailsRequest();
		request.ReservationID = Const.reservationId;
		request.InvoiceID = Const.invoiceId;

		api.getInvoiceDetails(request)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<JsonServerResponse<GetInvoiceDetailsData>>() {
					@Override
					public void onCompleted() {
						DisplayProcessMessage(false);
					}

					@Override
					public void onError(Throwable e) {
						DisplayProcessMessage(false);
					}

					@Override
					public void onNext(JsonServerResponse<GetInvoiceDetailsData> getInvoiceDetailsDataJsonServerResponse) {
						if (getInvoiceDetailsDataJsonServerResponse.IsSuccess) {
							getInvoceData = new ArrayList<GetInvoiceDetailsData.InvoiceItem>();
							getInvoceData = (List<GetInvoiceDetailsData.InvoiceItem>) getInvoiceDetailsDataJsonServerResponse.Content.InvoiceItems;
							if (getInvoceData != null && getInvoceData.size() > 0) {
								generateListUi(getInvoceData);
							} else {
								llAprvTipAd.setVisibility(View.GONE);

							}

						}
					}
				});
	}

	@SuppressLint("HandlerLeak")
	private Handler handlerWeb = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 2 &&  msg.arg1 == 0)
			{
				finish();
			}
		}
	};
	
	private void generateListUi(List<GetInvoiceDetailsData.InvoiceItem> feeCodeList) {
		llAprvTipAd.removeAllViews();
		int i = 0;
		adjustTotal = 0;
		for (GetInvoiceDetailsData.InvoiceItem httptFeecode : feeCodeList) {
			if(!httptFeecode.FeeDescription.equals("Estimated Fare"))
			{
				View view = LayoutInflater.from(ApproveTipActivity.this).inflate(R.layout.adjustment_row, null);
				txtAdjustDesc = (TextView) view.findViewById(R.id.txtAdjustDesc);
				edtAdjstRowAmnt = (EditText) view
						.findViewById(R.id.edtAdjstRowAmnt);
				View viewRow = (View) view.findViewById(R.id.viewRow);
	
				txtAdjustDesc.setText(httptFeecode.FeeDescription);
				edtAdjstRowAmnt.setText(httptFeecode.Amount + "");
	
				if (i == (feeCodeList.size() - 1)) {
					viewRow.setVisibility(View.VISIBLE);
				} else {
					viewRow.setVisibility(View.GONE);
				}
				
				adjustTotal = httptFeecode.Amount + adjustTotal;
				
				llAprvTipAd.addView(view);
			}
			i++;
		}
		
		txtAprvTipQuotedAmnt.setText((fare - adjustTotal)+"");
		calculateSubTotalFare(fare);
		txtAprvTip.setText(tip+"");
		calculateTotalFare();

	}



	private void calculateSubTotalFare(double fare) {

		txtAprvTipSubTotal.setText(new DecimalFormat("#####.00")
				.format(fare) + "");

	}

	private void calculateTotalFare() {

		txtAprvTipTotal.setText(new DecimalFormat("#####.00")
				.format((fare + tip)) + "");
	}

	private double calculatePercentageAmnt(double subTotal, int prcrnt) {

		return tip = ((subTotal * prcrnt) / 100);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if (id == R.id.rbTrtyPer) {
			if(isChecked)
			{
			txtAprvTip.setText(calculatePercentageAmnt(
					Double.parseDouble(txtAprvTipSubTotal.getText().toString()
							.trim()), 30)
					+ "");
			calculateTotalFare();
			}
		} else if (id == R.id.rbTwntyPer) {
			if(isChecked)
			{
			txtAprvTip.setText(calculatePercentageAmnt(
					Double.parseDouble(txtAprvTipSubTotal.getText().toString()
							.trim()), 20)
					+ "");
			calculateTotalFare();
			}
		} else if (id == R.id.rbTenPer) {
			if(isChecked)
			{
			txtAprvTip.setText(calculatePercentageAmnt(
					Double.parseDouble(txtAprvTipSubTotal.getText().toString()
							.trim()), 10)
					+ "");
			calculateTotalFare();
			}
		} else if (id == R.id.rbNoTip) {
			if(isChecked)
			{
			txtAprvTip.setText(0.0 + "");
			tip = 0.0;
			calculateTotalFare();
			}
		}
	}
}
