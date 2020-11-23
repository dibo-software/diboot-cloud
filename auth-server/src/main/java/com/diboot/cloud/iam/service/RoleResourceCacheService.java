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
package com.diboot.cloud.iam.service;

/**
 * 角色资源缓存Service
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/23
 */
public interface RoleResourceCacheService {

    /**
     * 刷新资源角色的缓存Map
     */
    boolean refreshResourceRolesCache();

    /**
     * 刷新用户角色缓存
     * @param userType
     * @param userId
     * @return
     */
    boolean refreshUserRolesCache(String userType, Long userId);

    /**
     * 刷新用户认证缓存等待区
     * @param userType
     * @param userId
     */
    void addIntoPendingRefresh(String userType, Long userId);
}
