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
package com.diboot.iam.controller;

import com.diboot.cloud.redis.config.RedisCons;
import com.diboot.core.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 接口权限示例Controller
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/09
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /***
     * api接口列表（供前端选择）
     * @return
     * @throws Exception
     */
    @GetMapping("/apiList")
    public JsonResult apiList() throws Exception{
        Map<Object, Object> moduleToPermissionsMap = redisTemplate.opsForHash().entries(RedisCons.KEY_APP_MODULE_PERMISSIONS_MAP);
        return JsonResult.OK(moduleToPermissionsMap);
    }

}