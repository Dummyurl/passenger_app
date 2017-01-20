package com.passengerapp.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.passengerapp.R;
import com.passengerapp.main.services.backendPushService.BootService;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.Pref;
import com.passengerapp.util.Utils;


public class SplashActivity extends Activity {

	private static final int SPLASH_SCREEN_MTIME = 3000;
	//public static TimeEventHandler eventHandler = new TimeEventHandler();

	public static Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ctx == null) {
			ctx = getApplicationContext();
		}
		setContentView(R.layout.activity_splash);

		initUI();

		setupLoggerLocalService();

		// Start service which register device in GoogleCloudMessage Server to get push notification from backend
		startService(new Intent(this, GCMIntentService.class));

		// Start service which listener notification from GCM and sent broadcast event
		startService(new Intent(SplashActivity.this, BootService.class));

		Utils.systemUpgrade(this);



		Const.deviceId = Utils.getDeviceId(SplashActivity.this);
		Pref.setValue(SplashActivity.this, Const.DEVICE_ID, Const.deviceId + "");

//		eventHandler.addRefreshTimeEventMessage(this, this,
//				SPLASH_SCREEN_MTIME);
//		eventHandler.start();

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				startMainActivity();
			}
		}, SPLASH_SCREEN_MTIME);

	}

	private void initUI() {
		ImageView imageLogo = (ImageView)findViewById(R.id.image_logo);
		imageLogo.setBackgroundResource(getResources().getIdentifier(Const.splashScreenDrawableImageID, "drawable", getPackageName()));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

//	@Override
//	public void onTimeEvent(int count) {
//		eventHandler.removeAllMessages();
//		eventHandler.stop();
//		startMainActivity();
//	}

	public void setupLoggerLocalService() {
		Logger.logDir = "RideezeLogs";
		Logger.isLoggingEnabled = true;
		Logger.logLevel = Logger.LOG_LEVEL_FULL;

	}

	public void startMainActivity() {
		startActivity(new Intent(SplashActivity.this, MainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();

        /*if(Const.isVIP) {
            if (Pref.getValue(SplashActivity.this, Const.VISIT_SET_COMPANYLINK, "0").equals("0")) {
                startActivity(new Intent(SplashActivity.this,
                        SetCompanyLinkActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this,
                        MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        } else {*/

        //}

	}

}
