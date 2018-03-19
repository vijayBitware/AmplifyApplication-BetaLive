package com.tylersuehr.chips;

import android.content.Context;
import android.graphics.PorterDuff;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tylersuehr.chips.data.CallBackInt;
import com.tylersuehr.chips.data.Chip;

/**
 * Copyright © 2017 Tyler Suehr
 * <p>
 * This view displays the normal chip (specified in Material Design Guide).
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public class ChipView extends FrameLayout implements ChipComponent {
    private ChipImageRenderer mImageRenderer;

    private CircleImageView mAvatarView;
    private ImageButton mButtonDelete;
    private TextView mLabelView;
    private Chip mChip;
    CallBackInt mCallBack = null;

    ChipView(@NonNull Context context, CallBackInt callBack) {
        super(context);
        System.out.println("**********set chip*******1");
        mCallBack = callBack;
        inflate(context, R.layout.chip_view, this);
        mAvatarView = findViewById(R.id.avatar);
        mLabelView = findViewById(R.id.label);
        mButtonDelete = findViewById(R.id.button_delete);
    }

    @Override
    public void setChipOptions(ChipOptions options) {
        // Options will permit showing/hiding avatar

        Utils.TAG_COUNT = Utils.TAG_COUNT + 1;
        mCallBack.UpdateMyText(Utils.TAG_COUNT);
        System.out.println("**********set chip*******2");
        if (!options.mShowAvatar) {
            // Hide the avatar image
            mAvatarView.setVisibility(GONE);

            // Adjust left label margins according to the
            // Google Material Design Guide.
            ConstraintLayout.LayoutParams lp = (ConstraintLayout
                    .LayoutParams) mLabelView.getLayoutParams();
            lp.leftMargin = (int) (12f * getResources().getDisplayMetrics().density);
        }

        // Options will permit showing/hiding delete button
        if (!options.mShowDelete) {
            // Hide the delete button
            mButtonDelete.setVisibility(GONE);

            // Adjust right label margins according to the
            // Google Material Design Guide.
            ConstraintLayout.LayoutParams lp = (ConstraintLayout
                    .LayoutParams) mLabelView.getLayoutParams();
            lp.rightMargin = (int) (12f * getResources().getDisplayMetrics().density);
        }

        // Set other options
        if (options.mChipDeleteIcon != null) {
            mButtonDelete.setImageDrawable(options.mChipDeleteIcon);
        }
        if (options.mChipBackgroundColor != null) {
            setBackgroundColor(options.mChipBackgroundColor.getDefaultColor());
        }
        if (options.mChipDeleteIconColor != null) {
            mButtonDelete.setColorFilter(options.mChipDeleteIconColor
                    .getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
        }
        if (options.mChipTextColor != null) {
            mLabelView.setTextColor(options.mChipTextColor);
        }
        mLabelView.setTypeface(options.mTypeface);

        mImageRenderer = options.mImageRenderer;
    }

    /**
     * Displays the information stored in the given chip object.
     *
     * @param chip {@link Chip}
     */
    public void inflateFromChip(Chip chip) {

        mChip = chip;
        mLabelView.setText("#" + mChip.getTitle());
        System.out.println("*******inflate******"+mChip.getTitle());
        if(!Utils.TAGLIST.contains(mChip.getTitle()))
        {
            Utils.TAGLIST.add(mChip.getTitle());
        }
        System.out.println("********"+Utils.TAGLIST);
        if (mImageRenderer == null) {
            throw new NullPointerException("Image renderer must be set!");
        }
        mImageRenderer.renderAvatar(mAvatarView, chip);
    }

    public Chip getChip() {
        System.out.println("******getchip******");
        return mChip;
    }

    /**
     * Sets an OnClickListener on the ChipView itself.
     *
     * @param listener {@link OnChipClickListener}
     */
    public void setOnChipClicked(final OnChipClickListener listener) {
        getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChipClicked(ChipView.this);
            }
        });
    }

    /**
     * Sets an OnClickListener on the delete button.
     *
     * @param listener {@link OnChipDeleteListener}
     */
    public void setOnDeleteClicked(final OnChipDeleteListener listener) {
        mButtonDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.TAG_COUNT = Utils.TAG_COUNT - 1;
                System.out.println("**********delete****"+Utils.TAG_COUNT);
                mCallBack.UpdateMyText(Utils.TAG_COUNT);
                listener.onChipDeleted(ChipView.this);
            }
        });
    }

    /**
     * Defines callbacks for chip click events.
     */
    public interface OnChipClickListener {
        void onChipClicked(ChipView v);
    }

    /**
     * Defines callbacks for chip delete events.
     */
    public interface OnChipDeleteListener {
        void onChipDeleted(ChipView v);
    }
}