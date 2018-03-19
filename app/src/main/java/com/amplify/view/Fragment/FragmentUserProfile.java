package com.amplify.view.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.controller.UserProfileTags;
import com.amplify.customphoto.cropoverlay.CropOverlayView;
import com.amplify.customphoto.cropoverlay.edge.Edge;
import com.amplify.customphoto.cropoverlay.utils.ConstantsImageCrop;
import com.amplify.customphoto.cropoverlay.utils.InternalStorageContentProvider;
import com.amplify.customphoto.cropoverlay.utils.Utils;
import com.amplify.customphoto.customcropper.CropperView;
import com.amplify.model.InviteFriend;
import com.amplify.model.MyProfileRes.MyProfleDetailsResponse;
import com.amplify.utils.AlertClass;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.utils.UploadPhotoDialog;
import com.amplify.view.Activity.ActivityImageFilter;
import com.amplify.view.Activity.ActivityProfilePreview;
import com.amplify.view.Activity.ActivitySetting;
import com.amplify.view.Activity.EditProfileActivity;
import com.amplify.view.Activity.IMainActivity;
import com.amplify.view.MyApplication;
import com.amplify.webservice.APIRequest;
import com.amplify.webservice.APIRequestWithDefaultloader;
import com.amplify.webservice.BaseResponse;
import com.bumptech.glide.Glide;
import com.amplify.utils.SharedPref;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bitware on 9/2/18.
 */

public class FragmentUserProfile extends Fragment implements IMainActivity, APIRequestWithDefaultloader.ResponseHandler, View.OnClickListener,APIRequest.ResponseHandler, ActivityCompat.OnRequestPermissionsResultCallback {

    /////////////////////////////////////////////////////////////////////////////
    String flag = "no";
    String message="";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    //private Uri fileUri;
    //Bitmap tempBmp;
    private final int IMAGE_MAX_SIZE = 1024;
    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    protected ImageView imgImage, userImage,imgSettingtwo;
    ProgressDialog dialog;
    private float minScale = 1f;
    private RelativeLayout relativeImage;
    private Button btnUploadImage, btnSubmit;
    private ImageView cropDone, cancelUpload;
    private CropperView cropperView;
    private CropOverlayView cropOverlayView;
    //private File mFileTemp;
    private String currentDateandTime = "";
   // private String mImagePath = null;
   // private Uri mSaveUri = null;
    //private Uri mImageUri = null;
    private ContentResolver mContentResolver;
    //CircleImageView profile_image;
    //for camera permission
    private boolean isDeleted = false;
    final int PIC_CROP = 2;
    View view;
    ImageView imgProfileEdit, imgProfileView, imgSetting, imgProfile, imgInstagram, imgSnapchat, imgTwitter, imgKik, imgGender;
    private static int RESULT_LOAD_IMAGE = 3;
    //final int CAMERA_CAPTURE = 1;
    int profile_pic = 0;
    String network_status;
    Context c;
    TextView txtName, txtAge, txtIntro;
    //RecyclerView rv_tags;
    //File fullImageFile, croppedImageFile;
    Button btnInvite;
    //////////////////////////////////////////////////
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private static int GALLARY = 3;
    final int CAMERA_CAPTURE = 1;
    Bitmap tempBmp;
    //UploadPhotoDialog mUploadPhotoDialog;
    Uri fileUri;
    //ImageView iv_scrapPhoto;
    String scrapType, scrapDesciption, scrapAmount, scrapId, scrapUnit;
    //SharedPref sharedPref;
    ArrayList<String> arrScrapNames, arrScrapIDs;
    ImageView imgBack, imgNotification;
    TextView txtHeading, txtNotCount;
    RelativeLayout relativeNotCnt;
    ImageView ivImgView1, ivImgView2, ivImgView3, ivImgView4;
    LinearLayout linearImgView;
    String imgNo;
    String photo1 = "", photo2 = "", photo3 = "", photo4 = "";
    UploadPhotoDialog mUploadPhotoDialog;
    SharedPref pref;
    RecyclerView rv_tags;
    LinearLayout ll_noInternet, ll_setData, ll_user_data;
    Button btnRetry;
    private Tracker mTracker;
    //////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        init();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("UserProfile");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    private void init() {

