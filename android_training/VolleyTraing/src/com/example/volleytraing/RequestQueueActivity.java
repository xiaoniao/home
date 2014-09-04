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

		// һ��RequestQueue��Ҫ��������֧�����Ĺ�����
		// һ�����������������ִ����������ݴ���,����һ���������������������Cache

		// ��Volley�Ĺ������а����˱�׼��ʵ�ַ�ʽ��
		// DiskBasedCache�ṩ��ÿ���ļ����Ӧ��Ӧ����һһӳ��Ļ���ʵ�֡�
		// BasicNetwork�ṩ��һ�����紫���ʵ�֣����ӷ�ʽ������AndroidHttpClient ������ HttpURLConnection.

		// BasicNetwork��VolleyĬ�ϵ��������ʵ�ַ�ʽ��һ��BasicNetwork����ʹ��HTTP Client���г�ʼ����
		// ���Clientͨ����AndroidHttpClient ���� HttpURLConnection:

		// ��ʾ�ĵ���RequestQueue (��ʵ����ʵ�ֵĸ�Volley.newRequestQueue(this) һģһ��, ֻ������ʾ�Ľ��и���һЩĬ��ֵ)

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

		/** ����ģʽ **/

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