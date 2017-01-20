package com.passengerapp.main.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.passengerapp.R;
import com.passengerapp.main.network.model.response.SearchDriversData;


public class ReviewDriverDialog extends Activity implements OnClickListener {


	private TextView txtDrivrName;

	private ImageView btnBack;

	private RatingBar rbDrvrRate;

	private SearchDriversData searchdriverdata = new SearchDriversData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review_dialog);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		searchdriverdata = (SearchDriversData) getIntent().getSerializableExtra("searchDriverData");

		txtDrivrName = (TextView) findViewById(R.id.txtDrivrName);
		btnBack = (ImageView) findViewById(R.id.btnBack);
		rbDrvrRate = (RatingBar) findViewById(R.id.rbDrvrRate);
		
		txtDrivrName.setText(this.searchdriverdata.Driver.firstName + " " + this.searchdriverdata.Driver.lastName);
		rbDrvrRate.setRating((float)this.searchdriverdata.Rating);
		//rbDrvrRate.setRating(Float.parseFloat(this.searchdriverdata.Rating));

		btnBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnBack) {
			finish();
		}
	}

}
