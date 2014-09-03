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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * �����̣߳�����߳̽���ByteArrayͼƬ
 * 
 * ����߳���PhotoTask������ʵ����,ֻҪ��ʵ����TaskRunnableDecodeMethods�ӿ�
 */
class PhotoDecodeRunnable implements Runnable {
    
	// ���Խ���ͼƬ����
	private static final int    NUMBER_OF_DECODE_TRIES  = 2;

	// ��ͣʱ��
	private static final long   SLEEP_TIME_MILLISECONDS = 250;

	// ״̬��
	static final int            DECODE_STATE_FAILED     = -1;  // ����ʧ��
	static final int            DECODE_STATE_STARTED    = 0;   // ������ʼ
	static final int            DECODE_STATE_COMPLETED  = 1;   // �������

	// �ӿ�ʵ��
	final TaskRunnableDecodeMethods mPhotoTask;

	// ���췽�� ͨ���ӿں�PhotoTask���� �൱�ڻ��PhotoTask������
    PhotoDecodeRunnable(TaskRunnableDecodeMethods downloadTask) {
        mPhotoTask = downloadTask;
    }
    
	// �Զ�������ӿ�
	interface TaskRunnableDecodeMethods {

		void   setImageDecodeThread(Thread currentThread);
		byte[] getByteBuffer(); // ���غͽ����ӿ��ж�������ӿڣ����ʵ�������ؽӿںͽ����ӿ�ͨ��PhotoTask���� �� ��ȻҲ���Բ���ͬһ����������
		void   handleDecodeState(int state);
		int    getTargetWidth();
		int    getTargetHeight();
		void   setImage(Bitmap image);
	}

	@Override
	public void run() {

	    // ���浱ǰ�̣߳��Ա�PhotoTask����ֹͣ�߳�
		mPhotoTask.setImageDecodeThread(Thread.currentThread());

		// ���CacheBuffer
		byte[] imageBuffer = mPhotoTask.getByteBuffer();

		Bitmap returnBitmap = null;

		// �������ص�ͼƬ
		try {
			
		    // ����״̬ ��ʼ����
			mPhotoTask.handleDecodeState(DECODE_STATE_STARTED);

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

			int targetWidth = mPhotoTask.getTargetWidth();
			int targetHeight = mPhotoTask.getTargetHeight();

			if (Thread.interrupted()) {    // ����߳��Ƿ�ֹͣ
				return;
			}

			bitmapOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);

			int hScale = bitmapOptions.outHeight / targetHeight;
			int wScale = bitmapOptions.outWidth / targetWidth;

			int sampleSize = Math.max(hScale, wScale);

			if (sampleSize > 1) {
				bitmapOptions.inSampleSize = sampleSize;
			}

			if (Thread.interrupted()) {   // ����߳��Ƿ�ֹͣ
				return;
			}

			bitmapOptions.inJustDecodeBounds = false;

			// ��ʼ����buffer�����OOM���׳��쳣�����ٴν���
			for (int i = 0; i < NUMBER_OF_DECODE_TRIES; i++) {
				try {
					
				    returnBitmap = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);
				
				} catch (Throwable e) {

					java.lang.System.gc();         // �����ڴ棬��ø����ڴ棬ע�⣬������ܲ�����

					if (Thread.interrupted()) {    // ����߳��Ƿ�ֹͣ
						return;
					}
					
					// �߳����� 250 ms
					try {
						Thread.sleep(SLEEP_TIME_MILLISECONDS);
					} catch (java.lang.InterruptedException interruptException) {
						return;
					}
				}
			}

		} finally {
		    
			if (null == returnBitmap) {               // ����ʧ��
				// ������Ϣ ����ʧ��
				mPhotoTask.handleDecodeState(DECODE_STATE_FAILED);
				
			} else {                                  // �����ɹ�
				mPhotoTask.setImage(returnBitmap);    // ������ʾͼƬ

				// ������Ϣ �����ɹ�
				mPhotoTask.handleDecodeState(DECODE_STATE_COMPLETED);
			}

			// ���õ�ǰ�߳�Ϊ�գ������ͷ��ڴ������
			mPhotoTask.setImageDecodeThread(null);

			// ֹͣ�߳�
			Thread.interrupted();
		}

	}
}