        Constant.NAVIGATE_TAB_USER = Constant.SELECTED_TAB;
        Constant.NAVIGATE_TAB_LIKE = Constant.SELECTED_TAB_LIKE;
        Constant.SELECTED_TAB = "";
        Constant.SELECTED_TAB_LIKE = "";
        Constant.isUpdateProfile = "no";
        pref = new SharedPref(getActivity());
        imgGender = view.findViewById(R.id.imgGender);
        txtIntro = view.findViewById(R.id.txtIntro);
        btnInvite = view.findViewById(R.id.btnInvite);
        txtName = view.findViewById(R.id.txtName);
        txtAge = view.findViewById(R.id.txtAge);
        imgInstagram = view.findViewById(R.id.imgInstagram);
        imgKik = view.findViewById(R.id.imgKik);
        imgSnapchat = view.findViewById(R.id.imgSnapchat);
        imgTwitter = view.findViewById(R.id.imgTwitter);
        imgProfileEdit = view.findViewById(R.id.imgProfileEdit);
        imgProfileView = view.findViewById(R.id.imgProfileView);
        imgSetting = view.findViewById(R.id.imgSetting);
        imgProfile = view.findViewById(R.id.imgProfile);
        ll_noInternet = view.findViewById(R.id.ll_noInternet);
        btnRetry = view.findViewById(R.id.btnRetry);
        ll_user_data = view.findViewById(R.id.ll_user_data);
        imgSettingtwo = view.findViewById(R.id.imgSettingtwo);

        imgProfileEdit.setOnClickListener(this);
        imgProfileView.setOnClickListener(this);
        imgSetting.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        imgSettingtwo.setOnClickListener(this);

