package com.passengerapp.main;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.passengerapp.R;
import com.passengerapp.util.Logger;
import com.passengerapp.util.StorageDataHelper;

import java.io.IOException;

import static com.passengerapp.main.gcm.CommonUtilities.SENDER_ID;
import static com.passengerapp.main.gcm.CommonUtilities.displayMessage;



/*import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;*/

/**
 * {@link IntentService} responsible for handling GCM messages.
 */
public class  GCMIntentService extends IntentService {
	public static String TAG = "GCMIntentService";
	static int cur_count;
	static int update_count;
	public static String action = "";

    GoogleCloudMessaging gcm;
    String regid;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

	// GcmTokenId
	public GCMIntentService() {
        super(SENDER_ID);
        Logger.writeFull("GCMIntentService start");
	}

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.writeFull("onCreate start");
        onRegistered();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

	// register GCM
	protected void onRegistered() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(getBaseContext());
            Logger.writeFull("checkPlayServices true");

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Logger.writeFull("checkPlayServices false");
        }
	}

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    StorageDataHelper.getInstance(getBaseContext()).setPhoneToken(regid);


                    storeRegistrationId(getBaseContext(), regid);
                } catch (IOException ex) {
                    msg = "Error onRegistered :" + ex.getMessage();
                    Logger.writeFull(msg);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                Logger.writeFull(msg);
            }
        }.execute();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Logger.writeFull("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Logger.writeFull("App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),Context.MODE_PRIVATE);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Logger.writeFull("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

	protected void onUnregistered(Context context, String registrationId) throws IOException {
        Logger.writeFull("=======================================");
        Logger.writeFull("Device unregistered");
        Logger.writeFull("=======================================");
        Logger.writeFull("=============ON GCM UNREGISTER::::::::::");

        gcm.unregister();

	}

	// Get PushNotification
	protected void onMessage(Context context, Intent intent) {
        Logger.writeFull("GCMIntentService receive onMessage");

		try {
			String message = intent.getExtras().getString("notification");
			String msgCode = intent.getExtras().getString("msgcod");

            Logger.writeFull("GCMIntentService receive onMessage msg: "+message+" and code "+ msgCode);
			if (msgCode.equals("2052")) {
				startActivity(new Intent(GCMIntentService.this,ApproveTipActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("Tip", intent.getExtras().getString("tip_amount"))
                        .putExtra("invoice_id", intent.getExtras().getString("invoice_id"))
                        .putExtra("fare_amount", intent.getExtras().getString("fare_amount"))
                        .putExtra("message", message).putExtra("reservation_id",intent.getExtras().getString("reservation_id")));
			} else {
				String recervationId = intent.getExtras().getString("rsrvid");
				String replyCode = "";

				if (msgCode.equals("132")) {
					replyCode = intent.getExtras().getString("rplycod");
				}

				displayMessage(context, message, recervationId, msgCode,replyCode);
			}

			generateNotification(context, message);
		} catch (Exception e) {
			e.printStackTrace();
            Logger.writeFull(e + "");
		}
	}

	protected void onDeletedMessages(Context context, int total) {
        Logger.writeFull("=============ON GCM OnDELETED MESSAGES::::::::::");
        String message = "From GCM: server deleted " + total
                + " pending messages!";
        // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);

        Logger.writeFull("=============ON GCM DELETE MESSAGE::::::::::");
    }

	public void onError(Context context, String errorId) {
		// sendBroadcast(new
		// Intent(CommonUtilities.REGISTRATION_FINISH_ACTION));
        Logger.writeFull("=============ON GCM OnERROR::::::::::" + errorId);
		// displayMessage(context, "From GCM: error (" + errorId + ")");

		Log.e("GCM onError", errorId);
	}

    @Override
    protected void onHandleIntent(Intent intent) {

        Logger.writeFull("onHandlerIntent");

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                onError(getApplicationContext(), extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                onDeletedMessages(getApplicationContext(), Integer.parseInt(extras.toString()));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                onMessage(getApplicationContext(), intent);
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {

        Logger.writeFull("generate notification with msg: "+message);

        NotificationManager notificationManager = null;

		int icon = R.mipmap.ic_launcher;
		long when = System.currentTimeMillis();

		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getResources().getString(R.string.app_name);

		Intent notificationIntent;

		notificationIntent = new Intent(context, SplashActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent intent = null;
		intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.sound = Uri.parse("android.resource://"
				+ context.getPackageName() + "/" + R.raw.dings);
		notificationManager.notify(0, notification);
	}
}
