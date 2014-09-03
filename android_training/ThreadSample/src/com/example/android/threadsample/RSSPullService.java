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
 * 通过Intent获取url下载RSS数据 ,之后通过LocalBroadcastManager发送它的状态
 * 
 * 所有的组件都可以接收它的状态，只要接收了广播，并且category为CATEGORY_DEFAULT，action为Constants.BROADCAST_ACTION.
 * 
 */
public class RSSPullService extends IntentService {

	// log
	public static final String LOG_TAG = "RSSPullService";

	// 实例化BroadcastNotifier类
	private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

	// 构造方法
	public RSSPullService() {
		super("RSSPullService"); // RSSPullService为后台线程的名字
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {

		// 获得URL
		String localUrlString = workIntent.getDataString();

		// 创建一个projection用于ContentProvider查询 (获取id，和修改时间)
		final String[] dateProjection = new String[] { DataProviderContract.ROW_ID, DataProviderContract.DATA_DATE_COLUMN };

		// URL
		URL localURL;

		// 游标
		Cursor cursor = null;

		try {

			// 转换为URL对象 -->ps:下面会捕获转换异常
			localURL = new URL(localUrlString);

			/*
			 * 会释放出IOException异常
			 */
			URLConnection localURLConnection = localURL.openConnection(); // 链接类型和URL有关 例如 http: ftp:

			// 如果链接是http连接
			if ((localURLConnection instanceof HttpURLConnection)) {

				// 发送消息 ， 开始下载啦
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_STARTED);

				// 转换为HttpURLConnection
				HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURLConnection;

				// 设置user agent
				localHttpURLConnection.setRequestProperty("User-Agent", Constants.USER_AGENT);

				// 查询返回cursor
				cursor = getContentResolver().query(DataProviderContract.DATE_TABLE_CONTENTURI, dateProjection, null, null, null);

				// 是否需要返回数据
				boolean newMetadataRetrieved;

				// 循环cursor 获取数据
				if (null != cursor && cursor.moveToFirst()) {

					// 获取修改时间
					long storedModifiedDate = cursor.getLong(cursor.getColumnIndex(DataProviderContract.DATA_DATE_COLUMN));

					if (0 != storedModifiedDate) {
						// 设置 If-Modified-Since 把修改时间也发送给服务器 和缓存结合使用 (http://www.cnblogs.com/zh2000g/archive/2010/03/22/1692002.html)
						localHttpURLConnection.setRequestProperty("If-Modified-Since", org.apache.http.impl.cookie.DateUtils.formatDate(new Date(storedModifiedDate), org.apache.http.impl.cookie.DateUtils.PATTERN_RFC1123));
					}

					// 不需要返回数据
					newMetadataRetrieved = false;
				} else {
					// 需要返回数据
					newMetadataRetrieved = true;

				}

				// 发送报告正在连接
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_CONNECTING);

				// 获得状态码
				int responseCode = localHttpURLConnection.getResponseCode(); // ?在这里就获得状态码，什么时候是真正的连接

				switch (responseCode) {

				case HttpStatus.SC_OK: // 200 -->接收到数据

					// 获得URL最新的修改时间 (Last-Modified)
					long lastModifiedDate = localHttpURLConnection.getLastModified();

					// 发送报告正在解析
					mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_PARSING);

					// 实例化RSSPullParser,RSS解析
					RSSPullParser localPicasaPullParser = new RSSPullParser();

					// 开始解析xml数据
					localPicasaPullParser.parseXml(localURLConnection.getInputStream(), mBroadcaster);

					// 发送报告，正在向content provider中写数据
					mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_WRITING);

					// 从RSSPullParser中获得所有的图片
					Vector<ContentValues> imageValues = localPicasaPullParser.getImages();

					// 图片集合大小
					int imageVectorSize = imageValues.size();

					// ContentValues 键值对数据
					ContentValues[] imageValuesArray = new ContentValues[imageVectorSize];

					// 集合转数组
					imageValuesArray = imageValues.toArray(imageValuesArray);

					System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
					// 测试输出
					for (int i = 0; i < imageValuesArray.length; i++) {
						System.out.println("i = " + i + "  " + imageValuesArray[i].toString());

					}

					// 把图片信息保存到ContentProovider中
					getContentResolver().bulkInsert(DataProviderContract.PICTUREURL_TABLE_CONTENTURI, imageValuesArray);

					// 又实例化了一个ContentValues用来存别的数据
					ContentValues dateValues = new ContentValues();

					// 添加URL修改时间 -->dateValues
					dateValues.put(DataProviderContract.DATA_DATE_COLUMN, lastModifiedDate);

					if (newMetadataRetrieved) { // 是新接收的数据

						// 插入数据
						getContentResolver().insert(DataProviderContract.DATE_TABLE_CONTENTURI, dateValues);

					} else { // 不是新接收的数据

						// 更新数据
						getContentResolver().update(DataProviderContract.DATE_TABLE_CONTENTURI, dateValues, DataProviderContract.ROW_ID + "=" + cursor.getString(cursor.getColumnIndex(DataProviderContract.ROW_ID)), null);
					}
					break;

				}

				// 发送报告 线程完毕
				mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
			}

		} catch (MalformedURLException localMalformedURLException) { // 捕获转换URL异常

			localMalformedURLException.printStackTrace();

		} catch (IOException localIOException) {

			localIOException.printStackTrace();

		} catch (XmlPullParserException localXmlPullParserException) {

			localXmlPullParserException.printStackTrace();

		} finally {
			// 如果出现异常，就关闭游标cursor,防止内存泄漏
			if (null != cursor) {
				cursor.close();
			}
		}
	}

}
