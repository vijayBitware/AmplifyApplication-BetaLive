package com.amplify.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.amplify.R;
import com.amplify.view.Activity.LoginActivity;
import com.amplify.view.Activity.PrivacyPolicyActivity;


/**
 * This class for display error alert.
 *
 * @author (Arun Chougule)
 */

public class AlertClass {
    Context context;

    public void customDialogforAlertMessage(Activity activity, String title, String message) {
        // TODO Auto-generated method stub

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_alert);


        // set the custom dialog components - text, image and button

        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtAlertTile);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtAlertMesssage);

        txtTitle.setText(title);
        txtMsg.setText(message);

        if (title.equalsIgnoreCase(""))
            txtTitle.setVisibility(View.GONE);

        // if button is clicked, close the custom dialog
        txtOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        dialog.show();

    }
//	public void customDialogforAlertMessageForAppVersion(final Activity activity, String title,
//														 String message,String version_code) {
//		// TODO Auto-generated method stub
//
//		String text = "";
//		final Dialog dialog = new Dialog(activity);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog.getWindow().setBackgroundDrawable(new ColorDrawable (android.R.color.transparent));
//		dialog.setContentView(R.layout.popup_applink);
//
//		dialog.setCancelable(false);
//
//		fontChanger = new FontChangeCrawler(activity.getAssets());
//		fontChanger.replaceFonts((ViewGroup) dialog.getWindow().getDecorView());
//		// set the custom dialog components - text, image and button
//
//
//		TextView txtTitle = (TextView) dialog.findViewById(R.id.txtAlertTile);
//		TextView txtMsg= (TextView) dialog.findViewById(R.id.txtAlertMesssage);
//		TextView txtUpdate = (TextView) dialog.findViewById(R.id.txtUpdate);
//		TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
//
//		if(title.equalsIgnoreCase("Error"))
//		{
//			text = "<font color=#FF0000>BENEDEAL</font>";
//			txtTitle.setText(Html.fromHtml (text));
//		}
//
//		else
//			txtTitle.setText(title);
//
//
//		if(version_code.equalsIgnoreCase("-10"))
//		{
//			txtUpdate.setVisibility(View.VISIBLE);
//			txtCancel.setVisibility(View.VISIBLE);
//
//		}else if(version_code.equalsIgnoreCase("-20"))
//		{
//			txtUpdate.setVisibility(View.VISIBLE);
//			txtCancel.setVisibility(View.GONE);
//		}
//
//		txtMsg.setText(message);
//
//
//		// if button is clicked, close the custom dialog
//		txtCancel.setOnClickListener(new View.OnClickListener () {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//
//			}
//		});
//
//		// if button is clicked, close the custom dialog
//		txtUpdate.setOnClickListener(new View.OnClickListener () {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//
//				activity.startActivity(new Intent(Intent.ACTION_VIEW,
//						Uri.parse ("https:\\/\\/play.google.com\\/store\\/apps\\/details?id=com.corporateapp&hl=en")));
//
//
//				System.exit(0);
//
//			}
//		});
//
//
//		dialog.show();
//
//	}


    public void customDialog(Context activity, String title, String message) {
        // TODO Auto-generated method stub

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_alert);


        // set the custom dialog components - text, image and button

        TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtAlertTile);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txtAlertMesssage);

        txtTitle.setText(title);
        txtMsg.setText(message);

        if (title.equalsIgnoreCase(""))
            txtTitle.setVisibility(View.GONE);

        // if button is clicked, close the custom dialog
        txtOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        dialog.show();

    }


    public void customDialogforAlertMessageForAppVersion(final Activity activity,
                                                         String message, int version_code) {
        // TODO Auto-generated method stub

        String text = "";
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_link);

        dialog.setCancelable(false);

//		fontChanger = new FontChangeCrawler(activity.getAssets());
//		fontChanger.replaceFonts((ViewGroup) dialog.getWindow().getDecorView());
        // set the custom dialog components - text, image and button


        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtAlertTile);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtUpdate = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.btn_no);

//        if (title.equalsIgnoreCase("Error")) {
//            text = "<font color=#FF0000>BENEDEAL</font>";
//            txtTitle.setText(Html.fromHtml(text));
//        } else
//            txtTitle.setText(title);


//        if (version_code.equalsIgnoreCase("-10")) {
//            txtUpdate.setVisibility(View.VISIBLE);
//            txtCancel.setVisibility(View.VISIBLE);
//
//        } else if (version_code.equalsIgnoreCase("-20")) {
//            txtUpdate.setVisibility(View.VISIBLE);
//            txtCancel.setVisibility(View.GONE);
//        }

        txtMsg.setText(message);


        // if button is clicked, close the custom dialog
        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });


        // if button is clicked, close the custom dialog
        txtUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https:\\/\\/play.google.com\\/store\\/apps\\/details?id=com.corporateapp&hl=en")));


                System.exit(0);

            }
        });


        dialog.show();

    }


    public void customDialogforAlertMessagLoggedInDifferentDevice(final Context context,
                                                                  String message) {
        // TODO Auto-generated method stub

        String text = "";
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        dialog.setContentView(R.layout.popup_logged_in_another_device);

        dialog.setCancelable(false);
        //  TextView txtTitle = (TextView) dialog.findViewById(R.id.txtAlertTile);
        TextView txtMsg = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txtOk = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.btn_no);
        txtMsg.setText(message);
        // if button is clicked, close the custom dialog
        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        // if button is clicked, close the custom dialog
        txtOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(context, LoginActivity.class);
                context.startActivity(i);
                System.exit(0);
            }
        });
        dialog.show();
    }

}
