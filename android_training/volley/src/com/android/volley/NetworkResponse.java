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
 * 网络请求返回数据:
 * 
 * performRequest(Request)返回的Data和headers
 */
public class NetworkResponse {

	/** 构造Data和header和状态码和是否修改 **/
	public NetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
		this.statusCode = statusCode;
		this.data = data;
		this.headers = headers;
		this.notModified = notModified;
	}

	/** 构造Data **/
	public NetworkResponse(byte[] data) {
		this(HttpStatus.SC_OK, data, Collections.<String, String> emptyMap(), false);
	}

	/** 构造Data和head **/
	public NetworkResponse(byte[] data, Map<String, String> headers) {
		this(HttpStatus.SC_OK, data, headers, false);
	}

	/** HTTP状态码 */
	public final int statusCode;

	/** HTTP返回的数据 */
	public final byte[] data;

	/** HTTP返回的头部 */
	public final Map<String, String> headers;

	/** 是否需要修改 304 为 true 代表有缓存 */
	public final boolean notModified; //
}