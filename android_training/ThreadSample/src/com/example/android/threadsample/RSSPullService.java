package com.example.android.threadsample;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Vector;

/**
 * ͨ��Intent��ȡurl����RSS���� ,֮��ͨ��LocalBroadcastManager��������״̬
 * 
 * ���е���������Խ�������״̬��ֻҪ�����˹㲥������categoryΪCATEGORY_DEFAULT��actionΪConstants.BROADCAST_ACTION.
 * 
 */
public class RSSPullService extends IntentService {

	// log
	public static final String LOG_TAG = "RSSPullService";

	// ʵ����BroadcastNotifier��
	private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

	// ���췽��
	public RSSPullService() {
		super("RSSPullService"); // RSSPullServiceΪ��̨�̵߳�����
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {

		// ���URL
		String localUrlString = workIntent.getDataString();

		// ����һ��projection����ContentProvider��ѯ (��ȡid�����޸�ʱ��)
		final String[] dateProjection = new String[] { DataProviderContract.ROW_ID, DataProviderContract.DATA_DATE_COLUMN };

		// URL
		URL localURL;

		// �α�
		Cursor cursor = null;

		try {

			// ת��ΪURL���� -->ps:����Ჶ��ת���쳣
			localURL = new URL(localUrlString);

			/*
			 * ���ͷų�IOException�쳣
			 */
			URLConnection localURLConnection = localURL.openConnection(); // �������ͺ�URL�й� ���� http: ftp:

			// ���������http����
			if ((localURLConnection instanceof HttpURLConnection)) {

				// ������Ϣ �� ��ʼ������
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_STARTED);

				// ת��ΪHttpURLConnection
				HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURLConnection;

				// ����user agent
				localHttpURLConnection.setRequestProperty("User-Agent", Constants.USER_AGENT);

				// ��ѯ����cursor
				cursor = getContentResolver().query(DataProviderContract.DATE_TABLE_CONTENTURI, dateProjection, null, null, null);

				// �Ƿ���Ҫ��������
				boolean newMetadataRetrieved;

				// ѭ��cursor ��ȡ����
				if (null != cursor && cursor.moveToFirst()) {

					// ��ȡ�޸�ʱ��
					long storedModifiedDate = cursor.getLong(cursor.getColumnIndex(DataProviderContract.DATA_DATE_COLUMN));

					if (0 != storedModifiedDate) {
						// ���� If-Modified-Since ���޸�ʱ��Ҳ���͸������� �ͻ�����ʹ�� (http://www.cnblogs.com/zh2000g/archive/2010/03/22/1692002.html)
						localHttpURLConnection.setRequestProperty("If-Modified-Since", org.apache.http.impl.cookie.DateUtils.formatDate(new Date(storedModifiedDate), org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123));
					}

					// ����Ҫ��������
					newMetadataRetrieved = false;
				} else {
					// ��Ҫ��������
					newMetadataRetrieved = true;

				}

				// ���ͱ�����������
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_CONNECTING);

				// ���״̬��
				int responseCode = localHttpURLConnection.getResponseCode(); // ?������ͻ��״̬�룬ʲôʱ��������������

				switch (responseCode) {

				case HttpStatus.SC_OK: // 200 -->���յ�����

					// ���URL���µ��޸�ʱ�� (Last-Modified)
					long lastModifiedDate = localHttpURLConnection.getLastModified();

					// ���ͱ������ڽ���
					mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_PARSING);

					// ʵ����RSSPullParser,RSS����
					RSSPullParser localPicasaPullParser = new RSSPullParser();

					// ��ʼ����xml����
					localPicasaPullParser.parseXml(localURLConnection.getInputStream(), mBroadcaster);

					// ���ͱ��棬������content provider��д����
					mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_WRITING);

					// ��RSSPullParser�л�����е�ͼƬ
					Vector<ContentValues> imageValues = localPicasaPullParser.getImages();

					// ͼƬ���ϴ�С
					int imageVectorSize = imageValues.size();

					// ContentValues ��ֵ������
					ContentValues[] imageValuesArray = new ContentValues[imageVectorSize];

					// ����ת����
					imageValuesArray = imageValues.toArray(imageValuesArray);

					System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
					// �������
					for (int i = 0; i < imageValuesArray.length; i++) {
						System.out.println("i = " + i + "  " + imageValuesArray[i].toString());

					}

					// ��ͼƬ��Ϣ���浽ContentProovider��
					getContentResolver().bulkInsert(DataProviderContract.PICTUREURL_TABLE_CONTENTURI, imageValuesArray);

					// ��ʵ������һ��ContentValues������������
					ContentValues dateValues = new ContentValues();

					// ���URL�޸�ʱ�� -->dateValues
					dateValues.put(DataProviderContract.DATA_DATE_COLUMN, lastModifiedDate);

					if (newMetadataRetrieved) { // ���½��յ�����

						// ��������
						getContentResolver().insert(DataProviderContract.DATE_TABLE_CONTENTURI, dateValues);

					} else { // �����½��յ�����

						// ��������
						getContentResolver().update(DataProviderContract.DATE_TABLE_CONTENTURI, dateValues, DataProviderContract.ROW_ID + "=" + cursor.getString(cursor.getColumnIndex(DataProviderContract.ROW_ID)), null);
					}
					break;

				}

				// ���ͱ��� �߳����
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
			}

		} catch (MalformedURLException localMalformedURLException) { // ����ת��URL�쳣

			localMalformedURLException.printStackTrace();

		} catch (IOException localIOException) {

			localIOException.printStackTrace();

		} catch (XmlPullParserException localXmlPullParserException) {

			localXmlPullParserException.printStackTrace();

		} finally {
			// ��������쳣���͹ر��α�cursor,��ֹ�ڴ�й©
			if (null != cursor) {
				cursor.close();
			}
		}
	}

}
