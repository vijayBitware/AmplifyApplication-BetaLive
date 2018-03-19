package com.amplify.view.Activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.controller.HashTagAdapter;

import com.amplify.model.EditProfileResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.SnackBarUtils;
import com.amplify.view.MyApplication;
import com.amplify.view.UpdateText;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestNew;
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
 * Created by bitware on 8/2/18.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener, APIRequestWithDefaultloader.ResponseHandler, UpdateText , TagsEditText.TagsEditListener{

    SharedPref pref;
    LinearLayout ll_age, ll_male, ll_female;
    TextView txtSelectedAge;
    ImageView imgBack, imgInstagram, imgTwitter, imgSnapchat, imgKik, imgFemale, imgMaleActive, imgMale, imgFemaleActive;
    EditText edtInsta, edtTwitter, edtSnapchat, edtKik, edtFirstName, edt_intro;
   // RecyclerView recyclerView;
  //  AutoCompleteTextView autoCompleteTextView;
    boolean isSearchTextCalled = false;
    ArrayList arrNew;
    Button btnSave;
    String tags = "", gender = "", kik = "", twitter = "", insta = "", snapchat = "",tag="";
    LinearLayoutManager lm;
    String strSearchKeyword;
    String network_status;
    TextView txtTagCnt;
    private Tracker mTracker;
    private TagsEditText mTagsEditText;
    String arr[];
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        init();
    }

    private void init() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        arrNew = new ArrayList<String>();
        pref = new SharedPref(this);
        edtFirstName = findViewById(R.id.edtFirstName);
        edt_intro = findViewById(R.id.edt_intro);
        edtInsta = findViewById(R.id.edtInsta);
        edtSnapchat = findViewById(R.id.edtSnapchat);
        edtKik = findViewById(R.id.edtKik);
        edtTwitter = findViewById(R.id.edtTwitter);
        imgInstagram = findViewById(R.id.imgInstagram);
        imgKik = findViewById(R.id.imgKik);
        imgSnapchat = findViewById(R.id.imgSnapchat);
        imgTwitter = findViewById(R.id.imgTwitter);
        imgBack = findViewById(R.id.imgBack);
        ll_age = findViewById(R.id.ll_age);
        txtSelectedAge = findViewById(R.id.txtSelectedAge);
        imgBack.setOnClickListener(this);
        ll_age.setOnClickListener(this);
        ll_male = findViewById(R.id.ll_male);
        ll_female = findViewById(R.id.ll_female);
        ll_male.setOnClickListener(this);
        ll_female.setOnClickListener(this);
        imgMaleActive = findViewById(R.id.imgMaleActive);
        imgMale = findViewById(R.id.imgMale);
        imgFemale = findViewById(R.id.imgFemale);
        imgFemaleActive = findViewById(R.id.imgFemaleActive);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        arrNew = new ArrayList<String>();
        pref = new SharedPref(this);
        txtTagCnt = findViewById(R.id.txtTagCnt);
        mTagsEditText = (TagsEditText) findViewById(R.id.tagsEditText);
        mTagsEditText.setHint("Tags");
        mTagsEditText.setTagsListener(this);
        mTagsEditText.setTagsWithSpacesEnabled(true);

        List<String> tags = Arrays.asList(Constant.USER_PROFILE_TAG.split("\\s*,\\s*"));
        arr = new String[tags.size()];
        //arrNew.add(tags);
        for (int i = 0; i < tags.size(); i++) {
            System.out.println("********tagggg*********" + tags.get(i));
            arr[i]=tags.get(i);

        }
        ResourceUtils.tagEditCount = arr.length;
        txtTagCnt.setText(String.valueOf(ResourceUtils.tagEditCount));
       // txtTagCnt.setText(String.valueOf(Constant.USER_PROFILE_TAG.size()));
        mTagsEditText.setTags(arr);

        mTagsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("************above******" + ResourceUtils.tagEditCount);
                if (ResourceUtils.tagEditCount != 10) {
                    System.out.println("*********else**********");
                    mTagsEditText.setFocusableInTouchMode(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 1);
                } else {
                    ResourceUtils.showSnackBarPink(EditProfileActivity.this, findViewById(android.R.id.content), "You have reached maximum limit for tags");
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
                if (ResourceUtils.tagEditCount == 10) {
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
                        ResourceUtils.showSnackBarPink(EditProfileActivity.this, findViewById(android.R.id.content), getResources().getString(R.string.tagError));
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
                        return "";
                    }
                }
        });

        /*recyclerView = (RecyclerView) findViewById(R.id.rv_tag);

        recyclerView.setHasFixedSize(true);
        arrNew = new ArrayList<String>();

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        lm = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isSearchTextCalled = false;
                if (charSequence.toString().contains(" ")){
                    isSearchTextCalled = true;
                    String strMyVal = autoCompleteTextView.getText().toString();
                    if (arrNew.size() < 10) {
                        if (!arrNew.contains(strMyVal)) {
                            arrNew.add(strMyVal);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Maximum tag is 10", Toast.LENGTH_SHORT).show();
                    }

                    autoCompleteTextView.setText("");
                    strSearchKeyword = android.text.TextUtils.join(",", arrNew);

                    Constant.tagEditCount = arrNew.size();

                    txtTagCnt.setText(String.valueOf(Constant.tagEditCount));

                    HashTagAdapter searchAdapter = new HashTagAdapter(EditProfileActivity.this, arrNew, callback, "edit");
                    LinearLayoutManager llManager = new LinearLayoutManager(EditProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(llManager);
                    recyclerView.setAdapter(searchAdapter);
                    recyclerView.scrollToPosition(arrNew.size() - 1);

                    *//*InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);*//*
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        setData();

        edtInsta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    insta = "";
                    imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.ic_instagram));
                } else {
                    insta = String.valueOf(s);
                    imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.icon_insta_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtTwitter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    twitter = "";
                    imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter));
                } else {
                    twitter = String.valueOf(s);
                    imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.icon_twitter_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtKik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    kik = "";
                    imgKik.setImageDrawable(getResources().getDrawable(R.drawable.ic_kik));
                } else {
                    kik = String.valueOf(s);
                    imgKik.setImageDrawable(getResources().getDrawable(R.drawable.icon_kik_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtSnapchat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("******s*****" + s.toString());
                if (s.toString().equalsIgnoreCase("")) {
                    snapchat = "";
                    imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.ic_snapchat));
                } else {
                    snapchat = String.valueOf(s);
                    imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.icon_snap_active));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setData() {
        edtFirstName.setText(Constant.USER_FIRST_NAME + " " + Constant.USER_LAST_NAME);
        kik = edtKik.getText().toString();
        //   insta = edtInsta.getText().toString();
        txtSelectedAge.setText(Constant.USER_AGE);
        edt_intro.setText(Constant.USER_INTRO);
        if (Constant.USER_GENDER.equalsIgnoreCase("female")) {
            imgFemaleActive.setVisibility(View.VISIBLE);
            imgFemale.setVisibility(View.GONE);
            imgMaleActive.setVisibility(View.GONE);
            imgMale.setVisibility(View.VISIBLE);

//            imgMale.setImageResource(R.drawable.uncheck);
//            imgFemale.setImageResource(R.drawable.check);
        } else {
            imgFemaleActive.setVisibility(View.GONE);
            imgFemale.setVisibility(View.VISIBLE);
            imgMaleActive.setVisibility(View.VISIBLE);

            imgMale.setVisibility(View.GONE);
//            imgMale.setImageResource(R.drawable.check);
//            imgFemale.setImageResource(R.drawable.uncheck);
        }
        if (!Constant.USER_INSTA_ACCOUNT.equalsIgnoreCase("")) {
            imgInstagram.setImageResource(R.drawable.icon_insta_active);
            edtInsta.setText(Constant.USER_INSTA_ACCOUNT);
        } else {
            edtInsta.setText(insta);
            imgInstagram.setImageResource(R.drawable.ic_instagram);
        }
        if (!Constant.USER_TWITTER_ACCOUNT.equalsIgnoreCase("")) {
            imgTwitter.setImageResource(R.drawable.icon_twitter_active);
            edtTwitter.setText(Constant.USER_TWITTER_ACCOUNT);
        } else {
            edtTwitter.setText(twitter);
            imgTwitter.setImageResource(R.drawable.ic_twitter);
        }

        if (!Constant.USER_SNAPCHAT_ACCOUNT.equalsIgnoreCase("")) {
            imgSnapchat.setImageResource(R.drawable.icon_snap_active);
            edtSnapchat.setText(Constant.USER_SNAPCHAT_ACCOUNT);
        } else {
            edtSnapchat.setText(snapchat);
            imgSnapchat.setImageResource(R.drawable.ic_snapchat);
        }

        if (!Constant.USER_KIK_ACCOUNT.equalsIgnoreCase("")) {
            imgKik.setImageResource(R.drawable.icon_kik_active);
            edtKik.setText(Constant.USER_KIK_ACCOUNT);
        } else {
            edtKik.setText(kik);
            imgKik.setImageResource(R.drawable.ic_kik);
        }

        List<String> tags = Arrays.asList(Constant.USER_PROFILE_TAG.split("\\s*,\\s*"));

        //arrNew.add(tags);

        for (int i = 0; i < tags.size(); i++) {
            System.out.println("********tagggg*********" + tags.get(i));
            arrNew.add(tags.get(i));
        }

        ResourceUtils.tagEditCount = arrNew.size();
        txtTagCnt.setText(String.valueOf(ResourceUtils.tagEditCount));
        HashTagAdapter searchAdapter = new HashTagAdapter(EditProfileActivity.this, arrNew, callback, "edit");
        LinearLayoutManager llManager = new LinearLayoutManager(EditProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        /*recyclerView.setLayoutManager(llManager);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.scrollToPosition(arrNew.size() - 1);*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                backPopUp();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.ll_age:
                show();
                break;
            case R.id.ll_male:
//                imgMale.setImageResource(R.drawable.check);
//                imgFemale.setImageResource(R.drawable.uncheck);
                imgFemaleActive.setVisibility(View.GONE);
                imgFemale.setVisibility(View.VISIBLE);
                imgMaleActive.setVisibility(View.VISIBLE);

                imgMale.setVisibility(View.GONE);
                break;
            case R.id.ll_female:
//                imgMale.setImageResource(R.drawable.uncheck);
//                imgFemale.setImageResource(R.drawable.check);
                imgFemaleActive.setVisibility(View.VISIBLE);
                imgFemale.setVisibility(View.GONE);
                imgMaleActive.setVisibility(View.GONE);
                imgMale.setVisibility(View.VISIBLE);
                break;

            case R.id.btnSave:
                editProfileWebservice();
                break;
        }
    }

    private void editProfileWebservice() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("SaveProfile")
                .build());
        if (list.size() != 0) {
            Constant.USER_FIRST_NAME = edtFirstName.getText().toString();
            Constant.USER_INSTA_ACCOUNT = edtInsta.getText().toString();
            Constant.USER_SNAPCHAT_ACCOUNT = edtSnapchat.getText().toString();
            Constant.USER_TWITTER_ACCOUNT = edtTwitter.getText().toString();
            Constant.USER_KIK_ACCOUNT = edtKik.getText().toString();
            Constant.USER_INTRO = edt_intro.getText().toString();
            Constant.USER_AGE = txtSelectedAge.getText().toString();
            String[] arr = Constant.USER_FIRST_NAME.split(" ");
            if (arr.length == 1) {
                System.out.println("*******111*******" + arr[0]);
                Constant.USER_FIRST_NAME = arr[0];
            } else if (arr.length == 2) {
                System.out.println("*******222*******" + arr[0]);
                Constant.USER_FIRST_NAME = arr[0];
                Constant.USER_LAST_NAME = arr[1];
            } else if (arr.length == 3) {
                System.out.println("*******333*******" + arr[0]);
                Constant.USER_FIRST_NAME = arr[0];
                Constant.USER_LAST_NAME = arr[2];
            }

            if (imgMaleActive.getVisibility() == View.VISIBLE) {
                Constant.USER_GENDER = "male";

            } else if (imgFemaleActive.getVisibility() == View.VISIBLE) {
                Constant.USER_GENDER = "female";
            }

            for (int i = 0; i < list.size(); i++) {
                System.out.println("*******data******" + list.get(i).toString());
                tags = tags + "," + list.get(i).toString();
            }

            if (tags.startsWith(",")) {
                tags = tags.substring(1);
            }

            Constant.USER_PROFILE_TAG = tags;

            System.out.println("*******Constant.USER_PROFILE_TAG*********" + Constant.USER_PROFILE_TAG);

            network_status = NetworkStatus.checkConnection(EditProfileActivity.this);
            if (network_status.equals("false")) {
                /*AlertClass alert = new AlertClass();
                alert.customDialog(EditProfileActivity.this, "", getResources().getString(R.string.no_internet));*/
                SnackBarUtils.showSnackBarPink(getApplicationContext(), findViewById(android.R.id.content), getResources().getString(R.string.no_internet));
            } else {

                if (!Constant.USER_INSTA_ACCOUNT.isEmpty() || !Constant.USER_TWITTER_ACCOUNT.isEmpty() || !Constant.USER_SNAPCHAT_ACCOUNT.isEmpty() || !Constant.USER_KIK_ACCOUNT.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("instagram", Constant.USER_INSTA_ACCOUNT);
                        jsonObject.put("twitter", Constant.USER_TWITTER_ACCOUNT);
                        jsonObject.put("snapchat", Constant.USER_SNAPCHAT_ACCOUNT);
                        jsonObject.put("kik", Constant.USER_KIK_ACCOUNT);
                        jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID, ""));
                        jsonObject.put("access_token", SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, ""));
                        jsonObject.put("fname", Constant.USER_FIRST_NAME);
                        jsonObject.put("lname", Constant.USER_LAST_NAME);
                        jsonObject.put("age", Constant.USER_AGE);
                        jsonObject.put("gender", Constant.USER_GENDER);
                        jsonObject.put("short_bio", Constant.USER_INTRO);
                        jsonObject.put("tags", tags);
                        jsonObject.put("device_id", SharedPref.getPreferences().getString(SharedPref.GCMREGID, ""));
                        jsonObject.put("device_type", Constant.DEVICE_TYPE);
                        jsonObject.put("registration_ip", SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, ""));

                        Log.e("TAG", "request > " + jsonObject);

                        new APIRequestWithDefaultloader(EditProfileActivity.this, jsonObject, Constant.edit_profile, this, Constant.API_EDIT_PROFILE, Constant.POST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    SnackBarUtils.showSnackBarPink(EditProfileActivity.this,findViewById(android.R.id.content),getResources().getString(R.string.social_account_validation));
                   // Toast.makeText(EditProfileActivity.this, "Please Enter At Least One Social Account", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            SnackBarUtils.showSnackBarPink(EditProfileActivity.this,findViewById(android.R.id.content),getResources().getString(R.string.tag_validation));
            // Toast.makeText(EditProfileActivity.this, "Please enter atleast one tag", Toast.LENGTH_SHORT).show();
        }
    }

    private void backPopUp() {
        final Dialog dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_save_changes);

        // set the custom dialog components - text, image and button

        final TextView btn_yes = (TextView) dialog.findViewById(R.id.btn_yes);
    //    TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        final TextView btn_no = (TextView) dialog.findViewById(R.id.btn_no);

     //   txtTitle.setText("Save Changes");
       // txtMsg.setText("Do you want to save changes?");

        // if button is clicked, close the custom dialog
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                editProfileWebservice();

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.UPDATE_PROFILE_FLAG = "no";
                setData();
                dialog.dismiss();
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    public void show() {
        final Dialog d = new Dialog(EditProfileActivity.this);
       // d.setTitle("NumberPicker");
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        d.setContentView(R.layout.popup_age_selector);
        TextView txtDone = (TextView) d.findViewById(R.id.txtDone);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setMaxValue(90); // max value 100
        np.setMinValue(18);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setValue(Integer.parseInt(txtSelectedAge.getText().toString()));
        np.setOnValueChangedListener(this);
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSelectedAge.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
            }
        });

        d.show();

    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

    }

    @Override
    public void onBackPressed() {
        backPopUp();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onSuccess(BaseResponse response) {

        EditProfileResponse editProfileResponse = (EditProfileResponse) response;
        if (editProfileResponse.getStatus().equalsIgnoreCase("success")) {
            SharedPref.writeString(SharedPref.USER_TOKEN, editProfileResponse.getAccessToken());

            // Toast.makeText(EditProfileActivity.this, editProfileResponse.getDescription(), Toast.LENGTH_SHORT).show();
            SnackBarUtils.showSnackBarBlue(getApplicationContext(), findViewById(android.R.id.content), editProfileResponse.getDescription());
            Constant.UPDATE_PROFILE_FLAG = "yes";
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();

                }
            }, 1000);

        } else {
            if (editProfileResponse.getStatus().equalsIgnoreCase("Failed")) {
                if ((editProfileResponse.getCode() == 300))
                {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(this,editProfileResponse.getDescription());
                }
            }else
            if (!editProfileResponse.getDescription().equalsIgnoreCase("Send JSON data in proper format") ||
                    !editProfileResponse.getDescription().contains("This API accpets") ||
                    !editProfileResponse.getDescription().equalsIgnoreCase("Please Send JSON data") ||
                    !editProfileResponse.getDescription().equalsIgnoreCase("Your account is blocked please contact admin")) {

                SharedPref.writeString(SharedPref.USER_TOKEN, editProfileResponse.getAccessToken());
                Toast.makeText(EditProfileActivity.this, editProfileResponse.getDescription(), Toast.LENGTH_SHORT).show();

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
        txtTagCnt.setText(String.valueOf(options));
    }

    @Override
    public void onCounterChange(String cnt) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("EditProfileActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

            ResourceUtils.tagEditCount = Integer.parseInt(cnt);
            txtTagCnt.setText(cnt);
            System.out.println("**************" + list.size());
        }
    }

    @Override
    public void onEditingFinished() {
        Log.d("TaG", "OnEditing finished");
        if (ResourceUtils.tagEditCount == 10) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), 0);
            mTagsEditText.setCursorVisible(false);

            //ResourceUtils.showSnackBarPink(EditProfileActivity.this, findViewById(android.R.id.content), "You have reached maximum limit for tags");

        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTagsEditText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
            mTagsEditText.setCursorVisible(true);

        }
    }
}
