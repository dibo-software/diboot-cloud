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
package com.diboot.cloud.initializer;

import com.diboot.cloud.annotation.process.ApiPermissionExtractor;
import com.diboot.cloud.annotation.process.ApiPermissionWrapper;
import com.diboot.cloud.redis.RedisCons;
import com.diboot.core.util.V;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 接口权限缓存初始化执行器
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Order(999)
@Component
@Slf4j
public class ApiPermissionInitializer implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 提取模块全部权限列表并更新缓存
        List<ApiPermissionWrapper> permissionWrapperList = ApiPermissionExtractor.extractAllApiPermissions();
        if(V.isEmpty(permissionWrapperList)){
            return;
        }
        log.info("提取 {} 模块的权限注解 {} 条.", applicationName, permissionWrapperList.size());
        redisTemplate.opsForHash().put(RedisCons.KEY_APP_MODULE_PERMISSIONS_MAP, applicationName, permissionWrapperList);
    }

}
