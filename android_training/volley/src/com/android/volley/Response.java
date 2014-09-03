/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley;

/**
 * ��Ӧ (��װ�������͵���Ӧ)
 * 
 * <T> ��Ӧ������
 */
public class Response<T> {

	/** ���ͺͽ�����Ӧ�Ļص� **/
	public interface Listener<T> {
		/** ����Ӧ���յ�ʱ���� **/
		public void onResponse(T response);
	}

	/** ������Ӧ�Ļص� **/
	public interface ErrorListener {
		/** ����Ӧʧ��ʱ���� **/
		public void onErrorResponse(VolleyError error);
	}

	/** Returns a successful response containing the parsed result. */
	public static <T> Response<T> success(T result, Cache.Entry cacheEntry) { // ���سɹ���Ϣ�������������
		return new Response<T>(result, cacheEntry);
	}

	/**
	 * Returns a failed response containing the given error code and
	 * 
	 * an optional localized message displayed to the user.
	 */
	public static <T> Response<T> error(VolleyError error) { // ���ش�����Ϣ
		return new Response<T>(error);
	}

	/** Parsed response, or null in the case of error. */
	public final T result; // ��������Ӧ��� ����Ļ�Ϊnull

	/** Cache metadata for this response, or null in the case of error. */
	public final Cache.Entry cacheEntry; // �����Ԫ����, ����Ļ�Ϊnull

	/** Detailed error information if <code>errorCode != OK</code>. */
	public final VolleyError error; // ��ϸ�Ĵ�����Ϣ����errorCode != OK ��ʱ��

	/** True if this response was a soft-expired one and a second one MAY be coming. */
	public boolean intermediate = false;

	/**
	 * ���ظ���Ӧ�Ƿ�ɹ�
	 */
	public boolean isSuccess() {
		return error == null;
	}

	/** ���췽�� **/
	private Response(T result, Cache.Entry cacheEntry) { // ������� �� ����metaData
		this.result = result;
		this.cacheEntry = cacheEntry;
		this.error = null;
	}

	/** ���췽�� **/
	private Response(VolleyError error) { // ����
		this.result = null;
		this.cacheEntry = null;
		this.error = error;
	}
}