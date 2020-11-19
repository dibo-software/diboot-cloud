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
package com.diboot.cloud.iam.init;

import com.diboot.cloud.redis.RedisCons;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.vo.ResourceRoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    private IamRoleResourceService iamRoleResourceService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, List<String>> resourceRolesMap = new TreeMap<>();
        // 获取全部资源-角色关联
        List<ResourceRoleVO> voList = iamRoleResourceService.getAllResourceRoleVOList();
        if(V.notEmpty(voList)){
            for(ResourceRoleVO vo : voList){
                // 忽略 无URL
                if(V.isEmpty(vo.getApiSet())){
                    continue;
                }
                List<String> roleCodes = vo.getRoleCodes();
                if(V.isEmpty(vo.getRoleCodes())){
                    roleCodes = Collections.emptyList();
                }
                else{
                    roleCodes = roleCodes.stream().map(i -> i = RedisCons.PREFIX_ROLE + i).collect(Collectors.toList());
                }
                // 组装
                String[] apiArray = S.split(vo.getApiSet());
                for(String api : apiArray){
                    String httpMethod = S.substringBefore(api,":");
                    String fullUri = httpMethod.toUpperCase() + ":/" + vo.getAppModule() + S.substringAfter(api, ":");
                    resourceRolesMap.put(fullUri, roleCodes);
                }
            }
        }
        log.info("初始化资源角色缓存完成, 共加载 {} 项", resourceRolesMap.size());
        log.debug("资源-角色匹配: {}", resourceRolesMap);
        redisTemplate.opsForHash().putAll(RedisCons.KEY_RESOURCE_ROLES_MAP, resourceRolesMap);
    }
}
