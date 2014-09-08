package com.example.layout_view;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;

public class MainActivity extends ActionBarActivity {

	// LiearyLayout - > FrameLayout - > ����

	private View msgView;
	private boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** ǰ�� ֪������״����ʱʹ�� hierarchy�鿴��д�� **/
		// View view = this.findViewById(R.id.activity_main_rl);
		// ViewParent frameLayout = view.getParent();
		// ViewParent lineayLayout = frameLayout.getParent();
		// ViewGroup linearLayoutVp = (ViewGroup) lineayLayout;
		// ImageView imageView = (ImageView) linearLayoutVp.findViewById(R.id.home);
		// imageView.setBackgroundColor(Color.GREEN);

		// ʹ��merge��ǩ��������ǵ� ��ǰ��ǩ�͸���ǩһ��ʱ�Ϳ���ʹ��
		// ע���Ŀ¼�ĸ���ǩʱFrameLayout

		// ע����merge����������ֵ�ǲ����õ�

		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				System.out.print("111");
				if (flag) {
					showMsgView();
				} else {
					closeMsgView();
				}
				flag = !flag;
			}
		});

	}

	private void showMsgView() {
		if (msgView != null) {
			msgView.setVisibility(View.VISIBLE);
			return;
		}
		ViewStub stub = (ViewStub) findViewById(R.id.msg_layout);
		msgView = stub.inflate();
	}

	private void closeMsgView() {
		if (msgView != null) {
			msgView.setVisibility(View.GONE);
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
