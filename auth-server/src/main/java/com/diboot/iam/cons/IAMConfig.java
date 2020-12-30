/*
 * Copyright (c) 2015-2021, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.iam.cons;

/**
 * IAM相关常量定义
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
public class IAMConfig {
    public static final String CLIENT_ID = "client-id";

    public static final String CLIENT_SECRET = "client-sec";

    /**
     * Java KeyStore 密码，重新生成jks后修改此处
     */
    public static final String JKS_PASSWORD = "diboot";

    /**
     * JWT token前缀
     */
    public static final String URI_RSA_PUBLIC_KEY = "/oauth/token_key";

    /**
     * 登录提示信息
     */
    public static final String LOGIN_MSG_ACCOUNT_ERROR = "用户名或密码错误，请重试！";
    public static final String LOGIN_MSG_CREDENTIALS_EXPIRED = "您的登录凭证已过期，请重新登录！";
    public static final String LOGIN_MSG_ACCOUNT_DISABLED = "您的账号已被禁用，请联系管理员！";
    public static final String LOGIN_MSG_ACCOUNT_LOCKED = "您的账号已被锁定，请联系管理员！";
    public static final String LOGIN_MSG_ACCOUNT_EXPIRED = "您的账号已过期，请联系管理员！";

}
