package com.amplify.view.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.controller.HashTagAdapter;
import com.amplify.model.TagResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.view.UpdateText;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mabbas007.tagsedittext.TagsEditText;
import mabbas007.tagsedittext.utils.ResourceUtils;

import static com.amplify.view.MyApplication.getContext;

/**
 * Created by Admin on 1/5/2018.
 */
public class TagActivity extends Activity implements View.OnTouchListener, APIRequestWithDefaultloader.ResponseHandler, UpdateText, TagsEditText.TagsEditListener {

    SharedPref pref;
    Button btnNext;
    ImageView imgBack;
    View viewAddingTag, viewAddedTag;
    private TagsEditText mTagsEditText;
    TextView txtTagCnt;
    String tag = "";
    String network_status;
    List arrNew;
    boolean isSearchTextCalled = false;
    String strSearchKeyword;
    String arr[];
    List<String> list = new ArrayList<>();
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }

    private void init() {
        arr = new String[3];
        arrNew = new ArrayList<String>();
        pref = new SharedPref(this);
        txtTagCnt = findViewById(R.id.txtTagCnt);
        //recyclerView = (RecyclerView) findViewById(R.id.rv_tag);
        //recyclerView = (RecyclerView) findViewById(R.id.rvMain);
        viewAddingTag = findViewById(R.id.viewAddingTag);
        viewAddedTag = findViewById(R.id.viewAddedTag);
        //recyclerView.setHasFixedSize(true);
        arrNew = new ArrayList<String>();

        arrNew.add(SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, ""));
        arrNew.add(SharedPref.getPreferences().getString(SharedPref.USER_ADDRESS, ""));
        arrNew.add(SharedPref.getPreferences().getString(SharedPref.USER_CITY, ""));

        mTagsEditText = (TagsEditText) findViewById(R.id.tagsEditText);
        mTagsEditText.setHint("Tags");
        mTagsEditText.setTagsListener(this);
        mTagsEditText.setTagsWithSpacesEnabled(true);

        arr[0] = SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, "");
        String alreadyAddedTag = SharedPref.getPreferences().getString(SharedPref.USER_ADDRESS, "");
        String arr1[] = alreadyAddedTag.split(",");
        SharedPref.writeString(SharedPref.USER_ADDRESS, arr1[0]);
        arr[1] = SharedPref.getPreferences().getString(SharedPref.USER_ADDRESS, "");
        arr[2] =  SharedPref.getPreferences().getString(SharedPref.USER_CITY, "");

        txtTagCnt.setText(String.valueOf(arrNew.size()));
        mTagsEditText.setTags(arr);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitTag();

            }
        });

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mTagsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("************above******" + ResourceUtils.tagAddCount);
                if (ResourceUtils.tagAddCount != 10) {
                    System.out.println("*********else**********");
                    mTagsEditText.setFocusableInTouchMode(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 1);
                } else {
                    ResourceUtils.showSnackBarPink(TagActivity.this, findViewById(android.R.id.content), "You have reached maximum limit for tags");
                    mTagsEditText.setFocusableInTouchMode(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
                }
            }
        });

        mTagsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("********edit before*********");
                if (ResourceUtils.tagAddCount == 10) {
                    System.out.println("********if********");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
                    mTagsEditText.setFocusable(false);
                } else {
                    System.out.println("*********else**********");
                    mTagsEditText.setFocusableInTouchMode(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 1);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mTagsEditText.setFilters(new InputFilter[] {
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            System.out.println("********special*******");
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z ]+")){
                            System.out.println("********allowed********");
                            return src;
                        }
                        ResourceUtils.showSnackBarPink(TagActivity.this, findViewById(android.R.id.content), getResources().getString(R.string.tagError));
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
                        return "";
                    }
                }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //mTagsEditText.showDropDown();
        }
    }

    private void submitTag() {

        /*Utils.TAGLIST.add(SharedPref.getPreferences().getString(SharedPref.FIRST_NAME, ""));
        Utils.TAGLIST.add("Pune");//(SharedPref.getPreferences().getString(SharedPref.USER_ADDRESS, ""));*/

        if (list.size() == 0) {
           // Toast.makeText(TagActivity.this, "Please add atleast one tag", Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), getResources().getString(R.string.tag_validation));

        } else {
            /*for (int i = 0; i < list.size(); i++) {
                System.out.println("*******data******" + list.get(i).toString());
                tag = tag + "," + list.get(i).toString();
            }

            if (tag.startsWith(",")) {
                tag = tag.substring(1);
            }*/

            System.out.println("**************" + tag);
            network_status = NetworkStatus.checkConnection(TagActivity.this);
            if (network_status.equals("false")) {

//                AlertClass alert = new AlertClass();
//                alert.customDialog(TagActivity.this, "", getResources().getString(R.string.no_internet));
                SnackBarUtils.showSnackBarPink(MyApplication.getContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));

            } else {
                try {

                    String url = Constant.add_tag;
                    System.out.println("Login Url >" + url);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                        jsonObject.put("device_type", Constant.DEVICE_TYPE);
                        jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                        jsonObject.put("tags", tag);
                        jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));
                        jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));

                        Log.e("TAG", "request > " + jsonObject);

                        new APIRequestWithDefaultloader(TagActivity.this, jsonObject, url, this, Constant.API_ADD_TAG, Constant.POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (NullPointerException e) {
                    System.out.println("Nullpointer Exception at Login Screen" + e);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        viewAddingTag.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("LoginActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onSuccess(BaseResponse response) {
        System.out.println("**************");
        TagResponse tagRes = (TagResponse) response;
        if (tagRes.getStatus().equalsIgnoreCase("Success")) {
            SharedPref.writeString(SharedPref.USER_TOKEN, tagRes.getAccessToken());
            //  Toast.makeText(TagActivity.this, tagRes.getDescription(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(getApplicationContext(), findViewById(android.R.id.content), tagRes.getDescription());
            System.out.println("******tag******" + tag);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(TagActivity.this, LocationPermissionActivity.class);
                    startActivity(i);

                }
            }, 1000);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            if (!tagRes.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                    !tagRes.getDescription().contains("This API accpets") ||
                    !tagRes.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                    !tagRes.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                SharedPref.writeString(SharedPref.USER_TOKEN, tagRes.getAccessToken());
                Toast.makeText(TagActivity.this, tagRes.getDescription(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    public UpdateText callback = new UpdateText() {
        @Override
        public void onDataChange(String options) {
            txtTagCnt.setText(String.valueOf(options));
        }

        @Override
        public void onCounterChange(String cnt) {

        }

    };

    @Override
    public void onDataChange(String options) {
        txtTagCnt.setText(options);
    }

    @Override
    public void onCounterChange(String cnt) {

    }

    @Override
    public void onTagsChanged(Collection<String> tags) {
        if (list.size() > 10) {
            System.out.println("***tag exceeds******");
        } else {
            Log.d("TaG", "Tags changed: ");
            Log.d("TaG", Arrays.toString(tags.toArray()));

            String addedTag = Arrays.toString(tags.toArray());

            tag = addedTag.substring(addedTag.indexOf("[") + 1, addedTag.indexOf("]"));
            System.out.println("*******tagArr*******" + tag);

            list = Arrays.asList(tag.split("\\s*,\\s*"));
            String cnt;
            if (tag.equalsIgnoreCase("")) {
                cnt = "0";
                list = new ArrayList<>();
            } else {
                cnt = String.valueOf(list.size());
            }

            ResourceUtils.tagAddCount = Integer.parseInt(cnt);
            txtTagCnt.setText(cnt);
            System.out.println("**************" + list.size());
        }

    }

    @Override
    public void onEditingFinished() {
        Log.d("TaG", "OnEditing finished");
        if (ResourceUtils.tagAddCount == 10) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
            mTagsEditText.setCursorVisible(false);

          //  ResourceUtils.showSnackBarPink(TagActivity.this, findViewById(android.R.id.content), "You have reached maximum limit for tags");

        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
            mTagsEditText.setCursorVisible(true);

        }

    }
}