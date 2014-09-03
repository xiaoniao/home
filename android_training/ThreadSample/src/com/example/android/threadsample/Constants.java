package com.example.android.threadsample;

import java.util.Locale;

/** 常量 **/
public final class Constants {

	public static final boolean LOGV = false; // verbose false
	public static final boolean LOGD = true;  // debug true

	// 自定义Action
	public static final String ACTION_VIEW_IMAGE = "com.example.android.threadsample.ACTION_VIEW_IMAGE"; // 查看缩略图
	public static final String ACTION_ZOOM_IMAGE = "com.example.android.threadsample.ACTION_ZOOM_IMAGE"; // 查看原图

	// 自定义Intent Action
	public static final String BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST";

	// 大图Fragment Tag
	public static final String PHOTO_FRAGMENT_TAG = "com.example.android.threadsample.PHOTO_FRAGMENT_TAG";
	// 小图Fragment Tag
	public static final String THUMBNAIL_FRAGMENT_TAG = "com.example.android.threadsample.THUMBNAIL_FRAGMENT_TAG";

	// Intent额外数据键
	public static final String EXTENDED_DATA_STATUS = "com.example.android.threadsample.STATUS";
	// Intent额外数据log键
	public static final String EXTENDED_STATUS_LOG = "com.example.android.threadsample.LOG";
	// 保存全屏状态键
	public static final String EXTENDED_FULLSCREEN = "com.example.android.threadsample.EXTENDED_FULLSCREEN";

	// Http user-agent
	public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android " + android.os.Build.VERSION.RELEASE + ";" + Locale.getDefault().toString() + "; " + android.os.Build.DEVICE + "/" + android.os.Build.ID + ")";

	// 广播通知Activity状态码
	public static final int STATE_ACTION_STARTED = 0;    // 开始下载
	public static final int STATE_ACTION_CONNECTING = 1; // 线程和RSS连接中
	public static final int STATE_ACTION_PARSING = 2;    // 线程正在解析RSS
	public static final int STATE_ACTION_WRITING = 3;    // 线程正在往内容提供者中写数据
	public static final int STATE_ACTION_COMPLETE = 4;   // 线程结束
	public static final int STATE_LOG = -1;              // 后台线程结束log

	// 通知信息
	public static final CharSequence BLANK = " ";
}
