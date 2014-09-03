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

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoFragment extends Fragment implements View.OnClickListener {
	// Constants
	private static final String PHOTO_URL_KEY = "com.example.android.threadsample.PHOTO_URL_KEY";

	PhotoView mPhotoView;

	String mURLString;

	ShareCompat.IntentBuilder mShareCompatIntentBuilder;

	// ����ͼƬ
	public void loadPhoto() {
		// �������setPhoto()������������ͼƬurl����仰�Ż�ִ��
		if (mURLString != null) {
			try {
				// ת��ΪURL
				URL localURL = new URL(mURLString);
				// ���ز�����URL
				mPhotoView.setImageURL(localURL, false, null);
			} catch (MalformedURLException localMalformedURLException) { // ����ת��URL�쳣
				localMalformedURLException.printStackTrace();
			}
		}
	}

	// ���ͼƬurl
	public String getURLString() {
		return mURLString;
	}

	// ����¼�
	@Override
	public void onClick(View view) {
		// ����ԭͼ�㲥
		Intent localIntent = new Intent(Constants.ACTION_ZOOM_IMAGE);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(localIntent);
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
		super.onCreateView(inflater, viewGroup, bundle);

		View localView = inflater.inflate(R.layout.photo, viewGroup, false);
		mPhotoView = ((PhotoView) localView.findViewById(R.id.photoView));
		mPhotoView.setOnClickListener(this);

		// �쳣���ٻָ�ʱ�ٶ�ȡ�Ƿ��ϴ��б���ͼƬurl ????
		if (bundle != null) {
			mURLString = bundle.getString(PHOTO_URL_KEY);
		}

		if (mURLString != null) {
			loadPhoto();
		}
		return localView;
	}

	@Override
	public void onDestroyView() {
		// �ÿ�mPhotoView�͵���¼�
		if (mPhotoView != null) {
			mPhotoView.setOnClickListener(null);
			this.mPhotoView = null;
		}
		super.onDestroyView();
	}

	// Fragment�����������һ��
	@Override
	public void onDetach() {
		// �ÿ�ͼƬurl
		mURLString = null;
		super.onDetach();
	}

	// �쳣ʱ����������
	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putString(PHOTO_URL_KEY, mURLString);
	}

	// ����ͼƬurl
	public void setPhoto(String urlString) {
		mURLString = urlString;
	}
}
