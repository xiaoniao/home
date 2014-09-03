package com.example.android.threadsample;

import java.util.Locale;

/** ���� **/
public final class Constants {

	public static final boolean LOGV = false; // verbose false
	public static final boolean LOGD = true;  // debug true

	// �Զ���Action
	public static final String ACTION_VIEW_IMAGE = "com.example.android.threadsample.ACTION_VIEW_IMAGE"; // �鿴����ͼ
	public static final String ACTION_ZOOM_IMAGE = "com.example.android.threadsample.ACTION_ZOOM_IMAGE"; // �鿴ԭͼ

	// �Զ���Intent Action
	public static final String BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST";

	// ��ͼFragment Tag
	public static final String PHOTO_FRAGMENT_TAG = "com.example.android.threadsample.PHOTO_FRAGMENT_TAG";
	// СͼFragment Tag
	public static final String THUMBNAIL_FRAGMENT_TAG = "com.example.android.threadsample.THUMBNAIL_FRAGMENT_TAG";

	// Intent�������ݼ�
	public static final String EXTENDED_DATA_STATUS = "com.example.android.threadsample.STATUS";
	// Intent��������log��
	public static final String EXTENDED_STATUS_LOG = "com.example.android.threadsample.LOG";
	// ����ȫ��״̬��
	public static final String EXTENDED_FULLSCREEN = "com.example.android.threadsample.EXTENDED_FULLSCREEN";

	// Http user-agent
	public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android " + android.os.Build.VERSION.RELEASE + ";" + Locale.getDefault().toString() + "; " + android.os.Build.DEVICE + "/" + android.os.Build.ID + ")";

	// �㲥֪ͨActivity״̬��
	public static final int STATE_ACTION_STARTED = 0;    // ��ʼ����
	public static final int STATE_ACTION_CONNECTING = 1; // �̺߳�RSS������
	public static final int STATE_ACTION_PARSING = 2;    // �߳����ڽ���RSS
	public static final int STATE_ACTION_WRITING = 3;    // �߳������������ṩ����д����
	public static final int STATE_ACTION_COMPLETE = 4;   // �߳̽���
	public static final int STATE_LOG = -1;              // ��̨�߳̽���log

	// ֪ͨ��Ϣ
	public static final CharSequence BLANK = " ";
}
