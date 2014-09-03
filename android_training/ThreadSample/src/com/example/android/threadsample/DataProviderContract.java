package com.example.android.threadsample;

import android.net.Uri;
import android.provider.BaseColumns;

// �����˷���ContentProvider��һЩ����
public final class DataProviderContract implements BaseColumns {

	private DataProviderContract() {

	}

	// BaseColumns _ID (_id) _COUNT(_count)

	// URI scheme
	public static final String SCHEME = "content";

	// authority
	public static final String AUTHORITY = "com.example.android.threadsample";

	// DataProvider URI
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);

	// MIME multiple rows
	public static final String MIME_TYPE_ROWS = "vnd.android.cursor.dir/vnd.com.example.android.threadsample";

	// MIME single row
	public static final String MIME_TYPE_SINGLE_ROW = "vnd.android.cursor.item/vnd.com.example.android.threadsample";

	// primary key
	public static final String ROW_ID = BaseColumns._ID;

	// table name
	public static final String PICTUREURL_TABLE_NAME = "PictureUrlData";

	// table content URI
	public static final Uri PICTUREURL_TABLE_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, PICTUREURL_TABLE_NAME);

	// table thumbnail URL column name ����ͼurl
	public static final String IMAGE_THUMBURL_COLUMN = "ThumbUrl";

	// table thumbnail filename column name ����ͼ����
	public static final String IMAGE_THUMBNAME_COLUMN = "ThumbUrlName";

	// full picture URL column name ԭͼUrl
	public static final String IMAGE_URL_COLUMN = "ImageUrl";

	// full picture filename column name ԭͼ����
	public static final String IMAGE_PICTURENAME_COLUMN = "ImageName";

	// Modification date table name
	public static final String DATE_TABLE_NAME = "DateMetadatData";

	// Content URI for modification date table
	public static final Uri DATE_TABLE_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, DATE_TABLE_NAME); // ����baseUri ����һ��new Uri

	// Modification date table date column name
	public static final String DATA_DATE_COLUMN = "DownloadDate"; // �޸�ʱ��

	// The content provider database name ���ݿ�����
	public static final String DATABASE_NAME = "PictureDataDB";

	// The starting version of the database ���ݿ�汾��
	public static final int DATABASE_VERSION = 1;
}
