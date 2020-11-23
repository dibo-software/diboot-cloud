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
package com.diboot.cloud.iam.initializer;

import com.diboot.cloud.iam.service.RoleResourceCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 角色资源缓存初始化执行器
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@Order(998)
@Component
@Slf4j
public class RoleSourcesCacheInitializer implements ApplicationRunner {

    @Autowired
    private RoleResourceCacheService roleResourceCacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleResourceCacheService.refreshResourceRolesCache();
    }

}
