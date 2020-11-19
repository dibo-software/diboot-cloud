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
package com.diboot.cloud.redis;

/**
 * Redis常量定义
 * @author JerryMa
 * @version v1.0
 * @date 2020/11/19
 */
public class RedisCons {

    public static final String PREFIX_ROLE = "ROLE_";

    public static final String KEY_CLAIM_NAME = "authorities";

    /**
     * key：资源角色的映射Map
     */
    public static final String KEY_RESOURCE_ROLES_MAP = "DIBOOT:AUTH:RESOURCE_ROLES_MAP";

    /**
     * 应用模块权限key前缀
     */
    public static final String KEY_APP_MODULE_PERMISSIONS_MAP = "DIBOOT:APP_MODULE_PERMISSIONS_MAP";

}
