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
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;

// 指示布局 帧布局 实现了动画接口
@SuppressLint("ViewConstructor")
public class IndicatorLayout extends FrameLayout implements AnimationListener {

    static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

    private Animation mInAnim;
    private Animation mOutAnim;
    
    private ImageView mArrowImageView;          // 箭头图片

    private final Animation mRotateAnimation;
    private final Animation mResetRotateAnimation;

    // 构造方法
    public IndicatorLayout(Context context, PullToRefreshBase.Mode mode) {
        super(context);
        mArrowImageView = new ImageView(context);

        // 设置图片
        Drawable arrowD = getResources().getDrawable(R.drawable.indicator_arrow);
        mArrowImageView.setImageDrawable(arrowD);

        // 图片设置pading值
        final int padding = getResources().getDimensionPixelSize(R.dimen.indicator_internal_padding);
        mArrowImageView.setPadding(padding, padding, padding, padding);

        // 把图片添加进布局中
        addView(mArrowImageView);

        int inAnimResId, outAnimResId;

        switch (mode) {
            case PULL_FROM_END: // 从底部或者右部刷新

                inAnimResId = R.anim.slide_in_from_bottom;
                outAnimResId = R.anim.slide_out_to_bottom;
                setBackgroundResource(R.drawable.indicator_bg_bottom); // 设置背景

                // Rotate Arrow so it's pointing the correct way
                // 旋转图片
                mArrowImageView.setScaleType(ScaleType.MATRIX);
                Matrix matrix = new Matrix();
                matrix.setRotate(180f, arrowD.getIntrinsicWidth() / 2f, arrowD.getIntrinsicHeight() / 2f);
                mArrowImageView.setImageMatrix(matrix);

                break;
            default:
            case PULL_FROM_START: // 从上部或者左部刷新
                inAnimResId = R.anim.slide_in_from_top;
                outAnimResId = R.anim.slide_out_to_top;
                setBackgroundResource(R.drawable.indicator_bg_top); // 设置背景
                break;
        }

        // 进动画
        mInAnim = AnimationUtils.loadAnimation(context, inAnimResId);
        mInAnim.setAnimationListener(this);

        // 出动画
        mOutAnim = AnimationUtils.loadAnimation(context, outAnimResId);
        mOutAnim.setAnimationListener(this);

        // 速率
        final Interpolator interpolator = new LinearInterpolator();

        // 旋转动画
        mRotateAnimation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(interpolator);
        mRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setFillAfter(true);

        // 反旋转动画
        mResetRotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mResetRotateAnimation.setInterpolator(interpolator);
        mResetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
        mResetRotateAnimation.setFillAfter(true);

    }

    // 是否可见
    public final boolean isVisible() {
        Animation currentAnim = getAnimation();
        if (null != currentAnim) {
            return mInAnim == currentAnim;
        }

        return getVisibility() == View.VISIBLE;
    }

    // 隐藏
    public void hide() {
        startAnimation(mOutAnim);         // 展示隐藏动画
    }

    // 展示
    public void show() {
        mArrowImageView.clearAnimation(); // 清除动画
        startAnimation(mInAnim);          // 开始展示动画
    }

    // 动画结束时调用
    @Override
    public void onAnimationEnd(Animation animation) {  // 如果是展示动画，就设置控件可见，如果是隐藏动画，就设置控件不可见。
        if (animation == mOutAnim) {          // 隐藏动画
            mArrowImageView.clearAnimation(); // 清除动画
            setVisibility(View.GONE);         // 隐藏本自定义View
        } else if (animation == mInAnim) {    // 展示动画
            setVisibility(View.VISIBLE);      // 设置本自定义View可见
        }

        clearAnimation();                     // 清除动画
    }

    // 动画重复执行时调用
    @Override
    public void onAnimationRepeat(Animation animation) {
        // NO-OP
    }

    // 动画开始时调用
    @Override
    public void onAnimationStart(Animation animation) {
        setVisibility(View.VISIBLE);          // 设置本自定义View可见
    }

    // 松开
    public void releaseToRefresh() {
        mArrowImageView.startAnimation(mRotateAnimation);
    }

    // 下拉刷新
    public void pullToRefresh() {
        mArrowImageView.startAnimation(mResetRotateAnimation);
    }

}
