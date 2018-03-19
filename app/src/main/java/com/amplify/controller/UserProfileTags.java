package com.amplify.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amplify.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitware on 16/2/18.
 */

public class UserProfileTags extends RecyclerView.Adapter<UserProfileTags.MyViewHolder> {

    private List<String> arrSearchData;
    Context context ;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView,tvRemove;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.name);
            //tvRemove = (TextView) view.findViewById(R.id.tvRemove);
        }
    }
    public UserProfileTags(Context context,List<String> horizontalList) {
        this.arrSearchData = horizontalList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_hashtag, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtView.setText("#"+arrSearchData.get(position)+" ");
    }
    @Override
    public int getItemCount() {
        return arrSearchData.size();
    }
}
