package com.amplify.view.Activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amplify.R;
import com.amplify.controller.UserProfileTags;
import com.amplify.utils.Constant;
import com.amplify.view.MyApplication;
import com.bumptech.glide.Glide;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bitware on 9/2/18.
 */

public class ActivityProfilePreview extends AppCompatActivity implements View.OnClickListener {
    TextView txtName, txtAge, txtShortBio, txtFollowersCnt, txtDist, txtAddress, txtCancel;
    ImageView imgFlag, image, imgBack, imgGender, imgInsta, imgInstaAct, imgTwitter, imgTwitterAct, imgSnapchat, imgSnapchatAct, imgKik, imgKikAct;
    SharedPref pref;
    RecyclerView rv_tags;
    ProgressBar progressBar;
    ImageLoader imageLoader;
    Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_preview);
        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        pref = new SharedPref(this);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtShortBio = findViewById(R.id.txtShortBio);
        txtFollowersCnt = findViewById(R.id.txtFollowersCnt);
        txtDist = findViewById(R.id.txtDist);
        txtAddress = findViewById(R.id.txtAddress);
        txtCancel = findViewById(R.id.txtCancel);
        imgFlag = findViewById(R.id.imgFlag);
        imgFlag.setVisibility(View.INVISIBLE);
        image = findViewById(R.id.image);
        image.setImageResource(R.drawable.user_one);
        imgBack = findViewById(R.id.imgBack);
        imgGender = findViewById(R.id.imgGender);
        imgInsta = findViewById(R.id.imgInsta);
        imgInstaAct = findViewById(R.id.imgInstaAct);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgTwitterAct = findViewById(R.id.imgTwitterAct);
        imgSnapchat = findViewById(R.id.imgSnapchat);
        imgSnapchatAct = findViewById(R.id.imgSnapchatAct);
        imgKik = findViewById(R.id.imgKik);
        imgKikAct = findViewById(R.id.imgKikAct);
        progressBar = findViewById(R.id.progress);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MyApplication.getContext()));

        rv_tags = findViewById(R.id.rv_tags);
        rv_tags.setLayoutManager(new LinearLayoutManager(ActivityProfilePreview.this, LinearLayoutManager.HORIZONTAL, true));
        imgBack.setOnClickListener(this);
        setData();

    }

    private void setData() {
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.user_placeholder)
                .cacheOnDisc()
                .build();
        System.out.println("****Constant.USER_PROFILE_IMAGE >> "+Constant.USER_PROFILE_IMAGE);
        imageLoader.displayImage(Constant.USER_PROFILE_IMAGE, image, options,new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
                image.setImageResource(R.drawable.user_placeholder);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                imageLoader.displayImage(Constant.USER_PROFILE_IMAGE,image,options);
            }
        });
        txtAddress.setText(Constant.capitalize(Constant.USER_FB_LOCATION));
        txtFollowersCnt.setText(Constant.USER_LIKE_CNT);
        txtName.setText(Constant.capitalize(Constant.USER_FIRST_NAME ));
        txtAge.setText(", "+Constant.USER_AGE);
        if (!Constant.USER_INTRO.equalsIgnoreCase("")) {
            txtShortBio.setText(Constant.onlyFirstLetterCapital(Constant.USER_INTRO));
        }

        List<String> tags = Arrays.asList( Constant.USER_PROFILE_TAG.split("\\s*,\\s*"));
        List<String> reverceTags = new ArrayList<>();
        for(int j = tags.size() - 1; j >= 0; j--) {
            reverceTags.add(tags.get(j));
            //System.out.println(tags.get(j));
        }
        rv_tags.setAdapter(new UserProfileTags(ActivityProfilePreview.this, reverceTags));
        rv_tags.scrollToPosition(tags.size()-1);

        if (Constant.USER_GENDER.equalsIgnoreCase("female")) {
            imgGender.setImageResource(R.drawable.icon_female_sm);

        } else {
            imgGender.setImageResource(R.drawable.icon_male_sm);
        }

        if (!Constant.USER_INSTA_ACCOUNT.equalsIgnoreCase("")) {
            imgInstaAct.setVisibility(View.VISIBLE);
            imgInsta.setVisibility(View.INVISIBLE);

        } else {
            imgInsta.setVisibility(View.VISIBLE);
            imgInstaAct.setVisibility(View.INVISIBLE);
        }

        if (!Constant.USER_TWITTER_ACCOUNT.equalsIgnoreCase("")) {
            imgTwitterAct.setVisibility(View.VISIBLE);
            imgTwitter.setVisibility(View.INVISIBLE);

        } else {
            imgTwitter.setVisibility(View.VISIBLE);
            imgTwitterAct.setVisibility(View.INVISIBLE);
        }

        if (!Constant.USER_SNAPCHAT_ACCOUNT.equalsIgnoreCase("")) {
            imgSnapchatAct.setVisibility(View.VISIBLE);
            imgSnapchat.setVisibility(View.INVISIBLE);

        } else {
            imgSnapchat.setVisibility(View.VISIBLE);
            imgSnapchatAct.setVisibility(View.INVISIBLE);
        }

        if (!Constant.USER_KIK_ACCOUNT.equalsIgnoreCase("")) {
            imgKikAct.setVisibility(View.VISIBLE);
            imgKik.setVisibility(View.INVISIBLE);

        } else {
            imgKik.setVisibility(View.VISIBLE);
            imgKikAct.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("ActivityProfilePreview");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
