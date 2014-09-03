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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * ��Activity (ͨ��IntentService��̨�߳�����ͼƬ��IntentHandlerʵ��Fragment��Activityͨ��)
 */
@SuppressLint("NewApi")
public class DisplayActivity extends FragmentActivity implements OnBackStackChangedListener {

	View mMainView;

	// �Զ���㲥������
	DownloadStateReceiver mDownloadStateReceiver;

	boolean mSideBySide;
	boolean mHideNavigation;
	boolean mFullScreen;
	int mPreviousStackCount;

	// �Զ���㲥������
	private FragmentDisplayer mFragmentDisplayer = new FragmentDisplayer();

	private static final String CLASS_TAG = "DisplayActivity";

	// ����ȫ��
	public void setFullScreen(boolean fullscreen) {
		getWindow().setFlags(fullscreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mFullScreen = fullscreen;
		if (Build.VERSION.SDK_INT >= 11) {
			int flag = fullscreen ? View.SYSTEM_UI_FLAG_LOW_PROFILE : 0;
			if (Build.VERSION.SDK_INT >= 14 && fullscreen) {
				flag |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			}
			mMainView.setSystemUiVisibility(flag);
			if (fullscreen) {
				this.getActionBar().hide();
			} else {
				this.getActionBar().show();
			}
		}
	}

	/*
	 * onDestroyView -> onCreateView
	 */
	@Override
	public void onBackStackChanged() {
		int previousStackCount = mPreviousStackCount;

		FragmentManager localFragmentManager = getSupportFragmentManager();

		int currentStackCount = localFragmentManager.getBackStackEntryCount();

		mPreviousStackCount = currentStackCount;

		boolean popping = currentStackCount < previousStackCount;

		if (popping) {
			setFullScreen(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.EXTENDED_FULLSCREEN, mFullScreen);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle stateBundle) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);

		super.onCreate(stateBundle);

		mMainView = getLayoutInflater().inflate(R.layout.fragmenthost, null);

		setContentView(mMainView);

		/********* DownloadStateReceiver *********/

		// ����Action
		IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
		// ����Category
		statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		// ʵ�����㲥
		mDownloadStateReceiver = new DownloadStateReceiver();
		// ע��㲥
		LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadStateReceiver, statusIntentFilter);

		/********* mFragmentDisplayer *********/

		IntentFilter displayerIntentFilter = new IntentFilter(Constants.ACTION_VIEW_IMAGE);
		displayerIntentFilter.addDataScheme("http");
		LocalBroadcastManager.getInstance(this).registerReceiver(mFragmentDisplayer, displayerIntentFilter);

		/********* mFragmentDisplayer *********/

		displayerIntentFilter = new IntentFilter(Constants.ACTION_ZOOM_IMAGE);
		LocalBroadcastManager.getInstance(this).registerReceiver(mFragmentDisplayer, displayerIntentFilter);

		FragmentManager localFragmentManager = getSupportFragmentManager();

		mSideBySide = getResources().getBoolean(R.bool.sideBySide);
		mHideNavigation = getResources().getBoolean(R.bool.hideNavigation);

		// ���û�ջ�¼�
		localFragmentManager.addOnBackStackChangedListener(this);

