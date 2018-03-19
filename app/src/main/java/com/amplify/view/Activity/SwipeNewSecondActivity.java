package com.amplify.view.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amplify.R;
import com.amplify.controller.SwipeTwoAdapter;
import com.amplify.model.GetLikedMe.UserDatum;
import com.amplify.utils.Constant;
import com.amplify.view.MyApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class SwipeNewSecondActivity extends AppCompatActivity {

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_new);
        init();
        System.out.println("Current Position > " +Constant.userPosition);
        mPager.setCurrentItem(Constant.userPosition);

    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SwipeTwoAdapter(SwipeNewSecondActivity.this, (List<UserDatum>) Constant.arrGetLiked));
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTracker.setScreenName("Image~" + "SwipeNewSecondActivity");
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
        mTracker.setScreenName("SwipeNewSecondActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
