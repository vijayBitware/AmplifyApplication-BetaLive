package com.amplify.view.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.FilterResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bitware on 2/2/18.
 */

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, APIRequestWithDefaultloader.ResponseHandler {

    TextView txt_male, txt_female, txt_both, txt_instagram, txt_twitter, txt_snapchat, txt_kik, txt_all;
    boolean isInstagramSelected = true, isTwitterSelected = true, isSnapchatSelected = true, isKikSelected = true, isAllSelcted = true, isBoth = true;
    ImageView iv_back;
    String network_status;
    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);

        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        txt_all = findViewById(R.id.txt_all);
        txt_male = findViewById(R.id.txt_male);
        txt_female = findViewById(R.id.txt_female);
        txt_both = findViewById(R.id.txt_both);
        txt_instagram = findViewById(R.id.txt_instagram);
        txt_twitter = findViewById(R.id.txt_twitter);
        txt_snapchat = findViewById(R.id.txt_snapchat);
        txt_kik = findViewById(R.id.txt_kik);
        iv_back = findViewById(R.id.iv_back);

        txt_male.setOnClickListener(this);
        txt_female.setOnClickListener(this);
        txt_both.setOnClickListener(this);
        txt_instagram.setOnClickListener(this);
        txt_twitter.setOnClickListener(this);
        txt_snapchat.setOnClickListener(this);
        txt_kik.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        txt_all.setOnClickListener(this);

        if (Constant.kik.equalsIgnoreCase("1")) {
            txt_kik.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txt_kik.setTextColor(getResources().getColor(R.color.black));
        }

        if (Constant.insta.equalsIgnoreCase("1")) {
            txt_instagram.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txt_instagram.setTextColor(getResources().getColor(R.color.black));
        }

        if (Constant.twitter.equalsIgnoreCase("1")) {
            txt_twitter.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txt_twitter.setTextColor(getResources().getColor(R.color.black));
        }

        if (Constant.snapchat.equalsIgnoreCase("1")) {
            txt_snapchat.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txt_snapchat.setTextColor(getResources().getColor(R.color.black));
        }

        if (Constant.insta.equalsIgnoreCase("1") && Constant.twitter.equalsIgnoreCase("1")
                && Constant.snapchat.equalsIgnoreCase("1") && Constant.kik.equalsIgnoreCase("1")) {
            txt_all.setTextColor(getResources().getColor(R.color.blue));
        } else {
            txt_all.setTextColor(getResources().getColor(R.color.black));
        }

        if (Constant.insta.equalsIgnoreCase("0") && Constant.twitter.equalsIgnoreCase("0")
                && Constant.snapchat.equalsIgnoreCase("0") && Constant.kik.equalsIgnoreCase("0")) {
            txt_all.setTextColor(getResources().getColor(R.color.blue));
            txt_twitter.setTextColor(getResources().getColor(R.color.blue));
            txt_snapchat.setTextColor(getResources().getColor(R.color.blue));
            txt_instagram.setTextColor(getResources().getColor(R.color.blue));
            txt_kik.setTextColor(getResources().getColor(R.color.blue));
        }

        if (Constant.gender.equalsIgnoreCase("0")) {
            txt_both.setTextColor(getResources().getColor(R.color.blue));
        } else if (Constant.gender.equalsIgnoreCase("1")) {
            txt_male.setTextColor(getResources().getColor(R.color.blue));

        } else {
            txt_female.setTextColor(getResources().getColor(R.color.blue));
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_male:
                txt_male.setTextColor(getResources().getColor(R.color.blue));
                txt_female.setTextColor(getResources().getColor(R.color.black));
                txt_both.setTextColor(getResources().getColor(R.color.black));
                Constant.gender = "1";
                break;
            case R.id.txt_female:
                Constant.gender = "2";
                txt_male.setTextColor(getResources().getColor(R.color.black));
                txt_female.setTextColor(getResources().getColor(R.color.blue));
                txt_both.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.txt_both:

                Constant.gender = "0";
                txt_male.setTextColor(getResources().getColor(R.color.black));
                txt_female.setTextColor(getResources().getColor(R.color.black));
                txt_both.setTextColor(getResources().getColor(R.color.blue));

                break;
            case R.id.txt_instagram:
                if (!isInstagramSelected) {
                    System.out.println("********select*******");
                    Constant.insta = "1";
                    txt_instagram.setTextColor(getResources().getColor(R.color.blue));
                    isInstagramSelected = true;
                } else {
                    System.out.println("********not select*******");
                    Constant.insta = "0";
                    txt_all.setTextColor(getResources().getColor(R.color.black));
                    txt_instagram.setTextColor(getResources().getColor(R.color.black));
                    isInstagramSelected = false;
                }
                break;
            case R.id.txt_twitter:
                if (!isTwitterSelected) {
                    Constant.twitter = "1";
                    txt_twitter.setTextColor(getResources().getColor(R.color.blue));
                    isTwitterSelected = true;
                } else {
                    Constant.twitter = "0";
                    txt_all.setTextColor(getResources().getColor(R.color.black));
                    txt_twitter.setTextColor(getResources().getColor(R.color.black));
                    isTwitterSelected = false;
                }
                break;
            case R.id.txt_snapchat:
                if (!isSnapchatSelected) {
                    Constant.snapchat = "1";
                    txt_snapchat.setTextColor(getResources().getColor(R.color.blue));
                    isSnapchatSelected = true;
                } else {
                    Constant.snapchat = "0";
                    txt_all.setTextColor(getResources().getColor(R.color.black));
                    txt_snapchat.setTextColor(getResources().getColor(R.color.black));
                    isSnapchatSelected = false;
                }
                break;
            case R.id.txt_kik:
                if (!isKikSelected) {
                    Constant.kik = "1";
                    txt_kik.setTextColor(getResources().getColor(R.color.blue));
                    isKikSelected = true;
                } else {
                    Constant.kik = "0";
                    txt_all.setTextColor(getResources().getColor(R.color.black));
                    txt_kik.setTextColor(getResources().getColor(R.color.black));
                    isKikSelected = false;
                }
                break;
            case R.id.iv_back:
                if (validation()) {

                    applyFilter();
                }
                break;

            case R.id.txt_all:
                Constant.twitter = "1";
                Constant.insta = "1";
                Constant.snapchat = "1";
                Constant.kik = "1";
                txt_all.setTextColor(getResources().getColor(R.color.blue));
                txt_kik.setTextColor(getResources().getColor(R.color.blue));
                txt_instagram.setTextColor(getResources().getColor(R.color.blue));
                txt_twitter.setTextColor(getResources().getColor(R.color.blue));
                txt_snapchat.setTextColor(getResources().getColor(R.color.blue));
                isAllSelcted = true;

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (validation()) {
            applyFilter();
        }

    }

    private boolean validation() {
        if (Constant.kik.equalsIgnoreCase("0") && Constant.insta.equalsIgnoreCase("0") && Constant.twitter.equalsIgnoreCase("0") && Constant.snapchat.equalsIgnoreCase("0")) {
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content),getResources().getString(R.string.social_account_validation));
            return false;
        }
        return true;
    }

    private void applyFilter() {
        network_status = NetworkStatus.checkConnection(FilterActivity.this);
        if (network_status.equals("false")) {
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content),getResources().getString(R.string.no_internet));

        } else {
            try {
                String url = Constant.apply_filter;
                System.out.println("Login Url >" + url);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                    jsonObject.put("gender", Constant.gender);
                    jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                    jsonObject.put("device_type", Constant.DEVICE_TYPE);
                    jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                    jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                    jsonObject.put("instagram", Constant.insta);
                    jsonObject.put("kik", Constant.kik);
                    jsonObject.put("snapchat", Constant.snapchat);
                    jsonObject.put("twitter", Constant.twitter);
                    Log.e("TAG", "request json data> " + jsonObject);

                    new APIRequestWithDefaultloader(FilterActivity.this, jsonObject, url, this, Constant.API_FILTER, Constant.POST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (NullPointerException e) {
                System.out.println("Nullpointer Exception at Login Screen" + e);
            }
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        FilterResponse res = (FilterResponse) response;
        if (res.getStatus().equalsIgnoreCase("success")) {
            SharedPref.writeString(SharedPref.USER_TOKEN, res.getAccessToken());
           // Toast.makeText(FilterActivity.this, res.getDescription(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(FilterActivity.this,findViewById(android.R.id.content),res.getDescription());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();

                }
            }, 1000);

            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }else
        {
            if ((res.getCode() == 300))
            {
                AlertClass alert = new AlertClass();
                alert.customDialogforAlertMessagLoggedInDifferentDevice(this,res.getDescription());
            }else
            if (!res.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                    !res.getDescription().contains("This API accpets") ||
                    !res.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                    !res.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                SharedPref.writeString(SharedPref.USER_TOKEN, res.getAccessToken());
                SnackBarUtils.showSnackBarPink(FilterActivity.this,findViewById(android.R.id.content),res.getDescription());

            }
        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("FilterActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
