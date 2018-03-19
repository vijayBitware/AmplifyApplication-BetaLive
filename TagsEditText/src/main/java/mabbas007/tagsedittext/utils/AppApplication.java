package mabbas007.tagsedittext.utils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;


public class AppApplication extends Application {

    private static boolean activityVisible = false;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mContext = this.getApplicationContext();

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
}
