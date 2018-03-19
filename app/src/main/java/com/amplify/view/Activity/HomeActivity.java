package com.amplify.view.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.utils.AlertClass;
import com.amplify.utils.StatusBarView;
import com.amplify.view.Fragment.FragmentUserProfile;
import com.amplify.view.MyApplication;
import com.amplify.view.MyFirebaseMessagingService;
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
import com.amplify.R;
import com.amplify.controller.MyPagerAdapter;
import com.amplify.utils.Constant;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.amplify.utils.SharedPref;
import com.amplify.view.Fragment.AllFragment;
import com.amplify.view.Fragment.FragmentLikedMe;
import com.amplify.view.Fragment.FragmentSearchedFound;
import com.amplify.view.Fragment.NearbyFragment;
import com.amplify.view.Fragment.TrendingFragment;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.BaseResponse;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity implements APIRequest.ResponseHandler, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

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

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;

    //private ActivityMainBinding mBinding;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private String mLastUpdateTimeLabel;
    static double latitude = 0.0;
    static double longitude = 0.0;
    //private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    LocationManager locationManager;
    private long startTime = 60000; // 15 MINS IDLE TIME
    private final long interval = 1 * 60000;
    ImageView imgHeart, imgTabHome, imgHeartAct, imgHomeAct, imgSearch, imgSearchAct, img_profile, img_profileAct;
    String userStatus;
    FrameLayout contentContainer;
    LinearLayout linear;
    private int[] tabIcons = {
            R.drawable.ic_tab_favourite,
            R.drawable.ic_tab_call,
            R.drawable.ic_tab_contacts
    };
    Animation anim;
    RelativeLayout relativeLikeCount;
    //RelativeLayout rl_all_fragment, rl_block_account;
    ImageView imgFilter;
    Button btnContactUs;
    LinearLayout linearBlock, linearUnblock;
    StatusBarView statusBar;
    RelativeLayout rl_home,rl_search,rl_like,rl_profile;
    TextView txtLikeCount;
    private Tracker mTracker;
    private String flag="no";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        SharedPref shared_pref = new SharedPref(HomeActivity.this);
        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        init();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("**********tab************" + tab.getPosition() + "**" + tab.getText().toString());
                if (tab.getPosition() == 0) {
                    Constant.SELECTED_TAB_LIKE = "";
                    Constant.SELECTED_TAB = "all";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.all_active);
                } else if (tab.getPosition() == 1) {
                    Constant.SELECTED_TAB_LIKE = "";
                    Constant.SELECTED_TAB = "nearby";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.nearby_active);
                } else {
                    Constant.SELECTED_TAB_LIKE = "";
                    Constant.SELECTED_TAB = "trending";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.popular_active);
                }

                viewPager.setCurrentItem(tab.getPosition());
               /* TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getResources().getColor(R.color.blue));*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                /*TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getResources().getColor(R.color.dark_grey));*/
                if (tab.getPosition() == 0) {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.all_grey);
                } else if (tab.getPosition() == 1) {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.nearby_grey);
                } else {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.popular_grey);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void init() {
        SharedPref pref = new SharedPref(this);
        txtLikeCount = findViewById(R.id.txtLikeCount);
        statusBar = findViewById(R.id.statusBar);
        btnContactUs = findViewById(R.id.btnContactUs);
        linearUnblock = findViewById(R.id.linearUnblock);
        linearBlock = findViewById(R.id.linearBlock);
        contentContainer = findViewById(R.id.contentContainer);
        linear = findViewById(R.id.linear);
        imgSearchAct = findViewById(R.id.imgSearchAct);
        imgSearch = findViewById(R.id.imgSearch);
        imgHeartAct = findViewById(R.id.imgHeartAct);
        imgHomeAct = findViewById(R.id.imgHomeAct);
        imgTabHome = findViewById(R.id.imgHome);
        imgHeart = findViewById(R.id.imgHeart);
        imgFilter = findViewById(R.id.imgFilter);
        img_profile = findViewById(R.id.img_profile);
        locationManager = (LocationManager) HomeActivity.this.getSystemService(Context.LOCATION_SERVICE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        img_profileAct = findViewById(R.id.img_profileAct);
        img_profileAct.setOnClickListener(this);
        btnContactUs.setOnClickListener(this);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        imgFilter.setOnClickListener(this);
        anim = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.slide_in_left);
        relativeLikeCount = findViewById(R.id.relativeLikeCount);
        rl_home = findViewById(R.id.rl_home);
        rl_like = findViewById(R.id.rl_like);
        rl_profile = findViewById(R.id.rl_profile);
        rl_search = findViewById(R.id.rl_search);

        imgSearch.setOnClickListener(this);
        imgHeart.setOnClickListener(this);
        imgTabHome.setOnClickListener(this);
        img_profile.setOnClickListener(this);
        imgFilter.setOnClickListener(this);

        rl_home.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        rl_profile.setOnClickListener(this);
        rl_like.setOnClickListener(this);


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
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //showRationaleDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllFragment(), "All");
        adapter.addFragment(new NearbyFragment(), "Nearby");
        adapter.addFragment(new TrendingFragment(), "Trending");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void setupTabIcons() {

        ImageView tabOne = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setImageResource(R.drawable.all_active);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        ImageView tabTwo = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setImageResource(R.drawable.nearby_grey);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        ImageView tabThree = (ImageView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setImageResource(R.drawable.popular_grey);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    //receiver to receive chat & notification counterv when activity is open
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, "").equalsIgnoreCase("") || SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, "").equalsIgnoreCase("0")) {
                txtLikeCount.setText("");
                txtLikeCount.setVisibility(View.GONE);
                relativeLikeCount.setVisibility(View.GONE);
            } else {
                txtLikeCount.setText(SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, ""));
                txtLikeCount.setVisibility(View.VISIBLE);
                relativeLikeCount.setVisibility(View.VISIBLE);
            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("******HomeFragment******");
        try {
            this.registerReceiver(mMessageReceiver, new IntentFilter("homeScreen"));

        } catch (Exception e) {
        }
        Log.e("TAG","*****fromLikeNotification****"+Constant.fromLikeNotification);
        if (Constant.fromLikeNotification == 1){
            Log.e("TAG","*****if condition****"+Constant.fromLikeNotification);
            txtLikeCount.setVisibility(View.GONE);
            relativeLikeCount.setVisibility(View.GONE);
            SharedPref.writeString(SharedPref.NOTIFICATION_COUNTER,"0");
            statusBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            Constant.SELECTED_TAB_LIKE = Constant.NAVIGATE_TAB_LIKE;
            Constant.SELECTED_TAB = Constant.NAVIGATE_TAB_USER;
            //relativeLikeCount.setVisibility(View.VISIBLE);
            imgSearch.setVisibility(View.VISIBLE);
            imgSearchAct.setVisibility(View.GONE);
            imgTabHome.setVisibility(View.VISIBLE);
            imgHomeAct.setVisibility(View.GONE);
            imgHeartAct.setVisibility(View.VISIBLE);
            imgHeart.setVisibility(View.GONE);
            linear.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
            img_profile.setImageResource(R.drawable.tab_profile);
            img_profileAct.setVisibility(View.GONE);
            img_profile.setVisibility(View.VISIBLE);
            Constant.replaceFragment(new FragmentLikedMe(), getSupportFragmentManager(), null);
        }else {
            Log.e("TAG","*****else condition****"+Constant.fromLikeNotification);
        }
        if (Constant.isUpdateProfile.equalsIgnoreCase("yes")){
            statusBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
            imgSearch.setVisibility(View.VISIBLE);
            imgSearchAct.setVisibility(View.GONE);
            imgTabHome.setVisibility(View.VISIBLE);
            imgHomeAct.setVisibility(View.GONE);
            imgHeartAct.setVisibility(View.GONE);
            imgHeart.setVisibility(View.VISIBLE);
            linear.setVisibility(View.GONE);
            contentContainer.setVisibility(View.VISIBLE);
            img_profileAct.setVisibility(View.VISIBLE);
            img_profile.setVisibility(View.GONE);
            Constant.replaceFragment(new FragmentUserProfile(), getSupportFragmentManager(), null);
        }else {
            Log.e("TAG","*****else condition****"+Constant.isUpdateProfile);
        }

        if (SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, "").equalsIgnoreCase("") ||
                SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, "").equalsIgnoreCase("0")) {
            txtLikeCount.setText("");
            txtLikeCount.setVisibility(View.GONE);
            relativeLikeCount.setVisibility(View.GONE);
        } else {
            txtLikeCount.setText(SharedPref.getPreferences().getString(SharedPref.NOTIFICATION_COUNTER, ""));
            txtLikeCount.setVisibility(View.VISIBLE);
            relativeLikeCount.setVisibility(View.VISIBLE);
        }
        if (Constant.BLOCK_USER.equalsIgnoreCase("yes")) {
            System.out.println("******if******");
            System.out.println("******if******" + Constant.BLOCK_USER);
            linearBlock.setVisibility(View.VISIBLE);
            linearUnblock.setVisibility(View.GONE);
        }else
        {
            linearBlock.setVisibility(View.GONE);
            linearUnblock.setVisibility(View.VISIBLE);
        }

        userStatus = "1";
        isPlayServicesAvailable(this);

        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        mTracker.setScreenName("HomeActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("***********stop**********");
        userStatus = "0";
        //UserOnOffStatus status = new UserOnOffStatus(userStatus);
        //status.userOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("***********destroy**********");
        unregisterReceiver(mMessageReceiver);
        userStatus = "0";
        //UserOnOffStatus status = new UserOnOffStatus(userStatus);
        //status.userOffline();
    }

    @Override
    public void onSuccess(BaseResponse response) {

    }

    @Override
    public void onFailure(BaseResponse response) {
        Toast.makeText(HomeActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.imgFilter:
                i = new Intent(HomeActivity.this, FilterActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btnContactUs:
                /*Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
                startActivity(intent);*/
                Constant.sendMail("Request for unblock");
                break;

            case R.id.imgSearch:
                Constant.SELECTED_TAB_LIKE = Constant.NAVIGATE_TAB_LIKE;
                Constant.SELECTED_TAB = Constant.NAVIGATE_TAB_USER;
                relativeLikeCount.setVisibility(View.GONE);
                imgSearch.setVisibility(View.GONE);
                imgSearchAct.setVisibility(View.VISIBLE);
                imgTabHome.setVisibility(View.VISIBLE);
                imgHomeAct.setVisibility(View.GONE);
                imgHeartAct.setVisibility(View.GONE);
                imgHeart.setVisibility(View.VISIBLE);
                linear.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                img_profile.setImageResource(R.drawable.tab_profile);
                img_profileAct.setVisibility(View.GONE);
                img_profile.setVisibility(View.VISIBLE);
                Constant.replaceFragment(new FragmentSearchedFound(), getSupportFragmentManager(), null);
                break;

            case R.id.imgHeart:
                txtLikeCount.setVisibility(View.GONE);
                relativeLikeCount.setVisibility(View.GONE);
                SharedPref.writeString(SharedPref.NOTIFICATION_COUNTER,"0");

                MyFirebaseMessagingService msg = new MyFirebaseMessagingService();
                msg.cancelNotification();

                statusBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
                Constant.SELECTED_TAB_LIKE = Constant.NAVIGATE_TAB_LIKE;
                Constant.SELECTED_TAB = Constant.NAVIGATE_TAB_USER;
                //relativeLikeCount.setVisibility(View.VISIBLE);
                imgSearch.setVisibility(View.VISIBLE);
                imgSearchAct.setVisibility(View.GONE);
                imgTabHome.setVisibility(View.VISIBLE);
                imgHomeAct.setVisibility(View.GONE);
                imgHeartAct.setVisibility(View.VISIBLE);
                imgHeart.setVisibility(View.GONE);
                linear.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                img_profile.setImageResource(R.drawable.tab_profile);
                img_profileAct.setVisibility(View.GONE);
                img_profile.setVisibility(View.VISIBLE);
                Constant.replaceFragment(new FragmentLikedMe(), getSupportFragmentManager(), null);
                break;

            case R.id.imgHome:
                statusBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
                Constant.SELECTED_TAB_LIKE = Constant.NAVIGATE_TAB_LIKE;
                Constant.SELECTED_TAB = Constant.NAVIGATE_TAB_USER;
                relativeLikeCount.setVisibility(View.GONE);
                imgSearch.setVisibility(View.VISIBLE);
                imgSearchAct.setVisibility(View.GONE);
                imgTabHome.setVisibility(View.GONE);
                imgHomeAct.setVisibility(View.VISIBLE);
                imgHeartAct.setVisibility(View.GONE);
                imgHeart.setVisibility(View.VISIBLE);
                linear.setVisibility(View.VISIBLE);
                contentContainer.setVisibility(View.GONE);
                img_profile.setImageResource(R.drawable.tab_profile);
                img_profileAct.setVisibility(View.GONE);
                img_profile.setVisibility(View.VISIBLE);
                linear.setAnimation(anim);
                break;
            case R.id.img_profile:
                statusBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
                imgSearch.setVisibility(View.VISIBLE);
                imgSearchAct.setVisibility(View.GONE);
                imgTabHome.setVisibility(View.VISIBLE);
                imgHomeAct.setVisibility(View.GONE);
                imgHeartAct.setVisibility(View.GONE);
                imgHeart.setVisibility(View.VISIBLE);
                linear.setVisibility(View.GONE);
                contentContainer.setVisibility(View.VISIBLE);
                img_profileAct.setVisibility(View.VISIBLE);
                img_profile.setVisibility(View.GONE);
                Constant.replaceFragment(new FragmentUserProfile(), getSupportFragmentManager(), null);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //popup_age_selector.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_exit);

        // set the custom popup_age_selector components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
     //   TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);

//        txtTitle.setText("Exit");
//        txtMsg.setText("Do you want to exit?");

        // if button is clicked, close the custom popup_age_selector
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup_age_selector.dismiss();
              //  finish();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();

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
                        System.out.println("*************success****fff********");
                        //  flagLocation = true;
                        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, HomeActivity.this);

                       /* Intent i = new Intent(LocationPermissionActivity.this, HomeActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // 設定が有効になっていないのでダイアログを表示する
                        try {
                            status.startResolutionForResult(HomeActivity.this, REQUEST_CHECK_SETTINGS);
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
        System.out.println("********act*********" + requestCode + "**" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

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
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        /*if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }*/
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

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

}




