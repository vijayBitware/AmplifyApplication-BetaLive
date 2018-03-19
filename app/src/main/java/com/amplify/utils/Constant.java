package com.amplify.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.amplify.R;
import com.amplify.model.GetAllUsers.UserDatum;

import com.amplify.view.MyApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class for local storage of constants. This contains all web service url.
 *
 * @author (Arun Chougule)
 */

public class Constant {

    public static final int API_CREATE_ACCOUNT = 101;
    public static final int API_ADD_SOCIAL_ACCT = 102;
    public static final int API_ADD_TAG = 103;
    public static final int API_LOCATION = 104;
    public static final int API_GET_PROFILE = 112;
    public static final int API_GET_LIKED_ME_USERS = 108;
    public static final int API_GET_LIKES_ME_USERS = 109;
    public static final int API_GET_VIEWED_ME_USERS = 110;
    public static final int API_GET_ALL_USERS = 105;
    public static final int API_GET_NEARBY_USERS = 106;
    public static final int API_LIKE_USER_PROFILE = 107;
    public static final int API_FLAG_USER = 111;
    public static final int API_DELETE_ACCOUNT = 113;
    public static final int API_LOGOUT = 114;
    public static final int API_SEARCH_TAG = 116;
    public static final int API_EDIT_PROFILE = 115;
    public static final int API_FILTER = 117;
    public static final int API_ADD_VIEW = 118;
    public static final int API_INVITE_FRIEND = 119;
    public static int VERSION = 2;

    public static String baseurl = "http://103.224.243.227/codeigniter/amplify/Api_controller/";
    public static String create_account = baseurl + "create_account";
    public static String add_tag = baseurl + "add_tags";
    public static String INVITE_FRIEND_LINK = baseurl + "invite_friends";
    public static String CROPPED_IMAGE = "";
    public static String add_social_account = baseurl + "add_social_accounts";
    public static String add_location = baseurl + "add_device_geo_location";
    public static String get_profile_details = baseurl + "get_my_profile_details";

    public static String get_liked_me_users = baseurl + "get_liked_me_users/?";
    public static String get_likes_me_users = baseurl + "get_likes_me_users/?";
    public static String get_viewed_me_users = baseurl + "get_viewed_me_users/?";
    public static String like_user_profile = baseurl + "like_user_profile";

    public static String get_all_user = baseurl + "get_all_users/?";
    public static String get_nearby_users = baseurl + "get_nearby_users/?";
    public static String get_popular_users = baseurl + "get_popular_users/?";
    public static String flag_user_profile = baseurl + "flag_user_profile";
    public static String delete_my_account = baseurl + "delete_my_account";
    public static String apply_filter = baseurl + "update_filter";
    public static String search_by_tag = baseurl + "search_by_tag";
    public static String logout = baseurl + "logout";
    public static String edit_profile = baseurl + "edit_my_profile";
    public static String update_profile_pic = baseurl + "update_profile_pic";
    public static String add_viewed_profile = baseurl +"add_viewed_profile";
    public static String isUpdateProfile = "no";

    //  public static String
    public static List<com.amplify.model.GetLikedMe.UserDatum> arrGetLiked = new ArrayList<>();
    public static List<UserDatum> arrAllUsers = new ArrayList<>();
    public static int userPosition = 0;
    public static int isActive = 0;
    public static String gender = "0";
    public static String twitter = "1";
    public static String insta = "1";
    public static String snapchat = "1";
    public static String kik = "1";
    public static String filterFlag = "";
    public static int tagCount = 2;
    public static String BLOCK_USER = "no";
    public static int tagEditCount = 2;
    public static String orignalImagePath = "";
    public static Bitmap orignalBitmap = null;

