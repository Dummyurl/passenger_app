package com.passengerapp.main.services.backendPushService;

/**
 * Created by adventis on 10/8/15.
 */
public interface IBackendPushNotificationListener {
    public void onReceiveMsg(int code, String data);
}
