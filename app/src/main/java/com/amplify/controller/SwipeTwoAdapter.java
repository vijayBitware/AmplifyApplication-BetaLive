package com.amplify.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplify.R;

import com.amplify.model.GetLikedMe.UserDatum;
import com.amplify.model.UserFlag;
import com.amplify.model.UserLike;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;

import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestNew;
import com.amplify.webservice.BaseResponse;

import com.kik.kikapi.KikClient;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.amplify.utils.SharedPref;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.amplify.utils.Constant.round;

/**
 * Created by Admin on 2/8/2018.
 */

public class SwipeTwoAdapter extends PagerAdapter implements APIRequest.ResponseHandler,APIRequestNew.ResponseHandler {

    Animation slideUpAnimation, slideDownAnimation;
    List<UserDatum> arrListUsers;
    private LayoutInflater inflater;
    private Context context;
    int currentPosition;
    String network_status;
    View myView,snakBarView;
    ImageLoader imageLoader;
    boolean isLikedClicked = true;
    TextView txtSelected;
    FrameLayout flSlide;

    public SwipeTwoAdapter(Context context, List<UserDatum> arrListUsers) {
        this.context = context;
        this.arrListUsers = arrListUsers;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrListUsers.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {

        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        snakBarView = myImageLayout;
        slideUpAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_up_animation);

        slideDownAnimation = AnimationUtils.loadAnimation(context,
                R.anim.slide_down_animation);

        flSlide = myImageLayout.findViewById(R.id.flSlide);
        final ImageView myImage = myImageLayout.findViewById(R.id.image);
        ImageView imgGender = myImageLayout.findViewById(R.id.imgGender);
        TextView txtName = myImageLayout.findViewById(R.id.txtName);
        TextView txtAge = myImageLayout.findViewById(R.id.txtAge);
        ImageView imgHeartIcon = myImageLayout.findViewById(R.id.imgHeartIcon);
        imgHeartIcon.setTag("imgUserLike");
        final TextView txtFollowersCnt = myImageLayout.findViewById(R.id.txtFollowersCnt);
        txtFollowersCnt.setTag("txtLikeCount");
        TextView txtAddress = myImageLayout.findViewById(R.id.txtAddress);
        TextView txtDist = myImageLayout.findViewById(R.id.txtDist);
        TextView txtSortBio = myImageLayout.findViewById(R.id.txtSortBio);
        ImageView imgInsta = myImageLayout.findViewById(R.id.imgInsta);
        ImageView imgTwitter = myImageLayout.findViewById(R.id.imgTwitter);
        ImageView imgSnapchat = myImageLayout.findViewById(R.id.imgSnapchat);
        ImageView imgKik = myImageLayout.findViewById(R.id.imgKik);
        ImageView imgBack = myImageLayout.findViewById(R.id.imgBack);
        ImageView imgFlag = myImageLayout.findViewById(R.id.imgFlag);
        imgFlag.setTag("userReportFlag");
        final View ViewOverlay = myImageLayout.findViewById(R.id.ViewOverlay);
        final LinearLayout linearFlag = myImageLayout.findViewById(R.id.linearFlag);
        final LinearLayout linearProfileView = myImageLayout.findViewById(R.id.linearProfileView);
        TextView txtCancel = myImageLayout.findViewById(R.id.txtCancel);
        final ProgressBar progress = myImageLayout.findViewById(R.id.progress);
        final RelativeLayout relativeFollower = myImageLayout.findViewById(R.id.relativeFollower);
        RecyclerView rv_tags = myImageLayout.findViewById(R.id.rv_tags);
        rv_tags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true));
        LinearLayout ll_shortBio = myImageLayout.findViewById(R.id.ll_shortBio);

        final TextView txt_incorrectGender = myImageLayout.findViewById(R.id.txt_incorrectGender);
        final TextView txt_fakeUsername = myImageLayout.findViewById(R.id.txt_fakeUsername);
        final TextView txt_inappropiateBio = myImageLayout.findViewById(R.id.txt_inappropiateBio);
        final TextView txt_inappropiateImage = myImageLayout.findViewById(R.id.txt_inappropiateImage);

        txtName.setText(Constant.capitalize(arrListUsers.get(position).getFname())+", ");
        txtAge.setText(arrListUsers.get(position).getAge());
        txtAddress.setText(Constant.capitalize(arrListUsers.get(position).getFbLocation()));
        txtDist.setText("("+round(Double.parseDouble(arrListUsers.get(position).getDistance()), 1)+" miles away)");
        List<String> tags = Arrays.asList(arrListUsers.get(position).getTags().split("\\s*,\\s*"));
        Collections.reverse(tags);
        rv_tags.setAdapter(new UserProfileTags(context,tags));
        rv_tags.scrollToPosition(tags.size()-1);
        if (arrListUsers.get(position).getBio().equalsIgnoreCase("")){
            ll_shortBio.setVisibility(View.GONE);
        }else {
            ll_shortBio.setVisibility(View.VISIBLE);
            txtSortBio.setText(Constant.onlyFirstLetterCapital(arrListUsers.get(position).getBio()));
        }
        if (arrListUsers.get(position).getInstagram().equalsIgnoreCase("")) {
            imgInsta.setImageResource(R.drawable.instagram_lt);
        } else {
            imgInsta.setImageResource(R.drawable.instagram_white);
        }
        if (arrListUsers.get(position).getSnapchat().equalsIgnoreCase("")) {
            imgSnapchat.setImageResource(R.drawable.snapchat_lt);
        } else {
            imgSnapchat.setImageResource(R.drawable.snapchat_white);
        }
        if (arrListUsers.get(position).getTwitter().equalsIgnoreCase("")) {
            imgTwitter.setImageResource(R.drawable.twitter_lt);
        } else {
            imgTwitter.setImageResource(R.drawable.twitter_white);
        }
        if (arrListUsers.get(position).getKik().equalsIgnoreCase("")) {
            imgKik.setImageResource(R.drawable.kik_lt);
        } else {
            imgKik.setImageResource(R.drawable.kik_white);
        }

        if (arrListUsers.get(position).getGender().equalsIgnoreCase("female")) {
            imgGender.setImageResource(R.drawable.icon_female_sm);
        } else {
            imgGender.setImageResource(R.drawable.icon_male_sm);
        }

