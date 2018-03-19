package com.amplify.view.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.amplify.R;
import com.amplify.controller.GridViewAdapter;
import com.amplify.model.GetAllUsers.GetAllUsers;
import com.amplify.model.GetAllUsers.UserDatum;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.BaseResponse;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bitware on 5/2/18.
 */

public class NearbyFragment extends Fragment implements APIRequest.ResponseHandler,SwipyRefreshLayout.OnRefreshListener {

    Context context;
    LinearLayout ll_noInternet;
    RelativeLayout ll_noRecords;
    GridView gridView;
    Button btnRetry;
    String arr[][];
    int startingRange = 0, endRange;
    boolean flagRefresh = false;
    int limit = 0;
    SwipyRefreshLayout swipyrefreshlayout;
    public static final int DISMISS_TIMEOUT = 2000;
    private Tracker mTracker;
    String startLimit="0";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all, container, false);
        init(view);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    if (NetworkStatus.isConnectingToInternet(getContext())) {
                        ll_noInternet.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.VISIBLE);
                        callGetUserApi();
                    } else {
                        ll_noInternet.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Nearby");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        System.out.println("*******menu nearby*******");
        if (menuVisible) {
            if (getActivity() != null) {
                if (NetworkStatus.isConnectingToInternet(getContext())) {
                    ll_noInternet.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    callGetUserApi();
                } else {
                    ll_noInternet.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void init(View view) {
        context = MyApplication.getContext();
        ll_noInternet = view.findViewById(R.id.ll_noInternet);
        gridView = view.findViewById(R.id.gridView);
        btnRetry = view.findViewById(R.id.btnRetry);
        ll_noRecords = view.findViewById(R.id.ll_noRecords);
        swipyrefreshlayout = view.findViewById(R.id.swipyrefreshlayout);
    }

    private void callGetUserApi() {
        String url = Constant.get_nearby_users + "user_id=" + SharedPref.getPreferences().getString(SharedPref.USER_ID, "")
                + "&access_token=" + SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, "")
                + "&device_id=" + SharedPref.getPreferences().getString(SharedPref.GCMREGID, "")
                + "&device_type=" + Constant.DEVICE_TYPE + "&start_limit="+"0" + "&limit=15" + "&offset=45"
                + "&registration_ip=" + SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, "")
                + "&latitude=" + SharedPref.getPreferences().getString(SharedPref.CURRENt_LAT, "")
                + "&longitude=" + SharedPref.getPreferences().getString(SharedPref.CURRENT_LONG, "");
        System.out.println("Nearby user url > " + url);
        new APIRequest(getContext(), new JSONObject(), url, this, Constant.API_GET_ALL_USERS, Constant.GET);

    }

    @Override
    public void onSuccess(BaseResponse response) {
        GetAllUsers getAllUsers = (GetAllUsers) response;
        if (getAllUsers.getStatus().equalsIgnoreCase("success")) {
            System.out.println("**********all success********");
            Constant.BLOCK_USER = "no";
            ll_noRecords.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            if (!getAllUsers.getDescription().equalsIgnoreCase("You have logged out your account.Please log in.")) {
                Constant.gender = getAllUsers.getFilters().getGender();
                Constant.insta = getAllUsers.getFilters().getInstagram();
                Constant.twitter = getAllUsers.getFilters().getTwitter();
                Constant.snapchat = getAllUsers.getFilters().getSnapchat();
                Constant.kik = getAllUsers.getFilters().getKik();
                SharedPref.writeString(SharedPref.USER_TOKEN, getAllUsers.getAccessToken());
                Constant.arrAllUsers = new ArrayList<>();
                Constant.arrAllUsers = getAllUsers.getUserData();
                //dummyStrings = Constant.arrAllUsers;

                /*int limit = Integer.parseInt(startLimit)+1;
                startLimit = String.valueOf(limit);

                arr = new String[Constant.arrAllUsers.size()][22];
                //limit = arr.length;

                System.out.println("*******size********"+Constant.arrAllUsers.size());
                for (int i = 0; i < Constant.arrAllUsers.size(); i++) {
                    arr[i][0] = Constant.arrAllUsers.get(i).getId().toString();
                    arr[i][1] = Constant.arrAllUsers.get(i).getFbProfilePic().toString();
                    arr[i][2] = Constant.arrAllUsers.get(i).getFname().toString();
                    arr[i][3] = Constant.arrAllUsers.get(i).getLname().toString();
                    arr[i][4] = Constant.arrAllUsers.get(i).getGender().toString();
                    arr[i][5] = Constant.arrAllUsers.get(i).getAge().toString();
                    arr[i][6] = Constant.arrAllUsers.get(i).getBio().toString();
                    arr[i][7] = Constant.arrAllUsers.get(i).getFbLocation().toString();
                    arr[i][8] = Constant.arrAllUsers.get(i).getDeviceGeoLocation().toString();
                    arr[i][9] = Constant.arrAllUsers.get(i).getTags().toString();
                    arr[i][10] = Constant.arrAllUsers.get(i).getViewedCount().toString();
                    arr[i][11] = Constant.arrAllUsers.get(i).getLikesCount().toString();
                    arr[i][12] = Constant.arrAllUsers.get(i).getFbRelationshipStatus().toString();
                    arr[i][13] = Constant.arrAllUsers.get(i).getFbFriendsCount().toString();
                    arr[i][14] = Constant.arrAllUsers.get(i).getInstagram().toString();
                    arr[i][15] = Constant.arrAllUsers.get(i).getTwitter().toString();
                    arr[i][16] = Constant.arrAllUsers.get(i).getSnapchat().toString();
                    arr[i][17] = Constant.arrAllUsers.get(i).getKik().toString();
                    arr[i][18] = Constant.arrAllUsers.get(i).getViewsCount().toString();
                    arr[i][19] = Constant.arrAllUsers.get(i).getDistance().toString();
                    arr[i][20] = Constant.arrAllUsers.get(i).getLikeFlag().toString();
                    arr[i][21] = Constant.arrAllUsers.get(i).getFlagged().toString();
                    //arr[i][22] = Constant.arrAllUsers.get(i).getFlagReason().toString();

                }

                if (arr.length <= 15) {
                    endRange = arr.length;
                } else {
                    endRange = 15;
                }

                Constant.arrAllUsers = new ArrayList<>();
                for (int i = startingRange; i < endRange; i++) {
                    UserDatum data = new UserDatum();
                    data.setId(arr[i][0]);
                    data.setFbProfilePic(arr[i][1]);
                    data.setFname(arr[i][2]);
                    data.setLname(arr[i][3]);
                    data.setGender(arr[i][4]);
                    data.setAge(arr[i][5]);
                    data.setBio(arr[i][6]);
                    data.setFbLocation(arr[i][7]);
                    data.setDeviceGeoLocation(arr[i][8]);
                    data.setTags(arr[i][9]);
                    data.setViewedCount(arr[i][10]);
                    data.setLikesCount(arr[i][11]);
                    data.setFbRelationshipStatus(arr[i][12]);
                    data.setFbFriendsCount(arr[i][13]);
                    data.setInstagram(arr[i][14]);
                    data.setTwitter(arr[i][15]);
                    data.setSnapchat(arr[i][16]);
                    data.setKik(arr[i][17]);
                    data.setViewsCount(arr[i][18]);
                    data.setDistance(arr[i][19]);
                    data.setLikeFlag(arr[i][20]);
                    data.setFlagged(arr[i][21]);
                    //data.setFlagReason(arr[i][22]);

                    Constant.arrAllUsers.add(data);
                }*/

                GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.row_searched_found,
                        "", Constant.arrAllUsers);
                gridView.setAdapter(adapter);
                swipyrefreshlayout.setOnRefreshListener(this);

            }
        }else {
            if ((getAllUsers.getCode() == 300)) {
                AlertClass alert = new AlertClass();
                alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),getAllUsers.getDescription());
            }else
            if (getAllUsers.getDescription().equalsIgnoreCase("No records found")) {
                SharedPref.writeString(SharedPref.USER_TOKEN, getAllUsers.getAccessToken());
                Constant.gender = getAllUsers.getFilters().getGender();
                Constant.insta = getAllUsers.getFilters().getInstagram();
                Constant.twitter = getAllUsers.getFilters().getTwitter();
                Constant.snapchat = getAllUsers.getFilters().getSnapchat();
                Constant.kik = getAllUsers.getFilters().getKik();
                ll_noRecords.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                Constant.arrAllUsers = new ArrayList<>();
                GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.row_searched_found,
                        "", Constant.arrAllUsers);
                gridView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Constant.SELECTED_TAB.equalsIgnoreCase("nearby")) {
            System.out.println("*******onresume nearby******");
            if (NetworkStatus.isConnectingToInternet(getContext())) {
                ll_noInternet.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
                callGetUserApi();
            } else {
                ll_noInternet.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        Log.d("ActivityImageFilter", "Refresh triggered at "
                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));

        System.out.println("****dir***"+direction);
        if(direction == SwipyRefreshLayoutDirection.TOP)
        {
            System.out.println("****if***");
            callGetUserApi();
        }else
        {
            System.out.println("******range1******"+startingRange+"**"+endRange);
            //System.out.println("*********limit*******"+dummyStrings.size()+"**"+limit);
            if(Constant.arrAllUsers.size() != limit) {
                if (arr.length >= endRange + 15 && arr.length >= 15) {
                    System.out.println("******iffff******");

                    startingRange = startingRange + 15;
                    endRange = startingRange + 15;

                    for (int i = startingRange; i < endRange; i++) {
                        System.out.println("********for*********" + arr[i] + "**" + i);
                        UserDatum data = new UserDatum();
                        data.setId(arr[i][0]);
                        data.setFbProfilePic(arr[i][1]);
                        data.setFname(arr[i][2]);
                        data.setLname(arr[i][3]);
                        data.setGender(arr[i][4]);
                        data.setAge(arr[i][5]);
                        data.setBio(arr[i][6]);
                        data.setFbLocation(arr[i][7]);
                        data.setDeviceGeoLocation(arr[i][8]);
                        data.setTags(arr[i][9]);
                        data.setViewedCount(arr[i][10]);
                        data.setLikesCount(arr[i][11]);
                        data.setFbRelationshipStatus(arr[i][12]);
                        data.setFbFriendsCount(arr[i][13]);
                        data.setInstagram(arr[i][14]);
                        data.setTwitter(arr[i][15]);
                        data.setSnapchat(arr[i][16]);
                        data.setKik(arr[i][17]);
                        data.setViewsCount(arr[i][18]);
                        data.setDistance(arr[i][19]);
                        data.setLikeFlag(arr[i][20]);
                        data.setFlagged(arr[i][21]);
                        Constant.arrAllUsers.add(data);
                    }
                    System.out.println("******range2******" + startingRange + "**" + endRange);
                    System.out.println("*******size*****" + Constant.arrAllUsers.size());
                    //mBinding.listview.setAdapter(new DummyListViewAdapter(this, dummyStrings));
                    GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrAllUsers);
                    gridView.setAdapter(adapter);
                    gridView.setSelection(startingRange);
                }else
                {
                    System.out.println("*******else*******");
                    startingRange = endRange;
                    endRange = arr.length;
                    for (int i = startingRange; i < endRange; i++) {
                        System.out.println("********for*********" + arr[i] + "**" + i);
                        UserDatum data = new UserDatum();
                        data.setId(arr[i][0]);
                        data.setFbProfilePic(arr[i][1]);
                        data.setFname(arr[i][2]);
                        data.setLname(arr[i][3]);
                        data.setGender(arr[i][4]);
                        data.setAge(arr[i][5]);
                        data.setBio(arr[i][6]);
                        data.setFbLocation(arr[i][7]);
                        data.setDeviceGeoLocation(arr[i][8]);
                        data.setTags(arr[i][9]);
                        data.setViewedCount(arr[i][10]);
                        data.setLikesCount(arr[i][11]);
                        data.setFbRelationshipStatus(arr[i][12]);
                        data.setFbFriendsCount(arr[i][13]);
                        data.setInstagram(arr[i][14]);
                        data.setTwitter(arr[i][15]);
                        data.setSnapchat(arr[i][16]);
                        data.setKik(arr[i][17]);
                        data.setViewsCount(arr[i][18]);
                        data.setDistance(arr[i][19]);
                        data.setLikeFlag(arr[i][20]);
                        data.setFlagged(arr[i][21]);
                        Constant.arrAllUsers.add(data);
                    }
                    System.out.println("******range2******" + startingRange + "**" + endRange);
                    //System.out.println("*******size*****" + dummyStrings.size());
                    GridViewAdapter adapter = new GridViewAdapter(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrAllUsers);
                    gridView.setAdapter(adapter);
                    gridView.setSelection(startingRange);
                }

                // dummyStrings = new ArrayList<>();

            }else
            {
                System.out.println("******no data*******");
            }
        }


        //mBinding.listview.setAdapter(new DummyListViewAdapter(ActivityImageFilter.this, dummyStrings));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipyrefreshlayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }
}
