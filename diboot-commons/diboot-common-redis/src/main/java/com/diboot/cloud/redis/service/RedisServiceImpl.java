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

import com.diboot.cloud.redis.config.RedisCons;
import com.diboot.core.util.JSON;
import com.diboot.core.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Redis相关Service实现
 * @author JerryMa
 * @version v2.2
 * @date 2021/01/21
 */
@Service
public class RedisServiceImpl implements RedisService{
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void addAppModule(String applicationName) {
        List moduleList = redisTemplate.opsForList().range(RedisCons.KEY_APP_MODULES, 0,-1);
        if(moduleList != null){
            // 已添加过
            if(moduleList.contains(applicationName)){
                return;
            }
            else{
                redisTemplate.opsForList().rightPush(RedisCons.KEY_APP_MODULES, applicationName);
            }
        }
        else{
            redisTemplate.opsForList().rightPush(RedisCons.KEY_APP_MODULES, applicationName);
        }
    }

    @Override
    public List<String> getAppModules() {
        List moduleList = redisTemplate.opsForList().range(RedisCons.KEY_APP_MODULES, 0,-1);
        if(moduleList != null){
            return moduleList;
        }
        return Collections.emptyList();
    }
}
