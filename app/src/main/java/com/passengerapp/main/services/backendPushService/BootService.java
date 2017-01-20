package com.passengerapp.main.services.backendPushService;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import com.passengerapp.main.gcm.CommonUtilities;
import com.passengerapp.util.Const;
import com.passengerapp.util.Logger;
import com.passengerapp.util.Pref;

import java.util.List;

public class BootService extends Service {
	WakeAppReceiver receiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		receiver = new WakeAppReceiver();
		registerReceiver(receiver, new IntentFilter(
				CommonUtilities.WAKE_APP_ACTION));

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void FindDetectorProcess(Intent intent) {
        // Set flag to false which determine that no showing waiting dialog on main view
        Pref.setValue(this, Const.IS_SHOW_WAITING_DLG_FOR_DRIVER, "");

        /*String processName = getCurrentTopActivity(getBaseContext());
		System.out.println("=================HARDIK PROCESS NAME::::::::::::" + processName);
		System.out.println("=================HARDIK PROCESS NAME::::::::::::" + MainActivity.class.getPackage());

		Logger.writeFull("BootService Proccess Name: " + processName);
		Logger.writeFull("BootService Main Activity Name: " + MainActivity.class.getPackage());*/

		//if (processName.startsWith("com.passengerapp")) {
		if (!isAppIsInBackground(getBaseContext())) {
			Logger.writeFull("BootService Sent broadcast message");
            Intent broadcastintent = new Intent(CommonUtilities.DISPLAY_MESSAGE_ACTION);
			broadcastintent.putExtra(CommonUtilities.EXTRA_MESSAGE, intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE));
			broadcastintent.putExtra(CommonUtilities.EXTRA_RESERVATIONID, intent.getStringExtra(CommonUtilities.EXTRA_RESERVATIONID));
			broadcastintent.putExtra(CommonUtilities.EXTRA_MESSAGECODE, intent.getStringExtra(CommonUtilities.EXTRA_MESSAGECODE));
			broadcastintent.putExtra(CommonUtilities.EXTRA_REPLYCODE, intent.getStringExtra(CommonUtilities.EXTRA_REPLYCODE));
			getApplicationContext().sendBroadcast(broadcastintent);

		}
		else {
			Logger.writeFull("BootService Didn't sent broadcast message");
			/*try {
				Intent telecare1 = new Intent("android.intent.action.VIEW");
				telecare1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				telecare1.setClassName(getPackageName(), MainActivity.class.getName());
				//telecare1.setClassName("com.herebygps","com.herebygps.generic.MainActivity");
				telecare1.putExtra("AppBackground", true);
				telecare1.putExtra(CommonUtilities.EXTRA_MESSAGE, intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE));
				telecare1.putExtra(CommonUtilities.EXTRA_RESERVATIONID, intent.getStringExtra(CommonUtilities.EXTRA_RESERVATIONID));
				telecare1.putExtra(CommonUtilities.EXTRA_MESSAGECODE, intent.getStringExtra(CommonUtilities.EXTRA_MESSAGECODE));
				telecare1.putExtra(CommonUtilities.EXTRA_REPLYCODE, intent.getStringExtra(CommonUtilities.EXTRA_REPLYCODE));
				Log.v("telecare1", "MainActivity Creating");
				getApplicationContext().startActivity(telecare1);
			}catch (Exception e) {

			}*/
		}
	}

	private boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}

		return isInBackground;
	}


	public static String getCurrentTopActivity(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
		ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
		return ar.topActivity.getClassName().toString();
	}

	public class WakeAppReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(CommonUtilities.WAKE_APP_ACTION)) {
				FindDetectorProcess(intent);
			}
		}

	}

}