        rv_tags = view.findViewById(R.id.rv_tags);
        rv_tags.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        mContentResolver = getActivity().getContentResolver();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        createDirIfNotExists();
        getProfileData();

    }

    private void getProfileData() {
        c = MyApplication.getContext();
        network_status = NetworkStatus.checkConnection(getActivity());
        if (network_status.equals("false")) {
            ll_noInternet.setVisibility(View.VISIBLE);
            ll_user_data.setVisibility(View.GONE);
        } else {
            ll_noInternet.setVisibility(View.GONE);
            ll_user_data.setVisibility(View.VISIBLE);
            try {

                String url = Constant.get_profile_details + "?" + "user_id=" + SharedPref.getPreferences().getString(SharedPref.USER_ID, "") +
                        "&access_token=" + SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, "") +
                        "&device_id=" + SharedPref.getPreferences().getString(SharedPref.GCMREGID, "")
                        + "&device_type=" + Constant.DEVICE_TYPE + "&registration_ip=" + SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, "");

                System.out.println("Login Url >" + url);
                JSONObject jsonObject = new JSONObject();
                try {
                    Log.e("TAG", "request > " + jsonObject);

                    new APIRequestWithDefaultloader(getContext(), jsonObject, url, this, Constant.API_GET_PROFILE, Constant.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (NullPointerException e) {
                System.out.println("Nullpointer Exception at Login Screen" + e);
            }
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (result1 == PackageManager.PERMISSION_GRANTED || result4 == PackageManager.PERMISSION_GRANTED || result5 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    //turnOnGps();

                    System.out.println("************granted*********");

                    mUploadPhotoDialog = new UploadPhotoDialog(getActivity(), mMediaDialogListener, "no_image");
                    mUploadPhotoDialog.show();
                    //startActivity(new Intent(getContext(), ActivityCropImage.class));
                    //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    //not granted
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Need Permissions");
                    builder.setMessage("This app needs camera permissions.");
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
            case R.id.imgProfileEdit:
                if (flag.equalsIgnoreCase("yes")) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),message);
                } else {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
            case R.id.imgProfileView:
                if (flag.equalsIgnoreCase("yes")) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),message);
                } else {
                    startActivity(new Intent(getContext(), ActivityProfilePreview.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
            case R.id.imgSetting:
                if (flag.equalsIgnoreCase("yes")) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),message);
                }else {
                    startActivity(new Intent(getContext(), ActivitySetting.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                break;
            case R.id.imgProfile:
                if (flag.equalsIgnoreCase("yes")) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),message);
                } else {
                    int MyVersion = Build.VERSION.SDK_INT;
                    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        System.out.println("***********version*********" + MyVersion);
                        if (!checkIfAlreadyhavePermission()) {
                            requestForSpecificPermission();
                        } else {
                            Constant.mImagePath = null;//do again
                            mUploadPhotoDialog = new UploadPhotoDialog(getActivity(), mMediaDialogListener, "no_image");
                            mUploadPhotoDialog.show();
                            //startActivity(new Intent(getContext(), ActivityCropImage.class));
                            //getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    } else {
                        Constant.mImagePath = null;//do again
                        mUploadPhotoDialog = new UploadPhotoDialog(getActivity(), mMediaDialogListener, "no_image");
                        mUploadPhotoDialog.show();
                    }
                }
                break;
            case R.id.btnInvite:
                if (flag.equalsIgnoreCase("yes")) {
                    AlertClass alert = new AlertClass();
                    alert.customDialogforAlertMessagLoggedInDifferentDevice(getActivity(),message);
                } else {
                    inviteFriend();
                }

                break;
            case R.id.btnRetry:
                if (getActivity() != null) {
                    if (NetworkStatus.isConnectingToInternet(getContext())) {
                        ll_noInternet.setVisibility(View.INVISIBLE);
                        ll_user_data.setVisibility(View.VISIBLE);
                        getProfileData();
                    } else {
                        ll_noInternet.setVisibility(View.VISIBLE);
                        ll_user_data.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.imgSettingtwo:
                startActivity(new Intent(getContext(), ActivitySetting.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
        }
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
    /**
     * Helper method to carry out crop operation
     *
     * @param picUri
     */
    public void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        if (response.getApiName() == Constant.API_GET_PROFILE) {
            MyProfleDetailsResponse profileRes = (MyProfleDetailsResponse) response;
            if (profileRes.getStatus().equalsIgnoreCase("Success")) {
                SharedPref.writeString(SharedPref.USER_TOKEN, profileRes.getAccessToken());
                if (profileRes.getMyProfile().getCrop_profile().equalsIgnoreCase("")) {
                    System.out.println("In if condition");
                    Glide.with(getActivity()).load(profileRes.getMyProfile().getFbProfilePic()).into(imgProfile);
                } else {
                    System.out.println("In else condition");
                    Glide.with(getActivity()).load(profileRes.getMyProfile().getCrop_profile()).into(imgProfile);

                }

                String fullName = profileRes.getMyProfile().getFname() + " " + profileRes.getMyProfile().getLname();
                txtName.setText(Constant.capitalize(fullName));
                txtAge.setText(profileRes.getMyProfile().getAge());
                if (!profileRes.getMyProfile().getBio().equalsIgnoreCase("")) {
                    txtIntro.setText(Constant.onlyFirstLetterCapital(profileRes.getMyProfile().getBio()));
                }else {
                    txtIntro.setText(profileRes.getMyProfile().getBio());
                }
                System.out.println("******txtIntro********" + txtIntro.toString());

                if (!profileRes.getMyProfile().getInstagram().equalsIgnoreCase("")) {
                    imgInstagram.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_insta_active));
                } else {
                    imgInstagram.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_instagram));
                }

                if (!profileRes.getMyProfile().getKik().equalsIgnoreCase("")) {
                    imgKik.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_kik_active));
                } else {
                    imgKik.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_kik));
                }

                if (!profileRes.getMyProfile().getTwitter().equalsIgnoreCase("")) {
                    imgTwitter.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_twitter_active));
                } else {
                    imgTwitter.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_twitter));
                }

                if (!profileRes.getMyProfile().getSnapchat().equalsIgnoreCase("")) {
                    imgSnapchat.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_snap_active));
                } else {
                    imgSnapchat.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_snapchat));
                }

                if (profileRes.getMyProfile().getGender().equalsIgnoreCase("female")) {
                    imgGender.setImageDrawable(getResources().getDrawable(R.mipmap.icon_woman));
                } else {
                    imgGender.setImageDrawable(getResources().getDrawable(R.mipmap.icon_man));
                }

                List<String> tags = Arrays.asList(profileRes.getMyProfile().getTags().split("\\s*,\\s*"));
                Collections.reverse(tags);
                rv_tags.setAdapter(new UserProfileTags(getActivity(), tags));
                rv_tags.scrollToPosition(tags.size() - 1);

                Constant.USER_FIRST_NAME = txtName.getText().toString();
                Constant.USER_AGE = txtAge.getText().toString();
                Constant.USER_INTRO = txtIntro.getText().toString();
                Constant.USER_GENDER = profileRes.getMyProfile().getGender();
                Constant.USER_INSTA_ACCOUNT = profileRes.getMyProfile().getInstagram();
                Constant.USER_SNAPCHAT_ACCOUNT = profileRes.getMyProfile().getSnapchat();
                Constant.USER_TWITTER_ACCOUNT = profileRes.getMyProfile().getTwitter();
                Constant.USER_KIK_ACCOUNT = profileRes.getMyProfile().getKik();
                Constant.USER_FB_LOCATION = profileRes.getMyProfile().getFbLocation();
                Constant.USER_LIKE_CNT = profileRes.getMyProfile().getLikeCnt();
                Constant.USER_PROFILE_IMAGE = profileRes.getMyProfile().getFbProfilePic();
                Constant.USER_PROFILE_TAG = profileRes.getMyProfile().getTags();

            }else if (profileRes.getStatus().equalsIgnoreCase("Failed")) {
                if ((profileRes.getCode() == 300))
                {
                   flag="yes";
                   message=profileRes.getDescription().toString();
                    System.out.println("*****message***"+message);
                }
            }
        }else {
            InviteFriend inviteFriend = (InviteFriend) response;
            if (inviteFriend.getStatus().equalsIgnoreCase("success")) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Invitation");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invite_friend_text)+"\n"+inviteFriend.getLink());
                startActivity(Intent.createChooser(sharingIntent, "Invite Friends Via"));
            }
        }

    }

    @Override
    public void onFailure(BaseResponse response) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (Constant.UPDATE_PROFILE_FLAG.equalsIgnoreCase("yes")) {
            System.out.println("******onresume*******" + Constant.USER_FIRST_NAME);
            txtName.setText(Constant.USER_FIRST_NAME +" "+ Constant.USER_LAST_NAME);
            txtAge.setText(Constant.USER_AGE);
           // txtIntro.setText(Constant.onlyFirstLetterCapital(Constant.USER_INTRO));
            if (!Constant.USER_INTRO.isEmpty()) {
                txtIntro.setText(Constant.onlyFirstLetterCapital(Constant.USER_INTRO));
            }else {
                txtIntro.setText(Constant.USER_INTRO);
            }

            if (Constant.USER_GENDER.equalsIgnoreCase("female")) {
                imgGender.setImageDrawable(getResources().getDrawable(R.mipmap.icon_woman));
            } else {
                imgGender.setImageDrawable(getResources().getDrawable(R.mipmap.icon_man));
            }

            if (!Constant.USER_INSTA_ACCOUNT.equalsIgnoreCase("")) {
                imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.icon_insta_active));
            } else {
                imgInstagram.setImageDrawable(getResources().getDrawable(R.drawable.ic_instagram));
            }

            if (!Constant.USER_SNAPCHAT_ACCOUNT.equalsIgnoreCase("")) {
                imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.icon_snap_active));
            } else {
                imgSnapchat.setImageDrawable(getResources().getDrawable(R.drawable.ic_snapchat));
            }

            if (!Constant.USER_KIK_ACCOUNT.equalsIgnoreCase("")) {
                imgKik.setImageDrawable(getResources().getDrawable(R.drawable.icon_kik_active));
            } else {
                imgKik.setImageDrawable(getResources().getDrawable(R.drawable.ic_kik));
            }

            if (!Constant.USER_TWITTER_ACCOUNT.equalsIgnoreCase("")) {
                // imgTwitter.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_twitter_active));
                imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.icon_twitter_active));
            } else {
                imgTwitter.setImageDrawable(getResources().getDrawable(R.drawable.ic_twitter));
            }

            System.out.println("****Constant.USER_PROFILE_TAG userFragment****"+Constant.USER_PROFILE_TAG);
            List<String> tags = Arrays.asList(Constant.USER_PROFILE_TAG.split("\\s*,\\s*"));
            List<String> reverceTags = new ArrayList<>();
            for(int j = tags.size() - 1; j >= 0; j--) {
                reverceTags.add(tags.get(j));
                //System.out.println(tags.get(j));
            }

            rv_tags.setAdapter(new UserProfileTags(getActivity(), reverceTags));
            rv_tags.scrollToPosition(0);

          //  Glide.with(getActivity()).load(Constant.CROPPED_IMAGE).into(imgProfile);
        }
        else if (Constant.UPDATE_PROFILE.equalsIgnoreCase("yes")) {
            if (Constant.CROPPED_IMAGE.equalsIgnoreCase("")) {
                System.out.println("****in if condition***");
                Glide.with(getActivity()).load(Constant.USER_PROFILE_IMAGE).into(imgProfile);
            } else {
                System.out.println("****in else condition***");
                Glide.with(getActivity()).load(Constant.CROPPED_IMAGE).into(imgProfile);

            }
        }
    }

    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[512];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static int getCameraPhotoOrientation(@NonNull Context context, Uri imageUri) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            ExifInterface exif = new ExifInterface(
                    imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static boolean createDirIfNotExists() {
        boolean ret = true;
        File file = new File(Environment.getExternalStorageDirectory(), "Prototype");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false;
            }
        }
        return ret;

    }

    private void showCameraPreview() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(Constant.mFileTemp);
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            takePictureIntent.putExtra("return-data", true);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
        }
    }


    public void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String fname = "Image-" + "2" + ".jpg";
        Constant.croppedImageFile = new File(myDir, fname);
        if (Constant.croppedImageFile.exists()) Constant.croppedImageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(Constant.croppedImageFile);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }



    public void initViews() {
        String profile_ = "";
        profile_ = Constant.mImagePath;
        //showCropping();
        Bitmap b = getBitmap(Constant.mImageUri);
        System.out.println("*******Bitmap******" + b);
        Drawable bitmap = new BitmapDrawable(getResources(), b);
        int h = bitmap.getIntrinsicHeight();
        int w = bitmap.getIntrinsicWidth();
        final float cropWindowWidth = Edge.getWidth();
        final float cropWindowHeight = Edge.getHeight();
        if (h <= w) {
            minScale = (cropWindowHeight + 1f) / h;
        } else if (w < h) {
            minScale = (cropWindowWidth + 1f) / w;
        }

        cropperView.setMaximumScale(minScale * 9);
        cropperView.setMediumScale(minScale * 6);
        cropperView.setMinimumScale(minScale);
        cropperView.setImageDrawable(bitmap);
        cropperView.setScale(minScale);

    }

    @Override
    public void findViews() {

    }

    @Override
    public void makeLayoutSquare() {

    }

    @Override
    public void hideCropping() {

    }

    @Override
    public void showCropping() {

    }

    @Override
    public void initClickListner() {


    }

    @Override
    public void onGetImages(String action) {
        createTempFile();
        if (null != action) {
            switch (action) {
                case ConstantsImageCrop.IntentExtras.ACTION_CAMERA:
                    getActivity().getIntent().removeExtra("ACTION");
                    takePic();
                    return;
                case ConstantsImageCrop.IntentExtras.ACTION_GALLERY:
                    getActivity().getIntent().removeExtra("ACTION");
                    pickImage();
                    return;
            }
        }

    }

    @Override
    public void createTempFile() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Constant.mFileTemp = new File(Environment.getExternalStorageDirectory() + "/Amplify", currentDateandTime + TEMP_PHOTO_FILE_NAME);
            System.out.println("*******if***imagecroplikeinstagram*************" + Constant.mFileTemp.toString());
        } else {
            Constant.mFileTemp = new File(getActivity().getFilesDir() + "/Amplify", currentDateandTime + TEMP_PHOTO_FILE_NAME);
            System.out.println("*******else***imagecroplikeinstagram*************" + Constant.mFileTemp.toString());
        }
    }

    @Override
    public void takePic() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestForSpecificPermission();

        } else {
            showCameraPreview();
        }


    }

    @Override
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
        } catch (ActivityNotFoundException e) {
        }
    }

    private Bitmap getBitmap(Uri uri) {
        InputStream in = null;
        Bitmap returnedBitmap = null;
        try {
            in = mContentResolver.openInputStream(uri);
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            returnedBitmap = fixOrientationBugOfProcessedBitmap(bitmap);
            return returnedBitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private Bitmap fixOrientationBugOfProcessedBitmap(Bitmap bitmap) {
        try {
            if (getCameraPhotoOrientation(getActivity(), Uri.parse(Constant.mFileTemp.getPath())) == 0) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(getCameraPhotoOrientation(getActivity(), Uri.parse(Constant.mFileTemp.getPath())));
                // recreate the new Bitmap and set it back
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }




    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(cropperView.getWidth(), cropperView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(result);
        cropperView.draw(c);
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        createTempFile();
        //showCropping();
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                //hideCropping();
                //profile_image.setVisibility(View.VISIBLE);
                return;
            }
            Constant.mImagePath = Constant.mFileTemp.getPath();
            Log.e("Fragment uSer profile","Camera capture > "+Constant.mImagePath);
            Constant.orignalImagePath = Constant.mImagePath;
            Constant.imageToFilter = Constant.mImagePath;
            Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
            Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bmp = BitmapFactory.decodeFile(Constant.mImagePath, options);
             // tempBmp = bmp;

            if (!(bmp == null)) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(Constant.mImagePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    System.out.println("Orientation >>> " + orientation);

                    switch(orientation){
                        case 8:
                            Matrix matrix = new Matrix();
                            matrix.setRotate(270);
                            matrix.setRotate(270, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                            bmp = rotatedBitmap;
                            break;
                        case 6:
                            Matrix matrix1 = new Matrix();
                            matrix1.setRotate(90);
                            matrix1.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                            Bitmap rotatedBitmap1 = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix1, true);// Return result
                            bmp = rotatedBitmap1;
                            break;
                        case 3:
                            Matrix matrix2 = new Matrix();
                            matrix2.setRotate(180);
                            matrix2.setRotate(180, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                            Bitmap rotatedBitmap2 = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix2, true);// Return result
                            bmp = rotatedBitmap2;
                            break;
                    }
                    /*if (orientation == 8) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(270);
                        matrix.setRotate(270, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                        bmp = rotatedBitmap;
                    }else if (orientation==6){
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        matrix.setRotate(90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                        bmp = rotatedBitmap;
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Constant.orignalBitmap = bmp;
            getServiceResponseForPhoto(bmp);
           // init();
        } else if (requestCode == REQUEST_CODE_PICK_GALLERY && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //hideCropping();
                //profile_image.setVisibility(View.VISIBLE);
                //profile_image.setImageResource(R.mipmap.ic_launcher);

                //  imgImage.setVisibility(View.VISIBLE);
                //  imgImage.setImageResource(R.mipmap.ic_launcher);
                Toast.makeText(getActivity(), "NO permission on Storage", Toast.LENGTH_SHORT).show();

                //code for default image
                return;
            }

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(result.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(Constant.mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                Constant.mImagePath = Constant.mFileTemp.getPath();
                Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
                Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
                //init();
            } catch (Exception e) {
            }
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != result) {
            System.out.println("***********RESULT_LOAD_IMAGE********");
            Uri selectedImage = result.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            try {
                File f = null;
                if (picturePath != null) {
                    System.out.println("***********RESULT_LOAD_IMAGE********if" + picturePath);
                    f = new File(picturePath);
                } else
                    Toast.makeText(getActivity(), "Error while rendering image.", Toast.LENGTH_SHORT).show();
                f.createNewFile();
                UploadPhotoDialog.profile_ = f.toString();
                Log.e("Fragment uSer profile","Gallry capture > "+UploadPhotoDialog.profile_);
                Constant.imageToFilter = UploadPhotoDialog.profile_;
                Constant.orignalImagePath = UploadPhotoDialog.profile_;
                cursor.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error while rendering image.", Toast.LENGTH_SHORT).show();
            }
            Bitmap bmp = UploadPhotoDialog.decodeSampledBitmapFromPath(UploadPhotoDialog.profile_, 500, 500);//150*150
            if (bmp != null)
                tempBmp = bmp;

            if (!(tempBmp == null)) {
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(picturePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    System.out.println("Orientation >>> " + orientation);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    switch (orientation){
                        case 8:
                            Matrix matrix = new Matrix();
                            matrix.setRotate(270);
                            matrix.setRotate(270, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                            Bitmap rotatedBitmap = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                            tempBmp = rotatedBitmap;
                            break;
                        case 6:
                            Matrix matrix1 = new Matrix();
                            matrix1.setRotate(90);
                            matrix1.setRotate(90, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                            Bitmap rotatedBitmap2 = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix1, true);// Return result
                            tempBmp = rotatedBitmap2;
                            break;
                        case 3:
                            break;
                    }

                    /*if (orientation == 8) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(270);
                        matrix.setRotate(270, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                        tempBmp = rotatedBitmap;
                    }else if (orientation==6){
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        matrix.setRotate(90, (float) tempBmp.getWidth() / 2, (float) tempBmp.getHeight() / 2);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(tempBmp, 0, 0, options.outWidth, options.outHeight, matrix, true);// Return result
                        tempBmp = rotatedBitmap;
                    }*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /////////////////////////////////////
            //profile_image.setVisibility(View.VISIBLE);

            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(result.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(Constant.mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                Constant.mImagePath = UploadPhotoDialog.profile_;
                Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
                Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
                //init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Webservice call to upload image
            getServiceResponse();
        } else {
            //hideCropping();
            // imgImage.setVisibility(View.VISIBLE);
           // profile_image.setVisibility(View.VISIBLE);


        }
    }


    /* @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUploadPhoto:
                mImagePath = null;//do again
                mUploadPhotoDialog = new UploadPhotoDialog(ActivityCropImage.this, mMediaDialogListener, "no_image");
                mUploadPhotoDialog.show();
                break;
            case R.id.btnNext:
                if (mImagePath != null) saveAndUploadImage();
                break;
            case R.id.imgBack:
                finish();
                break;
        }
    }
*/
    UploadPhotoDialog.onMediaDialogListener mMediaDialogListener = new UploadPhotoDialog.onMediaDialogListener() {

        @Override
        public void onGalleryClick() {
            // TODO Auto-generated method stub
            Constant.orignalBitmap = null;
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(i, GALLARY);
        }

        @Override
        public void onDeleteClick() {

        }

        @Override
        public void onCameraClick() {
            Constant.orignalBitmap = null;
            // TODO Auto-generated method stub
            Constant.mImagePath = null;
            onGetImages(ConstantsImageCrop.IntentExtras.ACTION_CAMERA);
        }

    };

    void finishBack(boolean status) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isupdate", status);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    void finishBack() {
        Intent returnIntent = new Intent();
        getActivity().setResult(Activity.RESULT_CANCELED, returnIntent);
        getActivity().finish();
    }

    /*
       * returning image / video
       */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * This method for upload profile picture to server
     *
     *
     */
    public void getServiceResponseForPhoto(Bitmap bitmap) {
        tempBmp = bitmap;
        try {
            Constant.fullImageFile = new File(Environment.getExternalStorageDirectory() + "/_camera.png");
            if (Constant.fullImageFile != null) {
                Constant.fullImageFile.createNewFile();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray(); // convert camera photo to byte array
                // save it in your external storage.
                FileOutputStream fo = new FileOutputStream(Constant.fullImageFile);
                fo.write(byteArray);
                tempBmp = bitmap;
                FileOutputStream fos2;

                //callSubmitProfileApi();
                startActivity(new Intent(getContext(), ActivityImageFilter.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            } else {
            }

        } catch (Exception e) {
        }
    }

    /**
     * This method for upload profile picture to server
     */
    public void getServiceResponse() {
        try {
            if (!(Constant.mImagePath.equalsIgnoreCase(""))) {
                // create a file to write bitmap data
                System.out.println("******++******" + UploadPhotoDialog.profile_);
                Constant.fullImageFile = new File(getActivity().getCacheDir(), "android.png");
                Constant.fullImageFile.createNewFile();
                // Convert bitmap to byte array
                Bitmap bitmap = UploadPhotoDialog.decodeSampledBitmapFromPath(Constant.mImagePath, 500, 500); //50*50
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                // write the bytes in file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(Constant.fullImageFile, false);
                    fos.write(bitmapdata);
                    FileOutputStream fos2;

                    Constant.fullImageFile = new File(getActivity().getCacheDir(), "android1.png");
                    Constant.fullImageFile.createNewFile();
                    fos2 = new FileOutputStream(Constant.fullImageFile, false);
                    fos2.write(bitmapdata);
                    //imgProfile.setImageBitmap(tempBmp);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //callSubmitProfileApi();
                //profile_image.setImageBitmap(tempBmp);
                Constant.orignalBitmap = bitmap;
                startActivity(new Intent(getContext(), ActivityImageFilter.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);

            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

}
