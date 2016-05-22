package com.gripxtech.kasimrangwala.grophers.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {

    public static final String TAG = AppPrefs.class.getSimpleName();
    private static final String FirstLaunch = "FirstLaunch";
    private static final String LoggedIn = "LoggedIn";
    private static final String MobileNo = "MobileNo";
    SharedPreferences mPrefs;

    public AppPrefs(Context context) {
        mPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return mPrefs.getBoolean(FirstLaunch, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        mPrefs.edit().putBoolean(FirstLaunch, firstLaunch).apply();
    }

    public boolean isLoggedIn() {
        return mPrefs.getBoolean(LoggedIn, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        mPrefs.edit().putBoolean(LoggedIn, loggedIn).apply();
    }

    public String getMobileNo() {
        return mPrefs.getString(MobileNo, null);
    }

    public void setMobileNo(String mobileNo) {
        mPrefs.edit().putString(MobileNo, mobileNo).apply();
    }
}
