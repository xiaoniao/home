package com.example.volleytraing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;

/**
 * 常用请求类型：
 * 
 * StringRequest。指定一个URL并在相应回调中接受一个原始的raw string数据。请参考前一课的示例。
 * 
 * ImageRequest。指定一个URL并在相应回调中接受一个image。
 * 
 * JsonObjectRequest与JsonArrayRequest (均为JsonRequest的子类)。指定一个URL并在相应回调中获取到一个JSON对象或者JSON数组。
 *
 */

public class StandardRequestActivty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.standard_request);

		// ImageRequest

		final ImageView imageView = (ImageView) this.findViewById(R.id.imageView1);

		String url = "http://i.imgur.com/7spzG.png";
		ImageRequest imageRequest = new ImageRequest(url, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				imageView.setImageBitmap(response);
			}
		}, 50, 50, Config.ARGB_8888, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});

		MySingleton.getInstance(this).addToRequestQueue(imageRequest);
		MySingleton.getInstance(this).getRequestQueue().start();

		// ImageLoader
		final ImageView imageView2 = (ImageView) this.findViewById(R.id.imageView2);
		ImageLoader imageLoader = MySingleton.getInstance(this).getImageLoader();
		imageLoader.get(url, ImageLoader.getImageListener(imageView2, R.drawable.ic_launcher, R.drawable.abc_ab_bottom_solid_light_holo));

		// ImageLoader + NetworkImageView
		NetworkImageView networkImageView = (NetworkImageView) this.findViewById(R.id.networkImageView);
		networkImageView.setImageUrl(url, imageLoader);

		//
		ImageLoader imageLoader2 = new ImageLoader(MySingleton.getInstance(this).getRequestQueue(), new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
	}
}
