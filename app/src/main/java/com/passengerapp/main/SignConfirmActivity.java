package com.passengerapp.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.activities.PassengerBaseActivity;
import com.passengerapp.main.model.http.data.HttpGetInvoice;
import com.passengerapp.main.network.NetworkApi;
import com.passengerapp.main.network.NetworkService;
import com.passengerapp.main.network.model.request.SendInvoiceToDriverRequest;
import com.passengerapp.main.network.model.response.JsonServerResponse;
import com.passengerapp.main.uc.AlertDailogView;
import com.passengerapp.main.viewmodels.DriverViewModel;
import com.passengerapp.util.Const;
import com.passengerapp.util.Utils;

import java.io.ByteArrayOutputStream;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

;

public class SignConfirmActivity extends PassengerBaseActivity implements
		OnCheckedChangeListener, OnClickListener,
		AlertDailogView.OnCustPopUpDialogButoonClickListener {
	private static final float STROKE_WIDTH = 3f;
	private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
	private RadioButton rbPayStrdCrdtcard;
	private RadioButton rbPayWithCC;
	private RadioButton rbPayCash;
	private TextView txtSignConfrmNext;
	private ProgressDialog progressDialog = null;
	private DriverViewModel driverViewModel;
	private static String emailPassenger;
	private List<HttpGetInvoice> _feeCodeList;
	private static String _signatureIamge;
	private Message msg;
	private LinearLayout llSignCanvas;
	public static Paint mPaint;
	private CanvasView objCanvas;

	public static int width;
	public static int height;
	
	private double tipAmount;
	private String PaymentMethod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sign_confirm_activity);
		driverViewModel = new DriverViewModel();
		msg = new Message();
		rbPayStrdCrdtcard = (RadioButton) findViewById(R.id.rbPayStrdCrdtcard);
		rbPayWithCC = (RadioButton) findViewById(R.id.rbPayWithCC);
		rbPayCash = (RadioButton) findViewById(R.id.rbPayCash);

		txtSignConfrmNext = (TextView) findViewById(R.id.txtSignConfrmNext);
		llSignCanvas = (LinearLayout) findViewById(R.id.llSignCanvas);

		tipAmount = getIntent().getDoubleExtra("TipAmount",0);
	

		txtSignConfrmNext.setOnClickListener(this);
		rbPayWithCC.setOnCheckedChangeListener(this);
		rbPayCash.setOnCheckedChangeListener(this);
		rbPayStrdCrdtcard.setOnCheckedChangeListener(this);

        rbPayCash.setChecked(true);
        PaymentMethod = Const.CASH_PAYMENT_METHOD;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		width = llSignCanvas.getWidth();
		height = llSignCanvas.getHeight();

		if (llSignCanvas.getChildCount() == 0) {
			objCanvas = new CanvasView(SignConfirmActivity.this);
			llSignCanvas.addView(objCanvas);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if (id == R.id.rbPayWithCC) {
			if (isChecked) {
				PaymentMethod = Const.CREDIT_CARD_SCANNED_PAYMENT_METHOD;
			}
		} else if (id == R.id.rbPayCash) {
			if (isChecked) {
				PaymentMethod = Const.CASH_PAYMENT_METHOD;
			}
		} else if (id == R.id.rbPayStrdCrdtcard) {
			if (isChecked) {
				PaymentMethod = Const.CREDIT_CARD_STORED_PAYMENT_METHOD;
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.txtSignConfrmNext) {
			CanvasView objSaveView = (CanvasView) llSignCanvas.getChildAt(0);
			objSaveView.saveImage(objSaveView);
			sendInvoiceToDriver();
		}

	}

	public static void showTransactionCompleteAlert(final Context context,
			String email, final AlertDailogView.OnCustPopUpDialogButoonClickListener lisner) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.trasaction_complete_dialog);
		dialog.setCancelable(true);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.getWindow().getAttributes().width = LayoutParams.FILL_PARENT;

		TextView txtTraCmpMessage = (TextView) dialog
				.findViewById(R.id.txtTraCmpMessage);
		TextView txtTraCmpDone = (TextView) dialog
				.findViewById(R.id.txtTraCmpDone);

		txtTraCmpMessage.setText(Html.fromHtml(context.getResources().getString(R.string.tankyouMsg)+" email "+context.getResources().getString(R.string.tankyouMsglng)));

		txtTraCmpDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (lisner != null) {
					lisner.OnButtonClick(0, AlertDailogView.BUTTON_OK);
				}
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	private void showProcessingDialog(String msg) {
		if (progressDialog == null)
			progressDialog = ProgressDialog.show(this, "", msg, true, false);
	}

	public void DisplayProcessMessage(final String msg) {
		showProcessingDialog(msg);
	}

	private void hideProcessingDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}

	public void DisplayProcessMessage(final boolean hide) {

		hideProcessingDialog();

	}

	public void sendInvoiceToDriver() {
		if(!isNetworkAvailable())
			return;

		DisplayProcessMessage(Utils.getStringById(R.string.common_please_wait_3_point));

		NetworkApi api = (new NetworkService()).getApi();
		SendInvoiceToDriverRequest request = new SendInvoiceToDriverRequest();
		request.ReservationID = Const.reservationId;
		request.PassengerAction = true;
		request.InvoiceID = Const.invoiceId;
		request.Signature = _signatureIamge;
		request.TipAmount = (float)tipAmount;
		request.PaymentMethod = PaymentMethod;

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
							showTransactionCompleteAlert(SignConfirmActivity.this, "" ,SignConfirmActivity.this);
						}
					}
				});



	}

	@Override
	public void OnButtonClick(int tag, int buttonIndex) {
		switch (tag) {
		case 0:
			if (buttonIndex == AlertDailogView.BUTTON_OK) {
				startActivity(new Intent(SignConfirmActivity.this,
						MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			}
			break;

		}

	}

	public static class CanvasView extends View {

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Path mPath = new Path();
		private Paint mPaint;
		private float mCurX, mCurY;
		private static final float TOUCH_TOLERANCE = 1;
		private final RectF mRect = new RectF();

		public CanvasView(Context c) {
			super(c);

			mPaint = new Paint();
			mPaint.setAntiAlias(true);

			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(STROKE_WIDTH);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawPath(mPath, mPaint);
		}

		private void touch_start(float x, float y) {

			mPath.moveTo(x, y);
			mCurX = x;
			mCurY = y;

		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mCurX);
			float dy = Math.abs(y - mCurY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2);
				mCanvas.drawPath(mPath, mPaint);
				mCurX = x;
				mCurY = y;
			}
		}

		private void touch_up(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();

			resetRect(eventX, eventY);
			int historySize = event.getHistorySize();
			for (int i = 0; i < historySize; i++) {
				float historicalX = event.getHistoricalX(i);
				float historicalY = event.getHistoricalY(i);
				expandDirtyRect(historicalX, historicalY);
				mPath.lineTo(historicalX, historicalY);
			}
			mPath.lineTo(eventX, eventY);
		}

		private void resetRect(float eventX, float eventY) {
			mRect.left = Math.min(mCurX, eventX);
			mRect.right = Math.max(mCurX, eventX);
			mRect.top = Math.min(mCurY, eventY);
			mRect.bottom = Math.max(mCurY, eventY);
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < mRect.left) {
				mRect.left = historicalX;
			} else if (historicalX > mRect.right) {
				mRect.right = historicalX;
			}
			if (historicalY < mRect.top) {
				mRect.top = historicalY;
			} else if (historicalY > mRect.bottom) {
				mRect.bottom = historicalY;
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);

				return true;
			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:
				touch_up(event);
				this.saveImage(this);

				break;
			default:
				return false;
			}
			invalidate((int) (mRect.left - HALF_STROKE_WIDTH),
					(int) (mRect.top - HALF_STROKE_WIDTH),
					(int) (mRect.right + HALF_STROKE_WIDTH),
					(int) (mRect.bottom + HALF_STROKE_WIDTH));

			mCurX = x;
			mCurY = y;

			return true;
		}

		public void saveImage(View view) {
			Bitmap b = Bitmap.createBitmap(width,height,
					Bitmap.Config.ARGB_8888);

			Canvas c = new Canvas(b);
			view.draw(c);
			ByteArrayOutputStream stream = null;
			try {
				stream = new ByteArrayOutputStream();
				b.compress(Bitmap.CompressFormat.PNG, 100, stream);
				_signatureIamge = Base64.encodeToString(stream.toByteArray(),
						Base64.DEFAULT);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
