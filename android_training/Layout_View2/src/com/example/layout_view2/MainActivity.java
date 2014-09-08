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

	// addView �ķ�ʽ�� ������ include merge ����ʲô����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RelativeLayout relativeLayout = (RelativeLayout) this.findViewById(R.id.main_layout);

		LayoutInflater inflater = LayoutInflater.from(this);

		// merge
		// <merge /> can be used only with a valid ViewGroup root and attachToRoot=true

		// �Լ��ӵĲ����쳣
		try {
			// ֱ�Ӹ��ӵ��������еĻ�ʹ�õ�  Ҳ��ʹ�õ������attrs
			View btnViewMerge = inflater.inflate(R.layout.button, relativeLayout, true);
		} catch (InflateException e) {
			
		}

		// ����ת��Ϊ��ť��
		View btnView = inflater.inflate(R.layout.button2, null);

		relativeLayout.addView(btnView);
		
		
		// ����
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
