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
package com.diboot.iam.handler;

import com.diboot.iam.entity.LoginUserDetail;
import com.diboot.iam.service.AuthServerCacheService;
import com.diboot.cloud.redis.config.RedisCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色资源缓存初始化执行器
 * @author JerryMa
 * @version v2.2
 * @date 2020/11/23
 */
@Component
public class AccessInteceptor implements HandlerInterceptor {

    @Autowired
    private AuthServerCacheService authServerCacheService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (auth.isAuthenticated() && principal != null && principal instanceof LoginUserDetail) {
            LoginUserDetail userDetail = (LoginUserDetail)principal;
            String key = userDetail.getUserType() + ":" + userDetail.getUserId();
            boolean hasKey = redisTemplate.opsForHash().hasKey(RedisCons.KEY_USER_AUTH_REFRESH_MAP, key);
            if(!hasKey){
                return true;
            }
            authServerCacheService.refreshUserRolesCache(userDetail.getUserType(), userDetail.getUserId());
        }
        return true;
    }

}