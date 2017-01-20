package com.passengerapp.main.services.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WebInterface {
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileNetInfo = connMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			return ((wifiNetInfo.isAvailable() && wifiNetInfo.isConnected()) || (mobileNetInfo != null
					&& mobileNetInfo.isAvailable() && mobileNetInfo
						.isConnected()));
		} catch (Exception e) {
			return false;
		}
	}
}
