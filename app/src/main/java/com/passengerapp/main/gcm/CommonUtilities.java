package com.passengerapp.main.gcm;

import android.content.Context;
import android.content.Intent;


public final class CommonUtilities {

    // give your server registration url here
    static final String SERVER_URL = "http://192.168.1.100";

    // Google project id
    public static final String SENDER_ID = "430959191370";

//    public static final String SENDER_ID = "183947987799";
    // API key for browser
    static final String GOOGLE_API_KEY = "AIzaSyAwI1cPb44Bo3s0z47Zvs938HSRbuIgOt0";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Android GCM";

//    public static String DISPLAY_MESSAGE_ACTION = "notification";

    public static String DISPLAY_REPLY_ACTION = "reply";
    public static final String EXTRA_MESSAGE = "message";
    
    public static final String EXTRA_RESERVATIONID = "rsrvid";
    public static final String EXTRA_MESSAGECODE = "msgcod";
    public static final String EXTRA_REPLYCODE = "rplycod";

    public static String DISPLAY_NAVIGATION_ACTION = "navigation";

    public static String REGISTER_DIALOG_ACTION = "register";

    public static String UPDATE_PROFILE_ACTION = "updata";
    
    public static String SENDPICKUP_ACTION = "sendpichup";
    
    public static String PROGRESSDIALOGDISMISS_ACTION = "progressdismiss";

    public static String LOGIN_ACTION = "login";

    public static String UPDATE_INTERVAL_ACTION = "update";

    /*public static List<PickUpData> pickups = null;*/

    public static String message = "";

    public static String WAKE_APP_ACTION = "wakepassenger";
    
    /*
	 * Intent used to display a message in the screen.
	 */
	public static String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.passenger.app.DISPLAY_MESSAGE";

	/*
	 * Intent used to GCM registration finish
	 */
	public static String REGISTRATION_FINISH_ACTION = "com.google.android.gcm.app.REGISTRATION_DONE";
	

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message,String recervationId,String msgCode,String replyCode) {
            Intent wakeIntent = new Intent(CommonUtilities.WAKE_APP_ACTION);
            wakeIntent.putExtra(EXTRA_MESSAGE, message);
            wakeIntent.putExtra(EXTRA_RESERVATIONID, recervationId);
            wakeIntent.putExtra(EXTRA_MESSAGECODE,msgCode);
            wakeIntent.putExtra(EXTRA_REPLYCODE,replyCode);
            context.sendBroadcast(wakeIntent);
//        }
    }
}
