/*
 * Copyright (C) 2011 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * HttpStack接口
 */
public interface HttpStack {
    /**
     * 执行一个Http请求伴随着给定的参数
     * 
     * 如果 request.getPostBody()为空就是Get请求，不为空就是Post请求并且Content-Type通过request.getPostBodyContentType获得
     * 
     * additionalHeaders和Request.getHeaders一块发送
     */
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError;

    // 抛出一个IO异常一个认证异常

}
