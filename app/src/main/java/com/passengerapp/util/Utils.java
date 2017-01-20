package com.passengerapp.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.TypedValue;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.passengerapp.R;
import com.passengerapp.main.HGBPassengerApplication;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Utils {
	public static final String SIM_ABSENT = "Absent";
	public static final String SIM_READY = "Ready";
	public static final String SIM_UNKNOWN = "Unknown";

	private static final int MAX_METERS_GOOD = 20, MAX_METERS_OK = 40,
			MAX_METERS_WEAK = 100, MAX_METERS_BAD = 200;

	public static int indexOfStringArray(String[] strArray, String strFind) {
		int index;

		for (index = 0; index < strArray.length; index++)
			if (strArray[index].equals(strFind))
				break;
		return index;
	}

    public static String modifyFare(float fare) {
        /*int position = fare.indexOf(".")+1;
        if(position > 0) {
            String afterPoint = fare.substring(position, fare.length());
            if (afterPoint.length() == 1) {
                fare += "0";
            }
        }*/
		return String.format("%.2f", fare);
        //return fare+"";
    }
	
	public static int getIndexFromList(ArrayList<String> strArray, String strFind) {
		int index;

		for (index = 0; index < strArray.size(); index++)
			if (strArray.get(index).equals(strFind))
				break;
		return index;
	}
	
	public static void systemUpgrade(Context context) {
		DBHelper dbHelper = new DBHelper(context);
		int level = Integer.parseInt(Pref.getValue(context,"LEVEL","0"));

		if (level == 0) {
			dbHelper.upgrade(level);

			// Create not confirmed order
			level++;

		}
		Pref.setValue(context,"LEVEL",level + "");

	}
	public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	public static String getLocNameFromLatLong(double lat, double Long,
			Context contx) {

		Geocoder coder = new Geocoder(contx);
		List<Address> address;
		String location = null;

		try {

			address = coder.getFromLocation(lat, Long, 3);

			if (address != null && address.size() != 0) {
				location = address.get(0).getLocality() + ","
						+ address.get(0).getCountryName();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}
	public static String formatPhoneNumber(String number){  
        number  =   number.substring(0, number.length()-4) + "-" + number.substring(number.length()-4, number.length());
        number  =   number.substring(0,number.length()-8)+")"+number.substring(number.length()-8,number.length());
        number  =   number.substring(0, number.length()-12)+"("+number.substring(number.length()-12, number.length());
        return number;
    }
	public static String getDeviceId(Context context) {
		  TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	// Get the Distance from LAt Long.
	public static int CalculationByDistance(LatLng StartP, LatLng EndP) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		double valueResult = Radius * c;
		double km = valueResult / 1;
		DecimalFormat newFormat = new DecimalFormat("####");
		int kmInDec = Integer.valueOf(newFormat.format(km));
		double meter = valueResult % 1000;
		int meterInDec = Integer.valueOf(newFormat.format(meter));
		/*System.out.println("=======DISTANCE::::::::RadiusValue+" + valueResult
				+ "   KM  " + kmInDec + " Meter " + meterInDec);

		System.out.println("=======DISTANCE::::::::RadiusValue two +"
				+ new DecimalFormat("####.##").format(Radius * c));*/

		return meterInDec;
	}

	public static void turnGPSOn(Context ctx) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {

		} else {
			Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			ctx.sendBroadcast(intent);

			String provider = Settings.Secure.getString(
					ctx.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.contains("gps")) { // if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				ctx.sendBroadcast(poke);

			}
		}
	}

	public enum GPSReception {
		GOOD, OK, WEAK, BAD, NONE;
	}

	private String toString(GPSReception reception) {
		switch (reception) {
		case GOOD:
			return "" + 6;
		case OK:
			return "" + 4;
		case WEAK:
			return "" + 2;
		case BAD:
			return "" + 1;
		case NONE:
			return "" + 0;
		default:
			return "" + 0;
		}
	}

	public static GPSReception getGPSReception(float accuracy) {
		if (accuracy == 0.0) {
			return GPSReception.NONE;
		}
		if (accuracy <= MAX_METERS_GOOD) {
			return GPSReception.GOOD;
		} else if (accuracy <= MAX_METERS_OK) {
			return GPSReception.OK;
		} else if (accuracy <= MAX_METERS_WEAK) {
			return GPSReception.WEAK;
		} else if (accuracy <= MAX_METERS_BAD) {
			return GPSReception.BAD;
		} else {
			return GPSReception.NONE;
		}
	}

	public static int toImageResource(GPSReception reception) {
		switch (reception) {
		case GOOD:
			return R.drawable.setting_gpsgood;
		case OK:
			return R.drawable.setting_gpsaverage;
		case WEAK:
			return R.drawable.setting_gpsfair;
		case BAD:
			return R.drawable.setting_gpsbad;
		case NONE:
			return R.drawable.setting_gpsno;
		}
		return R.drawable.setting_gpsno;
	}

	public static int toArrowImageResource(GPSReception reception) {
		switch (reception) {
		case GOOD:
			return R.drawable.gpsblue;
		case OK:
			return R.drawable.gpsgreen;
		case WEAK:
			return R.drawable.gpsyellow;
		case BAD:
			return R.drawable.gpsred;
		case NONE:
			return R.drawable.gpsblack;
		}
		return R.drawable.gpsblack;
	}

	public static String toGetTextValue(GPSReception reception) {
		switch (reception) {
		case GOOD:
			return Utils.getStringById(R.string.utils_gps_signal_good);
		case OK:
			return Utils.getStringById(R.string.utils_gps_signal_average);
		case WEAK:
			return Utils.getStringById(R.string.utils_gps_signal_fair);
		case BAD:
			return Utils.getStringById(R.string.utils_gps_signal_bad);
		case NONE:
			return Utils.getStringById(R.string.utils_gps_signal_no_signal);
		}
		return Utils.getStringById(R.string.utils_gps_signal_no_signal);
	}

	public static String getStringById(int id) {
		return HGBPassengerApplication.getContext().getString(id);
	}

	// automatic turn off the gps
	public static void turnGPSOff(Context ctx) {
		if (android.os.Build.VERSION.SDK_INT >= 19) {

		} else {
			String provider = Settings.Secure.getString(
					ctx.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (provider.contains("gps")) { // if gps is enabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				ctx.sendBroadcast(poke);
			}
		}
	}

	// Get Time to travel between to location in HH:mm:ss formate.
	public static String splitToComponentTimes(BigDecimal biggy) {
		long longVal = biggy.longValue();
		int hours = (int) longVal / 3600;
		int remainder = (int) longVal - hours * 3600;
		int mins = remainder / 60;
		remainder = remainder - mins * 60;
		int secs = remainder;

		int[] ints = { hours, mins, secs };
		String time = ints[0] + ":" + ints[1] + ":" + ints[2];
		return time;
	}

	// To check that Any Internet service or wifi available before using process
	// that make connection to server.
	public static boolean isOnline(Context context) {

		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				return cm.getActiveNetworkInfo().isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	// Used to conver Date object to string
	public static String convertDateToString(Date objDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat).format(objDate);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	// Used to convert String to date
	public static Date convertStringToDate(String strDate, String parseFormat) {
		try {
			return new SimpleDateFormat(parseFormat).parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Used to convert Date string to string
	public static String convertDateStringToString(String strDate,
			String currentFormat, String parseFormat) {
		try {
			return convertDateToString(
					convertStringToDate(strDate, currentFormat), parseFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Used to convert milliseconds to date
	public static String millisToDate(long millis, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date mDate = new Date(millis);
			String date = sdf.format(new Date(millis));
			return date;
			//System.out.println("Date in milli :: " + date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String DateStringToSeconds(String dateString, String format) {
		String timeInMilliseconds = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
		    Date mDate = sdf.parse(dateString);
		     timeInMilliseconds = (mDate.getTime()/1000)+"";
		    System.out.println("Date in milli :: " + timeInMilliseconds);
		} catch (ParseException e) {
		            e.printStackTrace();
		}

		return timeInMilliseconds;
	}
	
	public static long converDatetomillis(String dateString, String dateFormate) {
		long timeInMilliseconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
		try {
			Date mDate = sdf.parse(dateString);
			timeInMilliseconds = mDate.getTime();
			System.out.println("Date in milli :: " + timeInMilliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeInMilliseconds;
	}

	public static void showWarningMsg(Context activity, String msg) {
		new AlertDialog.Builder(activity)
				.setTitle(R.string.common_warning)
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	public static void sentLogToServer(String tag, Map<String, String> articleParams) {
		//FlurryAgent.logEvent(tag, articleParams);
		//Crashlytics.log(Log.DEBUG, tag, (new Gson()).toJson(articleParams));
	}

	public static void sentErrorLogToServer(String tag, String title, String msg) {
		Map<String, String> params = new HashMap<String,String>();
		params.put(title, msg);
		//FlurryAgent.logEvent(tag, params);
		//FlurryAgent.onError(tag, title, msg);
		//Crashlytics.log(Log.ERROR, tag, title + " "+msg);
	}

	public static int convertToDp(Context ctx, int value) {
		Resources r = ctx.getResources();
		int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
		return px;
	}

	public static boolean checkGooglePlayServicesAvailable(Context ctx) {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			return false;
		}

		return true;
	}

	public static int getCarImage(Context ctx,String style, String color) {
		int styleidx = 4;
		int coloridx = 0;
		color = color.toLowerCase();
		style = style.toLowerCase();

		String[] styleArray = new String[] { "sedan", "suv", " "," ", " ", "van", "limo", "pickup", "compact" };
		String[] colorArray = new String[] { "yellow", "white", "black", "red", "green", "blue" };

		for (int i = 0; i < styleArray.length; i++) {

			if (styleArray[i].toLowerCase().toString().equals(style)) {
				styleidx = i;
			}

		}

		for (int i = 0; i < colorArray.length; i++) {

			if (colorArray[i].toLowerCase().toString().equals(color)) {
				coloridx = i;
			}

		}

		String mDrawableName = "car"+styleidx+""+coloridx;
		int resID = ctx.getResources().getIdentifier(mDrawableName , "drawable", ctx.getPackageName());

		//return R.drawable.car00 + styleidx * 6 + coloridx;
		return resID;
	}
}