		if (null == stateBundle) {

			FragmentTransaction localFragmentTransaction = localFragmentManager.beginTransaction();
			localFragmentTransaction.add(R.id.fragmentHost, new PhotoThumbnailFragment(), Constants.THUMBNAIL_FRAGMENT_TAG);
			localFragmentTransaction.commit();

		} else {

			mFullScreen = stateBundle.getBoolean(Constants.EXTENDED_FULLSCREEN);
			setFullScreen(mFullScreen);
			mPreviousStackCount = localFragmentManager.getBackStackEntryCount();
		}
	}

	@Override
	public void onDestroy() {

		// ȡ��ע��
		if (mDownloadStateReceiver != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadStateReceiver);
			mDownloadStateReceiver = null;
		}

		// ȡ��ע��
		LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mFragmentDisplayer);

		mMainView = null;

		super.onDestroy();
	}

	@Override
	protected void onStop() {

		// ȡ��������������
		PhotoManager.cancelAll();
		super.onStop();
	}

	/**
	 * �Զ���㲥 ����״̬
	 */
	private class DownloadStateReceiver extends BroadcastReceiver {

		// ˽�� ��ֹ��ʵ����
		private DownloadStateReceiver() {

		}

		@Override
		public void onReceive(Context context, Intent intent) {

			switch (intent.getIntExtra(Constants.EXTENDED_DATA_STATUS, Constants.STATE_ACTION_COMPLETE)) {

			case Constants.STATE_ACTION_STARTED: // ��ʼ

				if (Constants.LOGD) {
					Log.d(CLASS_TAG, "State: STARTED");
				}

				break;
			case Constants.STATE_ACTION_CONNECTING: // ����

				if (Constants.LOGD) {
					Log.d(CLASS_TAG, "State: CONNECTING");
				}

				break;
			case Constants.STATE_ACTION_PARSING: // ������

				if (Constants.LOGD) {
					Log.d(CLASS_TAG, "State: PARSING");
				}

				break;
			case Constants.STATE_ACTION_WRITING: // д����

				if (Constants.LOGD) {
					Log.d(CLASS_TAG, "State: WRITING");
				}

				break;
			case Constants.STATE_ACTION_COMPLETE: // ���

				// Logs the status
				if (Constants.LOGD) {
					Log.d(CLASS_TAG, "State: COMPLETE");
				}

				// ��ʾ����ͼ��Fragment
				PhotoThumbnailFragment localThumbnailFragment = (PhotoThumbnailFragment) getSupportFragmentManager().findFragmentByTag(Constants.THUMBNAIL_FRAGMENT_TAG);

				if ((localThumbnailFragment == null) || (!localThumbnailFragment.isVisible()))
					return;

				localThumbnailFragment.setLoaded(true);
				break;
			default:
				break;
			}
		}
	}

	/** �Զ���㲥 **/
	private class FragmentDisplayer extends BroadcastReceiver {

		// ���췽��
		public FragmentDisplayer() {
			super();
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			FragmentManager fragmentManager1;

			// ��ƬFragment
			PhotoFragment photoFragment;

			String urlString;

			if (intent.getAction().equals(Constants.ACTION_VIEW_IMAGE)) { // �鿴����ͼ

				fragmentManager1 = getSupportFragmentManager();

				photoFragment = (PhotoFragment) fragmentManager1.findFragmentByTag(Constants.PHOTO_FRAGMENT_TAG);

				// ͼƬurl
				urlString = intent.getDataString();

				if (null != photoFragment) {

					// �����ǰ���Ӻ�PhotoFragment�����Ӳ�һ��ʱ����ͼƬ
					if (!urlString.equals(photoFragment.getURLString())) {

						// ����ͼƬ
						photoFragment.setPhoto(urlString);

						// ���ؽ���ͼƬ
						photoFragment.loadPhoto();
					}

				} else {

					photoFragment = new PhotoFragment();

					photoFragment.setPhoto(urlString);

					FragmentTransaction localFragmentTransaction2 = fragmentManager1.beginTransaction();

					if (mSideBySide) {
						// ���
						localFragmentTransaction2.add(R.id.fragmentHost, photoFragment, Constants.PHOTO_FRAGMENT_TAG);
					} else {
						// �滻
						localFragmentTransaction2.replace(R.id.fragmentHost, photoFragment, Constants.PHOTO_FRAGMENT_TAG);
					}

					localFragmentTransaction2.addToBackStack(null);

					localFragmentTransaction2.commit();
				}

				if (!mSideBySide) {
					setFullScreen(true);
				}

			} else if (intent.getAction().equals(Constants.ACTION_ZOOM_IMAGE)) { // �鿴ԭͼ

				if (mSideBySide) {

					FragmentManager localFragmentManager2 = getSupportFragmentManager();

					PhotoThumbnailFragment localThumbnailFragment = (PhotoThumbnailFragment) localFragmentManager2.findFragmentByTag(Constants.THUMBNAIL_FRAGMENT_TAG);

					if (null != localThumbnailFragment) {

						if (localThumbnailFragment.isVisible()) {

							FragmentTransaction localFragmentTransaction2 = localFragmentManager2.beginTransaction();

							localFragmentTransaction2.hide(localThumbnailFragment);
							localFragmentTransaction2.addToBackStack(null);
							localFragmentTransaction2.commit();

						} else {

							localFragmentManager2.popBackStack();
						}
					}

					setFullScreen(true);
				}
			}
		}
	}

}
