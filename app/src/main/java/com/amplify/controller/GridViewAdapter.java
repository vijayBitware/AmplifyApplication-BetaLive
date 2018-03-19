package com.amplify.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.amplify.R;
import com.amplify.model.DeleteAccount;
import com.amplify.model.GetAllUsers.UserDatum;
import com.amplify.utils.Constant;
import com.amplify.utils.NetworkStatus;
import com.amplify.view.Activity.SwipeNewActivity;
import com.amplify.webservice.APIRequestNew;
import com.amplify.webservice.BaseResponse;
import com.bumptech.glide.Glide;
import com.amplify.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Admin on 2/12/2018.
 */

public class GridViewAdapter extends ArrayAdapter<UserDatum> implements APIRequestNew.ResponseHandler {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private List<UserDatum> imgURLs;

    public GridViewAdapter(Context context, int layoutResource, String append, List<UserDatum> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
    }

    private static class ViewHolder{
        ImageView image;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */

        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        if (imgURLs.get(position).getFbProfilePic().equalsIgnoreCase("")){
            holder.image.setImageResource(R.drawable.user_placeholder);
        }

        Glide.with(mContext)
                .load(imgURLs.get(position).getFbProfilePic())
                .placeholder(R.drawable.user_placeholder)
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.userPosition = position;
                Activity activity = (Activity) mContext;
                Intent intent = new Intent(mContext, SwipeNewActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                callAddViewApi(imgURLs.get(position).getId());
            }
        });

        return convertView;
    }

    private void callAddViewApi(String id) {
        if (NetworkStatus.isConnectingToInternet(mContext)){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", SharedPref.getPreferences().getString(SharedPref.USER_ID,""));
                jsonObject.put("profile_user_id",id);
                jsonObject.put("access_token",SharedPref.getPreferences().getString(SharedPref.USER_TOKEN,""));
                jsonObject.put("device_id",SharedPref.getPreferences().getString(SharedPref.GCMREGID,""));
                jsonObject.put("device_type",Constant.DEVICE_TYPE);
                jsonObject.put("registration_ip",SharedPref.getPreferences().getString(SharedPref.IP_ADDRESS,""));
                Log.e("TAG","Request > " +jsonObject);

                new APIRequestNew(mContext,jsonObject,Constant.add_viewed_profile,this,Constant.API_ADD_VIEW,Constant.POST);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
          //  Toast.makeText(mContext,mContext.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSuccess(BaseResponse response) {
        DeleteAccount deleteAccount = (DeleteAccount) response;
        if (deleteAccount.getStatus().equalsIgnoreCase("success")){
            SharedPref.writeString(SharedPref.USER_TOKEN, deleteAccount.getAccessToken());
            // Toast.makeText(mContext,deleteAccount.getDescription(),Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(mContext,deleteAccount.getDescription(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(BaseResponse response) {

    }
}
