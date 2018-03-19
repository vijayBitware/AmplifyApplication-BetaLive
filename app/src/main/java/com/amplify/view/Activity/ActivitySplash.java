package com.amplify.view.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.amplify.R;
import com.amplify.utils.SharedPref;

/**
 * Created by bitware on 9/3/18.
 */

public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        SharedPref shared_pref = new SharedPref(ActivitySplash.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("**************login************" + SharedPref.getPreferences().getString(SharedPref.REG_COMPLETE, ""));
                if (SharedPref.getPreferences().getString(SharedPref.REG_COMPLETE, "").equalsIgnoreCase("1")) {
                    System.out.println("*******already login*******");
                    Intent intent1 = new Intent(ActivitySplash.this, HomeActivity.class);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }else {
                    startActivity(new Intent(ActivitySplash.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
            }
        }, 1500);

    }
}
