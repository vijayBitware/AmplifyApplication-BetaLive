package com.amplify.view.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.customphoto.cropoverlay.CropOverlayView;
import com.amplify.customphoto.cropoverlay.edge.Edge;
import com.amplify.customphoto.cropoverlay.utils.ConstantsImageCrop;
import com.amplify.customphoto.cropoverlay.utils.ImageViewUtil;
import com.amplify.customphoto.cropoverlay.utils.InternalStorageContentProvider;
import com.amplify.customphoto.cropoverlay.utils.Utils;
import com.amplify.customphoto.customcropper.CropperView;
import com.amplify.customphoto.customcropper.CropperViewAttacher;
import com.amplify.utils.BitmapUtils;
import com.amplify.utils.Constant;
import com.amplify.utils.SharedPref;
import com.amplify.utils.UploadPhotoDialog;
import com.amplify.view.Fragment.EditImageFragment;
import com.amplify.view.Fragment.FiltersListFragment;
import com.amplify.view.MyApplication;
import com.amplify.webservice.WebServiceImage;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


@SuppressWarnings("ALL")
public class ActivityCropImage extends AppCompatActivity implements IMainActivity, View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    private static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_STORAGE = 1;
    //private Uri fileUri;
    //Bitmap tempBmp;
    private final int IMAGE_MAX_SIZE = 1024;
    private final Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    protected ImageView imgImage, userImage;
    ProgressDialog dialog;
    private float minScale = 1f;
    private RelativeLayout relativeImage;
    private Button btnUploadImage, btnSubmit;
    private ImageView cropDone, cancelUpload;
    private CropperView cropperView;
    private CropOverlayView cropOverlayView;
    //private File mFileTemp;
    private String currentDateandTime = "";
    //private String mImagePath = null;
    //private Uri mSaveUri = null;
    //private Uri mImageUri = null;
    private ContentResolver mContentResolver;
    CircleImageView profile_image;
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
    // File fullImageFile, croppedImageFile;
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
    private Tracker mTracker;
    File fullImageFile=null;
    //////////////////////////////////////////////////

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
            System.out.println("Orientation >> " +orientation);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropimage);
        createDirIfNotExists();
        findViews();
        initViews();

        createFileForFullPhoto();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestExternlStoragePermission();
        }

        showCropping();
        init();

    }

    private void createFileForFullPhoto() {
        fullImageFile = new File(MyApplication.getContext().getCacheDir(),Constant.getTimeStamp() +".png");
        try {
            fullImageFile.createNewFile();
            System.out.println("## crop image bitmap >> " +Constant.finalBitmapImage);
            Bitmap bitmap = Constant.finalBitmapImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(fullImageFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            ActivityCompat.requestPermissions(ActivityCropImage.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    private void requestExternlStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(ActivityCropImage.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePic();
            } else {
                Toast.makeText(this, "CAMERA permission was NOT granted.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Storage permission was NOT granted.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void initViews() {
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mContentResolver = getContentResolver();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());
        makeLayoutSquare();
        initClickListner();
        cropperView.addListener(new CropperViewAttacher.IGetImageBounds() {
            @Override
            public Rect getImageBounds() {
                return new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
            }
        });
    }

    @Override
    public void findViews() {
        btnUploadImage = (Button) findViewById(R.id.btnUploadPhoto);
        btnSubmit = (Button) findViewById(R.id.btnNext);
        btnSubmit.setOnClickListener(this);
        relativeImage = (RelativeLayout) findViewById(R.id.relativeImage);
        cropperView = (CropperView) findViewById(R.id.cropperView);
        cropOverlayView = (CropOverlayView) findViewById(R.id.cropOverlayView);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        userImage = findViewById(R.id.imgProfile);
        Glide.with(ActivityCropImage.this).load(Constant.USER_PROFILE_IMAGE).into(userImage);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void makeLayoutSquare() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        relativeImage.setLayoutParams(params);
    }


    @Override
    public void hideCropping() {
        userImage.setVisibility(View.GONE);
        profile_image.setVisibility(View.VISIBLE);
        profile_image.buildDrawingCache();
        Drawable drawable = profile_image.getDrawable();

        Bitmap bmp = drawableToBitmap(drawable);
        profile_image.setImageBitmap(bmp);
        cropperView.setVisibility(View.GONE);
        cropOverlayView.setVisibility(View.GONE);
        findViewById(R.id.test).setVisibility(View.GONE);
        SaveImage(bmp);
        callSubmitProfileApi();
    }


    public void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String fname = Constant.getTimeStamp() + ".png";
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

    @Override
    public void showCropping() {
        // imgImage.setVisibility(View.GONE);
        profile_image.setVisibility(View.GONE);
        cropperView.setVisibility(View.VISIBLE);
        cropOverlayView.setVisibility(View.GONE);
        findViewById(R.id.test).setVisibility(View.VISIBLE);
        userImage.setVisibility(View.GONE);
    }

    @Override
    public void initClickListner() {
        btnUploadImage.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
//        cropDone.setOnClickListener(this);
//        cancelUpload.setOnClickListener(this);


    }

    @Override
    public void onGetImages(String action) {
        //createTempFile();
        if (null != action) {
            switch (action) {
                case ConstantsImageCrop.IntentExtras.ACTION_CAMERA:
                    getIntent().removeExtra("ACTION");
                    takePic();
                    return;
                case ConstantsImageCrop.IntentExtras.ACTION_GALLERY:
                    getIntent().removeExtra("ACTION");
                    pickImage();
                    return;
            }
        }

    }

    @Override
    public void createTempFile() {
       /* String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Constant.mFileTemp = new File(Environment.getExternalStorageDirectory() + "/imagecroplikeinstagram", currentDateandTime + TEMP_PHOTO_FILE_NAME);
            System.out.println("*******if***imagecroplikeinstagram*************" + Constant.mFileTemp.toString());
        } else {
            Constant.mFileTemp = new File(getFilesDir() + "/imagecroplikeinstagram", currentDateandTime + TEMP_PHOTO_FILE_NAME);
            System.out.println("*******else***imagecroplikeinstagram*************" + Constant.mFileTemp.toString());
        }*/
    }

    @Override
    public void takePic() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();

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

    private void init() {
        String profile_ = "";
        profile_ = Constant.mImagePath;
        showCropping();
        //Bitmap b = getBitmap(Constant.mImageUri);
        System.out.println("*******Bitmap******" + Constant.finalBitmapImage);
        Drawable bitmap = new BitmapDrawable(getResources(), Constant.finalBitmapImage);
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
            if (getCameraPhotoOrientation(this, Uri.parse(Constant.mFileTemp.getPath())) == 0) {
                return bitmap;
            } else {
                Matrix matrix = new Matrix();
                matrix.postRotate(getCameraPhotoOrientation(this, Uri.parse(Constant.mFileTemp.getPath())));
                // recreate the new Bitmap and set it back
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void saveAndUploadImage() {
        configDialog();
        boolean saved = saveOutput();

        if (saved) {
            profile_image.setImageBitmap(getBitmap(Constant.mImageUri));
            //  imgImage.setImageBitmap(getBitmap(mImageUri));
            System.out.println("*******setImageBitmap*********" + Constant.mImageUri);
//            Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show();
            hideCropping();
        } else {
        }


    }

    void configDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading..");
    }

    void dismisDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

    void showDialog() {
        dialog.show();

    }

    private boolean saveOutput() {
        Bitmap croppedImage = getCroppedImage();

        if (Constant.mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                System.out.println("*********bitmap******" + Constant.mSaveUri.toString());
                outputStream = mContentResolver.openOutputStream(Constant.mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

                }
                System.out.println("*********croppedImage******" + croppedImage);
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable t) {
                    }
                }
            }
        } else {
            return false;
        }
        croppedImage.recycle();
        return true;
    }

    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(cropperView.getWidth(), cropperView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(result);
        cropperView.draw(c);
        return result;
    }

    public Bitmap getCroppedImage() {
        Bitmap mCurrentDisplayedBitmap = getCurrentDisplayedImage();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mCurrentDisplayedBitmap, cropperView);

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = mCurrentDisplayedBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = mCurrentDisplayedBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;

        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(mCurrentDisplayedBitmap, (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        //createTempFile();
        showCropping();
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                hideCropping();
                profile_image.setVisibility(View.VISIBLE);
                return;
            }
            Constant.mImagePath = Constant.mFileTemp.getPath();
            Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
            Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            Bitmap bmp = BitmapFactory.decodeFile(Constant.mImagePath, options);
            if (!(bmp == null))
                getServiceResponseForPhoto(bmp);
            init();
        } else if (requestCode == REQUEST_CODE_PICK_GALLERY && resultCode == RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                hideCropping();
                profile_image.setVisibility(View.VISIBLE);
                profile_image.setImageResource(R.mipmap.ic_launcher);

                //  imgImage.setVisibility(View.VISIBLE);
                //  imgImage.setImageResource(R.mipmap.ic_launcher);
                Toast.makeText(this, "NO permission on Storage", Toast.LENGTH_SHORT).show();

                //code for default image
                return;
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(result.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(Constant.mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                Constant.mImagePath = Constant.mFileTemp.getPath();
                Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
                Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
                init();
            } catch (Exception e) {
            }
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != result) {
            System.out.println("***********RESULT_LOAD_IMAGE********");
            Uri selectedImage = result.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            try {
                File f = null;
                if (picturePath != null) {
                    System.out.println("***********RESULT_LOAD_IMAGE********if" + picturePath);
                    f = new File(picturePath);
                } else
                    Toast.makeText(ActivityCropImage.this, "Error while rendering image.", Toast.LENGTH_SHORT).show();
                f.createNewFile();
                UploadPhotoDialog.profile_ = f.toString();

                cursor.close();
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
                Toast.makeText(ActivityCropImage.this, "Error while rendering image.", Toast.LENGTH_SHORT).show();
            }
            Bitmap bmp = UploadPhotoDialog.decodeSampledBitmapFromPath(UploadPhotoDialog.profile_, 500, 500);//150*150
            if (bmp != null)
                tempBmp = bmp;

            /////////////////////////////////////
            profile_image.setVisibility(View.VISIBLE);

            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(result.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(Constant.mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                Constant.mImagePath = UploadPhotoDialog.profile_;
                Constant.mSaveUri = Utils.getImageUri(Constant.mImagePath);
                Constant.mImageUri = Utils.getImageUri(Constant.mImagePath);
                init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Webservice call to upload image
            getServiceResponse();
        } else {
            hideCropping();
            // imgImage.setVisibility(View.VISIBLE);
            profile_image.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnUploadPhoto:
                Constant.mImagePath = null;//do again
                mUploadPhotoDialog = new UploadPhotoDialog(ActivityCropImage.this, mMediaDialogListener, "no_image");
                mUploadPhotoDialog.show();
                break;
            case R.id.btnNext:
                if (Constant.mImagePath != null) saveAndUploadImage();
                break;
            case R.id.imgBack:
                finish();
                break;
        }
    }

    UploadPhotoDialog.onMediaDialogListener mMediaDialogListener = new UploadPhotoDialog.onMediaDialogListener() {

        @Override
        public void onGalleryClick() {
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
            /*Constant.mImagePath = null;
            onGetImages(ConstantsImageCrop.IntentExtras.ACTION_CAMERA);*/
            // TODO Auto-generated method stub
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            System.out.println("File URI------" + fileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE);
        }

    };

    void finishBack(boolean status) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("isupdate", status);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    void finishBack() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
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
     * @param Bitmap - bitmap
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
                Constant.fullImageFile = new File(getCacheDir(), "android.png");
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

                    Constant.fullImageFile = new File(getCacheDir(), "android1.png");
                    Constant.fullImageFile.createNewFile();
                    fos2 = new FileOutputStream(Constant.fullImageFile, false);
                    fos2.write(bitmapdata);
                    //imgProfile.setImageBitmap(tempBmp);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //callSubmitProfileApi();
                //profile_image.setImageBitmap(tempBmp);

            }
        } catch (IOException e1) {
            e1.printStackTrace();
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
            //startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            //Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            //toast.show();
        }
    }


    private void callSubmitProfileApi() {

        try {
            String token = SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, "");
            System.out.println("Token is >" + token);
            WebServiceImage service = new WebServiceImage(callback);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("user_id", new StringBody(SharedPref.getPreferences().getString(SharedPref.USER_ID, "")));
            reqEntity.addPart("access_token", new StringBody(SharedPref.getPreferences().getString(SharedPref.USER_TOKEN, "")));
            reqEntity.addPart("device_id", new StringBody(SharedPref.getPreferences().getString(SharedPref.GCMREGID, "")));
            reqEntity.addPart("device_type", new StringBody(Constant.DEVICE_TYPE));
            reqEntity.addPart("registration_ip", new StringBody(SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS, "")));

            if (fullImageFile == null) {
                //  FLAG_IMG = true;
                reqEntity.addPart("image", new StringBody(""));
            } else {
                // FLAG_IMG = true;
                reqEntity.addPart("image", new FileBody(fullImageFile));
            }
            if (Constant.croppedImageFile == null) {
                //  FLAG_IMG = true;
                reqEntity.addPart("crop_image", new StringBody(""));
            } else {
                // FLAG_IMG = true;
                reqEntity.addPart("crop_image", new FileBody(Constant.croppedImageFile));
            }

            service.getService(ActivityCropImage.this, Constant.update_profile_pic, reqEntity);

        } catch (NullPointerException e) {
            System.out.println("Nullpointer Exception at Login Screen" + e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    WebServiceImage.CallbackImage callback = new WebServiceImage.CallbackImage() {
        @Override
        public void onSuccessImage(int reqestcode, JSONObject rootjson) {
            System.out.println("++++++-result image++++++" + rootjson);
            try {
                if (rootjson.get("status").toString().equals("success")) {
                    SharedPref.writeString(SharedPref.USER_TOKEN, rootjson.get("access_token").toString());
                    Constant.USER_PROFILE_IMAGE = rootjson.getString("profile_image_url");
                    Constant.CROPPED_IMAGE = rootjson.getString("crop_image");
                    Constant.UPDATE_PROFILE = "yes";
                    Constant.isUpdateProfile = "yes";
                    System.out.print("*****profile*****" + Constant.USER_PROFILE_IMAGE+"**"+Constant.CROPPED_IMAGE);
                    startActivity(new Intent(ActivityCropImage.this,HomeActivity.class));
                    finish();
                } else {
                    SharedPref.writeString(SharedPref.USER_TOKEN, rootjson.get("access_token").toString());
                }

            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("++++++-Exception++++++" + e);
            }
        }

        @Override
        public void onErrorImage(int reqestcode, String error) {
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("ActivityCropImage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}