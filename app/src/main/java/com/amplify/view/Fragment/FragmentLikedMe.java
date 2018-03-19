package com.amplify.view.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplify.R;
import com.amplify.controller.GridViewAdapter;
import com.amplify.controller.MyPagerAdapter;
import com.amplify.model.UserProfileModel;
import com.amplify.utils.Constant;
import com.amplify.view.Activity.FilterActivity;
import com.amplify.view.MyApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by Admin on 1/31/2018.
 */

public class FragmentLikedMe extends Fragment implements View.OnClickListener {
    Context context;
    ArrayList<UserProfileModel> imageList;
   // LinearLayout llLikedMe, llViewdMe;
    //TextView txtLikedMe, txtViewedMe;
    ImageView imgFilter;
    //View viewLikedMe, viewViewedMe;
    GridView gridView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Tracker mTracker;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_liked_me, container, false);
        init(view);
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyApplication application = (MyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("LikedMe");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void init(View view) {
        context = MyApplication.getContext();
        imgFilter = view.findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        Constant.fromLikeNotification=0;

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("**********tab************" + tab.getPosition() + "**" + tab.getText().toString());
                if(tab.getPosition() == 1)
                {
                    Constant.SELECTED_TAB = "";
                    Constant.SELECTED_TAB_LIKE = "viewedme";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.viewed_me_active);
                }else if(tab.getPosition() == 0)
                {
                    Constant.SELECTED_TAB = "";
                    Constant.SELECTED_TAB_LIKE = "likedme";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.liked_me_active);
                }else
                {
                    Constant.SELECTED_TAB = "";
                    Constant.SELECTED_TAB_LIKE = "mylikes";
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.my_likes_active);
                }
                viewPager.setCurrentItem(tab.getPosition());
                /*TextView textView = (TextView) tab.getCustomView();
                textView.setTextColor(getResources().getColor(R.color.blue));*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.liked_me_inactive);
                } else if (tab.getPosition() == 1) {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.viewed_me_inactive);
                } else {
                    ImageView imageView = (ImageView) tab.getCustomView();
                    imageView.setImageResource(R.drawable.my_likes_inactive);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupTabIcons() {

        ImageView tabOne = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        tabOne.setImageResource(R.drawable.liked_me_active);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        ImageView tabTwo = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        tabTwo.setImageResource(R.drawable.viewed_me_inactive);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        ImageView tabThree = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        tabThree.setImageResource(R.drawable.my_likes_inactive);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        /*TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_text, null);
        tabTwo.setText("Liked Me");
        tabTwo.setTextColor(getResources().getColor(R.color.blue));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_liked_me, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_text, null);
        tabOne.setText("Viewed Me");
        tabOne.setTextColor(getResources().getColor(R.color.text_colour_grey));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_all_tab, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabOne);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_text, null);
        tabThree.setText("My Likes");
        tabThree.setTextColor(getResources().getColor(R.color.text_colour_grey));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selector_my_likes_tab, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);*/

    }

    private void setupViewPager(ViewPager viewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new LikedMeFragment(), "Liked Me");
        adapter.addFragment(new ViewedMeFragment(), "Viewed Me");
        adapter.addFragment(new MyLikesFragment(), "My Likes");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgFilter:
                startActivity(new Intent(getContext(), FilterActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

        }
    }
}
