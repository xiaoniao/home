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

/** 请求处理 **/
public interface ResponseDelivery {

	// Request  请求
	// Response 响应

	/**
	 * 解析一个从网络或者缓存的请求，并发送它
	 */
	public void postResponse(Request<?> request, Response<?> response);

	/**
	 * 解析一个从网络或者缓存的请求，并发送它,并执行一个Runnable线程在发送这个请求之后
	 */
	public void postResponse(Request<?> request, Response<?> response, Runnable runnable);

	/**
	 * 发送给定这个请求的错误
	 */
	public void postError(Request<?> request, VolleyError error);
}
