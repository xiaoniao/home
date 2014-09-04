package com.example.volleytraing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/** ����ģʽ��RequestQueue **/
public class MySingleton {

	private static MySingleton mInstance;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	private Context context;

	public MySingleton(Context context) {
		this.context = context;

		/** ����RequestQueue **/
		requestQueue = getRequestQueue();

		/** ����ImageLoader **/
		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

			/** ���LruCache ����Bitmap **/
			private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				cache.put(url, bitmap);
			}

			@Override
			public Bitmap getBitmap(String url) {
				return cache.get(url);
			}
		});
	}

	/** ����ģʽ���ʵ�� **/
	public static synchronized MySingleton getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new MySingleton(context);
		}
		return mInstance;
	}

	/** ���RequestQueue **/
	public RequestQueue getRequestQueue() {
		if (requestQueue == null) {

			/** ʵ���� RequestQueue **/
			requestQueue = Volley.newRequestQueue(context.getApplicationContext()); // ʹ��getApplicationContext��ֹ�ڴ�й©
		}
		return requestQueue;
	}

	/** ����Request **/
	public <T> void addToRequestQueue(Request<T> reg) {
		getRequestQueue().add(reg);
	}

	/** ���ImageLoader **/
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
}