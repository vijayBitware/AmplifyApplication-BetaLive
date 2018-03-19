package com.amplify.view.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.LocationResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.amplify.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Admin on 1/5/2018.
 */
public class LocationPermissionActivity extends Activity implements APIRequestWithDefaultloader.ResponseHandler, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    LocationManager locationManager;
    ///////////////////////////////////////////////////////
    protected static final String TAG = "location-updates-sample";
    /**
     * 10秒間隔で位置情報を更新。実際には多少頻度が多くなるかもしれない。
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 7200000;

    /**
     * 最速の更新間隔。この値より頻繁に更新されることはない。
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private static final int REQUEST_CHECK_SETTINGS = 10;

    //private ActivityMainBinding mBinding;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;
    double latitude, longitude;
    String network_status;
    boolean flagLocation = false;
    SharedPref sharedPref;
    ///////////////////////////////////////////////////////

    Button btnAskMe;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    ImageView imgBack;
    private Tracker mTracker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }*/
        setContentView(R.layout.activity_location_permission);
        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        sharedPref = new SharedPref(this);
        Intent intent = getIntent();
        btnAskMe = (Button) findViewById(R.id.btnAskMe);
        imgBack = findViewById(R.id.imgBack);


        btnAskMe.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        buildGoogleApiClient();
        if (!isPlayServicesAvailable(this)) return;
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
        } else {
            return;
        }

        if (Build.VERSION.SDK_INT < 23) {
            // setButtonsEnabledState();
            startLocationUpdates();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // setButtonsEnabledState();
            startLocationUpdates();
        } /*else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }*/

    }

    ////////////////////////////////////////////////////////////////////
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        System.out.println("****************create*******");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        // 現在位置の取得の前に位置情報の設定が有効になっているか確認する
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // 設定が有効になっているので現在位置を取得する
                        System.out.println("*************success************");
                        flagLocation = true;
                        if (ActivityCompat.checkSelfPermission(LocationPermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationPermissionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationPermissionActivity.this);

                       /*Intent i = new Intent(LocationPermissionActivity.this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(LocationPermissionActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void submitLocation() {
        network_status = NetworkStatus.checkConnection(LocationPermissionActivity.this);
        if (network_status.equals("false")) {
            AlertClass alert = new AlertClass();
            alert.customDialog(LocationPermissionActivity.this, "", getResources().getString(R.string.no_internet));
        } else {
            try {

                String url = Constant.add_location;
                System.out.println("Login Url >" + url);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                    jsonObject.put("latitude", mCurrentLocation.getLatitude());
                    jsonObject.put("longitude", mCurrentLocation.getLongitude());
                    jsonObject.put("device_type", Constant.DEVICE_TYPE);
                    jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                    jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                    jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));

                    Log.e("TAG", "request > " + jsonObject);

                    new APIRequestWithDefaultloader(LocationPermissionActivity.this, jsonObject, url, this, Constant.API_LOCATION, Constant.POST);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (NullPointerException e) {
                System.out.println("Nullpointer Exception at Login Screen" + e);
            }
        }
    }

    private void updateUI() {
        if (mCurrentLocation == null) return;

    }

    protected void stopLocationUpdates() {
        Log.i(TAG, "stopLocationUpdates");

    }

    public static boolean isPlayServicesAvailable(Context context) {
        // Google Play Service APKが有効かどうかチェックする
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, resultCode, 2).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        isPlayServicesAvailable(this);

        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        mTracker.setScreenName("LocationPermissionActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        /*if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }*/
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        //mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        // int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        // int result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // int result5 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (result2 == PackageManager.PERMISSION_GRANTED || result3 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    //turnOnGps();
                    //Intent i = new Intent(this, HomeActivity.class);
                    //startActivity(i);
                    System.out.println("************granted*********");
                } else {
                    //not granted
                    AlertDialog.Builder builder = new AlertDialog.Builder(LocationPermissionActivity.this);
                    builder.setTitle("Need Permissions");
                    builder.setMessage("This app needs Location permissions.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            requestForSpecificPermission();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAskMe:
                int MyVersion = Build.VERSION.SDK_INT;
                if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    System.out.println("***********version*********" + MyVersion);
                    if (!checkIfAlreadyhavePermission()) {
                        requestForSpecificPermission();
                    } else {
                        startLocationUpdates();
                        submitLocation();
                       /* Intent i = new Intent(this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                    }
                } else {
                    startLocationUpdates();
                    submitLocation();
                    /*Intent i = new Intent(this, HomeActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                }
                break;
            case R.id.imgBack:
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        System.out.println("*******locccc******" + mCurrentLocation.getLatitude() + "****" + mCurrentLocation.getLongitude());
        SharedPref.writeString(SharedPref.CURRENt_LAT, String.valueOf(mCurrentLocation.getLatitude()));
        SharedPref.writeString(SharedPref.CURRENT_LONG, String.valueOf(mCurrentLocation.getLongitude()));

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onSuccess(BaseResponse response) {
        System.out.println("******res******" + response.toString());
        LocationResponse locationRes = (LocationResponse) response;
        if (locationRes.getStatus().equalsIgnoreCase("Success")) {
            SharedPref.writeString(SharedPref.USER_TOKEN, locationRes.getAccessToken());
            SharedPref.writeString(SharedPref.REG_COMPLETE, "1");
            // Toast.makeText(LocationPermissionActivity.this, locationRes.getDescription(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(getApplicationContext(), findViewById(android.R.id.content),getResources().getString(R.string.geo_location_success));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LocationPermissionActivity.this, HomeActivity.class);
                    startActivity(i);

                }
            }, 1000);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            if (!locationRes.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                    !locationRes.getDescription().contains("This API accpets") ||
                    !locationRes.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                    !locationRes.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                SharedPref.writeString(SharedPref.USER_TOKEN, locationRes.getAccessToken());
                Toast.makeText(LocationPermissionActivity.this, locationRes.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }
}