package com.passengerapp.main.services.backendPushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.main.model.http.data.PendingPickupData;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.StorageDataHelper;

/**
 * Created by adventis on 10/8/15.
 */
public class BackendPushNotificationReceiver extends BroadcastReceiver {
    public static final String PENDING_PICKUP_DATA = "PENDING_PICKUP_DATA";

    public static final int ACCEPT_DRIVER_DIALOG = 1;
    public static final int DECLINE_DRIVER_DIALOG = 2;
    public static final int ARRIVE_DRIVER_DIALOG = 3;
    public static final int SHOW_WAITING_DRIVER_DIALOG = 4;
    public static final int DISMISS_WAITING_DRIVER_DIALOG = 5;
    public static final int NOT_DRIVER_RESPONSE = 6;

    IBackendPushNotificationListener mDelegate;

    public BackendPushNotificationReceiver(IBackendPushNotificationListener delegate) {
        mDelegate = delegate;
    }

    private void sentMessageToUI(int msgCode, String obj) {
        if(mDelegate != null) {
            mDelegate.onReceiveMsg(msgCode, obj);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.writeFull("--------------Receive intent in MainActivity--------------");
        Logger.writeFull("Action = " + intent.getAction());


        if (intent.getAction().equals(CommonUtilities.DISPLAY_MESSAGE_ACTION)) {
            final String message = intent.getStringExtra("message");
            String msgCode = intent.getStringExtra("msgcod");
            Const.RESRAVTION_ID = intent.getStringExtra("rsrvid");

            Logger.writeFull("msgcod = " + msgCode);
            Logger.writeFull("message = " + message);
            Logger.writeFull("rsrvid = " + Const.RESRAVTION_ID);

            if (msgCode.equals("132")) {
                // Push notification which describe if driver ACCEPT OR DECLINE order
                String replyCode = intent.getStringExtra("rplycod");
                if (replyCode.equals("0")) {
                    sentMessageToUI(DECLINE_DRIVER_DIALOG, null);
                } else if (replyCode.equals("1")) {
                    sentMessageToUI(ACCEPT_DRIVER_DIALOG, null);
                }
            } else if(msgCode.equals("16388")) {
                // Push notification code if driver arrived
                sentMessageToUI(ARRIVE_DRIVER_DIALOG, message);
            } else {
                // show dialog when driver not responds
                sentMessageToUI(NOT_DRIVER_RESPONSE, message);
            }
        }

        if (intent.getAction().equals(CommonUtilities.PROGRESSDIALOGDISMISS_ACTION)) {
            StorageDataHelper.getInstance(context).setPendingPickupStatus(null);
            sentMessageToUI(DISMISS_WAITING_DRIVER_DIALOG, null);
        }

        if (intent.getAction().equals(CommonUtilities.SENDPICKUP_ACTION)) {
            if(intent.getSerializableExtra(PENDING_PICKUP_DATA) !=null) {
                PendingPickupData pendingPickupData = (PendingPickupData)intent.getSerializableExtra(PENDING_PICKUP_DATA);
                StorageDataHelper.getInstance(context).setPendingPickupStatus(pendingPickupData);
            }
            sentMessageToUI(SHOW_WAITING_DRIVER_DIALOG, null);
        }
    }
}
