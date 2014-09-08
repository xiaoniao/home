package com.example.layout_view2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends ActionBarActivity {

	// addView 的方式和 布局中 include merge 都有什么区别

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.main_layout);

		LayoutInflater inflater = LayoutInflater.from(this);

		// merge
		// <merge /> can be used only with a valid ViewGroup root and attachToRoot=true

		// 自己加的捕获异常
		try {
			// 直接附加到父容器中的话使用的  也会使用到自身的attrs
			View btnViewMerge = inflater.inflate(R.layout.button, relativeLayout, true);
		} catch (InflateException e) {
			
		}

		// 不能转化为按钮阿
		View btnView = inflater.inflate(R.layout.button2, null);

		relativeLayout.addView(btnView);
		
		
		// 错误
		// View binkView = inflater.inflate(R.layout.bink_layout, null);
		//
		// relativeLayout.addView(binkView);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
