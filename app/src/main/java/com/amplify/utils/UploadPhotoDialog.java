package com.amplify.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplify.R;

/**
 * This class handle functionality for Upload user profile picture.
 * @author (Arun Chougule)
 */
public class UploadPhotoDialog extends Dialog {

	Activity context;
	ImageView imgProfile;
	String image_tag;
	final int CAMERA_CAPTURE = 1;
	final int PIC_CROP = 2;
	public Uri picUri;
	public static String profile_="";
	String path = Environment.getExternalStorageDirectory() + "/image.jpg";

	public onMediaDialogListener mMediaDialogListener;

	public UploadPhotoDialog(Activity context,
                             onMediaDialogListener mediaDialogListener, String tag) {
		super(context);
		this.context = context;
		mMediaDialogListener = mediaDialogListener;
		image_tag = tag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.popup_uplaodprofilepic);
		
		
		initViews(); 
	}

	/**
	 *  Setup your content views.. Nothing special in this..
	 */
	private void initViews() {
		// TODO Auto-generated method stub

		this.setCancelable(false);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		// set the custom dialog components - text, image and button


		TextView btn_Gallery = (TextView) findViewById(R.id.txtGalery);
		TextView btn_Delete = (TextView) findViewById(R.id.txtDelete);
		TextView btn_ShowPhoto = (TextView) findViewById(R.id.txtShowProfilePic);
		TextView btn_Cancel = (TextView) findViewById(R.id.txtCancelPopup);
		LinearLayout lin_Delete = (LinearLayout) findViewById(R.id.linDelete);
		LinearLayout lin_ShowProfilePic = (LinearLayout) findViewById(R.id.linShowProfilePic);
		TextView btn_Camera = (TextView) findViewById(R.id.txtCamera);
			System.out.println("Photot tag============"+image_tag);

				if(image_tag.equalsIgnoreCase("no_image")){
					lin_Delete.setVisibility(View.GONE);
					lin_ShowProfilePic.setVisibility(View.GONE);
				}
					

		
		btn_Camera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				mMediaDialogListener.onCameraClick();
			}
		});

		btn_Gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				mMediaDialogListener.onGalleryClick();
			}
		});

		btn_Cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}

	public Bitmap onPhotoTaken() {
		// Log message
		System.out.println("onPhotoTaken");
		// BitmapFactory- Create an object
		BitmapFactory.Options options = new BitmapFactory.Options();
		// Set image size
		options.inSampleSize = 4;
		// Read bitmap from the path where captured image is stored
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		profile_ = path;
		System.out.println("Path of camera capture===== "+path);
		// Set ImageView with the bitmap read in the prev line

		return bitmap;
	}


	public interface onMediaDialogListener {
		public void onCameraClick();
		public void onGalleryClick();
		public void onDeleteClick();
	}

	void showAlert(String title, String message){
		AlertDialog.Builder alert=new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		
		alert.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});


		alert.create();
		alert.show();
	}



		
/**This method for resize the image before upload to database
 * @param path
 * @param reqWidth
 * @param reqHeight
 * @return
 */
	
	
	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth,
                                                     int reqHeight) {

			  final BitmapFactory.Options options = new BitmapFactory.Options();
			  options.inJustDecodeBounds = true;
			  BitmapFactory.decodeFile(path, options);

			  options.inSampleSize = calculateInSampleSize(options, reqWidth,
			    reqHeight);

			  // Decode bitmap with inSampleSize set
			  options.inJustDecodeBounds = false;
			  Bitmap bmp = BitmapFactory.decodeFile(path, options);
			  return bmp;
			 
			 }

			 public static int calculateInSampleSize(BitmapFactory.Options options,
			   int reqWidth, int reqHeight) {
			  final int height = options.outHeight;
			  final int width = options.outWidth;
			  int inSampleSize = 1;

			  if (height > reqHeight || width > reqWidth) {
			   if (width > height) {
			    inSampleSize = Math.round((float) height / (float) reqHeight);
			   } else {
			    inSampleSize = Math.round((float) width / (float) reqWidth);
			   }
			  }
			  return inSampleSize;
			 
			 }
}