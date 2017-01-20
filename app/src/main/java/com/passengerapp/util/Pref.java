package com.passengerapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class Pref {

	private static SharedPreferences sharedPreferences = null;

	public static void openPref(Context context) {
		try {
			sharedPreferences = context.getSharedPreferences(Const.PREF_FILE,
					Context.MODE_PRIVATE);
		}catch (Exception e) {

		}
	}

	public static String getValue(Context context, String key,
			String defaultValue) {
		try{
			Pref.openPref(context);
			String result = Pref.sharedPreferences.getString(key, defaultValue);
			Pref.sharedPreferences = null;
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

    public static Boolean getBooleanValue(Context context, String key, Boolean defaultValue) {
        try{
            Pref.openPref(context);
            String resultStr = Pref.sharedPreferences.getString(key, "false");
            Pref.sharedPreferences = null;
            return Boolean.parseBoolean(resultStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

	public static void setValue(Context context, String key, String value) {
		Pref.openPref(context);
		Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
		prefsPrivateEditor.putString(key, value);
		prefsPrivateEditor.commit();
		prefsPrivateEditor = null;
		Pref.sharedPreferences = null;
	}

    public static void setBooleanValue(Context context, String key, Boolean value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value+"");
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

}
