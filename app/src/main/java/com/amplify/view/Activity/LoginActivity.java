package com.amplify.view.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.model.LoginResponse;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.amplify.R;

import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends Activity implements APIRequestWithDefaultloader.ResponseHandler {

    FrameLayout linearFbLogin;
    CallbackManager callbackManager;
    String network_status;
    URL profile_pic;
    TextView text_policy;
    List<String> permissionNeeds;
    String profile_pic_uri, name = "", email = "", gender = "", fbId = "", bdate = "", mobile = "", about = "", fName = "", lName = "", ipAddress = "", hometown="", location="", relationship_status = "", friendsCount = "", bio = "",age="",city="";
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            // String packageName = info.packageName;
            Constant.VERSION = info.versionCode;

            // String versionName = info.versionName;
            System.out.println("********versionCode*********" + Constant.VERSION);


        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        SharedPref shared_pref = new SharedPref(LoginActivity.this);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
        }

        /*System.out.println("**************login************" + SharedPref.getPreferences().getString(SharedPref.REG_COMPLETE, ""));
        if (SharedPref.getPreferences().getString(SharedPref.REG_COMPLETE, "").equalsIgnoreCase("1")) {
            System.out.println("*******already login*******");
            Intent intent1 = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }*/

        init();

    }

    private void loginWebservice() {
        network_status = NetworkStatus.checkConnection(LoginActivity.this);
        if (network_status.equals("false")) {
//            AlertClass alert = new AlertClass();
//            alert.customDialog(LoginActivity.this, "", getResources().getString(R.string.no_internet));
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        } else {
            try {
                String url = Constant.create_account;
                System.out.println("Login Url >" + url);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("fb_id", SharedPref.getPreferences().getString(SharedPref.FB_ID, ""));
                    jsonObject.put("fname", SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, ""));
                    jsonObject.put("lname", SharedPref.getPreferences().getString(SharedPref.LAST_NAME, ""));
                    jsonObject.put("gender", gender);
                    jsonObject.put("age", age);
                    jsonObject.put("birthdate", bdate);
                    jsonObject.put("bio", bio);
                    jsonObject.put("fb_location", location);
                    jsonObject.put("fb_relationship_status", relationship_status);
                    jsonObject.put("fb_friends_count", friendsCount);
                    jsonObject.put("fb_friends_list", "");
                    jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                    jsonObject.put("device_type", Constant.DEVICE_TYPE);
                    jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                    jsonObject.put("fb_verified", "1");
                    jsonObject.put("fb_profile_pic", profile_pic_uri);
                    jsonObject.put("version",Build.VERSION.RELEASE);

                    Log.e("TAG", "request json data> " + jsonObject);

                    new APIRequestWithDefaultloader(LoginActivity.this, jsonObject, url, this, Constant.API_CREATE_ACCOUNT, Constant.POST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (NullPointerException e) {
                System.out.println("Nullpointer Exception at Login Screen" + e);
            }
        }
    }

    private void init() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        System.out.println("********ip1*********" + ipAddress);

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Constant.carrierName = manager.getNetworkOperatorName();

        if (ipAddress.equalsIgnoreCase("0.0.0.0")) {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            if (Character.isDigit(addr.getHostAddress().charAt(0))) {
                                System.out.println("*********ip2*******" + addr.getHostAddress());
                                ipAddress = addr.getHostAddress();
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

            SharedPref.writeString(SharedPref.IP_ADDRESS, ipAddress);
            text_policy = (TextView) findViewById(R.id.text_policy);
            linearFbLogin = (FrameLayout) findViewById(R.id.linearFbLogin);
            /*Button crashButton =findViewById(R.id.btnCrash);
            crashButton.setText("Crash!");
            crashButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                Crashlytics.getInstance().crash(); // Force a crash
                }
             });*/

            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {
            } catch (NoSuchAlgorithmException e) {
            }

            linearFbLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Action")
                            .setAction("Login")
                            .build());
                    network_status = NetworkStatus.checkConnection(LoginActivity.this);
                    if (network_status.equals("false")) {
                        SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));

                    } else {
                        loginWithFb();
                    }
                }
            });
            SpannableString ss = new SpannableString("Terms of Use and Privacy Policy");
            ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {

                    Intent i = new Intent(LoginActivity.this, TermsActivity.class);
                    startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };

            ClickableSpan clickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent i = new Intent(LoginActivity.this, PrivacyPolicyActivity.class);
                    startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(clickableSpan1, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(clickableSpan2, 17, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_policy.setText(ss);
            text_policy.setMovementMethod(LinkMovementMethod.getInstance());
            //text_policy.setHighlightColor(Color.BLUE);
    }

    private void loginWithFb() {
        callbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile","user_birthday","user_location","user_hometown", "user_relationships","user_friends","user_about_me"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {

                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                fbId = json.getString("id");
//                                                profile_pic = new URL("http://graph.facebook.com/" + fbId + "/picture?type=large");
                                                profile_pic = new URL("http://graph.facebook.com/" + fbId + "/picture?width=500&height=500");
                                                if (json.has("email")) {
                                                    email = json.getString("email");
                                                }

                                                name = json.getString("name");
                                                String[] namestr = name.split(" ");
                                                fName = namestr[0];
                                                lName = namestr[1];
                                                gender = json.getString("gender");
                                                if (json.has("birthday")){
                                                    bdate = json.getString("birthday");
                                                    System.out.println("Fb birthdate >>" +bdate);
                                                    String[] bdayArray = bdate.split("/");
                                                    String month =bdayArray[0];
                                                    String day = bdayArray[1];
                                                    String year = bdayArray[2];
                                                    age = Constant.calculateAge(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
                                                    System.out.println("Calculated age >> " +age);
                                                }else {
                                                    age = "21";
                                                }


                                               // country = json.getJSONObject("location").getString("country");
                                                location = json.getJSONObject("location").getJSONObject("location").getString("country");
                                                city = json.getJSONObject("location").getJSONObject("location").getString("city");
                                                SharedPref.writeString(SharedPref.USER_ADDRESS,location);
                                                SharedPref.writeString(SharedPref.USER_CITY,city);
                                                if(json.has("relationship_status")) {
                                                    relationship_status = json.getString("relationship_status");
                                                }

                                                if(json.has("about")) {
                                                    bio = json.getString("about");
                                                }

                                                friendsCount = json.getJSONObject("friends").getJSONObject("summary").getString("total_count");
                                                SharedPref.writeString(SharedPref.FIRST_NAME, fName);
                                                SharedPref.writeString(SharedPref.LAST_NAME, lName);
                                                SharedPref.writeString(SharedPref.FB_ID, fbId);
                                                profile_pic_uri = profile_pic.toString();
//                                                Log.e("TAG","fb pic url >> "+profile_pic_uri);

                                                LoginManager.getInstance().logOut();
                                                loginWebservice();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email,gender,birthday,location{location},hometown,relationship_status,friends,about");

                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG_CANCEL", "On cancel");
                        LoginManager.getInstance().logOut();
                    }

                    @Override
                    public void onError(FacebookException error) {

                        Log.d("TAG_ERROR", error.toString());
                        if (error instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                                loginWithFb();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);

    }

    @Override
    public void onSuccess(BaseResponse response) {
        System.out.println("rootjson======" + response.toString());
        LoginResponse responseLogIn = (LoginResponse) response;

        if (responseLogIn.getStatus().toString().equalsIgnoreCase("success")) {

            SharedPref.writeString(SharedPref.USER_ID, String.valueOf(responseLogIn.getUserId()));
            SharedPref.writeString(SharedPref.USER_TOKEN, responseLogIn.getAccessToken());
            SharedPref.writeString(SharedPref.REG_COMPLETE, responseLogIn.getRegComplete());
            SharedPref.writeString(SharedPref.USER_EMAIL, email);
            // Toast.makeText(LoginActivity.this, responseLogIn.getDescription().toString(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(getApplicationContext(), findViewById(android.R.id.content), responseLogIn.getDescription());


            if (responseLogIn.getRegComplete().equalsIgnoreCase("1")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent1 = new Intent(LoginActivity.this, HomeActivity.class);

                        startActivity(intent1);

                    }
                }, 1000);

                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            } else {
                if (responseLogIn.getDescription().equalsIgnoreCase("Your version is outdated.Please update the App")) {

                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessageForAppVersion(LoginActivity.this,
                            responseLogIn.getDescription(), Constant.VERSION);

                }
//                Intent intent1 = new Intent(LoginActivity.this, SocialAccountActivity.class);
//                intent1.putExtra("name", name);
//                intent1.putExtra("profilePic", profile_pic_uri);
//                startActivity(intent1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent1 = new Intent(LoginActivity.this, SocialAccountActivity.class);
                        intent1.putExtra("name", name);
                        intent1.putExtra("profilePic", profile_pic_uri);
                        startActivity(intent1);

                    }
                }, 1000);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);

                }
            }, 1000);
            Intent intent1 = new Intent(LoginActivity.this, HomeActivity.class);
            SharedPref.writeString(SharedPref.USER_ID, String.valueOf(responseLogIn.getUserId()));
            SharedPref.writeString(SharedPref.USER_TOKEN, responseLogIn.getAccessToken());

            startActivity(intent1);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);

        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("LoginActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
      //  finish();
        finishAffinity();
        System.exit(0);
    }
}
