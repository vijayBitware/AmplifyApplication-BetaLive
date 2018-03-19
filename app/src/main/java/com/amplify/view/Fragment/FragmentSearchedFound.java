package com.amplify.view.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.controller.AdapterSearchedFound;
import com.amplify.controller.GridViewAdapter;

import com.amplify.model.InviteFriend;
import com.amplify.model.SearchTag;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;

import com.amplify.utils.SharedPref;
import com.amplify.utils.SnackBarUtils;
import com.amplify.utils.StatusBarView;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.amplify.view.MyApplication.getContext;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 1/31/2018.
 */

public class FragmentSearchedFound extends Fragment implements APIRequestWithDefaultloader.ResponseHandler {

    Context context;
    TextView txtSearchFound, txtSerachingTag, txtSearchedTag, txt_failedSerch,txt_failedSerchTag;
    GridView gridView;
    LinearLayout ll_noSeach, ll_search, ll_topBlueBar;
    ImageView search;
    EditText edtSerachingTag;
    Button btnInviteFriends;
    StatusBarView statusBar;
    LinearLayout ll_noInternet;
    Button btnRetry;
    private Tracker mTracker;
    String startLimit="0";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searched_found, container, false);
        init(view);
        if (NetworkStatus.isConnectingToInternet(getContext())) {
            ll_noInternet.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

        } else {
            ll_noInternet.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }

        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SearchedFound");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @SuppressLint("ResourceAsColor")
    private void init(final View view) {
        ll_noInternet = view.findViewById(R.id.ll_noInternet);
        statusBar = getActivity().findViewById(R.id.statusBar);
        statusBar.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.statusbar_transparent));
        gridView = view.findViewById(R.id.gridView);
        txtSearchFound = (TextView) view.findViewById(R.id.txtSearchFound);
        btnRetry = view.findViewById(R.id.btnRetry);
        txtSearchedTag = (TextView) view.findViewById(R.id.txtSearchedTag);
        edtSerachingTag = view.findViewById(R.id.edtSerachingTag);
        ll_search = view.findViewById(R.id.ll_search);
        ll_noSeach = view.findViewById(R.id.ll_noSeach);
        context = MyApplication.getContext();
        gridView = view.findViewById(R.id.gridView);
        search = view.findViewById(R.id.search);
        txt_failedSerchTag = view.findViewById(R.id.txt_failedSerchTag);
        //imgBack = view.findViewById(R.id.imgBack);
        txtSearchFound.setTextColor(getResources().getColor(R.color.white));
        btnInviteFriends = view.findViewById(R.id.btnInviteFriends);
        txt_failedSerch = view.findViewById(R.id.txt_failedSerch);
        ll_topBlueBar = view.findViewById(R.id.ll_topBlueBar);
        ll_topBlueBar.setVisibility(View.INVISIBLE);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    if (NetworkStatus.isConnectingToInternet(getContext())) {
                        ll_noInternet.setVisibility(View.INVISIBLE);
                        ll_search.setVisibility(View.VISIBLE);
                        callSearchTagApi();
                    } else {
                        ll_noInternet.setVisibility(View.VISIBLE);
                        ll_search.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    if (NetworkStatus.isConnectingToInternet(getContext())) {
                        ll_noInternet.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.VISIBLE);
                        callSearchTagApi();
                    } else {
                        ll_noInternet.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });

        edtSerachingTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (NetworkStatus.isConnectingToInternet(MyApplication.getContext())) {
                      /*  ll_noInternet.setVisibility(View.GONE);
                        ll_search.setVisibility(View.GONE);*/
                        if (!edtSerachingTag.getText().toString().isEmpty()) {
                            callSearchTagApi();
                        }else {
                            SnackBarUtils.showSnackBarPink(getContext(),getActivity().findViewById(android.R.id.content),getResources().getString(R.string.search_validation));
                        }
                    } else {
                        //Toast.makeText(MyApplication.getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        ll_noInternet.setVisibility(View.VISIBLE);
                        ll_search.setVisibility(View.GONE);

                    }
                }
                return false;
            }
        });

        edtSerachingTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    search.setImageResource(R.drawable.icon_search_active);
                } else {
                    search.setImageResource(R.drawable.icon_close);
                    search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtSerachingTag.setText("");
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteFriend();

            }
        });

    }

    private void inviteFriend() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Invite")
                .build());
        if (NetworkStatus.isConnectingToInternet(MyApplication.getContext())) {
            new APIRequestWithDefaultloader(getContext(), new JSONObject(), Constant.INVITE_FRIEND_LINK, this, Constant.API_INVITE_FRIEND, Constant.GET);
        } else {
            AlertClass alert = new AlertClass();
            alert.customDialog(MyApplication.getContext(), "", getResources().getString(R.string.no_internet));
        }
    }

    private void callSearchTagApi() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
            jsonObject.put("tag", edtSerachingTag.getText().toString());
            jsonObject.put("start_limit", "0");
            jsonObject.put("offset", "45");
            jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
            jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
            jsonObject.put("device_type", Constant.DEVICE_TYPE);
            jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
            jsonObject.put("latitude", SharedPref.getPreferences().getString(SharedPref.CURRENt_LAT, ""));
            jsonObject.put("longitude", SharedPref.getPreferences().getString(SharedPref.CURRENT_LONG, ""));
            Log.e("TAG", "Request > " + jsonObject);

            new APIRequestWithDefaultloader(getContext(), jsonObject, Constant.search_by_tag, this, Constant.API_SEARCH_TAG, Constant.POST);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        if (response.getApiName() == Constant.API_SEARCH_TAG) {
            SearchTag searchTag = (SearchTag) response;
            if (searchTag.getStatus().equalsIgnoreCase("Success")) {
                /*int limit = Integer.parseInt(startLimit)+1;
                startLimit = String.valueOf(limit);*/
                SharedPref.writeString(SharedPref.USER_TOKEN, searchTag.getAccessToken());
                txtSearchFound.setText(searchTag.getUserData().size() + " Search found with ");
                txtSearchedTag.setText("#"+edtSerachingTag.getText().toString());
                ll_noSeach.setVisibility(View.GONE);
                ll_topBlueBar.setVisibility(View.VISIBLE);
                ll_search.setVisibility(View.VISIBLE);
                Constant.arrAllUsers = searchTag.getUserData();
                gridView.setAdapter(new GridViewAdapter(getContext(), R.layout.row_searched_found, "", searchTag.getUserData()));
            }
            else if (searchTag.getStatus().equalsIgnoreCase("Failed")) {
                if ((searchTag.getCode() == 300)) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),searchTag.getDescription());
                }else
                if (!searchTag.getDescription().equalsIgnoreCase("Send JSON data in proper format")
                        || !searchTag.getDescription().contains("This API accpets")
                        || !searchTag.getDescription().equalsIgnoreCase("Please Send JSON data")
                        || !searchTag.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {
                    SharedPref.writeString(SharedPref.USER_TOKEN, searchTag.getAccessToken());
                    ll_noSeach.setVisibility(View.VISIBLE);
                    ll_search.setVisibility(View.GONE);
                    ll_topBlueBar.setVisibility(View.GONE);
                    txt_failedSerch.setText("Sorry, unable to find ");
                    txt_failedSerchTag.setText("#"+edtSerachingTag.getText().toString());
                }
            }else if (searchTag.getStatus().equalsIgnoreCase("No record found")){
                ll_noSeach.setVisibility(View.VISIBLE);
                ll_topBlueBar.setVisibility(View.GONE);
                ll_search.setVisibility(View.GONE);
            }
        } else {
            InviteFriend inviteFriend = (InviteFriend) response;
            if (inviteFriend.getStatus().equalsIgnoreCase("success")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Invitation");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invite_friend_text) +"\n"+inviteFriend.getLink());
                startActivity(Intent.createChooser(sharingIntent, "Invite Friends Via"));
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }
}
