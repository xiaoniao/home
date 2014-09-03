/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

// 真正要显示的LoadingLayout,继承自帧布局 , 这是一个组合式自定义布局
@SuppressLint("ViewConstructor")
public abstract class LoadingLayout extends FrameLayout implements ILoadingLayout {

    static final String         LOG_TAG                = "PullToRefresh-LoadingLayout";

    static final Interpolator   ANIMATION_INTERPOLATOR = new LinearInterpolator();

    private FrameLayout         mInnerLayout;

    protected final ImageView   mHeaderImage;
    protected final ProgressBar mHeaderProgress;

    private boolean             mUseIntrinsicAnimation;

    private final TextView      mHeaderText;
    private final TextView      mSubHeaderText;

    protected final Mode        mMode;
    protected final Orientation mScrollDirection;

    private CharSequence        mPullLabel;
    private CharSequence        mRefreshingLabel;
    private CharSequence        mReleaseLabel;

    // 构造方法
    public LoadingLayout(Context context, final Mode mode, final Orientation scrollDirection, TypedArray attrs) {
        super(context);

        mMode = mode;
        mScrollDirection = scrollDirection;

        switch (scrollDirection) {
            case HORIZONTAL:
                // 垂直布局
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_horizontal, this); // this 布局关联到该自定义布局中
                break;
            case VERTICAL:
            default:
                // 水平布局
                LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);// this 布局关联到该自定义布局中
                break;
        }

        // 获得控件
        mInnerLayout = (FrameLayout)findViewById(R.id.fl_inner);
        mHeaderText = (TextView)mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mHeaderProgress = (ProgressBar)mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
        mSubHeaderText = (TextView)mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        mHeaderImage = (ImageView)mInnerLayout.findViewById(R.id.pull_to_refresh_image);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mInnerLayout.getLayoutParams();

        switch (mode) {
            case PULL_FROM_END: // 从底部刷新 (下或者右)
                lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.TOP : Gravity.LEFT; // 设置重心

                // Load in labels
                mPullLabel = context.getString(R.string.pull_to_refresh_from_bottom_pull_label);             // 下拉刷新…
                mRefreshingLabel = context.getString(R.string.pull_to_refresh_from_bottom_refreshing_label); // 正在载入…
                mReleaseLabel = context.getString(R.string.pull_to_refresh_from_bottom_release_label);       // 放开以刷新…
                break;

            case PULL_FROM_START: // 从顶部刷新 (上或者左)
            default:
                lp.gravity = scrollDirection == Orientation.VERTICAL ? Gravity.BOTTOM : Gravity.RIGHT; // 设置重心

                // Load in labels
                mPullLabel = context.getString(R.string.pull_to_refresh_pull_label);             // 下拉刷新…
                mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label); // 正在载入…
                mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);       // 放开以刷新…
                break;
        }

        if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
            Drawable background = attrs.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
            if (null != background) {
                ViewCompat.setBackground(this, background);
            }
        }

        if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance)) {
            TypedValue styleID = new TypedValue();
            attrs.getValue(R.styleable.PullToRefresh_ptrHeaderTextAppearance, styleID);
            setTextAppearance(styleID.data);
        }
        if (attrs.hasValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance)) {
            TypedValue styleID = new TypedValue();
            attrs.getValue(R.styleable.PullToRefresh_ptrSubHeaderTextAppearance, styleID);
            setSubTextAppearance(styleID.data);
        }

        // 使用自定义属性时，先检测是否有这个属性值的做法很赞...attrs.hasValue
        
        // Text Color attrs need to be set after TextAppearance attrs
        if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderTextColor)) {
            ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderTextColor);
            if (null != colors) {
                setTextColor(colors); // 设置文字颜色
            }
        }
        if (attrs.hasValue(R.styleable.PullToRefresh_ptrHeaderSubTextColor)) {
            ColorStateList colors = attrs.getColorStateList(R.styleable.PullToRefresh_ptrHeaderSubTextColor);
            if (null != colors) {
                setSubTextColor(colors); // 设置二级文字颜色
            }
        }

        // Try and get defined drawable from Attrs
        Drawable imageDrawable = null;
        if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawable)) {
            imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawable); // 从xml获得图片
        }

        // Check Specific Drawable from Attrs, these overrite the generic drawable attr above 同样也是获得图片，但这时在有设置mode的情况下，此时会覆盖上方的drawable
        switch (mode) {
            case PULL_FROM_START:
            default:
                if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableStart)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableStart);
                } else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableTop)) {
                    Utils.warnDeprecation("ptrDrawableTop", "ptrDrawableStart");
                    imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableTop);
                }
                break;

            case PULL_FROM_END:
                if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableEnd)) {
                    imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableEnd);
                } else if (attrs.hasValue(R.styleable.PullToRefresh_ptrDrawableBottom)) {
                    Utils.warnDeprecation("ptrDrawableBottom", "ptrDrawableEnd");
                    imageDrawable = attrs.getDrawable(R.styleable.PullToRefresh_ptrDrawableBottom);
                }
                break;
        }

        // If we don't have a user defined drawable, load the default
        if (null == imageDrawable) {
            imageDrawable = context.getResources().getDrawable(getDefaultDrawableResId()); // 如果都没有图片，就使用默认的
        }

        // Set Drawable, and save width/height
        setLoadingDrawable(imageDrawable);

        reset();
    }

    // 设置高度
    public final void setHeight(int height) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)getLayoutParams();
        lp.height = height;
        requestLayout();
    }

    // 设置宽度
    public final void setWidth(int width) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams)getLayoutParams();
        lp.width = width;
        requestLayout();
    }

    // 获得整个布局的大小 如果是水平展示就获得宽，如果是垂直展示就获得高。
    public final int getContentSize() {
        switch (mScrollDirection) {
            case HORIZONTAL:
                return mInnerLayout.getWidth();
            case VERTICAL:
            default:
                return mInnerLayout.getHeight();
        }
    }

    // 隐藏所有的View (隐藏前，先判断状态，好习惯)
    public final void hideAllViews() {
        if (View.VISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.INVISIBLE);
        }
        if (View.VISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.INVISIBLE);
        }
    }

    public final void onPull(float scaleOfLayout) {
        if (!mUseIntrinsicAnimation) {
            onPullImpl(scaleOfLayout);
        }
    }

    // 下拉刷新…
    public final void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }

        // Now call the callback
        pullToRefreshImpl();
    }

    // 正在载入…
    public final void refreshing() {
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }

        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable)mHeaderImage.getDrawable()).start();
        } else {
            // Now call the callback
            refreshingImpl();
        }

        if (null != mSubHeaderText) {
            mSubHeaderText.setVisibility(View.GONE);
        }
    }

    // 放开以刷新…
    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }

        // Now call the callback
        releaseToRefreshImpl();
    }

    // 重置
    public final void reset() {
        // 一级文字
        if (null != mHeaderText) {
            mHeaderText.setText(mPullLabel);
        }
        
        // 图片
        mHeaderImage.setVisibility(View.VISIBLE);

        // 动画
        if (mUseIntrinsicAnimation) {
            ((AnimationDrawable)mHeaderImage.getDrawable()).stop();
        } else {
            // Now call the callback
            resetImpl();
        }

        // 二级文字
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(mSubHeaderText.getText())) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) { // 设置上次更新内容
        setSubHeaderText(label);
    }

    @Override
    public final void setLoadingDrawable(Drawable imageDrawable) { // 设置图片
        // Set Drawable
        mHeaderImage.setImageDrawable(imageDrawable);
        mUseIntrinsicAnimation = (imageDrawable instanceof AnimationDrawable); // 是否可以使用动画

        // Now call the callback
        onLoadingDrawableSet(imageDrawable);
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
    }
    
    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    // 重新展示所有View
    public final void showInvisibleViews() {
        if (View.INVISIBLE == mHeaderText.getVisibility()) {
            mHeaderText.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderProgress.getVisibility()) {
            mHeaderProgress.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mHeaderImage.getVisibility()) {
            mHeaderImage.setVisibility(View.VISIBLE);
        }
        if (View.INVISIBLE == mSubHeaderText.getVisibility()) {
            mSubHeaderText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Callbacks for derivative Layouts , 这一堆抽象方法，由子类实现，很好的实现方式
     */

    protected abstract int getDefaultDrawableResId();                     // 获得默认图片

    protected abstract void onLoadingDrawableSet(Drawable imageDrawable); // 设置图片

    protected abstract void onPullImpl(float scaleOfLayout);              // 动画?

    protected abstract void pullToRefreshImpl();                          // 下拉刷新

    protected abstract void refreshingImpl();                             // 正在刷新

    protected abstract void releaseToRefreshImpl();                       // 放开以刷新

    protected abstract void resetImpl();                                  // 重置动画?

    // 设置二级文字
    private void setSubHeaderText(CharSequence label) {
        if (null != mSubHeaderText) {
            if (TextUtils.isEmpty(label)) {
                mSubHeaderText.setVisibility(View.GONE);
            } else {
                mSubHeaderText.setText(label);

                // Only set it to Visible if we're GONE, otherwise VISIBLE will
                // be set soon
                if (View.GONE == mSubHeaderText.getVisibility()) {
                    mSubHeaderText.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // 设置二级文字样式
    private void setSubTextAppearance(int value) {
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextAppearance(getContext(), value);
        }
    }

    // 设置二级文字颜色
    private void setSubTextColor(ColorStateList color) {
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextColor(color);
        }
    }

    // 设置一级文字样式
    private void setTextAppearance(int value) {
        if (null != mHeaderText) {
            mHeaderText.setTextAppearance(getContext(), value);
        }
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextAppearance(getContext(), value);
        }
    }

    // 设置一级文字颜色
    private void setTextColor(ColorStateList color) {
        if (null != mHeaderText) {
            mHeaderText.setTextColor(color);
        }
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextColor(color);
        }
    }

}
