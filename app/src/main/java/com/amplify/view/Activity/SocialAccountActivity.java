package com.amplify.view.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.AddSocialAccount;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SharedPref;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 11/30/2017.
 */

public class SocialAccountActivity extends Activity implements View.OnClickListener, APIRequestWithDefaultloader.ResponseHandler {

    String nick_name = "", profilePic;
    Button btnNext;
    SharedPref pref;
    EditText edtInsta, edtTwitter, edtSnapchat, edtKik;
    String token, FbId, instagram = "", snapchat = "", twitter = "", kik = "";
    TextView txtName;
    ImageView imgBack, imgInstagram, imgTwitter, imgSnapchat, imgKik;
    String network_status;
    private Tracker mTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_account);
        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        Intent intent = getIntent();
        pref = new SharedPref(this);
        edtInsta = findViewById(R.id.edtInsta);
        edtSnapchat = findViewById(R.id.edtSnapchat);
        edtKik = findViewById(R.id.edtKik);
        edtTwitter = findViewById(R.id.edtTwitter);

        imgInstagram = findViewById(R.id.imgInstagram);
        imgKik = findViewById(R.id.imgKik);
        imgSnapchat = findViewById(R.id.imgSnapchat);
        imgTwitter = findViewById(R.id.imgTwitter);

        imgBack = findViewById(R.id.imgBack);
        txtName = (TextView) findViewById(R.id.txtName);
        btnNext = (Button) findViewById(R.id.btnNext);
        profilePic = intent.getStringExtra("profilePic");
        txtName.setText(SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, "") + "!");//SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, "") + "!");

        btnNext.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        edtInsta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    instagram = "";
                    imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.ic_instagram));
                } else {
                    instagram = String.valueOf(s);
                    imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.icon_insta_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtTwitter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    twitter = "";
                    imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter));
                } else {
                    twitter = String.valueOf(s);
                    imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.icon_twitter_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtKik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    kik = "";
                    imgKik.setImageDrawable(getResources().getDrawable(R.drawable.ic_kik));
                } else {
                    kik = String.valueOf(s);
                    imgKik.setImageDrawable(getResources().getDrawable(R.drawable.icon_kik_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtSnapchat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    snapchat = "";
                    imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.ic_snapchat));
                } else {
                    snapchat = String.valueOf(s);
                    imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.icon_snap_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                callSocialAccountApi();
                break;

            case R.id.imgBack:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;
        }
    }

    private void callSocialAccountApi() {
        network_status = NetworkStatus.checkConnection(SocialAccountActivity.this);
        if (!network_status.equals("false")) {
            if (!instagram.isEmpty() || !twitter.isEmpty() || !snapchat.isEmpty() || !kik.isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("instagram", instagram);
                    jsonObject.put("twitter", twitter);
                    jsonObject.put("snapchat", snapchat);
                    jsonObject.put("kik", kik);
                    jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                    jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                    jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                    jsonObject.put("device_type", Constant.DEVICE_TYPE);
                    jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                    Log.e("TAG", "Request >> " + jsonObject);

                    new APIRequestWithDefaultloader(SocialAccountActivity.this, jsonObject, Constant.add_social_account, this, Constant.API_ADD_SOCIAL_ACCT, Constant.POST);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
               // Toast.makeText(SocialAccountActivity.this, "Please Enter At Least One Social Account", Toast.LENGTH_SHORT).show();
                SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content),getResources().getString(R.string.social_account_validation));

            }
        } else {
           // Toast.makeText(SocialAccountActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));

        }
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    public void onSuccess(BaseResponse response) {
        AddSocialAccount socialAccount = (AddSocialAccount) response;
        if (socialAccount.getStatus().equalsIgnoreCase("Success")) {
            SharedPref.writeString(SharedPref.USER_TOKEN, socialAccount.getAccessToken());
            // Toast.makeText(SocialAccountActivity.this, socialAccount.getDescription(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(getApplicationContext(), findViewById(android.R.id.content),"Social accounts added successfully");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SocialAccountActivity.this, TagActivity.class);
                    startActivity(i);

                }
            }, 1000);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            if (!socialAccount.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                    !socialAccount.getDescription().contains("This API accpets") ||
                    !socialAccount.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                    !socialAccount.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                SharedPref.writeString(SharedPref.USER_TOKEN, socialAccount.getAccessToken());
                Toast.makeText(SocialAccountActivity.this, socialAccount.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("SocialAccountActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