    //public static String latitude = "";
    //public static String longitude = "";
    public static String USER_FIRST_NAME = "";
    public static String USER_LAST_NAME = "";
    public static String USER_AGE = "";
    public static String USER_INTRO = "";
    public static String USER_GENDER = "";
    public static String USER_INSTA_ACCOUNT = "";
    public static String USER_KIK_ACCOUNT = "";
    public static String USER_TWITTER_ACCOUNT = "";
    public static String USER_SNAPCHAT_ACCOUNT = "";
    public static String UPDATE_PROFILE_FLAG = "no";
    public static String UPDATE_PROFILE = "no";
    public static String USER_FB_LOCATION = "";
    public static String USER_LIKE_CNT = "";
    public static String USER_PROFILE_IMAGE = "";
    public static String USER_PROFILE_TAG = "";
    public static String SELECTED_TAB = "all";
    public static String NAVIGATE_TAB_USER = "";
    public static String NAVIGATE_TAB_LIKE = "";
    public static String SELECTED_TAB_LIKE = "likedme";
    public static String mImagePath = null;
    public static File mFileTemp;
    public static Uri mSaveUri = null;
    public static Uri mImageUri = null;
    public  static File fullImageFile, croppedImageFile,imageAfterFilter;
    public static int fromLikeNotification = 0;
    public static String carrierName = "";
    public static String imageToFilter = "";
    public static Bitmap finalBitmapImage = null;
    public static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static String[][] contactList = null;

    public static String PATCH = "PATCH";
    public static String GET = "get";
    public static String POST = "post";
    public static String DEVICE_TYPE = "android";

    public static String shareSubject = "earn while you travel. Awesome free app!";
    public static String shareURL = "http://www.fundevoo.com/home";
    public static String shareTwitterMessage = "Download Fundevoo - #earn while you #travel app & make use of excess #baggage space www.zaldee.com #earnwhileyoutravel";
    public static String shareBody = "Hi,\n\nCheck out Fundevoo- earn while you travel app at www.Fundevoo.com" +
            "\nDownload this free app and make use of your excess baggage space available with you." +
            "\nHighly recommended!" +
            "\n\nBest,";
    public static String shareMessageBody = "Download Fundevoo - earn while you travel app & make use of excess baggage space available with you. www.zaldee.com";

    public static void replaceFragment(Fragment fragment, FragmentManager fragmentManager, Bundle bundle) {
        Animation anim = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.slide_in_left);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
//            transaction.startAnimation(anim);
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.contentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String onlyFirstLetterCapital(String s){
        String upperCase = s.substring(0,1).toUpperCase() + s.substring(1);
        return upperCase;
    }

    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public static void sendMail(String subject) {
        String data ="User Name:"+ SharedPref.getPreferences().getString(SharedPref.FIRST_NAME,"") +"\n"
                +"User Email:"+SharedPref.getPreferences().getString(SharedPref.USER_EMAIL,"")+"\n"
                +"App: com.amplify"+"\n"
                +"LG: en"+"\n"
                +"Carrier Name:"+Constant.carrierName +"\n"
                +"Version Code:"+Build.VERSION.RELEASE+"\n"
                +"Ip Address"+SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS,"") +"\n"
                +"Brand:"+Build.BRAND+"\n"
                +"Model:"+Build.MODEL+"\n"
                +"Manufacture:"+Build.MANUFACTURER+"\n"
                +"Serial:"+Build.SERIAL+"\n"
                +"ID:"+Build.ID+"\n"
                +"Type:"+Build.TYPE+"\n"
                +"User:"+Build.USER+"\n"
                +"Base:"+Build.VERSION_CODES.BASE+"\n"
                +"Incremental:"+Build.VERSION.INCREMENTAL+"\n"
                +"SDK:"+Build.VERSION.SDK+"\n"
                +"Board:"+Build.BOARD+"\n"
                +"Host:"+Build.HOST+"\n"
                +"Fingerprint:"+Build.FINGERPRINT;
        Intent intent = new Intent (Intent.ACTION_SENDTO);
        // it's not ACTION_SEND
        intent.setType ("text/plain");
        intent.putExtra (Intent.EXTRA_SUBJECT, subject);
        intent.putExtra (Intent.EXTRA_TEXT, data);
        intent.setData (Uri.parse ("mailto:amplifyme.official@gmail.com"));
        intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        MyApplication.getContext().startActivity (intent);
    }

    public static String calculateAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public static String getTimeStamp(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return timeStamp;
    }
}
