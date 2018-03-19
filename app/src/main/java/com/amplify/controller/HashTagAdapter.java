package com.amplify.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amplify.R;
import com.amplify.utils.Constant;
import com.amplify.view.UpdateText;

import java.util.ArrayList;

/**
 * Created by Admin on 05-12-2016.
 */
public class HashTagAdapter extends RecyclerView.Adapter<HashTagAdapter.MyViewHolder> {

    private ArrayList<String> arrSearchData;
    Context context;
    String flagEdit;
    UpdateText mCallBack = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        ImageButton button_delete;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.name);
            button_delete = view.findViewById(R.id.button_delete);
        }
    }

    public HashTagAdapter(Context context, ArrayList<String> horizontalList, UpdateText callback, String edit) {
        flagEdit = edit;
        this.arrSearchData = horizontalList;
        this.context = context;
        mCallBack = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hashtag, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txtView.setText("#"+arrSearchData.get(position) + " ");
        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrSearchData.remove(position);

                if(flagEdit.equalsIgnoreCase("edit"))
                {
                    System.out.println("*******tag edit*********"+Constant.tagEditCount);
                    Constant.tagEditCount = Constant.tagEditCount - 1;
                    System.out.println("*******tag edit*********"+Constant.tagEditCount);
                    mCallBack.onDataChange(String.valueOf(Constant.tagEditCount));
                }else
                {
                    Constant.tagCount = Constant.tagCount - 1;
                    mCallBack.onDataChange(String.valueOf(Constant.tagCount));
                }

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrSearchData.size();
    }
}
