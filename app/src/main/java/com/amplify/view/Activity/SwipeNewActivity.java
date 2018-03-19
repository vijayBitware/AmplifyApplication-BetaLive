package com.amplify.view.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplify.R;
import com.amplify.controller.SwipeAdapter;
import com.amplify.utils.Constant;
import com.amplify.view.MyApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class SwipeNewActivity extends AppCompatActivity {

    View ViewOverlay;
    private static ViewPager mPager;
    private static int currentPage = 0;
    ImageView imgFlag, imgBack;
    LinearLayout linearFlag, linearProfileView;
    TextView txtCancel;
    RelativeLayout relativeFollower;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_new);
        init();
        mPager.setCurrentItem(Constant.userPosition);
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SwipeAdapter(SwipeNewActivity.this, Constant.arrAllUsers));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTracker.setScreenName("Image~" + "SwipeNewActivity");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("SwipeNewActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
