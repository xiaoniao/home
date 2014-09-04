package com.example.volleytraing;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

public class RequestQueueActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 一个RequestQueue需要两部分来支持它的工作：
		// 一部分是网络操作用来执行请求的数据传输,另外一个是用来处理缓存操作的Cache

		// 在Volley的工具箱中包含了标准的实现方式：
		// DiskBasedCache提供了每个文件与对应响应数据一一映射的缓存实现。
		// BasicNetwork提供了一个网络传输的实现，连接方式可以是AndroidHttpClient 或者是 HttpURLConnection.

		// BasicNetwork是Volley默认的网络操作实现方式。一个BasicNetwork必须使用HTTP Client进行初始化。
		// 这个Client通常是AndroidHttpClient 或者 HttpURLConnection:

		// 显示的调用RequestQueue (其实这里实现的跟Volley.newRequestQueue(this) 一模一样, 只不过显示的进行更改一些默认值)

		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB

		String userAgent = "volley/0";

		try {
			String packageName = this.getPackageName();
			PackageInfo info = this.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {

		}

		HttpStack stack = null;
		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}
		Network network = new BasicNetwork(new HurlStack());

		RequestQueue requestQueue = new RequestQueue(cache, network);

		/** 单例模式 **/

		String url = "http://baidu.com";
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Toast.makeText(RequestQueueActivity.this, response, Toast.LENGTH_SHORT).show();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(RequestQueueActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
			}
		});

		MySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
		RequestQueue singleRequestQueue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

	}
}
