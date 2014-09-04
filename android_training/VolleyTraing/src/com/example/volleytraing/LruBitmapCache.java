package com.example.volleytraing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/** �Զ���LruCache ��ʵ��ImageCache **/
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {

	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

	public LruBitmapCache(Context context) {
		this(getCacheSize(context));
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}

	// �����СΪ��Ļ��С��ռ�ֽڴ�С
	public static int getCacheSize(Context context) {
		final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;

		final int screenBytes = screenWidth * screenHeight * 4; // ÿ����4�ֽ�
		return screenBytes;
	}

}