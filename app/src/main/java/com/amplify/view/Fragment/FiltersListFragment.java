package com.amplify.view.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amplify.R;
import com.amplify.controller.ThumbnailsAdapter;
import com.amplify.utils.BitmapUtils;
import com.amplify.utils.Constant;
import com.amplify.utils.SpacesItemDecoration;
import com.amplify.view.Activity.ActivityImageFilter;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FiltersListFragment extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    ThumbnailsAdapter mAdapter;
    List<ThumbnailItem> thumbnailItemList,fullImageThumbnails;
    FiltersListFragmentListener listener;
    FiltersListFragmentFullImageListener fullImageListner;

    public void setListener(FiltersListFragmentListener listener,FiltersListFragmentFullImageListener imageListener) {
        this.listener = listener;
        this.fullImageListner = imageListener;
    }


    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filters_list, container, false);

        ButterKnife.bind(this, view);
        thumbnailItemList = new ArrayList<>();
        fullImageThumbnails = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, fullImageThumbnails,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        /*int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));*/
        recyclerView.setAdapter(mAdapter);

        Bitmap fullImageBitmap = Constant.orignalBitmap;
        prepareThumbnail(fullImageBitmap);

        return view;
    }

    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage ;
                if (bitmap == null) {
                    // thumbImage = Constant.bitmapToFilter;
                    System.out.println("***if bitmap is null");
                    thumbImage = BitmapUtils.getBitmapFromAssets(getActivity(), ActivityImageFilter.IMAGE_NAME, 70, 120);
                    //thumbImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } else {
                    System.out.println("***else bitmap is not null");
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 70, 120, false);
                }

                if (thumbImage == null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = getString(R.string.filter_normal);
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());
                System.out.println("filter list size >>" +filters.size());
                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if (listener != null)
            listener.onFilterSelected(filter);
    }

    public interface FiltersListFragmentListener {
        void onFilterSelected(Filter filter);
    }

    public interface FiltersListFragmentFullImageListener {
        void onFullImageFilterSelected(Filter filter);
    }

    @Override
    public void onFullImageFilterSelected(Filter filter) {
        if (fullImageListner != null)
            fullImageListner.onFullImageFilterSelected(filter);
    }
}