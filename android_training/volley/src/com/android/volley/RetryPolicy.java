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
 * 重新请求数据
 */
public interface RetryPolicy {

	/**
	 * 返回当前超时时间 （用于log）
	 */
	public int getCurrentTimeout();

	/**
	 * 返回当前尝试重新请求数据次数 （用于log）
	 */
	public int getCurrentRetryCount();

	/**
	 * 捕获重新请求数据超时
	 */
	public void retry(VolleyError error) throws VolleyError;
}
