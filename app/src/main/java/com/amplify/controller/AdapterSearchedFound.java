package com.amplify.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amplify.R;
import com.amplify.model.UserProfileModel;
import com.amplify.view.Activity.SwipeNewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 1/31/2018.
 */

public class AdapterSearchedFound extends RecyclerView.Adapter<AdapterSearchedFound.MyViewHolder> {


    Context context;
    List<UserProfileModel> modellist = new ArrayList<>();

    public AdapterSearchedFound(Context context, List<UserProfileModel> modellist) {
        this.context = context;
        this.modellist = modellist;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_searched_found, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserProfileModel data = modellist.get(position);
//        Picasso.with(context)
//                .load(data.getImage())
//                .into(holder.image);
        holder.image.setImageResource(modellist.get(position).getImage());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(context, SwipeNewActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

    }


    @Override
    public int getItemCount() {
        return modellist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);

        }
    }
}