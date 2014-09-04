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

package com.android.volley;

import android.content.Intent;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

/**
 * 
 * 认证异常
 * 
 * Error indicating that there was an authentication failure when performing a Request.
 */
@SuppressWarnings("serial")
public class AuthFailureError extends VolleyError {

    /** An intent that can be used to resolve this exception. (Brings up the password dialog.) */
    private Intent mResolutionIntent; // 这个Intent用来解决异常 (调出密码对话框)

    /** 构造方法 **/
    public AuthFailureError() {}

    /** 构造方法 **/
    public AuthFailureError(Intent intent) {
        mResolutionIntent = intent;
    }

    /** 构造方法 **/
    public AuthFailureError(NetworkResponse response) {
        super(response);
    }

    /** 构造方法 **/
    public AuthFailureError(String message) {
        super(message);
    }

    /** 构造方法 **/
    public AuthFailureError(String message, Exception reason) {
        super(message, reason);
    }

    /** 获得解决异常Intent **/
    public Intent getResolutionIntent() {
        return mResolutionIntent;
    }

    @Override
    public String getMessage() {
        if (mResolutionIntent != null) {
            // 如果有解决异常的Intent，提示重新输入密码 (凭证)
            return "User needs to (re)enter credentials.";
        }
        return super.getMessage();
    }
}
