/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.threadsample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * �Զ����ImageView,���ء�������չʾͼƬ
 */
public class PhotoView extends ImageView {

	// ���������Ƿ����
	private boolean mCacheFlag;

	// ����ͼƬ�Ƿ������� onDraw�Ƿ����
	private boolean mIsDrawn;

	// ����һ�������õģ������÷�ֹ�����ڴ�й©�ͱ���,�����ô������ݵ����á�ProgressBar
	private WeakReference<View> mThisView;

	// ��Դid ������ProgressBar id
	private int mHideShowResId = -1;

	// ͼƬ·�� ����������ͼƬ·�� ����ʹ�÷��� Activity/Fragment --> PhotoView --> PhotoTask --> PhotoDownloadRunnable(����������ͼƬ)
	private URL mImageURL;

	// ����ͼƬ�߳�
	private PhotoTask mDownloadThread;

	// ���췽��
	public PhotoView(Context context) {
		super(context);
	}

	// ���췽��
	public PhotoView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		getAttributes(attributeSet);
	}

	// ���췽��
	public PhotoView(Context context, AttributeSet attributeSet, int defaultStyle) {
		super(context, attributeSet, defaultStyle);
		getAttributes(attributeSet);
	}

	// ��ȡxml����
	private void getAttributes(AttributeSet attributeSet) {
		TypedArray attributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.ImageDownloaderView);
		// ��ȡ���� id
		mHideShowResId = attributes.getResourceId(R.styleable.ImageDownloaderView_hideShowSibling, -1);
		attributes.recycle();
	}

	// ����ProgressBar�ɼ���
	private void showView(int visState) {
		if (mThisView != null) {
			// ͨ�������û��һ��ǿ����
			View localView = mThisView.get();
			if (localView != null) {
				localView.setVisibility(visState);
			}
		}
	}

	// ���ͼƬ
	public void clearImage() {
		setImageDrawable(null);
		showView(View.VISIBLE);
	}

	// ���ͼƬurl
	final URL getLocation() {
		return mImageURL;
	}

	/*
	 * ��ImageView��������Ļ���ǵ��ã���onDraw()ǰ���ã�Ҳ������onMeasure()������
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		// ����ڲ�������������mHideShowResId����
		if ((this.mHideShowResId != -1) && ((getParent() instanceof View))) {

			// ����ֵ�view,��Ӧ�������ProgressBar ps:������ÿؼ��ķ�ʽ�ܺ�
			View localView = ((View) getParent()).findViewById(this.mHideShowResId);

			// ʵ����Ϊ������
			if (localView != null) {
				this.mThisView = new WeakReference<View>(localView);
			}
		}
	}

	/*
	 * ��ImageView �� Window�Ƴ���ʱ�����, �ÿձ�����ֹ�ڴ�й©
	 */
	@Override
	protected void onDetachedFromWindow() {

		// ���ͼƬ���رջ��棬ȡ��ͼƬ��url'������
		setImageURL(null, false, null);

		// ���ͼƬ
		Drawable localDrawable = getDrawable();

		// ȡ��ͼƬ��ImageView�İ�
		if (localDrawable != null)
			localDrawable.setCallback(null);

		// ����ÿ�������
		if (mThisView != null) {
			mThisView.clear();
			mThisView = null;
		}

		// �ÿ������߳�
		this.mDownloadThread = null;

		super.onDetachedFromWindow();
	}

	/*
	 * ���ͼƬû�л��ƹ�������ͼƬ·����Ϊ�գ��͵��������߳�����ͼƬ�����������һ�¸�����
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if ((!mIsDrawn) && (mImageURL != null)) {
			// ��ʼ����ͼƬ�̣߳�ʹ�õ�ǰ�Ļ���ֵ
			mDownloadThread = PhotoManager.startDownload(this, mCacheFlag);
			mIsDrawn = true;
		}
		super.onDraw(canvas);
	}

	/**
	 * ����һ��viewΪ��ǰ������
	 */
	public void setHideView(View view) {
		this.mThisView = new WeakReference<View>(view);
	}

	// ����ͼƬ
	@Override
	public void setImageBitmap(Bitmap paramBitmap) {
		super.setImageBitmap(paramBitmap);
	}

	// ����ͼƬ
	@Override
	public void setImageDrawable(Drawable drawable) {
		int viewState; // �ɼ���

		// ����ProgressBar�ɼ���
		// ������setImageDrawableΪ�ա����ͼƬ��ʱ������Ϊ�ɼ�����������ProgressBar���ɼ���ˢ����֮ǰ
		if (drawable == null) {

			viewState = View.VISIBLE;
		} else {

			viewState = View.INVISIBLE;
		}
		showView(viewState);

		super.setImageDrawable(drawable);
	}

	// ��ʾͼƬ
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
	}

	// ͨ������Uri��ʾͼƬ ??
	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
	}

	/**
	 * ��ͼƬ����URL,Ȼ������ͼƬ
	 * 
	 * 1�����ͼƬ��URL�Ѿ����ڣ������µ�URL���Ѵ��ڵĲ�һ����Ȼ��PhotoView �Ƴ���ֹͣ�����߳�
	 * 
	 * 2������µ�URL���Ѵ��ڵ�һ������ʲôҲ����
	 * 
	 * 3������Ѵ��ڵ�URL�ǿյģ���ô��ʼ���ز��ҽ���ͼƬ
	 * 
	 */
	public void setImageURL(URL pictureURL, boolean cacheFlag, Drawable imageDrawable) {
		if (mImageURL != null) { // ���URL�Ѿ�����

			if (!mImageURL.equals(pictureURL)) { // ����µ�URL���Ѵ��ڵ�URL��һ��
				// �Ƴ��������ڵ��߳�
				PhotoManager.removeDownload(mDownloadThread, mImageURL);
			} else {
				// ���Ѵ���URLһ�£�ʲôҲ����
				return;
			}
		}

		// ����Ĭ��ͼƬ
		setImageDrawable(imageDrawable);

		// ����URLΪ��ǰURL
		mImageURL = pictureURL;

		// ����Ѿ�ִ����onDraw()����
		if ((mIsDrawn) && (pictureURL != null)) {
			// �Ƿ񻺴��ʶ
			mCacheFlag = cacheFlag;

			// ��ʼ����ͼƬ�ļ���ע�⣺�����ʼcache��ͼƬ�ļ��п����Ǵӻ����ж�ȡ��
			mDownloadThread = PhotoManager.startDownload(this, cacheFlag);
		}
	}

	// ����ͼƬ������������ǿյĻ�
	public void setStatusDrawable(Drawable drawable) {
		if (mThisView == null) {
			setImageDrawable(drawable);
		}
	}

	// ����ͼƬ������������ǿյĻ�
	public void setStatusResource(int resId) {

		if (mThisView == null) {
			setImageResource(resId);
		}
	}
}