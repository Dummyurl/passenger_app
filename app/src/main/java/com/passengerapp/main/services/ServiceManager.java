package com.passengerapp.main.services;

/**
 * Class is Alarm service that used to wake Up the service at every 30 seconds and Execute the logic.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

public class ServiceManager {
	public static PendingIntent piSR = null;
	public static AlarmManager amSR = null;
	public static PendingIntent piLR = null;
	public static AlarmManager amLR = null;
	public static PendingIntent piUR = null;
	public static AlarmManager amUR = null;

	public void start(Context context) {
		/*if (!GetDriverReceiver.STARTED) {
			// Wake up Service
			amLR = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intentLR = new Intent(context, GetDriverReceiver.class);
			piLR = PendingIntent.getBroadcast(context, 0, intentLR, 0);
			amLR.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis(), 30000 * 1, piLR); // 300000
		}*/

	}

	// this method is used to stop the Alarm service.
	public void stop() {
		/*if (amSR != null) {
			amSR.cancel(piSR);
			amSR = null;
		}

		if (amLR != null) {
			GetDriverReceiver.STARTED = false;
			amLR.cancel(piLR);
			amLR = null;
		}

		if (amUR != null) {
			amUR.cancel(piUR);
			amUR = null;
		}*/

	}
}
