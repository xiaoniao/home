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

import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

/**
 * �������󷵻�����:
 * 
 * performRequest(Request)���ص�Data��headers
 */
public class NetworkResponse {

	/** ����Data��header��״̬����Ƿ��޸� **/
	public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
		this.statusCode = statusCode;
		this.data = data;
		this.headers = headers;
		this.notModified = notModified;
	}

	/** ����Data **/
	public NetworkResponse(byte[] data) {
		this(HttpStatus.SC_OK, data, Collections.<String, String> emptyMap(), false);
	}

	/** ����Data��head **/
	public NetworkResponse(byte[] data, Map<String, String> headers) {
		this(HttpStatus.SC_OK, data, headers, false);
	}

	/** HTTP״̬�� */
	public final int statusCode;

	/** HTTP���ص����� */
	public final byte[] data;

	/** HTTP���ص�ͷ�� */
	public final Map<String, String> headers;

	/** �Ƿ���Ҫ�޸� 304 Ϊ true �����л��� */
	public final boolean notModified; //
}