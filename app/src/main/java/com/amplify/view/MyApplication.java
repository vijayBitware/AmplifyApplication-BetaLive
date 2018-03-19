package com.amplify.view;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.amplify.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    private static boolean activityVisible = false;
    private static Context mContext;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Fabric.with(this, new Crashlytics());
        mContext = this.getApplicationContext();
        sAnalytics = GoogleAnalytics.getInstance(this);

    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = true;
    }

    public static void activityStop() {
        activityVisible = false;
    }

    public static void activityFinish() {
        activityVisible = false;
    }

    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }
        return sTracker;
    }
}
