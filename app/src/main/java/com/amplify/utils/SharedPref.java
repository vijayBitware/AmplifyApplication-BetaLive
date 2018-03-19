package com.amplify.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * This class for Store data to locally in Shared Preferences.
 *
 * @author (Arun Chougule)
 */
public class SharedPref {
    public static final String IP_ADDRESS = "ipaddress";
    public static final String CURRENt_LAT = "lat";
    public static final String CURRENT_LONG = "long";
    public static String USER_ID = "user_id";
    public static String FB_ID = "fb_id";
    public static String FIRST_NAME = "fname";
    public static String USER_ADDRESS = "address";
    public static String LAST_NAME = "lname";
    public static String USER_TOKEN = "token";
    public static String REG_COMPLETE = "reg_complete";
    public static String STATUS = "status";
    public static String NICKNAME = "nickname";
    public static String USER_PROFILE = "profile";
    private static Context context;
    public static final String PREF_NAME = "amplify_preference";
    public static String GCMREGID = "rec_id";
    public static String NOTIFICATION_COUNTER = "counter";
    public static String USER_EMAIL = "email";
    public static String USER_CITY = "city";

    public SharedPref(Context c) {
        context = c;
    }

    public static SharedPreferences getPreferences() {

        return context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
    }

    public static void writeString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public static void getString(String key, String value) {
        SharedPref.getPreferences().getString(key, value);
    }

    public static Editor getEditor() {

        return getPreferences().edit();
    }

    public static boolean getBoolean(String pREF_KEY_TWITTER_LOGIN, boolean b) {
        // TODO Auto-generated method stub
        return getEditor().putBoolean(pREF_KEY_TWITTER_LOGIN, b).commit();
    }

}
