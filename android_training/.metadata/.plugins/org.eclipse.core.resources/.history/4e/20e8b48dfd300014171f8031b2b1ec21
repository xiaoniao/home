package com.example.android.threadsample;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 发送广播
 */
public class BroadcastNotifier {

	// 用来在同一个应用内的不同组件间发送Broadcast
	private LocalBroadcastManager mBroadcaster;

	// 构造方法
	public BroadcastNotifier(Context context) {
		// 获得LocalBroadcastManager实例
		mBroadcaster = LocalBroadcastManager.getInstance(context);
	}

	// 为了个给不同的Activity发送刚播，设置一个Constants 里面保存Action
	
	// 发送广播 status
	public void broadcastIntentWithState(int status) {
		
		Intent localIntent = new Intent();
		localIntent.setAction(Constants.BROADCAST_ACTION); // 设置Action
		localIntent.addCategory(Intent.CATEGORY_DEFAULT); // 设置Category
		localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, status); // 添加数据，设置状态
		
		mBroadcaster.sendBroadcast(localIntent);// 发送广播
	}

	// 发送广播 logData
	public void notifyProgress(String logData) {
		
		Intent localIntent = new Intent();
		localIntent.setAction(Constants.BROADCAST_ACTION); // 设置Action
		localIntent.addCategory(Intent.CATEGORY_DEFAULT); // 设置Category
		localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, -1); // 添加额外数据 -1
		localIntent.putExtra(Constants.EXTENDED_STATUS_LOG, logData); // 添加log数据

		mBroadcaster.sendBroadcast(localIntent); // 发送广播
	}
}
