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
package com.diboot.cloud.redis.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * Redis相关Service
 * @author JerryMa
 * @version v2.2
 * @date 2021/01/21
 */
public interface RedisService {
    /**
     * 获取redisTemplate
     * @return
     */
    RedisTemplate<String,Object> getRedisTemplate();

    /**
     * 添加应用模块
     * @param applicationName
     */
    void addAppModule(String applicationName);

    /**
     * 获取全部应用模块
     * @return
     */
    List<String> getAppModules();
}
