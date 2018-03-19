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
import android.widget.Toast;

import com.amplify.R;
import com.amplify.controller.GridViewAdapter;
import com.amplify.controller.GridViewAdapterSecond;
import com.amplify.model.FilterResponse;
import com.amplify.model.GetLikedMe.GetLikedMeResponse;
import com.amplify.model.GetLikedMe.UserDatum;
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

public class LikedMeFragment extends Fragment implements APIRequest.ResponseHandler,SwipyRefreshLayout.OnRefreshListener {

    String network_status;
    Context context;
    GridView gridView;
    LinearLayout ll_noInternet;
    RelativeLayout ll_noRecords;
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

        View view = inflater.inflate(R.layout.fragment_viewed_me, container, false);

        init(view);
        network_status = NetworkStatus.checkConnection(context);
        if (!network_status.equals("false")) {
            ll_noInternet.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            callGetUserApi();
        } else {
            ll_noInternet.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);
        }
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("LikedMe");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @SuppressLint("ResourceAsColor")
    private void init(View view) {
        System.out.println("********LikedMeFragment*******");
        swipyrefreshlayout = view.findViewById(R.id.swipyrefreshlayout);
        context = MyApplication.getContext();
        gridView = (GridView) view.findViewById(R.id.gridView);
        ll_noInternet = view.findViewById(R.id.ll_noInternet);
        btnRetry = view.findViewById(R.id.btnRetry);
        ll_noRecords = view.findViewById(R.id.ll_noRecords);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                network_status = NetworkStatus.checkConnection(context);
                if (!network_status.equals("false")) {
                    ll_noInternet.setVisibility(View.INVISIBLE);
                    gridView.setVisibility(View.VISIBLE);
                    callGetUserApi();
                } else {
                    ll_noInternet.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.INVISIBLE);

                }

            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        System.out.println("******liked me******");
        context = MyApplication.getContext();
        if (menuVisible) {
            if (getActivity() != null) {
                network_status = NetworkStatus.checkConnection(context);
                if (!network_status.equals("false")) {
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

    private void callGetUserApi() {
        String url = Constant.get_likes_me_users + "user_id=" + SharedPref.getPreferences().getString(SharedPref.USER_ID, "")
                + "&access_token=" + SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, "")
                + "&device_id=" + SharedPref.getPreferences().getString(SharedPref.GCMREGID, "")
                + "&device_type=" + Constant.DEVICE_TYPE + "&start_limit="+"0" + "&limit=15" + "&offset=45"
                + "&registration_ip=" + SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, "")
                + "&latitude=" + SharedPref.getPreferences().getString(SharedPref.CURRENt_LAT, "")
                + "&longitude=" + SharedPref.getPreferences().getString(SharedPref.CURRENT_LONG, "");

        System.out.println("****liked me*****" + url);
        new APIRequest(getContext(), new JSONObject(), url, this, Constant.API_GET_LIKED_ME_USERS, Constant.GET);
    }

    @Override
    public void onSuccess(BaseResponse response) {
        Constant.filterFlag = "likedme";
        if (response.getApiName() == 117) {
            FilterResponse res = (FilterResponse) response;
            if (res.getStatus().equalsIgnoreCase("success")) {
                SharedPref.writeString(SharedPref.USER_TOKEN, res.getAccessToken());
                Toast.makeText(getActivity(), res.getDescription(), Toast.LENGTH_SHORT).show();

            } else {
                if (!res.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                        !res.getDescription().contains("This API accpets") ||
                        !res.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                        !res.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                    SharedPref.writeString(SharedPref.USER_TOKEN, res.getAccessToken());
                }
            }
        } else {
            GetLikedMeResponse getLikedMeResponse = (GetLikedMeResponse) response;
            if (getLikedMeResponse.getStatus().equalsIgnoreCase("success")) {
                System.out.println("**********all success********");
                Constant.BLOCK_USER = "no";
                ll_noRecords.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                if (!getLikedMeResponse.getDescription().equalsIgnoreCase("You have logged out your account.Please log in.")) {
                    SharedPref.writeString(SharedPref.USER_TOKEN, getLikedMeResponse.getAccessToken());
                    Constant.arrGetLiked  = new ArrayList<>();
                    Constant.arrGetLiked = getLikedMeResponse.getUserData();
                    //dummyStrings = Constant.arrGetLiked ;

                    /*int limit = Integer.parseInt(startLimit)+1;
                    startLimit = String.valueOf(limit);
                    arr = new String[Constant.arrGetLiked .size()][22];
                    //limit = arr.length;

                    System.out.println("*******size********"+Constant.arrGetLiked .size());
                    for (int i = 0; i < Constant.arrGetLiked .size(); i++) {
                        arr[i][0] = Constant.arrGetLiked .get(i).getId().toString();
                        arr[i][1] = Constant.arrGetLiked .get(i).getFbProfilePic().toString();
                        arr[i][2] = Constant.arrGetLiked .get(i).getFname().toString();
                        arr[i][3] = Constant.arrGetLiked .get(i).getLname().toString();
                        arr[i][4] = Constant.arrGetLiked .get(i).getGender().toString();
                        arr[i][5] = Constant.arrGetLiked .get(i).getAge().toString();
                        arr[i][6] = Constant.arrGetLiked .get(i).getBio().toString();
                        arr[i][7] = Constant.arrGetLiked .get(i).getFbLocation().toString();
                        arr[i][8] = Constant.arrGetLiked .get(i).getDeviceGeoLocation().toString();
                        arr[i][9] = Constant.arrGetLiked .get(i).getTags().toString();
                        arr[i][10] = Constant.arrGetLiked .get(i).getViewedCount().toString();
                        arr[i][11] = Constant.arrGetLiked .get(i).getLikesCount().toString();
                        arr[i][12] = Constant.arrGetLiked .get(i).getFbRelationshipStatus().toString();
                        arr[i][13] = Constant.arrGetLiked .get(i).getFbFriendsCount().toString();
                        arr[i][14] = Constant.arrGetLiked .get(i).getInstagram().toString();
                        arr[i][15] = Constant.arrGetLiked .get(i).getTwitter().toString();
                        arr[i][16] = Constant.arrGetLiked .get(i).getSnapchat().toString();
                        arr[i][17] = Constant.arrGetLiked .get(i).getKik().toString();
                        arr[i][18] = Constant.arrGetLiked .get(i).getViewsCount().toString();
                        arr[i][19] = Constant.arrGetLiked .get(i).getDistance().toString();
                        arr[i][20] = Constant.arrGetLiked .get(i).getLikeFlag().toString();
                        arr[i][21] = Constant.arrGetLiked .get(i).getFlagged().toString();
                        //arr[i][22] = Constant.arrGetLiked .get(i).getFlagReason().toString();

                    }

                    if (arr.length <= 15) {
                        endRange = arr.length;
                    } else {
                        endRange = 15;
                    }

                    Constant.arrGetLiked  = new ArrayList<>();
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

                        Constant.arrGetLiked .add(data);
                    }*/


                    GridViewAdapterSecond adapter = new GridViewAdapterSecond(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrGetLiked );
                    gridView.setAdapter(adapter);
                    swipyrefreshlayout.setOnRefreshListener(this);

                }
            } else {
                if ((getLikedMeResponse.getCode() == 300)) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),getLikedMeResponse.getDescription());
                }else
                if (!getLikedMeResponse.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                        !getLikedMeResponse.getDescription().contains("This API accpets") ||
                        !getLikedMeResponse.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                        !getLikedMeResponse.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                    SharedPref.writeString(SharedPref.USER_TOKEN, getLikedMeResponse.getAccessToken());
                    ll_noRecords.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.INVISIBLE);
                    Constant.arrGetLiked = new ArrayList<>();
                    GridViewAdapterSecond adapter = new GridViewAdapterSecond(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrGetLiked);
                    gridView.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.SELECTED_TAB_LIKE.equalsIgnoreCase("likedme")) {
            System.out.println("*******onresume likedme******");
            network_status = NetworkStatus.checkConnection(context);
            if (!network_status.equals("false")) {
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
            if(Constant.arrGetLiked .size() != limit) {
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
                        Constant.arrGetLiked .add(data);
                    }
                    System.out.println("******range2******" + startingRange + "**" + endRange);
                    System.out.println("*******size*****" + Constant.arrGetLiked .size());
                    //mBinding.listview.setAdapter(new DummyListViewAdapter(this, dummyStrings));
                    GridViewAdapterSecond adapter = new GridViewAdapterSecond(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrGetLiked );
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
                        Constant.arrGetLiked .add(data);
                    }
                    System.out.println("******range2******" + startingRange + "**" + endRange);
                    //System.out.println("*******size*****" + dummyStrings.size());
                    GridViewAdapterSecond adapter = new GridViewAdapterSecond(getActivity(), R.layout.row_searched_found,
                            "", Constant.arrGetLiked );
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
