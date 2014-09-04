package com.example.volleytraing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends ActionBarActivity {

	RequestQueue requestQueue;

	TextView baiduTv;
	TextView fgltTv;

	Button customBtn;
	Button standardBtn;

	String cancleTAG = "fglt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		baiduTv = (TextView) this.findViewById(R.id.textView2);
		fgltTv = (TextView) this.findViewById(R.id.textView4);

		customBtn = (Button) this.findViewById(R.id.custombtn);
		customBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, RequestQueueActivity.class));
			}
		});
		standardBtn = (Button) this.findViewById(R.id.standardbtn);
		standardBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, StandardRequestActivty.class));
			}
		});

		// 简单的请求

		// 创建一个请求队列
		requestQueue = Volley.newRequestQueue(this);

		// 实例化Request
		String url = "http://baidu.com";
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
				baiduTv.setText(response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
				baiduTv.setText(error.toString());
			}
		});

		// 实例化Request
		String urlFg = "http://fglt.com";
		StringRequest stringRequestFg = new StringRequest(Request.Method.GET, urlFg, new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
				fgltTv.setText(response);

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Toast.makeText(MainActivity.this, "富贵 :" + error.toString(), Toast.LENGTH_SHORT).show();
				fgltTv.setText(error.toString());
			}
		});

		stringRequestFg.setTag(cancleTAG);

		// 添加请求 添加了两条请求 -->Volley触发执行一个缓存处理线程以及网络一系列的网络处理线程。
		requestQueue.add(stringRequest);
		requestQueue.add(stringRequestFg);

		// 开始请求
		requestQueue.start();

		// 把解析过后的数据返回到主线程,通过接口 .. 面向接口编程

		// 你可以在任何线程中添加一个请求，但是响应结果都是返回到主线程的。
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (requestQueue != null) {

			// 取消所有线程
			// requestQueue.cancelAll(this);

			// 取消指定tag的线程
			requestQueue.cancelAll(cancleTAG);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
