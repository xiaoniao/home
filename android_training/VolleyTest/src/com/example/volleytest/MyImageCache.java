package com.example.volleytest;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class MyImageCache implements ImageCache {

    private LruCache<String, Bitmap> cache;

    /** * ���캯�������LruCache<String,Bitmap>����ĳ�ʼ�� * @param maxSize LruCache�����ռ� ��λΪByte */
    public MyImageCache(int maxSize) {
        super();
        cache = new LruCache<String, Bitmap>(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return cache.get(url);// �ڻ�������Ѱ���Ƿ���
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // ���ﴫ������bitmap�Ѿ���������ʱ�����maxWidth��maxHeight�ı���ߴ���
        // �����NetworkImageView���������Ĭ��ԭ�ߴ硣����һ��һֱû�����ף�
        // Ϊ��bitmap�ĳߴ��С��û��Ӱ�쵽put��ȥ�Ļ����ļ���С�أ�����
        // ����Ū���׵ĸ������һ�£���л��(^o^)/
        cache.put(url, bitmap);// ��ͼƬ�������
    }
}
