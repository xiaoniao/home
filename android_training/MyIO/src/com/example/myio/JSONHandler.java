package com.example.myio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.gson.JsonElement;

/**
 * 解析Json，抽象类
 */
public abstract class JSONHandler {

	protected static Context mContext;

	// 构造方法
	public JSONHandler(Context context) {
		mContext = context;
	}

	// 添加到ContentProvider中
	public abstract void makeContentProviderOperations(ArrayList<ContentProviderOperation> list);

	// 解析
	public abstract void process(JsonElement element);

	// 解析本地数据Raw
	public static String parseResource(Context context, int resource) throws IOException {
		InputStream is = context.getResources().openRawResource(resource);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}

		return writer.toString();
	}
}
