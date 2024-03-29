package com.example.volleytraing;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/** 单例模式的RequestQueue **/
public class MySingleton {

	private static MySingleton mInstance;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	private Context context;

	public MySingleton(Context context) {
		this.context = context;

		/** 生成RequestQueue **/
		requestQueue = getRequestQueue();

		/** 生成ImageLoader **/
		imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

			/** 结合LruCache 缓存Bitmap **/
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

	/** 单例模式获得实例 **/
	public static synchronized MySingleton getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new MySingleton(context);
		}
		return mInstance;
	}

	/** 获得RequestQueue **/
	public RequestQueue getRequestQueue() {
		if (requestQueue == null) {

			/** 实例化 RequestQueue **/
			requestQueue = Volley.newRequestQueue(context.getApplicationContext()); // 使用getApplicationContext防止内存泄漏
		}
		return requestQueue;
	}

	/** 添加Request **/
	public <T> void addToRequestQueue(Request<T> reg) {
		getRequestQueue().add(reg);
	}

	/** 获得ImageLoader **/
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
}
