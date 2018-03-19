package com.amplify.view.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.DeleteAccount;
import com.amplify.model.LogOutResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;

import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestNew;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 2/8/2018.
 */

public class ActivitySetting extends Activity implements View.OnClickListener, APIRequestWithDefaultloader.ResponseHandler {
    ImageView imgBack;
    Button btnLogOut;
    TextView txtDeleteAccount, txtContactUs, txtPrivacyPolicy, txtTermsServices;
    String network_status;
    private Tracker mTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        imgBack = findViewById(R.id.imgBack);
        btnLogOut = findViewById(R.id.btnLogOut);
        txtDeleteAccount = findViewById(R.id.txtDeleteAccount);
        txtContactUs = findViewById(R.id.txtContactUs);
        txtTermsServices = findViewById(R.id.txtTermsServices);
        txtPrivacyPolicy = findViewById(R.id.txtPrivacyPolicy);

        imgBack.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        txtDeleteAccount.setOnClickListener(this);
        txtContactUs.setOnClickListener(this);
        txtTermsServices.setOnClickListener(this);
        txtPrivacyPolicy.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.imgBack:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.txtContactUs:
                Constant.sendMail("Feedback/question about amplify");
                break;
            case R.id.txtTermsServices:
                i = new Intent(ActivitySetting.this, TermsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.txtPrivacyPolicy:
                i = new Intent(ActivitySetting.this, PrivacyPolicyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.btnLogOut:
                logout();
                break;
            case R.id.txtDeleteAccount:
                showDeleteAccountDialog();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void logout() {
        final Dialog dialog = new Dialog(ActivitySetting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_logout);

        // set the custom dialog components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                network_status = NetworkStatus.checkConnection(ActivitySetting.this);
                if (!network_status.equals("false")) {
                    LogOutAccount();
                } else {
                    Toast.makeText(ActivitySetting.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

                finish();
                Intent intent = new Intent(ActivitySetting.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void LogOutAccount() {
        network_status = NetworkStatus.checkConnection(ActivitySetting.this);
        if (network_status.equals("false")) {
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        } else {
            Constant.SELECTED_TAB = "";
            Constant.SELECTED_TAB_LIKE = "";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                jsonObject.put("device_type", Constant.DEVICE_TYPE);
                jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                Log.e("TAG", "Request > " + jsonObject);

                new APIRequestWithDefaultloader(ActivitySetting.this, jsonObject, Constant.logout, this, Constant.API_LOGOUT, Constant.POST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showDeleteAccountDialog() {
        final Dialog dialog = new Dialog(ActivitySetting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_delete);

        // set the custom dialog components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog.dismiss();
                network_status = NetworkStatus.checkConnection(ActivitySetting.this);
                if (!network_status.equals("false")) {
                    DeleteAccount();
                } else {
                    Toast.makeText(ActivitySetting.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void DeleteAccount() {
        network_status = NetworkStatus.checkConnection(ActivitySetting.this);
        if (!network_status.equals("false")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                jsonObject.put("device_type", Constant.DEVICE_TYPE);
                jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                Log.e("TAG", "Request > " + jsonObject);

                new APIRequestWithDefaultloader(ActivitySetting.this, jsonObject, Constant.delete_my_account, this, Constant.API_DELETE_ACCOUNT, Constant.POST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        System.out.println("*********api name*********" + response.getApiName());
        if (response.getApiName() == 113) {
            DeleteAccount deleteAccount = (DeleteAccount) response;
            if (deleteAccount.getStatus().equalsIgnoreCase("success")) {
                SharedPref.writeString(SharedPref.REG_COMPLETE, "0");
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);
                finish();
                Toast.makeText(ActivitySetting.this, deleteAccount.getDescription(), Toast.LENGTH_SHORT).show();

            } else {
                if (deleteAccount.getStatus().equalsIgnoreCase("Failed")) {
                    if ((deleteAccount.getCode() == 300))
                    {
                        AlertClass alert = new AlertClass();
                        alert.customDialogforAlertMessagLoggedInDifferentDevice(this,deleteAccount.getDescription());
                    }
                }
                //Toast.makeText(ActivitySetting.this, deleteAccount.getDescription(), Toast.LENGTH_SHORT).show();
            }
        } else {

            System.out.println("******logout*******");
            LogOutResponse logOutResponse = (LogOutResponse) response;
            if (logOutResponse.getStatus().equalsIgnoreCase("success")) {
                System.out.println("*******logout success*******");
                SharedPref.writeString(SharedPref.REG_COMPLETE, "0");
                Toast.makeText(ActivitySetting.this, logOutResponse.getDescription(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity(intent);
                finish();
            } else {
                if (logOutResponse.getStatus().equalsIgnoreCase("Failed")) {
                    if ((logOutResponse.getCode() == 300))
                    {
                        AlertClass alert = new AlertClass();
                        alert.customDialogforAlertMessagLoggedInDifferentDevice(this,logOutResponse.getDescription());
                    }
                }
                //Toast.makeText(ActivitySetting.this, deleteAccount.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("ActivitySetting");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