//        Glide.with(context).load(arrListUsers.get(position).getFbProfilePic()).placeholder(R.drawable.profile_placeholder).into(myImage);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.profile_placeholder)
                .cacheOnDisc()
                .build();
        // imageLoader.displayImage(arrListUsers.get(position).getFbProfilePic(),myImage,options);
        imageLoader.displayImage(arrListUsers.get(position).getFbProfilePic(), myImage, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progress.setVisibility(View.GONE);
                myImage.setImageResource(R.drawable.profile_placeholder);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progress.setVisibility(View.GONE);
                imageLoader.displayImage(arrListUsers.get(position).getFbProfilePic(),myImage);
            }
        });
        imgFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myView = view;
                relativeFollower.setVisibility(View.INVISIBLE);
                linearProfileView.setVisibility(View.INVISIBLE);
                ViewOverlay.setVisibility(View.VISIBLE);
                linearFlag.setVisibility(View.VISIBLE);
                linearFlag.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideDownAnimation);
                relativeFollower.startAnimation(slideDownAnimation);

            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFlag.startAnimation(slideDownAnimation);
                relativeFollower.setVisibility(View.VISIBLE);
                linearProfileView.setVisibility(View.VISIBLE);
                relativeFollower.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideUpAnimation);
                ViewOverlay.setVisibility(View.INVISIBLE);
                linearFlag.setVisibility(View.INVISIBLE);

            }
        });

        if (arrListUsers.get(position).getLikeFlag().equalsIgnoreCase("0")){
            System.out.println("if like flag = 0");
            imgHeartIcon.setImageResource(R.drawable.icon_heart_inactive1);
            txtFollowersCnt.setTextColor(context.getResources().getColor(R.color.black));
            txtFollowersCnt.setText(arrListUsers.get(position).getLikesCount());
        }else if (arrListUsers.get(position).getLikeFlag().equalsIgnoreCase("1")){
            System.out.println("if like flag = 1");
            imgHeartIcon.setImageResource(R.drawable.icon_like_heart);
            txtFollowersCnt.setTextColor(context.getResources().getColor(R.color.white));
            txtFollowersCnt.setText(arrListUsers.get(position).getLikesCount());
        }

        if (arrListUsers.get(position).getFlagged().equalsIgnoreCase("1")){
            imgFlag.setImageResource(R.drawable.icon_flag_pink);
        }else {
            imgFlag.setImageResource(R.drawable.ic_slating_flag);
        }

        imgInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrListUsers.get(position).getInstagram().equalsIgnoreCase("")){
                    String username = arrListUsers.get(position).getInstagram();
                    boolean isAppInstalled = Constant.appInstalledOrNot("com.instagram.android",context);
                    if (isAppInstalled) {
                        context.startActivity(newInstagramProfileIntent(context.getPackageManager(),"http://instagram.com/_u/"+username+""));
                    }else {
                        Snackbar snackbar = Snackbar.make(flSlide, "Instagram not installed", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        snackbar.show();                    }
                }

            }
        });

        imgSnapchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrListUsers.get(position).getSnapchat().equalsIgnoreCase("")){
                    String username = arrListUsers.get(position).getSnapchat();
                    boolean isAppInstalled = Constant.appInstalledOrNot("com.snapchat.android",context);
                    if (isAppInstalled) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/"+username+"")));
                    }else {
                        Snackbar snackbar = Snackbar.make(flSlide, "Snapchat not installed", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        snackbar.show();                    }
                }
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrListUsers.get(position).getTwitter().equalsIgnoreCase("")){
                    String username = arrListUsers.get(position).getTwitter();
                    boolean isAppInstalled = Constant.appInstalledOrNot("com.twitter.android",context);
                    if (isAppInstalled) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+username+"")));
                    }else {
                        Snackbar snackbar = Snackbar.make(flSlide, "Twitter not installed", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        snackbar.show();
                    }
                }
            }
        });

        imgKik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!arrListUsers.get(position).getTwitter().equalsIgnoreCase("")){
                    String username = arrListUsers.get(position).getKik();
                    boolean isAppInstalled = Constant.appInstalledOrNot("kik.android",context);
                    if (isAppInstalled){
                        KikClient.getInstance().openProfileForKikUsername(context, username);
                    }else {
                        Snackbar snackbar = Snackbar.make(flSlide, "Kik not installed", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        else
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        snackbar.show();
                    }
                }

            }
        });
        txt_incorrectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFlag.startAnimation(slideDownAnimation);
                relativeFollower.setVisibility(View.VISIBLE);
                linearProfileView.setVisibility(View.VISIBLE);
                relativeFollower.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideUpAnimation);
                ViewOverlay.setVisibility(View.INVISIBLE);
                linearFlag.setVisibility(View.INVISIBLE);
                callFlagUserProfile(txt_incorrectGender.getText().toString(),arrListUsers.get(position).getId());
            }
        });
        txt_fakeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFlag.startAnimation(slideDownAnimation);
                relativeFollower.setVisibility(View.VISIBLE);
                linearProfileView.setVisibility(View.VISIBLE);
                relativeFollower.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideUpAnimation);
                ViewOverlay.setVisibility(View.INVISIBLE);
                linearFlag.setVisibility(View.INVISIBLE);
                callFlagUserProfile(txt_fakeUsername.getText().toString(),arrListUsers.get(position).getId());
            }
        });
        txt_inappropiateBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFlag.startAnimation(slideDownAnimation);
                relativeFollower.setVisibility(View.VISIBLE);
                linearProfileView.setVisibility(View.VISIBLE);
                relativeFollower.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideUpAnimation);
                ViewOverlay.setVisibility(View.INVISIBLE);
                linearFlag.setVisibility(View.INVISIBLE);
                callFlagUserProfile(txt_inappropiateBio.getText().toString(),arrListUsers.get(position).getId());
            }
        });
        txt_inappropiateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearFlag.startAnimation(slideDownAnimation);
                relativeFollower.setVisibility(View.VISIBLE);
                linearProfileView.setVisibility(View.VISIBLE);
                relativeFollower.startAnimation(slideUpAnimation);
                linearProfileView.startAnimation(slideUpAnimation);
                ViewOverlay.setVisibility(View.INVISIBLE);
                linearFlag.setVisibility(View.INVISIBLE);
                callFlagUserProfile(txt_inappropiateImage.getText().toString(),arrListUsers.get(position).getId());
            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                activity.finish();
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        imgHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLikedClicked ==true){
                    isLikedClicked = false;
                    currentPosition = position;
                    myView = view;
                        callLikeUserApi(Constant.arrGetLiked.get(position).getId(),position, txtFollowersCnt);
                }
            }
        });
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }


    private void callLikeUserApi(String id, int position, TextView txtFollowersCnt) {
        if (NetworkStatus.isConnectingToInternet(context)) {
            JSONObject jsonObject = new JSONObject();
            txtSelected = txtFollowersCnt;
            currentPosition = position;
            try {
                jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                jsonObject.put("profile_user_id", id);
                jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                jsonObject.put("device_type", Constant.DEVICE_TYPE);
                jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                Log.e("TAG", "Request > " + jsonObject);

                new APIRequestNew(context, jsonObject, Constant.like_user_profile, this, Constant.API_LIKE_USER_PROFILE, Constant.POST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Snackbar snackbar = Snackbar.make(flSlide, context.getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
            snackbar.show();        }

    }

    private void callFlagUserProfile(String reason,String id) {
        if (NetworkStatus.isConnectingToInternet(context)){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id",SharedPref.getPreferences().getString(SharedPref.USER_ID,""));
                jsonObject.put("profile_user_id",id);
                jsonObject.put("access_token",SharedPref.getPreferences().getString(SharedPref.USER_TOKEN,""));
                jsonObject.put("device_id",SharedPref.getPreferences().getString(SharedPref.GCMREGID,""));
                jsonObject.put("device_type",Constant.DEVICE_TYPE);
                jsonObject.put("registration_ip",SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS,""));
                jsonObject.put("flag_reason",reason);
                Log.e("TAG","Request > " +jsonObject);

                new APIRequest(context,jsonObject,Constant.flag_user_profile,this,Constant.API_FLAG_USER,Constant.POST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Snackbar snackbar = Snackbar.make(flSlide, context.getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.pink));
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
            snackbar.show();        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void onSuccess(BaseResponse response) {

        if (response.getApiName() == Constant.API_LIKE_USER_PROFILE) {
            UserLike userLike = (UserLike) response;
            if (userLike.getStatus().equalsIgnoreCase("success")) {
                isLikedClicked = true;
                SharedPref.writeString(SharedPref.USER_TOKEN, userLike.getAccessToken());
                ImageView imageView = myView.findViewWithTag("imgUserLike");
                if (userLike.getDescription().equalsIgnoreCase("like added successfully")) {
                    imageView.setImageResource(R.drawable.icon_like_heart);
                    arrListUsers.get(currentPosition).setLikeFlag("1");
                    arrListUsers.get(currentPosition).setLikesCount(userLike.getUserLikes());
                    txtSelected.setText(userLike.getUserLikes());
                    txtSelected.setTextColor(context.getResources().getColor(R.color.white));
                }else if (userLike.getDescription().equalsIgnoreCase("unliked successfully")){
                    imageView.setImageResource(R.drawable.icon_heart_inactive1);
                    arrListUsers.get(currentPosition).setLikeFlag("0");
                    arrListUsers.get(currentPosition).setLikesCount(userLike.getUserLikes());
                    txtSelected.setText(userLike.getUserLikes());
                    txtSelected.setTextColor(context.getResources().getColor(R.color.black));

                }
            } else {
                if ((userLike.getCode() == 300)) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(MyApplication.getContext(),userLike.getDescription());
                }else
                if (!userLike.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                        !userLike.getDescription().contains("This API accpets") ||
                        !userLike.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                        !userLike.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {
                    isLikedClicked = true;
                    SharedPref.writeString(SharedPref.USER_TOKEN, userLike.getAccessToken());
                    Snackbar snackbar = Snackbar.make(flSlide, userLike.getDescription(), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    else
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    snackbar.show();
                }
            }
        }else {
            UserFlag userFlag = (UserFlag) response;
            if (userFlag.getStatus().equalsIgnoreCase("success")) {
                SharedPref.writeString(SharedPref.USER_TOKEN, userFlag.getAccessToken());
                Snackbar snackbar = Snackbar.make(flSlide, userFlag.getDescription(), Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                else
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                snackbar.show();
                ImageView imageView = myView.findViewWithTag("userReportFlag");
                imageView.setImageResource(R.drawable.icon_flag_pink);
            } else {
                if (!userFlag.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                        !userFlag.getDescription().contains("This API accpets") ||
                        !userFlag.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                        !userFlag.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {
                    SharedPref.writeString(SharedPref.USER_TOKEN, userFlag.getAccessToken());
                    Snackbar snackbar = Snackbar.make(flSlide, userFlag.getDescription(), Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    else
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    snackbar.show();
                }
            }
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    public static Intent newInstagramProfileIntent(PackageManager pm, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (pm.getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }
}
