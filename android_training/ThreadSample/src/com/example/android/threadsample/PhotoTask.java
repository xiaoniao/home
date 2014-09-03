/*
 * Copyright (C) 2012 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.android.threadsample;

import java.lang.ref.WeakReference;
import java.net.URL;

import android.graphics.Bitmap;

import com.example.android.threadsample.PhotoDecodeRunnable.TaskRunnableDecodeMethods;
import com.example.android.threadsample.PhotoDownloadRunnable.TaskRunnableDownloadMethods;

/**
 * ����࣬������PhotoDownloadRunnable �� PhotoDownloadRunnable ������ , ������������ͼƬ�ͽ���ͼƬ�߳� �෴�ģ�����������Щ��������������.
 * 
 * ��ͨ��ʵ��TaskRunnableDownloadMethods �� TaskRunnableDecodeMethods�ӿ� ������,���Ұ��Լ��������غͽ�����Ĳ���
 * 
 * ʵ���ϣ�������ί���������߳� ������ ʲôѽ ������
 * 
 * �������Ի㼯����������
 */
public class PhotoTask implements TaskRunnableDownloadMethods, TaskRunnableDecodeMethods {

    // ����һ��PhotoView������
    private WeakReference<PhotoView> mImageWeakRef; // ����������ͼƬ�ؼ�

    private URL                 mImageURL;          // ͼƬURL·��
    private int                 mTargetHeight;      // ����ͼƬʱ�ĸ�
    private int                 mTargetWidth;       // ����ͼƬʱ�Ŀ�
    private boolean             mCacheEnabled;      // �Ƿ���������
    byte[]                      mImageBuffer;       // һ�����壬����ͼƬ���ֽ�
    private Bitmap              mDecodedImage;      // ������Bitmap
    
    Thread                      mThreadThis;        // Ҫ���е��߳� �������ʲô�ط���ֵ�ģ� ʹ�õ�����PhotoManager�е�219��
    private Thread              mCurrentThread;     // �������е��߳�
   
    private Runnable            mDownloadRunnable;  // һ�������߳�
    private Runnable            mDecodeRunnable;    // һ�������߳�
    
    // һ�������̳߳ض���PhotoManagerʵ��
    private static PhotoManager sPhotoManager;

    /** ���췽�� ʵ�������ء������̺߳��̳߳ع�������  **/
    PhotoTask() {
        mDownloadRunnable =  new PhotoDownloadRunnable(this);     // ʵ���������߳�
        mDecodeRunnable   =  new PhotoDecodeRunnable(this);       // ʵ���������߳�
        sPhotoManager     =  PhotoManager.getInstance();          // ���PhotoManagerʵ��
    }

    /** ��ʼ������ **/
    void initializeDownloaderTask(PhotoManager photoManager, PhotoView photoView, boolean cacheFlag) {
        sPhotoManager =  photoManager;                             // ��ֵ�̳߳ض���
        mImageURL     =  photoView.getLocation();                  // ��ø�ֵͼƬURL
        mImageWeakRef =  new WeakReference<PhotoView>(photoView);  // ����������ͼƬ�ؼ������ö���
        mCacheEnabled =  cacheFlag;                                // �Ƿ���������
        mTargetWidth  =  photoView.getWidth();                     // ���ͼƬ��
        mTargetHeight =  photoView.getHeight();                    // ���ͼƬ��
    }

    // ʵ���� �����߳̽ӿ�
    @Override
    public byte[] getByteBuffer() {                               // �����ӿ��ж���������� ��ô����
        return mImageBuffer;
    }

    /** ������Դ **/
    void recycle() {
        if (null != mImageWeakRef) {
            mImageWeakRef.clear();  // ��ղ��ÿ������ö���
            mImageWeakRef = null;
        }
        mImageBuffer = null;        // �ÿ�byteBuffer
        mDecodedImage = null;       // �ÿ�Bitmap
    }

    // ʵ���� �����߳̽ӿ�
    @Override
    public int getTargetWidth() {
        return mTargetWidth;
    }

    // ʵ���� �����߳̽ӿ�
    @Override
    public int getTargetHeight() {
        return mTargetHeight;
    }

    // �Ƿ���������
    boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    // ʵ���� �����߳̽ӿ�
    @Override
    public URL getImageURL() {
        return mImageURL;
    }

    // ʵ���� �����߳̽ӿ�
    @Override
    public void setByteBuffer(byte[] imageBuffer) {
        mImageBuffer = imageBuffer;
    }

    // ��PhotoManager���͵�ǰ�����״̬
    void handleState(int state) {
        sPhotoManager.handleState(this, state);
    }

    // ����Bitmap
    Bitmap getImage() {
        return mDecodedImage;
    }

    // ��������Runnable
    Runnable getHTTPDownloadRunnable() {
        return mDownloadRunnable;
    }

    // ���ؽ���Runnable
    Runnable getPhotoDecodeRunnable() {
        return mDecodeRunnable;
    }

    // ���� PhototView
    public PhotoView getPhotoView() {
        if (null != mImageWeakRef) {
            return mImageWeakRef.get();
        }
        return null;
    }

    /*
     * ���ص�ǰ�������е��߳� mCurrentThread
     * 
     * The lock is needed because the Thread object reference is stored in the Thread object itself, and that object can
     * be changed by processes outside of this app.
     */
    public Thread getCurrentThread() {
        synchronized (sPhotoManager) {
            return mCurrentThread;
        }
    }

    /*
     * ����һ����ǰ�߳� mCurrentThread lockԭ�����
     */
    public void setCurrentThread(Thread thread) {
        synchronized (sPhotoManager) {
            mCurrentThread = thread;
        }
    }

    // ʵ���� �����߳�
    @Override
    public void setImage(Bitmap decodedImage) {
        mDecodedImage = decodedImage;
    }

    // ʵ���� �����߳�
    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    // ʵ���� �����߳� PhotoDownloadRunnable. Passes the download state to the ThreadPool object.
    @Override
    public void handleDownloadState(int state) {
        int outState;
        switch (state) {
            case PhotoDownloadRunnable.HTTP_STATE_COMPLETED: // ���

                outState = PhotoManager.DOWNLOAD_COMPLETE;

                break;
            case PhotoDownloadRunnable.HTTP_STATE_FAILED:    // ʧ��

                outState = PhotoManager.DOWNLOAD_FAILED;

                break;
            default:
                outState = PhotoManager.DOWNLOAD_STARTED;    // ��ʼ

                break;
        }
        // ���������һ������
        handleState(outState);
    }

    // ʵ���� �����߳�
    @Override
    public void setImageDecodeThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    // ʵ���� �����߳�
    @Override
    public void handleDecodeState(int state) {
        
        int outState;
        
        switch (state) {
            
            case PhotoDecodeRunnable.DECODE_STATE_COMPLETED: // �������

                outState = PhotoManager.TASK_COMPLETE;

                break;
            case PhotoDecodeRunnable.DECODE_STATE_FAILED:    // ����ʧ��

                outState = PhotoManager.DOWNLOAD_FAILED;

                break;
            default:

                outState = PhotoManager.DECODE_STARTED;      // ������ʼ

                break;
        }

        // �������淽��
        handleState(outState);
    }
}