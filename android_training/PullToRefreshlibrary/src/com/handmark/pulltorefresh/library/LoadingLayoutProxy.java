package com.handmark.pulltorefresh.library;

import java.util.HashSet;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.handmark.pulltorefresh.library.internal.LoadingLayout;

// LoadingLayout ���� ( ���ڲ���LoadingLayoutʵ������������ )
public class LoadingLayoutProxy implements ILoadingLayout {

    private final HashSet<LoadingLayout> mLoadingLayouts; // LoadingLayout����

    // ���췽��
    LoadingLayoutProxy() {
        mLoadingLayouts = new HashSet<LoadingLayout>();
    }

    /**
     * This allows you to add extra LoadingLayout instances to this proxy. This is only necessary if you keep your own
     * instances, and want to have them included in any
     * {@link PullToRefreshBase#createLoadingLayoutProxy(boolean, boolean) createLoadingLayoutProxy(...)} calls.
     * 
     * @param layout - LoadingLayout to have included.
     */
    public void addLayout(LoadingLayout layout) {
        if (null != layout) {
            mLoadingLayouts.add(layout); // ����LoadingLayoutʵ��������
        }
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        /** ѭ�����������ϴθ��� **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setLastUpdatedLabel(label);
        }
    }

    @Override
    public void setLoadingDrawable(Drawable drawable) {
        /** ѭ����������ͼƬ **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setLoadingDrawable(drawable);
        }
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        /** ѭ��������������ˢ������ **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setRefreshingLabel(refreshingLabel);
        }
    }

    @Override
    public void setPullLabel(CharSequence label) {
        /** ѭ������������ˢ������ **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setPullLabel(label);
        }
    }

    @Override
    public void setReleaseLabel(CharSequence label) {
        /** ѭ������������������ **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setReleaseLabel(label);
        }
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        /** ѭ����������������ʽ **/
        for (LoadingLayout layout : mLoadingLayouts) {
            layout.setTextTypeface(tf);
        }
    }
}